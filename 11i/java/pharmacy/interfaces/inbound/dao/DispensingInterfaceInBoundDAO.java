package inpatientWeb.pharmacy.interfaces.inbound.dao;

import inpatientWeb.Global.service.EcwAppContext;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.DISPENSINGSTOCKAREATABLES;
import inpatientWeb.utils.IPTzUtils;
import inpatientWeb.utils.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DispensingInterfaceInBoundDAO {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;	
	
	public DispensingInterfaceInBoundDAO(){
		namedParameterJdbcTemplate = (NamedParameterJdbcTemplate) EcwAppContext.getObject("namedParameterJdbcTemplate");
	}
	/**
	 * API to get drugDispenseCode quantity details for given  drugDispenseCode and dispenseStockId
	 * @param drugDispenseCode
	 * @param dispenseStockId
	 * @return Map<String, Object> - Drug dispense code quantity details map
	 */
	public Map<String, Object> getDrugDispenseCodeQtyDetailMap(String drugDispenseCode,String dispenseStockId) {
		Map<String, Object> paramMap = new HashMap<>();
		Map<String, Object> dataMap = null;
		try{
			StringBuilder strBuilderQuery = new StringBuilder("SELECT stockQtyMapp.id AS id,stockQtyMapp.quantity AS quantity FROM ")
			.append(DISPENSINGSTOCKAREATABLES.TBL_IP_DISPENSE_STOCKAREA_FORMULARY_QTYMAPPING +" AS stockQtyMapp ")
			.append(" WHERE stockQtyMapp.drugdispensecode=:drugDispenseCode AND stockQtyMapp.dispensestockid=:dispenseStockId");
			paramMap.put("drugDispenseCode", drugDispenseCode);
			paramMap.put("dispenseStockId", dispenseStockId);
			dataMap=namedParameterJdbcTemplate.queryForMap(strBuilderQuery.toString(), paramMap);
		}catch(EmptyResultDataAccessException ex){
			dataMap=null;
		}
		return dataMap;
	}
	/**
	 * API will get the formularyId associated for given drugDispenseCode
	 * @param dispenseDrugCode
	 * @return formularyId 
	 */
	public int getFormularyIdForGivenDrugDispenseCode(String dispenseDrugCode) {
		Map<String, Object> paramMap = new HashMap<>();
		int formularyId=0;
		try{
			StringBuilder strBuilderQuery = new StringBuilder(" SELECT id FROM ").append(DISPENSINGSTOCKAREATABLES.TBL_IP_DRUGFORMULARY).append(" WHERE drugdispensecode=:dispenseDrugCode;");
			paramMap.put("dispenseDrugCode", dispenseDrugCode);
			formularyId=namedParameterJdbcTemplate.queryForObject(strBuilderQuery.toString(), paramMap,Integer.class);
		}catch(EmptyResultDataAccessException ex){
			formularyId=0;
		}
		return formularyId;
	}
	/**
	 * API to update quantity detail for drugDispenseCode and Stock Area using dispenseFormularyQtyMapId and stockStatus
	 * @param qty
	 * @param dispenseFormularyQtyMapId
	 * @param stockStatus
	 * @param userId
	 * @return 1 - If updated else 0
	 */
	public int updateQtyDetail(int qty,int dispenseFormularyQtyMapId,String stockStatus,Integer userId) {
		Map<String, Object> paramMap = new HashMap<>();
		StringBuilder strBuilderQuery = new StringBuilder(" UPDATE ").append(DISPENSINGSTOCKAREATABLES.TBL_IP_DISPENSE_STOCKAREA_FORMULARY_QTYMAPPING)
	    .append(" SET  quantity=:qty, modifieddatetime=:modifiedtime, modifiedby=:modifiedby,status=:stockStatus")
		.append(" WHERE id = :dispenseFormularyQtyMapId");
		paramMap.put("stockStatus", stockStatus);
		paramMap.put("modifiedby", userId);
		paramMap.put("modifiedtime", IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT));
		paramMap.put("dispenseFormularyQtyMapId", dispenseFormularyQtyMapId);
		paramMap.put("qty", qty);
		return namedParameterJdbcTemplate.update(strBuilderQuery.toString(), paramMap);
	}
	/**
	 * API to insert quantity detail information for given drugDispenseCode,dispenseStockId and FormularyId
	 * @param dispenseStockId
	 * @param formularyId
	 * @param drugDispenseCode
	 * @param qty
	 * @param stockStatus
	 * @param userId
	 * @return 1 if inserted else 0
	 */
	public int insertQtyDetail(String dispenseStockId,int formularyId,String drugDispenseCode,String qty,String stockStatus,Integer userId) {
		Map<String, Object> paramMap = new HashMap<>();
		StringBuilder strBuilderQuery = new StringBuilder(" INSERT  INTO ").append(DISPENSINGSTOCKAREATABLES.TBL_IP_DISPENSE_STOCKAREA_FORMULARY_QTYMAPPING)
		.append("(formularyid,dispensestockid, drugdispensecode,quantity,createdby,createddatetime,modifiedby,modifieddatetime,status) VALUES ")
		.append(" (:formularyId,:dispenseStockId,:drugDispenseCode,:qty,:createdBy,:createdDateTime,:modifiedBy,:modifiedDateTime,:stockStatus)");
		paramMap.put("formularyId", formularyId);
		paramMap.put("dispenseStockId", dispenseStockId);
		paramMap.put("drugDispenseCode", drugDispenseCode);
		paramMap.put("qty", qty);
		paramMap.put("createdBy", userId);
		paramMap.put("createdDateTime", IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT));
		paramMap.put("modifiedBy", userId);
		paramMap.put("modifiedDateTime", IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT));	
		paramMap.put("stockStatus", stockStatus);	
		return Util.insertAndReturnId(namedParameterJdbcTemplate, strBuilderQuery.toString(), paramMap, "id");
	}
	
	/**
	 * API will get list of stock area details for given medication dispensing station name
	 * @param dispensingStationMachine - Unique value given to each medication dispensing station name 
	 * @return Map<String,Object> - Stock Area Detail information associated to dispensingStationMachine or else null  
	 */
	public Map<String,Object> getStockAreaDetailBasedOnExternalMachine(String dispensingStationMachine){
		Map<String,Object> paramMap= new HashMap<>();
		Map<String,Object> dataMap= null;
		try{
			StringBuilder stockAreaQuery = new StringBuilder("SELECT dispsa.id AS dispenseStockId,dispsa.name AS dispenseStockAreaName,dispsa.location,dispsa.chargeondispense,dispsa.updateinventory,dispsa.usecharge");
			stockAreaQuery.append(" FROM "+ DISPENSINGSTOCKAREATABLES.TBL_IP_DISPENSE_STOCKAREA + " AS dispsa INNER JOIN "+ DISPENSINGSTOCKAREATABLES.TBL_IP_DISPENSE_STOCKAREA_EXTERNAL_MAPPING +" AS dispextlocmap ON dispsa.id=dispextlocmap.dispensestockid"); 
			stockAreaQuery.append(" WHERE  dispsa.deleteflag=0 AND dispextlocmap.deleteflag=0 AND"); 
			stockAreaQuery.append(" dispextlocmap.externalmachinename=:externalMachine");
			paramMap.put("externalMachine", dispensingStationMachine);
			dataMap=namedParameterJdbcTemplate.queryForMap(stockAreaQuery.toString(), paramMap);
		}catch(EmptyResultDataAccessException ex){
			dataMap=null;
		}
		return dataMap;
	}
	/**
	 * API to log the hl7 inbound
	 * @param inBoundMessageMap
	 * @return int - Last inserted inbound message log id
	 */
	public int logInBoundMessage(Map<String, Object> inBoundMessageMap) {
		StringBuilder strBuilderQuery = new StringBuilder(" INSERT INTO ").append(DispensingInterfaceConstants.MEDDISPENSINGINTERFACETABLES.TBL_IP_MEDDISPENSING_INTERFACE_INBOUND_LOG)
				.append("(hl7message, status,datereceived,ackmessage,dateacksent,ack,createdby) VALUES ")
				.append("(:hl7Message,:status,:dateReceived,:ackMessage,:dateAckSent,:ack,:createdBy)");
		return Util.insertAndReturnId(namedParameterJdbcTemplate, strBuilderQuery.toString(), inBoundMessageMap, "id");
	}
	/**
	 * API to update the inbound message details
	 * @param inBoundMessageMap
	 * @return int 0 - Failed to update or greater then 1 if update successfully 
	 */
	public int updateInboundMessage(Map<String, Object> inBoundMessageMap) {
		StringBuilder strBuilderQuery = new StringBuilder(" UPDATE  ").append(DispensingInterfaceConstants.MEDDISPENSINGINTERFACETABLES.TBL_IP_MEDDISPENSING_INTERFACE_INBOUND_LOG)
		.append(" SET status=:Status,statusmessage=:Message,messagetype=:messageType,updatedon=:updatedOn")
		.append(" WHERE id=:logId");
		return namedParameterJdbcTemplate.update(strBuilderQuery.toString(), inBoundMessageMap);
	}
	/**
	 * API to log inbound message from inbound engine
	 * @param inBoundMessageMap 
	 * @return int - Last inserted inbound message log id
	 */
	public int logMessageFromInboundEngine(Map<String, Object> inBoundMessageMap) {
		StringBuilder strBuilderQuery = new StringBuilder(" INSERT INTO ").append(DispensingInterfaceConstants.MEDDISPENSINGINTERFACETABLES.TBL_IP_MEDDISPENSING_INTERFACE_INBOUND_LOG)
				.append("(hl7message, status,datereceived,ackmessage,dateacksent,ack,createdby) VALUES ")
				.append("(:hl7Message,:status,:dateReceived,:ackMessage,:dateAckSent,:ack,:createdBy)");
		return Util.insertAndReturnId(namedParameterJdbcTemplate, strBuilderQuery.toString(), inBoundMessageMap, "id");
	}
	/**
	 * API to get all pending message for the given dispensing interface id
	 * @return List<Map<String, Object>> - List of inbound message details map 
	 */
	public List<Map<String, Object>> getPendingInboundMessages() {
		Map<String, Object> inBoundMessagesMap=new HashMap<>();
		StringBuilder strBuilderQuery = new StringBuilder(" SELECT id, hl7message FROM ")
				.append(DispensingInterfaceConstants.MEDDISPENSINGINTERFACETABLES.TBL_IP_MEDDISPENSING_INTERFACE_INBOUND_LOG)
				.append(" WHERE status='pending'");
		return namedParameterJdbcTemplate.queryForList(strBuilderQuery.toString(), inBoundMessagesMap);
	}
	/**
	 * API to log the ZPM hl7 inbound message details
	 * @param inBoundZPMMessageMap
	 * @return int - Last inserted inbound ZPM message detail id
	 */
	public int logInBoundZPMMessageDetail(Map<String, Object> inBoundZPMMessageDetailMap) {
		StringBuilder strBuilderQuery = new StringBuilder(" INSERT INTO  ").append(DispensingInterfaceConstants.MEDDISPENSINGINTERFACETABLES.TBL_IP_MEDDISPENSING_INTERFACE_ZPM_DETAIL)
		.append("(logid,messageid,dispensinginterfaceid,dispensestockid,hl7message,existingqty,drugdispensecode,drugdescription,stationname,transactiontype,transactionamt,availabledrugcount,transactionuserid,transactionusername,transactiondatetime,createdon,updatedon,updatedby,reconcile,statusmessage,status) VALUES ")
		.append("(:logId,:messageId,:dispensingInterfaceId,:dispenseStockId,:hl7Message,:existingQty,:transactionDrugDispenseCode,:transactionDrugDescription,:transactionStationName,:transactionType,:transactionAmount,:transactionTotalDrugCount,:transactionUserId,:transactionUserName,:transactionDateTime,:createdOn,:updatedOn,:updatedBy,:reconcile,:statusMessage,:status)");
		return Util.insertAndReturnId(namedParameterJdbcTemplate, strBuilderQuery.toString(), inBoundZPMMessageDetailMap, "id");
	}
	/**
	 * API to log the DFT hl7 inbound message profile order details
	 * @param inBoundDFTMessageDetailMap
	 * @return int - Last inserted inbound DFT message profile order detail id
	 */
	public int logInBoundDFTMessageProfileOrderDetail(Map<String, Object> inBoundDFTMessageDetailMap) {
		StringBuilder strBuilderQuery = new StringBuilder(" INSERT INTO  ").append(DispensingInterfaceConstants.MEDDISPENSINGINTERFACETABLES.TBL_IP_MEDDISPENSING_INTERFACE_DFT_PROFILEORDER_DETAIL)
		.append("(logid,messageid,dispensinginterfaceid,dispensestockid,patientid,medorderid,hl7message,existingqty,drugdispensecode,drugdescription,stationname,transactiontype,transactionamt,availabledrugcount,transactionuserid,transactionusername,transactiondatetime,createdon,updatedon,updatedby,reconcile,statusmessage,status) VALUES ")
		.append("(:logId,:messageId,:dispensingInterfaceId,:dispenseStockId,:patientId,:medOrderId,:hl7Message,:existingQty,:transactionDrugDispenseCode,:transactionDrugDescription,:transactionStationName,:transactionType,:transactionAmount,:transactionTotalDrugCount,:transactionUserId,:transactionUserName,:transactionDateTime,:createdOn,:updatedOn,:updatedBy,:reconcile,:statusMessage,:status)");
		return Util.insertAndReturnId(namedParameterJdbcTemplate, strBuilderQuery.toString(), inBoundDFTMessageDetailMap, "id");
	}
	/**
	 * API to log the DFT hl7 inbound message override order details
	 * @param inBoundDFTMessageDetailMap
	 * @return int - Last inserted inbound DFT message override order detail id
	 */
	public int logInBoundDFTMessageOverridOrderDetail(Map<String, Object> inBoundDFTMessageDetailMap) {
		StringBuilder strBuilderQuery = new StringBuilder(" INSERT INTO  ").append(DispensingInterfaceConstants.MEDDISPENSINGINTERFACETABLES.TBL_IP_MEDDISPENSING_INTERFACE_DFT_OVERRIDEORDER_DETAIL)
		.append("(logid,messageid,dispensinginterfaceid,dispensestockid,patientid,medorderid,hl7message,existingqty,drugdispensecode,drugdescription,stationname,transactiontype,transactionamt,availabledrugcount,transactionuserid,transactionusername,transactiondatetime,createdon,updatedon,updatedby,reconcile,statusmessage,status) VALUES ")
		.append("(:logId,:messageId,:dispensingInterfaceId,:dispenseStockId,:patientId,:medOrderId,:hl7Message,:existingQty,:transactionDrugDispenseCode,:transactionDrugDescription,:transactionStationName,:transactionType,:transactionAmount,:transactionTotalDrugCount,:transactionUserId,:transactionUserName,:transactionDateTime,:createdOn,:updatedOn,:updatedBy,:reconcile,:statusMessage,:status)");
		return Util.insertAndReturnId(namedParameterJdbcTemplate, strBuilderQuery.toString(), inBoundDFTMessageDetailMap, "id");
	}
}
