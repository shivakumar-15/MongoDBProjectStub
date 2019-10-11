package com.freddiemac.lcax.common.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.util.StringUtils;

import com.freddiemac.lcax.common.logger.LogFactory;
import com.freddiemac.lcax.common.logger.Logger;

/**
 * The <b>ConfigHandler</b> is a Framework class, used to get cache the System
 * Property (key/value) values from .properties file.
 * 
 */

public final class ConfigHandler {

    /**
     * Variable to Hold a collection of Configurations.
     */
    private static Map<String, Properties> configurationsMap = new HashMap<>();

    private static final Logger LOGGER = LogFactory.getLogger(ConfigHandler.class);

    private static long lastModified = 0L;

    private static boolean iblnInitialized = false;

    /**
     * overriding the default Constructor to disallow instantiation.
     */
    private ConfigHandler() {

    }

    /**
     * Returns the parameter value of the given property name from the
     * Application properties file.
     * 
     * @param propertyName
     *            Name of the property.
     * @return String
     */
    public static String getSystemProperty(String propertyName) {
        Properties appProperties = null;
        appProperties = configurationsMap.get(ConfigConstants.APPLICATION_PROPERTIES);
        String configReload = System.getProperty("lcax_config_reload");
        if(configReload==null) {
        	configReload="false";
        }
        if(appProperties == null || "true".equalsIgnoreCase(configReload)) {
//	        String batchConfig = System.getProperty("lcax_config");
//	        
//	        if(batchConfig != null && !StringUtils.isEmpty(batchConfig)) {
//	        	batchConfig = System.getProperty("lcax_config").concat("lcax.properties");
//	        } else {
//	        	batchConfig = ConfigHandler.class.getClassLoader().getResource("lcax.properties").getFile();
//	        }
	        final String batchConfig="C:\\LCA\\config\\lcax.properties";  
	        final File file = new File(batchConfig);
	        if (!file.exists()) {
	            LOGGER.error("The 'lcax_config' is not assigned or lcax.properties is not exists.");
	            return null;
	        }
	        if (file.lastModified() != lastModified) {
	            iblnInitialized = false;
	            lastModified = file.lastModified();
	        }
	        if (appProperties == null || !iblnInitialized) {
	
	            appProperties = new Properties();
	
	            try (InputStream input = new FileInputStream(new File(batchConfig))) {
	                appProperties.load(input);
	            } catch (final Exception e) {
	                LOGGER.error("The 'lcax_config' is not assigned.");
	                LOGGER.error("Exception occured in Config Handler", e);
	            }
	            configurationsMap.put(ConfigConstants.APPLICATION_PROPERTIES, appProperties);
	            iblnInitialized = true;
	        }
        }
        return (String) appProperties.get(propertyName);
    }
    
    public static String getRDSUsername() {

        final String rdsUsername = getSystemProperty(ConfigConstants.RDS_USERNAME);
        if (rdsUsername != null) {
            return rdsUsername;
        } else {
            LOGGER.warning("RDS_USERNAME IS INCORRECT in lcax.properties. Key not found for :" + ConfigConstants.RDS_USERNAME);
            return ConfigConstants.RDS_USERNAME;
        }
    }
    
    public static String getRDSPassword() {

        final String rdsPassword = getSystemProperty(ConfigConstants.RDS_PASS);
        if (rdsPassword != null) {
            return rdsPassword;
        } else {
            LOGGER.warning("RDS_PASSWORD IS INCORRECT in lcax.properties. Key not found for :" + ConfigConstants.RDS_PASS);
            return ConfigConstants.RDS_PASS;
        }
    }
    
