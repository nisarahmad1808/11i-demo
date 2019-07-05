package inpatientWeb.pharmacy.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.google.gson.Gson;

import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;
import inpatientWeb.Global.ecw.ambulatory.json.JSONException;
import inpatientWeb.Global.ecw.ambulatory.json.JSONObject;
import inpatientWeb.Global.ecw.auditlogs.AuditLogService;
import inpatientWeb.admin.pharmacySettings.configureDictionary.dao.TemplateReasonDictionaryItems;
import inpatientWeb.admin.pharmacySettings.pharmacyHelpers.PharmacyAuditLogsConstants;
import inpatientWeb.admin.pharmacySettings.pharmacyHelpers.PharmacyConstants;
import inpatientWeb.cpoe.orders.medication.MedicationDispenseDetailDTO;
import inpatientWeb.cpoe.orders.medication.MedicationOrder;
import inpatientWeb.cpoe.orders.medication.MedicationOrderService;
import inpatientWeb.pharmacy.beans.MedOrder;
import inpatientWeb.pharmacy.beans.MedOrderDetail;
import inpatientWeb.pharmacy.beans.PharmacyVerificationSessionLog;
import inpatientWeb.pharmacy.beans.WorkQueue;
import inpatientWeb.pharmacy.service.MedicationProfileService;
import inpatientWeb.pharmacy.service.MyPatientsService;
import inpatientWeb.pharmacy.service.PharmacyConcurrencyService;
import inpatientWeb.pharmacy.service.PharmacyLabReviewService;
import inpatientWeb.pharmacy.service.PharmacyMedOrderService;
import inpatientWeb.pharmacy.service.PharmacyOrderScheduleService;
import inpatientWeb.pharmacy.service.PharmacyPreRequisiteService;
import inpatientWeb.pharmacy.service.WorkQueueService;
import inpatientWeb.utils.StatusMap;
import inpatientWeb.utils.Util;


@Controller
@SessionAttributes("uid")
public class PharmacyWorkQueueController {

	@Autowired
	public WorkQueueService workQueueService;

	@Autowired 
	MyPatientsService myPatientsService;

	@Autowired
	public MedicationOrderService medService;

	@Autowired
	public PharmacyConcurrencyService pharmacyConcurrencyService;

	@Autowired
	AuditLogService auditLogService;

	@Autowired
	PharmacyOrderScheduleService pharmacyOrderScheduleService;

	@Autowired
	MedicationProfileService medicationProfileService;

	@Autowired
	PharmacyLabReviewService pharmacyLabReviewService;

	@Autowired
	PharmacyPreRequisiteService pharmacyPreRequisiteService;

	@Autowired
	PharmacyMedOrderService pharmacyMedOrderService;

	@RequestMapping(value="/pharmacyWorkQueue.go/launchPharmacyQueue", method=RequestMethod.GET)
	public String launchWorkQueue(HttpServletRequest request, HttpServletResponse response)  {
		return "staticContent/pharmacy/workQueue/views/pharmacyWorkQueue";
	}

	@RequestMapping(value="/pharmacyWorkQueue.go/getAllActiveList", method=RequestMethod.GET)
	public String getAllActiveList(HttpServletRequest request, HttpServletResponse response)  {
		return "staticContent/pharmacy/workQueue/views/allActive";
	}

	@RequestMapping(value="/pharmacyWorkQueue.go/launchLabReview", method=RequestMethod.GET)
	public String launchLabReview(HttpServletRequest request, HttpServletResponse response)  {
		return "staticContent/pharmacy/workQueue/views/labReview";
	}

	@RequestMapping(value="/pharmacyWorkQueue.go/getAllLabReviewList",method= {RequestMethod.POST})
	public @ResponseBody String getAllLabReviewList(@RequestParam Map<String, Object> filterMap)  {
		String str = "";
		try {
			return pharmacyLabReviewService.getAllLabReviewList(filterMap);
		} catch (RuntimeException e) {
			EcwLog.AppendExceptionToLog(e);
		}
		return str;
	}

