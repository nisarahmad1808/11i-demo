package inpatientWeb.pharmacy.interfaces.outbound.serviceImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import inpatientWeb.Global.ecw.ambulatory.CwMobile.CwUtils;
import inpatientWeb.Global.medicationhelper.constant.MedicationConstants.RxType;
import inpatientWeb.devices.util.DeviceUtils;
import inpatientWeb.pharmacy.interfaces.outbound.dao.DispensingInterfaceLogDAO;
import inpatientWeb.pharmacy.interfaces.outbound.dto.DispensingInterfaceLogModel;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants;
import inpatientWeb.utils.DateUtil;

@Service
@Lazy
@Scope("prototype")
public class DispensingInterfaceLogServiceImpl {

	@Autowired
	DispensingInterfaceLogDAO dispensingInterfaceLogDAO;
	
	private static final String STARTDATE = "startDate"; 
	private static final String ENDDATE = "endDate";
	private static final String TODATEFORMAT = "yyyy-MM-dd";
	private static final String FROMDATEFORMAT = "MM/dd/yyyy";
	private static final int NUMBEROFPREVIOUSDAYS = 30;
	private static final int UNWANTEDSQLSYNTAXLENGTH = 4;
	private static final String RESPONSEEXCEPTION = "ResponseException";
	
	private Map<String, Object> params = null;
	private StringBuilder sql;
	private StringBuilder countQuery;
	private String selectedStatus;
	private int providerId;
	private int facilityId;
	private int patientId;
	private String startDateValue;
	private String endDateValue;
	
