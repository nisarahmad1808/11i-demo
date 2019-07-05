package inpatientWeb.pharmacy.billingDataFormulary.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecw.dao.EcwLog;

import inpatientWeb.Auth.exception.InvalidParameterException;
import inpatientWeb.pharmacy.billingDataFormulary.beans.FormularyChargeDetails;
import inpatientWeb.pharmacy.billingDataFormulary.dao.FormularyChargeDetailDao;
import inpatientWeb.utils.Util;

@Service
public class FormularyChargeDetailService {

	@Autowired
	public FormularyChargeDetailDao formularyChargeDetailDao;
	
	public Map<String, Object> getChargeDetailsForFormularyItemId(Map<String, Object> requestMap, int UserId) throws RuntimeException {			
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try 
		{
			if(requestMap == null || (requestMap.isEmpty())){
				throw new InvalidParameterException("Error: Request body is empty for get billing data api.");
			}
			if(Util.getIntValue(requestMap, "formularyId") <=0) {				
				throw new InvalidParameterException("Could not calculate Units and Cost as Formulary Id is missing or Invalid");				
			}
			if(Util.getIntValue(requestMap, "orderType") <=0) {
				throw new InvalidParameterException("Could not calculate Units and Cost as Order Type missing or Invalid");
			}
			if(Util.getStrValue(requestMap, "orderDispense").length() <=0) {
				throw new InvalidParameterException("Could not calculate Units and Cost as Order Dispense is missing or Invalid");
			}
			if(Util.getStrValue(requestMap, "orderDispenseUOM").length() <=0) {
				throw new InvalidParameterException("Could not calculate Units and Cost as Order Dispense UOM is missing or Invalid");
			}
			if(Util.getStrValue(requestMap, "orderDose").length() <=0) {
				throw new InvalidParameterException("Could not calculate Units and Cost as Order Dose is missing or Invalid");
			}
			if(Util.getStrValue(requestMap, "orderDoseUOM").length() <=0) {
				throw new InvalidParameterException("Could not calculate Units and Cost as Order Dose UOM is missing or Invalid");
			}
			if(Util.getIntValue(requestMap, "facilityId") <=0) {
				throw new InvalidParameterException("Could not calculate Units and Cost as Invalid Facility Id, Drug is not setup for this facility");
			}			
			responseMap.put("Status","success");			
			ArrayList<FormularyChargeDetails> formularyChargeDetails = formularyChargeDetailDao.getChargeDetailsForFormularyItemId(requestMap, UserId);
			if(formularyChargeDetails.size() > 0) {
				for(int i = 0; i < formularyChargeDetails.size(); i++) {
					responseMap.put("totalCost", formularyChargeDetails.get(i).getTotalCostCalculatedWithChargeType());
					responseMap.put("unit", formularyChargeDetails.get(i).getToTalHCPCSUnit());
					responseMap.put("unitCost", formularyChargeDetails.get(i).getUnitCostCalculatedWithChargeType());
					responseMap.put("cptItemId", formularyChargeDetails.get(i).getCptItemId());
					responseMap.put("chargeCode", Util.ifNullEmpty(formularyChargeDetails.get(i).getChargeCode(), ""));
				}
				formularyChargeDetailDao.createChargeDetailsForFormularyLog(requestMap, responseMap, "success", "Anesthesia", "success", UserId);
			}
		}catch(RuntimeException ex) {
			EcwLog.AppendExceptionToLog(ex);
			formularyChargeDetailDao.createChargeDetailsForFormularyLog(requestMap, responseMap, ex.getMessage(), "Anesthesia", "Error", UserId);
			responseMap.put("Status","Failed");
			responseMap.put("Message","Error :"+ex.getMessage());
		}
			
		return responseMap;
	}
}