	@RequestMapping(value="/pharmacyWorkQueue.go/markLabAsReviewed",method= {RequestMethod.POST})
	public @ResponseBody boolean markLabAsReviewed(HttpServletRequest request,@RequestParam("reportIdList") String reportIdList,@RequestParam("userId") int loggedInUserId)  {
		return pharmacyLabReviewService.markLabAsReviewed(reportIdList,loggedInUserId);
	}

	@RequestMapping(value="/pharmacyWorkQueue.go/getWorkQueueData",method=RequestMethod.POST)
	public @ResponseBody String getWorkQueueData(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> filterMap)  {

		JSONObject result = new JSONObject(workQueueService.getWorkQueueData(filterMap));
		return result.toString();

	}

	@RequestMapping(value="/pharmacyWorkQueue.go/updatePharmacyStatus",method=RequestMethod.POST)
	public @ResponseBody boolean updatePharmacyStatus(@RequestParam("status") int statusId, @RequestParam("verificationId") int verificationId, @RequestParam("reasonId") int reasonId, @RequestParam("modifiedBy") int modifiedBy, @RequestParam("verifiedBy") int verifiedBy)  {

		return workQueueService.updatePharmacyStatus(verificationId, statusId, reasonId, verifiedBy, modifiedBy);

	}

	@RequestMapping(value="/pharmacyWorkQueue.go/getInitDataForWorkQueueTabs",method=RequestMethod.POST)
	public @ResponseBody String getInitDataForWorkQueueTabs(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> dataMap)  {

		return new JSONObject(workQueueService.getInitDataForWorkQueueTabs(dataMap)).toString();

	}

	@RequestMapping(value="/pharmacyWorkQueue.go/getRecordCountForTabs",method=RequestMethod.GET)
	public @ResponseBody String getRecordCountForTabs(HttpServletRequest request, HttpServletResponse response)  {

		return workQueueService.getRecordCountForTabs();
	}

	@RequestMapping(value="/pharmacyWorkQueue.go/launchPharmacyTab/{patientId}/{encounterId}/{encounterType}/{episodeEncounterId}/{departmentId}/{serviceType}", method = RequestMethod.GET)
	public String launchPharmacyTab(HttpServletRequest request, HttpServletResponse response, @PathVariable("patientId") int patientId,  @PathVariable("encounterId") int encounterId, @PathVariable("encounterType") int encounterType, @PathVariable("episodeEncounterId") int episodeEncounterId, @PathVariable("departmentId") int departmentId, @PathVariable("serviceType") int serviceType ,Model model) {
		model.addAttribute("patientId", patientId);
		model.addAttribute("encounterId", encounterId);
		model.addAttribute("encounterType", encounterType);
		model.addAttribute("episodeEncounterId", episodeEncounterId);
		model.addAttribute("departmentId", departmentId);
		model.addAttribute("serviceType", serviceType);
		return "staticContent/pharmacy/workQueue/views/pharmacyTabs";
	}

	@RequestMapping(value = "/pharmacyWorkQueue.go/launchMedicationProfile/{patientId}/{encounterId}/{encounterType}/{episodeEncounterId}", method = RequestMethod.GET)
	public String launchMedicationProfile(HttpServletRequest request, HttpServletResponse response, @PathVariable("patientId") int patientId, @PathVariable("encounterId") int encounterId, @PathVariable("encounterType") int encounterType, @PathVariable("episodeEncounterId") int episodeEncounterId, Model model) {
		model.addAttribute("patientId", patientId);
		model.addAttribute("encounterId", encounterId);
		model.addAttribute("encounterType", encounterType);
		model.addAttribute("episodeEncounterId", episodeEncounterId);
		return "staticContent/pharmacy/workQueue/views/medicationProfile";
	}

	@RequestMapping(value = "/pharmacyWorkQueue.go/getMedicationProfile/{patientId}/{userId}", method = RequestMethod.GET)
	public @ResponseBody String getMedicationProfile(HttpServletRequest request, HttpServletResponse response, @PathVariable("patientId") int patientId, @PathVariable("userId") int loggedInUserId) {
		Map<String, Object> medicationProfile = medicationProfileService.getMedicationProfile(patientId, loggedInUserId);
		return new JSONObject(medicationProfile).toString();
	}

