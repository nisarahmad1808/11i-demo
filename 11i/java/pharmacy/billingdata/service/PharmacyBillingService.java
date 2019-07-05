package inpatientWeb.pharmacy.billingdata.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import inpatientWeb.Auth.exception.InternalServerException;
import inpatientWeb.Auth.exception.NotFoundException;
import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;
import inpatientWeb.admin.pharmacySettings.formularySetup.modal.CostPerFormulary;
import inpatientWeb.admin.pharmacySettings.formularySetup.modal.TotalCostPerOrderId;
import inpatientWeb.admin.pharmacySettings.formularySetup.service.FormularySetupService;
import inpatientWeb.billing.transactions.BillingDataTransaction;
import inpatientWeb.eMAR.util.EMARUtil;
import inpatientWeb.pharmacy.billingdata.constant.PharmacyLogEnum;
import inpatientWeb.pharmacy.billingdata.dao.PharmacyBillingDAO;
import inpatientWeb.pharmacy.billingdata.model.DrugNdcDetails;
import inpatientWeb.pharmacy.billingdata.model.OrderDetails;
import inpatientWeb.pharmacy.billingdata.model.PharmacyBillingData;
import inpatientWeb.pharmacy.billingdata.model.PharmacyBillingDetail;
import inpatientWeb.pharmacy.billingdata.model.PharmacyBillingLogData;
import inpatientWeb.pharmacy.billingdata.model.PharmacyBillingTransactionData;
import inpatientWeb.pharmacy.billingdata.util.PharmacyBillingUtil;
import inpatientWeb.registration.appointment.dto.SubEncounterData;
import inpatientWeb.registration.appointment.service.AppointmentService;
import inpatientWeb.utils.IPTzUtils;

/**
 * The Class PharmacyBillingService.
 *
 * @author Bharat Tulsiyani
 * @Copyright ©eClinicalWorks LLC.
 * @Date Feb 10, 2018
 */
@Service
@Scope("prototype")
@Lazy 
public class PharmacyBillingService {
	
	/** The pharmacy billing DAO. */
	private PharmacyBillingDAO pharmacyBillingDAO;
	
	/** The formulary setup service. */
	private FormularySetupService formularySetupService;	
	
	/** The appointment service. */
	private AppointmentService appointmentService;
	
	/** The pharmacy billing log handler. */
	private PharmacyBillingLogHandler pharmacyBillingLogHandler;
    
