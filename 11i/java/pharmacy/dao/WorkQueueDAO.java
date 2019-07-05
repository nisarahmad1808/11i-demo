package inpatientWeb.pharmacy.dao;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import inpatientWeb.pharmacy.beans.MedOrder;
import inpatientWeb.pharmacy.beans.MedOrderDetail;
import inpatientWeb.pharmacy.beans.OrderPriority;
import inpatientWeb.pharmacy.beans.WorkQueue;

public interface WorkQueueDAO {
	ArrayList<WorkQueue> getWorkQueueData(Map<String, Object> filterMap);

	int getWorkQueueCount(Map<String, Object> filterMap);

	ArrayList<OrderPriority> getOrderPriority();

	Hashtable<String, Integer> getRecordCountForTabs();

	ArrayList<WorkQueue> getMedicationProfileData(int patientId, int pharmacyStatus, int orderType, int loggedInUserId);

	MedOrder getMedicationOrder(int orderId, int patientId, int loggedInUserId);

	ArrayList<MedOrder> getActiveOrders(int patientId);

	Map<String, Object> getLabs(Map<String, Object> searchCriteria);
	
	boolean markLabAsReviewed(List<Integer> reportIdList, int userId);
	
	ArrayList<WorkQueue> getOrdersForLeftPanel(int patientId, int pharmacyStatus, int orderType);
	
	List<Map<String, Object>> getAllReflexAlerts(int stagingOrderId);
	
	Map<String, Object> getLoincCodeBasedOnFacilityId(int facilityId);
	
	Map<String, Object> getEncounterDetails(int encounterId);
	
	int getFacilityIdByUserId(int userId);
	
	ArrayList<MedOrderDetail> getMedOrderDetailsByMedOrderId(int medOrderId, final int facilityId, int orderType);
	
	Map<String, Object> getTotalLabCounts(Map<String, Object> oRequestMap);
}
