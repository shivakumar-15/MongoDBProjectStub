package com.freddiemac.lcax.common.dao.impl;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregationOptions;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
/**
 * LCXAppraisalDocumentDAOImpl for inserting the Document level data into mongo db and collection name "LCAXAppraisalDocument"
 */
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.freddiemac.lcax.common.DBColumnConstants;
import com.freddiemac.lcax.common.LCAXConstants;
import com.freddiemac.lcax.common.config.ConfigHandler;
import com.freddiemac.lcax.common.dao.LCXAppraisalDocumentDAO;
import com.freddiemac.lcax.common.entities.AppraisalDocumentVO;
import com.freddiemac.lcax.common.entities.LCASummaryFileVO;
import com.freddiemac.lcax.common.exception.CreateException;
import com.freddiemac.lcax.common.exception.FinderException;
import com.freddiemac.lcax.common.exception.UpdateException;
import com.freddiemac.lcax.common.logger.LogFactory;
import com.freddiemac.lcax.common.logger.Logger;
import com.freddiemac.lcax.common.mongo.factory.LocalMongoTemplateFactory;
import com.freddiemac.lcax.common.util.DuplicateFSFiles;
import com.freddiemac.lcax.common.util.DuplicateSummaries;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;


public class LCXAppraisalDocumentDAOImpl implements LCXAppraisalDocumentDAO {

	private static final Logger LOG = LogFactory.getLogger(LCXAppraisalDocumentDAOImpl.class);

	private MongoOperations mongoOps;
	LocalMongoTemplateFactory mongoTemplateFactory;
	private GridFsTemplate gridFsTemplate;
	private MongoDbFactory dbFactory;
	public static final long chunkSize=4194304;
	
	public LCXAppraisalDocumentDAOImpl(LocalMongoTemplateFactory mongoTemplateFactory) {

		this.mongoOps = mongoTemplateFactory.getMongoTemplate();
		this.gridFsTemplate= mongoTemplateFactory.getGridFsTemplate();
		this.dbFactory=mongoTemplateFactory.getMongoDbFactory();
	}
	
	public LCXAppraisalDocumentDAOImpl(boolean test){
		
	}
	
	private MongoOperations getMongoOps() {
		if(mongoOps==null){
			mongoOps=mongoTemplateFactory.getMongoTemplate();
		}
		return mongoOps;
	}
	/**
	 * persistSummaryDataToAppraisalTable persisting the AppraislaDocument Vo
	 * into db
	 */
	@Override
	public boolean persistSummaryDataToAppraisalTable(AppraisalDocumentVO appDoc) throws CreateException {
		LOG.debug("persistSummaryDataToAppraisalTable Method Start");
		try {
			Date currentTime=new Date();
			appDoc.setCreationDateTime(currentTime);
			appDoc.setUpdationDateTime(currentTime);
			this.getMongoOps().insert(appDoc, ConfigHandler.getLCAExtensionDocumentCollectionName());

			return true;
		} catch (Exception e) {

			LOG.error(e);
			throw new CreateException(e);

		} finally {
			LOG.debug("persistSummaryDataToAppraisalTable Method end");
		}
	}

	/**
	 * persistSummaryDataToAppraisalTable
	 */

	@Override
	public boolean persistSummaryDataToAppraisalTable(List<AppraisalDocumentVO> appDocs) throws CreateException {
		LOG.debug(" persistSummaryDataToAppraisalTable Method Start");

		try {
			Date currentTime=new Date();
			
			for (AppraisalDocumentVO appDoc : appDocs) {
				appDoc.setCreationDateTime(currentTime);
				appDoc.setUpdationDateTime(currentTime);
				this.getMongoOps().insert(appDoc, ConfigHandler.getLCAExtensionDocumentCollectionName());
			}
			return true;
		} catch (Exception e) {
			LOG.error(e);
			throw new CreateException(e);

		} finally {
			LOG.debug("persistSummaryDataToAppraisalTable Method end");
		}
	}
	
/**	@Override
	public boolean deleteAppraisalDocuments(String summaryUniqueIdentifier) throws FinderException {
		LOG.debug("deleteAppraisalDocuments Method Start for summaryUniqueIdentifier"+summaryUniqueIdentifier);
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.UNIQUE_ID).is(summaryUniqueIdentifier));
			this.getMongoOps().remove(query, AppraisalDocumentVO.class,
					ConfigHandler.getLCAExtensionDocumentCollectionName());
		} catch (Exception e) {
			LOG.error(e);
			throw new FinderException(e.getMessage());
		} finally {
			LOG.debug("deleteAppraisalDocuments Method End");
		}
		return true;
	}
**/
	
