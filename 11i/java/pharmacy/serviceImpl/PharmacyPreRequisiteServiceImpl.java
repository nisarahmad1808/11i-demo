package inpatientWeb.pharmacy.serviceImpl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import inpatientWeb.Auth.exception.InvalidParameterException;
import inpatientWeb.Global.progressnotes.service.IPVitalsService;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.FormularySetupDao;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.TemplateForPreVitals;
import inpatientWeb.admin.pharmacySettings.pharmacyHelpers.PharmacyConstants;
import inpatientWeb.pharmacy.beans.MedOrder;
import inpatientWeb.pharmacy.beans.MedOrderDetail;
import inpatientWeb.pharmacy.service.PharmacyMedOrderService;
import inpatientWeb.pharmacy.service.PharmacyPreRequisiteService;
import inpatientWeb.registration.appointment.service.AppointmentService;
import inpatientWeb.utils.IPTzUtils;

@Service
public class PharmacyPreRequisiteServiceImpl implements PharmacyPreRequisiteService {
	
	@Autowired
	IPVitalsService ipVitalsService;
	
	@Autowired
	FormularySetupDao formularySetupDAO;
	
	@Autowired
	PharmacyMedOrderService pharmacyMedOrderService;		
	
	private AppointmentService appointmentService;

	@Autowired
	public void setAppointmentService(AppointmentService ref) {
		this.appointmentService = ref;
	}
	
	public Map<Integer, Map<String, Object>> getLatestVitalValues(int episodeEncId, int loggedInUserId){
		
		if(episodeEncId <= 0){
			throw new InvalidParameterException("Invalid episodeEncId");
		}
		if(loggedInUserId <= 0){
			throw new InvalidParameterException("Invalid loggedInUserId");
		}
		Map<Integer, Map<String, Object>> result = new HashMap<Integer, Map<String,Object>>();
		
		int masterEncId = appointmentService.getLatestSubEncounterId(episodeEncId);	
		String userTimeZone = IPTzUtils.getTimeZoneForResource(loggedInUserId);
		ArrayList<Map<String, Object>> vitals = (ArrayList<Map<String, Object>>)ipVitalsService.getLatestVitalValues(masterEncId);

		for(Map<String, Object> vital: vitals){

			String convertedDateTime = "";
			String reportedTime = vital.get("reportedTime").toString(); 
			try {
				convertedDateTime = IPTzUtils.convertDateStrInTz(reportedTime, IPTzUtils.DEFAULT_DB_DT_FMT , IPTzUtils.DEFAULT_DB_TIME_ZONE, IPTzUtils.DEFAULT_USER_DT_FMT, userTimeZone );
			} catch (ParseException e) {
				convertedDateTime = reportedTime;
			}
			vital.remove("reportedTime");
			vital.put("reportedTime", convertedDateTime);

			result.put((Integer)vital.get("vitalid"), vital);

		}

		return result;
	}

	public HashMap<String, ArrayList<HashMap<String, Object>>> getPreRequisitesForOrder(List<MedOrderDetail> medOrderDetailList, int routedGenericItemId, int episodeEncId, boolean showMandatory, int loggedInUserId){
		HashMap<String, ArrayList<HashMap<String, Object>>> result = new HashMap<String, ArrayList<HashMap<String, Object>>>();

		ArrayList<HashMap<String, Object>> vitalPreRequisites  = new ArrayList<HashMap<String, Object>>();
		ArrayList<HashMap<String, Object>> labPreRequisites  = new ArrayList<HashMap<String, Object>>();
		
		int masterEncId = appointmentService.getLatestSubEncounterId(episodeEncId);		

		String userTimeZone = IPTzUtils.getTimeZoneForResource(loggedInUserId);
		ArrayList<Map<String, Object>> vitals = (ArrayList<Map<String, Object>>)ipVitalsService.getLatestVitalValues(masterEncId);

		ArrayList<Integer> mandatoryIds = new ArrayList<Integer>();

		for(MedOrderDetail medOrderDetail : medOrderDetailList){

			ArrayList<TemplateForPreVitals> preRequisites = (ArrayList<TemplateForPreVitals>)formularySetupDAO.getPreRequisiteVitalsDetails(medOrderDetail.getDrugFormularyId(), String.valueOf(routedGenericItemId));

			for(TemplateForPreVitals preRequisite : preRequisites){

				int itemId = Integer.parseInt(preRequisite.getRightsideid());

				if(!mandatoryIds.contains(itemId)){

					addPreRequisite(vitalPreRequisites, labPreRequisites, userTimeZone, vitals, mandatoryIds, preRequisite, itemId);
				}
			}
		}

		if(!showMandatory){
			putAllVitalsInMap( vitalPreRequisites, userTimeZone, vitals, mandatoryIds);
		}

		result.put("vitals", vitalPreRequisites);
		result.put("labs", labPreRequisites);

		return result;
	}

