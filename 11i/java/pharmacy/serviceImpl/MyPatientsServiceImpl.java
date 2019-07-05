package inpatientWeb.pharmacy.serviceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import inpatientWeb.Auth.exception.InvalidParameterException;
import inpatientWeb.Global.ecw.ambulatory.catalog.Root;
import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;
import inpatientWeb.Global.ecw.ambulatory.json.JSONArray;
import inpatientWeb.Global.ecw.ambulatory.json.JSONException;
import inpatientWeb.Global.ecw.ambulatory.json.JSONObject;
import inpatientWeb.Global.ecw.auditlogs.AuditLogService;
import inpatientWeb.Global.progressnotes.dao.IPPnService;
import inpatientWeb.Global.progressnotes.dao.ImmunizationDAO;
import inpatientWeb.Global.progressnotes.dao.pnorders.LabResultsDao;
import inpatientWeb.Global.progressnotes.dao.pnorders.ResultsHelper;
import inpatientWeb.Global.progressnotes.service.IPVitalsService;
import inpatientWeb.admin.pharmacySettings.pharmacyHelpers.PharmacyConstants;
import inpatientWeb.cpoe.util.CPOEEnum;
import inpatientWeb.pharmacy.beans.FlowsheetTemplateItem;
import inpatientWeb.pharmacy.dao.MyPatientsDAO;
import inpatientWeb.pharmacy.service.MyPatientsService;
import inpatientWeb.utils.IPTzUtils;

@Service
public class MyPatientsServiceImpl implements MyPatientsService {

	public static final int DAYS_IN_WEEK = 7;

	@Autowired MyPatientsDAO myPatientsDAO;

	@Autowired
	private IPPnService ipPnService;

	@Autowired 
	private LabResultsDao labResultsDao;
	
	@Autowired
    ResultsHelper resultsHelper;
	
	@Autowired
	ImmunizationDAO immunizationDAO;

	@Autowired
	IPVitalsService ipVitalsService;

	@Autowired
	AuditLogService auditLogService;
	
	public JSONObject getMyPatientsInitData(int loggedInUserId){
		
		if(loggedInUserId <= 0){
			throw new InvalidParameterException("Invalid loggedInUserId");
		}
		
		JSONObject result = new JSONObject();
		try {
			result.put("flowsheetTemplates", myPatientsDAO.getFlowsheetTemplates());
			result.put("datesToShow", getPreviousDatesFromCurrentDate(loggedInUserId));
		} catch (JSONException e) {
			EcwLog.AppendExceptionToLog(e);
		}

		return result;
	}

	public JSONArray getPatientsData(List<Map<String, Object>> patientList, int totalCount){
		if(null ==patientList){
			throw new InvalidParameterException("Invalid PatientList");
		}
		if(totalCount < 0){
			throw new InvalidParameterException("Invalid Patient total count");
		}
		JSONArray result = new JSONArray();
		try{
			for (Map<String, Object> patient : patientList){
				int encId = (Integer)patient.get("encounterId");
				int encType = (Integer)patient.get("encType");
				int episodeEncounterId = (Integer)patient.get("episodeEncounterId");
				JSONObject jsonPatient = new JSONObject(ipPnService.getHeaderPatientInfo(null, encId, encType));

				jsonPatient.put("orderEncounterId", encId);
				jsonPatient.put("orderEncType", encType);
				jsonPatient.put("orderEpisodeEncId", episodeEncounterId);
				jsonPatient.put("assignedTo", patient.get("assignedTo"));
				jsonPatient.put("deptId", patient.get("deptId"));
				jsonPatient.put("serviceType", patient.get("serviceType"));
				jsonPatient.put("totalCount", totalCount);
				jsonPatient.put("assignedToName", Root.TrimString((String)patient.get("assignedToFname"))+" "+ Root.TrimString((String)patient.get("assignedToLname")));
				result.put(jsonPatient);
			}
		}catch(Exception e){
			EcwLog.AppendExceptionToLog(e);
		}
		return result;
	}


	public JSONObject getFlowsheetTemplateById(int id){
		
		if(id <= 0){
			throw new InvalidParameterException("Invalid FlowsheetTemplateId");
		}

		JSONObject result = new JSONObject(myPatientsDAO.getFlowsheetTemplateById(id));
		return result;
	}

