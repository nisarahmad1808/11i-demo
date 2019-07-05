package inpatientWeb.pharmacy.dao;

import inpatientWeb.pharmacy.beans.PharmacyVerificationSessionLog;

import java.util.Map;

public interface PharmacyConcurrencyDAO {
	
	boolean createPharmacyVerificationSessionLog(PharmacyVerificationSessionLog pharmacyVerificationSessionLog);

	PharmacyVerificationSessionLog readPharmacyVerificationSessionLog(Map<String, Object> filterMap);
	
	int getPharmacyVerificationSessionLogCount(Map<String, Object> filterMap);
	
	boolean deletePharmacyVerificationSessionLog(Map<String, Object> filterMap);
}
