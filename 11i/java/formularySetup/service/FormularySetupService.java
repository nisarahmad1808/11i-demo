package inpatientWeb.admin.pharmacySettings.formularySetup.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import inpatientWeb.Auth.exception.InvalidParameterException;
import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;
import inpatientWeb.Global.ecw.ambulatory.com.ecw.security.DataValidation;
import inpatientWeb.Global.ecw.ambulatory.json.JSONException;
import inpatientWeb.Global.ecw.ambulatory.json.JSONObject;
import inpatientWeb.Global.ecw.auditlogs.AuditLogService;
import inpatientWeb.Global.medicationhelper.msclinical.model.AHFSClassification;
import inpatientWeb.Global.rxOrderRadixTree.RxOrderTreeServices;
import inpatientWeb.Global.service.EcwAppContext;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.FormularySearchParam;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.FormularySetupDao;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.Template;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.TemplateForAssoProducts;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.TemplateForAssoProductsWithLotDetails;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.TemplateForBillingInfo;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.TemplateForCommonSetting;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.TemplateForDrug;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.TemplateForFacilities;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.TemplateForFormularyDrugRoutes;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.TemplateForIVRate;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.TemplateForNotes;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.TemplateForOTFrequency;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.TemplateForOTRoutes;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.TemplateForOTS;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.TemplateForPreVitals;
import inpatientWeb.admin.pharmacySettings.formularySetup.modal.CostPerFormulary;
import inpatientWeb.admin.pharmacySettings.formularySetup.modal.DispenseStockAreaQuantityMappingModal;
import inpatientWeb.admin.pharmacySettings.formularySetup.modal.TotalCostPerOrderId;
import inpatientWeb.admin.pharmacySettings.formularySetup.util.CustomMedInputs;
import inpatientWeb.admin.pharmacySettings.formularySetup.util.SchemaDetails;
import inpatientWeb.admin.pharmacySettings.pharmacyHelpers.PharmacyHelper;
import inpatientWeb.admin.pharmacySettings.pharmacyHelpers.PharmacySecurityConstants;
import inpatientWeb.admin.pharmacySettings.pharmacyUtility.model.PriceRuleParam;
import inpatientWeb.admin.usermanagement.securitysettings.annotation.CheckForUserAccess;
import inpatientWeb.cpoe.admin.referencedata.service.ReferenceDataService;
import inpatientWeb.cpoe.orders.medication.util.MedicationDosageReqParam;
import inpatientWeb.eMAR.modal.LotSearchData;
import inpatientWeb.pharmacy.drugdb.model.DrugClassification;
import inpatientWeb.pharmacy.drugdb.model.DrugClassificationRequest;
import inpatientWeb.pharmacy.drugdb.model.DrugFormulation;
import inpatientWeb.pharmacy.drugdb.model.DrugRequest;
import inpatientWeb.pharmacy.drugdb.model.DrugRoute;
import inpatientWeb.pharmacy.drugdb.model.Unit;
import inpatientWeb.pharmacy.drugdb.service.DrugDbService;
import inpatientWeb.pharmacy.interaction.constants.InteractionConstants.INTERACTION_FORMAT;
import inpatientWeb.utils.IPTzUtils;
import inpatientWeb.utils.StatusMap;
import inpatientWeb.utils.Util;

@Service
@Scope("prototype")
@Lazy
public class FormularySetupService {

	@Autowired
	public FormularySetupDao forSetupDao;

	@Autowired
	private PharmacyHelper pharmacyHelper;

	@Autowired
	private AuditLogService auditLogService;

	@Autowired
	private DrugDbService drugDbService;
	
	private ReferenceDataService refDataSvc;

	@Autowired
	public void setReferenceDataService(ReferenceDataService ref) {
		this.refDataSvc = ref;
	}

	private static final String INTERACTION_LEVEL_MODIFIED = "interactionlevelModified";
	private static final String FORMULARY_ID = "formularyId";
	private static final String DRUG_DISPENSE_CODE = "drugdispenseCode";
	private static final String USER_ID = "trUserId";
	private static final String FORMULARY_GPI = "formularyGPI";
	private static final String INTERACTING_GPI = "interactingGPI";
	private static final String MESSAGE = "message";
	private static final String MODIFIED_BY = "modifiedBy";
	private static final String MODIFIED_DATE = "modifiedDate";
	private static final String ROUTED_GENERIC_ITEMID = "routedGenericItemId";
	private static final int NUM_100 = 100;
	private static final String FORMULARY_MODULE_TAG = "--formularyId [";

	public List<Template> getFormularySetupDrugList(FormularySearchParam fSearchParamObj) {
		if(Util.isNullEmpty(fSearchParamObj.getStatus())){
			throw new InvalidParameterException("Invalid Status");
		}
		if(fSearchParamObj.getRecordsPerPage() <= 0){
			throw new InvalidParameterException("Invalid recordsPerPage");
		}
		if(fSearchParamObj.getSelectedPage()<=0){
			throw new InvalidParameterException("Invalid SelectedPage");
		}
		return forSetupDao.getFormularySetupDrugList(fSearchParamObj);
	}

	public List<Template> getFormularySetupDrugListForComponent(FormularySearchParam fSearchParamObj) {
		if(Util.isNullEmpty(fSearchParamObj.getSearchBy())){
			throw new InvalidParameterException("Invalid Search By");
		}
		if(fSearchParamObj.getRecordsPerPage() <= 0){
			throw new InvalidParameterException("Invalid recordsPerPage");
		}
		if(fSearchParamObj.getSelectedPage()<=0){
			throw new InvalidParameterException("Invalid SelectedPage");
		}
		return forSetupDao.getFormularySetupDrugListForComponent(fSearchParamObj);
	}

	public List<TemplateForDrug> getDrugDetails(String drugId) {
		if (Util.isNullEmpty(drugId)) {
			throw new InvalidParameterException("Invalid drugId");
		}
		if ("-1".equals(Util.trimStr(drugId))) {
			throw new InvalidParameterException("Invalid drugId");
		}
		if ("0".equals(Util.trimStr(drugId))) {
			throw new InvalidParameterException("Invalid drugId");
		}
		return forSetupDao.getDrugDetails(drugId);
	}

	public List<TemplateForAssoProducts> getAssociatedProductDetails(String drugId) {
		if (Util.isNullEmpty(drugId)) {
			throw new InvalidParameterException("Invalid drugId");
		}
		if ("-1".equals(Util.trimStr(drugId))) {
			throw new InvalidParameterException("Invalid drugId");
		}
		if ("0".equals(Util.trimStr(drugId))) {
			throw new InvalidParameterException("Invalid drugId");
		}
		return forSetupDao.getAssociatedProductDetails(drugId);
	}

	public Integer getAssoProductDetailsLotDetailsCount(LotSearchData lotSearchData) {
		return forSetupDao.getAssoProductDetailsLotDetailsCount(lotSearchData);
	}

	public List<TemplateForAssoProductsWithLotDetails> getAssoProductDetailsLotDetails(LotSearchData lotSearchData) {
		return forSetupDao.getAssoProductDetailsLotDetails(lotSearchData);
	}

	public List<TemplateForAssoProducts> getAssociatedProductDetailsByNDC(String strNDC) {
		if (Util.isNullEmpty(strNDC)) {
			throw new InvalidParameterException("Invalid NDC");
		}
		return forSetupDao.getAssociatedProductDetailsByNDC(strNDC);
	}

	public List<TemplateForPreVitals> getPreRequisiteVitalsDetails(int nformularyId, String routedGenericItemId) {
		if (nformularyId <= 0) {
			throw new InvalidParameterException("Invalid formularyId");
		}
		if (Util.isNullEmpty(routedGenericItemId)) {
			throw new InvalidParameterException("Invalid routedGenericItemId");
		}
		return forSetupDao.getPreRequisiteVitalsDetails(nformularyId, routedGenericItemId);
	}

	public List<TemplateForPreVitals> getPreRequisiteVitalsDetails(Long nformularyId) {
		if (nformularyId <= 0) {
			throw new InvalidParameterException("Invalid formularyId");
		}
		return forSetupDao.getPreRequisiteVitalsDetails(nformularyId);
	}

	public List<TemplateForOTS> getOrderTypeSetupDetails(String drugId){
		if (Util.isNullEmpty(drugId)) {
			throw new InvalidParameterException("Invalid drugId");
		}
		if ("-1".equals(Util.trimStr(drugId))) {
			throw new InvalidParameterException("Invalid drugId");
		}
		if ("0".equals(Util.trimStr(drugId))) {
			throw new InvalidParameterException("Invalid drugId");
		}
		return forSetupDao.getOrderTypeSetupDetails(drugId);
	}

	public List<TemplateForOTS> getSlidingScale(String drugId) {
		if (Util.isNullEmpty(drugId)) {
			throw new InvalidParameterException("Invalid drugId");
		}
		if ("-1".equals(Util.trimStr(drugId))) {
			throw new InvalidParameterException("Invalid drugId");
		}
		if ("0".equals(Util.trimStr(drugId))) {
			throw new InvalidParameterException("Invalid drugId");
		}
		return forSetupDao.getSlidingScale(drugId);
	}

	public List<TemplateForOTS> getIVAndComplexOrderDiluentData() {
		return forSetupDao.getIVAndComplexOrderDiluentData();
	}

	public List<TemplateForFacilities> getFacilitiesList(int trUserId) {
		//truserid>0
		if(trUserId<=0){
			throw new InvalidParameterException("Invalid userId");
		}
		return forSetupDao.getFacilitiesList(trUserId);
	}

