package inpatientWeb.pharmacy.serviceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import inpatientWeb.Auth.exception.InvalidParameterException;
import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;
import inpatientWeb.Global.ecw.ambulatory.json.JSONException;
import inpatientWeb.Global.ecw.ambulatory.json.JSONObject;
import inpatientWeb.Global.ecw.auditlogs.AuditLogService;
import inpatientWeb.Global.service.EcwAppContext;
import inpatientWeb.admin.pharmacySettings.batchesSetup.beans.TemplateForBatch;
import inpatientWeb.admin.pharmacySettings.batchesSetup.service.BatchesSetupService;
import inpatientWeb.admin.pharmacySettings.batchesSetup.service.MedDispenseProcessService;
import inpatientWeb.admin.pharmacySettings.batchesSetup.util.PharmacyLabelConstant;
import inpatientWeb.admin.pharmacySettings.configureDictionary.dao.ConfigurationDictionaryDao;
import inpatientWeb.admin.pharmacySettings.configureDictionary.dao.TemplateReasonDictionaryItems;
import inpatientWeb.admin.pharmacySettings.configureDictionary.service.ConfigurationDictionaryService;
import inpatientWeb.admin.pharmacySettings.generalSettings.dao.PharmacySettingsDao;
import inpatientWeb.admin.pharmacySettings.generalSettings.model.PharmacySettingOrderSetting;
import inpatientWeb.admin.pharmacySettings.generalSettings.service.PharmacyPrinterSettingsService;
import inpatientWeb.admin.pharmacySettings.intervensionReason.service.IntervensionReasonService;
import inpatientWeb.admin.pharmacySettings.pharmacyHelpers.PharmacyAuditLogsConstants;
import inpatientWeb.admin.pharmacySettings.pharmacyHelpers.PharmacyConstants;
import inpatientWeb.admin.pharmacySettings.pharmacyHelpers.PharmacyHelper;
import inpatientWeb.admin.pharmacySettings.pharmacyHelpers.PharmacySecurityConstants;
import inpatientWeb.billing.transactions.BillingDataTransaction;
import inpatientWeb.lis.labresult.LabResultService;
import inpatientWeb.pharmacy.beans.MedOrder;
import inpatientWeb.pharmacy.beans.PharmacyStatus;
import inpatientWeb.pharmacy.beans.PharmacyVerification;
import inpatientWeb.pharmacy.beans.WorkQueue;
import inpatientWeb.pharmacy.billingdata.constant.PharmacyLogEnum;
import inpatientWeb.pharmacy.billingdata.model.DrugNdcDetails;
import inpatientWeb.pharmacy.billingdata.model.PharmacyBillingData;
import inpatientWeb.pharmacy.billingdata.service.PharmacyBillingService;
import inpatientWeb.pharmacy.dao.PharmacyVerificationDAO;
import inpatientWeb.pharmacy.dao.WorkQueueDAO;
import inpatientWeb.pharmacy.service.PharmacyMedOrderService;
import inpatientWeb.pharmacy.service.PharmacyPreRequisiteService;
import inpatientWeb.pharmacy.service.WorkQueueService;
import inpatientWeb.utils.IPTzUtils;
import inpatientWeb.utils.Util;

@Service
public class WorkQueueServiceImpl implements WorkQueueService {

	@Autowired
	public WorkQueueDAO workQueueDAO;

	@Autowired
	public PharmacyVerificationDAO pharmacyVerificationDAO;

	@Autowired
	public ConfigurationDictionaryDao configDictionaryDAO;

	@Autowired
	public BatchesSetupService batchesSetupService;

	@Autowired
	public IntervensionReasonService interventionReasonService;

	@Autowired
	public PharmacySettingsDao pharmacySettingsDao;

	@Autowired
	ConfigurationDictionaryService freqDictionaryService;

	@Autowired
	PharmacyHelper pharmacyHelper;

	@Autowired
	AuditLogService auditLogService;
	
	@Autowired
	PharmacyMedOrderService pharmacyMedOrderService;
	
	@Autowired
	PharmacyPreRequisiteService pharmacyPreRequisiteService;
	
	@Autowired
	public PharmacyBillingService pharmacyBillingService;
	
	@Autowired
	private PharmacyPrinterSettingsService pharmacyPrinterSettingsService;

	public static final int RECORDS_PER_PAGE = 10;
	
