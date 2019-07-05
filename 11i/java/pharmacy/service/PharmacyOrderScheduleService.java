package inpatientWeb.pharmacy.service;

import java.util.List;
import java.util.Map;

import inpatientWeb.admin.pharmacySettings.configureDictionary.dao.TemplateForTimeSchedule;
import inpatientWeb.pharmacy.beans.MedOrder;
import inpatientWeb.pharmacy.beans.PharmacyOrderScheduleOrg;
import inpatientWeb.pharmacy.beans.PharmacyOrderScheduleUpdated;

public interface PharmacyOrderScheduleService {
	
boolean createPharmacyOrderScheduleOrg(PharmacyOrderScheduleOrg pharmacyOrderScheduleOrg);
	
	boolean createPharmacyOrderScheduleUpdated(PharmacyOrderScheduleUpdated pharmacyOrderScheduleUpdated);
	
	boolean deletePharmacyOrderScheduleUpdated(Map<String, Object> paramMap);
	
	List<PharmacyOrderScheduleUpdated> getPharmacyOrderScheduleUpdated(Map<String, Object> paramMap);
	
	boolean isPharmacyOrderScheduleOrgExist(Map<String, Object> paramMap);
	
	boolean isPharmacyOrderScheduleUpdatedExist(Map<String, Object> paramMap);
	
	boolean updateOrderSchedule(MedOrder medOrder, int loggedInUserId);
	
	List<TemplateForTimeSchedule> getFrequencySchedule(int orderId, int frequencyId);

}
