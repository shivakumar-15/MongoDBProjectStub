package com.freddiemac.lcax.common.dao.impl;

import java.util.Date;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.freddiemac.lcax.common.DBColumnConstants;
import com.freddiemac.lcax.common.config.ConfigHandler;
import com.freddiemac.lcax.common.dao.LCXBatchTransactionDAO;
import com.freddiemac.lcax.common.entities.DOCSSBatchTransactionVO;
import com.freddiemac.lcax.common.entities.LCATransactionTableVO;
import com.freddiemac.lcax.common.exception.CreateException;
import com.freddiemac.lcax.common.exception.FinderException;
import com.freddiemac.lcax.common.exception.UpdateException;
import com.freddiemac.lcax.common.logger.LogFactory;
import com.freddiemac.lcax.common.logger.Logger;
import com.freddiemac.lcax.common.mongo.factory.LocalMongoTemplateFactory;

public class LCXBatchTransactionDAOImpl implements LCXBatchTransactionDAO {

	private static final Logger LOG = LogFactory.getLogger(LCXBatchTransactionDAOImpl.class);

	private MongoOperations mongoOps;
	LocalMongoTemplateFactory mongoTemplateFactory;

	public LCXBatchTransactionDAOImpl(LocalMongoTemplateFactory mongoTemplateFactory) {
		this.mongoOps = mongoTemplateFactory.getMongoTemplate();
	}

	private MongoOperations getMongoOps() {
		if (mongoOps == null) {
			mongoOps = mongoTemplateFactory.getMongoTemplate();
		}
		return mongoOps;
	}

	@Override
	public boolean persistTransactionElements(LCATransactionTableVO transVO) throws CreateException {
		LOG.debug("persistTransactionElements Method Start");
		try {
			Date currentTime = new Date();
			transVO.setCreationDateTime(currentTime);
			transVO.setUpdationDateTime(currentTime);
			this.getMongoOps().insert(transVO, ConfigHandler.getLCABranchTransactionCollectionName());

			return true;
		} catch (Exception e) {

			LOG.error(e);
			throw new CreateException(e.getMessage());
		} finally {
			LOG.debug("persistTransactionElements Method End");
		}

	}
	