	public JSONArray getPatientsByFilter(Map<String, Object> dataMap){	
		if(null ==dataMap){
			throw new InvalidParameterException("Invalid filter data map list");
		}
		int totalRecords = myPatientsDAO.getMyPatientCount(dataMap);
		HashMap<String, Object> result = new HashMap<String, Object>();
		double totalPages =  Math.ceil( (float) totalRecords / (int)dataMap.get("recordsPerPage"));
		int selectedPage = (int)dataMap.get("selectedPage");

		if(totalPages < selectedPage){
			selectedPage = 1;
		}
		dataMap.put("selectedPage", selectedPage);
		result.put("totalMyPatientRecords", totalRecords);
		result.put("recordsPerPage", (int)dataMap.get("recordsPerPage"));
		result.put("totalPages", totalPages);
		return getPatientsData(myPatientsDAO.getPatientsByFilter(dataMap), totalRecords);

	}

	public JSONArray getFlowsheetItems(int flowsheetId, int patientId, int episodeEncounterId, List<String> datesToShow, int loggedInUserId){
		
		if(patientId <= 0){
			throw new InvalidParameterException("Invalid patientId");
		}
		if(loggedInUserId <= 0){
			throw new InvalidParameterException("Invalid loggedInUserId");
		}
		if(flowsheetId <= 0){
			throw new InvalidParameterException("Invalid flowsheetId");
		}
		if(episodeEncounterId <= 0){
			throw new InvalidParameterException("Invalid episodeEncounterId");
		}
		if(datesToShow.size()<=0){
			throw new InvalidParameterException("Invalid dateRange");
		}
		ArrayList<FlowsheetTemplateItem> result = new ArrayList<FlowsheetTemplateItem>();

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			String userTimeZone = IPTzUtils.getTimeZoneForResource(loggedInUserId);
			String strStartDate = datesToShow.get(0);
			String strEndDate = datesToShow.get(datesToShow.size() - 1);

			Date startDate = sdf.parse(strStartDate);
			Date endDate = sdf.parse(strEndDate);

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("startDate", startDate);
			params.put("endDate", endDate);
			params.put("patientId", patientId);

			HashMap<String, ArrayList<HashMap<String, Object>>> dataMap = null;

			ArrayList<FlowsheetTemplateItem> templateItems =  myPatientsDAO.getFlowsheetTemplateItemsByFlowsheetId(flowsheetId);
			for(FlowsheetTemplateItem flowsheetTemplateItem : templateItems){

				params.put("itemId", flowsheetTemplateItem.getItemId());
				int nType = flowsheetTemplateItem.getType();

				switch (nType) 
				{
				case PharmacyConstants.FLOWSHEET_ITEM_TYPE_LABS : // Lab
					getLabDetails(datesToShow, result, userTimeZone, params, flowsheetTemplateItem);
					break;

				case PharmacyConstants.FLOWSHEET_ITEM_TYPE_XRAYS: //X-Ray 
					List<Map<String, Object>> diResults = resultsHelper.getResultDetails(CPOEEnum.LAB_TYPE.XRAYS.getType(), params);
					dataMap = getDataInHashMap(diResults, "collDateTime", "reason", "collDateTime", "", userTimeZone);
					break;

				case PharmacyConstants.FLOWSHEET_ITEM_TYPE_PROCEDURES: //Procedure
					List<Map<String, Object>> procedureResults = resultsHelper.getResultDetails(CPOEEnum.LAB_TYPE.PROCEDURES.getType(), params); 
					dataMap = getDataInHashMap(procedureResults, "collDateTime", "reason", "collDateTime", "", userTimeZone);
					break;

				case PharmacyConstants.FLOWSHEET_ITEM_TYPE_LABATTRIBUTES://LabAttributes
					ArrayList<String> attributeIds = new ArrayList<String>();
					attributeIds.add(String.valueOf(flowsheetTemplateItem.getItemId()));
					params.put("attributes",attributeIds);
					params.put("nLabType", CPOEEnum.LAB_TYPE.LABS.getType());
					params.put("itemId", 0);
					List<Map<String, Object>> attributesResults = resultsHelper.getResultDetails(CPOEEnum.LAB_TYPE.LABS.getType(), params);
					dataMap = getDataInHashMap(attributesResults, "collDateTime", "value", "attrResultTime", "", userTimeZone);
					break;
				case PharmacyConstants.FLOWSHEET_ITEM_TYPE_IMMUNIZATION:	// Immunization
					List<Map<String, Object>> immResults = immunizationDAO.getImmunizationValues(params);
					dataMap = getDataInHashMap(immResults, "collDateTime", "", "collDateTime", "", userTimeZone);
					break;

				case PharmacyConstants.FLOWSHEET_ITEM_TYPE_RX: // Rx
					List<Map<String, Object>> rxResults =  myPatientsDAO.getRxDataForPatient(patientId, flowsheetTemplateItem.getItemId(), strStartDate, strEndDate);
					dataMap = getDataInHashMap(rxResults, "administereddatetime", "", "administereddatetime", "", userTimeZone);
					break;

				case PharmacyConstants.FLOWSHEET_ITEM_TYPE_VITALS: // Vitals
					List<Map<String, Object>> vitalResults = ipVitalsService.getVitalValues(episodeEncounterId, flowsheetTemplateItem.getItemId(), startDate, endDate);
					dataMap = getDataInHashMap(vitalResults, "reportedTime", "value", "reportedTime","", userTimeZone);
					break;

				case PharmacyConstants.FLOWSHEET_ITEM_TYPE_STRUCTURED: // Structured
					break;

				case PharmacyConstants.FLOWSHEET_ITEM_TYPE_DRUGCLASS: // Drug Class
					break;

				case PharmacyConstants.FLOWSHEET_ITEM_TYPE_RXGROUP: // Rx group
					List<Map<String, Object>> rxGroupItems = myPatientsDAO.getMedicationItemIdsForRxGroup(flowsheetTemplateItem.getItemId());

					for(Map<String, Object> rxGroupItem : rxGroupItems){

						String groupName = (String)rxGroupItem.get("groupName");
						String itemName = (String)rxGroupItem.get("itemName");
						int medItemId = (Integer)rxGroupItem.get("itemId");

						List<Map<String, Object>> rxResult =  myPatientsDAO.getRxDataForPatient(patientId, medItemId, strStartDate, strEndDate);
						dataMap = getDataInHashMap(rxResult, "administereddatetime", "", "administereddatetime", "", userTimeZone);

						FlowsheetTemplateItem rxItem = new FlowsheetTemplateItem();
						rxItem.setItemName(itemName+" - "+groupName);	
						rxItem.setItemId(medItemId);
						rxItem.setValDateTime(true);
						rxItem.setDataListPerDates(mapDataPerDates(datesToShow, dataMap));

						result.add(rxItem);
					}
					break;	

				default: // Others
					break;

				}

				if(nType != PharmacyConstants.FLOWSHEET_ITEM_TYPE_LABS && nType != PharmacyConstants.FLOWSHEET_ITEM_TYPE_RXGROUP){

					boolean isValDateTime = false;
					if(nType == PharmacyConstants.FLOWSHEET_ITEM_TYPE_IMMUNIZATION || nType == PharmacyConstants.FLOWSHEET_ITEM_TYPE_RX){
						isValDateTime = true;
					}

					flowsheetTemplateItem.setValDateTime(isValDateTime);
					flowsheetTemplateItem.setDataListPerDates(mapDataPerDates(datesToShow, dataMap));
					result.add(flowsheetTemplateItem);
				}

			}

		} catch (ParseException e) {
			EcwLog.AppendExceptionToLog(e);
		}

