package inpatientWeb.pharmacy.beans;

import java.util.List;

import inpatientWeb.admin.pharmacySettings.configureDictionary.dao.TemplateForTimeSchedule;
import inpatientWeb.admin.pharmacySettings.configureScale.utility.ConfigureScaleTemplate;
import inpatientWeb.eMAR.modal.EMARDrugAdministrationSchedule;

public class MedOrder {
	
	private int patientId;
	private int orderId;
	private int stagingOrderId;
	private int medOrderId;
	private int itemId;
	private int routedGenericItemId;
	private int pharmacyVerificationId;
	private int pharmacyStatus;
	private int orderEntryMethod;
	private String orderEntryMethodName;
	private int orderingProviderId;
	private String orderingProvider;
	private String orderDateTime;
	private int documentedById;
	private String documentedByName;
	private String documentedDateTime;
	private String orderType;
	private int orderTypeInt;
	
	private String rxNumber;

	private int priorityId;
	private String priorityName;
	private String drugName;
	private String brandName;
	private int brandItemId;

	private String dose;
	private String doseUnit;

	private String route;
	private int frequencyId;
	private String frequency;

	private int assignedToId;
	private String assignedToName;

	private boolean isSubstitute;
	private String pharmacyLocation;
	private boolean isOwnMed;

	private boolean generateBarcode;
	private String generateBarcodeDisp;
	private boolean isPRN;
	private String prnIndication;
	private String indication;
	private boolean includeNow;
	private String duration;
	private String durationUnit;
	private String startDateTime;
	private String stopDateTime;
	private String eMARDetails;

	private String lastTaken;
	private String orderEntryInstruction;
	private String rphInstruction;
	private String emarInstruction;
	private String internalNotes;
	
	private boolean dualVerification;
	private boolean drugTypeBulk;
	private boolean chargeAtDispense;
	private boolean billable;
	private boolean PCA;
	private boolean floorStock;
	private boolean drugDispenseCode;
	
	private String interventionStatus;
	
	private double totalPrice;
	
	private String ivRate;
	private String ivRateUOM;
	
	private String ivTitration;
	
	private int orderStatus;
	private String orderStatusName;
	
	private int facilityId;
	
	private String totalVolume;
	
	private	boolean isRequestToRenew;

	private List<MedOrderDetail> medOrderDetailList;
	
	private List<MedOrderDetail> availableProducts;
	
	private List<MedOrderDetail> availableDiluents;
	

	private List<EMARDrugAdministrationSchedule> emarSchedule;
	
	private List<TemplateForTimeSchedule> frequencySchedule;
	
	private boolean continuousFrequency;
	
	private int episodeEncounterId;
	private String episodeType;
	private int encounterId;
	private int encounterType;
	private int encPracticeId;
	private int encPatientType;
	private int encServiceType;
	private int encDeptId;
	private int encUnitId;
	private int encAreaId;
	private int encBedId;
	
	private boolean discontinued;
	
	private boolean titrationAllowed;
	
	private boolean isISSFlag;
	private boolean isConfiguredISSFlag;
	private ConfigureScaleTemplate issTemplateData = null;
	
	private int verifiedBy;
	private String verifiedByName;
	private String verifiedDateTime;

    private double toTalHcpcsUnit;	
	private double toTalHcpcsUnitCost;	
	private double costCalculatedWithChargeType;

	
	public int getEncAreaId() {
		return encAreaId;
	}

	public void setEncAreaId(int encAreaId) {
		this.encAreaId = encAreaId;
	}

	public int getEncBedId() {
		return encBedId;
	}

	public void setEncBedId(int encBedId) {
		this.encBedId = encBedId;
	}

	public boolean isDrugDispenseCode() {
		return drugDispenseCode;
	}

