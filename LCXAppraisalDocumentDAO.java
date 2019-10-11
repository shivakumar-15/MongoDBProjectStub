package com.freddiemac.lcax.common.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * Dao interface for CRUD operations on AppraisalDocument  
 * @author c44264
 *
 */


import com.freddiemac.lcax.common.entities.AppraisalDocumentVO;
import com.freddiemac.lcax.common.entities.LCASummaryFileVO;
import com.freddiemac.lcax.common.exception.CreateException;
import com.freddiemac.lcax.common.exception.FinderException;
import com.freddiemac.lcax.common.exception.UpdateException;
import com.freddiemac.lcax.common.util.DuplicateFSFiles;
import com.freddiemac.lcax.common.util.DuplicateSummaries;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;

public interface LCXAppraisalDocumentDAO {

	public boolean persistSummaryDataToAppraisalTable(AppraisalDocumentVO appDoc) throws CreateException;
	public boolean persistSummaryDataToAppraisalTable(List<AppraisalDocumentVO> appDocs) throws CreateException;
	/**public boolean deleteAppraisalDocuments(String summaryUniqueIdentifier) throws FinderException;*/
	public AppraisalDocumentVO getSingleAppraisal(String summaryUniqueIdentifier,String appraisalSurrogateIdentifier) throws FinderException;
	public List<AppraisalDocumentVO> getAppraisalList(String uniqueId) throws FinderException;
	public boolean updateScrubbedAddress(List<AppraisalDocumentVO> appraisalDocuments) throws UpdateException;
	public boolean updateAppraisalSLID(List<AppraisalDocumentVO> appDoc)throws UpdateException;	
	public InputStream retrieveFile(String fileName) throws FinderException;
	public List<DuplicateFSFiles> findDuplicateFiles() throws FinderException;
	/**public List<DuplicateSummaries> findDuplicateSummaries() throws FinderException;*/
	public List<GridFSDBFile> getDuplicateFiles(String fileName) throws FinderException;
	/**public List<LCASummaryFileVO> getDuplicateSummariesDate(Date endDateTime) throws FinderException;*/
	public boolean updateCCPStatus(String uniqueId,String status)throws UpdateException;
	public boolean updateCCPFileNames(AppraisalDocumentVO appraisal) throws UpdateException;
	public long saveCCPResponse(InputStream inputStream,String filename,String type,DBObject metaData) throws CreateException,IOException;
	public void deleteFile(String fileName)  throws FinderException;
	public void deleteFileByID(Object id)  throws FinderException;
}
