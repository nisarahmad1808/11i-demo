package inpatientWeb.pharmacy.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import inpatientWeb.admin.pharmacySettings.configureDictionary.dao.TemplateReasonDictionaryItems;
import inpatientWeb.pharmacy.beans.MedOrder;
import inpatientWeb.pharmacy.beans.PharmacyStatus;
import inpatientWeb.pharmacy.beans.PharmacyVerification;

public interface WorkQueueService {

	HashMap<String, Object> getWorkQueueData(Map<String, Object> filterMap);

	boolean updatePharmacyStatus(int verificationId, int status, int reasonId, int verifiedBy, int modifiedBy );

	String getRecordCountForTabs();

	Map<String, Object> getInitDataForWorkQueueTabs(Map<String, Object> filterMap);

	boolean createPharmacyVerification(PharmacyVerification pharmacyVerification);

	Map<String, Object> getMedicationOrderDetails(int orderId, int patientId, int pharmacyStatus, int loggedInUserId, boolean loadAll);

	boolean assignOrders(String[] verificationIds, int assignTo, int verifiedBy, int modifiedBy );

	ArrayList<TemplateReasonDictionaryItems> getPendingReasons();

	ArrayList<HashMap<String, String>> getMedOrdersByPatient(int patientId);

	PharmacyVerification readPharmacyVerificationById(int pharmacyVerificationId);

	boolean updatePharmacyVerification(PharmacyVerification pharmacyVerification);

	boolean isInterventionsCompleted(int orderId);

	PharmacyStatus getPharmacyStatusByName(String statusName);

	boolean createPharmacyVerificationByOrderId(int orderId,int orderById);
	
	boolean resetToUnverifiedByOrderId(int orderId,int orderById);
	
	Map<String, Object> printLabelForMedDispense(Map<String, Object> requestMap);
	
	boolean isPrintLabelRequired(int orderId);
	
	PharmacyVerification getPharmacyVerificationObj(MedOrder medOrder, int loggedInUserId, int pharmacyStatus, int reasonId);
	
	void savePharmacyBillingData(MedOrder medOrder, int loggedInUserId);
	
	Map<String, Object> getOrderLabelDataPVQ(Map<String, Object> requestMap);
	
	void savePharmacyBillingData(MedOrder medOrder, int loggedInUserId, String strMode);
	
	int checkPharmacyPrinterSetting(int facilityId,int orderType);

}
