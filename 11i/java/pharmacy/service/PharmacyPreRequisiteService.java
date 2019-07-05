package inpatientWeb.pharmacy.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import inpatientWeb.pharmacy.beans.MedOrderDetail;

public interface PharmacyPreRequisiteService {
	
	HashMap<String, ArrayList<HashMap<String, Object>>> getPreRequisitesForOrder(List<MedOrderDetail> medOrderDetailList, int routedGenericItemId, int episodeEncId, boolean showMandatory, int loggedInUserId);

	HashMap<String, ArrayList<HashMap<String, Object>>> getPreRequisitesByOrderId(int orderId, int patientId, boolean showMandatory, int loggedInUserId);

	

}
