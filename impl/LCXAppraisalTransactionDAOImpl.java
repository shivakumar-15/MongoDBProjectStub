package com.freddiemac.lcax.common.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.freddiemac.lcax.common.DBColumnConstants;
import com.freddiemac.lcax.common.LCAXConstants;
import com.freddiemac.lcax.common.StatusConstants;
import com.freddiemac.lcax.common.config.ConfigHandler;
import com.freddiemac.lcax.common.dao.LCXAppraisalTransactionDAO;
import com.freddiemac.lcax.common.entities.CCPTransactionVO;
import com.freddiemac.lcax.common.entities.DOCSSTransactionVO;
import com.freddiemac.lcax.common.entities.LCAAppraisalTransactionVO;
import com.freddiemac.lcax.common.entities.LCATransactionTableVO;
import com.freddiemac.lcax.common.entities.PMLTransactionVO;
import com.freddiemac.lcax.common.exception.CreateException;
import com.freddiemac.lcax.common.exception.FinderException;
import com.freddiemac.lcax.common.exception.UpdateException;
import com.freddiemac.lcax.common.logger.LogFactory;
import com.freddiemac.lcax.common.logger.Logger;
import com.freddiemac.lcax.common.mongo.factory.LocalMongoTemplateFactory;
import com.mongodb.WriteResult;

public class LCXAppraisalTransactionDAOImpl implements LCXAppraisalTransactionDAO {

	private static final Logger LOG = LogFactory.getLogger(LCXAppraisalTransactionDAOImpl.class);

	private MongoOperations mongoOps;
	LocalMongoTemplateFactory mongoTemplateFactory;

	public LCXAppraisalTransactionDAOImpl(LocalMongoTemplateFactory mongoTemplateFactory) {
		this.mongoOps = mongoTemplateFactory.getMongoTemplate();
	}
	
	private MongoOperations getMongoOps() {
		if(mongoOps==null){
			mongoOps=mongoTemplateFactory.getMongoTemplate();
		}
		return mongoOps;
	}

	@Override
	public boolean persistTransactionElements(LCAAppraisalTransactionVO transVO) throws CreateException {
		LOG.debug("persistAppraisalTransactionElements Method Start");
		try {
			Date currentTime=new Date();
			transVO.setCreationDateTime(currentTime);
			transVO.setUpdationDateTime(currentTime);
			this.getMongoOps().insert(transVO, ConfigHandler.getLCAAppraisalTransactionCollectionName());

			return true;
		} catch (Exception e) {

			LOG.error(e);
			throw new CreateException(e);
		} finally {
			LOG.debug("persistAppraisalTransactionElements Method End");
		}

	}
	
	@Override
	public boolean persistTransactionList(List<LCAAppraisalTransactionVO> appTransList) throws CreateException 
	{
		LOG.debug(" persistTransactionList Method Start");

		try {
			Date currentTime=new Date();
			
			for (LCAAppraisalTransactionVO transVO : appTransList) {
				transVO.setCreationDateTime(currentTime);
				transVO.setUpdationDateTime(currentTime);
				this.getMongoOps().insert(transVO, ConfigHandler.getLCAAppraisalTransactionCollectionName());
			}
			return true;
		} catch (Exception e) {

			LOG.error(e);
			throw new CreateException(e);
		} finally {
			LOG.debug("persistTransactionList Method End");
		}
	}

	/** @Override
	public boolean deleteAppraisalTransactions(String summaryUniqueIdentifier) throws FinderException {
		LOG.debug("deleteAppraisalTransactions Method Start for summaryUniqueIdentifier"+summaryUniqueIdentifier);
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.UNIQUE_ID).is(summaryUniqueIdentifier));
			this.getMongoOps().remove(query, LCAAppraisalTransactionVO.class,
					ConfigHandler.getLCAAppraisalTransactionCollectionName());
		} catch (Exception e) {
			LOG.error(e);
			throw new FinderException(e.getMessage());
		} finally {
			LOG.debug("deleteAppraisalTransactions Method End");
		}
		return true;
	} **/