	@Override
	public boolean deleteBatchTransaction(String summaryUniqueIdentifier) throws FinderException {
		LOG.debug("deleteBatchTransaction Method Start for summaryUniqueIdentifier"+summaryUniqueIdentifier);
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.UNIQUE_ID).is(summaryUniqueIdentifier));
			this.getMongoOps().remove(query, LCATransactionTableVO.class,
					ConfigHandler.getLCABranchTransactionCollectionName());
		} catch (Exception e) {
			LOG.error(e);
			throw new FinderException(e.getMessage());
		} finally {
			LOG.debug("deleteBatchTransaction Method End");
		}
		return true;
	}
	
	@Override
	public LCATransactionTableVO getTransactionElements(String uniqueId) throws FinderException {
		LOG.debug("getTransactionElements Method Start");
		try {

			Query query = new Query(Criteria.where(DBColumnConstants.UNIQUE_ID).is(uniqueId));
			query.fields().exclude(DBColumnConstants.SLS_REQUEST_IDENTIFIER);
			LOG.debug("query for findone " + query);
			return this.getMongoOps().findOne(query, LCATransactionTableVO.class,
					ConfigHandler.getLCABranchTransactionCollectionName());
		} catch (Exception e) {

			LOG.error(e);
			throw new FinderException(e.getMessage());
		} finally {
			LOG.debug("getTransactionElements Method End");
		}
	}
	
	@Override
	public LCATransactionTableVO getSLSTransactionElements(String uniqueId) throws FinderException {
		LOG.debug("getSLSTransactionElements Method Start");
		try {

			Query query = new Query(Criteria.where(DBColumnConstants.UNIQUE_ID).is(uniqueId));
			LOG.debug("query for findone " + query);
			return this.getMongoOps().findOne(query, LCATransactionTableVO.class,
					ConfigHandler.getLCABranchTransactionCollectionName());
		} catch (Exception e) {

			LOG.error(e);
			throw new FinderException(e.getMessage());
		} finally {
			LOG.debug("getSLSTransactionElements Method End");
		}
	}

	@Override
	public boolean updateRDSElements(String uniqueId, String request, String response, int index)
			throws UpdateException {
		LOG.debug("updateRDSElements Method Start");
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.UNIQUE_ID).is(uniqueId));
			Update up = new Update();
			up.set(DBColumnConstants.RDS_REQUEST + "." + index, request);
			up.set(DBColumnConstants.RDS_RESPONSE + "." + index, response);
			up.set(DBColumnConstants.UPDATION_DATETIME, new Date());
			this.getMongoOps().updateFirst(query, up, LCATransactionTableVO.class,
					ConfigHandler.getLCABranchTransactionCollectionName());
		} catch (Exception e) {
			LOG.error(e);
			throw new UpdateException(e.getMessage());
		} finally {
			LOG.debug("updateRDSElements Method End");
		}
		return true;
	}

	@Override
	public boolean updateSLSElements(String uniqueId, String request, String response,String requestIdentifier,int index)
			throws UpdateException {
		LOG.debug("updateSLSElements Method Start");
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.UNIQUE_ID).is(uniqueId));

			Update up = new Update();
			up.set(DBColumnConstants.SLS_REQUEST + "." + index, request);
			up.set(DBColumnConstants.SLS_RESPONSE + "." + index, response);
			if(requestIdentifier!=null && !requestIdentifier.isEmpty()){
				up.set(DBColumnConstants.SLS_REQUEST_IDENTIFIER + "." + index, requestIdentifier);
			}
			up.set(DBColumnConstants.UPDATION_DATETIME, new Date());
			this.getMongoOps().updateFirst(query, up, LCATransactionTableVO.class,
					ConfigHandler.getLCABranchTransactionCollectionName());
		} catch (Exception e) {
			LOG.error(e);
			throw new UpdateException(e.getMessage());
		} finally {
			LOG.debug("updateSLSElements Method End");
		}
		return true;
	}

	
	@Override
	public boolean updateBAMSlsElements(String uniqueId, String request, int index) throws UpdateException {
		LOG.debug("updateSLSElements Method Start");
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.UNIQUE_ID).is(uniqueId));

			Update up = new Update();
			up.set(DBColumnConstants.SLS_BAM_REQUEST + "." + index, request);
			up.set(DBColumnConstants.UPDATION_DATETIME, new Date());
			this.getMongoOps().updateFirst(query, up, LCATransactionTableVO.class,
					ConfigHandler.getLCABranchTransactionCollectionName());
		} catch (Exception e) {
			LOG.error(e);
			throw new UpdateException(e.getMessage());
		} finally {
			LOG.debug("updateSLSElements Method End");
		}
		return true;
	}
	
	@Override
	public boolean updateDocSSElements(String uniqueId, DOCSSBatchTransactionVO docBatchVO, int index)
			throws UpdateException {
		LOG.debug("updateDocSSElements Method Start");
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.UNIQUE_ID).is(uniqueId));

			Update up = new Update();
			if (docBatchVO.getDocSSSystemRequest() != null && docBatchVO.getDocSSSystemRequest().trim().length() > 0) {
				up.set(DBColumnConstants.DOCSS_BATCH_TRANSACTION + "." + index + "." + DBColumnConstants.DOCSS_REQUEST,
						docBatchVO.getDocSSSystemRequest());
			}
			if (docBatchVO.getDocSSSystemResponse() != null
					&& docBatchVO.getDocSSSystemResponse().trim().length() > 0) {
				up.set(DBColumnConstants.DOCSS_BATCH_TRANSACTION + "." + index + "." + DBColumnConstants.DOCSS_RESPONSE,
						docBatchVO.getDocSSSystemResponse());
			}
			if (docBatchVO.getDocSSSystemMessageIdentifier() != null
					&& docBatchVO.getDocSSSystemMessageIdentifier().trim().length() > 0) {
				up.set(DBColumnConstants.DOCSS_BATCH_TRANSACTION + "." + index + "."
						+ DBColumnConstants.DOCSS_SYSTEM_MESSAGE_IDENTIFIER,
						docBatchVO.getDocSSSystemMessageIdentifier());
			}
			if (docBatchVO.getDocSSSystemTransactionIdentifier() != null
					&& docBatchVO.getDocSSSystemTransactionIdentifier().trim().length() > 0) {
				up.set(DBColumnConstants.DOCSS_BATCH_TRANSACTION + "." + index + "."
						+ DBColumnConstants.DOCSS_SYSTEM_TRANSACTION_IDENTIFIER,
						docBatchVO.getDocSSSystemTransactionIdentifier());
			}
			if (docBatchVO.getDocSSSystemRequestDateTime() != null) {
				up.set(DBColumnConstants.DOCSS_BATCH_TRANSACTION + "." + index + "."
						+ DBColumnConstants.DOCSS_SYSTEM_REQUEST_DATETIME,
						docBatchVO.getDocSSSystemRequestDateTime());
			}
			if (docBatchVO.getDocSSSystemResponseDateTime() != null) {
				up.set(DBColumnConstants.DOCSS_BATCH_TRANSACTION + "." + index + "."
						+ DBColumnConstants.DOCSS_SYSTEM_RESPONSE_DATETIME,
						docBatchVO.getDocSSSystemResponseDateTime());
			}
			up.set(DBColumnConstants.UPDATION_DATETIME, new Date());
			this.getMongoOps().updateFirst(query, up, LCATransactionTableVO.class,
					ConfigHandler.getLCABranchTransactionCollectionName());
		} catch (Exception e) {
			LOG.error(e);
			throw new UpdateException(e.getMessage());
		} finally {
			LOG.debug("updateDocSSElements Method End");
		}
		return true;
	}

	@Override
	public boolean updateRetryCount(LCATransactionTableVO transVO) throws UpdateException {
		LOG.debug("updateRetryCount Method Start");
		try {
			Query query = new Query(
					Criteria.where(DBColumnConstants.UNIQUE_ID).is(transVO.getSummaryUniqueIdentifier()));
			Update up = new Update();
			if (transVO.getRdsretryCount() > 0) {
				up.set(DBColumnConstants.RDS_RETRY_COUNT, transVO.getRdsretryCount());
			}
			if (transVO.getSlsretryCount() > 0) {
				up.set(DBColumnConstants.SLS_RETRY_COUNT, transVO.getSlsretryCount());
			}
			if (transVO.getDocSSretryCount() > 0) {
				up.set(DBColumnConstants.DOCSS_RETRY_COUNT, transVO.getDocSSretryCount());
			}
			up.set(DBColumnConstants.UPDATION_DATETIME, new Date());
			this.getMongoOps().updateFirst(query, up, LCATransactionTableVO.class,
					ConfigHandler.getLCABranchTransactionCollectionName());

		} catch (Exception e) {

			LOG.error(e);
			throw new UpdateException(e.getMessage());
		} finally {
			LOG.debug("updateRetryCount Method End");
		}
		return true;
	}

}