	@RequestMapping(value = "/pharmacyWorkQueue.go/getMedicationProfileData/{patientId}/{pharmacyStatus}/{userId}", method = RequestMethod.GET)
	public @ResponseBody String getMedicationProfileData(HttpServletRequest request, HttpServletResponse response, @PathVariable("patientId") int patientId, @PathVariable("pharmacyStatus") int pharmacyStatus, @PathVariable("userId") int loggedInUserId) {
		return medicationProfileService.getMedicationProfileData(patientId, pharmacyStatus, loggedInUserId);
	}

	@RequestMapping(value = "/pharmacyWorkQueue.go/launchMedicationOrders/{orderId}/{patientId}/{encounterId}/{encounterType}/{pharmacyStatus}/{concurrentUserId}", method = RequestMethod.GET)
	public String launchMedicationOrders(HttpServletRequest request, HttpServletResponse response, @PathVariable("orderId") int orderId, @PathVariable("patientId") int patientId,@PathVariable("encounterId") int encounterId, @PathVariable("encounterType") int encounterType, @PathVariable("pharmacyStatus") int pharmacyStatus, @PathVariable("concurrentUserId") int concurrentUserId, Model model) {
		model.addAttribute("orderId", orderId);
		model.addAttribute("patientId", patientId);
		model.addAttribute("encounterId", encounterId);
		model.addAttribute("encounterType", encounterType);
		model.addAttribute("pharmacyStatus", pharmacyStatus);
		model.addAttribute("concurrentUserId", concurrentUserId);
		return "staticContent/pharmacy/workQueue/views/medicationOrders";
	}


	@RequestMapping(value = "/pharmacyWorkQueue.go/getMedOrderDetails/{orderId}/{patientId}/{pharmacyStatus}/{userId}/{concurrentUserId}", method = RequestMethod.GET)
	public @ResponseBody String getMedOrderDetails(HttpServletRequest request, HttpServletResponse response, @PathVariable("orderId") int orderId, @PathVariable("patientId") int patientId, @PathVariable("pharmacyStatus") int pharmacyStatus, @PathVariable("userId") int loggedInUserId, @PathVariable("concurrentUserId") int concurrentUserId) {
		if(concurrentUserId > 0){
			pharmacyConcurrencyService.clearSessionLog(orderId, patientId, concurrentUserId);
		}

		Map<String, Object> medOrderDetails = workQueueService.getMedicationOrderDetails(orderId, patientId, pharmacyStatus, loggedInUserId, true);

		pharmacyConcurrencyService.createPharmacyVerificationSessionLog(orderId, patientId, loggedInUserId);

		return new JSONObject(medOrderDetails).toString();

	}

	@RequestMapping(value = "/pharmacyWorkQueue.go/getMedOrder", method = RequestMethod.GET)
	public @ResponseBody String getMedOrder(HttpServletRequest request, HttpServletResponse response, @RequestParam("patientId") int patientId, @RequestParam("orderId") int orderId, @RequestParam("userId") int loggedInUserId, @RequestParam("concurrentUserId") int concurrentUserId) {

		if(concurrentUserId > 0){
			pharmacyConcurrencyService.clearSessionLog(orderId, patientId, concurrentUserId);
		}

		Map<String, Object> medOrder = workQueueService.getMedicationOrderDetails(orderId, patientId, -1, loggedInUserId, true);

		pharmacyConcurrencyService.createPharmacyVerificationSessionLog(orderId, patientId, loggedInUserId);


		return new JSONObject(medOrder).toString();
	}

