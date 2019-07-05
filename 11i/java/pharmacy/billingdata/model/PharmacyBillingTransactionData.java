package inpatientWeb.pharmacy.billingdata.model;

import inpatientWeb.billing.transactions.BillingDataTransaction.OPERATION;
import inpatientWeb.billing.transactions.BillingDataTransaction.OPERATIONMODULE;

/**
*	@author bharat tulsiyani on Jun 19, 2018
*/
public class PharmacyBillingTransactionData {
	
	private int encounterId;
	private int itemId;
	private String chargeCode;
	private String ndcCode;
	private double fee;
	private double units;
	private OPERATION operation;
	private int billingDataId;
	private int userId;
	private int practiceId;
	private int facilityId;
	private int deptId;
	private int serviceTypeId;
	private int patientTypeId;
	private int encTypeId;
	private int provId;
	private String mod1;
	private String mod2;
	private String mod3;
	private String mod4;
	private String icd1;
	private String icd2;
	private String icd3;
	private String icd4;
	private String icdcode1;
	private String icdcode2;
	private String icdcode3;
	private String icdcode4;
	private String timezone;
	private String transactionDate;
	private OPERATIONMODULE fromModule;
	
	public PharmacyBillingTransactionData() {
		super();
	}
	
	public int getEncounterId() {
		return encounterId;
	}
	public void setEncounterId(int encounterId) {
		this.encounterId = encounterId;
	}
	public int getItemId() {
		return itemId;
	}
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	public String getChargeCode() {
		return chargeCode;
	}
	public void setChargeCode(String chargeCode) {
		this.chargeCode = chargeCode;
	}
	public String getNdcCode() {
		return ndcCode;
	}
	public void setNdcCode(String ndcCode) {
		this.ndcCode = ndcCode;
	}
	public double getFee() {
		return fee;
	}
	public void setFee(double fee) {
		this.fee = fee;
	}
	public double getUnits() {
		return units;
	}
	public void setUnits(double units) {
		this.units = units;
	}
	public OPERATION getOperation() {
		return operation;
	}
	public void setOperation(OPERATION operation) {
		this.operation = operation;
	}
	public int getBillingDataId() {
		return billingDataId;
	}
	public void setBillingDataId(int billingDataId) {
		this.billingDataId = billingDataId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getPracticeId() {
		return practiceId;
	}
	public void setPracticeId(int practiceId) {
		this.practiceId = practiceId;
	}
	public int getFacilityId() {
		return facilityId;
	}
	public void setFacilityId(int facilityId) {
		this.facilityId = facilityId;
	}
	public int getDeptId() {
		return deptId;
	}
	public void setDeptId(int deptId) {
		this.deptId = deptId;
	}
	public int getServiceTypeId() {
		return serviceTypeId;
	}
	public void setServiceTypeId(int serviceTypeId) {
		this.serviceTypeId = serviceTypeId;
	}
	public int getPatientTypeId() {
		return patientTypeId;
	}
	public void setPatientTypeId(int patientTypeId) {
		this.patientTypeId = patientTypeId;
	}
	public int getEncTypeId() {
		return encTypeId;
	}
	public void setEncTypeId(int encTypeId) {
		this.encTypeId = encTypeId;
	}
	public int getProvId() {
		return provId;
	}
	public void setProvId(int provId) {
		this.provId = provId;
	}
	public String getMod1() {
		return mod1;
	}
	public void setMod1(String mod1) {
		this.mod1 = mod1;
	}
	public String getMod2() {
		return mod2;
	}
	public void setMod2(String mod2) {
		this.mod2 = mod2;
	}
	public String getMod3() {
		return mod3;
	}
	public void setMod3(String mod3) {
		this.mod3 = mod3;
	}
	public String getMod4() {
		return mod4;
	}
	public void setMod4(String mod4) {
		this.mod4 = mod4;
	}
	public String getIcd1() {
		return icd1;
	}
	public void setIcd1(String icd1) {
		this.icd1 = icd1;
	}
	public String getIcd2() {
		return icd2;
	}
	public void setIcd2(String icd2) {
		this.icd2 = icd2;
	}
	public String getIcd3() {
		return icd3;
	}
	public void setIcd3(String icd3) {
		this.icd3 = icd3;
	}
	public String getIcd4() {
		return icd4;
	}
	public void setIcd4(String icd4) {
		this.icd4 = icd4;
	}
	public String getIcdcode1() {
		return icdcode1;
	}
	public void setIcdcode1(String icdcode1) {
		this.icdcode1 = icdcode1;
	}
	public String getIcdcode2() {
		return icdcode2;
	}
	public void setIcdcode2(String icdcode2) {
		this.icdcode2 = icdcode2;
	}
	public String getIcdcode3() {
		return icdcode3;
	}
	public void setIcdcode3(String icdcode3) {
		this.icdcode3 = icdcode3;
	}
	public String getIcdcode4() {
		return icdcode4;
	}
	public void setIcdcode4(String icdcode4) {
		this.icdcode4 = icdcode4;
	}
	public String getTimezone() {
		return timezone;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	public String getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}
	public OPERATIONMODULE getFromModule() {
		return fromModule;
	}
	public void setFromModule(OPERATIONMODULE fromModule) {
		this.fromModule = fromModule;
	}

	@Override
	public String toString() {
		return "PharmacyBillingTransactionData [encounterId=" + encounterId + ", itemId=" + itemId + ", chargeCode="
				+ chargeCode + ", ndcCode=" + ndcCode + ", fee=" + fee + ", units=" + units + ", operation=" + operation
				+ ", billingDataId=" + billingDataId + ", userId=" + userId + ", practiceId=" + practiceId
				+ ", facilityId=" + facilityId + ", deptId=" + deptId + ", serviceTypeId=" + serviceTypeId
				+ ", patientTypeId=" + patientTypeId + ", encTypeId=" + encTypeId + ", provId=" + provId + ", mod1="
				+ mod1 + ", mod2=" + mod2 + ", mod3=" + mod3 + ", mod4=" + mod4 + ", icd1=" + icd1 + ", icd2=" + icd2
				+ ", icd3=" + icd3 + ", icd4=" + icd4 + ", icdcode1=" + icdcode1 + ", icdcode2=" + icdcode2
				+ ", icdcode3=" + icdcode3 + ", icdcode4=" + icdcode4 + ", timezone=" + timezone + ", transactionDate="
				+ transactionDate + ", fromModule=" + fromModule + "]";
	}

}
