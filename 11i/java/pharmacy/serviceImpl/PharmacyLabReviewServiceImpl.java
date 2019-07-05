package inpatientWeb.pharmacy.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import inpatientWeb.Auth.exception.InvalidParameterException;
import inpatientWeb.Global.ecw.ambulatory.CwMobile.CwUtils;
import inpatientWeb.Global.ecw.ambulatory.catalog.Root;
import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;
import inpatientWeb.Global.ecw.ambulatory.json.JSONObject;
import inpatientWeb.Global.progressnotes.dao.pnorders.LabResultsDao;
import inpatientWeb.cpoe.util.CPOEEnum;
import inpatientWeb.pharmacy.dao.WorkQueueDAO;
import inpatientWeb.pharmacy.service.PharmacyLabReviewService;
import inpatientWeb.pharmacy.util.PharmacyDateUtil;
import inpatientWeb.utils.DateUtil;
import inpatientWeb.utils.IPTzUtils;
import inpatientWeb.utils.Util;


@Service
@Scope("prototype")
@Lazy
public class PharmacyLabReviewServiceImpl implements PharmacyLabReviewService {
	
	@Autowired
	LabResultsDao labResultsDao;
	
	@Autowired
	private WorkQueueDAO workQueueDAO;
	
	public Map<String, Object> getLabs(Map<String,Object> searchCriteria){
		if(searchCriteria==null || searchCriteria.isEmpty()){
			throw new InvalidParameterException("Error: Request body is empty for search criteria");
		}
		Map<String, Object> oResponseMap = null;
		oResponseMap =  workQueueDAO.getLabs(searchCriteria);
		return oResponseMap;
	}

	@SuppressWarnings("unchecked")
	public String getAllLabReviewList(Map<String,Object> oRequestMap) {	
		if(oRequestMap==null || oRequestMap.isEmpty()){
			throw new InvalidParameterException("Error: Request body is empty for search criteria");
		}
		JSONObject result = new JSONObject();
		try {
			String fromDate = Util.getStrValue(oRequestMap, "startDate");
			String toDate = Util.getStrValue(oRequestMap, "endDate");
			int loggedInUserId = Util.getIntValue(oRequestMap, "userId");
			fromDate = Util.ifNullEmpty(fromDate, "",PharmacyDateUtil.convertDateTimeInUtcTz(fromDate+" 00:00:00", loggedInUserId,IPTzUtils.DEFAULT_DB_DT_FMT,IPTzUtils.DEFAULT_DB_DT_FMT));
			toDate = Util.ifNullEmpty(fromDate, "",PharmacyDateUtil.convertDateTimeInUtcTz(toDate+" 23:59:59", loggedInUserId,IPTzUtils.DEFAULT_DB_DT_FMT,IPTzUtils.DEFAULT_DB_DT_FMT));
			oRequestMap.put("fromDate",fromDate);
			oRequestMap.put("toDate",toDate);
			oRequestMap.put("DisplayLabResultsInPharmacyQueue", true);
			
			oRequestMap.put("received", 1);
			oRequestMap.put("labType", CPOEEnum.LAB_TYPE.LABS.getType());
			oRequestMap.put("deleteFlag", 0);
			
			Map<String, Object> labResult = workQueueDAO.getTotalLabCounts(oRequestMap);
			int totalLabCount = Util.getIntValue(labResult, "recordCnt");
			int recordsPerPage = Util.getIntValue(oRequestMap, "recordsPerPage",1);
			int selectedPage = Util.getIntValue(oRequestMap, "selectedPage");
			double totalPages =  Math.ceil( (float) totalLabCount / recordsPerPage);
			
			if(totalPages < selectedPage){
				selectedPage = 1;
			}
			oRequestMap.put("selectedPage", selectedPage);
			
			result.put("selectedPage", selectedPage);
			result.put("totalLabCount", totalLabCount);
			
			labResult =  getLabDIProc(oRequestMap);
			List<Map<String, Object>> lsResults = (List<Map<String, Object>>) labResult.get("labs");
			List<Map<String, Object>> labAttributes = this.getLabAttributesForLabs(lsResults);
			List<Map<String, Object>> lsAttr = null;
			int reportId;
			
			for (Map<String, Object> resultMap : lsResults) {	
				String orderDate = Util.getStrValue(resultMap,"orderDate","");
				resultMap.put("orderDate", Root.TrimString(orderDate).length() > 0 ? CwUtils.ConvertDateFormat(Root.TrimString(orderDate), "yyyy-MM-dd", "MM/dd/yyyy") : "");
				String gender = Util.getStrValue(resultMap,"gender","");
				resultMap.put("gender",gender.length() > 0 ? gender.substring(0,1).toUpperCase() : "");
				String age = Util.getStrValue(resultMap,"age","");
				resultMap.put("age",age.length() > 0 ? CwUtils.CalculateAge(CwUtils.ConvertDateFormat(age,"MM/dd/yyyy","yyyy-MM-dd"), DateUtil.getTodaysDate("yyyy-MM-dd")) : "");
				resultMap.put("showPaperClip",Util.isEmptyOrWhitespace(Util.getStrValue(resultMap,"processedxml",""))?false:true);
				reportId = Util.getIntValue(resultMap, "reportId");
				lsAttr = (List<Map<String, Object>>) resultMap.get("labAttributes");
				
				addLabAttributesToList(labAttributes, lsAttr, reportId, resultMap);
			}
			result.put("labReviewList", lsResults);
		} catch (Exception e) {
			EcwLog.AppendExceptionToLog(e);
		}
		return result.toString();
	}

