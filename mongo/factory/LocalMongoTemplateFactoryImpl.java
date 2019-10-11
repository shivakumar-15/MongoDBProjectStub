package com.freddiemac.lcax.common.mongo.factory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.freddiemac.lcax.common.LCAXConstants;
import com.freddiemac.lcax.common.config.ConfigConstants;
import com.freddiemac.lcax.common.config.ConfigHandler;
import com.freddiemac.lcax.common.logger.LogFactory;
import com.freddiemac.lcax.common.logger.Logger;
import com.freddiemac.lcax.common.security.MacVaultCredentials;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

public class LocalMongoTemplateFactoryImpl implements LocalMongoTemplateFactory{
	
	private static final Logger LOG = LogFactory.getLogger(LocalMongoTemplateFactoryImpl.class);
	
	protected List<ServerAddress> seeds;
	protected MongoClientOptions mongoClientOptions;
	private MongoMappingContext mappingContext;
  
    private ApplicationContext appContext;

    private ApplicationEventPublisher applicationEventPublisher;
	
	// Cached variables
	protected static String username = null;
	protected static String password = null;
	protected static MongoClient client;
	private static MongoDbFactory dbFactory;
	private static MongoTemplate mongoTemplate;
	private static GridFsTemplate gridFsTemplate;
	private static String databaseName;
	
	private String serverName = null;//"localhost"
	private String port = null; //"27017"
	private String requiredReplicaSetName=null;//"lca-rs"
	private static int connectionCount = 0;
	private static int connectionTimeout = 0;

	public LocalMongoTemplateFactoryImpl() {
		LOG.debug("LocalMongoTemplateFactoryImpl Initialized");
		serverName = ConfigHandler.getMongoHost();
		port = ConfigHandler.getMongoPort();
		if(connectionCount==0) {
			connectionCount = ConfigHandler.getMongoConnectionCount();
		}
		if(connectionTimeout==0) {
			connectionTimeout = ConfigHandler.getMongoConnectionTimeout();
		}		
		String servers[] = serverName.split(",");
		String ports[] = port.split(",");
		requiredReplicaSetName=ConfigHandler.getMongoSetName();
		seeds = new ArrayList<>();
		int i = 0;
		for(String server:servers) {
			seeds.add(new ServerAddress(server, Integer.valueOf(ports[i])));
			i++;
		}
		databaseName = ConfigHandler.getMongoDBName();// "LCX"
		mongoClientOptions = getMongoClientOptions();
		mappingContext = new MongoMappingContext();
		mappingContext.setApplicationContext(appContext);
		mappingContext.setApplicationEventPublisher(applicationEventPublisher);
		mappingContext.afterPropertiesSet();
	}

	/***
	 * creates if username/password has changed
	 * 
	 * @return
	 */
	@Override
	public MongoTemplate getMongoTemplate() {
		checkUpdateMongoCredentials();
		return mongoTemplate;
	}

	/***
	 * creates if username/password has changed
	 * 
	 * @return
	 */
	@Override
	public GridFsTemplate getGridFsTemplate() {
		checkUpdateMongoCredentials();
		return gridFsTemplate;
	}

	/***
	 * creates if username/password has changed
	 * 
	 * @return
	 */
	@Override
	public MongoDbFactory getMongoDbFactory() {
		checkUpdateMongoCredentials();
		return dbFactory;
	}

	@Override
	public String getDatabaseName() {
		return databaseName;
	}

	@Override
	public MongoClient getClient() {
		return client;
	}

	public static void  setDatabaseName(String dbName) {
		LocalMongoTemplateFactoryImpl.databaseName = dbName;
	}

	public void setSeeds(List<ServerAddress> seeds) {
		this.seeds = seeds;
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	private void checkUpdateMongoCredentials() {
		String currentUsername;
		String currentPassword;
		String isUserMacVaulted = ConfigHandler.getSystemProperty(ConfigConstants.IS_USER_MAC_VALUTED);
		if(LCAXConstants.YES.equalsIgnoreCase(isUserMacVaulted)) {
			MacVaultCredentials macVaultCredentials = new MacVaultCredentials();
			String[] details = macVaultCredentials.getCredentials(ConfigHandler.getSystemProperty(ConfigConstants.MONGO_DB_MAC_VALUTED_ALIAS));
			currentUsername = details[0];
			currentPassword = details[1];
		} else 	{
			currentUsername = ConfigHandler.getMongoUsername();		
			currentPassword = ConfigHandler.getMongoPassword();
		}
		
		if (!currentUsername.equals(username) || !currentPassword.equals(password)) {
			LOG.debug("Creating a new Client for Changed Username or password");
			setUsername(currentUsername);
			setPassword(currentPassword);
			if(client != null) {
				client.close();
		}	
			createClient();
			setDbFactory(new SimpleMongoDbFactory(client, databaseName));
			DbRefResolver dbRefResolver = new DefaultDbRefResolver(dbFactory);
			MongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, mappingContext);
			setMongoTemplate(new MongoTemplate(dbFactory, mappingConverter));
			setGridFsTemplate(new GridFsTemplate(dbFactory, mappingConverter));
		}
	}

	/**
	 * this method exists so it can be overridden for local implementation
	 */
	protected void createClient(){
		List<MongoCredential> credentialsList = new ArrayList<>();
		credentialsList.add(MongoCredential.createPlainCredential(username, "$external", password.toCharArray()));
		if(client != null){
			client.close();
		}
		if("localhost".equalsIgnoreCase(serverName)) {
			LOG.debug("NEW LOCAL MONGO CLIENT CREATED");
			setClient(new MongoClient(serverName,Integer.parseInt(port)));
		} else {
			LOG.debug("NEW MONGO CLIENT CREATED");
			setClient(new MongoClient(seeds, credentialsList, mongoClientOptions));
		}
	}

	/**
	 * this method exists so it can be overridden for local implementation
	 */
	protected MongoClientOptions getMongoClientOptions(){
		Builder mongoClientOptionsBuilder = new MongoClientOptions.Builder();
		WriteConcern wc= new WriteConcern("majority");
		if(requiredReplicaSetName != null && !requiredReplicaSetName.isEmpty()) {
			mongoClientOptionsBuilder = mongoClientOptionsBuilder.requiredReplicaSetName(requiredReplicaSetName);
		} mongoClientOptionsBuilder = mongoClientOptionsBuilder.sslEnabled(false).sslInvalidHostNameAllowed(true).writeConcern(wc);
		mongoClientOptionsBuilder=mongoClientOptionsBuilder.connectTimeout(connectionTimeout).connectionsPerHost(connectionCount);
		return mongoClientOptionsBuilder.build();
	}

	
	public void closeDBConnections() {
		this.client.close();
	}

	public static String getUsername() {
		return username;
	}

	public static void setUsername(String username) {
		LocalMongoTemplateFactoryImpl.username = username;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		LocalMongoTemplateFactoryImpl.password = password;
	}

	public static void setDbFactory(MongoDbFactory dbFactory) {
		LocalMongoTemplateFactoryImpl.dbFactory = dbFactory;
	}

	public static void setMongoTemplate(MongoTemplate mongoTemplate) {
		LocalMongoTemplateFactoryImpl.mongoTemplate = mongoTemplate;
	}

	public static void setGridFsTemplate(GridFsTemplate gridFsTemplate) {
		LocalMongoTemplateFactoryImpl.gridFsTemplate = gridFsTemplate;
	}

	public static void setClient(MongoClient client) {
		LocalMongoTemplateFactoryImpl.client = client;
	}

}
