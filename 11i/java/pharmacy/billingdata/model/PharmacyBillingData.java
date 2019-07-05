package inpatientWeb.pharmacy.billingdata.model;

import java.util.Date;
import java.util.List;

import inpatientWeb.billing.transactions.BillingDataTransaction;
import inpatientWeb.pharmacy.billingdata.constant.PharmacyLogEnum;

/**
 * @author Bharat Tulsiyani
 * @Copyright ©eClinicalWorks LLC.
 * @Date Feb 10, 2018
 */
public class PharmacyBillingData {
	
	private long orderId;
	private int userId;
	private int masterEncounterId;
	private long eMARDrugAdminScheduleId;
	private int billingDataId;
	private Date billingDate;
	private List<DrugNdcDetails> drugNdcDetails;
	private BillingDataTransaction.OPERATION operation;
	private BillingDataTransaction.OPERATIONMODULE operationModule;
	private PharmacyLogEnum.SubModule subModule;
	private String userTimeZone;
	private String transactionDate;
	private Date encounterDateTime;
	
	public PharmacyBillingData() {
		super();
		subModule = PharmacyLogEnum.SubModule.DEFAULT;
	}

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getMasterEncounterId() {
		return masterEncounterId;
	}

	public void setMasterEncounterId(int masterEncounterId) {
		this.masterEncounterId = masterEncounterId;
	}
	
	public long geteMARDrugAdminScheduleId() {
		return eMARDrugAdminScheduleId;
	}

	public void seteMARDrugAdminScheduleId(long eMARDrugAdminScheduleId) {
		this.eMARDrugAdminScheduleId = eMARDrugAdminScheduleId;
	}
	
	public int getBillingDataId() {
		return billingDataId;
	}

	public void setBillingDataId(int billingDataId) {
		this.billingDataId = billingDataId;
	}

	public Date getBillingDate() {
		return billingDate;
	}

	public void setBillingDate(Date billingDate) {
		this.billingDate = billingDate;
	}

	public List<DrugNdcDetails> getDrugNdcDetails() {
		return drugNdcDetails;
	}

	public void setDrugNdcDetails(List<DrugNdcDetails> drugNdcDetails) {
		this.drugNdcDetails = drugNdcDetails;
	}
	
	public BillingDataTransaction.OPERATION getOperation() {
		return operation;
	}

	public void setOperation(BillingDataTransaction.OPERATION operation) {
		this.operation = operation;
	}

	public BillingDataTransaction.OPERATIONMODULE getOperationModule() {
		return operationModule;
	}

	public void setOperationModule(BillingDataTransaction.OPERATIONMODULE operationModule) {
		this.operationModule = operationModule;
	}
	
	public PharmacyLogEnum.SubModule getSubModule() {
		return subModule;
	}

	public void setSubModule(PharmacyLogEnum.SubModule subModule) {
		this.subModule = subModule;
	}

	public String getUserTimeZone() {
		return userTimeZone;
	}

	public void setUserTimeZone(String userTimeZone) {
		this.userTimeZone = userTimeZone;
	}
	
	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}
	
	public Date getEncounterDateTime() {
		return encounterDateTime;
	}

	public void setEncounterDateTime(Date encounterDateTime) {
		this.encounterDateTime = encounterDateTime;
	}

	@Override
	public String toString() {
		return "PharmacyBillingData [orderId=" + orderId + ", userId=" + userId + ", masterEncounterId="
				+ masterEncounterId + ", eMARDrugAdminScheduleId=" + eMARDrugAdminScheduleId + ", billingDataId="
				+ billingDataId + ", billingDate=" + billingDate + ", drugNdcDetails=" + drugNdcDetails + ", operation="
				+ operation + ", operationModule=" + operationModule + ", subModule=" + subModule + ", userTimeZone="
				+ userTimeZone + ", transactionDate=" + transactionDate + ", encounterDateTime=" + encounterDateTime
				+ "]";
	}

}