	private void addLabAttributesToList(List<Map<String, Object>> labAttributes, List<Map<String, Object>> lsAttr, int reportId, Map<String, Object> resultMap) {
		if(lsAttr == null) {
			lsAttr = new ArrayList<Map<String,Object>>();
			resultMap.put("labAttributes", lsAttr);
		}
		for (Map<String, Object> attrMap : labAttributes) {
			if(Util.getIntValue(attrMap, "reportId") == reportId){
				lsAttr.add(attrMap);
			}
		}
	}

	public Map<String, Object> getLabDIProc(Map<String,Object> oRequestMap) {

		int nLabType = Util.getIntValue(oRequestMap, "labType", -1);
		Boolean bDisplayLabResultsInPharmacyQueue = Util.getBooleanValue(oRequestMap, "DisplayLabResultsInPharmacyQueue");

		if( nLabType == -1 || !bDisplayLabResultsInPharmacyQueue)
			throw new InvalidParameterException(" labType, DisplayLabResultsInPharmacyQueue must have to be passed ");

		oRequestMap.put("received", 1);

		return getLabs(oRequestMap);
	}

	public List<Map<String, Object>> getLabAttributesForLabs(List<Map<String, Object>> labList){
		Set<String> itemIds = new HashSet<String>(),reportIds = new HashSet<String>();
		List<Map<String, Object>> labAttributes = new ArrayList<Map<String,Object>>();
		if(labList == null || labList.size() ==0) return Collections.emptyList();
		for(Map<String, Object> lab : labList){
			itemIds.add(String.valueOf(lab.get("itemId")));
			reportIds.add(String.valueOf(lab.get("reportId")));
		}

		if(reportIds.size() > 0){
			labAttributes = labResultsDao.getLabAttributeData(reportIds, null); 
			itemIds.clear();
		}
		return labAttributes;

	}

	public boolean markLabAsReviewed(String reportIdList, int userId){
		if(Util.isNullEmpty(reportIdList)) {
			throw new InvalidParameterException("Error: Report Id List is empty");
		}
		if(userId <=0) {
			throw new InvalidParameterException("Error: Invalid User Id");
		}
		try {
			List<Integer> reportList=new ObjectMapper().readValue(reportIdList, new TypeReference<List<Integer>>() {});
			return workQueueDAO.markLabAsReviewed(reportList,userId);
		} catch (JsonParseException e) {
			EcwLog.AppendExceptionToLog(e);
		} catch (JsonMappingException e) {
			EcwLog.AppendExceptionToLog(e);
		} catch (IOException e) {
			EcwLog.AppendExceptionToLog(e);
		}

		return false;
	}

}
