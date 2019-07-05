package inpatientWeb.pharmacy.dao;

import java.util.List;
import java.util.Map;

import inpatientWeb.pharmacy.beans.PharmacyOrderScheduleOrg;
import inpatientWeb.pharmacy.beans.PharmacyOrderScheduleUpdated;

public interface PharmacyOrderScheduleDAO {

	boolean createPharmacyOrderScheduleOrg(PharmacyOrderScheduleOrg pharmacyOrderScheduleOrg);
	
	boolean createPharmacyOrderScheduleUpdated(PharmacyOrderScheduleUpdated pharmacyOrderScheduleUpdated);
	
	boolean deletePharmacyOrderScheduleUpdated(Map<String, Object> paramMap);
	
	List<PharmacyOrderScheduleUpdated> getPharmacyOrderScheduleUpdated(Map<String, Object> paramMap);
	
	boolean isPharmacyOrderScheduleOrgExist(Map<String, Object> paramMap);
	
	
	boolean isPharmacyOrderScheduleUpdatedExist(Map<String, Object> paramMap);
}