	/**
	 * Save billing data, 
	 * it will also save data in table "ip_pharmacy_billing_detail" for future reference.
	 *
	 * @param pharmacyBillingData the pharmacy billing data
	 * @return true, if successful
	 */
	public boolean saveBillingData(PharmacyBillingData pharmacyBillingData){
		PharmacyBillingTransactionData pharmacyBillingTransactionData = null;
		SubEncounterData subEncounterData = null;
		CostPerFormulary costPerFormulary = null;
		OrderDetails orderDetails = null;
		int practiceId = 0;
		
		// prerequisite data
		pharmacyBillingData = PharmacyBillingUtil.setBillingData(pharmacyBillingData);
		try{
			// validate pharmacy billing data
			String validationMessage  = validatePharmacyBillingData(pharmacyBillingData);
			if(!validationMessage.isEmpty()) {
				savePharmacyBillingDetails(costPerFormulary, pharmacyBillingData, null, pharmacyBillingData.getOperation(), PharmacyBillingUtil.FAILURE_VALUE,pharmacyBillingData.getSubModule());
				pharmacyBillingTransactionData = PharmacyBillingUtil.getPharmacyBillingTransactionData(subEncounterData, costPerFormulary, pharmacyBillingData, pharmacyBillingData.getOperation(), orderDetails, practiceId, 0);
				logPharmacyBillingData(pharmacyBillingTransactionData, pharmacyBillingData, pharmacyBillingData.getOperation().name(), validationMessage);
				return false;
			}
			
			// fetching order details
			orderDetails = pharmacyBillingDAO.getOrderDetails(pharmacyBillingData.getOrderId());
			
			// fetching encounter details
			subEncounterData = getSubEncounterData(pharmacyBillingData);
			practiceId = pharmacyBillingDAO.getPracticeId(subEncounterData.getEncounterId());
			
			// fetching cost details
			TotalCostPerOrderId totalCostPerOrderId = getCostDetail(pharmacyBillingData);
			
			// to cost details found then log the request and return the failure case
			if(totalCostPerOrderId == null || totalCostPerOrderId.getCostProductList() == null || totalCostPerOrderId.getCostProductList().isEmpty()) {
				savePharmacyBillingDetails(costPerFormulary, pharmacyBillingData, null, pharmacyBillingData.getOperation(), PharmacyBillingUtil.FAILURE_VALUE,pharmacyBillingData.getSubModule());
				pharmacyBillingTransactionData = PharmacyBillingUtil.getPharmacyBillingTransactionData(subEncounterData, costPerFormulary, pharmacyBillingData, pharmacyBillingData.getOperation(), orderDetails, practiceId, 0);
				logPharmacyBillingData(pharmacyBillingTransactionData, pharmacyBillingData, pharmacyBillingData.getOperation().name(), "Error occurred while getting the cost details");
				return false;
			}
			
			for(int index = 0 ; index < totalCostPerOrderId.getCostProductList().size(); index++){
				costPerFormulary = totalCostPerOrderId.getCostProductList().get(index);
				
				List<Integer> billingDataIdList = BillingDataTransaction.saveBillingData(null, subEncounterData.getEncounterId(),
						costPerFormulary.getCptItemId(), costPerFormulary.getChargeCode(), costPerFormulary.getPpId(),
						costPerFormulary.getCalculatedCostWithDispense(), costPerFormulary.getTotalHcpcsUnit(), pharmacyBillingData.getOperation(), pharmacyBillingData.getBillingDataId(),
						pharmacyBillingData.getUserId(), practiceId, subEncounterData.getFacilityId(), NumberUtils.toInt(subEncounterData.getDepartMent(), 0),
						subEncounterData.getServiceType(), subEncounterData.getPatientType(),
						subEncounterData.getEncType(), orderDetails.getOrderedById(), "", "", "", "", "", "", "", "",
						"", "", "", "", pharmacyBillingData.getUserTimeZone(), pharmacyBillingData.getTransactionDate(), pharmacyBillingData.getOperationModule());
				
				// check if billing data is saved successfully
				if(billingDataIdList.isEmpty() || billingDataIdList.contains(0) || billingDataIdList.contains(-1)) {
					savePharmacyBillingDetails(costPerFormulary, pharmacyBillingData, null, pharmacyBillingData.getOperation(), PharmacyBillingUtil.FAILURE_VALUE,pharmacyBillingData.getSubModule());
					pharmacyBillingTransactionData = PharmacyBillingUtil.getPharmacyBillingTransactionData(subEncounterData, costPerFormulary, pharmacyBillingData, pharmacyBillingData.getOperation(), orderDetails, practiceId, 0);
					logPharmacyBillingData(pharmacyBillingTransactionData, pharmacyBillingData, pharmacyBillingData.getOperation().name(), "Error occurred while saving the billing data");
					return false;
				}
				
				for(Integer billingId : billingDataIdList){
					if(savePharmacyBillingDetails(costPerFormulary, pharmacyBillingData, billingId, pharmacyBillingData.getOperation(), PharmacyBillingUtil.SUCCESS_VALUE,pharmacyBillingData.getSubModule()) == 0) {
						pharmacyBillingTransactionData = PharmacyBillingUtil.getPharmacyBillingTransactionData(subEncounterData, costPerFormulary, pharmacyBillingData, pharmacyBillingData.getOperation(), orderDetails, practiceId, billingId);
						logPharmacyBillingData(pharmacyBillingTransactionData, pharmacyBillingData, pharmacyBillingData.getOperation().name(), "Error occurred while saving the billing details data");
						return false;
					}
					pharmacyBillingTransactionData = PharmacyBillingUtil.getPharmacyBillingTransactionData(subEncounterData, costPerFormulary, pharmacyBillingData, pharmacyBillingData.getOperation(), orderDetails, practiceId, billingId);
					logPharmacyBillingData(pharmacyBillingTransactionData, pharmacyBillingData, pharmacyBillingData.getOperation().name(), StringUtils.EMPTY);
				}
			}
			return true;
		} catch(Exception e) {
			savePharmacyBillingDetails(costPerFormulary, pharmacyBillingData, null, pharmacyBillingData.getOperation(), PharmacyBillingUtil.FAILURE_VALUE,pharmacyBillingData.getSubModule());
			pharmacyBillingTransactionData = PharmacyBillingUtil.getPharmacyBillingTransactionData(subEncounterData, costPerFormulary, pharmacyBillingData, pharmacyBillingData.getOperation(), orderDetails, practiceId, 0);
			logPharmacyBillingData(pharmacyBillingTransactionData, pharmacyBillingData, pharmacyBillingData.getOperation().name(), StringUtils.EMPTY);
			EcwLog.AppendExceptionToLog(e);
		}
		return false;
	}
	
