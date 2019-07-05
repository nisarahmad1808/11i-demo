package inpatientWeb.pharmacy.beans;

import inpatientWeb.admin.pharmacySettings.pharmacyUtility.model.PriceRuleParam;

public class MedOrderDetail {
	
	private int medOrderDetailId;
	private int medOrderId;
	private int ipItemId;
	private int routedGenericItemId;
	private String itemName;
	private String genericName;
	private String orderStrength;
	private String orderFormulation;

	private String orderNDCCode;
	private int orderDDID;
	private int drugFormularyId;
	private String expiresOn;
	private String expiresOnUOM;
	private String dispense;
	private String dispenseQty;
	private String dispenseUOM;
	private String actualDispense;
	
	private String ivVolume;
	private String ivVolumeUnit;
	private String isRefrigeration;
	private boolean floorStock;
	private boolean nonBillable;
	
	private int ivDiluentId;
	private String price;
	
	private double awup;
	private int chargeTypeId;
	private double totalCostCalculatedWithChargeType;
	private double unitCostCalculatedWithChargeType;
	private double costCalculatedWithChargeType;
	
	private boolean PCA;
	private boolean slidingScale;
	private String slidingScaleValue;
	private String bolusLoadingDose;
	private String bolusLoadingDoseUOM;
	private String intermittenDose;
	private String intermittenDoseUOM;
	private String lockoutIntervalDose;
	private String lockoutIntervalDoseUOM;
	private String fourHourLimit;
	private String fourHourLimitUOM;
	private String continuous;
	private String continuousUOM;
	private String chargeCode;
	private String brandName;
	
	private String formularyStrength;
	private String formularyDoseSize;
	private String formularyDoseUOM;
	private String formularyDispenseSize;
	private String formularyDispenseUOM;
	private String formularyHCPCSCodeRange;
	private String formularyHCPCSCodeType;
	private String formularyHCPCSCodeUnit;
	private double toTalHCPCSUnit;
	private String drugDispenseCode;
	private boolean isDrugTypeBulk;
	private String orderDispense;
	private String orderDose;
	
	private boolean isPrimary;
	
	private String eMARInstructions;
	private String internalNotes;
	
	private boolean ivDiluent;
	private boolean additive;
	
	private int formularyItemId;
	private int displayIndex;
	
	private boolean calculate;
	private String upcCode;
	private int drugNameId;
	private PriceRuleParam priceRuleParam;
	
	private String rxNorm;
	
	
	private boolean isISSFlag;
	private boolean isConfigureISSFlag;
	private String minDose;
	private String maxDose;
	private String packSizeUnitcode;
	private String packSize;
	
	public String getOrderDispense() {
		return orderDispense;
	}
	public void setOrderDispense(String orderDispense) {
		this.orderDispense = orderDispense;
	}
	public String getExpiresOn() {
		return expiresOn;
	}
	public void setExpiresOn(String expiresOn) {
		this.expiresOn = expiresOn;
	}
	public String getExpiresOnUOM() {
		return expiresOnUOM;
	}
	public void setExpiresOnUOM(String expiresOnUOM) {
		this.expiresOnUOM = expiresOnUOM;
	}
	public String getIsRefrigeration() {
		return isRefrigeration;
	}
	public void setIsRefrigeration(String isRefrigeration) {
		this.isRefrigeration = isRefrigeration;
	}