	public List<TemplateForFormularyDrugRoutes> getFormularyDrugRoutesList() {
		return forSetupDao.getFormularyDrugRoutesList();
	}

	public LinkedHashMap<String, Object> checkForDrugInUse(CustomMedInputs obj, int nUserId) {
		if (obj == null) {
			throw new InvalidParameterException("Invalid CustomMedInputs");
		}
		if (nUserId <= 0) {
			throw new InvalidParameterException("Invalid UserID");
		}
		String jsonDrugData = Util.trimStr(obj.getJsonDrugData());
		if (Util.isNullEmpty(jsonDrugData)) {
			throw new InvalidParameterException("Invalid jsonDrugData");
		}
		LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
		try {
		
			pharmacyHelper.generatePharmacyLog("checkForDrugInUse is called");
			pharmacyHelper.generatePharmacyLog("jsonDrugData start [");
			pharmacyHelper.generatePharmacyLog("" + jsonDrugData + "");
			pharmacyHelper.generatePharmacyLog("]jsonDrugData end");

			Map<String, String> hmFormularyData = checkIfExistInDrugFormulary(jsonDrugData);
			responseMap.put("ItemsData", hmFormularyData);
			responseMap.put("Message", "success");
		} catch (RuntimeException | IOException | JSONException ex) {
			EcwLog.AppendExceptionToLog(ex);
			responseMap.put("Message", "Error in checkForDrugInUse!!!");
		}
		return responseMap;
	}

	public LinkedHashMap<String, Object> checkForDuplicateDrugAlias(String drugAlias, String routedGenericItemId,
			int nUserId) {
		if (Util.isNullEmpty(drugAlias)) {
			throw new InvalidParameterException("Invalid DrugAlias");
		}
		if (Util.isNullEmpty(routedGenericItemId)) {
			throw new InvalidParameterException("Invalid routedGenericItemId");
		}
		if (nUserId <= 0) {
			throw new InvalidParameterException("Invalid userId");
		}
		LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
		try {
			pharmacyHelper.generatePharmacyLog("checkForDuplicateDrugAlias is called");
			pharmacyHelper.generatePharmacyLog("drugAlias start [");
			pharmacyHelper.generatePharmacyLog("" + drugAlias + "");
			pharmacyHelper.generatePharmacyLog("]drugAlias end");

			Map<String, String> hmFormularyData = null;
			hmFormularyData = new HashMap<>();
			hmFormularyData.put("isDataInUse", forSetupDao.checkForDuplicateDrugAlias(drugAlias, routedGenericItemId));
			responseMap.put("ItemsData", hmFormularyData);
			responseMap.put("Message", "success");
		} catch (RuntimeException ex) {
			EcwLog.AppendExceptionToLog(ex);
			responseMap.put("Message", "Error in checkForDuplicateDrugAlias!!!");
	}
		return responseMap;
	}

	public LinkedHashMap<String, Object> insertUpdateCustomDrugDetails(CustomMedInputs obj, String action,
			int nUserId) {
		if (Util.isNullEmpty(action)) {
			throw new InvalidParameterException("Invalid action");
		}
		if (nUserId <= 0) {
			throw new InvalidParameterException("Invalid userId");
		}
		LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
		Map<String, Object> responseDrugDispenseObject = null;
		StatusMap statusMapForDuplicateDispenseCode = null;
		boolean isDataInUse = false;
		Map<String,String> resultMap = null;
		int nDrugItemId = 0;
		try {
			String jsonDrugData = Util.trimStr(obj.getJsonDrugData());
			String orderTypeSetup = Util.trimStr(obj.getOrderTypeSetup());
			String ingredientList = Util.trimStr(obj.getIngredientList());
			String routeList = Util.trimStr(obj.getRouteList());
			String dosingRouteList = Util.trimStr(obj.getDosingRouteList());
			String frequencyList = Util.trimStr(obj.getFrequencyList());
			String formulationList = Util.trimStr(obj.getFormulationList());
			String selectedFacilitiesList = Util.trimStr(obj.getSelectedFacilitiesList());
			String selectedServiceTypeList = Util.trimStr(obj.getSelectedServiceTypeList());
			String strVisData = Util.trimStr(obj.getVisData());
			String associatedProducts = Util.trimStr(obj.getAssociatedProducts());
			String ahfsClassList = Util.trimStr(obj.getAhfsClassList());
			String drugClassList = Util.trimStr(obj.getDrugClassList());
			String isAddDuplicate = Util.trimStr(obj.getIsAddDuplicate());
			String dispenseUOMArr = Util.trimStr(obj.getDispenseUOMArr());
			String jsonNotesObj = Util.trimStr(obj.getJsonNotesObj());
			String isContinue = Util.trimStr(obj.getIsContinue());

			boolean bAddDuplicate = false;
			if ("true".equalsIgnoreCase(isAddDuplicate)) {
				bAddDuplicate = true;
			}
			if (null != obj && !"".equals(obj.getJsonDrugDispenseObj())) {
				responseDrugDispenseObject = new ObjectMapper().readValue(Util.trimStr(obj.getJsonDrugDispenseObj()),
						HashMap.class);
			}
			int formularyId = -1;

			PharmacyHelper.generatePharmacyLog("insertUpdateForDrugDetails is called");
			PharmacyHelper.generatePharmacyLog("jsonDrugData start [");
			PharmacyHelper.generatePharmacyLog("" + jsonDrugData + "");
			PharmacyHelper.generatePharmacyLog("]jsonDrugData end");

			Map<String, String> hmFormularyData = checkIfExistInDrugFormulary(jsonDrugData);
			if ("true".equalsIgnoreCase(Util.getStrValue(hmFormularyData, "isDataInUse", "false"))
					&& !"true".equalsIgnoreCase(isContinue)) {
				isDataInUse = true;
			}
			if (null != responseDrugDispenseObject && !responseDrugDispenseObject.isEmpty()) {
				statusMapForDuplicateDispenseCode = checkDispenseCodeInFormularyDetail(responseDrugDispenseObject);
			}
			PharmacyHelper.generatePharmacyLog("--action [" + action + "]-hmFormularyData.size() ["
					+ hmFormularyData.size() + "]--bAddDuplicate [" + bAddDuplicate + "]-----");
			// if hashmap is empty then this will be fresh entry
			// if action is modify then updating existing records
			// if bAddDuplicate is true then allow to insert duplicate
			if (!isDataInUse && ("modify".equalsIgnoreCase(action) || hmFormularyData.size() == 0 || bAddDuplicate)) {
				resultMap = insertUpdateDrugDetails(jsonDrugData, action, nUserId);
				if(resultMap!=null && !"".equals(Util.trimStr(resultMap.get("formularyId"))))
				{
					formularyId = Integer.parseInt(Util.trimStr(resultMap.get("formularyId")));
				}
				if(resultMap!=null && !"".equals(Util.trimStr(resultMap.get("itemId"))))
				{
					nDrugItemId = Integer.parseInt(Util.trimStr(resultMap.get("itemId")));
				}
				
				PharmacyHelper.generatePharmacyLog(
						FORMULARY_MODULE_TAG + formularyId + "]-dosingRouteList [" + dosingRouteList + "]");
				
				if(!Util.isNullEmpty(dosingRouteList)){
					insertUpdateRouteListExternal(formularyId, dosingRouteList, nUserId);
				}
				PharmacyHelper.generatePharmacyLog(FORMULARY_MODULE_TAG + formularyId + "]-frequencyList [" + frequencyList + "]");
				
				if(!Util.isNullEmpty(frequencyList)){
					insertUpdateFrequencyListExternal(formularyId, frequencyList, nUserId);
				}
				PharmacyHelper.generatePharmacyLog(FORMULARY_MODULE_TAG + formularyId + "]-formulationList [" + formulationList + "]");
				
				if(!Util.isNullEmpty(formulationList)){
					insertUpdateFormulationListExternal(formularyId, formulationList, nUserId);
				}
//				PharmacyHelper.generatePharmacyLog(FORMULARY_MODULE_TAG + formularyId + "]-strVisData [" + strVisData + "]");
//				if(!Util.isNullEmpty(strVisData)){ //will come in future release
////					insertUpdateFormularyImmunizationData(formularyId, strVisData, nUserId);	
//				}

				if (!"".equals(orderTypeSetup) && "".equals(routeList) && "".equals(frequencyList)
						&& "".equals(formulationList)) {
					insertUpdateOrderTypeSetup(formularyId, orderTypeSetup, nUserId);
				}
				PharmacyHelper.generatePharmacyLog(FORMULARY_MODULE_TAG + formularyId + "]-associatedProducts [" + associatedProducts + "]");
				
				if(!Util.isNullEmpty(associatedProducts)){
					insertUpdateAssociatedProducts(formularyId, associatedProducts, nUserId);
				}
				PharmacyHelper.generatePharmacyLog(FORMULARY_MODULE_TAG + formularyId + "]-drugClassList [" + drugClassList + "]");
				PharmacyHelper.generatePharmacyLog(FORMULARY_MODULE_TAG + formularyId + "]-ingredientList [" + ingredientList + "]");
				
				if(!"".equals(ingredientList))
				{
					updateIngredientDetails(ingredientList, formularyId);
				}
				if(!Util.isNullEmpty(drugClassList)){
					insertUpdateDrugClassification(drugClassList, formularyId, nUserId);
				}
				PharmacyHelper.generatePharmacyLog(FORMULARY_MODULE_TAG + formularyId + "]-ahfsClassList [" + ahfsClassList + "]");
				
				if(!Util.isNullEmpty(ahfsClassList)){
					insertUpdateAhfsClassification(ahfsClassList, formularyId, nUserId);
				}
				PharmacyHelper.generatePharmacyLog(FORMULARY_MODULE_TAG + formularyId + "]-dispenseUOMArr [" + dispenseUOMArr + "]");
				
				if(!Util.isNullEmpty(dispenseUOMArr)){
					insertUpdateDispenseUOM(dispenseUOMArr, formularyId, nUserId);
				}
				PharmacyHelper.generatePharmacyLog(FORMULARY_MODULE_TAG + formularyId + "]-selectedFacilitiesList ["+ selectedFacilitiesList + "]");
				
				insertUpdateFacilitiesList(formularyId, selectedFacilitiesList, nUserId);
				PharmacyHelper.generatePharmacyLog(FORMULARY_MODULE_TAG + formularyId + "]-selectedServiceTypeList ["
						+ selectedServiceTypeList + "]");
				
				insertUpdateServiceTypeList(formularyId, selectedServiceTypeList, nUserId);
				
				if(!Util.isNullEmpty(jsonNotesObj)){
					insertUpdateNotesData(formularyId, jsonNotesObj, nUserId);
				}
				if (null != responseDrugDispenseObject && !responseDrugDispenseObject.isEmpty()
						&& null != statusMapForDuplicateDispenseCode
						&& "success".equals(statusMapForDuplicateDispenseCode.get("status"))) {
					saveUpdateDispenseDetails(responseDrugDispenseObject);
				}
				if(nDrugItemId > 0)
				{
					TemplateForDrug drug = new ObjectMapper().readValue(jsonDrugData, TemplateForDrug.class);
					RxOrderTreeServices rxOrderTreeService = (RxOrderTreeServices) EcwAppContext.getObject(RxOrderTreeServices.class);
					if(drug == null){
						throw new InvalidParameterException("Error parsing json into TemplateForDrug"); 
					}
		   			rxOrderTreeService.addOrUpdateNewDrugInFormulary(nDrugItemId);
				}
			}
			PharmacyHelper.generatePharmacyLog("-generated formulary id [" + formularyId + "]----");
			responseMap.put("Number", formularyId);
			responseMap.put("ItemsData", hmFormularyData);
			responseMap.put("Message", "success");
			responseMap.put("statusForDrugDispenseCode", statusMapForDuplicateDispenseCode);
		}catch (RuntimeException | IOException | JSONException ex) {
			EcwLog.AppendExceptionToLog(ex);
			responseMap.put("Message", "Error while inserting/updating custom medication into formulary!!!");
		}
		return responseMap;
	}