	private String validatePharmacyBillingData(PharmacyBillingData pharmacyBillingData) {
		if (pharmacyBillingData.getMasterEncounterId() == 0) {
			return "EncounterId is required";
		}
		if (pharmacyBillingData.getOrderId() == 0) {
			return "OrderId is required";
		}
		if (pharmacyBillingData.getOperation() == null) {
			return "Operation is required";
		}
		/*for (DrugNdcDetails drugNdcDetails : pharmacyBillingData.getDrugNdcDetails()) {
			if (drugNdcDetails.getNdc() == null || drugNdcDetails.getNdc().isEmpty()) {
				return "Error occurred as NDC code not found";
			}
		}*/
		return StringUtils.EMPTY;
	}
	
	/**
	 * Gets the sub encounter data.
	 * Based on masterEncounterId and activeStartTime
	 *
	 * @param pharmacyBillingData the pharmacy billing data
	 * @param orderDetails the order details
	 * @return the sub encounter data
	 */
	private SubEncounterData getSubEncounterData(PharmacyBillingData pharmacyBillingData){
		try {
			Map<String, Integer> paramMap = new HashMap<>();
			String encounterDateStr = IPTzUtils.getStrFromDt(pharmacyBillingData.getEncounterDateTime(), IPTzUtils.DEFAULT_DB_DT_FMT);
			inpatientWeb.Global.ecw.ambulatory.json.JSONObject encounterData = appointmentService.getActiveApptTrailDetail(pharmacyBillingData.getMasterEncounterId(), encounterDateStr, false);
			paramMap.put("subEncounterId", encounterData.getInt("subEncId"));
		
			List<SubEncounterData> encDataList = appointmentService.getSubEncData(paramMap);
			if(encDataList == null || encDataList.isEmpty()){
				throw new NotFoundException("Error while fetching encoutner details");
			}
			return encDataList.get(0);
		} catch (Exception e) {
			throw new InternalServerException("Error while fetching encoutner details");
		}			
	}
	
	/**
	 * Gets the cost detail based on orderId and NDC code passed.
	 *
	 * @param pharmacyBillingData the pharmacy billing data
	 * @return the cost detail
	 */
	private TotalCostPerOrderId getCostDetail(PharmacyBillingData pharmacyBillingData){
		ArrayList<CostPerFormulary> ndcCodeList= new ArrayList<>(pharmacyBillingData.getDrugNdcDetails().size());
		CostPerFormulary costPerFormularyObj = null;
		for(DrugNdcDetails drugNdcDetails: pharmacyBillingData.getDrugNdcDetails()){
			costPerFormularyObj = new CostPerFormulary();
			costPerFormularyObj.setNdcCode(drugNdcDetails.getNdc());
			costPerFormularyObj.setDispense(drugNdcDetails.getDispense());
			
			ndcCodeList.add(costPerFormularyObj);
		}
		return formularySetupService.getTotalCostByOrderId(pharmacyBillingData.getOrderId(), ndcCodeList);
	}
	
