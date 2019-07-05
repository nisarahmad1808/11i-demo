package inpatientWeb.pharmacy.dao;

import inpatientWeb.pharmacy.beans.FlowsheetTemplate;
import inpatientWeb.pharmacy.beans.FlowsheetTemplateItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public interface MyPatientsDAO {
	
	ArrayList<FlowsheetTemplate> getFlowsheetTemplates();
	
	FlowsheetTemplate getFlowsheetTemplateById(int id);
	
	int getMyPatientCount(Map<String, Object> filterMap);
	
	ArrayList<FlowsheetTemplateItem> getFlowsheetTemplateItemsByFlowsheetId(int id);
	
	List<Map<String, Object>> getPatientsByFilter(Map<String, Object> filterMap);
	
	ArrayList<HashMap<String, Object>> getPatientsByFlowsheetId(int flowsheetId);
	
	List<Map<String, Object>> getRxDataForPatient(int patientId, int drugItemId, String startDate, String endDate);
	
	HashMap<String, ArrayList<String>> getImmunizationDatesPatient(int patientId, int itemId, String startDate, String endDate, int loggedInUserId);
	
	List<Map<String,Object>> getMedicationItemIdsForRxGroup(int rxGroupId);
	
	List<Map<String, Object>> getMyPatientsList(int loggedInUserId);
}