	private Map<String,String> insertUpdateDrugDetails(String jsonDrugData, String action, int nTrUserId) {
		if (nTrUserId <= 0) {
			throw new InvalidParameterException("Invalid UserID");
		}
		if (jsonDrugData == null || (jsonDrugData.isEmpty())) {
			throw new InvalidParameterException("Request body is empty");
		}
		if (Util.isNullEmpty(action)){
			throw new InvalidParameterException("Invalid Action");
		}
		Map<String,String> resultMap = null;
		try {
			resultMap = forSetupDao.insertUpdateDrugDetails(jsonDrugData, action, nTrUserId);
			String actionToLog = "Created";
			if ("modify".equalsIgnoreCase(action)) {
				actionToLog = "Modified";
			}
			JSONObject json = new JSONObject(jsonDrugData);
			auditLogService.logEvent(nTrUserId, "FormularySetupModule", actionToLog, json,
					"Specific Setting -> Product Details");

		} catch (RuntimeException | IOException | JSONException ex) {
			EcwLog.AppendExceptionToLog(ex);
		}
		return resultMap;
	}

	public void insertUpdateDrugClassification(String jsonDrugClassiData, int nFormularyId, int nTrUserId) {
		if(nFormularyId<=0){
			throw new InvalidParameterException("Invalid formularyId");
		}
		if(nTrUserId<=0){
			throw new InvalidParameterException("Invalid UserId");
		}
		if(Util.isNullEmpty(jsonDrugClassiData)){
			throw new InvalidParameterException("Invalid jsonDrugClassiData");
		}
		forSetupDao.insertUpdateDrugClassification(jsonDrugClassiData, nFormularyId, nTrUserId);
	}

	public void insertUpdateAhfsClassification(String jsonAhfsClassiData, int nFormularyId, int nTrUserId) {
		if(nFormularyId<=0){
			throw new InvalidParameterException("Invalid formularyId");
		}
		if(nTrUserId<=0){
			throw new InvalidParameterException("Invalid UserId");
		}
		if(Util.isNullEmpty(jsonAhfsClassiData)){
			throw new InvalidParameterException("Invalid jsonAhfsClassiData");
		}
		forSetupDao.insertUpdateAhfsClassification(jsonAhfsClassiData, nFormularyId, nTrUserId);
	}

	public void updateIngredientDetails(String jsonIngredientData, int nFormularyId) {
		if(Util.isNullEmpty(jsonIngredientData)){
			throw new InvalidParameterException("Invalid jsonIngredientData");
		}
		if(nFormularyId<=0){
			throw new InvalidParameterException("Invalid formularyId");
		}
		if (nFormularyId > 0 && !"".equals(jsonIngredientData)) {
			forSetupDao.updateIngredientDetails(jsonIngredientData, nFormularyId);
		}
	}

	public void insertUpdateDispenseUOM(String dispenseUOMArr, int nFormularyId, int nTrUserId) {
		if(nFormularyId<=0){
			throw new InvalidParameterException("Invalid formularyId");
		}
		if(nTrUserId<=0){
			throw new InvalidParameterException("Invalid UserId");
		}
		if (nFormularyId > 0 && !"".equals(dispenseUOMArr)) {
			forSetupDao.insertUpdateDispenseUOM(dispenseUOMArr, nFormularyId, nTrUserId);
		}
	}

	public int insertUpdateForPreVitals(int nFormularyId, String jsonVitalsObj, int nRoutedGenericItemId,
			int nTrUserId) throws org.json.JSONException, JSONException {
		if(nFormularyId<=0){
			throw new InvalidParameterException("Invalid formularyId");
		}
		if(nTrUserId<=0){
			throw new InvalidParameterException("Invalid userId");
		}
		if(nRoutedGenericItemId<=0){
			throw new InvalidParameterException("Invalid RoutedGenericItemId");
		}
		return forSetupDao.insertUpdateForPreVitals(nFormularyId, jsonVitalsObj, nRoutedGenericItemId, nTrUserId);
	}

	public void setPRNIndicationData(int nRoutedGenericItemId, String selectedPRNIds, int nTrUserId) {
		if (nTrUserId <= 0) {
			throw new InvalidParameterException("Invalid UserID");
		}
		if (nRoutedGenericItemId <= 0) {
			throw new InvalidParameterException("Invalid RoutedGenericID");
		}
		forSetupDao.setPRNIndicationData(nRoutedGenericItemId, selectedPRNIds, nTrUserId);
	}

	public int insertUpdateOrderTypeSetup(int drugId, String jsonOrderTypeData, int nTrUserId) {
		if(drugId<=0){
			throw new InvalidParameterException("Invalid drugId");
		}
		if(nTrUserId<0){
			throw new InvalidParameterException("Invalid userId");
		}
		return forSetupDao.insertUpdateOrderTypeSetup(drugId, jsonOrderTypeData, nTrUserId);
	}
	// associated product   // json data can be blank as per nisar
	public int insertUpdateAssociatedProducts(int drugId, String jsonData, int nTrUserId) throws JSONException, UnsupportedEncodingException {
		if(drugId<=0){
			throw new InvalidParameterException("Invalid drugId");
		}
		if(nTrUserId<0){
			throw new InvalidParameterException("Invalid userId");
		}
		int no = 0;
		if (drugId > 0 && !"".equals(jsonData)) {
			no = forSetupDao.insertUpdateAssociatedProducts(drugId, jsonData, nTrUserId);
		}
		return no;
	}

	public int insertUpdateFacilitiesList(int drugId, String facilitiesList, int nTrUserId) {
		if(drugId<=0){
			throw new InvalidParameterException("Invalid drugId");
		}
		if(nTrUserId<0){
			throw new InvalidParameterException("Invalid userId");
		}
		return forSetupDao.insertUpdateFacilitiesList(drugId, facilitiesList, nTrUserId);
	}

	public int insertUpdateServiceTypeList(int drugId, String serviceTypeList, int nTrUserId) {
		if(drugId<=0){
			throw new InvalidParameterException("Invalid drugId");
		}
		if(nTrUserId<0){
			throw new InvalidParameterException("Invalid userId");
		}
		return forSetupDao.insertUpdateServiceTypeList(drugId, serviceTypeList, nTrUserId);
	}

	public int insertUpdateRouteListExternal(int drugId, String routeList, int nTrUserId) {
		if(drugId<=0){
			throw new InvalidParameterException("Invalid drugId");
		}
		if(nTrUserId<=0){
			throw new InvalidParameterException("Invalid userId");
		}
		int no = 0;//Remove in-Lining of variable && improper stack track begin created
		if (!"".equals(routeList)) {
			no = forSetupDao.insertUpdateRouteListExternal(drugId, routeList, nTrUserId);
		}
		return no;
	}

	public int insertUpdateFrequencyListExternal(int drugId, String freqList, int nTrUserId) {
		if(drugId<=0){
			throw new InvalidParameterException("Invalid drugId");
		}
		if(nTrUserId<=0){
			throw new InvalidParameterException("Invalid userId");
		}
		int no = 0;
		if (drugId > 0 && !"".equals(freqList)) {
			no = forSetupDao.insertUpdateFrequencyListExternal(drugId, freqList, nTrUserId);
		}
		return no;
	}