	private void addPreRequisite(ArrayList<HashMap<String, Object>> vitalPreRequisites, ArrayList<HashMap<String, Object>> labPreRequisites, String userTimeZone, ArrayList<Map<String, Object>> vitals, ArrayList<Integer> mandatoryIds, TemplateForPreVitals preRequisite, int itemId) {
		HashMap<String, Object> preMap = new HashMap<String, Object>();
		preMap.put("itemId", itemId);
		preMap.put("itemName", preRequisite.getItemname());
		preMap.put("isMandatory", true);

		mandatoryIds.add(itemId);

		int leftSideId = Integer.parseInt(preRequisite.getLeftsideid());

		if(leftSideId == PharmacyConstants.FORMULARY_PREREQUISITE_VITALS){

			for(Map<String, Object> vital: vitals){
				if(itemId == (Integer)vital.get("vitalid")){
					putPreRequisiteInMap(userTimeZone, preMap, vital);
					break;
				}
			}

			vitalPreRequisites.add(preMap);

		} else if(leftSideId == PharmacyConstants.FORMULARY_PREREQUISITE_LABS){

			labPreRequisites.add(preMap);
		}
	}

	private void putAllVitalsInMap(ArrayList<HashMap<String, Object>> vitalPreRequisites, String userTimeZone, ArrayList<Map<String, Object>> vitals, ArrayList<Integer> mandatoryIds) {
		for(Map<String, Object> vital: vitals){
			if(!mandatoryIds.contains((Integer)vital.get("vitalid"))){
				HashMap<String, Object> preMap = new HashMap<String, Object>();
				preMap.put("itemId", vital.get("vitalid"));
				preMap.put("itemName", vital.get("vitalName"));

				putPreRequisiteInMap(userTimeZone, preMap, vital);

				vitalPreRequisites.add(preMap);

			}
		}
	}

	private void putPreRequisiteInMap(String userTimeZone, HashMap<String, Object> preMap, Map<String, Object> vital) {
		String convertedDateTime = "";
		String reportedTime = vital.get("reportedTime").toString(); 
		try {
			convertedDateTime = IPTzUtils.convertDateStrInTz(reportedTime, IPTzUtils.DEFAULT_DB_DT_FMT , IPTzUtils.DEFAULT_DB_TIME_ZONE, IPTzUtils.DEFAULT_USER_DT_FMT, userTimeZone );
		} catch (ParseException e) {
			convertedDateTime = reportedTime;
		}
		String value = vital.get("value").toString();

		preMap.put("value", value);
		preMap.put("reportedTime", convertedDateTime);
	}
	
	public HashMap<String, ArrayList<HashMap<String, Object>>> getPreRequisitesByOrderId(int orderId, int patientId, boolean showMandatory, int loggedInUserId){

		if(orderId <= 0){
			throw new InvalidParameterException("Invalid orderId");
		}
		if(patientId <= 0){
			throw new InvalidParameterException("Invalid patientId");
		}
		if(loggedInUserId <= 0){
			throw new InvalidParameterException("Invalid loggedInUserId");
		}
		
		MedOrder medOrder = pharmacyMedOrderService.getMedicationOrder(orderId, patientId, loggedInUserId);

		HashMap<String, ArrayList<HashMap<String, Object>>> result = getPreRequisitesForOrder(medOrder.getMedOrderDetailList(), medOrder.getRoutedGenericItemId(), medOrder.getEpisodeEncounterId(), showMandatory, loggedInUserId);

		return result;
	}

}