	/**
	 * It creates the query based on filters and returns the Med Dispense Interface Log(list) and the number of records based on filter.
	 * @exception IllegalArgumentException
	 * @exception UnsupportedOperationException
	 * @exception StringIndexOutOfBoundsException
	 * @exception IllegalStateException
	 * @exception ClassCastException
	 * @exception IOException
	 * @param pathVariables
	 * @return String  - Med Dispense Interface Log(list)
	 * 				   - In case of exception :"ResponseException" 
	 */
	public String getMedicationDispenseLog(Map<String, String> pathVariables) {

		String response = null;
		String outboundLogQuery = null;
		String outboundLogCountQuery = null;
		params = new HashMap<>();
		int selectedPage;
		int recordsPerPage;
		String typeOfCall;
		Gson gson = new Gson();
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("MedDispensingLogData", RESPONSEEXCEPTION);
		try {
			sql = new StringBuilder("");
			countQuery = new StringBuilder("");
			selectedPage = Integer.parseInt(pathVariables.get("selectedPage"));
			recordsPerPage = Integer.parseInt( pathVariables.get("recordsPerPage"));
			selectedStatus = pathVariables.get("selectedStatus");
			providerId = Integer.parseInt(pathVariables.get("providerId"));
			facilityId = Integer.parseInt(pathVariables.get("facilityId"));
			patientId = Integer.parseInt(pathVariables.get("patientId"));
			startDateValue = pathVariables.get(STARTDATE);
			endDateValue = pathVariables.get(ENDDATE);
			typeOfCall = pathVariables.get("typeOfCall");

			if (!(("0").equals(startDateValue.trim()))) {
				// Convert date format from UI format to Db default format
				startDateValue = CwUtils.ConvertDateFormat(startDateValue, FROMDATEFORMAT, TODATEFORMAT);
			}else{
				startDateValue = DeviceUtils.getPreviousDateFromToday(TODATEFORMAT, NUMBEROFPREVIOUSDAYS);
			}
			if (!(("0").equals(endDateValue.trim()))) {
				// Convert date format from UI format to DB default format
				endDateValue = CwUtils.ConvertDateFormat(endDateValue, FROMDATEFORMAT, TODATEFORMAT);
			}else{
				endDateValue =  DateUtil.getTodaysDate(TODATEFORMAT);
			}
			
			sql.append("SELECT pagination_syntax ol.status, ol.createddatetime, ol.hl7message, il.data, ef.Name ");
			sql.append("FROM ").append(DispensingInterfaceConstants.MEDDISPENSINGINTERFACETABLES.TBL_IP_MEDDISPENSING_INTERFACE_OUTBOUND_LOG).append(" ol ");
			sql.append("INNER JOIN edi_facilities ef ON ef.id = ol.facilityid ");
			sql.append("INNER JOIN ").append(DispensingInterfaceConstants.MEDDISPENSINGINTERFACETABLES.TBL_IP_MEDDISPENSING_INTERFACE_DETAILS).append(" il ON il.id = ol.dispensinginterfacedetailid WHERE il.ishl7msgcreated = 1 AND ");
			
			countQuery.append("SELECT COUNT(ol.id) FROM ").append(DispensingInterfaceConstants.MEDDISPENSINGINTERFACETABLES.TBL_IP_MEDDISPENSING_INTERFACE_OUTBOUND_LOG).append(" ol "); 
			countQuery.append("INNER JOIN edi_facilities ef ON ef.id = ol.facilityid INNER JOIN ").append(DispensingInterfaceConstants.MEDDISPENSINGINTERFACETABLES.TBL_IP_MEDDISPENSING_INTERFACE_DETAILS).append(" il ON il.id = ol.dispensinginterfacedetailid WHERE il.ishl7msgcreated = 1 AND ");
			
			if (("onPageLoad").equals(typeOfCall)) {
				sql.append(" ol.facilityid =:facilityId AND CAST(ol.createddatetime AS DATE) =:startDate AND ");
				countQuery.append(" ol.facilityid =:facilityId AND CAST(ol.createddatetime AS DATE) =:startDate AND ");
				params.put("facilityId", facilityId);
				params.put(STARTDATE, startDateValue);

			} else {
				createQueryBasedOnSelectedFilters();
			}

			// finalizing select query and count query
			outboundLogQuery = sql.toString().trim();
			outboundLogCountQuery = countQuery.toString().trim();

			if (("AND").equalsIgnoreCase(outboundLogQuery.substring(outboundLogQuery.length() - UNWANTEDSQLSYNTAXLENGTH, outboundLogQuery.length()).trim())) {
				outboundLogQuery = outboundLogQuery.substring(0, outboundLogQuery.length() - UNWANTEDSQLSYNTAXLENGTH);
				outboundLogCountQuery = outboundLogCountQuery.substring(0, outboundLogCountQuery.length() - UNWANTEDSQLSYNTAXLENGTH);
			} 
			if (selectedPage != 0) {
				outboundLogQuery = "SELECT TOP " + recordsPerPage + " * FROM(" + outboundLogQuery.replace("pagination_syntax", " ROW_NUMBER() OVER ( ORDER BY ol.createddatetime DESC) AS ROWNUMBER,") + " ) AS X WHERE ROWNUMBER > " + ((selectedPage - 1) * recordsPerPage);
			}
			responseMap = dispensingInterfaceLogDAO.getMedicationDispenseLog(outboundLogQuery, outboundLogCountQuery, params);
			if(responseMap.get("MedDispensingLogData") != RESPONSEEXCEPTION) {
				responseMap.put("MedDispensingLogData", setDispensingInterfaceLogObjects((List<DispensingInterfaceLogModel>) responseMap.get("MedDispensingLogData")));
			}
			response =gson.toJson(responseMap);
			
		}catch (IllegalArgumentException | UnsupportedOperationException | StringIndexOutOfBoundsException | IllegalStateException | ClassCastException | IOException e) {
			inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog.AppendExceptionToLog(e);
			response = gson.toJson(responseMap);
		}
		return response;
	}