	private int insertUpdateFormulationListExternal(int drugId, String formulationList, int nTrUserId) throws IOException {
		int no = 0;
		if (drugId > 0 && !"".equals(formulationList)) {
			no = forSetupDao.insertUpdateFormulationListExternal(drugId, formulationList, nTrUserId);
		}
		return no;
	}

	private int insertUpdateFormularyImmunizationData(int drugId, String strVisData, int nTrUserId) throws org.json.JSONException {
		//this feature not in use , will come in future release
		if(drugId<=0){
			throw new InvalidParameterException("Invalid drugId");
		}
		if(nTrUserId<0){
			throw new InvalidParameterException("Invalid userId"); 
		}
		int no = 0;
		if (drugId > 0 && !"".equals(strVisData)) {
			no = forSetupDao.insertUpdateFormularyImmunizationData(drugId, strVisData, nTrUserId);
		}
		return no;
	}

	public int insertUpdateDrugAlias(String jsonDrugData, int nTrUserId) throws IOException {
		//this feature not in use , will come in future release
		if(nTrUserId<0){
			throw new InvalidParameterException("Invalid UserId");
		}
		
		return forSetupDao.insertUpdateDrugAlias(jsonDrugData, nTrUserId);
	}

	public int getFormularySetupDrugCounter(FormularySearchParam fSearchParamObj) {
		if(fSearchParamObj == null)
		{
			throw new InvalidParameterException("Invalid fSearchParamObj");
		}
		if(Util.isNullEmpty(fSearchParamObj.getSearchBy())){
			throw new InvalidParameterException("Invalid searchBy");
		}
		if(Util.isNullEmpty(fSearchParamObj.getStatus())){
			throw new InvalidParameterException("Invalid status");
		}
		return forSetupDao.getFormularySetupDrugCounter(fSearchParamObj);
	}

	public int getFormularySetupDrugCounterForComponent(FormularySearchParam fSearchParamObj) {
		if(fSearchParamObj == null)
		{
			throw new InvalidParameterException("Invalid fSearchParamObj");
		}
		if(Util.isNullEmpty(fSearchParamObj.getSearchBy())){
			throw new InvalidParameterException("Invalid searchBy"); 
		}
		return forSetupDao.getFormularySetupDrugCounterForComponent(fSearchParamObj);
	}

	public List<TemplateForOTRoutes> getOrderTypeSetupRoutes(String formularyId) {
		if (Util.isNullEmpty(formularyId)) {
			throw new InvalidParameterException("Invalid formularyId");
		}
		if ("-1".equals(Util.trimStr(formularyId))) {
			throw new InvalidParameterException("Invalid formularyId");
		}
		if ("0".equals(Util.trimStr(formularyId))) {
			throw new InvalidParameterException("Invalid formularyId");
		}
		return forSetupDao.getOrderTypeSetupRoutes(formularyId);
	}

	public List<TemplateForOTS> getOrderTypeSetupData(String formularyId) {
		if (Util.isNullEmpty(formularyId)) {
			throw new InvalidParameterException("Invalid formularyId");
		}
		if ("-1".equals(Util.trimStr(formularyId))) {
			throw new InvalidParameterException("Invalid formularyId");
		}
		if ("0".equals(Util.trimStr(formularyId))) {
			throw new InvalidParameterException("Invalid formularyId");
		}
		return forSetupDao.getOrderTypeSetupData(formularyId);
	}

	// specific setting notes
	public int insertUpdateNotesData(int nFormularyId, String jsonNotesObj, int nTrUserId) throws IOException, JSONException {
		if(nFormularyId<=0){
			throw new InvalidParameterException("Invalid formularyId");
		}
		if(nTrUserId<=0){
			throw new InvalidParameterException("Invalid nTrUserId");
		}
		return forSetupDao.insertUpdateNotesData(nFormularyId, jsonNotesObj, nTrUserId);
	}

	// common settings -- notes
	public int insertUpdateCommonSettingNotesData(int nFormularyId, int nRoutedGenericItemId, String jsonNotesObj,
			int nTrUserId) throws IOException {
		if(nFormularyId<=0){
			throw new InvalidParameterException("Invalid formularyId");
		}
		if(nTrUserId<=0){
			throw new InvalidParameterException("Invalid nTrUserId");
		}
		if(nRoutedGenericItemId<=0){
			throw new InvalidParameterException("Invalid nRoutedGenericItemId");
		}
		return forSetupDao.insertUpdateCommonSettingNotesData(nFormularyId, nRoutedGenericItemId, jsonNotesObj,
				nTrUserId);
	}

	public int updateCommonSettingDrugData(int nFormularyId, int nRoutedGenericItemId, String jsonDrugObj,
			int nTrUserId) throws IOException, JSONException {
		if(nFormularyId<=0){
			throw new InvalidParameterException("Invalid formularyId");
		}
		if(nTrUserId<=0){
			throw new InvalidParameterException("Invalid nTrUserId");
		}
		if(nRoutedGenericItemId<=0){
			throw new InvalidParameterException("Invalid nRoutedGenericItemId");
		}
		return forSetupDao.updateCommonSettingDrugData(nFormularyId, nRoutedGenericItemId, jsonDrugObj, nTrUserId);
	}

	public List<TemplateForCommonSetting> loadCommonSettings(int nformularyId) {
		if(nformularyId<=0){
			throw new InvalidParameterException("Invalid formularyId");
		}
		return forSetupDao.loadCommonSettings(nformularyId);
	}

	public List<TemplateForCommonSetting> loadCommonSettings(int nRoutedGenericItemId, int nformularyId) {
		if(nformularyId<=0){
			throw new InvalidParameterException("Invalid formularyId");
		}
		if(nRoutedGenericItemId<=0){
			throw new InvalidParameterException("Invalid RoutedGenericItemId");
		}
		return forSetupDao.loadCommonSettings(nRoutedGenericItemId, nformularyId);
	}

	public List<TemplateForDrug> loadCommonSettingsForDrugDetails(int nRoutedGenericItemId, int nformularyId) {
		if(nformularyId<=0){
			throw new InvalidParameterException("Invalid formularyId");
		}
		if(nRoutedGenericItemId<=0){
			throw new InvalidParameterException("Invalid RoutedGenericItemId");
		}
		return forSetupDao.loadCommonSettingsForDrugDetails(nRoutedGenericItemId, nformularyId);
	}

	public List<TemplateForNotes> loadCommonSettingsForNotes(int nRoutedGenericItemId, int nformularyId) {
		if(nformularyId<=0){
			throw new InvalidParameterException("Invalid formularyId");
		}
		if(nRoutedGenericItemId<=0){
			throw new InvalidParameterException("Invalid RoutedGenericItemId");
		}
		return forSetupDao.loadCommonSettingsForNotes(nRoutedGenericItemId, nformularyId);
	}

	public List<TemplateForDrug> loadBrandNames(int nRoutedGenericItemId) {
		if(nRoutedGenericItemId<=0){
			throw new InvalidParameterException("Invalid RoutedGenericItemId");
		}
		return forSetupDao.loadBrandNames(nRoutedGenericItemId);
	}

	// formulary listing
	private Map<String, String> checkIfExistInDrugFormulary(String jsonDrugData)throws JSONException, IOException {
		Map<String, String> hmFormularyData = null;
		List<TemplateForDrug> tmplList = null;
		PharmacyHelper.generatePharmacyLog("checkIfExistInDrugFormulary is called");
		hmFormularyData = new HashMap<>();
		TemplateForDrug drug = new ObjectMapper().readValue(jsonDrugData, TemplateForDrug.class);

		if ("0".equals(drug.getIsactive()) && !"".equalsIgnoreCase(drug.getId())) {
			List<Integer> formularyCntList = forSetupDao.isFormularyActive(Integer.parseInt(drug.getId()));
			if(formularyCntList!=null)
			{
				for (int nCnt : formularyCntList) {
					if (nCnt > 0) {
						List<SchemaDetails> sList = new ArrayList<>();
						sList.add(new SchemaDetails("ip_med_signaturedetails", "drugformularyid", "deleteflag"));
						sList.add(new SchemaDetails("ip_pharmacy_billing_detail", "formularyid", "deleteflag"));
						if (isDataInUse(sList, drug.getId())) {
							hmFormularyData.put("isDataInUse", "true");
						}
						break; 
					}
				}
			}
		}
		// this will return the list of equivalent added drug into formulary having same
		// itemname,strength and formulation
		if(!"-1".equals(Util.trimStr(drug.getGpi())) && !"".equals(Util.trimStr(drug.getGpi())))
		{
			tmplList = forSetupDao.getEquivalentFormularyDrugDataUsingItem(drug);
			for (int cnt = 0; cnt < tmplList.size(); cnt++) {
				if (!"".equals(tmplList.get(cnt).getItemId())) {
					JSONObject item = new JSONObject();
					item.put("ndc", tmplList.get(cnt).getNdc());
					item.put("upc", tmplList.get(cnt).getUpc());
					item.put("itemid", tmplList.get(cnt).getItemId());
					item.put("itemName", tmplList.get(cnt).getItemName());
					hmFormularyData.put("DrugDataDetails", item.toString());
					hmFormularyData.put("bExistInDrugFormulary", "true");
				}
			}
		}
		PharmacyHelper.generatePharmacyLog("hmFormularyData [" + hmFormularyData.size() + "]");
		return hmFormularyData;
	}

