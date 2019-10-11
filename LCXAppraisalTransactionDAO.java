package com.freddiemac.lcax.common.dao;

import java.util.Date;
import java.util.List;

import com.freddiemac.lcax.common.entities.CCPTransactionVO;
import com.freddiemac.lcax.common.entities.LCAAppraisalTransactionVO;
import com.freddiemac.lcax.common.exception.CreateException;
import com.freddiemac.lcax.common.exception.FinderException;
import com.freddiemac.lcax.common.exception.UpdateException;
import com.mongodb.WriteResult;

/**
 * Dao interface for maintaining the transaction details which can be used for Retry mechanisam
 * @author c44264
 *
 */

public interface LCXAppraisalTransactionDAO {

	public boolean persistTransactionElements(LCAAppraisalTransactionVO transVO) throws CreateException;
	
	public boolean persistTransactionList(List<LCAAppraisalTransactionVO> appTransList) throws CreateException;
	
/**	public boolean deleteAppraisalTransactions(String summaryUniqueIdentifier) throws FinderException; **/

	public LCAAppraisalTransactionVO getTransactionElement(String appraisalSurrogateIdentifier) throws FinderException;
	
	public LCAAppraisalTransactionVO getTransactionByDocSSRequestId(String docCorrelationId) throws FinderException;

	public boolean updatePMLElements(String correlationId, LCAAppraisalTransactionVO transVO) throws UpdateException;
	
	public WriteResult updatePMLResponseElements(String pmlMessageIdentifier, LCAAppraisalTransactionVO transVO) throws UpdateException;
	
	public boolean updateDocSSElements(String appraisalSurrogateIdentifier, LCAAppraisalTransactionVO transVO) throws UpdateException;

	public boolean updateCCPElements(String correlationId, LCAAppraisalTransactionVO transVO) throws UpdateException;

	public List<LCAAppraisalTransactionVO> getFailedPMLTransactions(String summaryUniqueIdentifier) throws FinderException;
	
	public List<LCAAppraisalTransactionVO> getFailedBAMTransactionsforPML(String summaryUniqueIdentifier) throws FinderException ;
	
	public List<LCAAppraisalTransactionVO> getFailedDocSSTransactions(String uniqueId) throws FinderException;
	
	public List<LCAAppraisalTransactionVO> getTransactionList(String uniqueId) throws FinderException;
	
	public boolean updatePMLAppraisalStatus(String summaryUniqueIdentifier,String appraisalSurrogateIdentifier,String status) throws UpdateException;
	
	public boolean updateBAMAppraisalStatusforPML(String summaryUniqueIdentifier,String appraisalSurrogateIdentifier,String status) throws UpdateException;
	
	public boolean updateCCPAppraisalStatus(String summaryUniqueIdentifier,String appraisalSurrogateIdentifier,String status,String fileType) throws UpdateException ;
	
	public boolean updateDOCSSAppraisalStatus(String summaryUniqueIdentifier,String appraisalSurrogateIdentifier,String status,String fileType) throws UpdateException ;
	
	public boolean updateCCPResponse(CCPTransactionVO responsevo, String summaryUniqueIdentifier) throws  UpdateException;
	
	public List<LCAAppraisalTransactionVO> getFailedCCPTransactions(String summaryUniqueIdentifier) throws FinderException;
	
	public List<LCAAppraisalTransactionVO> getInProgressTransactions(Date date, String summaryUniqueIdentifier) throws FinderException;
	
	public List<LCAAppraisalTransactionVO> getInProgressDOCSSTransactions(String summaryUniqueIdentifier) throws FinderException ;
	
	public List<LCAAppraisalTransactionVO> getInProgressPMLTransactions(String summaryUniqueIdentifier) throws FinderException ;

	public List<LCAAppraisalTransactionVO> getUndefinedPMLTransactions(String summaryUniqueIdentifier) throws FinderException;
	
	public List<LCAAppraisalTransactionVO> getUndefinedBAMTransactionsforPML(String summaryUniqueIdentifier) throws FinderException;
	
	public List<LCAAppraisalTransactionVO> getUndefinedCCPTransactions(String summaryUniqueIdentifier) throws FinderException;
	
	public List<LCAAppraisalTransactionVO> getUndefinedDOCSSTransactions(String summaryUniqueIdentifier) throws FinderException;

	public boolean updateSlsTransactionStatus(String appraisalSurrogateIdentifier, String slsStatus) throws UpdateException;

	public List<LCAAppraisalTransactionVO> getFailedSLSTransactions(String summaryUniqueIdentifier) throws FinderException;

	public boolean updateSlsTransStatusFailure(String uniqueId, String statusFailure) throws UpdateException;
}
