package com.freddiemac.lcax.common.dao.impl;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.data.mongodb.core.MongoOperations;

import com.freddiemac.lcax.common.config.ConfigHandler;
import com.freddiemac.lcax.common.dao.LCAXLastSummaryDAO;
import com.freddiemac.lcax.common.entities.LastSummaryVO;
import com.freddiemac.lcax.common.exception.FinderException;
import com.freddiemac.lcax.common.exception.UpdateException;
import com.freddiemac.lcax.common.logger.LogFactory;
import com.freddiemac.lcax.common.logger.Logger;
import com.freddiemac.lcax.common.mongo.factory.LocalMongoTemplateFactory;
/**
 * DAO implemenation to maintain the end time of the last accepeted file
 * @author c44264
 *
 */
public class LCAXLastSummaryDAOImpl implements LCAXLastSummaryDAO {

	private static final Logger LOG = LogFactory.getLogger(LCAXLastSummaryDAOImpl.class);
	
	private MongoOperations mongoOps;
	LocalMongoTemplateFactory mongoTemplateFactory;

	String collectionName;

	public LCAXLastSummaryDAOImpl(LocalMongoTemplateFactory mongoTemplateFactory) {
		collectionName = ConfigHandler.getLCAXLastSummaryCollectionName();
		this.mongoOps = mongoTemplateFactory.getMongoTemplate();
	}

	private MongoOperations getMongoOps() {
		if(mongoOps==null){
			mongoOps=mongoTemplateFactory.getMongoTemplate();
		}
		return mongoOps;
	}
	
	@Override
	public boolean updateLastSummaryData(LastSummaryVO lastSummary) throws UpdateException {
		try {
			this.getMongoOps().dropCollection(collectionName);
			lastSummary.setUpdationDateTime(new Date());
			this.getMongoOps().insert(lastSummary, collectionName);
			return true;
		} catch (Exception e) {
			LOG.error("Exception while updateLastSummaryData : "+e);
			return false;
		}
	}

	@Override
	public LastSummaryVO getLastSummaryData() throws FinderException {

		try {
			List<LastSummaryVO> lastSummaryList = this.getMongoOps().findAll(LastSummaryVO.class, collectionName);
			if (lastSummaryList.size() == 1) {
				return lastSummaryList.get(0);
			} else {
				LastSummaryVO lastSummary = new LastSummaryVO();
				LocalDate date1 = LocalDate.now();
				Calendar calendar = Calendar.getInstance();
				int timezoneOffset = (calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET)) / (60 * 1000);				
				
				XMLGregorianCalendar endTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(date1.getYear(), date1.getMonth().getValue(),
						date1.getDayOfMonth(), 0, 0, 0, 0, timezoneOffset);	
				lastSummary.setEndDateTime(endTime.toGregorianCalendar().getTime());
				Date currentTime=new Date();
				lastSummary.setCreationDateTime(currentTime);
				lastSummary.setUpdationDateTime(currentTime);
				if (updateLastSummaryData(lastSummary)) {
					return lastSummary;
				}
			}
		} catch (Exception e) {
			LOG.error("Exception while getLastSummaryData : "+e);
			return null;
		}
		return null;

	}

}
