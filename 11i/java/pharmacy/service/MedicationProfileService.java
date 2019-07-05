package inpatientWeb.pharmacy.service;

import java.util.Map;

public interface MedicationProfileService {
	
	Map<String, Object> getMedicationProfile(int patientId, int loggedInUserId);

	String getMedicationProfileData(int patientId, int pharmacyStatus, int loggedInUserId);
	
	String generateHtmlForMedicationProfile(int patientId, int loggedInUserId);

}
