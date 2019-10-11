package com.freddiemac.lcax.common.config;

public final class ConfigConstants {

	/**
	 * Variable to hold the Application Properties.
	 */
	public static final String APPLICATION_PROPERTIES = "lcax.properties";

	public static final String LCA_EXTENSION_FILE_COLLECTION_NAME = "LCAExtensionFileCollectionName";
	public static final String LCA_EXTENSION_FILE_COLLECTION_DEFAULT = "AppraisalSummaryFile";

	public static final String LCAX_APPRAISAL_DOCUMENT_COLLECTION_NAME = "LCAXAppraisalDocumentCollectionName";
	public static final String LCAX_APPRAISAL_DOCUMENT_COLLECTION_DEFAULT = "AppraisalDocumentDetail";

	public static final String LCAX_BATCH_TRANSACTION_COLLECTION_NAME = "LCAXBatchTransactionCollectionName";
	public static final String LCAX_BATCH_TRANSACTION_COLLECTION_DEFAULT = "AppraisalBatchTransaction";

	public static final String LCAX_LAST_SUMMARY_COLLECTION_NAME = "LCAXLastSummaryCollectionName";
	public static final String LCAX_LAST_SUMMARY_COLLECTION_DEFAULT = "LastSummary";
	
	public static final String LCAX_APPRAISAL_TRANSACTION_COLLECTION_NAME = "LCAXAppraisalTransactionCollectionName";
	public static final String LCAX_APPRAISAL_TRANSACTION_COLLECTION_DEFAULT = "AppraisalTransaction";
	
	public static final String MESSAGE_CODE_DB_ERROR = "DB_ERROR_MSG";

	public static final String RDS_USERNAME = "RDS_USERNAME";
	public static final String RDS_PASS = "RDS_PASSWORD";
	public static final String RDS_WSDL_URL = "RDS_WSDL_URL";
	public static final String RDS_WSDL_URL_DEFAULT = "EnterprisePMService_V1.wsdl";
	public static final String RDS_BATCH_REQUEST_SIZE= "RDS_BATCH_REQUEST_SIZE";
	public static final String RDS_BATCH_REQUEST_SIZE_DEFAULT= "3000";
	public static final String RDS_REQUEST_SYSTEM_ID= "RDS_REQUEST_SYSTEM_ID";
	public static final String RDS_REQUEST_SYSTEM_ID_DEFAULT= "LCAE";

	public static final String SLS_USERNAME = "SLS_USERNAME";
	public static final String SLS_PASS = "SLS_PASSWORD";
	public static final String SLS_WSDL_URL = "SLS_WSDL_URL";
	public static final String SLS_WSDL_URL_DEFAULT = "EnterprisePMService_V1.wsdl";
	public static final String SLS_BATCH_REQUEST_SIZE= "SLS_BATCH_REQUEST_SIZE";
	public static final String SLS_BATCH_REQUEST_SIZE_DEFAULT= "3000";
	
	public static final String DOCSS_NAS_MOUNT_PATH="DOCSS_NAS_MOUNT_PATH";
	public static final String DOCSS_NAS_PATH="DOCSS_NAS_PATH";
	public static final String DOCSS_BATCH_REQUEST_SIZE= "DOCSS_BATCH_REQUEST_SIZE";
	public static final String DOCSS_BATCH_REQUEST_SIZE_DEFAULT= "2000";
	public static final String DOCSS_REQUEST_SYSTEM_ID_DEFAULT= "LCAE";
	public static final String DOCSS_FILE_PERMISSION="DOCSS_FILE_PERMISSION";
	public static final String DOCSS_FILE_PERMISSION_DEFAULT="rwxrwxr--";
	public static final String DOCSS_FOLDER_OWNER_GROUP="DOCSS_FOLDER_OWNER_GROUP";

	public static final String NAME_SPACE_URL = "http://lcaext.freddiemac.com/lcaext_ws";

	public static final String LCAX_DOCSS_COLLECTION_NAME = "LCAX_DOCSS_COLLECTION_NAME";
	public static final String LCAX_CCP_COLLECTION_NAME = "LCAX_CCP_COLLECTION_NAME";
	public static final String LCAX_PML_COLLECTION_NAME = "LCAX_PML_COLLECTION_NAME";

	public static final String CCP_REST_URL = "CCP_REST_URL";
	public static final String CCP_REST_URL_DEFAULT = "CCP_REST_URL_DEFAULT";
	public static final String CCP_USER_NAME = "CCP_USER_NAME";
	public static final String CCP_PASS = "CCP_PASSWORD";
	public static final String CCP_CRED_DEFAULT ="fmcc.api@clearcapital.com" + ":" + "rt#8mZ7!19s7";
	public static final String CCP_XML_ERROR_RETRY_COUNT ="CCP_XML_ERROR_RETRY_COUNT";
	public static final String CCP_PDF_ERROR_RETRY_COUNT ="CCP_PDF_ERROR_RETRY_COUNT";
	public static final String CCP_ERROR_RETRY_COUNT_DEFAULT ="5";