	// get drug list as per order type
	public List<Template> getDrugListAsPerOrderType(String orderType) {
		if(Util.isNullEmpty(orderType)){
			throw new InvalidParameterException("Invalid orderType");
		}
		return forSetupDao.getDrugListAsPerOrderType(orderType);
	}

	/* delete methods */
	@CheckForUserAccess(securitySettingKeys = PharmacySecurityConstants.Delete_ConfigureFormulary_SECURITY_KEY)
	public int deleteDrugDetails(String drugId, int nRoutedGenericItemId, String strItemName, int nTrUserId) throws JSONException {
		if(Util.isNullEmpty(drugId)){
			throw new InvalidParameterException("Invalid drugId");
		}
		if(Util.isNullEmpty(strItemName)){
			throw new InvalidParameterException("Invalid strItemName");
		}
		if(nRoutedGenericItemId<=0){
			throw new InvalidParameterException("Invalid RoutedGenericItemId");
		}
		if(nTrUserId<=0){
			throw new InvalidParameterException("Invalid UserId");
		}
		return forSetupDao.deleteDrugDetails(drugId, nRoutedGenericItemId, strItemName, nTrUserId);
	}

	public int deleteAssociatedProducts(String formularyId, String assoProdId, String ndc, int trUserId) {
		if(Util.isNullEmpty(formularyId)){
			throw new InvalidParameterException("Invalid formularyId");
		}
		if(Util.isNullEmpty(assoProdId)){
			throw new InvalidParameterException("Invalid assoProdId");
		}
		if(trUserId<=0){
			throw new InvalidParameterException("Invalid userId");
		}
		return forSetupDao.deleteAssociatedProducts(formularyId, assoProdId, ndc, trUserId);
	}

	public int deletePrerequisite(int formularyId, String vitalId, int trUserId) throws JSONException {
		if(formularyId<=0){
			throw new InvalidParameterException("Invalid formularyId");
		}
		if(Util.isNullEmpty(vitalId)){
			throw new InvalidParameterException("Invalid vitalId");
		}
		if(trUserId<=0){
			throw new InvalidParameterException("Invalid trUserId");
		}
		return forSetupDao.deletePrerequisite(formularyId, vitalId, trUserId);
	}

	public int deleteAssociatedProductsLotDetails(String lotId, String formularyId, String assoProdId, String lotno,
			int trUserId) throws JSONException {
		if(Util.isNullEmpty(lotId)){
			throw new InvalidParameterException("Invalid lotId");
		}
		if(Util.isNullEmpty(formularyId)){
			throw new InvalidParameterException("Invalid formularyId");
		}
		if(Util.isNullEmpty(assoProdId)){
			throw new InvalidParameterException("Invalid assoProdId");
		}
		if(Util.isNullEmpty(lotno)){
			throw new InvalidParameterException("Invalid lotno");
		}
		if(trUserId<=0){
			throw new InvalidParameterException("Invalid userId");
		}
		return forSetupDao.deleteAssociatedProductsLotDetails(lotId, formularyId, assoProdId, lotno, trUserId);
	}

	public List<TemplateForOTFrequency> getOrderTypeSetupFrequency(String formularyId) {
		if(Util.isNullEmpty(formularyId)){
			throw new InvalidParameterException("Invalid formularyId");
		}
		return forSetupDao.getOrderTypeSetupFrequency(formularyId);
	}

	public List<Map<String, Object>> getDrugFormularyByItemId(final int nItemId, final int formularyId) {
//		if(formularyId<=0){//sandip api commented because of issue in dose list
//			throw new InvalidParameterException("Invalid formularyId");
//		}
//		if(nItemId<=0){
//			throw new InvalidParameterException("Invalid itemId");
//		}
		return forSetupDao.getDrugFormularyByItemId(nItemId, formularyId);
	}

	public List<Map<String, Object>> getDrugFormularyWithOTSetupByItemId(final MedicationDosageReqParam reqParam) {
		return forSetupDao.getDrugFormularyWithOTSetupByItemId(reqParam);
	}

	/**
	 * @param dispenseMap
	 * @return StatusMap API to get drug dispense detail
	 */
	public StatusMap getDrugDispenseDetail(Map<String, Object> dispenseMap) {
		StatusMap statusMap = new StatusMap();
		Map<String, Object> returnDrugMap = new HashMap<>();
		try {
			if(null==dispenseMap || "".equals(dispenseMap))
		        throw  new IllegalArgumentException();
			 int formularyId = null!=dispenseMap.get(FORMULARY_ID)?Integer.parseInt(DataValidation.sanitizeInt(dispenseMap.get(FORMULARY_ID).toString())):0;
			returnDrugMap = forSetupDao.getDrugDispenseDetailFromFormularyDetails(formularyId);
			statusMap.setCustom("dispenseDrugCodeDetail", returnDrugMap);
			statusMap.setSuccess();
		} catch (DataAccessException ex) {
			EcwLog.AppendExceptionToLog(ex);
			statusMap.setFail("Failed to get the drug dispense details");
		}catch (IllegalArgumentException ex) {
			EcwLog.AppendExceptionToLog(ex);
			statusMap.setFail("Failed to get the drug dispense details : Reason (Invalid value)");
		} catch (Exception ex) {
			EcwLog.AppendExceptionToLog(ex);
			statusMap.setFail("Failed to get the drug dispense details");
		}
		return statusMap;
	}

	/**
	 * @param dispenseStockDetail
	 * @return StatusMap API to save or update dispense details with quantity
	 */
	public StatusMap saveUpdateDispenseDetails(Map<String, Object> dispenseStockDetail) {
		StatusMap responseMapObject = new StatusMap();
		responseMapObject.setFail("Failed to save/update drug dispense detail");
		String dbDateFormat = IPTzUtils.DEFAULT_DB_DT_FMT;
		String currentDate = IPTzUtils.getCurrentUTCDateTime(dbDateFormat);
		try {
			if(null==dispenseStockDetail || "".equals(dispenseStockDetail))
		        throw  new IllegalArgumentException();
			
			dispenseStockDetail.put("modifiedTime", currentDate);
			dispenseStockDetail.put("createdTime", currentDate);
			dispenseStockDetail.put("deleteFlag", 0);
			int formularyId = null!=dispenseStockDetail.get(FORMULARY_ID)?Integer.parseInt(DataValidation.sanitizeInt(dispenseStockDetail.get(FORMULARY_ID).toString())):0;
			
			String drugDispenseCode = dispenseStockDetail.get(DRUG_DISPENSE_CODE).toString();

			if (forSetupDao.updateDrugDispenseCodeInFormulary(formularyId, drugDispenseCode) > 0) {
				responseMapObject.setSuccess();
				responseMapObject.setCustom(MESSAGE, "Drug dispense code saved successfully");
			}

			if (("no").equals(dispenseStockDetail.get("isQtyUpdated"))) {
				if (forSetupDao.updateDispenseCodeForQuantityWise(formularyId, drugDispenseCode) > 0) {
					responseMapObject.setSuccess();
					responseMapObject.setCustom(MESSAGE, "Drug dispense code updated successfully");
				}
			} else {
				responseMapObject = forSetupDao.saveUpdateDispenseDetails(dispenseStockDetail);
			}
		}catch (DataAccessException ex) {
			EcwLog.AppendExceptionToLog(ex);
			responseMapObject.setFail("Failed to save/update drug dispense detail");
		}catch(IllegalArgumentException ex) {
			responseMapObject.setFail("Failed to save/update drug dispense detail:Reason (Invalid value)");
			EcwLog.AppendExceptionToLog(ex);
		}catch (Exception ex) {
			EcwLog.AppendExceptionToLog(ex);
			responseMapObject.setFail("Failed to save/update drug dispense detail");
		}
		return responseMapObject;

	}

	/**
	 * @param dispenseStockDetail
	 * @return StatusMap API to get drug dispense detail location with available
	 *         quantity
	 */
	public StatusMap getStockAreaDetailWithQuantity(Map<String, Object> dispenseStockDetail) {
		StatusMap responseMapObject = new StatusMap();
		responseMapObject.setFail("Failed to get dispense stock area details");
		List<DispenseStockAreaQuantityMappingModal> dispenseStockAreaQtyDetails = null;
		try {
			if(null==dispenseStockDetail || "".equals(dispenseStockDetail))
		        throw  new IllegalArgumentException();
			int drugId = null!=dispenseStockDetail.get(FORMULARY_ID)?Integer.parseInt(DataValidation.sanitizeInt(dispenseStockDetail.get(FORMULARY_ID).toString())):0;
			int userId = null!=dispenseStockDetail.get(USER_ID)?Integer.parseInt(DataValidation.sanitizeInt(dispenseStockDetail.get(USER_ID).toString())):0;
			
			dispenseStockAreaQtyDetails = forSetupDao.getAllDispenseDetails(drugId, userId);
			if (!dispenseStockAreaQtyDetails.isEmpty()) {
				responseMapObject.setCustom("dispenseStockAreaQtyList", dispenseStockAreaQtyDetails);
				responseMapObject.setSuccess();
			} else {
				responseMapObject.setSuccess();
				responseMapObject.setCustom(MESSAGE, "No records found");
			}
		} catch (DataAccessException ex) {
			EcwLog.AppendExceptionToLog(ex);
			responseMapObject.setFail("Failed to get dispense stock area details");
		}catch(IllegalArgumentException e) {
			responseMapObject.setFail("Failed to get dispense stock area details : Reason (Invalid value)");
  			EcwLog.AppendExceptionToLog(e);
		} catch (Exception ex) {
			EcwLog.AppendExceptionToLog(ex);
			responseMapObject.setFail("Failed to get dispense stock area details");
		}
		return responseMapObject;
	}