	/**
	 * Gets the pharmacy billing details.
	 *
	 * @param orderId the order id
	 * @param ndcCode the ndc code
	 * @return the pharmacy billing details
	 */
	public PharmacyBillingDetail getPharmacyBillingDetails(long orderId, String ndcCode){
		PharmacyBillingDetail pharmacyBillingDetail = pharmacyBillingDAO.getPharmacyBillingDetail(orderId, ndcCode);
		if(pharmacyBillingDetail != null){
			return pharmacyBillingDetail; 
		}
		return new PharmacyBillingDetail();
	}
	
	private int savePharmacyBillingDetails(CostPerFormulary costPerFormulary, PharmacyBillingData pharmacyBillingData, Integer billingDataId, BillingDataTransaction.OPERATION operation, int successFlag,PharmacyLogEnum.SubModule moduleType) {
				
		PharmacyBillingDetail pharmacyBillingDetail = new PharmacyBillingDetail();
		pharmacyBillingDetail.setBillingDataId(billingDataId);
		if(costPerFormulary != null) {
			pharmacyBillingDetail.setFormularyId(costPerFormulary.getFormularyId());
			pharmacyBillingDetail.setNdcCode(costPerFormulary.getPpId());
			pharmacyBillingDetail.setDispense(costPerFormulary.getDispense());
			pharmacyBillingDetail.setAwup(costPerFormulary.getAwup());
			pharmacyBillingDetail.setFee(costPerFormulary.getCalculatedCostWithDispense());
			pharmacyBillingDetail.setPriceRuleId(costPerFormulary.getPriceRuleParam().getPriceRuleId());
			pharmacyBillingDetail.setCostToProcure(costPerFormulary.getCostToProcure());
			pharmacyBillingDetail.setCptItemId(costPerFormulary.getCptItemId());
		}		
		pharmacyBillingDetail.setOrderId(pharmacyBillingData.getOrderId());
		pharmacyBillingDetail.seteMARDrugAdminScheduleId(pharmacyBillingData.geteMARDrugAdminScheduleId());
		pharmacyBillingDetail.setAction(operation == null ? "" : operation.name());
		pharmacyBillingDetail.setSuccessFlag(successFlag);
		pharmacyBillingDetail.setModuleType(moduleType.getValue());
		if (operation == BillingDataTransaction.OPERATION.ADD) {
			pharmacyBillingDetail.setCreatedBy(pharmacyBillingData.getUserId());
			pharmacyBillingDetail.setCreatedDate(EMARUtil.convertTimeZoneToUTC(new Date(), pharmacyBillingData.getUserTimeZone()));
		}
		if (operation == BillingDataTransaction.OPERATION.UPDATE) {
			pharmacyBillingDetail.setModifiedBy(pharmacyBillingData.getUserId());
			pharmacyBillingDetail.setModifiedDate(EMARUtil.convertTimeZoneToUTC(new Date(), pharmacyBillingData.getUserTimeZone()));
		}
		// saving data with billingId 
		return savePharmacyBillingDetails(pharmacyBillingDetail);
	}
	
	/**
	 * Save pharmacy billing details.
	 *
	 * @param pharmacyBillingDetails the pharmacy billing details
	 * @return the int
	 */
	public int savePharmacyBillingDetails(PharmacyBillingDetail pharmacyBillingDetails) {
		try {
			if (pharmacyBillingDetails.getId() > 0) {
				return pharmacyBillingDAO.updatePharmacyBillingDetail(pharmacyBillingDetails);
			}
			return pharmacyBillingDAO.savePharmacyBillingDetail(pharmacyBillingDetails);
		} catch (Exception e) {
			EcwLog.AppendExceptionToLog(e);
		}
		return 0;
	}
	