	@Override
	public List<AppraisalDocumentVO> getAppraisalList(String uniqueId) throws FinderException {
		LOG.debug("LCXAppraisalDocumentDAOImpl: getAppraisalList Method Start");
		List<AppraisalDocumentVO> appDocList = null;
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.UNIQUE_ID).is(uniqueId).andOperator(Criteria.where(DBColumnConstants.PROPERTY_VALUATION_VALID_INDICATOR).ne("N")));
			appDocList = this.getMongoOps().find(query, AppraisalDocumentVO.class, ConfigHandler.getLCAExtensionDocumentCollectionName());
			LOG.debug("list size "+ appDocList.size());
		} catch (Exception e) {
			LOG.error("Exception in LCXAppraisalDocumentDAOImpl.getAppraisalList Exception Message: {0}, Exception: {1}", e, e);
			throw new FinderException(e);
		} finally {
			LOG.debug("getAppraisalList Method end");
		}

		return appDocList;
	}
	
	@Override
	public AppraisalDocumentVO getSingleAppraisal(String summaryUniqueIdentifier,String appraisalSurrogateIdentifier) throws FinderException{
		LOG.debug("LCXAppraisalDocumentDAOImpl: getAppraisalList Method Start");
		AppraisalDocumentVO appDoc = null;
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.UNIQUE_ID).is(summaryUniqueIdentifier)
					.andOperator(Criteria.where(DBColumnConstants.APPRAISAL_SURROGATE_IDENTIFIER).is(appraisalSurrogateIdentifier)));
			appDoc = this.getMongoOps().findOne(query, AppraisalDocumentVO.class, ConfigHandler.getLCAExtensionDocumentCollectionName());
		
		} catch (Exception e) {
			LOG.error(e);
			throw new FinderException(e);
		} finally {
			LOG.debug("getAppraisalList Method end");
		}

		return appDoc;
	
	}

	@Override
	public boolean updateScrubbedAddress(List<AppraisalDocumentVO> appraisalDocuments) throws UpdateException {
		LOG.debug("updateScrubbedAddress Method Start");
		boolean updateFlag = false;
		try {
			appraisalDocuments.forEach(document -> {

				Query query = new Query();

				query.addCriteria(Criteria.where(DBColumnConstants.APPRAISAL_SURROGATE_IDENTIFIER).exists(true).andOperator(Criteria.where(DBColumnConstants.UNIQUE_ID).is(document.getSummaryUniqueIdentifier()),
						Criteria.where(DBColumnConstants.APPRAISAL_SURROGATE_IDENTIFIER).is(document.getAppraisalSurrogateIdentifier())));
				
				Update up = new Update();
				up.set(DBColumnConstants.SCRUBBED_ADDRESS, document.getScrubbedAddress());
				up.set(DBColumnConstants.UPDATION_DATETIME, new Date());
				this.getMongoOps().findAndModify(query, up, AppraisalDocumentVO.class, ConfigHandler.getLCAExtensionDocumentCollectionName());
			});
			updateFlag = true;
		} catch (Exception e) {
			updateFlag = false;
			LOG.error(e);
			throw new UpdateException(e);
		} finally {
			LOG.debug("updateScrubbedAppraisal Method end");
		}
		return updateFlag;
	}
	
	@Override
	public boolean updateAppraisalSLID(List<AppraisalDocumentVO> appDocVOList) throws UpdateException {
		LOG.debug("updateAppraisalSLID Method Start");
		try{
			for(AppraisalDocumentVO appDoc:appDocVOList) {			
			Query query = new Query();
			query.addCriteria(Criteria
					.where(DBColumnConstants.APPRAISAL_SURROGATE_IDENTIFIER).exists(true)
					.andOperator(Criteria.where(DBColumnConstants.UNIQUE_ID).is(appDoc.getSummaryUniqueIdentifier()),
							Criteria.where(DBColumnConstants.APPRAISAL_SURROGATE_IDENTIFIER).is(appDoc.getAppraisalSurrogateIdentifier())));
			Update up=new Update();
			up.set(DBColumnConstants.SLID, appDoc.getSubmissionLinkServiceIdentifier());
			up.set(DBColumnConstants.UPDATION_DATETIME, new Date());
			this.getMongoOps().findAndModify(query, up, AppraisalDocumentVO.class,ConfigHandler.getLCAExtensionDocumentCollectionName());
			}
			return true;
		} catch(Exception e) {
			LOG.error(e);
			throw new UpdateException(e);
		}  finally {
			LOG.debug("updateAppraisalSLID Method End");
		}
	}
	
	@Override
	public InputStream retrieveFile(String fileName) throws FinderException {
		LOG.debug("retrieveFile Method start");
		InputStream inStream=null;
		try {

			Query query = new Query().addCriteria(Criteria.where("filename").is(fileName));

			List<GridFSDBFile> result = gridFsTemplate.find(query);
			if (result.size() > 1) {
				LOG.warning("Found more than one File (" + result.size() + ") in GridFS for Filename: " + fileName +". Returning the Latest File");
				
				GridFSDBFile file = result.get(result.size()-1);
				LOG.debug("File UploadDate :" + file.getUploadDate());
				LOG.debug("Filename :" + file.getFilename());
				LOG.debug("ContentType :" + file.getContentType());
				inStream=file.getInputStream();				
			} else if(result.isEmpty()){
				LOG.warning(LCAXConstants.GRIDFS_MISSING_FILE+" for Filename " + fileName);
				
				throw new FinderException(LCAXConstants.GRIDFS_MISSING_FILE+" for Filename " + fileName);
			} else { 
				GridFSDBFile file = result.get(0); 
				LOG.debug(file.getFilename());
				LOG.debug(file.getContentType());
				inStream=file.getInputStream();				
			}
			return inStream;
		} 
		catch (FinderException fe) {
			throw fe;
		}
		catch (Exception e) {
			LOG.error(e);
			throw new FinderException("Exception Occured in retrieve file");
		} finally {
			LOG.debug("retrieveFile Method End");
			
		}
}
	@Override
	public List<DuplicateFSFiles> findDuplicateFiles() throws FinderException{
		try {
			Aggregation agg = newAggregation(
					match(Criteria.where("filename").exists(true)),
					group("filename").count().as("total"),
					project("total").and("filename").previousOperation(),
					sort(Sort.Direction.DESC, "total"),
					match(Criteria.where("total").gt(1))
					).withOptions(newAggregationOptions().
							allowDiskUse(true).build());
			LOG.info("findDuplicateFiles Query :" + agg.toString());

			//Convert the aggregation result into a List
			AggregationResults<DuplicateFSFiles> groupResults
			= mongoOps.aggregate(agg,"fs.files",DuplicateFSFiles.class);
			List<DuplicateFSFiles> result = groupResults.getMappedResults();

			return result;
		}catch (Exception e) {
			throw new FinderException(e);
		}
	}