	/**
	 * @param dispenseMap
	 * @return StatusMap API to check duplicate Dispense Drug Code
	 */
	public StatusMap checkDispenseCodeInFormularyDetail(Map<String, Object> dispenseMap) {
		StatusMap responseMapObject = new StatusMap();
 		responseMapObject.setFail("Failed to validate the drug dispense code in formulary");
		List<String> rList = new ArrayList<>();
		try {
			if(null==dispenseMap || "".equals(dispenseMap))
		        throw  new IllegalArgumentException();
			
			rList = forSetupDao.checkDispenseCodeInFormularyDetail(dispenseMap);
			if (rList.isEmpty()) {
				responseMapObject.setSuccess();
				responseMapObject.put("message", "");
			} else {
				String message = null != dispenseMap.get(DRUG_DISPENSE_CODE)
						? ("The entered Drug Dispensing Code '" + dispenseMap.get(DRUG_DISPENSE_CODE).toString()
								+ "' is already mapped with other formulary drug")
						: ("The entered Drug Dispensing Code is invalid");
				responseMapObject.setFail(message);
			}
		} catch (DataAccessException ex) {
			EcwLog.AppendExceptionToLog(ex);
			responseMapObject.setFail("Failed to validate the drug dispense code in formulary");
		}catch(IllegalArgumentException e) {
			responseMapObject.setFail("Failed to validate the drug dispense code: Reason (Invalid value)");
  			EcwLog.AppendExceptionToLog(e);
		}
		return responseMapObject;
	}

	public List<TemplateForNotes> getFormularyNoteList(int formularyId) {
		if(formularyId<=0){
			throw new InvalidParameterException("Invalid formularyId");
		}
		return forSetupDao.getFormularyNoteList(formularyId);
	}

	public String getTC2ClassIdByItemId(final int nItemId) {
		return forSetupDao.getTC2ClassIdByItemId(nItemId);
	}

	public List<Integer> getItemIdsByTC2ClassId(final String strTC2ClassId) {
		return forSetupDao.getItemIdsByTC2ClassId(strTC2ClassId);
	}
	
	public List<Integer> getRoutedGenericItemIdsByTC2ClassId(final String strTC2ClassId) {
		return forSetupDao.getRoutedGenericItemIdsByTC2ClassId(strTC2ClassId);
	}

	/**
	 * This method is used to get drug formulary common settings by routed generic
	 * itemid
	 * 
	 * @param routedgenericitemid
	 * @return
	 */
	public List<Map<String, Object>> getFormularyCommonSettings(int routedgenericitemid, int formularyId) {
		if(routedgenericitemid<=0){
			throw new InvalidParameterException("Invalid routedgenericitemid");
		}
		if(formularyId<=0){
			throw new InvalidParameterException("Invalid formularyId");
		}
		return forSetupDao.getFormularyCommonSettings(routedgenericitemid, formularyId);
	}

	/**
	 * @param orderId
	 * @return TotalCostPerOrderId which has totalcost per orderid, and list of
	 *         formularywise costs.
	 */
	public TotalCostPerOrderId getTotalCostByOrderId(long orderId, ArrayList<CostPerFormulary> ndcCodeList) {
		TotalCostPerOrderId result = new TotalCostPerOrderId();
		List<CostPerFormulary> resultData = new ArrayList<>();

		List<CostPerFormulary> dataPerFormulary = forSetupDao.getCostPerFormularyByOrderId(orderId);

		for (CostPerFormulary cost : dataPerFormulary) {
			if (cost.getPrimaryNDC().equals(cost.getAssocProdNDC())) {

				double costCalculatedWithDispense = this.getCostCalculatedWithPriceRule(cost.getPriceRuleParam(), cost.getDispense());
				double totalHcpcsUnit  = cost.getTotalHcpcsUnit();
				double costCalculatedPerUnitWithDispense = costCalculatedWithDispense;
				cost.setCalculatedCostWithDispense(costCalculatedPerUnitWithDispense);
				cost.setNdcCode(cost.getPrimaryNDC());
				cost.setTotalHcpcsUnit(totalHcpcsUnit);
				resultData.add(cost);
			}
		}
		result.setPrimaryNDCCost(true);

		boolean oneNDCMatchFound = false;

		if (ndcCodeList != null && !ndcCodeList.isEmpty()) {

			for (CostPerFormulary ndcCodeProvided : ndcCodeList) {
				boolean ndcMatchFound = false;
				for (CostPerFormulary cost : dataPerFormulary) {
					if (cost.getAssocProdNDC().equals(ndcCodeProvided.getNdcCode())) {
						ndcCodeProvided.setFormularyId(cost.getFormularyId());
						ndcCodeProvided.setCptItemId(cost.getCptItemId());
						ndcCodeProvided.setChargeTypeId(cost.getChargeTypeId());
						ndcCodeProvided.setCalculatedCostWithoutDispense(cost.getCalculatedCostWithoutDispense());

						double costCalculatedWithDispense = this.getCostCalculatedWithPriceRule(cost.getPriceRuleParam(), cost.getDispense());
						double totalHcpcsUnit  = cost.getTotalHcpcsUnit();
						double costCalculatedPerUnitWithDispense = costCalculatedWithDispense;	
						ndcCodeProvided.setCalculatedCostWithDispense(costCalculatedPerUnitWithDispense);

						ndcCodeProvided.setDispense(cost.getDispense());
						ndcCodeProvided.setPriceRuleParam(cost.getPriceRuleParam());
						ndcCodeProvided.setAwup(cost.getAwup());
						ndcCodeProvided.setCostToProcure(cost.getCostToProcure());
						ndcCodeProvided.setPpId(cost.getPpId());
						ndcCodeProvided.setTotalHcpcsUnit(totalHcpcsUnit);
						

						ndcMatchFound = true;
						break;
					}
				}
				ndcCodeProvided.setNdcMatchFound(ndcMatchFound);
				if (ndcMatchFound) {
					oneNDCMatchFound = true;
				}
			}

			if (oneNDCMatchFound) {
				resultData.clear();
				resultData.addAll(ndcCodeList);
				result.setPrimaryNDCCost(false);
			}

		}

		double totalCost = 0.0;
		double totalHcpcsUnit = 0.0;
		for (CostPerFormulary cost : resultData) {
			double costPerFormulary = cost.getCalculatedCostWithDispense();
			totalCost += costPerFormulary;
			
			double unitCharged = cost.getTotalHcpcsUnit();
			totalHcpcsUnit = unitCharged; 
		}

		result.setCostProductList(resultData);
		result.setNdcCodeList(ndcCodeList);
		result.setTotalCost(totalCost);
		result.setOrderId(orderId);
		result.setTotalHcpcsUnit(totalHcpcsUnit);

		return result;
	}

	public List<TemplateForIVRate> getFormularyIVDiluentRateList(int formularyId, int nSigTypeId) {
		if(formularyId<=0){
			throw new InvalidParameterException("Invalid formularyId");
		}
		if(nSigTypeId<=0){
			throw new InvalidParameterException("Invalid SigTypeId");
		}
		return forSetupDao.getFormularyIVDiluentRateList(formularyId, nSigTypeId);
	}

	/**
	 * Gets the routed generic id.
	 *
	 * @param formularyId the formulary id
	 * @return the routed generic id
	 */
	public Map<String, String> getRoutedGenericId(String formularyId) {
		Map<String, String> idsMap = forSetupDao.getRoutedGenericId(formularyId);
		Map<String, String> responseObj = new HashMap<>();
		responseObj.put("routedGenericId", idsMap.get("routedGenericId"));
		responseObj.put("gpi", idsMap.get("gpi"));
		return responseObj;
	}

	/**
	 * Save drug drug interaction.
	 *
	 * @param requestMap the request map
	 * @param userId     the user id
	 * @return the status map
	 * @throws Exception
	 */
	public StatusMap saveDrugDrugInteraction(Map<String, Object> requestMap, int userId) {
		StatusMap response = new StatusMap();
		try {
			/*************************************************
			 * Fetch RoutedGenricItemId from GenericProductId
			 ************************************************/
			String formularyGPI = Util.getStrValue(requestMap, FORMULARY_GPI).replace("-", "");
			String interactingGPI = Util.getStrValue(requestMap, INTERACTING_GPI).replace("-", "");

			List<String> gpiList = new ArrayList<>();
			gpiList.add(formularyGPI);
			gpiList.add(interactingGPI);

			Map<String, Integer> routedGenItemIdMap = forSetupDao.getRoutedGenricItemIds(gpiList);

			/**********************************************
			 * Prepare query data map
			 *********************************************/
			String formatedDate = IPTzUtils.getDateTimeStrForUser(userId, INTERACTION_FORMAT.DATE_TIME_FORMAT.format());

			Map<String, Object> queryData = new HashMap<>();
			queryData.put(FORMULARY_ID, requestMap.get(FORMULARY_ID));
			queryData.put("formularyRoutedGenericItemId", Util.getIntValue(routedGenItemIdMap, formularyGPI));
			queryData.put(ROUTED_GENERIC_ITEMID, Util.getLongValue(requestMap, ROUTED_GENERIC_ITEMID));
			queryData.put("formularyDrugNameid", Util.getIntValue(requestMap, "formularyDrugNameid"));
			queryData.put("formularyDrugNameDesc", Util.getStrValue(requestMap, "formularyDrugNameDesc"));
			queryData.put("formularyRoutedDrugId", Util.getIntValue(requestMap, "formularyRoutedDrugId"));
			queryData.put(FORMULARY_GPI, formularyGPI);
			queryData.put("formularyDDID", Util.getIntValue(requestMap, "formularyDDID"));
			queryData.put("interactingRoutedGenericItemId", Util.getIntValue(routedGenItemIdMap, interactingGPI));
			queryData.put("interactingDrugNameid", Util.getIntValue(requestMap, "interactingDrugNameid"));
			queryData.put("interactingDrugNameDesc", Util.getStrValue(requestMap, "interactingDrugNameDesc"));
			queryData.put("interactingRoutedDrugId", Util.getIntValue(requestMap, "interactingRoutedDrugId"));
			queryData.put(INTERACTING_GPI, interactingGPI);
			queryData.put("interactingDDID", Util.getIntValue(requestMap, "interactingDDID"));
			queryData.put("interactionlevelOriginal", Util.getIntValue(requestMap, "interactionlevelOriginal"));
			queryData.put(INTERACTION_LEVEL_MODIFIED, Util.getIntValue(requestMap, INTERACTION_LEVEL_MODIFIED));
			queryData.put("interactionDesc", requestMap.get("interactionDesc"));
			queryData.put("interactionType", "drugtodrug");
			queryData.put("createdBy", userId);
			queryData.put("createdDate", formatedDate);
			int interactionId = forSetupDao.saveDrugDrugInteraction(convertMapToMapSqlParameterSource(queryData));
			response.setData(interactionId);
			response.setSuccess();
			return response;
		} catch (Exception e) {
			EcwLog.AppendExceptionToLog(e);
			response.setFail("An error occured while saving drugTodrug interaction settings");
			return response;
		}
	}