    public static String getRDSWsdlUrl() {

        final String rdsWsdlUrl = getSystemProperty(ConfigConstants.RDS_WSDL_URL);
        if (rdsWsdlUrl != null) {
            return rdsWsdlUrl;
        } else {
            LOGGER.warning("RDS_WSDL_URL IS INCORRECT in lcax.properties. Key not found for :" + ConfigConstants.RDS_WSDL_URL);
            return ConfigConstants.RDS_WSDL_URL_DEFAULT;
        }
    }
    
    
    public static String getSLSUsername() {

        final String slsUsername = getSystemProperty(ConfigConstants.SLS_USERNAME);
        if (slsUsername != null) {
            return slsUsername;
        } else {
            LOGGER.warning("SLS_USERNAME IS INCORRECT in lcax.properties. Key not found for :" + ConfigConstants.SLS_USERNAME);
            return ConfigConstants.SLS_USERNAME;
        }
    }
    
    public static String getSLSPassword() {

        final String slsPassword = getSystemProperty(ConfigConstants.SLS_PASS);
        if (slsPassword != null) {
            return slsPassword;
        } else {
            LOGGER.warning("SLS_PASSWORD IS INCORRECT in lcax.properties. Key not found for :" + ConfigConstants.SLS_PASS);
            return ConfigConstants.SLS_PASS;
        }
    }
    
    public static String getSLSWsdlUrl() {

        final String slssWsdlUrl = getSystemProperty(ConfigConstants.SLS_WSDL_URL);
        if (slssWsdlUrl != null) {
            return slssWsdlUrl;
        } else {
            LOGGER.warning("SLS_WSDL_URL IS INCORRECT in lcax.properties. Key not found for :" + ConfigConstants.SLS_WSDL_URL);
            return ConfigConstants.SLS_WSDL_URL_DEFAULT;
        }
    }
    
    public static String getCCPRestUrl() {

        final String ccpRestUrl = getSystemProperty(ConfigConstants.CCP_REST_URL);
        if (ccpRestUrl != null) {
            return ccpRestUrl;
        } else {
            LOGGER.warning("CCP_REST_URL IS INCORRECT in lcax.properties. Key not found for :" + ConfigConstants.CCP_REST_URL);
            return ConfigConstants.CCP_REST_URL_DEFAULT;
        }
    }

    public static String getLCAExtensionFileCollectionName() {

        final String extensionFileCollectionName = getSystemProperty(ConfigConstants.LCA_EXTENSION_FILE_COLLECTION_NAME);
        if (extensionFileCollectionName != null) {
            return extensionFileCollectionName;
        } else {
            LOGGER.error("USING LCAExtensionFileCollectionName AS CONFIGURATION IS INCORRECT. Key not found for :" + ConfigConstants.LCA_EXTENSION_FILE_COLLECTION_NAME);
            return ConfigConstants.LCA_EXTENSION_FILE_COLLECTION_DEFAULT;
        }
    }
    
    public static String getLCAExtensionDocumentCollectionName() {

        final String extensionFileCollectionName = getSystemProperty(ConfigConstants.LCAX_APPRAISAL_DOCUMENT_COLLECTION_NAME);
        if (extensionFileCollectionName != null) {
            return extensionFileCollectionName;
        } else {
            LOGGER.error("USING LCAXAppraisalDocumentCollectionName AS CONFIGURATION IS INCORRECT. Key not found for :" + ConfigConstants.LCAX_APPRAISAL_DOCUMENT_COLLECTION_NAME);
            return ConfigConstants.LCAX_APPRAISAL_DOCUMENT_COLLECTION_DEFAULT;
        }
    }
    
    public static String getLCABranchTransactionCollectionName() {

        final String extensionFileCollectionName = getSystemProperty(ConfigConstants.LCAX_BATCH_TRANSACTION_COLLECTION_NAME);
        if (extensionFileCollectionName != null) {
            return extensionFileCollectionName;
        } else {
            LOGGER.error("USING LCAXBatchTransactionCollectionName AS CONFIGURATION IS INCORRECT. Key not found for :" + ConfigConstants.LCAX_BATCH_TRANSACTION_COLLECTION_NAME);
            return ConfigConstants.LCAX_BATCH_TRANSACTION_COLLECTION_DEFAULT;
        }
    }
    
