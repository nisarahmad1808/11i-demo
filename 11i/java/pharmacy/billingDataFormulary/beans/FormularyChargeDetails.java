package inpatientWeb.pharmacy.billingDataFormulary.beans;
import inpatientWeb.admin.pharmacySettings.pharmacyUtility.model.PriceRuleParam;
public class FormularyChargeDetails {	
	
	private int drugFormularyId;	
	private String dispenseUOM;	
	private boolean nonBillable;	
	private String price;	
	private double awup;
	private int chargeTypeId;
	private double totalCostCalculatedWithChargeType;
	private double unitCostCalculatedWithChargeType;
	private double costCalculatedWithChargeType;	
	private String chargeCode;	
	private String formularyDoseSize;
	private String formularyDoseUOM;
	private String formularyDispenseSize;
	private String formularyDispenseUOM;
	private String formularyHCPCSCodeRange;
	private String formularyHCPCSCodeType;
	private String formularyHCPCSCodeUnit;
	private double toTalHCPCSUnit;	
	private boolean isDrugTypeBulk;
	private String orderDispense;
	private String orderDose;	
	private PriceRuleParam priceRuleParam;
	private int cptItemId;
	private String packSizeUnitcode;
	private String packSize;
	private boolean calculate;
	
	public String getOrderDispense() {
		return orderDispense;
	}
	public void setOrderDispense(String orderDispense) {
		this.orderDispense = orderDispense;
	}	
	public boolean isDrugTypeBulk() {
		return isDrugTypeBulk;
	}
	public void setDrugTypeBulk(boolean isDrugTypeBulk) {
		this.isDrugTypeBulk = isDrugTypeBulk;
	}
	public int getDrugFormularyId() {
		return drugFormularyId;
	}
	public void setDrugFormularyId(int drugFormularyId) {
		this.drugFormularyId = drugFormularyId;
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
	
	public String getChargeCode() {
		return chargeCode;
	}
	public void setChargeCode(String chargeCode) {
		this.chargeCode = chargeCode;
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
	public String getDispenseUOM() {
		return dispenseUOM;
	}
	public void setDispenseUOM(String dispenseUOM) {
		this.dispenseUOM = dispenseUOM;
	}	
	public String getOrderDose() {
		return orderDose;
	}
	public void setOrderDose(String orderDose) {
		this.orderDose = orderDose;
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
	public int getCptItemId() {
		return cptItemId;
	}
	public void setCptItemId(int cptItemId) {
		this.cptItemId = cptItemId;
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
	public boolean isCalculate() {
		return calculate;
	}
	public void setCalculate(boolean calculate) {
		this.calculate = calculate;
	}	
	
}
