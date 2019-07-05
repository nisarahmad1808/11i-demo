package inpatientWeb.pharmacy.billingdata.util;

import org.apache.commons.lang.math.NumberUtils;

import inpatientWeb.admin.pharmacySettings.formularySetup.modal.CostPerFormulary;
import inpatientWeb.billing.transactions.BillingDataTransaction;
import inpatientWeb.pharmacy.billingdata.model.OrderDetails;
import inpatientWeb.pharmacy.billingdata.model.PharmacyBillingData;
import inpatientWeb.pharmacy.billingdata.model.PharmacyBillingTransactionData;
import inpatientWeb.registration.appointment.dto.SubEncounterData;
import inpatientWeb.utils.IPTzUtils;

/**
*	@author bharat tulsiyani on Jun 21, 2018
*/
public class PharmacyBillingUtil {

	public static final int FAILURE_VALUE = 0;
	public static final int SUCCESS_VALUE = 1;
	
	public static PharmacyBillingTransactionData getPharmacyBillingTransactionData(SubEncounterData subEncounterData, CostPerFormulary costPerFormulary, PharmacyBillingData pharmacyBillingData, BillingDataTransaction.OPERATION operation, OrderDetails orderDetails, int practiceId, int billingDataId) {
		PharmacyBillingTransactionData pharmacyBillingTransactionData = new PharmacyBillingTransactionData();
		if(subEncounterData != null) {
			pharmacyBillingTransactionData.setEncounterId(subEncounterData.getEncounterId());
			pharmacyBillingTransactionData.setFacilityId(subEncounterData.getFacilityId());
			pharmacyBillingTransactionData.setDeptId(NumberUtils.toInt(subEncounterData.getDepartMent(), 0));
			pharmacyBillingTransactionData.setServiceTypeId(subEncounterData.getServiceType());
			pharmacyBillingTransactionData.setPatientTypeId(subEncounterData.getPatientType());
			pharmacyBillingTransactionData.setEncTypeId(subEncounterData.getEncType());
		}
		
		if(costPerFormulary != null) {
			pharmacyBillingTransactionData.setItemId(costPerFormulary.getCptItemId());
			pharmacyBillingTransactionData.setChargeCode(costPerFormulary.getChargeCode());
			pharmacyBillingTransactionData.setNdcCode(costPerFormulary.getPpId());
			pharmacyBillingTransactionData.setFee(costPerFormulary.getCalculatedCostWithDispense());
			pharmacyBillingTransactionData.setUnits(costPerFormulary.getDispense());
		}
		
		pharmacyBillingTransactionData.setOperation(operation);
		pharmacyBillingTransactionData.setBillingDataId(billingDataId);
		pharmacyBillingTransactionData.setPracticeId(practiceId);
		
		if (pharmacyBillingData != null) {
			pharmacyBillingTransactionData.setUserId(pharmacyBillingData.getUserId());
			pharmacyBillingTransactionData.setTimezone(pharmacyBillingData.getUserTimeZone());
			pharmacyBillingTransactionData.setTransactionDate(pharmacyBillingData.getTransactionDate());
			pharmacyBillingTransactionData.setFromModule(pharmacyBillingData.getOperationModule());
		}
		
		if(orderDetails != null) {
			pharmacyBillingTransactionData.setProvId(orderDetails.getOrderedById());
		}
		pharmacyBillingTransactionData.setMod1("");
		pharmacyBillingTransactionData.setMod2("");
		pharmacyBillingTransactionData.setMod3("");
		pharmacyBillingTransactionData.setMod4("");
		pharmacyBillingTransactionData.setIcd1("");
		pharmacyBillingTransactionData.setIcd2("");
		pharmacyBillingTransactionData.setIcd3("");
		pharmacyBillingTransactionData.setIcd4("");
		pharmacyBillingTransactionData.setIcdcode1("");
		pharmacyBillingTransactionData.setIcdcode2("");
		pharmacyBillingTransactionData.setIcdcode3("");
		pharmacyBillingTransactionData.setIcdcode4("");
		
		return pharmacyBillingTransactionData;
	}
	
	public static PharmacyBillingData setBillingData(PharmacyBillingData pharmacyBillingData) {
		if(pharmacyBillingData.getTransactionDate() == null || pharmacyBillingData.getTransactionDate().isEmpty()) {
			pharmacyBillingData.setTransactionDate(IPTzUtils.getStrFromDt(pharmacyBillingData.getBillingDate(),IPTzUtils.DEFAULT_DB_DT_FMT));
		}
		
		if(pharmacyBillingData.getUserTimeZone() == null || pharmacyBillingData.getUserTimeZone().isEmpty()) {
			pharmacyBillingData.setUserTimeZone(IPTzUtils.getTimeZoneForResource(pharmacyBillingData.getUserId()));
		}
		return pharmacyBillingData;
	}
}