    public static String getLCAXLastSummaryCollectionName() {

        final String extensionFileCollectionName = getSystemProperty(ConfigConstants.LCAX_LAST_SUMMARY_COLLECTION_NAME);
        if (extensionFileCollectionName != null) {
            return extensionFileCollectionName;
        } else {
            LOGGER.error("USING LCAXLastSummaryCollectionName AS CONFIGURATION IS INCORRECT. Key not found for :" + ConfigConstants.LCAX_LAST_SUMMARY_COLLECTION_NAME);
            return ConfigConstants.LCAX_LAST_SUMMARY_COLLECTION_DEFAULT;
        }
    } 
    
  
    public static String getMongoHost() {

        final String hostname = getSystemProperty(ConfigConstants.MONGO_HOST);
        if (hostname != null) {
            return hostname;
        } else {
            LOGGER.warning("MONGO_HOST IS INCORRECT in lcax.properties. Key not found for :" + ConfigConstants.MONGO_HOST);
            return ConfigConstants.MONGO_HOST;
        }
    }
    
    public static String getMongoPort() {

        final String mongoPort = getSystemProperty(ConfigConstants.MONGO_PORT);
        if (mongoPort != null) {
            return mongoPort;
        } else {
            LOGGER.warning("MONGO_PORT IS INCORRECT in lcax.properties. Key not found for :" + ConfigConstants.MONGO_PORT);
            return ConfigConstants.MONGO_PORT;
        }
    }
    
    public static String getMongoUsername() {

        final String mongoUsername = getSystemProperty(ConfigConstants.MONGO_USERNAME);
        if (mongoUsername != null) {
            return mongoUsername;
        } else {
            LOGGER.warning("MONGO_USERNAME IS INCORRECT in lcax.properties. Key not found for :" + ConfigConstants.MONGO_USERNAME);
            return ConfigConstants.MONGO_USERNAME;
        }
    }   
   
    public static String getMongoPassword() {

        final String mongoPassword = getSystemProperty(ConfigConstants.MONGO_PASS);
        if (mongoPassword != null) {
            return mongoPassword;
        } else {
            LOGGER.warning("MONGO_PASSWORD IS INCORRECT in lcax.properties. Key not found for :" + ConfigConstants.MONGO_PASS);
            return ConfigConstants.MONGO_PASS;
        }
    }
    
    public static String getMongoDBName() {

        final String dbName = getSystemProperty(ConfigConstants.MONGO_DBNAME);
        if (dbName != null) {
            return dbName;
        } else {
            LOGGER.warning("MONGO_DBNAME IS INCORRECT in lcax.properties. Key not found for :" + ConfigConstants.MONGO_DBNAME);
            return ConfigConstants.MONGO_DBNAME_DEFAULT;
        }
    }
    
    public static String getMongoSetName() {

        final String setName = getSystemProperty(ConfigConstants.MONGO_SETNAME);
        if (setName != null) {
            return setName;
        } else {
            LOGGER.warning("MONGO_SETNAME IS INCORRECT in lcax.properties. Key not found for :" + ConfigConstants.MONGO_SETNAME);
            return ConfigConstants.MONGO_SETNAME_DEFAULT;
        }
    }      
    
    public static boolean isEnableSchemaValidation() {

        final String enableSchema = getSystemProperty(ConfigConstants.ENABLE_SCHEMA_VALIDATION);
        if (enableSchema != null) {
            return new Boolean(enableSchema.trim());
        } else {
            return false;
        }
    } 
    
    public static boolean isEnablePmlSchemaValidation() {

        final String enableSchema = getSystemProperty(ConfigConstants.ENABLE_PML_SCHEMA_VALIDATION);
        if (enableSchema != null) {
            return new Boolean(enableSchema.trim());
        } else {
            return false;
        }
    } 
    
    public static String getMailJndi() {

        final String mailJndi = getSystemProperty(ConfigConstants.EMAIL_JNDI);
        if (mailJndi != null) {
            return mailJndi;
        } else {
            LOGGER.warning("EMAIL_JNDI IS INCORRECT in lcax.properties. Key not found for :" + ConfigConstants.EMAIL_JNDI);
            return ConfigConstants.EMAIL_JNDI;
        }
    }
    