/**	@Override
	public List<DuplicateSummaries> findDuplicateSummaries() throws FinderException{ 
		try{
			Aggregation agg = newAggregation(
					match(Criteria.where("SummaryEndDateTime").exists(true)),
					group("SummaryEndDateTime").count().as("total"),
					project("total").and("SummaryEndDateTime").previousOperation(),
					sort(Sort.Direction.DESC, "total"),
					match(Criteria.where("total").gt(1))
					).withOptions(newAggregationOptions().
							allowDiskUse(true).build());

			//Convert the aggregation result into a List
			AggregationResults<DuplicateSummaries> groupResults
			= mongoOps.aggregate(agg,"AppraisalSummaryFile",DuplicateSummaries.class);
			List<DuplicateSummaries> result = groupResults.getMappedResults();

			return result;
		}catch (Exception e) {
			throw new FinderException(e);
		}
	}
	**/

	@Override
	public List<GridFSDBFile> getDuplicateFiles(String fileName) throws FinderException{
		LOG.debug("getDuplicateFiles Method Start");
		List<GridFSDBFile> results=null;
		/**InputStream inStream=null;*/
		try {

			Query query = new Query().addCriteria(Criteria.where("filename").is(fileName));

			results = gridFsTemplate.find(query);
			/**GridFSDBFile fs=new GridFSDBFile();*/
			if(results.size()>1){
				LOG.debug("getDuplicateFiles Method End");
				return results;
			}

		}catch (Exception e) {
			LOG.error(e);
			throw new FinderException(e);
		}
		return null;
	}
	