	@Autowired
	public MedDispenseProcessService medDispenseProService;
	
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getWorkQueueData(Map<String, Object> filterMap)  {
		
		if(filterMap == null || filterMap.isEmpty()){
			throw new InvalidParameterException("filterMap cannot be null or empty");
		}

		HashMap<String, Object> result = new HashMap<String, Object>();

		int loggedInUserId = Util.getIntValue(filterMap, "loggedInUserId");
		final String userTimeZone = IPTzUtils.getTimeZoneForResource(loggedInUserId);

		try{

			String startDate = !Util.isNullEmpty(Util.getStrValue(filterMap, "startDate")) ? IPTzUtils.convertDateStrInTz((String)(filterMap.get("startDate")) + " 00:00:00", "yyyy-MM-dd HH:mm:ss", userTimeZone, IPTzUtils.DEFAULT_DB_DT_FMT, IPTzUtils.DEFAULT_DB_TIME_ZONE  ) : "";
            String endDate = !Util.isNullEmpty(Util.getStrValue(filterMap, "endDate")) ? IPTzUtils.convertDateStrInTz((String)filterMap.get("endDate") + " 23:59:59", "yyyy-MM-dd HH:mm:ss", userTimeZone, IPTzUtils.DEFAULT_DB_DT_FMT, IPTzUtils.DEFAULT_DB_TIME_ZONE  ) : "";
			ArrayList<String> pharmacyStatuses = filterMap.get("pharmacyStatuses") != null && !"".equals(filterMap.get("pharmacyStatuses")) ? (ArrayList<String>)filterMap.get("pharmacyStatuses") : new ArrayList<String>();

			filterMap.put("startDate", startDate);
			filterMap.put("endDate", endDate);
			filterMap.put("pharmacyStatuses", pharmacyStatuses);

			int totalRecords = workQueueDAO.getWorkQueueCount(filterMap);
			double totalPages =  Math.ceil( (float) totalRecords / (int)filterMap.get("recordsPerPage"));
			int selectedPage = (int)filterMap.get("selectedPage");

			if(totalPages < selectedPage){
				selectedPage = 1;
			}
			filterMap.put("selectedPage", selectedPage);

			ArrayList<WorkQueue> workQueueList = workQueueDAO.getWorkQueueData(filterMap);

			result.put("workQueue", workQueueList);
			result.put("totalWorkQueueRecords", totalRecords);
			result.put("recordsPerPage", (int)filterMap.get("recordsPerPage"));
			result.put("totalPages", totalPages);

			result.put("orderPriorityList", workQueueDAO.getOrderPriority());
			result.put("pharmacyStatusList", pharmacyVerificationDAO.getPharmacyStatus());
			result.put("pendingReasonList", getPendingReasons());
			result.put("hasUpdateMedOrderAccess", pharmacyHelper.hasAccessForSecurityItem(PharmacySecurityConstants.PVQ_UPDATE_MEDORDER_SECURITY_KEY, Util.getIntValue(filterMap, "loggedInUserId")));
		}catch(ParseException e){
			EcwLog.AppendExceptionToLog(e);
		}

		return result;
	}

	public ArrayList<TemplateReasonDictionaryItems> getPendingReasons(){
		ArrayList<TemplateReasonDictionaryItems> pendingReasonList = (ArrayList<TemplateReasonDictionaryItems>)configDictionaryDAO.getAllActiveFormularyReasonDictionaryItems(TemplateReasonDictionaryItems.DICTIONARY_ACTION_PENDING_REASON);
		return pendingReasonList;
	}

	public boolean updatePharmacyStatus(int verificationId, int status, int reasonId, int verifiedBy, int modifiedBy ){
		
		if(verificationId <= 0){
			throw new InvalidParameterException("Invalid verificationId");
		}
		
		boolean result = false;
		if(status != PharmacyConstants.PharmacyVerificationStatus.VERIFIED.getId()){
			verifiedBy = 0;
		}
		Map<String,Object> paramMap = new HashMap<String,Object>();
		String currentDateTime = IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT);
		paramMap.put("status", status);
		paramMap.put("verifiedBy", verifiedBy);
		paramMap.put("modifiedBy", modifiedBy);
		paramMap.put("reasonId", reasonId);
		paramMap.put("verifiedOn", currentDateTime);
		paramMap.put("modifiedOn", currentDateTime);
		paramMap.put("verificationId", verificationId);

