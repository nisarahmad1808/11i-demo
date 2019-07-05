package inpatientWeb.pharmacy.billingdata.model;

/**
*	@author bharat tulsiyani on Jun 19, 2018
*/
public class PharmacyBillingLogData {
	
	private long orderId;
	private long eMARDrugAdminScheduleId;
	private String errorMessage;
	private PharmacyBillingTransactionData pharmacyBillingTransactionData;
	private PharmacyBillingData pharmacyBillingData;
	public long getOrderId() {
		return orderId;
	}
	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	public long geteMARDrugAdminScheduleId() {
		return eMARDrugAdminScheduleId;
	}
	public void seteMARDrugAdminScheduleId(long eMARDrugAdminScheduleId) {
		this.eMARDrugAdminScheduleId = eMARDrugAdminScheduleId;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public PharmacyBillingTransactionData getPharmacyBillingTransactionData() {
		return pharmacyBillingTransactionData;
	}
	public void setPharmacyBillingTransactionData(PharmacyBillingTransactionData pharmacyBillingTransactionData) {
		this.pharmacyBillingTransactionData = pharmacyBillingTransactionData;
	}
	public PharmacyBillingData getPharmacyBillingData() {
		return pharmacyBillingData;
	}
	public void setPharmacyBillingData(PharmacyBillingData pharmacyBillingData) {
		this.pharmacyBillingData = pharmacyBillingData;
	}
	@Override
	public String toString() {
		return "PharmacyBillingLogData [orderId=" + orderId + ", eMARDrugAdminScheduleId=" + eMARDrugAdminScheduleId
				+ ", errorMessage=" + errorMessage + ", pharmacyBillingTransactionData="
				+ pharmacyBillingTransactionData + ", pharmacyBillingData=" + pharmacyBillingData + "]";
	}

}
