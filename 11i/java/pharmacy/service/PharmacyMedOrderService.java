package inpatientWeb.pharmacy.service;

import java.util.List;

import inpatientWeb.cpoe.orders.medication.MedicationDispenseDetailDTO;
import inpatientWeb.cpoe.orders.medication.MedicationOrder;
import inpatientWeb.pharmacy.beans.MedOrder;
import inpatientWeb.pharmacy.beans.MedOrderDetail;
import inpatientWeb.pharmacy.beans.WorkQueue;

public interface PharmacyMedOrderService {
	
	MedOrder getMedicationOrder(int orderId, int patientId, int loggedInUserId);
	
	boolean isMedAvailableForFacility(MedOrder medOrder);
	
	MedicationDispenseDetailDTO getMedOrderDetailCPOEObj(MedOrder medOrder, MedOrderDetail medOrderDetail) throws Exception;
	
	MedicationOrder getMedOrderCPOEObj(MedOrder medOrder);
	
	 List<WorkQueue> getOrderLogs(int patientId, int loggedInUserId);
}