	public String getDrugDispenseCode() {
		return drugDispenseCode;
	}
	public void setDrugDispenseCode(String drugDispenseCode) {
		this.drugDispenseCode = drugDispenseCode;
	}
	public boolean isDrugTypeBulk() {
		return isDrugTypeBulk;
	}
	public void setDrugTypeBulk(boolean isDrugTypeBulk) {
		this.isDrugTypeBulk = isDrugTypeBulk;
	}
	public boolean isPrimary() {
		return isPrimary;
	}
	public void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}
	public int getMedOrderDetailId() {
		return medOrderDetailId;
	}
	public void setMedOrderDetailId(int medOrderDetailId) {
		this.medOrderDetailId = medOrderDetailId;
	}
	public int getMedOrderId() {
		return medOrderId;
	}
	public void setMedOrderId(int medOrderId) {
		this.medOrderId = medOrderId;
	}
	public int getIpItemId() {
		return ipItemId;
	}
	public void setIpItemId(int ipItemId) {
		this.ipItemId = ipItemId;
	}
	
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getOrderStrength() {
		return orderStrength;
	}
	public void setOrderStrength(String orderStrength) {
		this.orderStrength = orderStrength;
	}
	public String getOrderFormulation() {
		return orderFormulation;
	}
	public void setOrderFormulation(String orderFormulation) {
		this.orderFormulation = orderFormulation;
	}
	public String getOrderNDCCode() {
		return orderNDCCode;
	}
	public void setOrderNDCCode(String orderNDCCode) {
		this.orderNDCCode = orderNDCCode;
	}
	public int getOrderDDID() {
		return orderDDID;
	}
	public void setOrderDDID(int orderDDID) {
		this.orderDDID = orderDDID;
	}
	public int getDrugFormularyId() {
		return drugFormularyId;
	}
	public void setDrugFormularyId(int drugFormularyId) {
		this.drugFormularyId = drugFormularyId;
	}
	public boolean isFloorStock() {
		return floorStock;
	}
	public void setFloorStock(boolean floorStock) {
		this.floorStock = floorStock;
	}
	public boolean isNonBillable() {
		return nonBillable;
	}
	public void setNonBillable(boolean nonBillable) {
		this.nonBillable = nonBillable;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public double getAwup() {
		return awup;
	}
	public void setAwup(double awup) {
		this.awup = awup;
	}
	public int getChargeTypeId() {
		return chargeTypeId;
	}
	public void setChargeTypeId(int chargeTypeId) {
		this.chargeTypeId = chargeTypeId;
	}
	public boolean isPCA() {
		return PCA;
	}
	public void setPCA(boolean pCA) {
		PCA = pCA;
	}
	public boolean isSlidingScale() {
		return slidingScale;
	}
	public void setSlidingScale(boolean slidingScale) {
		this.slidingScale = slidingScale;
	}
	public String getSlidingScaleValue() {
		return slidingScaleValue;
	}
	public void setSlidingScaleValue(String slidingScaleValue) {
		this.slidingScaleValue = slidingScaleValue;
	}
	public String getBolusLoadingDose() {
		return bolusLoadingDose;
	}
	public void setBolusLoadingDose(String bolusLoadingDose) {
		this.bolusLoadingDose = bolusLoadingDose;
	}
	public String getBolusLoadingDoseUOM() {
		return bolusLoadingDoseUOM;
	}
	public void setBolusLoadingDoseUOM(String bolusLoadingDoseUOM) {
		this.bolusLoadingDoseUOM = bolusLoadingDoseUOM;
	}
	public String getIntermittenDose() {
		return intermittenDose;
	}
	public void setIntermittenDose(String intermittenDose) {
		this.intermittenDose = intermittenDose;
	}
	public String getIntermittenDoseUOM() {
		return intermittenDoseUOM;
	}
	public void setIntermittenDoseUOM(String intermittenDoseUOM) {
		this.intermittenDoseUOM = intermittenDoseUOM;
	}
	public String getLockoutIntervalDose() {
		return lockoutIntervalDose;
	}
	public void setLockoutIntervalDose(String lockoutIntervalDose) {
		this.lockoutIntervalDose = lockoutIntervalDose;
	}
	public String getLockoutIntervalDoseUOM() {
		return lockoutIntervalDoseUOM;
	}
	public void setLockoutIntervalDoseUOM(String lockoutIntervalDoseUOM) {
		this.lockoutIntervalDoseUOM = lockoutIntervalDoseUOM;
	}
	public String getFourHourLimit() {
		return fourHourLimit;
	}
	public void setFourHourLimit(String fourHourLimit) {
		this.fourHourLimit = fourHourLimit;
	}
	public String getFourHourLimitUOM() {
		return fourHourLimitUOM;
	}
	public void setFourHourLimitUOM(String fourHourLimitUOM) {
		this.fourHourLimitUOM = fourHourLimitUOM;
	}
	public String getContinuous() {
		return continuous;
	}
	public void setContinuous(String continuous) {
		this.continuous = continuous;
	}
	public String getContinuousUOM() {
		return continuousUOM;
	}
	public void setContinuousUOM(String continuousUOM) {
		this.continuousUOM = continuousUOM;
	}
	public String getChargeCode() {
		return chargeCode;
	}
	public void setChargeCode(String chargeCode) {
		this.chargeCode = chargeCode;
	}

	public String getIvVolumeUnit() {
		return ivVolumeUnit;
	}
	public void setIvVolumeUnit(String ivVolumeUnit) {
		this.ivVolumeUnit = ivVolumeUnit;
	}

	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public String getFormularyStrength() {
		return formularyStrength;
	}
	public void setFormularyStrength(String formularyStrength) {
		this.formularyStrength = formularyStrength;
	}
	public String getFormularyDoseSize() {
		return formularyDoseSize;
	}
	public void setFormularyDoseSize(String formularyDoseSize) {
		this.formularyDoseSize = formularyDoseSize;
	}
	public String getFormularyDoseUOM() {
		return formularyDoseUOM;
	}
	public void setFormularyDoseUOM(String formularyDoseUOM) {
		this.formularyDoseUOM = formularyDoseUOM;
	}
	public String getFormularyDispenseSize() {
		return formularyDispenseSize;
	}
	public void setFormularyDispenseSize(String formularyDispenseSize) {
		this.formularyDispenseSize = formularyDispenseSize;
	}
	public String getFormularyDispenseUOM() {
		return formularyDispenseUOM;
	}
	public void setFormularyDispenseUOM(String formularyDispenseUOM) {
		this.formularyDispenseUOM = formularyDispenseUOM;
	}
	public String geteMARInstructions() {
		return eMARInstructions;
	}
	public void seteMARInstructions(String eMARInstructions) {
		this.eMARInstructions = eMARInstructions;
	}
	public boolean isIvDiluent() {
		return ivDiluent;
	}
	public void setIvDiluent(boolean ivDiluent) {
		this.ivDiluent = ivDiluent;
	}
	public boolean isAdditive() {
		return additive;
	}
	public void setAdditive(boolean additive) {
		this.additive = additive;
	}
	public String getDispense() {
		return dispense;
	}
	public void setDispense(String dispense) {
		this.dispense = dispense;
	}
	public String getDispenseQty() {
		return dispenseQty;
	}
	public void setDispenseQty(String dispenseQty) {
		this.dispenseQty = dispenseQty;
	}
	public String getDispenseUOM() {
		return dispenseUOM;
	}
	public void setDispenseUOM(String dispenseUOM) {
		this.dispenseUOM = dispenseUOM;
	}
	public String getActualDispense() {
		return actualDispense;
	}
	public void setActualDispense(String actualDispense) {
		this.actualDispense = actualDispense;
	}
	public int getIvDiluentId() {
		return ivDiluentId;
	}
	public void setIvDiluentId(int ivDiluentId) {
		this.ivDiluentId = ivDiluentId;
	}
	public String getIvVolume() {
		return ivVolume;
	}
	public void setIvVolume(String ivVolume) {
		this.ivVolume = ivVolume;
	}
	public int getFormularyItemId() {
		return formularyItemId;
	}
	public void setFormularyItemId(int formularyItemId) {
		this.formularyItemId = formularyItemId;
	}
	
	public String getOrderDose() {
		return orderDose;
	}
	public void setOrderDose(String orderDose) {
		this.orderDose = orderDose;
	}
	public int getDisplayIndex() {
		return displayIndex;
	}
	public void setDisplayIndex(int displayIndex) {
		this.displayIndex = displayIndex;
	}
	public boolean isCalculate() {
		return calculate;
	}
	public void setCalculate(boolean calculate) {
		this.calculate = calculate;
	}
	public String getUpcCode() {
		return upcCode;
	}
	public void setUpcCode(String upcCode) {
		this.upcCode = upcCode;
	}
	public int getDrugNameId() {
		return drugNameId;
	}
	public void setDrugNameId(int drugNameId) {
		this.drugNameId = drugNameId;
	}
	public PriceRuleParam getPriceRuleParam() {
		return priceRuleParam;
	}
	public void setPriceRuleParam(PriceRuleParam priceRuleParam) {
		this.priceRuleParam = priceRuleParam;
	}
	public double getTotalCostCalculatedWithChargeType() {
		return totalCostCalculatedWithChargeType;
	}
	public void setTotalCostCalculatedWithChargeType(double totalCostCalculatedWithChargeType) {
		this.totalCostCalculatedWithChargeType = totalCostCalculatedWithChargeType;
	}
	public double getUnitCostCalculatedWithChargeType() {
		return unitCostCalculatedWithChargeType;
	}
	public void setUnitCostCalculatedWithChargeType(double unitCostCalculatedWithChargeType) {
		this.unitCostCalculatedWithChargeType = unitCostCalculatedWithChargeType;
	}
	public String getRxNorm() {
		return rxNorm;
	}
	public void setRxNorm(String rxNorm) {
		this.rxNorm = rxNorm;
	}
	public int getRoutedGenericItemId() {
		return routedGenericItemId;
	}
	public void setRoutedGenericItemId(int routedGenericItemId) {
		this.routedGenericItemId = routedGenericItemId;
	}
	public String getGenericName() {
		return genericName;
	}
	public void setGenericName(String genericName) {
		this.genericName = genericName;
	}
	public String getInternalNotes() {
		return internalNotes;
	}
	public void setInternalNotes(String internalNotes) {
		this.internalNotes = internalNotes;
	}
	public String getFormularyHCPCSCodeRange() {
		return formularyHCPCSCodeRange;
	}
	public void setFormularyHCPCSCodeRange(String formularyHCPCSCodeRange) {
		this.formularyHCPCSCodeRange = formularyHCPCSCodeRange;
	}
	public String getFormularyHCPCSCodeType() {
		return formularyHCPCSCodeType;
	}
	public void setFormularyHCPCSCodeType(String formularyHCPCSCodeType) {
		this.formularyHCPCSCodeType = formularyHCPCSCodeType;
	}
	public String getFormularyHCPCSCodeUnit() {
		return formularyHCPCSCodeUnit;
	}
	public void setFormularyHCPCSCodeUnit(String formularyHCPCSCodeUnit) {
		this.formularyHCPCSCodeUnit = formularyHCPCSCodeUnit;
	}
	public double getToTalHCPCSUnit() {
		return toTalHCPCSUnit;
	}
	public void setToTalHCPCSUnit(double toTalHCPCSUnit) {
		this.toTalHCPCSUnit = toTalHCPCSUnit;
	}
	public double getCostCalculatedWithChargeType() {
		return costCalculatedWithChargeType;
	}
	public void setCostCalculatedWithChargeType(double costCalculatedWithChargeType) {
		this.costCalculatedWithChargeType = costCalculatedWithChargeType;
	}
	public boolean isISSFlag() {
		return isISSFlag;
	}
	public void setISSFlag(boolean isISSFlag) {
		this.isISSFlag = isISSFlag;
	}
	public boolean isConfigureISSFlag() {
		return isConfigureISSFlag;
	}
	public void setConfigureISSFlag(boolean isConfigureISSFlag) {
		this.isConfigureISSFlag = isConfigureISSFlag;
	}
	public String getMinDose() {
		return minDose;
	}
	public void setMinDose(String minDose) {
		this.minDose = minDose;
	}
	public String getMaxDose() {
		return maxDose;
	}
	public void setMaxDose(String maxDose) {
		this.maxDose = maxDose;
	}
	public String getPackSizeUnitcode() {
		return packSizeUnitcode;
	}
	public void setPackSizeUnitcode(String packSizeUnitcode) {
		this.packSizeUnitcode = packSizeUnitcode;
	}
	public String getPackSize() {
		return packSize;
	}
	public void setPackSize(String packSize) {
		this.packSize = packSize;
	}
	
}
