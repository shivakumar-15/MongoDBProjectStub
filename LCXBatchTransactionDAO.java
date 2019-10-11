package com.freddiemac.lcax.common.dao;

import com.freddiemac.lcax.common.entities.DOCSSBatchTransactionVO;
import com.freddiemac.lcax.common.entities.LCATransactionTableVO;
import com.freddiemac.lcax.common.exception.CreateException;
import com.freddiemac.lcax.common.exception.FinderException;
import com.freddiemac.lcax.common.exception.UpdateException;

/**
 * Dao interface for maintaining the transaction details which can be used for Retry mechanisam
 * @author c44264
 *
 */

public interface LCXBatchTransactionDAO {

	public boolean persistTransactionElements(LCATransactionTableVO transVO) throws CreateException;
	
	public boolean deleteBatchTransaction(String summaryUniqueIdentifier) throws FinderException;

	public LCATransactionTableVO getTransactionElements(String uniqueId) throws FinderException;
	
	public LCATransactionTableVO getSLSTransactionElements(String uniqueId) throws FinderException;

	public boolean updateRDSElements(String uniqueId, String request, String response,int index) throws UpdateException;

	public boolean updateSLSElements(String uniqueId, String request, String response,String requestIdentifier,int index) throws UpdateException;
	
	public boolean updateBAMSlsElements(String uniqueId, String request, int index) throws UpdateException;

	public boolean updateDocSSElements(String uniqueId, DOCSSBatchTransactionVO docBatchVO ,int index) throws UpdateException;
	
	public boolean updateRetryCount(LCATransactionTableVO transVO) throws UpdateException;

}