	/**
	 * API returns the Dispensing Interface Log Model objects
	 * @param dispensingLogReport
	 * @return List  - List of DispensingInterfaceLogModel objects
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	private List<DispensingInterfaceLogModel> setDispensingInterfaceLogObjects(List<DispensingInterfaceLogModel> dispensingLogReport) throws IOException{

		for(DispensingInterfaceLogModel dispensingInterfaceLogObj : dispensingLogReport ) {
			String msgContent = dispensingInterfaceLogObj.getData();
			Map<String, Object> msgContentMap = new ObjectMapper().readValue(msgContent, LinkedHashMap.class);
			dispensingInterfaceLogObj.setMeOrderType(String.valueOf(RxType.getRxType((Integer.parseInt(msgContentMap.get("rDispenseOrderType").toString())))));
			dispensingInterfaceLogObj.setProvider("" == msgContentMap.get("rOrderingProviderLName") ? String.valueOf(msgContentMap.get("rOrderingProviderFName")) : msgContentMap.get("rOrderingProviderLName")+", "+msgContentMap.get("rOrderingProviderFName"));
			dispensingInterfaceLogObj.setPatientName("" == msgContentMap.get("rPatientLName")? String.valueOf(msgContentMap.get("rPatientFName")) : msgContentMap.get("rPatientLName")+ ", "+msgContentMap.get("rPatientFName"));
			dispensingInterfaceLogObj.setLocation(msgContentMap.get("rPatientUnitLocation")+"/"+msgContentMap.get("rPatientRoomNo")+"/"+msgContentMap.get("rPatientBedNo"));
			setDrugDescription((List<Map<String, Object>>) msgContentMap.get("dipsenseRXDetails"), dispensingInterfaceLogObj);
			dispensingInterfaceLogObj.setApprovedBy("" == msgContentMap.get("oPhramacistLName")? String.valueOf(msgContentMap.get("oPhramacistFName")) : msgContentMap.get("oPhramacistLName")+", "+msgContentMap.get("oPhramacistFName"));
			dispensingInterfaceLogObj.setPatientId(Integer.parseInt(msgContentMap.get("rPatientId").toString()));
		}
		return dispensingLogReport;
	}
	
	/**
	 * API gets the Dispense RX Details and creates the string for Drug Description
	 * @param dispensingLogReport
	 * @return DispensingInterfaceLogModel  - Sets the drug description and returns the reference object
	 */
	private DispensingInterfaceLogModel setDrugDescription(List<Map<String, Object>> dipsenseRXDetails, DispensingInterfaceLogModel dispensingInterfaceLogObj){
		StringBuilder sb = new StringBuilder("");
		for(Map<String, Object> dipsenseRXDetailObj : dipsenseRXDetails ) {
			sb.append(String.valueOf(dipsenseRXDetailObj.get("rDispenseDrugDesc"))+",");
		}
		dispensingInterfaceLogObj.setDrugDesc(sb.toString().substring(0, sb.toString().length() -1));
		return dispensingInterfaceLogObj;
	}
	
	/**
	 * It creates query syntax on the basis of selected filters and appends to main query
	 * @return boolean  - True  : Executed Successfully
	 * 					- False : Error
	 */
	private boolean createQueryBasedOnSelectedFilters() {
		
		// creating dynamic query based on filters
		boolean bFlag = false;
		if (!(("Select").equals(selectedStatus.trim()))) {
			sql.append(" ol.status =:selectedStatus AND ");
			countQuery.append(" ol.status =:selectedStatus AND ");
			params.put("selectedStatus", selectedStatus);
		}

		if (providerId != 0) {
			sql.append(" ol.orderingproviderid =:providerId AND ");
			countQuery.append(" ol.orderingproviderid =:providerId AND ");
			params.put("providerId", providerId);
		}
		
		if ( facilityId != 0 ) {
			sql.append(" ol.facilityid =:faciltyId AND ");
			countQuery.append(" ol.facilityid =:faciltyId AND ");
			params.put("faciltyId", facilityId);
		}
		
		if ( patientId !=0 ) {
			sql.append(" ol.patientid =:patientId AND ");
			countQuery.append(" ol.patientid =:patientId AND ");
			params.put("patientId", patientId);
		}

		if ((!(("0").equals(startDateValue.trim()))) && (!(("0").equals(endDateValue.trim())))) {
			sql.append(" (CAST(ol.createddatetime AS DATE) BETWEEN :startDate AND :endDate) ");
			countQuery.append(" (CAST(ol.createddatetime AS DATE) BETWEEN :startDate AND :endDate) ");
			params.put(STARTDATE, startDateValue);
			params.put(ENDDATE, endDateValue);
		} else if ((("0").equals(endDateValue.trim())) && (!(("0").equals(startDateValue.trim())))) {
				sql.append(" (CAST(ol.createddatetime AS DATE) BETWEEN :startDate AND :startDate1) ");
				countQuery.append(" (CAST(ol.createddatetime AS DATE) BETWEEN :startDate AND :startDate1) ");
				params.put(STARTDATE, startDateValue);
				params.put("startDate1", startDateValue);
		} else if ((("0").equals(startDateValue.trim()) ) && (!(("0").equals(endDateValue.trim()))) ) {
				sql.append(" (CAST(ol.createddatetime AS DATE) BETWEEN :endDate AND :endDate1) ");
				countQuery.append(" (CAST(ol.createddatetime AS DATE) BETWEEN :endDate AND :endDate1) ");
				params.put(ENDDATE, endDateValue);
				params.put("endDate1", endDateValue);
		}

		bFlag = true;
		return bFlag;
	}
}