	@RequestMapping(value="/pharmacyWorkQueue.go/assignOrders",method=RequestMethod.POST)
	public @ResponseBody boolean assignOrders(@RequestParam("assignToId") int assignToId, @RequestParam("workQueueIds") String workQueueIds, @RequestParam("modifiedBy") int modifiedBy, @RequestParam("assignedBy") int assignedBy)  {

		String verificationIds[] = workQueueIds.split(",");
		return workQueueService.assignOrders(verificationIds, assignToId, assignedBy, modifiedBy);

	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value="/pharmacyWorkQueue.go/getPendingReasons",method=RequestMethod.GET)
	public @ResponseBody String getPendingReasons()  {

		JSONArray arrPendingReasons = new JSONArray();
		try {
			ArrayList<TemplateReasonDictionaryItems>  pendingReasonList = workQueueService.getPendingReasons();
			for(TemplateReasonDictionaryItems pendingReason : pendingReasonList){
				JSONObject jObject = new JSONObject();
				jObject.put("id", pendingReason.getId());
				jObject.put("reasonName", pendingReason.getReasonName());
				jObject.put("status", pendingReason.getStatus());
				arrPendingReasons.add(jObject);

			}
		} catch (JSONException e) {
			EcwLog.AppendExceptionToLog(e);
		}


		return arrPendingReasons.toString();

	}

	@RequestMapping(value="/pharmacyWorkQueue.go/saveOrder", method=RequestMethod.POST)
	public @ResponseBody String saveOrder(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> dataMap)  {

		JSONObject result = new JSONObject();

		try{
			Gson gson = new Gson();
			MedOrder medOrder = gson.fromJson(dataMap.get("medOrder").toString(), MedOrder.class);
			int loggedInUserId = gson.fromJson(dataMap.get("userId").toString(), Integer.class);
			int pharmacyStatus = gson.fromJson(dataMap.get("pharmacyStatus").toString(), Integer.class);
			int reasonId = gson.fromJson(dataMap.get("reasonId").toString(), Integer.class);

			boolean printLabel = false;

			ArrayList<MedicationDispenseDetailDTO> medOrderDetailListCPOE = new ArrayList<MedicationDispenseDetailDTO>();

			MedicationOrder medOrderCPOE = pharmacyMedOrderService.getMedOrderCPOEObj(medOrder);

			for(MedOrderDetail medOrderDetail : medOrder.getMedOrderDetailList()){

				MedicationDispenseDetailDTO medOrderDetailCPOE = pharmacyMedOrderService.getMedOrderDetailCPOEObj(medOrder, medOrderDetail);
				medOrderDetailListCPOE.add(medOrderDetailCPOE);

				if(pharmacyStatus == PharmacyConstants.PharmacyVerificationStatus.VERIFIED.getId()){
					if(!medOrderDetail.isFloorStock() && !medOrder.isDrugTypeBulk() && !medOrder.isPRN() && !medOrder.isDiscontinued()){
						printLabel = true;
					}
				}
			}

			/* All the products in the order should have floor stock for sending msg to pyxis, else the order will be added for printing label [JIRA #IPUSA-5063]*/
			if(printLabel){
				auditLogService.logEvent(loggedInUserId, PharmacyAuditLogsConstants.MODULE_PHARMACY_VERIFICATION, PharmacyAuditLogsConstants.ACTION_PVQ_PRINT_LABEL, new JSONObject(medOrder), "");
			} else {
				auditLogService.logEvent(loggedInUserId, PharmacyAuditLogsConstants.MODULE_PHARMACY_VERIFICATION, PharmacyAuditLogsConstants.ACTION_PVQ_SEND_PYXIS_MSG, new JSONObject(medOrder), "");
			}

			StatusMap statusMap = medService.updateMedicationOrderService(medOrderCPOE, medOrderDetailListCPOE);
			if(statusMap.isSuccess()){
				auditLogService.logEvent(loggedInUserId, PharmacyAuditLogsConstants.MODULE_PHARMACY_VERIFICATION, PharmacyAuditLogsConstants.ACTION_PVQ_UPDATE_ORDER, new JSONObject(medOrder), "");
			}

			//update schedule
			if(pharmacyOrderScheduleService.updateOrderSchedule(medOrder, loggedInUserId)){
				auditLogService.logEvent(loggedInUserId, PharmacyAuditLogsConstants.MODULE_PHARMACY_VERIFICATION, PharmacyAuditLogsConstants.ACTION_PVQ_UPDATE_SCHEDULE, new JSONObject(medOrder), "");
			}

			workQueueService.updatePharmacyVerification(workQueueService.getPharmacyVerificationObj(medOrder, loggedInUserId, pharmacyStatus, reasonId));

			boolean printLabelMsg = false;

			if( pharmacyStatus == PharmacyConstants.PharmacyVerificationStatus.VERIFIED.getId()){

				workQueueService.savePharmacyBillingData(medOrder, loggedInUserId);

				if(printLabel){
					//Check if printLabel has to be called manually
					printLabelMsg = workQueueService.isPrintLabelRequired(medOrder.getOrderId());

				}
			}

			result.put("status", "success");
			result.put("printLabelMsg", printLabelMsg);
			try {
				Map<String,Object> requestMap = new HashMap<>();
				requestMap.put("isDiscontinued",medOrder.isDiscontinued());
				requestMap.put("isFloorStock",medOrder.isFloorStock());
				requestMap.put("isDrugTypeBulk", medOrder.isDrugTypeBulk());
				requestMap.put("isPRN", medOrder.isPRN());
				requestMap.put("isDrugDispenseCode", medOrder.isDrugDispenseCode());
				requestMap.put("userId",loggedInUserId);
				requestMap.put("orderId",medOrder.getOrderId());
				requestMap.put("medOrderId",medOrder.getMedOrderId());
				requestMap.put("facilityId",medOrder.getFacilityId());
				requestMap.put("bedId",medOrder.getEncBedId());
				requestMap.put("unitId",medOrder.getEncUnitId());
				requestMap.put("roomId",medOrder.getEncAreaId());
				requestMap.put("deptId",medOrder.getEncDeptId());
				Map<String,Object> responseMap =  workQueueService.printLabelForMedDispense(requestMap);
				result.put("PyxisAndLabelResponse", responseMap);
			} catch(Exception e){
				EcwLog.AppendExceptionToLog(e);
			}
		} catch(Exception e){
			EcwLog.AppendExceptionToLog(e);
		}

		return result.toString();

	}