	public static final String MONGO_USERNAME = "Mongo_UserName";
	public static final String MONGO_PASS = "Mongo_Password";
	public static final String MONGO_HOST = "Mongo_Host";
	public static final String MONGO_PORT = "Mongo_Port";
	public static final String MONGO_DBNAME = "Mongo_DBName";
	public static final String MONGO_DBNAME_DEFAULT = "LCX_PROD";
	public static final String MONGO_SETNAME = "Mongo_SetName";
	public static final String MONGO_SETNAME_DEFAULT = "lca-prd-rs";

	public static final String ENABLE_SCHEMA_VALIDATION = "EnableSchemaValidation";

	/**
	 * Variable to hold the InitialContextFactory constant.
	 */
	public static final String INITIAL_CONTEXT_FACTORY = "InitialContextFactory";

	/**
	 * Variable to hold the ProviderURL constant.
	 */
	public static final String PROVIDER_URL = "ProviderURL";

	/**
	 * Variable to hold the FactoryUrlPkgs constant.
	 */
	public static final String FACTORY_URL_PKGS = "FactoryUrlPkgs";

	/**
	 * Variable to hold the SecurityPrincipal constant.
	 */
	public static final String SECURITY_PRINCIPAL = "SecurityPrincipal";

	public static final String EMAIL_JNDI = "EMailJNDI";
	
	public static final String FROM_EMAIL = "Email_From_Address";
	public static final String TO_EMAIL = "Email_To_Address";
	
	public static final String LCAX_REPORT_PATH = "LCAX_REPORT_PATH";
	
	public static final String IS_JNDI_USER_MAC_VALUTED= "IsJNDIUserMacValuted";
	public static final String IS_USER_MAC_VALUTED= "IsUserMacValuted";
	
	public static final String SEND_TO_PMLAPP="SendToPMLApp";
	public static final String SEND_TO_DOCSS="SendToDOCSS";
	public static final String SEND_TO_CCP="SendToCCP";
	public static final String SEND_TO_BAM_PML="SendToBamPML";
	public static final String SEND_TO_BAM_SLS="SendToBamSls";
	
	public static final String MONGO_DB_MAC_VALUTED_ALIAS = "MongoDBMacValutedAlias";
	public static final String JNDI_MAC_VALUTED_ALIAS = "JNDIMacValutedAlias";
	public static final String RDS_MAC_VALUTED_ALIAS = "RDSMacValutedAlias";	
	public static final String SLS_MAC_VALUTED_ALIAS = "SLSMacValutedAlias";
	public static final String EIH_MAC_VALUTED_ALIAS = "EIHMacValutedAlias";
	
	public static final String RETRY_FILE_COUNT = "Retry_File_Count";
	public static final String RETRY_FILE_COUNT_DEFAULT = "4";
	public static final String LAST_SUMM_RETRY_TIME_LIMIT = "Last_Summary_Retry_Time_Limit";
	public static final String LAST_SUMM_RETRY_TIME_LIMIT_DEFAULT = "30";
	public static final String CCP_RETRY_FLAG="CCP_RETRY_FLAG";

	public static final String INPROGRESS_TIME_LIMIT = "InProgressTimeLimit";
	public static final String INPROGRESS_TIME_LIMIT_DEFAULT = "1440";

	public static final String MONGO_CONNECTION_COUNT = "Mongo_Connection_Count";
	public static final String MONGO_CONNECTION_COUNT_DEFAULT = "100";

	public static final String MONGO_CONNECTION_TIMEOUT = "Mongo_Connection_Timeout";
	public static final String MONGO_CONNECTION_TIMEOUT_DEFAULT = "450000";
	
	public static final String CLEARCAPITAL_CREDENTIAL_FILE = "ClearCapitalCredentialFile";
	public static final String CLEARCAPITAL_CREDENTIAL_FILE_DEFAULT = "clearcapital.properties";
	
	public static final String LAST_SUMMARY_FILE_CHECK_DURATION = "LastSummaryFileCheckDuration";
	
	public static final String LAST_SUMMARY_BATCH_REFRESH_DURATION = "LastSummaryBatchRefreshDuration";
	
	public static final String DELETE_DUPLICATE_FSFILES="DeleteDuplicateFSFiles";
	
	public static final String DELETE_DUPLICATE_SUMMARIES="DeleteDuplicateSummaries";
	
	public static final String BAM_START_DATE="BamStartDate";
	
	public static final String DCU_SCRIPT_PATH="DCU_SCRIPT_PATH";
	
	public static final String DCU_SCRIPT_FILENAMES="DCU_SCRIPT_FILENAMES";
	
	public static final String DCU_SKIP_ON_ERROR_FLAG="DCU_SKIP_ON_ERROR_FLAG";

	public static final String ENABLE_PML_SCHEMA_VALIDATION = "EnablePmlSchemaValidation";

	public static final String DOCSS_PRODUCER_NAME = "DocssProducerName";

	public static final String DOCSS_PRODUCER_NAME_DEFAULT = "LCAE";
	
	private ConfigConstants() {

	}

}
