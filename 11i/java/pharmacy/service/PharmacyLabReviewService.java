package inpatientWeb.pharmacy.service;

import java.util.List;
import java.util.Map;

public interface PharmacyLabReviewService {
	Map<String, Object> getLabs(Map<String,Object> searchCriteria);
	
	String getAllLabReviewList(Map<String,Object> oRequestMap);
	
	Map<String, Object> getLabDIProc(Map<String,Object> oRequestMap);
	
	List<Map<String, Object>> getLabAttributesForLabs(List<Map<String, Object>> labList);
	
	boolean markLabAsReviewed(String reportIdList, int userId);
}