    public static String getEmailFromAddress() {

        final String fromAddress = getSystemProperty(ConfigConstants.FROM_EMAIL);
        if (fromAddress != null) {
            return fromAddress;
        } else {
            LOGGER.warning("FROM_EMAIL IS INCORRECT in lcax.properties. Key not found for :" + ConfigConstants.FROM_EMAIL);
            return ConfigConstants.FROM_EMAIL;
        }
    }
    
    public static String getEmailToAddress() {

        final String toAddress = getSystemProperty(ConfigConstants.TO_EMAIL);
        if (toAddress != null) {
            return toAddress;
        } else {
            LOGGER.warning("TO_EMAIL IS INCORRECT in lcax.properties. Key not found for :" + ConfigConstants.TO_EMAIL);
            return ConfigConstants.TO_EMAIL;
        }
    }      
    
    public static String getLCAXReportPath() {

        final String extensionFileCollectionName = getSystemProperty(ConfigConstants.LCAX_REPORT_PATH);
        if (extensionFileCollectionName != null) {
            return extensionFileCollectionName;
        } else {
            LOGGER.error("USING LCAXReportPath AS CONFIGURATION IS INCORRECT. Key not found for :" + ConfigConstants.LCAX_REPORT_PATH);
            return ConfigConstants.LCAX_REPORT_PATH;
        }
    }
    
    
    public static String getRDSBatchRequestSize() {
    	 final String rdsBatchRequestSize = getSystemProperty(ConfigConstants.RDS_BATCH_REQUEST_SIZE);
    	 if (rdsBatchRequestSize != null) {
             return rdsBatchRequestSize;
         } else {
             LOGGER.error("USING RDS_BATCH_REQUEST_SIZE_DEFAULT AS CONFIGURATION IS INCORRECT. Key not found for :" + ConfigConstants.RDS_BATCH_REQUEST_SIZE);
             return ConfigConstants.RDS_BATCH_REQUEST_SIZE_DEFAULT;
         }
    }
    
    public static String getDOCSBatchRequestSize() {
    	 final String docsBatchRequestSize = getSystemProperty(ConfigConstants.DOCSS_BATCH_REQUEST_SIZE);
    	 if (docsBatchRequestSize != null) {
             return docsBatchRequestSize;
         } else {
             LOGGER.error("USING DOCSS_BATCH_REQUEST_SIZE AS CONFIGURATION IS INCORRECT. Key not found for :" + ConfigConstants.DOCSS_BATCH_REQUEST_SIZE);
             return ConfigConstants.DOCSS_BATCH_REQUEST_SIZE_DEFAULT;
         }
    }
    
    public static String getSLSBatchRequestSize() {
    	 final String slsBatchRequestSize = getSystemProperty(ConfigConstants.SLS_BATCH_REQUEST_SIZE);
    	 if (slsBatchRequestSize != null) {
             return slsBatchRequestSize;
         } else {
             LOGGER.error("USING SLS_BATCH_REQUEST_SIZE_DEFAULT AS CONFIGURATION IS INCORRECT. Key not found for :" + ConfigConstants.SLS_BATCH_REQUEST_SIZE);
             return ConfigConstants.SLS_BATCH_REQUEST_SIZE_DEFAULT;
         }
    }

	public static String getRDSCallingSystemID() {
		
		final String rdsCallingSystemId = getSystemProperty(ConfigConstants.RDS_REQUEST_SYSTEM_ID);
   	 if (rdsCallingSystemId != null) {
            return rdsCallingSystemId;
        } else {
            LOGGER.error("USING RDS_REQUEST_SYSTEM_ID_DEFAULT AS CONFIGURATION IS INCORRECT. Key not found for :" + ConfigConstants.RDS_REQUEST_SYSTEM_ID);
            return ConfigConstants.RDS_REQUEST_SYSTEM_ID_DEFAULT;
        }
		
	}
	
