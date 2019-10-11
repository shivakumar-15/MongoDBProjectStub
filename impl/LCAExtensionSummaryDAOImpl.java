package com.freddiemac.lcax.common.dao.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.MongoDbFactory;

/**
 * DAO implemenation for CRUD operations on LCA Extensionfile  
 * @author c44264
 *
 */

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.freddiemac.lcax.common.DBColumnConstants;
import com.freddiemac.lcax.common.StatusConstants;
import com.freddiemac.lcax.common.config.ConfigHandler;
import com.freddiemac.lcax.common.dao.LCAExtensionSummaryDAO;
import com.freddiemac.lcax.common.entities.LCASummaryFileVO;
import com.freddiemac.lcax.common.exception.CreateException;
import com.freddiemac.lcax.common.exception.FinderException;
import com.freddiemac.lcax.common.exception.UpdateException;
import com.freddiemac.lcax.common.logger.LogFactory;
import com.freddiemac.lcax.common.logger.Logger;
import com.freddiemac.lcax.common.mongo.factory.LocalMongoTemplateFactory;
import com.itextpdf.xmp.options.Options;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;

public class LCAExtensionSummaryDAOImpl implements LCAExtensionSummaryDAO {

	private static final Logger LOG = LogFactory.getLogger(LCAExtensionSummaryDAOImpl.class);
	private MongoOperations mongoOps;
	LocalMongoTemplateFactory mongoTemplateFactory;
	private GridFsTemplate gridFsTemplate;
	private MongoDbFactory dbFactory;
	
	public LCAExtensionSummaryDAOImpl(LocalMongoTemplateFactory mongoTemplateFactory) {
		this.mongoOps = mongoTemplateFactory.getMongoTemplate();
		this.gridFsTemplate = mongoTemplateFactory.getGridFsTemplate();
		this.dbFactory=mongoTemplateFactory.getMongoDbFactory();
	}

	private MongoOperations getMongoOps() {
		if(mongoOps==null){
			mongoOps=mongoTemplateFactory.getMongoTemplate();
		}
		return mongoOps;
	}
	/**
	 * persistLCAEXtensionFileData file for inserting the data into
	 */
	@Override
	public boolean persistLCAEXtensionFileData(LCASummaryFileVO summaryFile, ByteArrayOutputStream bos)
			throws CreateException {
		LOG.debug("persistLCAEXtensionFileData Method Start");
		try {
			Date currentTime = new Date();
			String fileName=persistLCAExtensionFileXml(summaryFile, bos);
			summaryFile.setSummaryXML(fileName);
			summaryFile.setCreationDateTime(currentTime);
			summaryFile.setUpdationDateTime(currentTime);
			this.getMongoOps().insert(summaryFile, ConfigHandler.getLCAExtensionFileCollectionName());
			return true;
		} catch (Exception e) {
			LOG.error(e);
			throw new CreateException(e);
		} finally {
			LOG.debug("persistLCAEXtensionFileData Method End");
		}

	}

	public String persistLCAExtensionFileXml(LCASummaryFileVO summaryFile, ByteArrayOutputStream bos)
			throws CreateException {
		LOG.debug("persistLCAExtensionFileXml Method Start");
		try (InputStream isFromFirstData = new ByteArrayInputStream(bos.toByteArray())) {
			DBObject metaData = new BasicDBObject();

			String fileName = summaryFile.getSummaryUniqueIdentifier() + ".txt";
			metaData.put("filename", fileName);
			metaData.put("startTime", summaryFile.getStartDateTime().toString());
			metaData.put("endTime", summaryFile.getEndDateTime().toString());

			String id = gridFsTemplate.store(isFromFirstData, fileName, "application/text", metaData).getId()
					.toString();
			LOG.trace("persistLCAExtensionFileXml persisted the xml file and id is " + id);
			return fileName;
		} catch (IOException e) {
			LOG.error(e);
			throw new CreateException(e);
		}
	}
	