	public boolean logPharmacyBillingData(PharmacyBillingTransactionData pharmacyBillingTransactionData, PharmacyBillingData pharmacyBillingData, String operation, String errorMessage) {
		PharmacyBillingLogData pharmacyBillingLogData = new PharmacyBillingLogData();
		pharmacyBillingLogData.setPharmacyBillingData(pharmacyBillingData);
		pharmacyBillingLogData.setPharmacyBillingTransactionData(pharmacyBillingTransactionData);
		pharmacyBillingLogData.setErrorMessage(errorMessage);
		pharmacyBillingLogData.setOrderId(pharmacyBillingData.getOrderId());
		pharmacyBillingLogData.seteMARDrugAdminScheduleId(pharmacyBillingData.geteMARDrugAdminScheduleId());
		String operationStr = operation == null ? StringUtils.EMPTY : operation;
		pharmacyBillingLogHandler.executeLog(pharmacyBillingData.getUserId(), pharmacyBillingData.getSubModule(), operationStr, pharmacyBillingLogData);
		return true;
	}
	
	/**
	 * Delete billing details by billing id.
	 *
	 * @param pharmacyBillingDetails the pharmacy billing details
	 * @return the int
	 */
	public int deleteBillingDetailsByBillingId(PharmacyBillingDetail pharmacyBillingDetails){
		return pharmacyBillingDAO.deletePharmacyBillingDetail(pharmacyBillingDetails, true);
	}
	
	/**
	 * Delete billing details by rx number id.
	 *
	 * @param pharmacyBillingDetails the pharmacy billing details
	 * @return the int
	 */
	public int deleteBillingDetailsByRxNumberId(PharmacyBillingDetail pharmacyBillingDetails){
		return pharmacyBillingDAO.deletePharmacyBillingDetail(pharmacyBillingDetails, false);
	}

	/**
	 * Gets the formulary setup service.
	 *
	 * @return the formulary setup service
	 */
	public FormularySetupService getFormularySetupService() {
		return formularySetupService;
	}

	/**
	 * Sets the formulary setup service.
	 *
	 * @param formularySetupService the new formulary setup service
	 */
	@Autowired
	public void setFormularySetupService(FormularySetupService formularySetupService) {
		this.formularySetupService = formularySetupService;
	}

	/**
	 * Gets the pharmacy billing DAO.
	 *
	 * @return the pharmacy billing DAO
	 */
	public PharmacyBillingDAO getPharmacyBillingDAO() {
		return pharmacyBillingDAO;
	}

	/**
	 * Sets the pharmacy billing DAO.
	 *
	 * @param pharmacyBillingDAO the new pharmacy billing DAO
	 */
	@Autowired
	public void setPharmacyBillingDAO(PharmacyBillingDAO pharmacyBillingDAO) {
		this.pharmacyBillingDAO = pharmacyBillingDAO;
	}

	/**
	 * Gets the appointment service.
	 *
	 * @return the appointment service
	 */
	public AppointmentService getAppointmentService() {
		return appointmentService;
	}
	
	/**
	 * Sets the appointment service.
	 *
	 * @param appointmentService the new appointment service
	 */
	@Autowired
	public void setAppointmentService(AppointmentService appointmentService) {
		this.appointmentService = appointmentService;
	}

	/**
	 * Gets the pharmacy billing log handler.
	 *
	 * @return the pharmacy billing log handler
	 */
	public PharmacyBillingLogHandler getPharmacyBillingLogHandler() {
		return pharmacyBillingLogHandler;
	}

	/**
	 * Sets the pharmacy billing log handler.
	 *
	 * @param pharmacyBillingLogHandler the new pharmacy billing log handler
	 */
	@Autowired
	public void setPharmacyBillingLogHandler(PharmacyBillingLogHandler pharmacyBillingLogHandler) {
		this.pharmacyBillingLogHandler = pharmacyBillingLogHandler;
	}
	
	
	
}