	public static String getLCAAppraisalTransactionCollectionName() {

        final String extensionFileCollectionName = getSystemProperty(ConfigConstants.LCAX_APPRAISAL_TRANSACTION_COLLECTION_NAME);
        if (extensionFileCollectionName != null) {
            return extensionFileCollectionName;
        } else {
            LOGGER.error("USING LCAXBatchTransactionCollectionName AS CONFIGURATION IS INCORRECT. Key not found for :" + ConfigConstants.LCAX_APPRAISAL_TRANSACTION_COLLECTION_NAME);
            return ConfigConstants.LCAX_APPRAISAL_TRANSACTION_COLLECTION_DEFAULT;
        }
    }
	
    public static int getRetryFileCount() {
    	 final String slsBatchRequestSize = getSystemProperty(ConfigConstants.RETRY_FILE_COUNT);
    	 if (slsBatchRequestSize != null && !StringUtils.isEmpty(slsBatchRequestSize) ) {
             return new Integer(slsBatchRequestSize);
         } else {
             LOGGER.error("USING RETRY_FILE_COUNT AS CONFIGURATION IS INCORRECT. Key not found for :" + ConfigConstants.RETRY_FILE_COUNT);
             return new Integer(ConfigConstants.RETRY_FILE_COUNT_DEFAULT);
         }
    }
    
    public static String getDCUScriptPath() {

        final String dcuPath = getSystemProperty(ConfigConstants.DCU_SCRIPT_PATH);
        if (dcuPath != null) {
            return dcuPath;
        } else {
            LOGGER.warning("DCU_SCRIPT_PATH IS INCORRECT in lcax.properties. Key not found for :" + ConfigConstants.DCU_SCRIPT_PATH);
            return null;
        }
    }
    
    public static String getDCUScriptFileNames() {

        final String dcuPath = getSystemProperty(ConfigConstants.DCU_SCRIPT_FILENAMES);
        if (dcuPath != null) {
            return dcuPath;
        } else {
            LOGGER.warning("DCU_SCRIPT_FILENAMES IS INCORRECT in lcax.properties. Key not found for :" + ConfigConstants.DCU_SCRIPT_FILENAMES);
            return null;
        }
    }
 
    public static String getDCUSkipOnErroFlag() {

        final String dcuErrorFLAG = getSystemProperty(ConfigConstants.DCU_SKIP_ON_ERROR_FLAG);
        if (dcuErrorFLAG != null) {
            return dcuErrorFLAG;
        } else {
            LOGGER.warning("DCU_SKIP_ON_ERROR_FLAG IS INCORRECT in lcax.properties. Key not found for :" + ConfigConstants.DCU_SKIP_ON_ERROR_FLAG);
            return "false";
        }
    }
    
    public static String getDOCSSNASMountPath() {

        final String nasMountPath = getSystemProperty(ConfigConstants.DOCSS_NAS_MOUNT_PATH);
        if (nasMountPath != null) {
            return nasMountPath;
        } else {
            LOGGER.warning("DOCSS_NAS_MOUNT_PATH IS INCORRECT in lcax.properties. Key not found for :" + ConfigConstants.DOCSS_NAS_MOUNT_PATH);
            return null;
        }
    }
    
    public static String getDOCSSNASPath() {

        final String nasPath = getSystemProperty(ConfigConstants.DOCSS_NAS_PATH);
        if (nasPath != null) {
            return nasPath;
        } else {
            LOGGER.warning("DOCSS_NAS_PATH IS INCORRECT in lcax.properties. Key not found for :" + ConfigConstants.DOCSS_NAS_PATH);
            return null;
        }
    }

	public static String getCCPCredentials() {
		final String ccpUserName = getSystemProperty(ConfigConstants.CCP_USER_NAME);
		final String ccpPassWord = getSystemProperty(ConfigConstants.CCP_PASS);
		
		 if (ccpUserName != null && ccpPassWord != null) {
	            return ccpUserName + ":" + ccpPassWord;
	        } else {
	            LOGGER.warning("CCP_CREDENTIALS ARE NOT AVAILABLE for :" + ConfigConstants.CCP_USER_NAME+" and "+ConfigConstants.CCP_PASS);
	            return ConfigConstants.CCP_CRED_DEFAULT;
	        }
	}
	
