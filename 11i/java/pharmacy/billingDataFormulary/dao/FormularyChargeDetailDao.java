package inpatientWeb.pharmacy.billingDataFormulary.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import inpatientWeb.Auth.exception.InvalidParameterException;
import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;
import inpatientWeb.Global.medicationhelper.util.RxUtil;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.FormularySetupDao;
import inpatientWeb.pharmacy.billingDataFormulary.beans.FormularyChargeDetails;
import inpatientWeb.pharmacy.billingDataFormulary.util.PharmacyChargeDetailQueryUtil;
import inpatientWeb.pharmacy.daoImpl.WorkQueueDAOImpl;
import inpatientWeb.utils.IPTzUtils;
import inpatientWeb.utils.Util;

@Repository
@Scope("prototype")
@Lazy
public class FormularyChargeDetailDao {
	
	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Autowired
	FormularySetupDao formularySetupDAO;
	
	@Autowired
	public WorkQueueDAOImpl workQueueDAOImpl;
	
	public ArrayList<FormularyChargeDetails> getChargeDetailsForFormularyItemId(Map<String, Object> requestMap, int UserId){

		ArrayList<FormularyChargeDetails> result = new ArrayList<FormularyChargeDetails>();
		final Map<String,Object> responseMap = new HashMap<String,Object>();
		try{
			Map<String,Object> paramMap = new HashMap<String,Object>();		
			final HashMap<String,Object> paramMapHCPCS = new HashMap<String,Object>();
			paramMap.put("formularyId", Util.getIntValue(requestMap, "formularyId"));
			paramMap.put("ordertype", Util.getIntValue(requestMap, "orderType"));
			paramMap.put("facilityid", Util.getIntValue(requestMap, "facilityId"));
			final int facilityId = Util.getIntValue(requestMap, "facilityId");			
			final String OrderDose = Util.getStrValue(requestMap, "orderDose");
			final String orderDoseUOM = Util.getStrValue(requestMap, "orderDoseUOM");
			final double OrderDispense = Util.getDoubleValue(requestMap, "orderDispense");
			final String orderDispenseUOM = Util.getStrValue(requestMap, "orderDispenseUOM");
			final String strOrderDispense = Util.getStrValue(requestMap, "orderDispense");			
			result = (ArrayList<FormularyChargeDetails>) namedParameterJdbcTemplate.query(PharmacyChargeDetailQueryUtil.getChargeDetaiForFormularylQuery().toString(), paramMap, new RowMapper<FormularyChargeDetails>(){
			public FormularyChargeDetails mapRow(ResultSet rs, int arg1) throws SQLException {
					FormularyChargeDetails formularyChargeDetails = new FormularyChargeDetails();					
					int chargeTypeId = rs.getInt("chargeTypeId");
					double awup = rs.getDouble("awup");					
					formularyChargeDetails.setChargeTypeId(chargeTypeId);
					formularyChargeDetails.setChargeCode(rs.getString("chargeCode"));
					formularyChargeDetails.setCptItemId(rs.getInt("itemid"));
					formularyChargeDetails.setAwup(awup);	
					formularyChargeDetails.setPackSizeUnitcode(rs.getString("packsizeunitcode"));
					formularyChargeDetails.setPackSize(rs.getString("packsize"));
					formularyChargeDetails.setDrugTypeBulk(rs.getInt("isdrugtypebulk_formulary") == 1);					
					formularyChargeDetails.setCalculate(rs.getInt("isCalculate") == 1);
					if(!WorkQueueDAOImpl.convertUOM(rs.getString("formDispenseUOM")).equalsIgnoreCase(WorkQueueDAOImpl.convertUOM(orderDispenseUOM))) {
						responseMap.put("orderDispenseUOM", orderDispenseUOM);
						responseMap.put("formularuDispenseUOM", rs.getString("formDispenseUOM"));
						throw new InvalidParameterException("Could not calculate Units and Cost as Order Dispense UOM dose not match Formulary Dispense UOM");
					}
					if(!WorkQueueDAOImpl.convertUOM(rs.getString("formDoseUOM")).equalsIgnoreCase(WorkQueueDAOImpl.convertUOM(orderDoseUOM))) {
						responseMap.put("orderDoseUOM", orderDoseUOM);
						responseMap.put("formularuDoseUOM", rs.getString("formDoseUOM"));
						throw new InvalidParameterException("Could not calculate Units and Cost as Order Dose UOM dose not match Formulary Dose UOM");
					}
					if(facilityId != rs.getInt("facilityid")) {
						responseMap.put("orderFacilityId", facilityId);
						responseMap.put("formularuFacilityid", rs.getInt("facilityid"));
						throw new InvalidParameterException("Could not calculate Units and Cost as formulary is not configured for provided facility Id");
					}
					paramMapHCPCS.put("orderDose", OrderDose);
					paramMapHCPCS.put("formularyDose", rs.getString("formDoseSize"));
					paramMapHCPCS.put("formularyDoseUOM", rs.getString("formDoseUOM"));
					paramMapHCPCS.put("orderDispense", strOrderDispense);
					paramMapHCPCS.put("packSize", rs.getString("packsize"));				
					paramMapHCPCS.put("isCalcuted", formularyChargeDetails.isCalculate());
					paramMapHCPCS.put("isBulk", formularyChargeDetails.isDrugTypeBulk());
					paramMapHCPCS.put("hcpcsCodeRange", rs.getString("HCPCSCodeRange"));
					paramMapHCPCS.put("hcpcsCodeType", rs.getString("HCPCSCodeType"));
					paramMapHCPCS.put("hcpcsCodeUnit", rs.getString("HCPCSCodeUnit"));
					double totalhcpcsUnit = workQueueDAOImpl.getTotalHCPCSUnitCalculated(paramMapHCPCS);
					formularyChargeDetails.setToTalHCPCSUnit(totalhcpcsUnit);
					if(!WorkQueueDAOImpl.convertUOM(rs.getString("HCPCSCodeType")).equalsIgnoreCase(WorkQueueDAOImpl.convertUOM(orderDoseUOM))) {
						EcwLog.AppendToLog("FormularyChargeDetailDao: Order Dose UOM dose not match HCPCS UOM");
					}
					double dispenseQty = Math.ceil(OrderDispense / Double.parseDouble(rs.getString("formDispenseSize")));				
					if(!formularyChargeDetails.isNonBillable()){
						double dispense = Double.parseDouble("".equals(strOrderDispense) ? "1" : String.valueOf(dispenseQty));
						if(formularyChargeDetails.isDrugTypeBulk() && WorkQueueDAOImpl.convertUOM(rs.getString("formDispenseUOM")).equalsIgnoreCase(WorkQueueDAOImpl.convertUOM(rs.getString("packsizeunitcode")))) {
							String strPackSize =  RxUtil.sanitizeStringAsDouble(rs.getString("packsize"), true);
							double orderDispense = Double.parseDouble(strOrderDispense);	
							double packSizeUnit = Math.ceil(orderDispense / Double.parseDouble(strPackSize));
							dispense = (Double.parseDouble(strPackSize) * packSizeUnit);							
						} else {
							EcwLog.AppendToLog("FormularyChargeDetailDao:Cost will be calculated based on when user have dispense not awup associated product");
						}						
						double costToProc = rs.getDouble("cost_to_proc");

						double costCalculatedWithChargeType = formularySetupDAO.getCostCalculatedWithChargeType(chargeTypeId, awup, costToProc, dispense, facilityId);
						if(costCalculatedWithChargeType == 0.0) {
							EcwLog.AppendToLog("FormularyChargeDetailDao:Cost not calculate because  of either formulary setup associated product not configure or pricing rule engine not setup");
						}
						formularyChargeDetails.setCostCalculatedWithChargeType(costCalculatedWithChargeType);						
						formularyChargeDetails.setTotalCostCalculatedWithChargeType(costCalculatedWithChargeType);
						double costCalculatedPerUnitWithChargeType = 0;
						if(formularyChargeDetails.getToTalHCPCSUnit()  > 0) {
						  costCalculatedPerUnitWithChargeType = costCalculatedWithChargeType / totalhcpcsUnit;	
						}
						formularyChargeDetails.setUnitCostCalculatedWithChargeType(costCalculatedPerUnitWithChargeType);
					}

					return formularyChargeDetails;
				}

			});
		} catch (DataAccessException e) {
			createChargeDetailsForFormularyLog(requestMap, responseMap, e.getMessage(), "Anesthesia", "Error", UserId);
			EcwLog.AppendExceptionToLog(e);
		}
		return result;

	}
	
public boolean createChargeDetailsForFormularyLog(Map<String, Object> requestMap, Map<String, Object> responseMap, String errorMessge, String moduleName, String strStatus, int UserId){		
		if(UserId <= 0){
			throw new InvalidParameterException("Invalid user Id");
		}
		
		Map<String,Object> paramMap = new HashMap<String,Object>();
		boolean result = false;

		StringBuffer strSQL = new StringBuffer("INSERT INTO ip_formulary_charge_detail_exception_logs (formularyId, trUserId, timeStamp, moduleName, logInputData, logOutPutData, staus, errorMessage) values (:formularyId, :trUserId, :timeStamp, :moduleName, :logInputData, :logOutPutData, :status, :errorMessage)");

		try{
			String currentDateTime = IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT);

			paramMap.put("formularyId", Util.getIntValue(requestMap, "formularyId"));
			paramMap.put("trUserId", UserId);
			paramMap.put("timeStamp", currentDateTime);
			paramMap.put("moduleName", moduleName);	
			paramMap.put("logInputData", requestMap.toString());
			paramMap.put("logOutPutData", responseMap.toString());
			paramMap.put("status", strStatus);
			paramMap.put("errorMessage", errorMessge);				

			namedParameterJdbcTemplate.update(strSQL.toString(), paramMap);

			result = true;

		} catch ( DataAccessException e ){
			EcwLog.AppendExceptionToLog(e);
		}
		return result;			
	}
}