/** @Override
	public List<LCASummaryFileVO> getDuplicateSummariesDate(Date endDateTime) throws FinderException{
		LOG.debug("getDuplicateSummariesDate Method Start");
		List<LCASummaryFileVO> results=null;
		try {

			Query query = new Query().addCriteria(Criteria.where("SummaryEndDateTime").is(endDateTime));

			results = this.getMongoOps().find(query, LCASummaryFileVO.class, ConfigHandler.getLCAExtensionFileCollectionName());
			if(results.size()>1){
				LOG.debug("getDuplicateSummariesDate Method End");
				return results;
			}
			
			}catch (Exception e) {
				LOG.error(e);
				throw new FinderException(e.getMessage());
			}
		return null;
	}
**/
	
	@Override
	public void deleteFile(String fileName) throws FinderException  {
		LOG.debug("deleteFile Method start");
		try {
			Query query = new Query().addCriteria(Criteria.where("filename").is(fileName));
			gridFsTemplate.delete(query);
		} catch (Exception e) {
			LOG.error("Exception occured while deleting the File: " + fileName);
			throw new FinderException(e);
		} finally {
			LOG.debug("deleteFile Method End");			
		}
	}
	
	@Override
	public void deleteFileByID(Object id)  throws FinderException{
		LOG.debug("deleteFileByID Method start");
		try {
			Query query = new Query().addCriteria(Criteria.where("_id").is(id));
			gridFsTemplate.delete(query);
		} catch (Exception e) {
			LOG.error("Exception occured while deleting the File with id : " + id);
			 throw new FinderException(e);
		} finally {
			LOG.debug("deleteFileByID Method End");			
		}
	}
	
	@Override
	public boolean updateCCPStatus(String uniqueId,String status) throws UpdateException {
	LOG.debug("updateCCPStatus Method start");
	try {
		Query query = new Query(Criteria.where(DBColumnConstants.UNIQUE_ID).is(uniqueId));
		Update up = new Update();
		up.set(DBColumnConstants.CCP_STATUS,status);
		up.set(DBColumnConstants.UPDATION_DATETIME, new Date());
		this.getMongoOps().findAndModify(query, up, LCASummaryFileVO.class, ConfigHandler.getLCAExtensionFileCollectionName());
		return true;
	} catch (Exception e) {
		LOG.error("Error while updateCCPStatus : "+e);
		throw new UpdateException(e);
	} finally {
		LOG.debug("updateCCPStatus Method End");
	}
	
}

	@Override
	public boolean updateCCPFileNames(AppraisalDocumentVO appraisal) throws UpdateException{
		LOG.debug("updateCCPFileNames Method start");	
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.APPRAISAL_SURROGATE_IDENTIFIER).is(appraisal.getAppraisalSurrogateIdentifier()));
			Update up = new Update();
			if(appraisal.getAppraisalPDFDocumentName()!=null && !appraisal.getAppraisalPDFDocumentName().isEmpty()){
				up.set(DBColumnConstants.APPRAISAL_PDF_DOCUMENT_NAME, appraisal.getAppraisalPDFDocumentName());
				up.set(DBColumnConstants.APPRAISAL_PDF_DOCUMENT_SIZE, appraisal.getAppraisalPDFDocumentSize());
			}
			if(appraisal.getAppraisalXMLDocumentName()!=null && !appraisal.getAppraisalXMLDocumentName().isEmpty()){
				up.set(DBColumnConstants.APPRAISAL_XML_DOCUMENT_NAME, appraisal.getAppraisalXMLDocumentName());
				up.set(DBColumnConstants.APPRAISAL_XML_DOCUMENT_SIZE, appraisal.getAppraisalXMLDocumentSize());
			}
			up.set(DBColumnConstants.UPDATION_DATETIME, new Date());
			
			this.getMongoOps().findAndModify(query, up, AppraisalDocumentVO.class, ConfigHandler.getLCAExtensionDocumentCollectionName());
		
			return true;
		} catch (Exception e) {
			LOG.error("Error while updateCCPFileNames :"+e);
			throw new UpdateException(e);
		} finally {
			LOG.debug("updateCCPFileNames Method End");
		}
		
	}
	
	@Override
	public long saveCCPResponse(InputStream inputStream,String filename,String type,DBObject metaData) throws CreateException,IOException {
		LOG.debug("saveCCPResponse Method start");
		/**GridFSFile fsFile=null;
		long length=-1;	*/	
		try
		{
			try 
			{				 
				Query query = new Query().addCriteria(Criteria.where("filename").is(filename));
				List<GridFSDBFile> result = gridFsTemplate.find(query);
				if (result != null && result.size() >= 1) {
					LOG.warning("saveCCPResponse:: " + filename  + " File already exists. So, NOT Stored now. Retrurning the existing File.");
					GridFSDBFile file = result.get(result.size()-1);
					if(file!=null)
					{
						return file.getLength();
					}
				}
			}
			catch(Exception exception) {
				LOG.error("Exception Error in saveCCPResponse Method. While retrieveFile/deleteFile. Original Message :"+exception);				
			}
			GridFS gridFS = new GridFS(dbFactory.getDb());
		    GridFSInputFile gridFSInputFile = gridFS.createFile(inputStream);
		    gridFSInputFile.setFilename(filename);
		    gridFSInputFile.setContentType("application/text");
		    gridFSInputFile.setMetaData(metaData);
		    gridFSInputFile.setChunkSize(chunkSize);
		    gridFSInputFile.saveChunks();
		    gridFSInputFile.save();
		    LOG.debug("File written to GridFS "+ filename);
		    
			/**fsFile = gridFsTemplate.store(inputStream,filename,type, metaData);*/	
			
			LOG.debug("saveCCPResponse able to persist the CCP filename "+filename);		
			return gridFSInputFile.getLength();
		}catch(Exception e)
		{	LOG.error("Error while saveCCPResponse :"+e);
			throw new CreateException(e); }	
		finally {
			LOG.debug("saveCCPResponse Method End");
		}
	}

	public void setMongoOps(MongoOperations mongoOps) {
		this.mongoOps = mongoOps;
	}

	public void setMongoTemplateFactory(LocalMongoTemplateFactory mongoTemplateFactory) {
		this.mongoTemplateFactory = mongoTemplateFactory;
	}

	public void setGridFsTemplate(GridFsTemplate gridFsTemplate) {
		this.gridFsTemplate = gridFsTemplate;
	}

	public void setDbFactory(MongoDbFactory dbFactory) {
		this.dbFactory = dbFactory;
	}
	
	

}

