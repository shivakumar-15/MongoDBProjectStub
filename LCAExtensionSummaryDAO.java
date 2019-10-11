package com.freddiemac.lcax.common.dao;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
/**
 * Dao interface for LCAExtension summary file
 */

import com.freddiemac.lcax.common.entities.LCASummaryFileVO;
import com.freddiemac.lcax.common.exception.CreateException;
import com.freddiemac.lcax.common.exception.FinderException;
import com.freddiemac.lcax.common.exception.UpdateException;
import com.mongodb.CommandResult;
import com.mongodb.DBObject;

public interface LCAExtensionSummaryDAO {
	
	public boolean persistLCAEXtensionFileData(LCASummaryFileVO summaryFile,ByteArrayOutputStream bos) throws CreateException;
	
	public boolean deleteSummaryFile(String summaryUniqueIdentifier) throws FinderException;
	
	public LCASummaryFileVO getSummaryFile(String uniqueId) throws FinderException;
	
	public List<LCASummaryFileVO> getFailedSummaryIds() throws FinderException;
	
	public void storeFileToGridFs(InputStream is,String fileName,DBObject metaData) throws CreateException;
	
	public boolean updateRDSStatus(String uniqueId,String status) throws UpdateException;
	
	public boolean updateSLSStatus(String uniqueId,String status) throws UpdateException;
	
	public boolean updatePMLStatus(String uniqueId,String status) throws UpdateException;
	
	public boolean updateCCPStatus(String uniqueId,String status) throws UpdateException;
	
	public boolean updateDocSSStatus(String uniqueId,String status) throws UpdateException;
	
	public boolean updateBamSlsStatus(String uniqueId, String status) throws UpdateException;
	
	public boolean updateBamPmlStatus(String uniqueId, String status) throws UpdateException;
	
	public List<LCASummaryFileVO> getSummaryFileByDate(Date startDate , Date endDate ) throws FinderException;
	
	public List<LCASummaryFileVO> getInProgressSummaryFiles(Date date) throws FinderException;
	
	public List<LCASummaryFileVO> getDocssSummaryFileByDate(Date startPeriod, Date endPeriod) throws FinderException;
	
	public CommandResult runCommandinDB(String query) throws FinderException;
}