	@RequestMapping(value="/pharmacyWorkQueue.go/launchMyPatientsList", method=RequestMethod.GET)
	public String launchMyPatientsList(HttpServletRequest request, HttpServletResponse response)  {
		return "staticContent/pharmacy/workQueue/views/myPatients";
	}

	@RequestMapping(value="/pharmacyWorkQueue.go/getMyPatientsInitData/{userId}", method=RequestMethod.GET)
	public @ResponseBody String getMyPatientsInitData(HttpServletRequest request, HttpServletResponse response, @PathVariable("userId") int loggedInUserId)  {

		return myPatientsService.getMyPatientsInitData(loggedInUserId).toString();
	}

	@RequestMapping(value="/pharmacyWorkQueue.go/getPatientsByFilter", method=RequestMethod.POST)
	public @ResponseBody String getPatientsByFlowsheetId(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> dataMap)  {
		return myPatientsService.getPatientsByFilter(dataMap).toString();
	}

	@RequestMapping(value="/pharmacyWorkQueue.go/getFlowsheetItems", method=RequestMethod.POST)
	public @ResponseBody String getFlowsheetItems(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> dataMap)  {
		Gson gson = new Gson();
		String[] datesToShow = gson.fromJson(dataMap.get("datesToShow").toString(), String[].class);
		int loggedInUserId = gson.fromJson(dataMap.get("userId").toString(), Integer.class);
		int flowsheetId = gson.fromJson(dataMap.get("flowsheetId").toString(), Integer.class);
		int patientId = gson.fromJson(dataMap.get("patientId").toString(), Integer.class);
		int episodeEncounterId = gson.fromJson(dataMap.get("episodeEncounterId").toString(), Integer.class);

		return myPatientsService.getFlowsheetItems(flowsheetId, patientId, episodeEncounterId, Arrays.asList(datesToShow), loggedInUserId).toString();
	}

