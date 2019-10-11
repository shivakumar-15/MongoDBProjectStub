package com.freddiemac.lcax.common.mongo.factory;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;

@Component
public interface LocalMongoTemplateFactory {

	MongoTemplate getMongoTemplate();

	GridFsTemplate getGridFsTemplate();

	MongoDbFactory getMongoDbFactory();

	String getDatabaseName();

	MongoClient getClient();
	
	void closeDBConnections();

	void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher);

}
