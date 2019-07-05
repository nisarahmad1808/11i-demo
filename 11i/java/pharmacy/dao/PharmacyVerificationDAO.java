package inpatientWeb.pharmacy.dao;

import java.util.ArrayList;
import java.util.Map;

import inpatientWeb.pharmacy.beans.PharmacyStatus;
import inpatientWeb.pharmacy.beans.PharmacyVerification;

public interface PharmacyVerificationDAO {
	
	boolean createPharmacyVerification(PharmacyVerification pharmacyVerification);
	
	boolean resetToPharmacyVerificationStatusByOrderId(PharmacyVerification pharmacyVerification);

	boolean updateStatus(Map<String, Object> paramMap );
	
	ArrayList<PharmacyStatus> getPharmacyStatus();

	PharmacyStatus getPharmacyStatusByName(String statusName);
	
	boolean updatePharmacyVerification(PharmacyVerification pharmacyVerification);
	
	PharmacyVerification readPharmacyVerificationById(int pharmacyVerificationId);
	
	boolean assignOrders(String[] verificationIds, int assignTo, int assignedBy, int modifiedBy );
	
}
