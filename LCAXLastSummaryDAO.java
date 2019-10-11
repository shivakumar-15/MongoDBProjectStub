package com.freddiemac.lcax.common.dao;

import com.freddiemac.lcax.common.entities.LastSummaryVO;
import com.freddiemac.lcax.common.exception.FinderException;
import com.freddiemac.lcax.common.exception.UpdateException;
/**
 * Dao interface for updating the timestamp with the last accepted file  
 * @author c44264
 *
 */

public interface LCAXLastSummaryDAO {
	
	public boolean updateLastSummaryData(LastSummaryVO lastSummary) throws UpdateException;
	
	public LastSummaryVO getLastSummaryData() throws FinderException;
	
}