		return new JSONArray(result);
	}

	private void getLabDetails(List<String> datesToShow, ArrayList<FlowsheetTemplateItem> result, String userTimeZone, Map<String, Object> params, FlowsheetTemplateItem flowsheetTemplateItem) throws ParseException {

		List<Map<String, Object>> labAttributes = labResultsDao.getAttributeDetails(flowsheetTemplateItem.getItemId());
		List<Map<String, Object>> labResults = labResultsDao.getLabResultDetails(params);

		for (Map<String, Object> attribute : labAttributes){
			int attributeItemId = (Integer)attribute.get("itemId");
			FlowsheetTemplateItem labAttribute = new FlowsheetTemplateItem();
			labAttribute.setItemName(attribute.get("itemName").toString());	
			labAttribute.setItemId(attributeItemId);

			HashMap<String, ArrayList<HashMap<String, Object>>> attribDataMap = new HashMap<String, ArrayList<HashMap<String, Object>>>();
			ArrayList<HashMap<String, Object>> attributeValues = null;

			for (Map<String, Object> attributeResult : labResults){
				int attributeResultItemId = (Integer)attributeResult.get("attributeItemId");
				if(attributeItemId == attributeResultItemId){

					String collDateTime = IPTzUtils.convertDateStrInTz(attributeResult.get("collDateTime").toString(), IPTzUtils.DEFAULT_DB_DT_FMT , IPTzUtils.DEFAULT_DB_TIME_ZONE, "MM/dd/yyyy", userTimeZone );

					HashMap<String, Object> valueMap = new HashMap<String, Object>();

					String value = ((String) attributeResult.get("value")) + ((String)attributeResult.get("units"));
					String reportedTime = (String) attributeResult.get("attrResultTime");

					valueMap.put("value", value);
					valueMap.put("time", reportedTime);

					attributeValues = getValues(attribDataMap, collDateTime);

					attributeValues.add(valueMap);
					attribDataMap.put(collDateTime, attributeValues);
				}
			}
			labAttribute.setValDateTime(false);
			labAttribute.setDataListPerDates(mapDataPerDates(datesToShow, attribDataMap));
			result.add(labAttribute);
		}
	}

	public ArrayList<String> getPreviousDatesFromCurrentDate(int loggedInUserId){
		
		if(loggedInUserId <= 0){
			throw new InvalidParameterException("Invalid loggedInUserId");
		}
		
		ArrayList<String> result = new ArrayList<String>();

		try{

			String userTimeZone = IPTzUtils.getTimeZoneForResource(loggedInUserId);

			SimpleDateFormat dateFormat = new SimpleDateFormat(IPTzUtils.DEFAULT_DB_DT_FMT);

			for (int i = DAYS_IN_WEEK ; i >= 1 ; i--){
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, -(i));
				Date date = cal.getTime();    
				String calDate = dateFormat.format(date);
				calDate = IPTzUtils.convertDateStrInTz(calDate, IPTzUtils.DEFAULT_DB_DT_FMT , IPTzUtils.DEFAULT_DB_TIME_ZONE, "MM/dd/yyyy", userTimeZone );
				result.add(calDate);
			}
		}catch(ParseException e){
			EcwLog.AppendExceptionToLog(e);
		}

		return result;
	}

	private ArrayList<HashMap<String, Object>> mapDataPerDates(List<String> datesToShow, HashMap<String, ArrayList<HashMap<String, Object>>> givenDateMap){
		ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();

		for(String date : datesToShow){
			ArrayList<HashMap<String, Object>> values = new ArrayList<HashMap<String, Object>>();
			if(givenDateMap.containsKey(date)){
				values = givenDateMap.get(date);
			}

			HashMap<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("date", date);
			dataMap.put("values", values);

			if(values.size() > 0){
				dataMap.put("valueToShow", values.get(values.size() - 1));
			}
			result.add(dataMap);
		}

		return result;
	}

	public HashMap<String, ArrayList<HashMap<String, Object>>> getDataInHashMap(List<Map<String, Object>> resultList, String dateTimeColumn, String valueColumn, String reportedTimeColumn, String userColumn, String userTimeZone){
		HashMap<String, ArrayList<HashMap<String, Object>>> dataMap = new HashMap<String, ArrayList<HashMap<String, Object>>>();

		try{
			ArrayList<HashMap<String, Object>> values = null;

			for (Map<String, Object> result : resultList){
				if(result.get(dateTimeColumn) != null ){
					String collDateTime = IPTzUtils.convertDateStrInTz(result.get(dateTimeColumn).toString(), IPTzUtils.DEFAULT_DB_DT_FMT , IPTzUtils.DEFAULT_DB_TIME_ZONE, "MM/dd/yyyy", userTimeZone );

					HashMap<String, Object> valueMap = new HashMap<String, Object>();

					String value = result.get(valueColumn) != null ? result.get(valueColumn) .toString() : "" ;
					String reportedTime =result.get(reportedTimeColumn) != null ? result.get(reportedTimeColumn).toString() : "" ;
					if(!"".equals(reportedTime)){
						reportedTime = IPTzUtils.convertDateStrInTz(reportedTime, IPTzUtils.DEFAULT_DB_DT_FMT , IPTzUtils.DEFAULT_DB_TIME_ZONE, "HHmm", userTimeZone );
					}
					String userName = ((String) result.get(userColumn)) ;

					valueMap.put("value", value);
					valueMap.put("time", reportedTime);
					valueMap.put("user", userName);

					values = getValues(dataMap, collDateTime);

					values.add(valueMap);
					dataMap.put(collDateTime, values);
				}

			}
		}catch(ParseException e){
			EcwLog.AppendExceptionToLog(e);
		}

		return dataMap;
	}

	private ArrayList<HashMap<String, Object>> getValues(HashMap<String, ArrayList<HashMap<String, Object>>> dataMap, String collDateTime) {
		ArrayList<HashMap<String, Object>> values = null;
		if(dataMap.containsKey(collDateTime)){
			values = dataMap.get(collDateTime);
		} else {
			values = new ArrayList<HashMap<String, Object>>();
		}
		return values;
	}


}
