package inpatientWeb.admin.pharmacySettings.formularySetup.modal;

import inpatientWeb.admin.pharmacySettings.pharmacyUtility.model.PriceRuleParam;

public class CostPerFormulary {

	private int formularyId;
	private int chargeTypeId;
	private String chargeCode;
	private double dispense;
	private String primaryNDC;
	private String assocProdNDC;
	private double calculatedCostWithoutDispense;
	private double calculatedCostWithDispense;
	private String ndcCode;
	private int cptItemId;
	private boolean ndcMatchFound;
	private PriceRuleParam priceRuleParam;
	private double awup;
	private double costToProcure;
	private String ppId;
	private double totalHcpcsUnit;
	private boolean isDrugTypeBulk;
	private boolean calculate;
	
	public int getFormularyId() {
		return formularyId;
	}
	public void setFormularyId(int formularyId) {
		this.formularyId = formularyId;
	}
	public int getChargeTypeId() {
		return chargeTypeId;
	}
	public void setChargeTypeId(int chargeTypeId) {
		this.chargeTypeId = chargeTypeId;
	}
	public double getDispense() {
		return dispense;
	}
	public void setDispense(double dispense) {
		this.dispense = dispense;
	}
	public String getPrimaryNDC() {
		return primaryNDC;
	}
	public void setPrimaryNDC(String primaryNDC) {
		this.primaryNDC = primaryNDC;
	}
	public String getAssocProdNDC() {
		return assocProdNDC;
	}
	public void setAssocProdNDC(String assocProdNDC) {
		this.assocProdNDC = assocProdNDC;
	}
	public double getCalculatedCostWithoutDispense() {
		return calculatedCostWithoutDispense;
	}
	public void setCalculatedCostWithoutDispense(double calculatedCostWithoutDispense) {
		this.calculatedCostWithoutDispense = calculatedCostWithoutDispense;
	}
	public double getCalculatedCostWithDispense() {
		return calculatedCostWithDispense;
	}
	public void setCalculatedCostWithDispense(double calculatedCostWithDispense) {
		this.calculatedCostWithDispense = calculatedCostWithDispense;
	}
	public String getNdcCode() {
		return ndcCode;
	}
	public void setNdcCode(String ndcCode) {
		this.ndcCode = ndcCode;
	}
	public int getCptItemId() {
		return cptItemId;
	}
	public void setCptItemId(int cptItemId) {
		this.cptItemId = cptItemId;
	}
	
	public String getChargeCode() {
		return chargeCode;
	}
	public void setChargeCode(String chargeCode) {
		this.chargeCode = chargeCode;
	}
	public boolean isNdcMatchFound() {
		return ndcMatchFound;
	}
	public void setNdcMatchFound(boolean ndcMatchFound) {
		this.ndcMatchFound = ndcMatchFound;
	}
	public PriceRuleParam getPriceRuleParam() {
		return priceRuleParam;
	}
	public void setPriceRuleParam(PriceRuleParam priceRuleParam) {
		this.priceRuleParam = priceRuleParam;
	}
	public double getAwup() {
		return awup;
	}
	public void setAwup(double awup) {
		this.awup = awup;
	}
	public double getCostToProcure() {
		return costToProcure;
	}
	public void setCostToProcure(double costToProcure) {
		this.costToProcure = costToProcure;
	}
	public String getPpId() {
		return ppId;
	}
	public void setPpId(String ppId) {
		this.ppId = ppId;
	}
	public double getTotalHcpcsUnit() {
		return totalHcpcsUnit;
	}
	public void setTotalHcpcsUnit(double totalHcpcsUnit) {
		this.totalHcpcsUnit = totalHcpcsUnit;
	}
	public boolean isDrugTypeBulk() {
		return isDrugTypeBulk;
	}
	public void setDrugTypeBulk(boolean isDrugTypeBulk) {
		this.isDrugTypeBulk = isDrugTypeBulk;
	}
	public boolean isCalculate() {
		return calculate;
	}
	public void setCalculate(boolean calculate) {
		this.calculate = calculate;
	}
	
	
}