	@Override
	public boolean deleteSummaryFile(String summaryUniqueIdentifier) throws FinderException {
		LOG.debug("deleteSummaryFile Method Start for summaryUniqueIdentifier"+summaryUniqueIdentifier);
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.UNIQUE_ID).is(summaryUniqueIdentifier));
			this.getMongoOps().remove(query, LCASummaryFileVO.class,
					ConfigHandler.getLCAExtensionFileCollectionName());
		} catch (Exception e) {
			LOG.error(e);
			throw new FinderException(e.getMessage());
		} finally {
			LOG.debug("deleteSummaryFile Method End");
		}
		return true;
	}
	
	@Override
	public void storeFileToGridFs(InputStream is,String fileName,DBObject metaData) throws CreateException{
		GridFS gridFS = new GridFS(dbFactory.getDb());
	    GridFSInputFile gridFSInputFile = gridFS.createFile(is);
	    gridFSInputFile.setFilename(fileName);
	    gridFSInputFile.setContentType("application/text");
	    gridFSInputFile.setMetaData(metaData);
	    gridFSInputFile.setChunkSize(1048576);
	    try {
	        gridFSInputFile.saveChunks();
	        gridFSInputFile.save();
	        LOG.debug("File written to GridFS "+ fileName);
	    } catch (IOException e) {
	    	LOG.error("IOException in storeFileToGridFs :"+e);
			throw new CreateException(e);
	    }
		/**gridFsTemplate.store(is, fileName, "application/text", metaData).getId().toString();*/
		
	}
	@Override
	public LCASummaryFileVO getSummaryFile(String uniqueId) throws FinderException {
		LOG.debug("getSummaryFile Method Start");
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.UNIQUE_ID).is(uniqueId));
			return this.getMongoOps().findOne(query, LCASummaryFileVO.class,
					ConfigHandler.getLCAExtensionFileCollectionName());
		} catch (Exception e) {
			LOG.error("Error in getSummaryFile :"+e);
			throw new FinderException(e);
		} finally {
			LOG.debug("getSummaryFile Method End");
		}
	}

	@Override
	public List<LCASummaryFileVO> getFailedSummaryIds() throws FinderException {
		LOG.debug("getFailedSummaryIds Method START");
		List<LCASummaryFileVO> failSummaries = null;
		try {
			Query query = new Query();
			query.addCriteria(new Criteria().orOperator(
					Criteria.where(DBColumnConstants.RDS_STATUS).is(StatusConstants.STATUS_FAILURE),
					Criteria.where(DBColumnConstants.SLS_STATUS).is(StatusConstants.STATUS_FAILURE),
					Criteria.where(DBColumnConstants.PML_STATUS).is(StatusConstants.STATUS_FAILURE),
					Criteria.where(DBColumnConstants.PML_STATUS).is(StatusConstants.STATUS_INPROGRESS),
					Criteria.where(DBColumnConstants.DOCSS_STATUS).is(StatusConstants.STATUS_FAILURE),
					Criteria.where(DBColumnConstants.DOCSS_STATUS).is(StatusConstants.STATUS_INPROGRESS),
					Criteria.where(DBColumnConstants.CCP_STATUS).is(StatusConstants.STATUS_INPROGRESS),
					Criteria.where(DBColumnConstants.CCP_STATUS).is(StatusConstants.STATUS_FAILURE),
					Criteria.where(DBColumnConstants.BAM_SLS_STATUS).is(StatusConstants.STATUS_FAILURE),
					Criteria.where(DBColumnConstants.BAM_PML_STATUS).is(StatusConstants.STATUS_INPROGRESS)));
			query.with(new Sort(Sort.Direction.ASC, DBColumnConstants.UPDATION_DATETIME));
			query.fields().exclude(DBColumnConstants.APPRAISAL_EVALUATION_DOCUMENT_FILE);
			LOG.debug("Query: "+query.toString());
			failSummaries = this.getMongoOps().find(query, LCASummaryFileVO.class,
					ConfigHandler.getLCAExtensionFileCollectionName());
			return failSummaries;
		} catch (Exception e) {
			LOG.error("Error in getFailedSummaryIds :"+e);
			throw new FinderException(e);
		} finally {
			LOG.debug("getFailedSummaryIds Method End");
		}
	}

	@Override
	public boolean updateRDSStatus(String uniqueId, String status) throws UpdateException {
		LOG.debug("updateRDSStatus Method START");
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.UNIQUE_ID).is(uniqueId));
			Update up = new Update();
			up.set(DBColumnConstants.RDS_STATUS, status);
			up.set(DBColumnConstants.UPDATION_DATETIME, new Date());
			this.getMongoOps().findAndModify(query, up, LCASummaryFileVO.class,
					ConfigHandler.getLCAExtensionFileCollectionName());
			LOG.trace("setting RDS Status to " + status);
			return true;
		} catch (Exception e) {
			LOG.error("Error in updateRDSStatus :"+e);
			throw new UpdateException(e);
		} finally {
			LOG.debug("updateRDSStatus Method End");
		}
	}

	@Override
	public boolean updateSLSStatus(String uniqueId, String status) throws UpdateException {
		LOG.debug("updateSLSStatus Method START");
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.UNIQUE_ID).is(uniqueId));
			Update up = new Update();
			up.set(DBColumnConstants.SLS_STATUS, status);
			up.set(DBColumnConstants.UPDATION_DATETIME, new Date());
			this.getMongoOps().findAndModify(query, up, LCASummaryFileVO.class,
					ConfigHandler.getLCAExtensionFileCollectionName());
			LOG.trace("setting SLS Status to " + status);
			return true;
		} catch (Exception e) {
			LOG.error("Error in updateSLSStatus :"+e);
			throw new UpdateException(e);
		} finally {
			LOG.debug("updateSLSStatus Method End");
		}
	}

	@Override
	public boolean updatePMLStatus(String uniqueId, String status) throws UpdateException {
		LOG.debug("updatePMLStatus Method START");
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.UNIQUE_ID).is(uniqueId));
			Update up = new Update();
			up.set(DBColumnConstants.PML_STATUS, status);
			up.set(DBColumnConstants.UPDATION_DATETIME, new Date());
			this.getMongoOps().findAndModify(query, up, LCASummaryFileVO.class,
					ConfigHandler.getLCAExtensionFileCollectionName());
			return true;
		} catch (Exception e) {
			LOG.error("Error in updatePMLStatus :"+e);
			throw new UpdateException(e);
		} finally {
			LOG.debug("updatePMLStatus Method End");
		}
	}

	@Override
	public boolean updateCCPStatus(String uniqueId, String status) throws UpdateException {
		LOG.debug("updateCCPStatus Method START");
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.UNIQUE_ID).is(uniqueId));
			Update up = new Update();
			up.set(DBColumnConstants.CCP_STATUS, status);
			up.set(DBColumnConstants.UPDATION_DATETIME, new Date());
			this.getMongoOps().findAndModify(query, up, LCASummaryFileVO.class,
					ConfigHandler.getLCAExtensionFileCollectionName());
			return true;
		} catch (Exception e) {
			LOG.error("Error in updateCCPStatus :"+e);
			throw new UpdateException(e);
		} finally {
			LOG.debug("updateCCPStatus Method End");
		}
	}

	@Override
	public boolean updateDocSSStatus(String uniqueId, String status) throws UpdateException {
		LOG.debug("updateDocSSStatus Method start");
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.UNIQUE_ID).is(uniqueId));
			Update up = new Update();
			up.set(DBColumnConstants.DOCSS_STATUS, status);
			up.set(DBColumnConstants.UPDATION_DATETIME, new Date());
			this.getMongoOps().findAndModify(query, up, LCASummaryFileVO.class,
					ConfigHandler.getLCAExtensionFileCollectionName());
			return true;
		} catch (Exception e) {
			LOG.error("Error in updateDocSSStatus :"+e);
			throw new UpdateException(e);
		} finally {
			LOG.debug("updateDocSSStatus Method End");
		}
	}
	
	
	@Override
	public boolean updateBamSlsStatus(String uniqueId, String status) throws UpdateException {
		LOG.debug("updateBamSlsStatus Method start");
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.UNIQUE_ID).is(uniqueId));
			Update up = new Update();
			up.set(DBColumnConstants.BAM_SLS_STATUS, status);
			up.set(DBColumnConstants.UPDATION_DATETIME, new Date());
			this.getMongoOps().findAndModify(query, up, LCASummaryFileVO.class,
					ConfigHandler.getLCAExtensionFileCollectionName());
			return true;
		} catch (Exception e) {
			LOG.error("Error in updateBamSlsStatus :"+e);
			throw new UpdateException(e);
		} finally {
			LOG.debug("updateBamSlsStatus Method End");
		}
	}
	
	@Override
	public boolean updateBamPmlStatus(String uniqueId, String status) throws UpdateException {
		LOG.debug("updateBamPmlStatus Method start");
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.UNIQUE_ID).is(uniqueId));
			Update up = new Update();
			up.set(DBColumnConstants.BAM_PML_STATUS, status);
			up.set(DBColumnConstants.UPDATION_DATETIME, new Date());
			this.getMongoOps().findAndModify(query, up, LCASummaryFileVO.class,
					ConfigHandler.getLCAExtensionFileCollectionName());
			return true;
		} catch (Exception e) {
			LOG.error("Error in updateDocSSStatus :"+e);
			throw new UpdateException(e);
		} finally {
			LOG.debug("updateBamPmlStatus Method End");
		}
	}

	// to get all summary files between a particular date range
	@Override
	public List<LCASummaryFileVO> getSummaryFileByDate(Date startPeriod, Date endPeriod) throws FinderException {
		List<LCASummaryFileVO> summaries = null;
		LOG.info("SummaryFileByDate Start");
		try {
			LOG.trace("startPeriod " + startPeriod);
			LOG.trace("endPeriod " + endPeriod);
			Query query = new Query();
			query.addCriteria(new Criteria().andOperator(Criteria.where(DBColumnConstants.RECEIVED_DATE_TIME).gte(startPeriod).lte(endPeriod)));
			query.fields().exclude(DBColumnConstants.APPRAISAL_EVALUATION_DOCUMENT_FILE);
			LOG.info("query " + query.toString());
			summaries = this.getMongoOps().find(query, LCASummaryFileVO.class,
					ConfigHandler.getLCAExtensionFileCollectionName());

			return summaries;
		} catch (Exception e) {
			LOG.error("Error in getSummaryFileByDate :"+e);
			throw new FinderException(e);
		} finally {
			LOG.info("getSummaryFileByDate End");
		}
	}

	// to get all summary files between a particular date range
	@Override
	public List<LCASummaryFileVO> getInProgressSummaryFiles(Date date) throws FinderException {
		List<LCASummaryFileVO> summaries = null;
		LOG.info("getInProgressSummaryFiles Start");
		try {
			LOG.trace("Date " + date);
			Query query = new Query();
			query.addCriteria(new Criteria().orOperator( 
					Criteria.where(DBColumnConstants.RDS_STATUS).is(StatusConstants.STATUS_INPROGRESS),
					Criteria.where(DBColumnConstants.SLS_STATUS).is(StatusConstants.STATUS_INPROGRESS),
					Criteria.where(DBColumnConstants.PML_STATUS).is(StatusConstants.STATUS_INPROGRESS),
					Criteria.where(DBColumnConstants.DOCSS_STATUS).is(StatusConstants.STATUS_INPROGRESS),
					Criteria.where(DBColumnConstants.CCP_STATUS).is(StatusConstants.STATUS_INPROGRESS))					
					.andOperator(Criteria.where(DBColumnConstants.UPDATION_DATETIME).lte(date)));
			query.fields().exclude(DBColumnConstants.APPRAISAL_EVALUATION_DOCUMENT_FILE);
			LOG.info("query " + query.toString());
			summaries = this.getMongoOps().find(query, LCASummaryFileVO.class,
					ConfigHandler.getLCAExtensionFileCollectionName());

			return summaries;
		} catch (Exception e) {
			LOG.error(e);
			throw new FinderException(e);
		} finally {
			LOG.info("getInProgressSummaryFiles End");
		}
	}
	
	// to get all summary files between a particular date range with DOCSS Status as SUCCESS
		@Override
		public List<LCASummaryFileVO> getDocssSummaryFileByDate(Date startPeriod, Date endPeriod) throws FinderException {
			List<LCASummaryFileVO> summaries = null;
			LOG.info("getDocssSummaryFileByDate Start");
			try {
				Query query = new Query();
				query.addCriteria(new Criteria().andOperator(Criteria.where(DBColumnConstants.RECEIVED_DATE_TIME).gte(startPeriod).lte(endPeriod),Criteria.where(DBColumnConstants.DOCSS_STATUS).is(StatusConstants.STATUS_SUCCESS)));
				query.fields().exclude(DBColumnConstants.APPRAISAL_EVALUATION_DOCUMENT_FILE);
				LOG.info("query " + query.toString());
				summaries = this.getMongoOps().find(query, LCASummaryFileVO.class,
						ConfigHandler.getLCAExtensionFileCollectionName());

				return summaries;
			} catch (Exception e) {
				LOG.error("Error in getDocssSummaryFileByDate :"+e.getMessage());
				throw new FinderException(e);
			} finally {
				LOG.info("getDocssSummaryFileByDate End");
			}
		}	
		
		public CommandResult runCommandinDB(String query) throws FinderException{

			LOG.debug("runCommandinDB Start");
			try {
				CommandResult result=this.mongoOps.executeCommand(query);
				return result;
			} catch (Exception e) {
				LOG.error("Error while Running Command in DB:"+ e.getMessage());
				LOG.error("Query: "+query);
				throw new FinderException(e);
			} finally {
				LOG.debug("runCommandinDB End");
			}

		}
}