    public static int getInProgressTimeLimit() {
    	 final String inProgressTimeLimit = getSystemProperty(ConfigConstants.INPROGRESS_TIME_LIMIT);
    	 if (inProgressTimeLimit != null && !StringUtils.isEmpty(inProgressTimeLimit) ) {
             return new Integer(inProgressTimeLimit);
         } else {
             LOGGER.error("USING INPROGRESS_TIME_LIMIT AS CONFIGURATION IS INCORRECT. Key not found for :" + ConfigConstants.INPROGRESS_TIME_LIMIT);
             return new Integer(ConfigConstants.INPROGRESS_TIME_LIMIT_DEFAULT);
         }
    }	
    
    public static int getMongoConnectionCount() {
    	 final String count = getSystemProperty(ConfigConstants.MONGO_CONNECTION_COUNT);
    	 if (count != null && !StringUtils.isEmpty(count) ) {
             return new Integer(count);
         } else {
             LOGGER.error("USING MONGO_CONNECTION_COUNT AS CONFIGURATION IS INCORRECT. Key not found for :" + ConfigConstants.MONGO_CONNECTION_COUNT);
             return new Integer(ConfigConstants.MONGO_CONNECTION_COUNT_DEFAULT);
         }
    }
    
    public static int getMongoConnectionTimeout() {
   	 final String timeout = getSystemProperty(ConfigConstants.MONGO_CONNECTION_TIMEOUT);
   	 if (timeout != null && !StringUtils.isEmpty(timeout) ) {
            return new Integer(timeout);
        } else {
            LOGGER.error("USING MONGO_CONNECTION_TIMEOUT AS CONFIGURATION IS INCORRECT. Key not found for :" + ConfigConstants.MONGO_CONNECTION_TIMEOUT);
            return new Integer(ConfigConstants.MONGO_CONNECTION_TIMEOUT_DEFAULT);
        }
   }
    
    public static String getDocSSFilePermission() {
		 final String nasPerm = getSystemProperty(ConfigConstants.DOCSS_FILE_PERMISSION);
	        if (nasPerm != null) {
	            return nasPerm;
	        } else {
	            LOGGER.warning("DOCSS_FILE_PERMISSION IS INCORRECT in lcax.properties. Key not found for :" + ConfigConstants.DOCSS_FILE_PERMISSION);
	            return ConfigConstants.DOCSS_FILE_PERMISSION_DEFAULT;
	        }
	}
    
    public static String getDOCSSFolderGroup() {

        final String folderGroup = getSystemProperty(ConfigConstants.DOCSS_FOLDER_OWNER_GROUP);
        if (folderGroup != null) {
            return folderGroup;
        } else {
            LOGGER.warning("DOCSS_FOLDER_OWNER_GROUP IS INCORRECT in lcax.properties. Key not found for :" + ConfigConstants.DOCSS_FOLDER_OWNER_GROUP);
            return null;
        }
    }
    
    public static String getClearCapitalCredentialFile() {
		 final String credential = getSystemProperty(ConfigConstants.CLEARCAPITAL_CREDENTIAL_FILE);
	        if (credential != null) {
	            return credential;
	        } else {
	            LOGGER.error("CLEARCAPITAL_CREDENTIAL_FILE IS INCORRECT in lcax.properties. Key not found for :" + ConfigConstants.CLEARCAPITAL_CREDENTIAL_FILE);
	            return ConfigConstants.CLEARCAPITAL_CREDENTIAL_FILE_DEFAULT;
	        }
    	
    }
    
	public static String getCCPUserName() {
		final String ccpUserName = getSystemProperty(ConfigConstants.CCP_USER_NAME);		
		 if (ccpUserName != null) {
	            return ccpUserName;
	        } else {
	            LOGGER.warning("CCP_USER_NAME ARE NOT AVAILABLE for :" + ConfigConstants.CCP_USER_NAME);
	            return "";
	        }
	}    
	