	@RequestMapping(value="/pharmacyWorkQueue.go/getPreRequisitesForOrder", method=RequestMethod.GET)
	public @ResponseBody String getPreRequisitesForOrder(HttpServletRequest request, HttpServletResponse response, @RequestParam("userId") int loggedInUserId, @RequestParam("patientId") int patientId, @RequestParam("orderId") int orderId, @RequestParam("showMandatory") boolean showMandatory )  {
		JSONObject result = new JSONObject(pharmacyPreRequisiteService.getPreRequisitesByOrderId(orderId, patientId, showMandatory, loggedInUserId));
		return result.toString();
	}


	@RequestMapping(value="/pharmacyWorkQueue.go/isInterventionsCompleted", method=RequestMethod.GET)
	public @ResponseBody boolean isInterventionsCompleted(HttpServletRequest request, HttpServletResponse response, @RequestParam("orderId") int orderId)  {
		boolean result = workQueueService.isInterventionsCompleted(orderId);
		return result;
	}

	@RequestMapping(value="/pharmacyWorkQueue.go/isMedAvailableForFacility", method=RequestMethod.POST)
	public @ResponseBody boolean isMedAvailableForFacility(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> dataMap)  {
		Gson gson = new Gson();
		MedOrder medOrder = gson.fromJson(dataMap.get("medOrder").toString(), MedOrder.class);

		return pharmacyMedOrderService.isMedAvailableForFacility(medOrder);

	}

	@RequestMapping(value="/pharmacyWorkQueue.go/getConcurrentUserLog/{orderId}/{patientId}/{userId}", method=RequestMethod.GET)
	public @ResponseBody PharmacyVerificationSessionLog getConcurrentUserLog(HttpServletRequest request, HttpServletResponse response, @PathVariable("patientId") int patientId, @PathVariable("userId") int userId, @PathVariable("orderId") int orderId)  {
		return pharmacyConcurrencyService.getConcurrentUserLog(orderId, patientId, userId);
	}

	@RequestMapping(value="/pharmacyWorkQueue.go/clearSessionLog", method=RequestMethod.POST)
	public @ResponseBody boolean clearSessionLog(HttpServletRequest request, HttpServletResponse response,  @RequestBody Map<String, Object> dataMap)  {
		Gson gson = new Gson();
		int orderId = gson.fromJson(dataMap.get("orderId").toString(), Integer.class);
		int patientId = gson.fromJson(dataMap.get("patientId").toString(), Integer.class);
		int userId = gson.fromJson(dataMap.get("userId").toString(), Integer.class);

		return pharmacyConcurrencyService.clearSessionLog(orderId, patientId, userId);

	}

	@RequestMapping(value="/pharmacyWorkQueue.go/getPrintData", method=RequestMethod.POST)
	@ResponseBody
	public StatusMap getPrintData(@RequestBody Map<String, Object> requestBody, @ModelAttribute("uid") int nTrUserId)  {
		StatusMap oStatus = new StatusMap();
		try{
			if(requestBody==null || requestBody.isEmpty() || "".equals(Util.getStrValue(requestBody, "patientId")))
			{
				oStatus.setFail("Failed to post Request on Server");
				return oStatus;
			}
			oStatus.put("PrintHtml",medicationProfileService.generateHtmlForMedicationProfile(Util.getIntValue(requestBody, "patientId"),nTrUserId));		
			oStatus.setSuccess();
		}catch (Exception e) {
			EcwLog.AppendExceptionToLog(e);
			oStatus.setFail("error occured");
		}
		return oStatus;
	}