		result = pharmacyVerificationDAO.updateStatus(paramMap);
		if(result){
			auditLogService.logEvent(modifiedBy, PharmacyAuditLogsConstants.MODULE_PHARMACY_VERIFICATION, PharmacyAuditLogsConstants.ACTION_PVQ_STATUS_CHANGE, paramMap, "");
		}

		return result;
	}

	public boolean createPharmacyVerification(PharmacyVerification pharmacyVerification){
		boolean result = false;
		result = pharmacyVerificationDAO.createPharmacyVerification(pharmacyVerification);
		if(result){
			auditLogService.logEvent(pharmacyVerification.getCreatedBy(), PharmacyAuditLogsConstants.MODULE_PHARMACY_VERIFICATION, PharmacyAuditLogsConstants.ACTION_PVQ_CREATE_VERIFICATION, new JSONObject(pharmacyVerification), "");
		}
		return result;
	}
	
	public boolean resetToPharmacyVerificationStatusByOrderId(PharmacyVerification pharmacyVerification){
		boolean result = false;
		result = pharmacyVerificationDAO.resetToPharmacyVerificationStatusByOrderId(pharmacyVerification);
		if(result){
			auditLogService.logEvent(pharmacyVerification.getModifiedBy(), PharmacyAuditLogsConstants.MODULE_PHARMACY_VERIFICATION, PharmacyAuditLogsConstants.ACTION_PVQ_CREATE_VERIFICATION, new JSONObject(pharmacyVerification), "");
		}
		return result;
	}
	
	public String getRecordCountForTabs() {

		return (new JSONObject(workQueueDAO.getRecordCountForTabs())).toString();
	}

	public Map<String, Object> getInitDataForWorkQueueTabs(Map<String, Object> filterMap) {
		
		if(filterMap == null || filterMap.isEmpty()){
			throw new InvalidParameterException("filterMap cannot be null or empty");
		}

		Map<String, Object> result = new HashMap<String, Object>();

		int facilityId = workQueueDAO.getFacilityIdByUserId(Util.getIntValue(filterMap, "loggedInUserId"));
		PharmacySettingOrderSetting pharmacyOrderSetting = pharmacySettingsDao.getPharmacyOrderSettings(facilityId);
		if(pharmacyOrderSetting != null){
			result.put("workQueueRefreshTime", pharmacyOrderSetting.getWorkQueueRefreshTime());
			result.put("workQueueRefreshTimeUnit", pharmacyOrderSetting.getWorkQueueRefreshTimeUnit());
		}
		result.put("recordCounts", workQueueDAO.getRecordCountForTabs());
		result.put("hasMyPatientsAccess", pharmacyHelper.hasAccessForSecurityItem(PharmacySecurityConstants.PVQ_MYPATIENTS_SECURITY_KEY, Util.getIntValue(filterMap, "loggedInUserId")));

		return result;
	}

	public  Map<String, Object> getMedicationOrderDetails(int orderId, int patientId, int pharmacyStatus, int loggedInUserId, boolean loadAll){
		
		if(orderId <= 0){
			throw new InvalidParameterException("Invalid orderId");
		}
		if(patientId <= 0){
			throw new InvalidParameterException("Invalid patientId");
		}
		if(loggedInUserId <= 0){
			throw new InvalidParameterException("Invalid loggedInUserId");
		}
		
		
		Map<String, Object> result = new  HashMap<String, Object>();

		MedOrder medOrder =  pharmacyMedOrderService.getMedicationOrder(orderId, patientId, loggedInUserId);

		result.put("medOrder", medOrder);
		result.put("frequencyList", freqDictionaryService.getFrequenciesByExternalMapping(medOrder.getFrequencyId(), medOrder.getFacilityId()));
		result.put("preRequisites", pharmacyPreRequisiteService.getPreRequisitesForOrder(medOrder.getMedOrderDetailList(), medOrder.getRoutedGenericItemId(), medOrder.getEpisodeEncounterId(), true,  loggedInUserId));
		result.put("batchesList", batchesSetupService.getBatchDetailsByOrderId(orderId));
		result.put("reflexAlerts", workQueueDAO.getAllReflexAlerts(medOrder.getStagingOrderId()));
		result.put("InterventionCounts", interventionReasonService.getInterventionCountsForOrder(orderId));

		if(loadAll){
			ArrayList<HashMap<String, String>> medList = getMedOrdersByPatient(patientId, pharmacyStatus, medOrder.getOrderTypeInt(), loggedInUserId);
			result.put("leftMedList", medList);
			result.put("orderPriorityMasterList", workQueueDAO.getOrderPriority());
		} 

		List<String> pvqSecurityKeys = new ArrayList<String>();
		pvqSecurityKeys.add(PharmacySecurityConstants.PVQ_VERIFY_MEDORDER_SECURITY_KEY);
		pvqSecurityKeys.add(PharmacySecurityConstants.PVQ_UPDATE_MEDORDER_SECURITY_KEY);
		pvqSecurityKeys.add(PharmacySecurityConstants.PVQ_PRINT_LABEL_SECURITY_KEY);
		if(medOrder.getFacilityId() > 0 && patientId > 0) {
			Map<String, Object> loincCode = workQueueDAO.getLoincCodeBasedOnFacilityId(medOrder.getFacilityId());
			if(!loincCode.isEmpty()) {
				LabResultService labResultService =(LabResultService) EcwAppContext.getObject(LabResultService.class);
				if(!Util.isNullEmpty(Util.getStrValue(loincCode, "scrLoincId")))
					result.put("scrLabValue", labResultService.getLabResultForPharmacy(patientId, Util.getStrValue(loincCode, "scrLoincId")));
				if(!Util.isNullEmpty(Util.getStrValue(loincCode, "crclLoincId")))
					result.put("crclLabValue", labResultService.getLabResultForPharmacy(patientId, Util.getStrValue(loincCode, "crclLoincId")));
			} 			
		}
		result.put("SecuritySettings", pharmacyHelper.getSecurityItemForUser(pvqSecurityKeys, loggedInUserId));
		result.put("SecuritySettingsIntervention", interventionReasonService.getInterventionSecurityItemsForUser(loggedInUserId));

		auditLogService.logEvent(loggedInUserId, PharmacyAuditLogsConstants.MODULE_PHARMACY_VERIFICATION, PharmacyAuditLogsConstants.ACTION_PVQ_VIEW_ORDER, new JSONObject(medOrder), "");

		return result;
	}

	public ArrayList<HashMap<String, String>> getMedOrdersByPatient(int patientId, int pharmacyStatus, int orderType, int loggedInUserId){
		
		if(patientId <= 0){
			throw new InvalidParameterException("Invalid patientId");
		}
		if(loggedInUserId <= 0){
			throw new InvalidParameterException("Invalid loggedInUserId");
		}

		ArrayList<HashMap<String, String>> medList = new ArrayList<HashMap<String, String>>();

		for(WorkQueue med : workQueueDAO.getOrdersForLeftPanel(patientId, pharmacyStatus, orderType)){
			HashMap<String, String> medMap = new HashMap<String, String>();

			medMap.put("id", String.valueOf(med.getId()));
			medMap.put("orderId", String.valueOf(med.getOrderId()));
			medMap.put("medOrderId", String.valueOf(med.getMedOrderId()));
			medMap.put("itemName", med.getItemName());
			medMap.put("pharmacyStatusId", String.valueOf(med.getPharmacyStatusId()));
			medMap.put("pharmacyStatus", med.getPharmacyStatus());
			medMap.put("interventionStatus", med.getInterventionStatus());
			medMap.put("isPOM", String.valueOf(med.isPOM()));
			medList.add(medMap);
		}

		return medList;

	}

	public ArrayList<HashMap<String, String>> getMedOrdersByPatient(int patientId){
		
		if(patientId <= 0){
			throw new InvalidParameterException("Invalid patientId");
		}
		ArrayList<HashMap<String, String>> medList = new ArrayList<HashMap<String, String>>();

		for(MedOrder med : workQueueDAO.getActiveOrders(patientId)){
			HashMap<String, String> medMap = new HashMap<String, String>();

			medMap.put("orderId", String.valueOf(med.getOrderId()));
			medMap.put("medOrderId", String.valueOf(med.getMedOrderId()));
			medMap.put("itemName", med.getDrugName());

			medList.add(medMap);
		}

		return medList;

	}

	public boolean assignOrders(String[] verificationIds, int assignTo, int assignedBy, int modifiedBy ){
		if(verificationIds == null || verificationIds.length<0){
			throw new InvalidParameterException("Invalid verificationIds");
		}
		if(assignTo<0){
			throw new InvalidParameterException("Invalid assignTo");
		}
		if(assignedBy<0){
			throw new InvalidParameterException("Invalid assignedBy");
		}
		boolean result = false;
		try{
			result = pharmacyVerificationDAO.assignOrders( verificationIds, assignTo, assignedBy, modifiedBy);
			if(result){
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("verificationIds", verificationIds);
				jsonObject.put("assignTo", assignTo);
				jsonObject.put("assignedBy", assignedBy);
				jsonObject.put("modifiedBy", modifiedBy);

				auditLogService.logEvent(assignedBy, PharmacyAuditLogsConstants.MODULE_PHARMACY_VERIFICATION, PharmacyAuditLogsConstants.ACTION_PVQ_ASSIGN_ORDER, jsonObject, "");
			}
		}catch(JSONException e){
			EcwLog.AppendExceptionToLog(e);
		}
		return result;
	}

	public PharmacyVerification readPharmacyVerificationById(int pharmacyVerificationId){
		
		if(pharmacyVerificationId <= 0){
			throw new InvalidParameterException("Invalid pharmacyVerificationId");
		}
		
		return pharmacyVerificationDAO.readPharmacyVerificationById(pharmacyVerificationId);
	}

	public boolean updatePharmacyVerification(PharmacyVerification pharmacyVerification){
		if(null == pharmacyVerification){
			throw new InvalidParameterException("Invalid pharmacyverification pojo");
		}
		boolean result = false;
		result = pharmacyVerificationDAO.updatePharmacyVerification(pharmacyVerification);
		if(result){
			auditLogService.logEvent(pharmacyVerification.getModifiedBy(), PharmacyAuditLogsConstants.MODULE_PHARMACY_VERIFICATION, PharmacyAuditLogsConstants.ACTION_PVQ_UPDATE_VERIFICATION, new JSONObject(pharmacyVerification), "");
		}
		return result;
	}

	public PharmacyStatus getPharmacyStatusByName(String statusName){
		return pharmacyVerificationDAO.getPharmacyStatusByName(statusName);
	}

	public boolean isInterventionsCompleted(int orderId){
		
		if(orderId <= 0){
			throw new InvalidParameterException("Invalid orderId");
		}
		
		String status = interventionReasonService.getInterventionStatusByOrderId(orderId);
		return ("Completed".equalsIgnoreCase(status) || "".equalsIgnoreCase(status));
	}

	@Override
	public boolean createPharmacyVerificationByOrderId(int orderId, int orderById) {
		boolean result = false;

		PharmacyVerification pharmacyVerification = new PharmacyVerification();
		pharmacyVerification.setOrderId(orderId);
		pharmacyVerification.setCreatedBy(orderById); 
		pharmacyVerification.setStatus(PharmacyConstants.PharmacyVerificationStatus.UNVERIFIED.getId());
		result = createPharmacyVerification(pharmacyVerification);	
		if(result){
			auditLogService.logEvent(pharmacyVerification.getCreatedBy(), PharmacyAuditLogsConstants.MODULE_PHARMACY_VERIFICATION, PharmacyAuditLogsConstants.ACTION_PVQ_CREATE_VERIFICATION, new JSONObject(pharmacyVerification), "");
		}
		return result;
	}
	
	@Override
	public boolean resetToUnverifiedByOrderId(int orderId, int orderById) {
		boolean result = false;

		PharmacyVerification pharmacyVerification = new PharmacyVerification();
		pharmacyVerification.setOrderId(orderId);
		pharmacyVerification.setModifiedBy(orderById); 		
		result = resetToPharmacyVerificationStatusByOrderId(pharmacyVerification);	
		if(result){
			auditLogService.logEvent(orderById, PharmacyAuditLogsConstants.MODULE_PHARMACY_VERIFICATION, PharmacyAuditLogsConstants.ACTION_PVQ_CREATE_VERIFICATION, new JSONObject(pharmacyVerification), "");
		}
		return result;
	}

	@Override
	public Map<String,Object> printLabelForMedDispense(Map<String, Object> requestMap) {
		return medDispenseProService.sendToMedDispensingCabinetOrPrintLabel(requestMap);

	}
	
	public boolean isPrintLabelRequired(int orderId){
		//Check if printLabel has to be called manually
		if(orderId<0){
			throw new InvalidParameterException("invalid orderId");
		}
		try {
			String currentTime = IPTzUtils.getCurrentUTCDateTime("HH:mm");

			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm") ;
			Date currentTimeDt = dateFormat.parse(currentTime);

			for(TemplateForBatch batch : batchesSetupService.getBatchDetailsByOrderId(orderId)){
				Date batchScheduleTime = dateFormat.parse(batch.getScheduleDailyTime().trim());
				if(currentTimeDt.before(batchScheduleTime)){
					return false;
				}
			}
		} catch (ParseException e) {
			EcwLog.AppendExceptionToLog(e);
		}
		
		return true;
	}
	
	public PharmacyVerification getPharmacyVerificationObj(MedOrder medOrder, int loggedInUserId, int pharmacyStatus, int reasonId) {

		String currentDateTime = IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT);

		PharmacyVerification pharmacyVerification = this.readPharmacyVerificationById(medOrder.getPharmacyVerificationId());
		pharmacyVerification.setStatus(pharmacyStatus);
		pharmacyVerification.setModifiedBy(loggedInUserId);
		pharmacyVerification.setModifiedOn(currentDateTime);
		pharmacyVerification.setGenerateBarcode(medOrder.isGenerateBarcode());
		pharmacyVerification.setDualVerification(medOrder.isDualVerification()); 

		if(pharmacyStatus == PharmacyConstants.PharmacyVerificationStatus.VERIFIED.getId()){ //VERIFY
			pharmacyVerification.setVerifiedBy(loggedInUserId);
			pharmacyVerification.setVerifiedOn(currentDateTime);

		} else if (pharmacyStatus == PharmacyConstants.PharmacyVerificationStatus.PENDING.getId()){ //MARK AS PENDING
			pharmacyVerification.setReasonId(reasonId);
		}

		if(medOrder.getAssignedToId() > 0){
			pharmacyVerification.setAssignedTo(medOrder.getAssignedToId());
			pharmacyVerification.setAssignedBy(loggedInUserId);
			pharmacyVerification.setAssignedOn(currentDateTime);
		}
		return pharmacyVerification;
	}
	
	public void savePharmacyBillingData(MedOrder medOrder, int loggedInUserId) {
		savePharmacyBillingData(medOrder, loggedInUserId, "");
	}
	
	public void savePharmacyBillingData(MedOrder medOrder, int loggedInUserId, String strMode) {

		PharmacyBillingData pharmacyBillingData = new PharmacyBillingData();
		pharmacyBillingData.setOrderId(medOrder.getMedOrderId());
		pharmacyBillingData.setUserId(loggedInUserId);
		pharmacyBillingData.setMasterEncounterId(medOrder.getEpisodeEncounterId());
		pharmacyBillingData.setDrugNdcDetails(new ArrayList<DrugNdcDetails>());
		pharmacyBillingData.setOperationModule(BillingDataTransaction.OPERATIONMODULE.PHARMACY);
		pharmacyBillingData.setOperation(BillingDataTransaction.OPERATION.ADD);
		if("chargeCapture".equals(strMode)) {
			pharmacyBillingData.setSubModule(PharmacyLogEnum.SubModule.PWQ_MANUAL_CHARGE);
		} else {
			pharmacyBillingData.setSubModule(PharmacyLogEnum.SubModule.PWQ_VERIFY);
		}			
		pharmacyBillingData.setBillingDate(IPTzUtils.getUTCNow());
		pharmacyBillingData.setEncounterDateTime(IPTzUtils.getUTCNow());
	
		//Save Billing Data - Post charges only if billable = true and ( drug type = bulk or  ischargeableAtdispense = true )
		if(!medOrder.isDiscontinued() && medOrder.isBillable() && (medOrder.isChargeAtDispense() || "chargeCapture".equals(strMode))){	
			pharmacyBillingService.saveBillingData(pharmacyBillingData);
		}else  if(!medOrder.isChargeAtDispense() && !medOrder.isDiscontinued() && medOrder.isBillable()){
			pharmacyBillingService.logPharmacyBillingData(null, pharmacyBillingData,PharmacyLogEnum.OPERATION.LOG.name(), PharmacyLogEnum.LogMessage.PWQ_LOG.getValue());
		}
	}
	
	@Override
	public Map<String, Object> getOrderLabelDataPVQ(Map<String, Object> requestMap){
		if(requestMap == null || (requestMap.isEmpty())){
			throw new InvalidParameterException("Invalid Request map");
		}
		return medDispenseProService.getOrderLabelDataPVQ(requestMap);
	}
	
	@Override
	public int checkPharmacyPrinterSetting(int facilityId,int orderType) {
		return  pharmacyPrinterSettingsService.checkPharmacyPrinterSetting(facilityId, orderType,PharmacyLabelConstant.PrintSourceType.PHARMACY_WORK_QUEUE);
	}
	
}