	/**
	 * Update drug drug interaction.
	 *
	 * @param updateMapList the update map list
	 * @param userId        the user id
	 * @return the status map
	 * @throws Exception
	 */
	public StatusMap updateDrugDrugInteraction(List<Map<String, Object>> updateMapList, int userId) {
		StatusMap response = new StatusMap();
		try {
			/**********************************************
			 * Prepare query data map for update
			 *********************************************/
			String formatedDate = IPTzUtils.getDateTimeStrForUser(userId, INTERACTION_FORMAT.DATE_TIME_FORMAT.format());
			boolean updateStatus = false;
			for (Map<String, Object> updateMap : updateMapList) {
				Map<String, Object> queryData = new HashMap<>();
				queryData.put("id", Util.getLongValue(updateMap, "id"));
				queryData.put(INTERACTION_LEVEL_MODIFIED, Util.getLongValue(updateMap, INTERACTION_LEVEL_MODIFIED));
				queryData.put(MODIFIED_BY, userId);
				queryData.put(MODIFIED_DATE, formatedDate);
				updateStatus = forSetupDao.updateDrugDrugInteraction(convertMapToMapSqlParameterSource(queryData));
				if (!updateStatus) {
					response.setFail("An error occured while updating drugTodrug interaction settings");
					return response;
				}
			}
			response.setData(updateStatus);
			response.setSuccess();
			return response;
		} catch (Exception e) {
			EcwLog.AppendExceptionToLog(e);
			response.setFail("An error occured while updating drugTodrug interaction settings");
			return response;
		}
	}

	/**
	 * Delete drug drug interaction.
	 *
	 * @param requestMap the request map
	 * @param userId     the user id
	 * @return the status map
	 * @throws Exception
	 */
	public StatusMap deleteDrugDrugInteraction(Map<String, Object> requestMap, int userId) {
		StatusMap response = new StatusMap();
		try {
			/**********************************************
			 * Prepare query data map for delete
			 *********************************************/
			String formatedDate = IPTzUtils.getDateTimeStrForUser(userId, INTERACTION_FORMAT.DATE_TIME_FORMAT.format());

			requestMap.put("deleteFlag", 1);
			requestMap.put(MODIFIED_BY, userId);
			requestMap.put(MODIFIED_DATE, formatedDate);
			boolean deleteStatus = forSetupDao.deleteDrugDrugInteraction(convertMapToMapSqlParameterSource(requestMap));
			if (!deleteStatus) {
				response.setFail("An error occured while deleting drugTodrug interaction settings");
				return response;
			}
			response.setData(deleteStatus);
			response.setSuccess();
			return response;
		} catch (Exception e) {
			EcwLog.AppendExceptionToLog(e);
			response.setFail("An error occured while deleting drugTodrug interaction settings");
			return response;
		}
	}

	/**
	 * Gets the saved drug drug interaction.
	 *
	 * @param gpi the gpi
	 * @return the saved drug drug interaction
	 * @throws Exception the exception
	 */
	public StatusMap getSavedDrugDrugInteraction(Integer routedGenericItemId) {
		StatusMap response = new StatusMap();
		try {
			List<Integer> routedGenericItemIds = new ArrayList<>();
			routedGenericItemIds.add(routedGenericItemId);
			List<Map<String, Object>> interactionList = forSetupDao.getSavedDrugDrugInteraction(routedGenericItemIds);
			response.setData(interactionList);
			response.setSuccess();
		} catch (Exception ex) {
			EcwLog.AppendExceptionToLog(ex);
		}
		return response;
	}

	/**
	 * Check duplicate interaction.
	 *
	 * @param requestMap the request map
	 * @return the status map
	 * @throws Exception the exception
	 */
	public StatusMap checkDuplicateInteraction(Map<String, Object> requestMap) {
		StatusMap response = new StatusMap();
		try {
			/*************************************************
			 * Fetch RoutedGenricItemId from GenericProductId
			 ************************************************/
			String formularyGPI = Util.getStrValue(requestMap, FORMULARY_GPI).replace("-", "");
			String interactingGPI = Util.getStrValue(requestMap, INTERACTING_GPI).replace("-", "");

			List<String> gpiList = new ArrayList<>();
			gpiList.add(formularyGPI);
			gpiList.add(interactingGPI);

			Map<String, Integer> routedGenItemIdMap = forSetupDao.getRoutedGenricItemIds(gpiList);

			requestMap.put("formularyRoutedGenericItemId", routedGenItemIdMap.get(formularyGPI));
			requestMap.put("interactingRoutedGenericItemId", routedGenItemIdMap.get(interactingGPI));

			/************************************************
			 * Check for duplicate interaction
			 ***********************************************/

			if (forSetupDao.checkDuplicateInteraction(requestMap)) {
				response.setFail("This medication has already been added to the interaction list and has a severity defined in the formulary.");
				return response;
			}
			response.setSuccess();
		} catch (Exception ex) {
			EcwLog.AppendExceptionToLog(ex);
		}
		return response;
	}

	/**
	 * Save enabled interactions.
	 *
	 * @param requestMap the request map
	 * @return the status map
	 * @throws Exception the exception
	 */
	public StatusMap saveOrUpdateEnableInteraction(Map<String, Object> requestMap, int userId) {
		/**********************************************
		 * Prepare query data map
		 *********************************************/
		StatusMap response = new StatusMap();
		try {
			String formatedDate = IPTzUtils.getDateTimeStrForUser(userId, INTERACTION_FORMAT.DATE_TIME_FORMAT.format());
			boolean status = false;
			Map<String, Object> queryData = new HashMap<>();
			queryData.put(ROUTED_GENERIC_ITEMID, requestMap.get(ROUTED_GENERIC_ITEMID));
			queryData.put("drugToFood", Util.getIntValue(requestMap, "drugToFood"));
			queryData.put("drugToAlcohol", Util.getIntValue(requestMap, "drugToAlcohol"));
			queryData.put("duplicateTherapy", Util.getIntValue(requestMap, "duplicateTherapy"));
			queryData.put("createdBy", userId);
			queryData.put("createdDate", formatedDate);
			queryData.put(MODIFIED_BY, userId);
			queryData.put(MODIFIED_DATE, formatedDate);
			status = forSetupDao.saveOrUpdateEnableInteraction(queryData);
			if (!status) {
				response.setFail("An error occured while saving or updating interaction settings");
				return response;
			}
			response.setData(status);
			response.setSuccess();
			return response;
		} catch (Exception e) {
			EcwLog.AppendExceptionToLog(e);
			response.setFail("An error occured while saving or updating interaction settings");
			return response;
		}
	}

	/**
	 * Fetch enabled interactions.
	 *
	 * @param requestMap the request map
	 * @return the status map
	 * @throws Exception the exception
	 */
	public StatusMap fetchEnableInteraction(Map<String, Object> requestMap) {
		/**********************************************
		 * Prepare query data map
		 *********************************************/
		StatusMap response = new StatusMap();
		try {
			Map<String, Object> queryData = new HashMap<>();
			queryData.put(ROUTED_GENERIC_ITEMID, requestMap.get(ROUTED_GENERIC_ITEMID));
			Map<String, Object> interParams = forSetupDao.fetchEnableInteraction(queryData);
			response.setData(interParams);
			response.setSuccess();
			return response;
		} catch (Exception e) {
			EcwLog.AppendExceptionToLog(e);
			response.setFail("Exception occured while fetching interaction settings");
			return response;
		}
	}