	@RequestMapping(value="/pharmacyWorkQueue.go/getOrderLabelPrintData", method=RequestMethod.POST)
	@ResponseBody
	public StatusMap getOrderLabelPrintData(@RequestBody Map<String, Object> requestBody, @ModelAttribute("uid") int nTrUserId)  {
		StatusMap oStatus = new StatusMap();
		try{
			if(requestBody==null || requestBody.isEmpty() || "".equals(Util.getStrValue(requestBody, "orderId")))
			{
				oStatus.setFail("Failed to post Request on Server");
				return oStatus;
			}
			requestBody.put("userId", nTrUserId);
			if(Util.getIntValue(requestBody, "numberOfPrint")>0) {

				Map<String, Object> responseMap = workQueueService.getOrderLabelDataPVQ(requestBody);
				if(responseMap.containsKey("Status") && "success".equalsIgnoreCase(responseMap.get("Status").toString())) {
					oStatus.setSuccess();
				}else{
					String message= responseMap.containsKey("Message")?responseMap.get("Message").toString():"Failed to print the label";
					oStatus.setFail(message);
				}
			}else{				
				oStatus.setFail("Failed to post Request on Server");
			}
			
		}catch (Exception e) {
			EcwLog.AppendExceptionToLog(e);
			oStatus.setFail("error occured");
		}
		return oStatus;
	}
	
	/**
	 * Added by: Narendra Kumar fetching med orders logs details data
	 * @param request
	 * @param response
	 * @param patientId
	 * @param orderId
	 * @param loggedInUserId
	 * @param concurrentUserId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/pharmacyWorkQueue.go/getMedOrderLogs", method = RequestMethod.POST)
	public @ResponseBody String getMedOrderLogs(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> dataMap) {
		JSONArray arrMedOrderLogs = new JSONArray();
		Gson gson = new Gson();
		try {
				int patientId = gson.fromJson(dataMap.get("patientId").toString(), Integer.class);
				int loggedInUserId = gson.fromJson(dataMap.get("userId").toString(), Integer.class);
				List<WorkQueue>  items = pharmacyMedOrderService.getOrderLogs(patientId, loggedInUserId);
				for(WorkQueue list : items){
					JSONObject jObject = new JSONObject();
					jObject.put("id", list.getId());
					jObject.put("drugName", list.getItemName() +" "+list.getOrderDetail());
					jObject.put("orderDateTime", list.getOrderDateTime());
					jObject.put("startDateTime", list.getStartDateTime());
					jObject.put("stopDateTime", list.getEndDateTime());
					jObject.put("verifiedBy", list.getVerifiedByName());
					jObject.put("verifiedDateTime", list.getVerifiedOn());
					jObject.put("orderingProviderName", list.getOrderingProviderName());
					arrMedOrderLogs.add(jObject);
				}
		} catch (JSONException e) {
			EcwLog.AppendExceptionToLog(e);
		}
		return arrMedOrderLogs.toString();	
	}
	
	@RequestMapping(value="/pharmacyWorkQueue.go/chargeCaptureBillingData", method=RequestMethod.POST)
	public @ResponseBody boolean chargeCaptureBillingData(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> dataMap)  {
		boolean result = false;
		try{
			Gson gson = new Gson();
			MedOrder medOrder = gson.fromJson(dataMap.get("medOrder").toString(), MedOrder.class);
			int loggedInUserId = gson.fromJson(dataMap.get("userId").toString(), Integer.class);
			int pharmacyStatus = gson.fromJson(dataMap.get("pharmacyStatus").toString(), Integer.class);
			if( pharmacyStatus == PharmacyConstants.PharmacyVerificationStatus.VERIFIED.getId()){
				workQueueService.savePharmacyBillingData(medOrder, loggedInUserId, "chargeCapture");
				result = true;
			}
			
		} catch(Exception e){
			EcwLog.AppendExceptionToLog(e);
		}

		return result;
	}
	
	@RequestMapping(value="/pharmacyWorkQueue.go/pharmacyPrinterSetup", method=RequestMethod.POST)
	public @ResponseBody StatusMap checkPharmacyPrinterSetup(@RequestBody Map<String, Object> requestBody)  {
		StatusMap oStatus = new StatusMap();
		try{
			int result = workQueueService.checkPharmacyPrinterSetting(Util.getIntValue(requestBody,"facilityId"),Util.getIntValue(requestBody,"orderTypeId"));
			oStatus.setSuccess();
			oStatus.setData(result);
		}catch(RuntimeException e){
			EcwLog.AppendExceptionToLog(e);
		}
		return oStatus;
	}

}