	public static String getCCPXMLErrorRetryCount(){
		String retryCount = getSystemProperty(ConfigConstants.CCP_XML_ERROR_RETRY_COUNT);
		if(retryCount != null ){
			return retryCount;
		} else {
			 LOGGER.warning("CCP_XML_ERROR_RETRY_COUNT ARE NOT AVAILABLE for :" + ConfigConstants.CCP_XML_ERROR_RETRY_COUNT);
	         return ConfigConstants.CCP_ERROR_RETRY_COUNT_DEFAULT;
		}
	}
	public static String getCCPPDFErrorRetryCount(){
		String retryCount = getSystemProperty(ConfigConstants.CCP_PDF_ERROR_RETRY_COUNT);
		if(retryCount != null ){
			return retryCount;
		} else {
			 LOGGER.warning("CCP_PDF_ERROR_RETRY_COUNT ARE NOT AVAILABLE for :" + ConfigConstants.CCP_PDF_ERROR_RETRY_COUNT);
	         return ConfigConstants.CCP_ERROR_RETRY_COUNT_DEFAULT;
		}
	}
    
	public static String getClearCapitalCredential() {
		final String filename = getClearCapitalCredentialFile();
		if (filename != null ) {
	    		 final File file = new File(filename);
	    	     if (file.exists()) {
	    	    	 FileInputStream fis = null;
	    	    	 try   
	    	    	 {
	    	    		 fis = new FileInputStream(file);
	    	    		 BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	    	    		 String password = br.readLine();
	    	    		 String username = getCCPUserName();
	    	    		 String credential = username.trim() + ":" + password.trim();
	    	        	 return credential;
	    	         } catch (final Exception e) {
	    	            LOGGER.error("Exception when fetching Clear Capital Credential. Original Message :" +  e.getMessage());
	    	            LOGGER.error(e);
	    	         }
	    	    	 finally {
	    	    		 closeStream(fis);
	    	    		 /**if(fis != null) {
	    	    			 try {
								fis.close();
							} catch (IOException e) {
								LOGGER.warning(e);
							}
	    	    		 }*/
	    	    	 }
	    	     } else {
	    	    	 LOGGER.error("clearcapital.properties is not Found.");
	    	     }
	    } else {
			LOGGER.error("CLEARCAPITAL_CREDENTIAL_FILE IS INCORRECT in lcax.properties. Key not found for :" + ConfigConstants.CLEARCAPITAL_CREDENTIAL_FILE);
	    }
		return "";
	}
	
	private static void closeStream(FileInputStream fis){
		 if(fis != null) {
			 try {
				fis.close();
			} catch (IOException e) {
				LOGGER.warning(e);
			}
		 }
	}
	
	public static String getDocssProducerName() {

        final String docssProducername = getSystemProperty(ConfigConstants.DOCSS_PRODUCER_NAME);
        if (docssProducername != null) {
            return docssProducername;
        } else {
            LOGGER.warning("DOCSS_PRODUCER_NAME IS INCORRECT in lcax.properties. Key not found for :" + ConfigConstants.DOCSS_PRODUCER_NAME);
            return ConfigConstants.DOCSS_PRODUCER_NAME_DEFAULT;
        }
    }
	
	
	public static String getLastReceivedFileRetryTimeLimit() {
		final String retryTimeLimit = getSystemProperty(ConfigConstants.LAST_SUMM_RETRY_TIME_LIMIT);
        if (retryTimeLimit != null) {
            return retryTimeLimit;
        } else {
            LOGGER.warning("LAST_SUMM_RETRY_TIME_LIMIT IS INCORRECT in lcax.properties. Key not found for :" + ConfigConstants.LAST_SUMM_RETRY_TIME_LIMIT);
            return ConfigConstants.LAST_SUMM_RETRY_TIME_LIMIT_DEFAULT;
        }
	}
	
	public static String getCCPRetryFlag(){
		final String CCPRetryFlag = getSystemProperty(ConfigConstants.CCP_RETRY_FLAG);
        if (CCPRetryFlag != null) {
            return CCPRetryFlag;
        } else {
            LOGGER.warning("CCP_RETRY_FLAG IS INCORRECT in lcax.properties.Returning as True since Key not found for :" + ConfigConstants.CCP_RETRY_FLAG);
            return "True";
        }
	}
}