	/**
	 *
	 * Convert Map entry to MapSqlParameterSource
	 * 
	 * @param Map
	 * 
	 * @return MapSqlParameterSource
	 * @throws Exception
	 */
	private MapSqlParameterSource convertMapToMapSqlParameterSource(Map<String, Object> map) {
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			if (null == key)
				key = "";
			mapSqlParameterSource.addValue(key, map.get(key));

		}
		return mapSqlParameterSource;
	}

	/**
	 * get AHFS Classification By ItemId
	 * 
	 * @param nItemId
	 * @return
	 */
	public String getAHFSClassificationByItemId(final int nItemId) {
		if (nItemId < 1) {
			return "";
		}
		return forSetupDao.getAHFSClassificationByItemId(nItemId);
	}

	/**
	 * get AHFS Classification By ItemId And Strength
	 * 
	 * @param nItemId
	 * @param strength
	 * @return
	 */
	public String getAHFSClassificationByItemIdAndStrength(final int nItemId, final String strength) {
		if (nItemId < 1 || strength == null || "".equalsIgnoreCase(strength)) {
			return "";
		}
		return forSetupDao.getAHFSClassificationByItemIdAndStrength(nItemId, strength);
	}

	/**
	 * get AHFS Classification By FormularyId
	 * 
	 * @param nFormularyId
	 * @return
	 */
	public String getAHFSClassificationByFormularyId(final int nFormularyId) {
		if(nFormularyId<=0){
			throw new InvalidParameterException("Invalid formularyId");
		}
		if (nFormularyId < 1) {
			return "";
		}
		return forSetupDao.getAHFSClassificationByFormularyId(nFormularyId);
	}

	/**
	 * Get Selected Frequencies By FormularyId
	 * 
	 * @author Sandip Dalsaniya
	 * @param formularyId : formulary id of medication
	 * @return list of {@link TemplateForOTFrequency}
	 */
	public List<TemplateForOTFrequency> getActiveFrequencies(final int formularyId, final int orderTypeId) {
		return forSetupDao.getActiveFrequencies(formularyId, orderTypeId);
	}

	/**
	 * Get Selected Routes By FormularyId
	 * 
	 * @author Sandip Dalsaniya
	 * @param formularyId : formulary id of medication
	 * @return list of {@link TemplateForOTRoutes}
	 */
	public List<TemplateForOTRoutes> getActiveRoutes(final int formularyId, final int orderTypeId) {
		return forSetupDao.getActiveRoutes(formularyId, orderTypeId);
	}

	public Map<String, Object> getConfigureFormularySecurityItemsForUser(int userId) {
		if (userId <= 0) {
			throw new InvalidParameterException("Invalid user Id");
		}
		List<String> securityItemKeys = new ArrayList<>();
		populateSecurityKeyList(securityItemKeys);
		return pharmacyHelper.getSecurityItemForUser(securityItemKeys, userId);
	}

	private List<String> populateSecurityKeyList(List<String> securityItemKeys) {
		securityItemKeys.add(PharmacySecurityConstants.ADD_ConfigureFormulary_SECURITY_KEY);
		securityItemKeys.add(PharmacySecurityConstants.EDIT_ConfigureFormulary_SECURITY_KEY);
		securityItemKeys.add(PharmacySecurityConstants.VIEW_ConfigureFormulary_SECURITY_KEY);
		securityItemKeys.add(PharmacySecurityConstants.Delete_ConfigureFormulary_SECURITY_KEY);
		return securityItemKeys;
	}

	/**
	 * get Drug from formulary based on itemId , strength and facilityId
	 * 
	 * @param ipitemid
	 * @param strength
	 * @param facilityId
	 * @return
	 */
	public List<Map<String, Object>> getDrugFormularyByItemIdAndStrength(final int itemId, final String strength,
			final int facilityId) {
		if (itemId < 1 || facilityId < 1 || strength == null || "".equals(strength)) {
			return Collections.emptyList();
		}
		return forSetupDao.getDrugFormularyByItemIdAndStrength(itemId, strength, facilityId);
	}

	public double getCostCalculatedWithPriceRule(PriceRuleParam priceRuleParam, double dispense) {

		double result = 0;

		if (priceRuleParam.isChargePerDose()) {
			result = (((priceRuleParam.getCostToBeConsidered() * dispense) * priceRuleParam.getMarkUp() / NUM_100)
					+ priceRuleParam.getAdditionalCharge());
		} else {
			result = ((priceRuleParam.getCostToBeConsidered() * priceRuleParam.getMarkUp() / NUM_100)
					+ priceRuleParam.getAdditionalCharge()) * dispense;
		}

		if (result < priceRuleParam.getMinimumCharge()) {
			result = priceRuleParam.getMinimumCharge();
		}

		DecimalFormat df = new DecimalFormat("#.###");
		result = Double.parseDouble(df.format(result));
		return result;
	}

	/**
	 * This method is used to check whether data in use or not It is required to
	 * pass list of schemadetails (tablename,columnname, deletecolumn name and
	 * target column value that is going to check)
	 * 
	 * @param sList
	 * @param targetColumnValue
	 * @return
	 */
	public boolean isDataInUse(List<SchemaDetails> sList, String targetColumnValue) {
		if(sList==null ||(sList.size()<=0)){
			throw new InvalidParameterException("empty SchemaList");
		}
		if (Util.isNullEmpty(targetColumnValue)){
			throw new InvalidParameterException("Invalid targetColumnValue");
		}
		boolean isDataInUse = false;
		String tableName;
		String columnName;
		String deleteColumnName;
		try {
			for (SchemaDetails oSchema : sList) {
				tableName = oSchema.getTableName();
				columnName = oSchema.getColumnName();
				deleteColumnName = oSchema.getDeleteColumnName();
				if (forSetupDao.queryToCheckResult(tableName, columnName, deleteColumnName, targetColumnValue)) {
					isDataInUse = true;
					break;
				}
			}
		} catch (RuntimeException e) {
			EcwLog.AppendExceptionToLog(e);
		}
		return isDataInUse;
	}

	public Map<String, Object> getDrugIntructionFromFormulary(int routedGenericID) {
		return forSetupDao.getDrugIntructionFromFormulary(routedGenericID);
	}

	public List<DrugRoute> getRouteListFromMsClinical()
	{
		return drugDbService.getRouteList("*"); 
	}	
	public Set<AHFSClassification> getAHFSClassificationList(String inputParam)
	{
		return drugDbService.getAHFSClassificationList(inputParam);//* for all
	}
	public List<DrugFormulation> getFormulationList(String inputParam)
	{
		return drugDbService.getFormulationList(inputParam);//* for all
	}
	public List<DrugClassification> getDrugClassifications(String inputParam)
	{
		DrugClassificationRequest request = new DrugClassificationRequest();	
		request.setClassType(inputParam);//* for all
		return drugDbService.getDrugClassifications(request);
	}	
	public Set<String> getPackageSizeUOMList()
	{
		return drugDbService.getPackageSizeUOMList();	
	}
	public Set<String> getPackageTypeUOMList()
	{
		return drugDbService.getPackageTypeUOMList();
	}
	public Set<String> getStrengthUOMList()
	{
		return drugDbService.getStrengthUOMList();
	}
	public List<Unit> getUnitList()
	{
		DrugRequest dreq = new DrugRequest();
		dreq.setId("");
		dreq.setIdType("All");
		return drugDbService.getUnitList(dreq);
	}
	
	/**
	 * This method is used to return reference data details as per formularyid
	 * 
	 * @param nFormularyId
	 * @param userId
	 * @return
	 */
	public Map<String, Object> getReferenceDataAsPerFormularyId(int nFormularyId, int userId) {
		if(nFormularyId<=0){
			throw new InvalidParameterException("Invalid formularyId");
		}
		if(userId<=0){
			throw new InvalidParameterException("Invalid userId");
		}
		StatusMap dictResp = new StatusMap();
		int nRoutedGenericItemId = 0;
		try {
			nRoutedGenericItemId = forSetupDao.getRoutedGenericItemId(nFormularyId);
			Map<String, Object> requestParams = new HashMap<String, Object>();
			requestParams.put("queryContext", "ALL");
			requestParams.put("context", "ITEM_REFDATA");
			requestParams.put("itemId", nRoutedGenericItemId);
			requestParams.put("userId", userId);
			dictResp.setCustom("referenceDataDetail", refDataSvc.getReferenceData(requestParams));
			dictResp.setCustom("status", "success");
		} catch (Exception e) {
			dictResp.put("data", null);
			EcwLog.AppendExceptionToLog(e);
		}
		return dictResp;
	}
	
	/**
	 * This method is used to return associated product details based on GPI 
	 * @param strGPI
	 * @return
	 */
	public List<TemplateForAssoProducts> getAssociatedProductDetailsByGPI(String strGPI) {
		if ("".equals(Util.trimStr(strGPI))) {
			throw new InvalidParameterException("Invalid GPI");
		}
		return forSetupDao.getAssociatedProductDetailsByGPI(strGPI);
	} 
	
	/** 
	 * This method is used to return associated product details based on GPI 
	 * @param strRoutedDrugId
	 * @return
	 */
	public List<TemplateForAssoProducts> getAssociatedProductDetailsByRoutedDrugId(String strRoutedDrugId) {
		if ("".equals(Util.trimStr(strRoutedDrugId))) {
			throw new InvalidParameterException("Invalid Routed Drug Id");
		}
		return forSetupDao.getAssociatedProductDetailsByRoutedDrugId(strRoutedDrugId);
	}
	
	/*
	 * this method is used to return billing information for hcpcs code
	 *  @param cptcodeitemid
	 *  @param nUserId
	 * */
	public List<TemplateForBillingInfo> getBillingInfo(int cptcodeitemid,int nUserId) {
		if (nUserId <= 0) {
			throw new InvalidParameterException("Invalid User ID");
		}
		return forSetupDao.getBillingInfo(cptcodeitemid);
	}
	
	/*
	 * This method is used to return formulary drug title with specific format
	 * @param drugId
	 * @return
	 * */
	public String getFormularyDrugTitle(String drugId)
	{
		if ("".equals(Util.trimStr(drugId))) {
			throw new InvalidParameterException("Invalid Drug Id");
		}
		return forSetupDao.getFormularyDrugTitle(drugId);
	}
}