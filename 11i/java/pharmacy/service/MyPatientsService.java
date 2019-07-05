package inpatientWeb.pharmacy.service;

import java.util.List;
import java.util.Map;

import inpatientWeb.Global.ecw.ambulatory.json.JSONArray;
import inpatientWeb.Global.ecw.ambulatory.json.JSONObject;

public interface MyPatientsService {
	
	JSONObject getMyPatientsInitData(int loggedInUserId);
	
	JSONObject getFlowsheetTemplateById(int id);
	
	JSONArray getPatientsByFilter(Map<String, Object> dataMap);
	
	JSONArray getFlowsheetItems(int flowsheetId, int patientId, int episodeEncounterId, List<String> datesToShow, int loggedInUserId);
	
}