	@Override
	public LCAAppraisalTransactionVO getTransactionElement(String correlationID) throws FinderException {
		LOG.debug("getAppraisalTransactionElements Method Start");
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.APPRAISAL_SURROGATE_IDENTIFIER).is(correlationID));
			return this.getMongoOps().findOne(query, LCAAppraisalTransactionVO.class, ConfigHandler.getLCAAppraisalTransactionCollectionName());
		} catch (Exception e) {

			LOG.error(e);
			throw new FinderException(e);
		} finally {
			LOG.debug("getAppraisalTransactionElements Method End");
		}
	}

	@Override
	public boolean updatePMLElements(String appraisalSurrogateIdentifier, LCAAppraisalTransactionVO transVO) throws UpdateException
	{
		LOG.debug("updatePMLElements Method Start");
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.APPRAISAL_SURROGATE_IDENTIFIER).is(appraisalSurrogateIdentifier));		
			Update up = new Update();
			Update upFinal=updatePMLData(transVO ,  up);
			/**if(transVO.getPmlTransaction() != null) {
				PMLTransactionVO pmlTrans= transVO.getPmlTransaction();
				
				if(pmlTrans.getPmlSystemRequest() != null && pmlTrans.getPmlSystemRequest().trim().length()>0) {
					up.set(DBColumnConstants.PML_TRANSACTION+"."+DBColumnConstants.PML_REQUEST, pmlTrans.getPmlSystemRequest());
				}
				if(pmlTrans.getPmlSystemResponse() != null && pmlTrans.getPmlSystemResponse().trim().length()>0) {
					up.set(DBColumnConstants.PML_TRANSACTION+"."+DBColumnConstants.PML_RESPONSE, pmlTrans.getPmlSystemResponse());
				}
				up.set(DBColumnConstants.PML_TRANSACTION+"."+DBColumnConstants.PML_STATUS_TEXT, pmlTrans.getPmlStatus());
				if(pmlTrans.getPmlEventIdentifier() != null && pmlTrans.getPmlEventIdentifier().trim().length()>0){
				up.set(DBColumnConstants.PML_TRANSACTION+"."+DBColumnConstants.PML_EVENT_IDENTIFIER, pmlTrans.getPmlEventIdentifier());
				}
				if(pmlTrans.getPmlRetryCount()>0){
					up.set(DBColumnConstants.PML_TRANSACTION+"."+DBColumnConstants.PML_RETRY_COUNT, pmlTrans.getPmlRetryCount());
				}
			}*/
			upFinal.set(DBColumnConstants.UPDATION_DATETIME, new Date());
			LOG.debug("Update Method: "+upFinal);
			LOG.debug("Update query Method: "+query);
			this.getMongoOps().updateFirst(query, upFinal, LCAAppraisalTransactionVO.class, ConfigHandler.getLCAAppraisalTransactionCollectionName());
		} catch (Exception e) {
			LOG.error(e);
			throw new UpdateException(e);
		}
		finally {
			LOG.debug("updatePMLElements Method End");
		}
		return true;
	}
	
	
	
	@Override
	public WriteResult updatePMLResponseElements(String pmlMessageIdentifier, LCAAppraisalTransactionVO transVO) throws UpdateException
	{
		WriteResult result=null;
		LOG.debug("updatePMLResponseElements Method Start");
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.PML_TRANSACTION+"."+DBColumnConstants.PML_MESSAGE_IDENTIFIER).is(pmlMessageIdentifier));		
			Update up = new Update();
			Update upFinal=updatePMLData(transVO ,  up);
			upFinal.set(DBColumnConstants.UPDATION_DATETIME, new Date());
			LOG.debug("Update Method: "+upFinal);
			LOG.debug("Update query Method: "+query);
			result=this.getMongoOps().updateFirst(query, upFinal, LCAAppraisalTransactionVO.class, ConfigHandler.getLCAAppraisalTransactionCollectionName());
			return result;
		} catch (Exception e) {
			LOG.error(e);
			throw new UpdateException(e);
		}
		finally {
			LOG.debug("updatePMLResponseElements Method End");
		}
	}
	
	
	@Override
	public boolean updateDocSSElements(String appraisalSurrogateIdentifier, LCAAppraisalTransactionVO transVO) throws UpdateException
	{
		LOG.debug("updateDocssElements Method Start");
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.APPRAISAL_SURROGATE_IDENTIFIER).is(appraisalSurrogateIdentifier));		
			Update up = new Update();
			if(transVO.getDocSSTransaction() != null) {
				DOCSSTransactionVO trans=transVO.getDocSSTransaction();
				if(trans.getDocSSPDFCorrelationID()!=null){
					up.set(DBColumnConstants.DOCSS_TRANSACTION+"."+DBColumnConstants.DOCSS_PDF_CORRELATIONID, trans.getDocSSPDFCorrelationID());
				}
				if(trans.getDocSSXMLCorrelationID()!=null){
					up.set(DBColumnConstants.DOCSS_TRANSACTION+"."+DBColumnConstants.DOCSS_XML_CORRELATIONID, trans.getDocSSXMLCorrelationID());
				}
				if(trans.getDocSSPDFSent()!=null){
					up.set(DBColumnConstants.DOCSS_TRANSACTION+"."+DBColumnConstants.DOCSS_PDF_SENT, trans.getDocSSPDFSent());
				}
				if(trans.getDocSSXMLSent()!=null){
					up.set(DBColumnConstants.DOCSS_TRANSACTION+"."+DBColumnConstants.DOCSS_XML_SENT, trans.getDocSSXMLSent());
				}
				if(trans.getDocSSPDFStatus()!=null){
					up.set(DBColumnConstants.DOCSS_TRANSACTION+"."+DBColumnConstants.DOCSS_PDF_STATUS, trans.getDocSSPDFStatus());
				}
				updateDocs(up, trans);
			}
			up.set(DBColumnConstants.UPDATION_DATETIME, new Date());
		
			this.getMongoOps().updateFirst(query, up, LCAAppraisalTransactionVO.class, ConfigHandler.getLCAAppraisalTransactionCollectionName());
		} catch (Exception e) {
			LOG.error(e);
			throw new UpdateException(e);
		}
		finally {
			LOG.debug("updateDocssElements Method End");
		}
		return true;
	}

	private void updateDocs(Update up, DOCSSTransactionVO trans) {
		if(trans.getDocSSXMLStatus()!=null){
			up.set(DBColumnConstants.DOCSS_TRANSACTION+"."+DBColumnConstants.DOCSS_XML_STATUS, trans.getDocSSXMLStatus());
		}
		if(trans.getDocSSSystemPDFResponse()!=null){
			up.set(DBColumnConstants.DOCSS_TRANSACTION+"."+DBColumnConstants.DOCSS_SYSTEM_PDF_RESPONSE, trans.getDocSSSystemPDFResponse());
		}
		if(trans.getDocSSSystemXMLResponse()!=null){
			up.set(DBColumnConstants.DOCSS_TRANSACTION+"."+DBColumnConstants.DOCSS_SYSTEM_XML_RESPONSE, trans.getDocSSSystemXMLResponse());
		}
		if(trans.getNasPdfPostedDateTime()!=null){
			up.set(DBColumnConstants.DOCSS_TRANSACTION+"."+DBColumnConstants.NAS_PDF_POSTED_DATETIME, trans.getNasPdfPostedDateTime());
		}
		if(trans.getNasXmlPostedDateTime()!=null){
			up.set(DBColumnConstants.DOCSS_TRANSACTION+"."+DBColumnConstants.NAS_XML_POSTED_DATETIME, trans.getNasXmlPostedDateTime());
		}
		if(trans.getDocSSSystemPDFResponseDateTime()!=null){
			up.set(DBColumnConstants.DOCSS_TRANSACTION+"."+DBColumnConstants.DOCSS_SYSTEM_PDF_RESPONSE_DATETIME, trans.getDocSSSystemPDFResponseDateTime());
		}
		if(trans.getDocSSSystemXMLResponseDateTime()!=null){
			up.set(DBColumnConstants.DOCSS_TRANSACTION+"."+DBColumnConstants.DOCSS_SYSTEM_XML_RESPONSE_DATETIME, trans.getDocSSSystemXMLResponseDateTime());
		}
	}

	@Override
	public boolean updateCCPElements(String appraisalSurrogateIdentifier, LCAAppraisalTransactionVO transVO) throws UpdateException
	{
		LOG.debug("updateCCPElements Method Start");
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.APPRAISAL_SURROGATE_IDENTIFIER).is(appraisalSurrogateIdentifier));		
			Update up = new Update();
			if(transVO.getCcpTransaction() != null) {
				up.set(DBColumnConstants.CCP_TRANSACTION, transVO.getCcpTransaction());
			}
			up.set(DBColumnConstants.UPDATION_DATETIME, new Date());
		
			this.getMongoOps().updateFirst(query, up, LCAAppraisalTransactionVO.class, ConfigHandler.getLCAAppraisalTransactionCollectionName());
		} catch (Exception e) {
			LOG.error(e);
			throw new UpdateException(e);
		}
		finally {
			LOG.debug("updateCCPElements Method End");
		}
		return true;
	}

	@Override
	public List<LCAAppraisalTransactionVO> getFailedPMLTransactions(String summaryUniqueIdentifier) throws FinderException {
		LOG.debug("getFailedPMLTransactions Method Start");
		List<LCAAppraisalTransactionVO> failedTransactions = null;
		try {
			Query query = new Query();			
			query.addCriteria(Criteria.where(DBColumnConstants.UNIQUE_ID).is(summaryUniqueIdentifier).
					andOperator(Criteria.where(DBColumnConstants.PML_TRANSACTION+"."+DBColumnConstants.PML_STATUS_TEXT).is(StatusConstants.STATUS_FAILURE)));			
			failedTransactions = this.getMongoOps().find(query, LCAAppraisalTransactionVO.class, ConfigHandler.getLCAAppraisalTransactionCollectionName());
			
		} catch (Exception e) {
			LOG.error(e);
			throw new FinderException(e);
		} finally {
			LOG.debug("getFailedPMLTransactions Method end");
		}
		return failedTransactions;
	}
	
	@Override
	public List<LCAAppraisalTransactionVO> getFailedSLSTransactions(String summaryUniqueIdentifier) throws FinderException {
		LOG.debug("getFailedSLSTransactions Method Start");
		List<LCAAppraisalTransactionVO> failedTransactions = null;
		try {
			Query query = new Query();			
			query.addCriteria(Criteria.where(DBColumnConstants.UNIQUE_ID).is(summaryUniqueIdentifier).
					andOperator(Criteria.where(DBColumnConstants.SLS_TRANS_STATUS).is(StatusConstants.STATUS_FAILURE)));			
			failedTransactions = this.getMongoOps().find(query, LCAAppraisalTransactionVO.class, ConfigHandler.getLCAAppraisalTransactionCollectionName());
		} catch (Exception e) {
			LOG.error(e);
			throw new FinderException(e);
		} finally {
			LOG.debug("getFailedSLSTransactions Method end");
		}
		return failedTransactions;
	}
	
	
	@Override
	public List<LCAAppraisalTransactionVO> getFailedBAMTransactionsforPML(String summaryUniqueIdentifier) throws FinderException {
		LOG.debug("getFailedBAMTransactionsforPML Method Start");
		List<LCAAppraisalTransactionVO> failedTransactions = null;
		try {
			Query query = new Query();			
			query.addCriteria(Criteria.where(DBColumnConstants.UNIQUE_ID).is(summaryUniqueIdentifier).
					andOperator(Criteria.where(DBColumnConstants.PML_TRANSACTION+"."+DBColumnConstants.PML_BAM_STATUS).is(StatusConstants.STATUS_FAILURE)));			
			failedTransactions = this.getMongoOps().find(query, LCAAppraisalTransactionVO.class, ConfigHandler.getLCAAppraisalTransactionCollectionName());
			
		} catch (Exception e) {
			LOG.error(e);
			throw new FinderException(e);
		} finally {
			LOG.debug("getFailedBAMTransactionsforPML Method end");
		}
		return failedTransactions;
	}
	   
	@Override
	public LCAAppraisalTransactionVO getTransactionByDocSSRequestId(String docCorrelationId) throws FinderException{
		LOG.debug("getTransactionByDocSSRequestId Method Start");
		LCAAppraisalTransactionVO resultTrans=null;
		try
		{
			
			Query query = new Query();
			query.addCriteria(Criteria.where(DBColumnConstants.DOCSS_TRANSACTION+"."+DBColumnConstants.DOCSS_PDF_CORRELATION_ID).is(docCorrelationId));
			resultTrans = this.getMongoOps().findOne(query, LCAAppraisalTransactionVO.class, ConfigHandler.getLCAAppraisalTransactionCollectionName());
			if(resultTrans!=null && resultTrans.getSummaryUniqueIdentifier()!=null){
				return resultTrans;
			}
				
			Query query1 = new Query();
			query1.addCriteria(Criteria.where(DBColumnConstants.DOCSS_TRANSACTION+"."+DBColumnConstants.DOCSS_XML_CORRELATION_ID).is(docCorrelationId));
			resultTrans = this.getMongoOps().findOne(query1, LCAAppraisalTransactionVO.class, ConfigHandler.getLCAAppraisalTransactionCollectionName());
			if(resultTrans!=null && resultTrans.getSummaryUniqueIdentifier()!=null){
				return resultTrans;
			}
		} catch (Exception e) {
			LOG.error(e);
			throw new FinderException(e);
		} finally {
			LOG.debug("getTransactionByDocSSRequestId Method end");
		}
		return resultTrans;
		
	}
	
	@Override
	public List<LCAAppraisalTransactionVO> getTransactionList(String summaryUniqueIdentifier) throws FinderException {
		LOG.debug("getTransactionList Method Start");
		List<LCAAppraisalTransactionVO> appTransactions = null;
		try {
			Query query = new Query();			
			query.addCriteria(Criteria.where(DBColumnConstants.UNIQUE_ID).is(summaryUniqueIdentifier));			
			appTransactions = this.getMongoOps().find(query, LCAAppraisalTransactionVO.class, ConfigHandler.getLCAAppraisalTransactionCollectionName());
			
		} catch (Exception e) {
			LOG.error(e);
			throw new FinderException(e);
		} finally {
			LOG.debug("getTransactionList Method end");
		}
		return appTransactions;
	}
	
	@Override
	public boolean updatePMLAppraisalStatus(String summaryUniqueIdentifier,String appraisalSurrogateIdentifier,String status) throws UpdateException {
		LOG.debug("updatePMLAppraisalStatus Method start");
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.UNIQUE_ID).is(summaryUniqueIdentifier).andOperator(Criteria.where(DBColumnConstants.APPRAISAL_SURROGATE_IDENTIFIER).is(appraisalSurrogateIdentifier)));
			Update up = new Update();
			up.set(DBColumnConstants.PML_TRANSACTION+"."+DBColumnConstants.PML_STATUS_TEXT,status);
			up.set(DBColumnConstants.UPDATION_DATETIME, new Date());
			this.getMongoOps().findAndModify(query, up, LCAAppraisalTransactionVO.class, ConfigHandler.getLCAAppraisalTransactionCollectionName());
			return true;
		} catch (Exception e) {
			throw new UpdateException(e);
		} finally {
			LOG.debug("updatePMLAppraisalStatus Method End");
		}
		
	}
	
	@Override
	public boolean updateBAMAppraisalStatusforPML(String summaryUniqueIdentifier,String appraisalSurrogateIdentifier,String status) throws UpdateException {
		LOG.debug("updateBAMAppraisalStatusforPML Method start");
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.UNIQUE_ID).is(summaryUniqueIdentifier).andOperator(Criteria.where(DBColumnConstants.APPRAISAL_SURROGATE_IDENTIFIER).is(appraisalSurrogateIdentifier)));
			Update up = new Update();
			up.set(DBColumnConstants.PML_TRANSACTION+"."+DBColumnConstants.PML_BAM_STATUS,status);
			up.set(DBColumnConstants.UPDATION_DATETIME, new Date());
			this.getMongoOps().findAndModify(query, up, LCAAppraisalTransactionVO.class, ConfigHandler.getLCAAppraisalTransactionCollectionName());
			return true;
		} catch (Exception e) {
			throw new UpdateException(e);
		} finally {
			LOG.debug("updateBAMAppraisalStatusforPML Method End");
		}
		
	}
	
	@Override
	public boolean updateCCPAppraisalStatus(String summaryUniqueIdentifier,String appraisalSurrogateIdentifier,String status,String fileType) throws UpdateException {
		LOG.debug("updateCCPAppraisalStatus Method start");
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.UNIQUE_ID).is(summaryUniqueIdentifier).andOperator(Criteria.where(DBColumnConstants.APPRAISAL_SURROGATE_IDENTIFIER).is(appraisalSurrogateIdentifier)));
			Update up = new Update();
			if(fileType.equals(LCAXConstants.DOCSS_FILE_TYPE_XML)){
			up.set(DBColumnConstants.CCP_TRANSACTION+"."+DBColumnConstants.CCP_XML_STATUS,status);
			}
			if(fileType.equals(LCAXConstants.DOCSS_FILE_TYPE_PDF)){
				up.set(DBColumnConstants.CCP_TRANSACTION+"."+DBColumnConstants.CCP_PDF_STATUS,status);
			}
			up.set(DBColumnConstants.UPDATION_DATETIME, new Date());
			this.getMongoOps().findAndModify(query, up, LCAAppraisalTransactionVO.class, ConfigHandler.getLCAAppraisalTransactionCollectionName());
			return true;
		} catch (Exception e) {
			throw new UpdateException(e);
		} finally {
			LOG.debug("updateCCPAppraisalStatus Method End");
		}
		
	}
	
	@Override
	public boolean updateDOCSSAppraisalStatus(String summaryUniqueIdentifier,String appraisalSurrogateIdentifier,String status,String fileType) throws UpdateException {
		LOG.debug("updateDOCSSAppraisalStatus Method start");
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.UNIQUE_ID).is(summaryUniqueIdentifier).andOperator(Criteria.where(DBColumnConstants.APPRAISAL_SURROGATE_IDENTIFIER).is(appraisalSurrogateIdentifier)));
			Update up = new Update();
			if(fileType.equals(LCAXConstants.DOCSS_FILE_TYPE_XML)){
				up.set(DBColumnConstants.DOCSS_TRANSACTION+"."+DBColumnConstants.DOCSS_XML_STATUS,status);
			}
			if(fileType.equals(LCAXConstants.DOCSS_FILE_TYPE_PDF)){
				up.set(DBColumnConstants.DOCSS_TRANSACTION+"."+DBColumnConstants.DOCSS_PDF_STATUS,status);
			}
			up.set(DBColumnConstants.UPDATION_DATETIME, new Date());
			this.getMongoOps().findAndModify(query, up, LCAAppraisalTransactionVO.class, ConfigHandler.getLCAAppraisalTransactionCollectionName());
			return true;
		} catch (Exception e) {
			throw new UpdateException(e);
		} finally {
			LOG.debug("updateDOCSSAppraisalStatus Method End");
		}
		
	}
	
	@Override
	public boolean updateCCPResponse(CCPTransactionVO responsevo, String appraisalSurrogateIdentifier) throws  UpdateException{
		LOG.debug("updateCCPResponse Method Start");
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.APPRAISAL_SURROGATE_IDENTIFIER).is(appraisalSurrogateIdentifier));		
			Update up = new Update();
			up.set(DBColumnConstants.CCP_TRANSACTION, responsevo);		
			up.set(DBColumnConstants.UPDATION_DATETIME, new Date());
			this.getMongoOps().updateFirst(query, up, LCAAppraisalTransactionVO.class, ConfigHandler.getLCAAppraisalTransactionCollectionName());
			return true;
		} catch (Exception e) {
			LOG.error(e);
			throw new UpdateException(e);
		}
		finally {
			LOG.debug("updateCCPResponse Method End");
		}
	
		
	}

	@Override
	public List<LCAAppraisalTransactionVO> getFailedDocSSTransactions(String summaryUniqueIdentifier) throws FinderException {
		LOG.debug("getFailedDocSSTransactions Method Start");
		List<LCAAppraisalTransactionVO> failedTransactions = null;
		List<LCAAppraisalTransactionVO> resultTransactions = new ArrayList<LCAAppraisalTransactionVO>();
		try {
			Query query = new Query();			
			query.addCriteria(new Criteria().orOperator(Criteria.where(DBColumnConstants.DOCSS_TRANSACTION+"."+DBColumnConstants.DOCSS_XML_STATUS).is(StatusConstants.STATUS_FAILURE)
							,Criteria.where(DBColumnConstants.DOCSS_TRANSACTION+"."+DBColumnConstants.DOCSS_PDF_STATUS).is(StatusConstants.STATUS_FAILURE)));
			LOG.debug("failed docs trans query "+query);
			failedTransactions = this.getMongoOps().find(query, LCAAppraisalTransactionVO.class, ConfigHandler.getLCAAppraisalTransactionCollectionName());
			for(LCAAppraisalTransactionVO tran:failedTransactions){
				if(tran.getSummaryUniqueIdentifier().equals(summaryUniqueIdentifier)){
					resultTransactions.add(tran);
				}
			}
			return resultTransactions;
		} catch (Exception e) {
			LOG.error(e);
			throw new FinderException(e);
		} finally {
			LOG.debug("getFailedDocSSTransactions Method end");
			
		}
		
	}
	
	@Override
	public List<LCAAppraisalTransactionVO> getFailedCCPTransactions(String summaryUniqueIdentifier) throws FinderException {
		LOG.debug("getFailedCCPTransactions Method Start");
		List<LCAAppraisalTransactionVO> failedTransactions = new ArrayList<LCAAppraisalTransactionVO>();
		List<LCAAppraisalTransactionVO> transactions = null;
		try {
			Query query = new Query();			
			
			query.addCriteria(Criteria.where(DBColumnConstants.UNIQUE_ID).is(summaryUniqueIdentifier));
			
			transactions = this.getMongoOps().find(query, LCAAppraisalTransactionVO.class, ConfigHandler.getLCAAppraisalTransactionCollectionName());
			for(LCAAppraisalTransactionVO transaction : transactions ) {
				boolean isCCPStatusNotNull = transaction.getCcpTransaction() != null && transaction.getCcpTransaction().getCcpPDFStatus()!=null && transaction.getCcpTransaction().getCcpXMLStatus()!=null ? true : false;
				
				if( isCCPStatusNotNull && (transaction.getCcpTransaction().getCcpPDFStatus().equals(StatusConstants.STATUS_FAILURE) || transaction.getCcpTransaction().getCcpXMLStatus().equals(StatusConstants.STATUS_FAILURE))){
					failedTransactions.add(transaction);
				}
						
				/**if(transaction.getCcpTransaction() != null && transaction.getCcpTransaction().getCcpPDFStatus()!=null && transaction.getCcpTransaction().getCcpXMLStatus()!=null &&
						(transaction.getCcpTransaction().getCcpPDFStatus().equals(StatusConstants.STATUS_FAILURE) || transaction.getCcpTransaction().getCcpXMLStatus().equals(StatusConstants.STATUS_FAILURE))){
					failedTransactions.add(transaction);
				}*/
			}
			
		} catch (Exception e) {
			LOG.error(e);
			throw new FinderException(e);
		} finally {
			LOG.debug("getFailedCCPTransactions Method end");
		}
		return failedTransactions;
	}

	// to get all transactions between a particular date range
	@Override
	public List<LCAAppraisalTransactionVO> getInProgressTransactions(Date date, String summaryUniqueIdentifier) throws FinderException {
		List<LCAAppraisalTransactionVO> transactions = null;
		LOG.debug("getInProgressTransactions Start");
		try {
			Query query = new Query();
			
			query.addCriteria(Criteria.where(DBColumnConstants.UNIQUE_ID).is(summaryUniqueIdentifier).orOperator( 
					Criteria.where(DBColumnConstants.PML_TRANSACTION+"."+DBColumnConstants.PML_STATUS_TEXT).is(StatusConstants.STATUS_INPROGRESS),
					Criteria.where(DBColumnConstants.DOCSS_TRANSACTION+"."+DBColumnConstants.DOCSS_XML_STATUS).is(StatusConstants.STATUS_INPROGRESS),
					Criteria.where(DBColumnConstants.DOCSS_TRANSACTION+"."+DBColumnConstants.DOCSS_PDF_STATUS).is(StatusConstants.STATUS_INPROGRESS))					
					.andOperator(Criteria.where(DBColumnConstants.UPDATION_DATETIME).lte(date)));			
			LOG.trace("query " + query.toString());
			transactions = this.getMongoOps().find(query, LCAAppraisalTransactionVO.class,
					ConfigHandler.getLCAAppraisalTransactionCollectionName());

			return transactions;
		} catch (Exception e) {
			LOG.error(e);
			throw new FinderException(e);
		} finally {
			LOG.debug("getInProgressTransactions End");
		}
	}
	
	@Override
	public List<LCAAppraisalTransactionVO> getInProgressDOCSSTransactions(String summaryUniqueIdentifier) throws FinderException {
		List<LCAAppraisalTransactionVO> transactions = null;
		/**List<LCAAppraisalTransactionVO> resultTransactions = new ArrayList<LCAAppraisalTransactionVO>();*/
		LOG.debug("getInProgressDOCSSTransactions Start");
		try {
			Query query = new Query();
			
			query.addCriteria(Criteria.where(DBColumnConstants.UNIQUE_ID).is(summaryUniqueIdentifier).orOperator( 
					Criteria.where(DBColumnConstants.DOCSS_TRANSACTION+"."+DBColumnConstants.DOCSS_XML_STATUS).is(StatusConstants.STATUS_INPROGRESS),
					Criteria.where(DBColumnConstants.DOCSS_TRANSACTION+"."+DBColumnConstants.DOCSS_PDF_STATUS).is(StatusConstants.STATUS_INPROGRESS)));			
			LOG.trace("qry " + query.toString());
			transactions = this.getMongoOps().find(query, LCAAppraisalTransactionVO.class,
					ConfigHandler.getLCAAppraisalTransactionCollectionName());
			/**for(LCAAppraisalTransactionVO tran:transactions){
				DOCSSTransactionVO docTrans=tran.getDocSSTransaction();
				if(docTrans==null || docTrans.getDocSSPDFStatus()==null ||docTrans.getDocSSPDFStatus().equalsIgnoreCase(StatusConstants.STATUS_INPROGRESS) || 
						docTrans.getDocSSXMLStatus()==null ||docTrans.getDocSSXMLStatus().equalsIgnoreCase(StatusConstants.STATUS_INPROGRESS)){
					resultTransactions.add(tran);
				}
			}*/
			return transactions;
		} catch (Exception e) {
			LOG.error(e);
			throw new FinderException(e);
		} finally {
			LOG.debug("getInProgressDOCSSTransactions End");
		}
	}
	
	@Override
	public List<LCAAppraisalTransactionVO> getInProgressPMLTransactions(String summaryUniqueIdentifier) throws FinderException {
		List<LCAAppraisalTransactionVO> transactions = null;
		LOG.debug("getInProgressPMLTransactions Start");
		try {
			Query query = new Query();
			
			query.addCriteria(Criteria.where(DBColumnConstants.UNIQUE_ID).is(summaryUniqueIdentifier).andOperator(
					Criteria.where(DBColumnConstants.PML_TRANSACTION+"."+DBColumnConstants.PML_STATUS_TEXT).is(StatusConstants.STATUS_INPROGRESS)));			
			LOG.trace("query " + query.toString());
			transactions = this.getMongoOps().find(query, LCAAppraisalTransactionVO.class,
					ConfigHandler.getLCAAppraisalTransactionCollectionName());

			return transactions;
		} catch (Exception e) {
			LOG.error(e);
			throw new FinderException(e);
		} finally {
			LOG.debug("getInProgressPMLTransactions End");
		}
	}

	
	private Update updatePMLData(LCAAppraisalTransactionVO transVO , Update up){
		Update upLocal=up;
		if(transVO.getPmlTransaction() != null) {
			PMLTransactionVO pmlTrans= transVO.getPmlTransaction();

			if(pmlTrans.getPmlSystemRequest() != null && pmlTrans.getPmlSystemRequest().trim().length()>0) {
				upLocal.set(DBColumnConstants.PML_TRANSACTION+"."+DBColumnConstants.PML_REQUEST, pmlTrans.getPmlSystemRequest());
			}
			if(pmlTrans.getPmlSystemResponse() != null && pmlTrans.getPmlSystemResponse().trim().length()>0) {
				upLocal.set(DBColumnConstants.PML_TRANSACTION+"."+DBColumnConstants.PML_RESPONSE, pmlTrans.getPmlSystemResponse());
			}
			if(pmlTrans.getPmlStatus() != null && pmlTrans.getPmlStatus().trim().length()>0) {
				upLocal.set(DBColumnConstants.PML_TRANSACTION+"."+DBColumnConstants.PML_STATUS_TEXT, pmlTrans.getPmlStatus());
			}
			if(pmlTrans.getPmlMessageIdentifier() != null && pmlTrans.getPmlMessageIdentifier().trim().length()>0){
				upLocal.set(DBColumnConstants.PML_TRANSACTION+"."+DBColumnConstants.PML_MESSAGE_IDENTIFIER, pmlTrans.getPmlMessageIdentifier());
			}
			if(pmlTrans.getPmlRetryCount()>0){
				upLocal.set(DBColumnConstants.PML_TRANSACTION+"."+DBColumnConstants.PML_RETRY_COUNT, pmlTrans.getPmlRetryCount());
			}
			if(pmlTrans.getPmlSystemRequestDateTime() != null){
				upLocal.set(DBColumnConstants.PML_TRANSACTION+"."+DBColumnConstants.PML_SYSTEM_REQUEST_DATETIME, pmlTrans.getPmlSystemRequestDateTime());
			}
			if(pmlTrans.getPmlSystemResponseDateTime() != null){
				upLocal.set(DBColumnConstants.PML_TRANSACTION+"."+DBColumnConstants.PML_SYSTEM_RESPONSE_DATETIME, pmlTrans.getPmlSystemResponseDateTime());
			}
			upLocal=UpdateBamElements(transVO,upLocal);
		}
		return upLocal;
	}
	
	private Update UpdateBamElements(LCAAppraisalTransactionVO transVO , Update up){
		Update upLocal=up;
		if(transVO.getPmlTransaction() != null) {
			PMLTransactionVO pmlTrans= transVO.getPmlTransaction();
			
			if(pmlTrans.getPmlSystemBamRequest() != null && pmlTrans.getPmlSystemBamRequest().trim().length()>0) {
				upLocal.set(DBColumnConstants.PML_TRANSACTION+"."+DBColumnConstants.PML_BAM_REQUEST, pmlTrans.getPmlSystemBamRequest());
			}
			if(pmlTrans.getPmlSystemResponseStatus() != null && pmlTrans.getPmlSystemResponseStatus().trim().length()>0) {
				upLocal.set(DBColumnConstants.PML_TRANSACTION+"."+DBColumnConstants.PML_RESPONSE_STATUS, pmlTrans.getPmlSystemResponseStatus());
			}
			if(pmlTrans.getPmlSystemBamStatus() != null && pmlTrans.getPmlSystemBamStatus().trim().length()>0) {
				upLocal.set(DBColumnConstants.PML_TRANSACTION+"."+DBColumnConstants.PML_BAM_STATUS, pmlTrans.getPmlSystemBamStatus());
			}
		}
		return upLocal;
	}
	
	@Override
	public List<LCAAppraisalTransactionVO> getUndefinedPMLTransactions(String summaryUniqueIdentifier) throws FinderException {
		LOG.debug("getUndefinedPMLTransactions Method Start");
		List<LCAAppraisalTransactionVO> undefinedTransactions = null;
		try {
			Query query = new Query();			
			query.addCriteria(Criteria.where(DBColumnConstants.PML_TRANSACTION+"."+DBColumnConstants.PML_STATUS_TEXT).exists(false).
					andOperator(Criteria.where(DBColumnConstants.UNIQUE_ID).is(summaryUniqueIdentifier)));			
			undefinedTransactions = this.getMongoOps().find(query, LCAAppraisalTransactionVO.class, ConfigHandler.getLCAAppraisalTransactionCollectionName());
			
		} catch (Exception e) {
			LOG.error(e);
			throw new FinderException(e.getMessage());
		} finally {
			LOG.debug("getUndefinedPMLTransactions Method end");
		}
		return undefinedTransactions;
	}
	
	@Override
	public List<LCAAppraisalTransactionVO> getUndefinedBAMTransactionsforPML(String summaryUniqueIdentifier) throws FinderException {
		LOG.debug("getUndefinedBAMTransactionsforPML Method Start");
		List<LCAAppraisalTransactionVO> undefinedTransactions = null;
		try {
			Query query = new Query();			
			query.addCriteria(Criteria.where(DBColumnConstants.PML_TRANSACTION+"."+DBColumnConstants.PML_BAM_STATUS).exists(false).
					andOperator(Criteria.where(DBColumnConstants.UNIQUE_ID).is(summaryUniqueIdentifier)));			
			undefinedTransactions = this.getMongoOps().find(query, LCAAppraisalTransactionVO.class, ConfigHandler.getLCAAppraisalTransactionCollectionName());
			
		} catch (Exception e) {
			LOG.error(e);
			throw new FinderException(e.getMessage());
		} finally {
			LOG.debug("getUndefinedBAMTransactionsforPML Method end");
		}
		return undefinedTransactions;
	}	

	@Override
	public List<LCAAppraisalTransactionVO> getUndefinedCCPTransactions(String summaryUniqueIdentifier) throws FinderException {
		LOG.debug("getUndefinedCCPTransactions Method Start");
		List<LCAAppraisalTransactionVO> undefinedTransactions = new ArrayList<LCAAppraisalTransactionVO>();
		List<LCAAppraisalTransactionVO> transactions = null;
		try {
			Query query = new Query();			
			
			query.addCriteria(Criteria.where(DBColumnConstants.UNIQUE_ID).is(summaryUniqueIdentifier));
			
			transactions = this.getMongoOps().find(query, LCAAppraisalTransactionVO.class, ConfigHandler.getLCAAppraisalTransactionCollectionName());
			for(LCAAppraisalTransactionVO transaction : transactions ) {
				/**boolean ccpXmlPtatus = ((transaction.getCcpTransaction() != null && transaction.getCcpTransaction().getCcpXMLStatus() != null) ? true:false);
				boolean ccpPdfPtatus = ((transaction.getCcpTransaction() != null && transaction.getCcpTransaction().getCcpPDFStatus() != null) ? true:false);*/
				boolean ccpXmlPtatus = transaction.getCcpTransaction() != null && transaction.getCcpTransaction().getCcpXMLStatus() != null ? true:false;
				boolean ccpPdfPtatus = transaction.getCcpTransaction() != null && transaction.getCcpTransaction().getCcpPDFStatus() != null ? true:false;
				if(!ccpXmlPtatus || !ccpPdfPtatus){					
					undefinedTransactions.add(transaction);
				}
			}
			
		} catch (Exception e) {
			LOG.error(e);
			throw new FinderException(e.getMessage());
		} finally {
			LOG.debug("getUndefinedCCPTransactions Method end");
		}
		return undefinedTransactions;
	}
	
	@Override
	public List<LCAAppraisalTransactionVO> getUndefinedDOCSSTransactions(String summaryUniqueIdentifier) throws FinderException {
		LOG.debug("getUndefinedDOCSSTransactions Method Start");
		List<LCAAppraisalTransactionVO> undefinedTransactions = new ArrayList<LCAAppraisalTransactionVO>();
		List<LCAAppraisalTransactionVO> transactions = null;
		try {
			Query query = new Query();			
			
			query.addCriteria(Criteria.where(DBColumnConstants.UNIQUE_ID).is(summaryUniqueIdentifier));
			
			transactions = this.getMongoOps().find(query, LCAAppraisalTransactionVO.class, ConfigHandler.getLCAAppraisalTransactionCollectionName());
			for(LCAAppraisalTransactionVO transaction : transactions ) {
				/**boolean xmlPtatus = ((transaction.getDocSSTransaction() != null && transaction.getDocSSTransaction().getDocSSXMLStatus() != null) ? true:false);
				boolean pdfPtatus = ((transaction.getDocSSTransaction() != null && transaction.getDocSSTransaction().getDocSSPDFStatus() != null) ? true:false);*/
				
				boolean xmlPtatus = transaction.getDocSSTransaction() != null && transaction.getDocSSTransaction().getDocSSXMLStatus() != null ? true:false;
				boolean pdfPtatus = transaction.getDocSSTransaction() != null && transaction.getDocSSTransaction().getDocSSPDFStatus() != null ? true:false;
				
				if(!xmlPtatus || !pdfPtatus){					
					undefinedTransactions.add(transaction);
				}
			}
			
		} catch (Exception e) {
			LOG.error(e);
			throw new FinderException(e.getMessage());
		} finally {
			LOG.debug("getUndefinedDOCSSTransactions Method end");
		}
		return undefinedTransactions;
	}	
	
	@Override
	public boolean updateSlsTransactionStatus(String appraisalSurrogateIdentifier, String slsStatus) throws UpdateException
	{
		LOG.debug("updateSlsTransactionStatus Method Start");
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.APPRAISAL_SURROGATE_IDENTIFIER).is(appraisalSurrogateIdentifier));		
			Update up = new Update();
			up.set(DBColumnConstants.SLS_TRANS_STATUS, slsStatus);
			up.set(DBColumnConstants.UPDATION_DATETIME, new Date());
			this.getMongoOps().updateFirst(query, up, LCAAppraisalTransactionVO.class, ConfigHandler.getLCAAppraisalTransactionCollectionName());
			
			return true;
		} catch (Exception e) {
			LOG.error(e);
			throw new UpdateException(e);
		}
		finally {
			LOG.debug("updateSlsTransactionStatus Method End");
		}
	}

	@Override
	public boolean updateSlsTransStatusFailure(String uniqueId, String statusFailure) throws UpdateException {
		LOG.debug("updateSlsTransStatusFailure Method Start");
		try {
			Query query = new Query(Criteria.where(DBColumnConstants.UNIQUE_ID).is(uniqueId));		
			Update up = new Update();
			up.set(DBColumnConstants.SLS_TRANS_STATUS, statusFailure);
			up.set(DBColumnConstants.UPDATION_DATETIME, new Date());
			this.getMongoOps().updateMulti(query, up, LCAAppraisalTransactionVO.class, ConfigHandler.getLCAAppraisalTransactionCollectionName());
			return true;
		} catch (Exception e) {
			LOG.error(e);
			throw new UpdateException(e);
		}
		finally {
			LOG.debug("updateSlsTransStatusFailure Method End");
		}
		
	}
}
