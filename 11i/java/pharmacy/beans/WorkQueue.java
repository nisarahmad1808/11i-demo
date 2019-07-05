package inpatientWeb.pharmacy.beans;

import inpatientWeb.HomeMedication.dao.DrugNameDTO;
import inpatientWeb.admin.pharmacySettings.configureScale.utility.ConfigureScaleTemplate;

/**
 * @author shwetap
 *
 */
public class WorkQueue {
	
	
	public static final int ORDER_TYPE_MED = 1;
	public static final int ORDER_TYPE_IV = 2;
	public static final int ORDER_TYPE_COMPLEX_IV = 3;
	
	public static final String ORDER_TYPE_MED_STR = "Medication";
	public static final String ORDER_TYPE_IV_STR = "IV";
	public static final String ORDER_TYPE_COMPLEX_IV_STR = "Complex";
	
	private int id;
	private int orderId;
	private int medOrderId;
	
	private int assignedTo;
	private String assignedToName;
	private int assignedBy;
	private int pharmacyStatusId;
	private String pharmacyStatus;
	private int modifiedById;
	private String modifiedByName;
	private String modifiedOn;
	private String startDateTime;
	private String endDateTime;
	private int patientId;
	private int encounterId;
	private int encounterType;
	private int episodeEncounterId;
	private String patientName;
	private String patientGender;
	private String patientAge;
	private String patientMRN;
	private String itemName;
	private String orderDetail;
	private int orderStatusId;
	private String orderStatusName;
	private int orderSetId;
	private String orderSetName;
	private String tag;
	private String chiefComplaint;
	private int orderingProviderId;
	private String orderingProviderName;
	private String orderDateTime;
	private int nurseId;
	private String NurseName;
	private int priorityId;
	private String priorityName;
	private int departmentId;
	private String departmentName;
	private int facilityId;
	private String facilityName;
	private int areaId;
	private String areaName;
	private int bedId;
	private String bedName;
	private int unitId;
	private String unitName;
	private String message;
	private int messageSentBy;
	private String messageSentByName;
	private String messageSentOn;
	private int orderType;
	private String admittingDiagnosis;
	private String pendingReason;
	private boolean isInterventionPending;
	private String interventionStatus;
	private boolean isPOM;
	private boolean isISSFlag;
	private boolean isConfiguredISSFlag;
	private ConfigureScaleTemplate issTemplateData = null;
	private int serviceType;
	private int verifiedById;
	private String verifiedByName;
	private String verifiedOn;
	private boolean isPatientSuppliedFlag;
	private boolean isSubstituteFlag;
	private int workFlowType;
	private DrugNameDTO itemsObj = null;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public int getMedOrderId() {
		return medOrderId;
	}
	public void setMedOrderId(int medOrderId) {
		this.medOrderId = medOrderId;
	}
	public int getAssignedTo() {
		return assignedTo;
	}
	public void setAssignedTo(int assignedTo) {
		this.assignedTo = assignedTo;
	}
	public String getAssignedToName() {
		return assignedToName;
	}
	public void setAssignedToName(String assignedToName) {
		this.assignedToName = assignedToName;
	}
	public int getAssignedBy() {
		return assignedBy;
	}
	public void setAssignedBy(int assignedBy) {
		this.assignedBy = assignedBy;
	}
	public int getPharmacyStatusId() {
		return pharmacyStatusId;
	}
	public void setPharmacyStatusId(int pharmacyStatusId) {
		this.pharmacyStatusId = pharmacyStatusId;
	}
	public String getPharmacyStatus() {
		return pharmacyStatus;
	}
	public void setPharmacyStatus(String pharmacyStatus) {
		this.pharmacyStatus = pharmacyStatus;
	}
	public int getModifiedById() {
		return modifiedById;
	}
	public void setModifiedById(int modifiedById) {
		this.modifiedById = modifiedById;
	}
	public String getModifiedByName() {
		return modifiedByName;
	}
	public void setModifiedByName(String modifiedByName) {
		this.modifiedByName = modifiedByName;
	}
	public String getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	public String getStartDateTime() {
		return startDateTime;
	}
	public void setStartDateTime(String startDateTime) {
		this.startDateTime = startDateTime;
	}
	public String getEndDateTime() {
		return endDateTime;
	}
	public void setEndDateTime(String endDateTime) {
		this.endDateTime = endDateTime;
	}
	public int getPatientId() {
		return patientId;
	}
	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}
	public int getEncounterId() {
		return encounterId;
	}
	public void setEncounterId(int encounterId) {
		this.encounterId = encounterId;
	}
	public int getEncounterType() {
		return encounterType;
	}
	public void setEncounterType(int encounterType) {
		this.encounterType = encounterType;
	}
	public int getEpisodeEncounterId() {
		return episodeEncounterId;
	}
	public void setEpisodeEncounterId(int episodeEncounterId) {
		this.episodeEncounterId = episodeEncounterId;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public String getPatientGender() {
		return patientGender;
	}
	public void setPatientGender(String patientGender) {
		this.patientGender = patientGender;
	}
	public String getPatientAge() {
		return patientAge;
	}
	public void setPatientAge(String patientAge) {
		this.patientAge = patientAge;
	}
	public String getPatientMRN() {
		return patientMRN;
	}
	public void setPatientMRN(String patientMRN) {
		this.patientMRN = patientMRN;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getOrderDetail() {
		return orderDetail;
	}
	public void setOrderDetail(String orderDetail) {
		this.orderDetail = orderDetail;
	}
	public int getOrderStatusId() {
		return orderStatusId;
	}
	public void setOrderStatusId(int orderStatusId) {
		this.orderStatusId = orderStatusId;
	}
	public String getOrderStatusName() {
		return orderStatusName;
	}
	public void setOrderStatusName(String orderStatusName) {
		this.orderStatusName = orderStatusName;
	}
	public int getOrderSetId() {
		return orderSetId;
	}
	public void setOrderSetId(int orderSetId) {
		this.orderSetId = orderSetId;
	}
	public String getOrderSetName() {
		return orderSetName;
	}
	public void setOrderSetName(String orderSetName) {
		this.orderSetName = orderSetName;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getChiefComplaint() {
		return chiefComplaint;
	}
	public void setChiefComplaint(String chiefComplaint) {
		this.chiefComplaint = chiefComplaint;
	}
	public int getOrderingProviderId() {
		return orderingProviderId;
	}
	public void setOrderingProviderId(int orderingProviderId) {
		this.orderingProviderId = orderingProviderId;
	}
	public String getOrderingProviderName() {
		return orderingProviderName;
	}
	public void setOrderingProviderName(String orderingProviderName) {
		this.orderingProviderName = orderingProviderName;
	}
	public String getOrderDateTime() {
		return orderDateTime;
	}
	public void setOrderDateTime(String orderDateTime) {
		this.orderDateTime = orderDateTime;
	}
	public int getNurseId() {
		return nurseId;
	}
	public void setNurseId(int nurseId) {
		this.nurseId = nurseId;
	}
	public String getNurseName() {
		return NurseName;
	}
	public void setNurseName(String nurseName) {
		NurseName = nurseName;
	}
	public int getPriorityId() {
		return priorityId;
	}
	public void setPriorityId(int priorityId) {
		this.priorityId = priorityId;
	}
	public String getPriorityName() {
		return priorityName;
	}
	public void setPriorityName(String priorityName) {
		this.priorityName = priorityName;
	}
	public int getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(int departmentId) {
		this.departmentId = departmentId;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public int getFacilityId() {
		return facilityId;
	}
	public void setFacilityId(int facilityId) {
		this.facilityId = facilityId;
	}
	public String getFacilityName() {
		return facilityName;
	}
	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}
	public int getAreaId() {
		return areaId;
	}
	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public int getBedId() {
		return bedId;
	}
	public void setBedId(int bedId) {
		this.bedId = bedId;
	}
	public String getBedName() {
		return bedName;
	}
	public void setBedName(String bedName) {
		this.bedName = bedName;
	}
	public int getUnitId() {
		return unitId;
	}
	public void setUnitId(int unitId) {
		this.unitId = unitId;
	}
	public String getUnitName() {
		return unitName;
	}
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getMessageSentBy() {
		return messageSentBy;
	}
	public void setMessageSentBy(int messageSentBy) {
		this.messageSentBy = messageSentBy;
	}
	public String getMessageSentByName() {
		return messageSentByName;
	}
	public void setMessageSentByName(String messageSentByName) {
		this.messageSentByName = messageSentByName;
	}
	public String getMessageSentOn() {
		return messageSentOn;
	}
	public void setMessageSentOn(String messageSentOn) {
		this.messageSentOn = messageSentOn;
	}
	public int getOrderType() {
		return orderType;
	}
	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}
	public String getAdmittingDiagnosis() {
		return admittingDiagnosis;
	}
	public void setAdmittingDiagnosis(String admittingDiagnosis) {
		this.admittingDiagnosis = admittingDiagnosis;
	}
	public String getPendingReason() {
		return pendingReason;
	}
	public void setPendingReason(String pendingReason) {
		this.pendingReason = pendingReason;
	}
	public boolean isInterventionPending() {
		return isInterventionPending;
	}
	public void setInterventionPending(boolean isInterventionPending) {
		this.isInterventionPending = isInterventionPending;
	}
	public String getInterventionStatus() {
		return interventionStatus;
	}
	public void setInterventionStatus(String interventionStatus) {
		this.interventionStatus = interventionStatus;
	}
	public boolean isPOM() {
		return isPOM;
	}
	public void setPOM(boolean isPOM) {
		this.isPOM = isPOM;
	}
	public int getServiceType() {
		return serviceType;
	}
	public void setServiceType(int serviceType) {
		this.serviceType = serviceType;
	}
	public int getVerifiedById() {
		return verifiedById;
	}
	public void setVerifiedById(int verifiedById) {
		this.verifiedById = verifiedById;
	}
	public String getVerifiedByName() {
		return verifiedByName;
	}
	public void setVerifiedByName(String verifiedByName) {
		this.verifiedByName = verifiedByName;
	}
	public String getVerifiedOn() {
		return verifiedOn;
	}
	public void setVerifiedOn(String verifiedOn) {
		this.verifiedOn = verifiedOn;
	}
	public boolean isISSFlag() {
		return isISSFlag;
	}
	public void setISSFlag(boolean isISSFlag) {
		this.isISSFlag = isISSFlag;
	}
	public boolean isConfiguredISSFlag() {
		return isConfiguredISSFlag;
	}
	public void setConfiguredISSFlag(boolean isConfiguredISSFlag) {
		this.isConfiguredISSFlag = isConfiguredISSFlag;
	}
	public ConfigureScaleTemplate getIssTemplateData() {
		return issTemplateData;
	}
	public void setIssTemplateData(ConfigureScaleTemplate issTemplateData) {
		this.issTemplateData = issTemplateData;
	}	
	public boolean isPatientSuppliedFlag() {
		return isPatientSuppliedFlag;
	}
	public void setPatientSuppliedFlag(boolean isPatientSuppliedFlag) {
		this.isPatientSuppliedFlag = isPatientSuppliedFlag;
	}
	public boolean isSubstituteFlag() {
		return isSubstituteFlag;
	}
	public void setSubstituteFlag(boolean isSubstituteFlag) {
		this.isSubstituteFlag = isSubstituteFlag;
	}
	public DrugNameDTO getItemsObj() {
		return itemsObj;
	}
	public void setItemsObj(DrugNameDTO itemsObj) {
		this.itemsObj = itemsObj;
	}
	public int getWorkFlowType() {
		return workFlowType;
	}
	public void setWorkFlowType(int workFlowType) {
		this.workFlowType = workFlowType;
	}
	
	
}
