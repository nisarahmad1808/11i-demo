package inpatientWeb.pharmacy.service;

import inpatientWeb.pharmacy.beans.PharmacyVerificationSessionLog;

public interface PharmacyConcurrencyService {
	
	PharmacyVerificationSessionLog getConcurrentUserLog(int orderId, int patientId, int userId);
	
	boolean createPharmacyVerificationSessionLog(int orderId, int patientId, int loggedInUserId);
	
	boolean clearSessionLog(int orderId, int patientId, int userId);

}
