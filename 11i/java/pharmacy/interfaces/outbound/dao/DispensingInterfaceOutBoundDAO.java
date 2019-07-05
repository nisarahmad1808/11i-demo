package inpatientWeb.pharmacy.interfaces.outbound.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import inpatientWeb.Global.service.EcwAppContext;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.DispensingInterfaceStatus;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceCustomException;
import inpatientWeb.utils.Util;

@Repository
public class DispensingInterfaceOutBoundDAO {

	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public DispensingInterfaceOutBoundDAO(){
		if(null==namedParameterJdbcTemplate)
			namedParameterJdbcTemplate = (NamedParameterJdbcTemplate) EcwAppContext.getObject("namedParameterJdbcTemplate");
	}
	/**
	 * API to insert/log the HL7 Outbound Message
	 * @exception DataAccessException
	 * @throws DispensingInterfaceCustomException
	 * @param paramMap
	 * @return True  - If Hl7 OutBound Message is logged successfully.
	 *         False - If Hl7 OutBound Message is not logged successfully.
	 */
	public boolean logDispensingHl7OutBoundMessage(Map<String, Object> paramMap) throws DispensingInterfaceCustomException {
		boolean status = false;
		StringBuilder query = new StringBuilder();
		try {
			query.append("INSERT INTO " + DispensingInterfaceConstants.MEDDISPENSINGINTERFACETABLES.TBL_IP_MEDDISPENSING_INTERFACE_OUTBOUND_LOG +" (messageid, dispensinginterfaceid, dispenseordertype, dispensemsgtype,  ");
			query.append(" dispensecomponenttype, hl7message, status, createddatetime, loggedinuser, patientid, rxorderid, encounterid, facilityid, orderingproviderid, dispensinginterfacedetailid  ) ");
			query.append(" VALUES(:messageid, :dispensinginterfaceid, :dispenseordertype, :dispensemsgtype, ");
			query.append(":dispensecomponenttype, :hl7message, :status, :createddatetime, :loggedInUser, :patientid, :rxorderid, :encounterid, :facilityid, :orderingproviderid , :dispensinginterfacedetailid )");
			if(namedParameterJdbcTemplate.update(query.toString(), paramMap) == 1)
				status = true;
		}catch (DataAccessException e) {
			inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog.AppendExceptionToLog(e);
			throw new DispensingInterfaceCustomException(DispensingInterfaceStatus.DBERRORMESSAGE);
		}
		return status;
	}

	/**
	 * API updates/logs pending HL7 message's ACK/NACK in batch
	 * @exception DataAccessException
	 * @param ackMessageMapArray
	 * @return True  - If Hl7 OutBound Message ACK is logged successfully.
	 *         False - If Hl7 OutBound Message ACK is not logged successfully.
	 */
	public boolean logOutboundMessageACK(Map<String,Object>[] ackMessageMapArray){
		boolean bFlag = false;
		try{
			String updateQuery = "UPDATE " + DispensingInterfaceConstants.MEDDISPENSINGINTERFACETABLES.TBL_IP_MEDDISPENSING_INTERFACE_OUTBOUND_LOG +" SET status=:status, ack=:ack, ackmessage=:ackmessage, ackstatus=:ackstatus, sentdatetime=:sentdatetime, receiveddatetime=:receiveddatetime, errormessage=:errormessage WHERE id=:id";
			int[] count = namedParameterJdbcTemplate.batchUpdate(updateQuery, ackMessageMapArray);
			if(count.length == ackMessageMapArray.length) {
				bFlag = true;
			}
		}catch (DataAccessException e) {
			inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog.AppendExceptionToLog(e);
		}
		return bFlag;
	}
	
	/**
	 * API returns the HL7 pending messages 
	 * @exception EmptyResultDataAccessException
	 * @param dispensingInterfaceId
	 * @return Map - Key : Id, Value :  hl7message
	 */
	public Map<Integer, Object> getOutBoundMessages(int dispensingInterfaceId) {
		Map<Integer, Object> outBoundMessages = null;
		try {
			String query = "SELECT id, hl7message FROM "+ DispensingInterfaceConstants.MEDDISPENSINGINTERFACETABLES.TBL_IP_MEDDISPENSING_INTERFACE_OUTBOUND_LOG +" WHERE dispensinginterfaceid =:dispensingInterfaceId and status='pending' and delflag = 0";
			SqlParameterSource namedParameter = new MapSqlParameterSource().addValue("dispensingInterfaceId", dispensingInterfaceId);
			outBoundMessages = namedParameterJdbcTemplate.query(query, namedParameter, new ResultSetExtractor<Map<Integer, Object>>() {
				@Override
				public Map<Integer, Object> extractData(ResultSet rs) throws SQLException{
					Map<Integer, Object> map = new TreeMap<>();
					while (rs.next()) {
						map.put(rs.getInt("id"),rs.getString("hl7message"));
					}
					return map;
				}
			});
		}catch (EmptyResultDataAccessException ex) {
			 outBoundMessages = null;
		}
		return outBoundMessages;
	}
	
	/**
	 * Logs the received Message from pharmacy
	 * @param paramMap
	 * @return logId  - 0 if data not inserted
	 * 				  - inserted logid, if data is inserted   
	 * @throws DispensingInterfaceCustomException
	 * @exception DataAccessException 
	 */
	public int logDispensingInterfaceMsgContent(Map<String, Object> paramMap) throws DispensingInterfaceCustomException {
		int id = 0;
		try{
			StringBuilder query = new StringBuilder();	
			query.append("INSERT INTO " + DispensingInterfaceConstants.MEDDISPENSINGINTERFACETABLES.TBL_IP_MEDDISPENSING_INTERFACE_DETAILS+" (context, data, status, createddate,");
			query.append(" modifieddate, ishl7msgcreated, medorderid ) ");
			query.append(" VALUES(:context, :data, :Status, :createddate, :modifieddate, :ishl7msgcreated, :medorderid ) ");
			id = Util.executeAndReturnKeyForSQLServer( query.toString(), new MapSqlParameterSource(paramMap));
			
		}catch (DataAccessException e) {
			inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog.AppendExceptionToLog(e);
			throw new DispensingInterfaceCustomException(DispensingInterfaceStatus.DBERRORMESSAGE);
		}
		return  id;
		
	}
	
	/**
	 * Update the hl7 message log status whether received message is processed or not - status can be failed or success
	 * @param paramMap
	 * @return true   - if status is updated
	 * 		   false  - if status is not updated
	 * @throws DataAccessException 
	 */
	public boolean updateLogDispensingInterfaceMsgContent(Map<String, Object> paramMap){
		boolean status = false;
		try{
			StringBuilder query = new StringBuilder();
			query.append("UPDATE " + DispensingInterfaceConstants.MEDDISPENSINGINTERFACETABLES.TBL_IP_MEDDISPENSING_INTERFACE_DETAILS+" SET modifieddate = :modifieddate, ");
			query.append(" status = :Status , ishl7msgcreated = :ishl7msgcreated, errormessage = :errormessage WHERE id= :id ");
			if(namedParameterJdbcTemplate.update(query.toString(), paramMap) == 1) {
				status = true;
			}
		}catch (DataAccessException e) {
			inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog.AppendExceptionToLog(e);
		}
		return  status;
		
	}
}