	public void setDrugDispenseCode(boolean drugDispenseCode) {
		this.drugDispenseCode = drugDispenseCode;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	
	public int getStagingOrderId() {
		return stagingOrderId;
	}

	public void setStagingOrderId(int stagingOrderId) {
		this.stagingOrderId = stagingOrderId;
	}

	public int getMedOrderId() {
		return medOrderId;
	}

	public void setMedOrderId(int medOrderId) {
		this.medOrderId = medOrderId;
	}
	
	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getRoutedGenericItemId() {
		return routedGenericItemId;
	}

	public void setRoutedGenericItemId(int routedGenericItemId) {
		this.routedGenericItemId = routedGenericItemId;
	}

	public int getEncounterId() {
		return encounterId;
	}

	public void setEncounterId(int encounterId) {
		this.encounterId = encounterId;
	}

	public int getEpisodeEncounterId() {
		return episodeEncounterId;
	}

	public void setEpisodeEncounterId(int episodeEncounterId) {
		this.episodeEncounterId = episodeEncounterId;
	}

	public int getPharmacyVerificationId() {
		return pharmacyVerificationId;
	}

	public void setPharmacyVerificationId(int pharmacyVerificationId) {
		this.pharmacyVerificationId = pharmacyVerificationId;
	}

	public int getPharmacyStatus() {
		return pharmacyStatus;
	}

	public void setPharmacyStatus(int pharmacyStatus) {
		this.pharmacyStatus = pharmacyStatus;
	}

	public int getOrderEntryMethod() {
		return orderEntryMethod;
	}

	public void setOrderEntryMethod(int orderEntryMethod) {
		this.orderEntryMethod = orderEntryMethod;
	}

	public String getOrderEntryMethodName() {
		return orderEntryMethodName;
	}

	public void setOrderEntryMethodName(String orderEntryMethodName) {
		this.orderEntryMethodName = orderEntryMethodName;
	}

	public int getOrderingProviderId() {
		return orderingProviderId;
	}

	public void setOrderingProviderId(int orderingProviderId) {
		this.orderingProviderId = orderingProviderId;
	}

	public String getOrderingProvider() {
		return orderingProvider;
	}

	public void setOrderingProvider(String orderingProvider) {
		this.orderingProvider = orderingProvider;
	}

	public String getOrderDateTime() {
		return orderDateTime;
	}

	public void setOrderDateTime(String orderDateTime) {
		this.orderDateTime = orderDateTime;
	}

	public int getDocumentedById() {
		return documentedById;
	}

	public void setDocumentedById(int documentedById) {
		this.documentedById = documentedById;
	}

	public String getDocumentedByName() {
		return documentedByName;
	}

	public void setDocumentedByName(String documentedByName) {
		this.documentedByName = documentedByName;
	}

	public String getDocumentedDateTime() {
		return documentedDateTime;
	}

	public void setDocumentedDateTime(String documentedDateTime) {
		this.documentedDateTime = documentedDateTime;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	
	public int getOrderTypeInt() {
		return orderTypeInt;
	}

	public void setOrderTypeInt(int orderTypeInt) {
		this.orderTypeInt = orderTypeInt;
	}

	public String getRxNumber() {
		return rxNumber;
	}

	public void setRxNumber(String rxNumber) {
		this.rxNumber = rxNumber;
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

	public String getDrugName() {
		return drugName;
	}

	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public int getBrandItemId() {
		return brandItemId;
	}

	public void setBrandItemId(int brandItemId) {
		this.brandItemId = brandItemId;
	}

	public String getDose() {
		return dose;
	}

	public void setDose(String dose) {
		this.dose = dose;
	}

	public String getDoseUnit() {
		return doseUnit;
	}

	public void setDoseUnit(String doseUnit) {
		this.doseUnit = doseUnit;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}
	
	public int getFrequencyId() {
		return frequencyId;
	}

	public void setFrequencyId(int frequencyId) {
		this.frequencyId = frequencyId;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public int getAssignedToId() {
		return assignedToId;
	}

	public void setAssignedToId(int assignedToId) {
		this.assignedToId = assignedToId;
	}

	public String getAssignedToName() {
		return assignedToName;
	}

	public void setAssignedToName(String assignedToName) {
		this.assignedToName = assignedToName;
	}

	public boolean isSubstitute() {
		return isSubstitute;
	}

	public void setSubstitute(boolean isSubstitute) {
		this.isSubstitute = isSubstitute;
	}

	public String getPharmacyLocation() {
		return pharmacyLocation;
	}

	public void setPharmacyLocation(String pharmacyLocation) {
		this.pharmacyLocation = pharmacyLocation;
	}

	public boolean isOwnMed() {
		return isOwnMed;
	}

	public void setOwnMed(boolean isOwnMed) {
		this.isOwnMed = isOwnMed;
	}

	public boolean isGenerateBarcode() {
		return generateBarcode;
	}

	public void setGenerateBarcode(boolean generateBarcode) {
		this.generateBarcode = generateBarcode;
	}

	public String getGenerateBarcodeDisp() {
		return generateBarcodeDisp;
	}

	public void setGenerateBarcodeDisp(String generateBarcodeDisp) {
		this.generateBarcodeDisp = generateBarcodeDisp;
	}

	public boolean isPRN() {
		return isPRN;
	}

	public void setPRN(boolean isPRN) {
		this.isPRN = isPRN;
	}

	public String getPrnIndication() {
		return prnIndication;
	}

	public void setPrnIndication(String prnIndication) {
		this.prnIndication = prnIndication;
	}

	public String getIndication() {
		return indication;
	}

	public void setIndication(String indication) {
		this.indication = indication;
	}

	public boolean isIncludeNow() {
		return includeNow;
	}

	public void setIncludeNow(boolean includeNow) {
		this.includeNow = includeNow;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getDurationUnit() {
		return durationUnit;
	}

	public void setDurationUnit(String durationUnit) {
		this.durationUnit = durationUnit;
	}

	public String getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(String startDateTime) {
		this.startDateTime = startDateTime;
	}

	public String getStopDateTime() {
		return stopDateTime;
	}

	public void setStopDateTime(String stopDateTime) {
		this.stopDateTime = stopDateTime;
	}

	public String geteMARDetails() {
		return eMARDetails;
	}

	public void seteMARDetails(String eMARDetails) {
		this.eMARDetails = eMARDetails;
	}

	public String getLastTaken() {
		return lastTaken;
	}

	public void setLastTaken(String lastTaken) {
		this.lastTaken = lastTaken;
	}

	public String getOrderEntryInstruction() {
		return orderEntryInstruction;
	}

	public void setOrderEntryInstruction(String orderEntryInstruction) {
		this.orderEntryInstruction = orderEntryInstruction;
	}

	public String getRphInstruction() {
		return rphInstruction;
	}

	public void setRphInstruction(String rphInstruction) {
		this.rphInstruction = rphInstruction;
	}

	public String getEmarInstruction() {
		return emarInstruction;
	}

	public void setEmarInstruction(String emarInstruction) {
		this.emarInstruction = emarInstruction;
	}

	public String getInternalNotes() {
		return internalNotes;
	}

	public void setInternalNotes(String internalNotes) {
		this.internalNotes = internalNotes;
	}

	public boolean isDualVerification() {
		return dualVerification;
	}

	public void setDualVerification(boolean dualVerification) {
		this.dualVerification = dualVerification;
	}

	public boolean isDrugTypeBulk() {
		return drugTypeBulk;
	}

	public void setDrugTypeBulk(boolean drugTypeBulk) {
		this.drugTypeBulk = drugTypeBulk;
	}

	public boolean isBillable() {
		return billable;
	}

	public void setBillable(boolean billable) {
		this.billable = billable;
	}

	public List<MedOrderDetail> getMedOrderDetailList() {
		return medOrderDetailList;
	}

	public void setMedOrderDetailList(List<MedOrderDetail> medOrderDetailList) {
		this.medOrderDetailList = medOrderDetailList;
	}

	public List<MedOrderDetail> getAvailableProducts() {
		return availableProducts;
	}

	public void setAvailableProducts(List<MedOrderDetail> availableProducts) {
		this.availableProducts = availableProducts;
	}

	public String getInterventionStatus() {
		return interventionStatus;
	}

	public void setInterventionStatus(String interventionStatus) {
		this.interventionStatus = interventionStatus;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	public String getIvRate() {
		return ivRate;
	}

	public void setIvRate(String ivRate) {
		this.ivRate = ivRate;
	}

	public String getIvRateUOM() {
		return ivRateUOM;
	}

	public void setIvRateUOM(String ivRateUOM) {
		this.ivRateUOM = ivRateUOM;
	}

	public boolean isPCA() {
		return PCA;
	}
	
	public void setPCA(boolean pCA) {
		PCA = pCA;
	}
	
	public boolean isFloorStock() {
		return floorStock;
	}

	public void setFloorStock(boolean floorStock) {
		this.floorStock = floorStock;
	}

	public List<EMARDrugAdministrationSchedule> getEmarSchedule() {
		return emarSchedule;
	}

	public void setEmarSchedule(List<EMARDrugAdministrationSchedule> emarSchedule) {
		this.emarSchedule = emarSchedule;
	}

	public String getOrderStatusName() {
		return orderStatusName;
	}

	public void setOrderStatusName(String orderStatusName) {
		this.orderStatusName = orderStatusName;
	}

	public List<TemplateForTimeSchedule> getFrequencySchedule() {
		return frequencySchedule;
	}

	public void setFrequencySchedule(List<TemplateForTimeSchedule> frequencySchedule) {
		this.frequencySchedule = frequencySchedule;
	}

	public int getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(int facilityId) {
		this.facilityId = facilityId;
	}

	public String getTotalVolume() {
		return totalVolume;
	}

	public void setTotalVolume(String totalVolume) {
		this.totalVolume = totalVolume;
	}

	public boolean isRequestToRenew() {
		return isRequestToRenew;
	}

	public void setRequestToRenew(boolean isRequestToRenew) {
		this.isRequestToRenew = isRequestToRenew;
	}

	public String getIvTitration() {
		return ivTitration;
	}

	public void setIvTitration(String ivTitration) {
		this.ivTitration = ivTitration;
	}

	public String getEpisodeType() {
		return episodeType;
	}

	public void setEpisodeType(String episodeType) {
		this.episodeType = episodeType;
	}

	public int getEncPracticeId() {
		return encPracticeId;
	}

	public void setEncPracticeId(int encPracticeId) {
		this.encPracticeId = encPracticeId;
	}

	public int getEncPatientType() {
		return encPatientType;
	}

	public void setEncPatientType(int encPatientType) {
		this.encPatientType = encPatientType;
	}

	public int getEncServiceType() {
		return encServiceType;
	}

	public void setEncServiceType(int encServiceType) {
		this.encServiceType = encServiceType;
	}

	public int getEncDeptId() {
		return encDeptId;
	}

	public void setEncDeptId(int encDeptId) {
		this.encDeptId = encDeptId;
	}

	public int getEncUnitId() {
		return encUnitId;
	}

	public void setEncUnitId(int encUnitId) {
		this.encUnitId = encUnitId;
	}

	public int getEncounterType() {
		return encounterType;
	}

	public void setEncounterType(int encounterType) {
		this.encounterType = encounterType;
	}

	public int getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(int orderStatus) {
		this.orderStatus = orderStatus;
	}

	public boolean isDiscontinued() {
		return discontinued;
	}

	public void setDiscontinued(boolean isDiscontinued) {
		this.discontinued = isDiscontinued;
	}

	public boolean isTitrationAllowed() {
		return titrationAllowed;
	}

	public void setTitrationAllowed(boolean titrationAllowed) {
		this.titrationAllowed = titrationAllowed;
	}
	
	public List<MedOrderDetail> getAvailableDiluents() {
		return availableDiluents;
	}

	public void setAvailableDiluents(List<MedOrderDetail> availableDiluents) {
		this.availableDiluents = availableDiluents;
	}

	public boolean isChargeAtDispense() {
		return chargeAtDispense;
	}

	public void setChargeAtDispense(boolean chargeAtDispense) {
		this.chargeAtDispense = chargeAtDispense;
	}

	public boolean isContinuousFrequency() {
		return continuousFrequency;
	}

	public void setContinuousFrequency(boolean continuousFrequency) {
		this.continuousFrequency = continuousFrequency;
	}

	public int getPatientId() {
		return patientId;
	}

	public void setPatientId(int patientId) {
		this.patientId = patientId;
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

	public int getVerifiedBy() {
		return verifiedBy;
	}

	public void setVerifiedBy(int verifiedBy) {
		this.verifiedBy = verifiedBy;
	}

	public String getVerifiedByName() {
		return verifiedByName;
	}

	public void setVerifiedByName(String verifiedByName) {
		this.verifiedByName = verifiedByName;
	}

	public String getVerifiedDateTime() {
		return verifiedDateTime;
	}

	public void setVerifiedDateTime(String verifiedDateTime) {
		this.verifiedDateTime = verifiedDateTime;
	}

	public double getToTalHcpcsUnit() {
		return toTalHcpcsUnit;
	}

	public void setToTalHcpcsUnit(double toTalHcpcsUnit) {
		this.toTalHcpcsUnit = toTalHcpcsUnit;
	}

	public double getToTalHcpcsUnitCost() {
		return toTalHcpcsUnitCost;
	}

	public void setToTalHcpcsUnitCost(double toTalHcpcsUnitCost) {
		this.toTalHcpcsUnitCost = toTalHcpcsUnitCost;
	}

	public double getCostCalculatedWithChargeType() {
		return costCalculatedWithChargeType;
	}

	public void setCostCalculatedWithChargeType(double costCalculatedWithChargeType) {
		this.costCalculatedWithChargeType = costCalculatedWithChargeType;
	}
	
	
}
