package inpatientWeb.admin.pharmacySettings.formularySetup.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import inpatientWeb.admin.pharmacySettings.formularySetup.dao.FormularySearchParam;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.Template;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.TemplateForAssoProducts;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.TemplateForCommonSetting;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.TemplateForDrug;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.TemplateForFacilities;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.TemplateForFormularyDrugRoutes;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.TemplateForOTRoutes;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.TemplateForOTS;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.TemplateForPreVitals;
import inpatientWeb.admin.pharmacySettings.formularySetup.service.FormularySetupService;
import inpatientWeb.admin.pharmacySettings.formularySetup.util.CustomMedInputs;
import inpatientWeb.admin.pharmacySettings.formularySetup.util.FormularyConstants;
import inpatientWeb.admin.pharmacySettings.formularySetup.util.SchemaDetails;
import inpatientWeb.admin.pharmacySettings.pharmacyHelpers.PharmacySecurityConstants;
import inpatientWeb.admin.usermanagement.securitysettings.annotation.CheckForUserAccess;
import inpatientWeb.cpoe.admin.ordermanagement.service.OrderManagementService;
import inpatientWeb.cpoe.admin.ordermanagement.service.OrderManagementStructDataService;
import inpatientWeb.cpoe.admin.orderruleengine.OrderRuleEngineService;
import inpatientWeb.cpoe.util.CPOEException;
import inpatientWeb.pharmacy.interaction.constants.InteractionConstants.INTERACTION;
import inpatientWeb.utils.StatusMap;
import inpatientWeb.utils.Util;

@Controller
@SessionAttributes("uid")
public class FormularySetupController 
{
	@Autowired
	public FormularySetupService formularySetupSvc;
	
	@Autowired
	private OrderManagementService orderService; 
	 
	@Autowired
	OrderRuleEngineService orderRuleEngineService;
	
	@Autowired
	private OrderManagementStructDataService orderManagementStructDataService;
	
	private static final String ROUTED_GENERIC_ITEMID = "routedGenericItemId";
	private static final String ITEMID = "itemId";
	private static final String FORMULARY_ID = "formularyId";
	private static final String DRUG_ID = "drugid";
	private static final String ACTION = "action";
	private static final String FORM_DATA = "FormData";
	private static final String SECURITY_SETTINGS = "SecuritySettings";
	private static final String STATUS = "status";
	
	@RequestMapping(value="/formularysetupcontroller.go/associatedProductModal", method=RequestMethod.GET)
	public String associatedProductModal(HttpServletRequest request, HttpServletResponse response)  {
		return "admin/pharmacySettings/formularySetup/Views/AssociatedProductModal";
	}
	
	@RequestMapping(value="/formularysetupcontroller.go/associatedProductModalForCustomDrugs", method=RequestMethod.GET)
	public String associatedProductModalForCustomDrugs(HttpServletRequest request, HttpServletResponse response)  {
		return "admin/pharmacySettings/formularySetup/Views/AssociatedProductModalForCustomDrugs";
	}
	
	/*Configure Question for formulary--start */
	
	@RequestMapping(value="/formularysetupcontroller.go/drugConfigQuestionModal", method=RequestMethod.GET)
	public String configureQuestionModal(HttpServletRequest request, HttpServletResponse response)  {
		return "admin/pharmacySettings/formularySetup/Views/modal/drugConfigQueModal";
	}
	
	@RequestMapping(value="/formularysetupcontroller.go/configureFields", method=RequestMethod.GET)
	public String configureFieldsModal(HttpServletRequest request, HttpServletResponse response)  {
		return "admin/pharmacySettings/formularySetup/Views/modal/configureFieldModal";
	}
	
	@RequestMapping(value="/formularysetupcontroller.go/getAllConfigFieldList",method= {RequestMethod.POST})
	public @ResponseBody Object getAllConfigFieldList(HttpServletRequest request, @RequestParam("userId") int loggedInUserId,@RequestParam("itemId") int itemId)  {
		String str=null;
		HashMap<String, Object> filterMap = new HashMap<>();
		try {
			filterMap.put("loggedInUserId", loggedInUserId);
			
			filterMap.put(ITEMID, itemId);
			filterMap.put("orderCategoryId", 0);
			filterMap.put("orderItemId", itemId);
			filterMap.put("orderSubTypeId", itemId);
				
			return orderService.getFieldConfig(filterMap);
				
		} catch (Exception e) {
			EcwLog.AppendExceptionToLog(e);
		}
		return str;
	}
	
	@RequestMapping(value="/formularysetupcontroller.go/saveConfigFieldList",method= {RequestMethod.POST})
	public @ResponseBody Object saveConfigFieldList(HttpServletRequest request,
	        @RequestParam Map<String, Object> dictRequestParams)  {
		String str=null;
		
		try {
			String configField = (String) dictRequestParams.get("configField");
			List<Map<String,Object>> configFieldlist = new ObjectMapper().readValue(configField, new TypeReference<List<Map<String, Object>>>() {}); 
					
			orderManagementStructDataService.serveAddUpdateConfigureFields(configFieldlist,Util.getIntValue(dictRequestParams, ITEMID), 0, 0, Util.getTrUserId(request));
				
		} catch (Exception e) {
			EcwLog.AppendExceptionToLog(e);
		}
		return str;
	}
	
	/*Configure Question for formulary--end */
	
	@RequestMapping(value="/formularysetupcontroller.go/formularyAdminSetup", method=RequestMethod.GET)
	public String adminFrequencyDictionary(HttpServletRequest request, HttpServletResponse response)  {
		return "admin/pharmacySettings/formularySetup/Views/FormularySetup";
	}
	
	
	/*Fetch formulary setup drug list*/
	
    @RequestMapping(value="/formularysetupcontroller.go/getFormularySetupDrugList",method = {RequestMethod.POST})
    public @ResponseBody StatusMap getFormularySetupDrugList(HttpServletRequest request, HttpServletResponse response,@ModelAttribute("uid") int trUserId){
    	String searchBy = Util.trimStr(request.getParameter("searchBy"));
    	String searchValue = Util.trimStr(request.getParameter("searchValue")); 
    	String status = Util.trimStr(request.getParameter(STATUS));
    	String sortBy = Util.trimStr(request.getParameter("sortBy"));
    	String sortOrder = Util.trimStr(request.getParameter("sortOrder"));
    	String searchFrom = Util.trimStr(request.getParameter("searchFrom"));
    	int recordsPerPage = Integer.parseInt(Util.trimStr(request.getParameter("recordsPerPage")));
		int selectedPage = Integer.parseInt(Util.trimStr(request.getParameter("selectedPage")));
		StatusMap result = new StatusMap();
		
		try
		{
			List<Template> tmpllist= null;
			int no = 0;
			
			FormularySearchParam fSearchParamObj = new FormularySearchParam();
			fSearchParamObj.setSearchBy(searchBy);
			fSearchParamObj.setSearchValue(searchValue);
			fSearchParamObj.setStatus(status);
			fSearchParamObj.setSortBy(sortBy);
			fSearchParamObj.setSortOrder(sortOrder);
			fSearchParamObj.setRecordsPerPage(recordsPerPage);
			fSearchParamObj.setSelectedPage(selectedPage);
			
			if("component".equalsIgnoreCase(searchFrom))
			{
				 tmpllist= formularySetupSvc.getFormularySetupDrugListForComponent(fSearchParamObj);
				 no = formularySetupSvc.getFormularySetupDrugCounterForComponent(fSearchParamObj);
			}
			else
			{
				tmpllist= formularySetupSvc.getFormularySetupDrugList(fSearchParamObj);
				no = formularySetupSvc.getFormularySetupDrugCounter(fSearchParamObj);
			}
			result.put("drugList", tmpllist);
			result.put("drugTotalCount", no);   
			result.put("routeList",formularySetupSvc.getRouteListFromMsClinical());
			result.put(SECURITY_SETTINGS, formularySetupSvc.getConfigureFormularySecurityItemsForUser(trUserId));
			result.setSuccess();    
		}
		catch(RuntimeException ex)
		{
			EcwLog.AppendExceptionToLog(ex);
		}
		return result;
    }
    
    /* get drug list total counter */
    @RequestMapping(value="/formularysetupcontroller.go/getFormularySetupDrugCounter",method = {RequestMethod.POST},produces={MediaType.ALL_VALUE})
    public @ResponseBody ResponseEntity<Integer> getFormularySetupDrugCounter(HttpServletRequest request, HttpServletResponse response){
    	
    	String searchBy = Util.trimStr(request.getParameter("searchBy"));
    	String searchValue = Util.trimStr(request.getParameter("searchValue"));
    	String status = Util.trimStr(request.getParameter(STATUS));
    	FormularySearchParam fSearchParamObj = new FormularySearchParam();
		fSearchParamObj.setSearchBy(searchBy);
		fSearchParamObj.setSearchValue(searchValue);
		fSearchParamObj.setStatus(status);
    	int no = formularySetupSvc.getFormularySetupDrugCounter(fSearchParamObj);
    	return new ResponseEntity<>(no,HttpStatus.OK);
    }  
    
    /*Fetch drug details*/
    @RequestMapping(value="/formularysetupcontroller.go/getDrugDetails",method = {RequestMethod.POST},produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public @ResponseBody StatusMap getDrugDetails(HttpServletRequest request, HttpServletResponse response,@ModelAttribute("uid") int trUserId){
    	
    	String strDrugId = Util.trimStr(request.getParameter(DRUG_ID));
    	String iscustommed = Util.trimStr(request.getParameter("iscustommed"));
		StatusMap result = new StatusMap();
		try
		{
			result.put("drugList", formularySetupSvc.getDrugDetails(strDrugId));
			try 
			{ 
				if("1".equals(iscustommed))
				{  
					result.put("formulationList", formularySetupSvc.getFormulationList("*"));
					result.put("AHFSClassification",formularySetupSvc.getAHFSClassificationList("*"));
					result.put("DrugClassification_TC2",formularySetupSvc.getDrugClassifications("TC2"));
					result.put("DrugClassification_TC4",formularySetupSvc.getDrugClassifications("TC4"));
					result.put("DrugClassification_TC6",formularySetupSvc.getDrugClassifications("TC6"));
					result.put("PackageSizeUomList",formularySetupSvc.getPackageSizeUOMList());
					result.put("PackageTypeUomList",formularySetupSvc.getPackageTypeUOMList());
					result.put("StrengthUOMList",formularySetupSvc.getStrengthUOMList());
					result.put("UnitList",formularySetupSvc.getUnitList());
				}
				List<SchemaDetails> sList = new ArrayList<SchemaDetails>();
		    	sList.add(new SchemaDetails("ip_ptmedicationorders_detail","drugformularyid",""));//suggested by malav : no deleteflag in table
		    	sList.add(new SchemaDetails("ip_orderset_med_orderitems_details","formularyId",""));//suggested by malav
		    	sList.add(new SchemaDetails("ip_ptstagingmedicationorders_detail","drugformularyid","deleteflag"));//suggested by malav
		    	
		    	if(formularySetupSvc.isDataInUse(sList, strDrugId))
		    	{
		    		result.put("isFormularyInUse",true);
		    	}
		    	else
		    	{
		    		result.put("isFormularyInUse",false);
		    	}
			}
			catch(Exception ex)
			{
				EcwLog.AppendExceptionToLog(ex);
				result.put("Message", "something went wrong");
			}
			result.put(SECURITY_SETTINGS, formularySetupSvc.getConfigureFormularySecurityItemsForUser(trUserId));
			result.put("WEB_SERVICE_FAILURE_MSG", FormularyConstants.WEB_SERVICE_FAILURE_MSG);
			result.setSuccess();
		}
		catch(RuntimeException ex)
		{
			EcwLog.AppendExceptionToLog(ex);
			result.put("Message", "something went wrong");
		}
		return result;
    } 
    
    /*Fetch Associated Product details*/
    @RequestMapping(value="/formularysetupcontroller.go/getAssociatedProductDetails",method = {RequestMethod.POST},produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public @ResponseBody ResponseEntity<List<TemplateForAssoProducts>> getAssociatedProductDetails(HttpServletRequest request, HttpServletResponse response){
    	String strDrugId = Util.trimStr(request.getParameter(DRUG_ID));
		List<TemplateForAssoProducts> tmpllist= formularySetupSvc.getAssociatedProductDetails(strDrugId);
		return new ResponseEntity<>(tmpllist,HttpStatus.OK);
    } 
    
    /*Fetch Associated Product details by NDC Code*/
    @RequestMapping(value="/formularysetupcontroller.go/getAssociatedProductDetailsByNDC",method = {RequestMethod.POST},produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public @ResponseBody ResponseEntity<List<TemplateForAssoProducts>> getAssociatedProductDetailsByNDC(HttpServletRequest request, HttpServletResponse response){
    	String strNDC = Util.trimStr(request.getParameter("ndc"));
		List<TemplateForAssoProducts> tmpllist= formularySetupSvc.getAssociatedProductDetailsByNDC(strNDC);
		return new ResponseEntity<>(tmpllist,HttpStatus.OK);
    } 
    
    
    
    /*Fetch order type setup details*/
    @RequestMapping(value="/formularysetupcontroller.go/getOrderTypeSetupDetails",method = {RequestMethod.POST},produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public @ResponseBody ResponseEntity<List<TemplateForOTS>> getOrderTypeSetupDetails(HttpServletRequest request, HttpServletResponse response){
    	
    	String strDrugId = Util.trimStr(request.getParameter(DRUG_ID));
		List<TemplateForOTS> tmpllist= formularySetupSvc.getOrderTypeSetupDetails(strDrugId);
		return new ResponseEntity<>(tmpllist,HttpStatus.OK);
    } 
    
    /*Fetch IV And ComplexOrder Diluent Data*/
    @RequestMapping(value="/formularysetupcontroller.go/getIVAndComplexOrderDiluentData",method = {RequestMethod.POST},produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public @ResponseBody ResponseEntity<List<TemplateForOTS>> getIVAndComplexOrderDiluentData(HttpServletRequest request, HttpServletResponse response){
    	
		List<TemplateForOTS> tmpllist= formularySetupSvc.getIVAndComplexOrderDiluentData();
		return new ResponseEntity<>(tmpllist,HttpStatus.OK);
    } 
    
    
    /*Fetch Facilities list*/
    @RequestMapping(value="/formularysetupcontroller.go/getFacilitiesList",method = {RequestMethod.POST},produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public @ResponseBody ResponseEntity<List<TemplateForFacilities>> getFacilitiesList(HttpServletRequest request, HttpServletResponse response,@ModelAttribute("uid") int trUserId){
    	
		List<TemplateForFacilities> tmpllist= formularySetupSvc.getFacilitiesList(trUserId);
		return new ResponseEntity<>(tmpllist,HttpStatus.OK);
    }  
    /*Fetch drug routes */
    @RequestMapping(value="/formularysetupcontroller.go/getFormularyDrugRoutesList",method = {RequestMethod.POST},produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public @ResponseBody ResponseEntity<List<TemplateForFormularyDrugRoutes>> getFormularyDrugRoutesList(HttpServletRequest request, HttpServletResponse response){
    	
		List<TemplateForFormularyDrugRoutes> tmpllist= formularySetupSvc.getFormularyDrugRoutesList();
		return new ResponseEntity<>(tmpllist,HttpStatus.OK);
    }  
    
    /*update drug details*/
   	@CheckForUserAccess (securitySettingKeys = {PharmacySecurityConstants.ADD_ConfigureFormulary_SECURITY_KEY,PharmacySecurityConstants.EDIT_ConfigureFormulary_SECURITY_KEY})
    @RequestMapping(value="/formularysetupcontroller.go/insertUpdateForDrugDetails",method = {RequestMethod.POST},produces={MediaType.ALL_VALUE})
    public @ResponseBody ResponseEntity<LinkedHashMap<String, Object>> updateForDrugDetails(HttpServletRequest request, HttpServletResponse response,@ModelAttribute("uid") int nUserId){
    	LinkedHashMap<String, Object> responseMap = null;
    	String action = Util.trimStr(request.getParameter(ACTION));
    	CustomMedInputs custMedObj = new CustomMedInputs();
    	custMedObj.setJsonDrugData(Util.trimStr(request.getParameter("jsonDrugData")));
    	custMedObj.setOrderTypeSetup(Util.trimStr(request.getParameter("orderTypeSetup")));
    	custMedObj.setIngredientList(Util.trimStr(request.getParameter("ingredientList")));
    	custMedObj.setRouteList(Util.trimStr(request.getParameter("routeList")));
    	custMedObj.setDosingRouteList(Util.trimStr(request.getParameter("dosingRouteList")));
    	custMedObj.setFrequencyList(Util.trimStr(request.getParameter("frequencyList")));
    	custMedObj.setFormulationList(Util.trimStr(request.getParameter("formulationList")));
    	custMedObj.setSelectedFacilitiesList(Util.trimStr(request.getParameter("selectedFacilitiesList")));
    	custMedObj.setSelectedServiceTypeList(Util.trimStr(request.getParameter("selectedServiceTypeList")));
    	custMedObj.setVisData(Util.trimStr(request.getParameter("visData")));
    	custMedObj.setAssociatedProducts( Util.trimStr(request.getParameter("associatedProducts")));
    	custMedObj.setAhfsClassList(Util.trimStr(request.getParameter("ahfsClassList")));
    	custMedObj.setDrugClassList(Util.trimStr(request.getParameter("drugClassList")));
    	custMedObj.setIsAddDuplicate(Util.trimStr(request.getParameter("isAddDuplicate")));
    	custMedObj.setDispenseUOMArr(Util.trimStr(request.getParameter("dispenseUOMArr")));
    	custMedObj.setJsonNotesObj(Util.trimStr(request.getParameter("jsonNotesObj")));    	
    	custMedObj.setJsonDrugDispenseObj(Util.trimStr(request.getParameter("jsonDrugDispenseObj")));
    	custMedObj.setIsContinue(Util.trimStr(request.getParameter("isContinue")));
    	responseMap = formularySetupSvc.insertUpdateCustomDrugDetails(custMedObj, action, nUserId);
    	return new ResponseEntity<>(responseMap,HttpStatus.OK); 
    }  
    
    @RequestMapping(value="/formularysetupcontroller.go/checkForDrugInUse",method = {RequestMethod.POST},produces={MediaType.ALL_VALUE})
    public @ResponseBody ResponseEntity<LinkedHashMap<String, Object>> checkForDrugInUse(HttpServletRequest request, HttpServletResponse response,@ModelAttribute("uid") int nUserId){
    	LinkedHashMap<String, Object> responseMap = null;
    	CustomMedInputs custMedObj = new CustomMedInputs();
    	custMedObj.setJsonDrugData(Util.trimStr(request.getParameter("jsonDrugData")));
    	responseMap = formularySetupSvc.checkForDrugInUse(custMedObj,nUserId);
    	return new ResponseEntity<>(responseMap,HttpStatus.OK); 
    } 
    
    /*
     * Required to check duplicate drug alias name
     * */
    
    @RequestMapping(value="/formularysetupcontroller.go/checkForDuplicateDrugAlias",method = {RequestMethod.POST},produces={MediaType.ALL_VALUE})
    public @ResponseBody ResponseEntity<LinkedHashMap<String, Object>> checkForDuplicateDrugAlias(@RequestBody Map<String,Object> commonSettingMap,@ModelAttribute("uid") int trUserId){
    	LinkedHashMap<String, Object> responseMap = null;
    	
    	String drugAlias = Util.trimStr(Util.getStrValue(commonSettingMap, "drugAlias"));
    	String routedGenericItemId = Util.trimStr(Util.getStrValue(commonSettingMap, "routedGenericItemId"));
    	responseMap = formularySetupSvc.checkForDuplicateDrugAlias(drugAlias,routedGenericItemId,trUserId);
    	return new ResponseEntity<>(responseMap,HttpStatus.OK); 
    } 
    
    /*update drug common setting data*/
  
    @RequestMapping(value="/formularysetupcontroller.go/insertUpdateForCommonSettings",method = {RequestMethod.POST},produces={MediaType.ALL_VALUE})
    public @ResponseBody ResponseEntity<Integer> insertUpdateForCommonSettings(@RequestBody Map<String,Object> commonSettingMap,@ModelAttribute("uid") int nUserId){
  
    	String jsonDrugObj = Util.trimStr(Util.getStrValue(commonSettingMap,"jsonDrugObj"));
    	String routedGenericItemId = Util.trimStr(Util.getStrValue(commonSettingMap,ROUTED_GENERIC_ITEMID));
    	String formularyId = Util.trimStr(Util.getStrValue(commonSettingMap,FORMULARY_ID));
    	String selectedPRNIds = Util.trimStr(Util.getStrValue(commonSettingMap,"selectedPRNIds"));
    	String jsonVitalsObj = Util.trimStr(Util.getStrValue(commonSettingMap,"jsonVitalsObj"));
    	
    	int nRoutedGenericItemId = 0;
    	int nFormularyId = 0;
    	if(!"".equals(routedGenericItemId))
    	{
    		nRoutedGenericItemId = Integer.parseInt(routedGenericItemId);
    	}
    	if(!"".equals(formularyId))
    	{
    		nFormularyId = Integer.parseInt(formularyId);
    	}
    	int no = 0;
    	try {
	    	if(nRoutedGenericItemId > 0)
	    	{
	    		no = formularySetupSvc.updateCommonSettingDrugData(nFormularyId,nRoutedGenericItemId,jsonDrugObj, nUserId);
	    		if(!"".equals(jsonVitalsObj))
	    		{
						no = formularySetupSvc.insertUpdateForPreVitals(nFormularyId,jsonVitalsObj,nRoutedGenericItemId,nUserId);
	    		}
	    		formularySetupSvc.setPRNIndicationData(nRoutedGenericItemId, selectedPRNIds, nUserId);
	    	}
    	} catch (JSONException | inpatientWeb.Global.ecw.ambulatory.json.JSONException | IOException e) {
    		EcwLog.AppendExceptionToLog(e);
		}
    	return new ResponseEntity<>(no,HttpStatus.OK);
    }
    
    /* check for sliding scale */
    @RequestMapping(value="/formularysetupcontroller.go/getSlidingScale",method = {RequestMethod.POST},produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public @ResponseBody StatusMap getSlidingScale(HttpServletRequest request, HttpServletResponse response,@ModelAttribute("uid") int trUserId){
    	
    	String strDrugId = Util.trimStr(request.getParameter(DRUG_ID));
		List<TemplateForOTS> tmpllist= formularySetupSvc.getSlidingScale(strDrugId);
		StatusMap result = new StatusMap();
		result.put("otsData", tmpllist);
		result.setSuccess();    
		return result;
    } 
    
    /*delete drug details*/
	@CheckForUserAccess (securitySettingKeys = {PharmacySecurityConstants.Delete_ConfigureFormulary_SECURITY_KEY})
    @RequestMapping(value="/formularysetupcontroller.go/deleteDrugDetails",method = {RequestMethod.POST},produces={MediaType.ALL_VALUE})
    public @ResponseBody StatusMap deleteDrugDetails(HttpServletRequest request, HttpServletResponse response,@ModelAttribute("uid") int nUserId){
    	
    	String strDrugId = Util.trimStr(request.getParameter(DRUG_ID));
    	String strItemName = Util.trimStr(request.getParameter("itemName"));
    	String routedGenericItemId = Util.trimStr(request.getParameter(ROUTED_GENERIC_ITEMID));
    	int nRoutedGenericItemId = 0;
    	int no = 0;
    	StatusMap result = new StatusMap();
    	try
    	{
	    	if(!"".equals(routedGenericItemId))
	    	{
	    		nRoutedGenericItemId = Integer.parseInt(routedGenericItemId);
	    	}
	    	List<SchemaDetails> sList = new ArrayList<SchemaDetails>();
	    	sList.add(new SchemaDetails("ip_med_signaturedetails","drugformularyid","deleteflag"));
	    	sList.add(new SchemaDetails("ip_pharmacy_billing_detail","formularyid","deleteflag"));
	    	sList.add(new SchemaDetails("ip_ptmedicationorders_detail","drugformularyid",""));//suggested by malav : no deleteflag in table
	    	sList.add(new SchemaDetails("ip_orderset_med_orderitems_details","formularyId",""));//suggested by malav
	    	sList.add(new SchemaDetails("ip_ptstagingmedicationorders_detail","drugformularyid","deleteflag"));//suggested by malav
	    	sList.add(new SchemaDetails("ip_drug_admin_scan","drugformularyid",""));//suggested by pavneet
	    	sList.add(new SchemaDetails("ip_pharmacy_billing_detail","formularyid","deleteflag"));//suggested by pavneet
	    	
	    	if(!formularySetupSvc.isDataInUse(sList, strDrugId))
	    	{
	    		no = formularySetupSvc.deleteDrugDetails(strDrugId,nRoutedGenericItemId,strItemName,nUserId);
	    	}
	    	else
	    	{
	    		result.put("Message", "Formulary Item cannot be deleted as it is in use. Please mark formulary item as inactive to prevent future use.");
	    	}
			result.put("Count", no);
			result.setSuccess();   
    	}
    	catch(RuntimeException | inpatientWeb.Global.ecw.ambulatory.json.JSONException ex)
    	{
    		result.setFail();
    		EcwLog.AppendExceptionToLog(ex);
    	}
    	return result;
    }  
    
    /*delete associated products
     * */
    @RequestMapping(value="/formularysetupcontroller.go/deleteAssociatedProducts",method = {RequestMethod.POST},produces={MediaType.ALL_VALUE})
    public @ResponseBody ResponseEntity<Integer> deleteAssociatedProducts(HttpServletRequest request, HttpServletResponse response,@ModelAttribute("uid") int trUserId){
    	
    	String formularyId = Util.trimStr(request.getParameter(FORMULARY_ID));
    	String assoProdId = Util.trimStr(request.getParameter("assoProdId"));
    	String ndc = Util.trimStr(request.getParameter("ndc"));
    	int no = 0;
    	try
    	{
    		no = formularySetupSvc.deleteAssociatedProducts(formularyId,assoProdId,ndc,trUserId);
    	}
    	catch(RuntimeException ex)
    	{
    		EcwLog.AppendExceptionToLog(ex);
    	}
    	return new ResponseEntity<>(no,HttpStatus.OK);
    }  
    
    /* delete prerequisite details - vitals
     * */
    @RequestMapping(value="/formularysetupcontroller.go/deletePrerequisite",method = {RequestMethod.POST},produces={MediaType.ALL_VALUE})
    public @ResponseBody ResponseEntity<Integer> deletePrerequisite(HttpServletRequest request, HttpServletResponse response,@ModelAttribute("uid") int trUserId){
    	
    	int nFormularyId = 0;
    	String formularyId = Util.trimStr(request.getParameter(FORMULARY_ID));
    	String vitalId = Util.trimStr(request.getParameter("vitalId"));
    	if(!"".equals(formularyId))
    	{
    		nFormularyId = Integer.parseInt(formularyId);
    	}
    	int no = 0;
		try {
			no = formularySetupSvc.deletePrerequisite(nFormularyId,vitalId,trUserId);
		} catch (inpatientWeb.Global.ecw.ambulatory.json.JSONException e) {
			EcwLog.AppendExceptionToLog(e);
		}
    	return new ResponseEntity<>(no,HttpStatus.OK);
    }  
    
    /*delete associated products lot details*/
    @RequestMapping(value="/formularysetupcontroller.go/deleteAssociatedProductsLotDetails",method = {RequestMethod.POST},produces={MediaType.ALL_VALUE})
    public @ResponseBody ResponseEntity<Integer> deleteAssociatedProductsLotDetails(HttpServletRequest request, HttpServletResponse response,@ModelAttribute("uid") int trUserId){
    	
    	String lotId = Util.trimStr(request.getParameter("lotId"));
    	String formularyId = Util.trimStr(request.getParameter(FORMULARY_ID));
    	String assoProdId = Util.trimStr(request.getParameter("assoProdId"));
    	String lotno = Util.trimStr(request.getParameter("lotno"));
    	int no = 0;
		try {
			no = formularySetupSvc.deleteAssociatedProductsLotDetails(lotId,formularyId,assoProdId,lotno,trUserId);
		} catch (inpatientWeb.Global.ecw.ambulatory.json.JSONException e) {
			EcwLog.AppendExceptionToLog(e);
		}
    	return new ResponseEntity<>(no,HttpStatus.OK);
    }  
    
    /*insert update drug alias*/
    @RequestMapping(value="/formularysetupcontroller.go/insertUpdateForDrugAlias",method = {RequestMethod.POST},produces={MediaType.ALL_VALUE})
    public @ResponseBody ResponseEntity<Integer> updateForDrugAlias(HttpServletRequest request, HttpServletResponse response,@ModelAttribute("uid") int nUserId){
    	
    	String jsonDrugData = Util.trimStr(request.getParameter("jsonDrugData"));
    	int no = 0;
    	try {
	    	if(!"".equals(jsonDrugData))
	    	{
				no = formularySetupSvc.insertUpdateDrugAlias(jsonDrugData,nUserId);
	    	}
    	} catch (IOException e) {
    		EcwLog.AppendExceptionToLog(e);
		}
    	return new ResponseEntity<>(no,HttpStatus.OK);
    }  
	
	@RequestMapping(value="/formularysetupcontroller.go/addEditRx", method=RequestMethod.GET)
	public String adminAddEditRx(HttpServletRequest request, HttpServletResponse response)  {
		return "admin/pharmacySettings/formularySetup/Views/modal/addEditRxModal";
	}
	
	@RequestMapping(value="/formularysetupcontroller.go/formularyModal", method=RequestMethod.GET)
	public String launchFormularyModal(HttpServletRequest request, HttpServletResponse response)  {
		return "admin/pharmacySettings/formularySetup/Views/modal/FormularyModal";
	}
	
	@RequestMapping(value="/formularysetupcontroller.go/associatedBrands", method=RequestMethod.GET)
	public String launchAssociatedBrands(HttpServletRequest request, HttpServletResponse response)  {
		return "admin/pharmacySettings/formularySetup/Views/modal/associatedBrands";
	}
	
	@RequestMapping(value="/formularysetupcontroller.go/autoSubstitution", method=RequestMethod.GET)
	public String launchAutoSubstitution(HttpServletRequest request, HttpServletResponse response)  {
		return "admin/pharmacySettings/formularySetup/Views/modal/autoSubstitution";
	}
	
	@RequestMapping(value="/formularysetupcontroller.go/clinical", method=RequestMethod.GET)
	public String launchClinical(HttpServletRequest request, HttpServletResponse response)  {
		return "admin/pharmacySettings/formularySetup/Views/modal/clinical";
	}
	
	@RequestMapping(value="/formularysetupcontroller.go/specificSetting", method=RequestMethod.GET)
	public String launchSpecifiSetting(HttpServletRequest request, HttpServletResponse response)  {
		return "admin/pharmacySettings/formularySetup/Views/modal/specificSetting";
	}
	
	@RequestMapping(value="/formularysetupcontroller.go/commonSetting", method=RequestMethod.GET)
	public String launchCommonSetting(HttpServletRequest request, HttpServletResponse response)  {
		return "admin/pharmacySettings/formularySetup/Views/modal/commonSetting";
	}
	
	
	@RequestMapping(value="/formularysetupcontroller.go/drugInteraction", method=RequestMethod.GET)
	public String launchDrugInteraction(HttpServletRequest request, HttpServletResponse response)  {
		return "admin/pharmacySettings/formularySetup/Views/modal/drugInteraction";
	}
	
	@RequestMapping(value="/formularysetupcontroller.go/notes", method=RequestMethod.GET)
	public String launchNotes(HttpServletRequest request, HttpServletResponse response)  {
		return "admin/pharmacySettings/formularySetup/Views/modal/notes";
	}
	
	@RequestMapping(value="/formularysetupcontroller.go/orderManagement", method=RequestMethod.GET)
	public String launchOrderManagement(HttpServletRequest request, HttpServletResponse response)  {
		return "admin/pharmacySettings/formularySetup/Views/modal/orderManagement";
	}
	
	@RequestMapping(value="/formularysetupcontroller.go/preRequisite", method=RequestMethod.GET)
	public String launchPreRequisite(HttpServletRequest request, HttpServletResponse response)  {
		return "admin/pharmacySettings/formularySetup/Views/modal/preRequisite";
	}
	/*Fetch pre-requisite vitals data*/
    @RequestMapping(value="/formularysetupcontroller.go/getPreRequisiteVitalsDetails",method = {RequestMethod.POST},produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public @ResponseBody ResponseEntity<List<TemplateForPreVitals>> getPreRequisiteVitalsDetails(HttpServletRequest request, HttpServletResponse response){
    	
    	String routedGenericItemId = Util.trimStr(request.getParameter(ROUTED_GENERIC_ITEMID));
    	String formularyId = Util.trimStr(request.getParameter(FORMULARY_ID));
    	int nformularyId = 0;
    	if(!"".equals(formularyId))
    	{
    		nformularyId = Integer.parseInt(formularyId);
    	}
    	
		List<TemplateForPreVitals> tmpllist= formularySetupSvc.getPreRequisiteVitalsDetails(nformularyId,routedGenericItemId);
		return new ResponseEntity<>(tmpllist,HttpStatus.OK);
    }  
    
    /*Fetch reflex order data*/
    @RequestMapping(value="/formularysetupcontroller.go/loadReflexOrdersData",method = {RequestMethod.POST},produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public @ResponseBody ResponseEntity<Map<String, Object>> loadReflexOrdersData(HttpServletRequest request, HttpServletResponse response){
    	
    	String routedGenericItemId =  Util.trimStr(request.getParameter("routedGenericItemId"));
    	Map<String, Object> tempData = null;
    	int nRoutedGenericItemId = 0;
    	try
    	{
    		nRoutedGenericItemId = "".equals(routedGenericItemId)?-1:Integer.parseInt(routedGenericItemId);
	    	tempData = orderRuleEngineService.getAllRuleConfigForItem(nRoutedGenericItemId);
    	}
    	catch(CPOEException ex)
    	{
    		EcwLog.AppendExceptionToLog(ex);
    	}
		return new ResponseEntity<>(tempData,HttpStatus.OK);
    }  
	
	@RequestMapping(value="/formularysetupcontroller.go/referenceRange", method=RequestMethod.GET)
	public String launchReferenceRange(HttpServletRequest request, HttpServletResponse response)  {
		return "admin/pharmacySettings/formularySetup/Views/modal/referenceRange";
	}
	
	@RequestMapping(value="/formularysetupcontroller.go/rulesEngine", method=RequestMethod.GET)
	public String launchRulesEngine(HttpServletRequest request, HttpServletResponse response)  {
		return "admin/pharmacySettings/formularySetup/Views/modal/rulesEngine";
	}
	 /*Fetch order type setup routes */
    @RequestMapping(value="/formularysetupcontroller.go/getOrderTypeSetupRoutes",method = {RequestMethod.POST},produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public @ResponseBody ResponseEntity<List<TemplateForOTRoutes>> getOrderTypeSetupRoutes(HttpServletRequest request, HttpServletResponse response){
    	
    	String formularyid = Util.trimStr(request.getParameter("formularyid"));
		List<TemplateForOTRoutes> tmpllist= formularySetupSvc.getOrderTypeSetupRoutes(formularyid);
		return new ResponseEntity<>(tmpllist,HttpStatus.OK);
    }  
    
    /*get ordertype setup */
    @RequestMapping(value="/formularysetupcontroller.go/getOrderTypeSetupData",method = {RequestMethod.POST},produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public @ResponseBody ResponseEntity<List<TemplateForOTS>> getOrderTypeSetupData(HttpServletRequest request, HttpServletResponse response){
    	String formularyid = Util.trimStr(request.getParameter("formularyid"));
    	List<TemplateForOTS> otsData = formularySetupSvc.getOrderTypeSetupData(formularyid);
    	return new ResponseEntity<>(otsData,HttpStatus.OK);
    }
    
    /*Fetch formulary setup common settings*/
    @RequestMapping(value="formularysetupcontroller.go/loadCommonSettings",method = {RequestMethod.POST},produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public @ResponseBody StatusMap loadCommonSettings(HttpServletRequest request, HttpServletResponse response,@ModelAttribute("uid") int trUserId){
    	String formularyId = Util.trimStr(request.getParameter(FORMULARY_ID));
    	int nformularyId = 0;
    	if(!"".equals(formularyId)) 
    	{
    		nformularyId = Integer.parseInt(formularyId);
    	}
    	List<TemplateForCommonSetting> commonSettingData= formularySetupSvc.loadCommonSettings(nformularyId);
    	
    	StatusMap result = new StatusMap();
		result.put("drugDetails", commonSettingData);
		result.put(SECURITY_SETTINGS, formularySetupSvc.getConfigureFormularySecurityItemsForUser(trUserId));
		result.setSuccess();    
		return result;
    }
    /* load brand names */
    @RequestMapping(value="formularysetupcontroller.go/loadBrandNames",method = {RequestMethod.POST},produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public @ResponseBody ResponseEntity<List<TemplateForDrug>> loadBrandNames(HttpServletRequest request, HttpServletResponse response){
    	String routedGenericItemId = Util.trimStr(request.getParameter(ROUTED_GENERIC_ITEMID));
    	
    	int nRoutedGenericItemId = 0;
    	if(!"".equals(routedGenericItemId))
    	{
    		nRoutedGenericItemId = Integer.parseInt(routedGenericItemId);
    	}
    	List<TemplateForDrug> commonSettingData= formularySetupSvc.loadBrandNames(nRoutedGenericItemId);
    	return new ResponseEntity<>(commonSettingData,HttpStatus.OK);
    }
    /*Fetch medication list : ordertype is 1 : medication, 2: iv and 3 : complex order*/
    @RequestMapping(value="/formularysetupcontroller.go/getDrugListAsPerOrderType",method = {RequestMethod.POST},produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public @ResponseBody String getDrugListAsPerOrderType(HttpServletRequest request, HttpServletResponse response){
    	String orderType = Util.trimStr(request.getParameter("orderType"));
		List<Template> tmpllist= formularySetupSvc.getDrugListAsPerOrderType(orderType);
		JSONObject result = new JSONObject();
		try {
			result.put("medicationList", tmpllist);
		} catch (JSONException e) {
			EcwLog.AppendExceptionToLog(e);
		}
		return result.toString();
    }
    
    /**
     * @param dispenseMap
     * @return StatusMap
     * API to get drug dispense Detail
     */
    @RequestMapping(value="/formularysetupcontroller.go/getDrugDispenseDetail",method = {RequestMethod.POST})
    public @ResponseBody StatusMap getDrugDispenseDetail(@RequestBody Map<String,Object> dispenseMap){
    	 return formularySetupSvc.getDrugDispenseDetail(dispenseMap);
    } 
    /**
     * @param dispenseMap
     * @return StatusMap
     * API to save or update dispense details with quantity
     */
    @RequestMapping(value="/formularysetupcontroller.go/saveUpdateDispenseDetails",method = {RequestMethod.POST})
    public @ResponseBody StatusMap saveUpdateDispenseDetails(@RequestBody Map<String,Object> dispenseStockDetail){
    	return formularySetupSvc.saveUpdateDispenseDetails(dispenseStockDetail);
    }  
   
    /**
     * @param dispenseMap
     * @return  StatusMap 
     * API to get drug dispense detail location with available quantity 
     */
    @RequestMapping(value="/formularysetupcontroller.go/getStockAreaDetailWithQuantity",method = {RequestMethod.POST})
    public @ResponseBody  StatusMap getStockAreaDetailWithQuantity(@RequestBody Map<String,Object> dispenseStockDetail){
    	return formularySetupSvc.getStockAreaDetailWithQuantity(dispenseStockDetail);
    }
    /**
     * @param dispenseMap
     * @return  StatusMap 
     * API to  check duplicate Dispense Drug Code
     */
    @RequestMapping(value="/formularysetupcontroller.go/checkDispenseCodeInFormularyDetail",method = {RequestMethod.POST})
    public @ResponseBody  StatusMap checkDispenseCodeInFormularyDetail(@RequestBody Map<String,Object> dispenseMap){
    	return formularySetupSvc.checkDispenseCodeInFormularyDetail(dispenseMap);
    }
    
    /**
     * Gets the routed generic id.
     *
     * @param request the request
     * @return the routed generic id
     */
    @RequestMapping(value = "/formularysetupcontroller.go/getRoutedGenericId",  method = {RequestMethod.POST})
	@ResponseBody public Map<String, Object> getRoutedGenericId(HttpServletRequest request) 
	{
		StatusMap oStatus =  new StatusMap();
		try		
		{	
			String  formularyId= request.getParameter(FORMULARY_ID);
			Map<String, String> respMap = formularySetupSvc.getRoutedGenericId(formularyId);
			oStatus.setObject(respMap);
			oStatus.setSuccess();
			return oStatus;
		}
		catch(Exception ex){
			EcwLog.AppendExceptionToLog(ex);
			oStatus.setFail("Exception ocurred to get RoutedGenericId in formulary setup");
			oStatus.setObject(Collections.emptyMap());
			return oStatus;
		}
	}
    
    /**
     * Save or update drug drug interaction.
     *
     * @param request the request
     * @param userId the user id
     * @return the status map
     */
    @RequestMapping(value = "/formularysetupcontroller.go/saveOrUpdateDrugDrugInteraction",  method = {RequestMethod.POST})
   	@ResponseBody public StatusMap saveOrUpdateDrugDrugInteraction(HttpServletRequest request, @ModelAttribute("uid") Integer userId)
   	{
    	StatusMap oStatus =  new StatusMap();
		try{
			oStatus.put("saveStatus","");
			oStatus.put("updateStatus","");
			JSONParser parser = new JSONParser();
			org.json.simple.JSONObject jObject = (org.json.simple.JSONObject) parser.parse(request.getParameter(FORM_DATA));
			if(jObject.containsKey("obj")){
				@SuppressWarnings("unchecked")
				Map<String, Object> saveMap = (Map<String, Object>) jObject.get("obj");
				oStatus.put("saveStatus",formularySetupSvc.saveDrugDrugInteraction(saveMap, userId));
			}
			if(jObject.containsKey("updateObj")){
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> updateMapList = (List<Map<String, Object>>) jObject.get("updateObj");
				oStatus.put("updateStatus",formularySetupSvc.updateDrugDrugInteraction(updateMapList, userId));
			}
			return oStatus;
		}
		catch(Exception ex){
			EcwLog.AppendExceptionToLog(ex);
			oStatus.setFail("Exception ocurred to save or update drug drug interaction in formulary setup");
			oStatus.setObject(null);
			return oStatus;
		}
   	}
    
    /**
     * Delete drug drug interaction.
     *
     * @param request the request
     * @param userId the user id
     * @return the status map
     */
    @RequestMapping(value = "/formularysetupcontroller.go/deleteDrugDrugInteraction",  method = {RequestMethod.POST})
   	@ResponseBody public StatusMap deleteDrugDrugInteraction(HttpServletRequest request, @ModelAttribute("uid") Integer userId) 
   	{
		try{
			JSONParser parser = new JSONParser();
			org.json.simple.JSONObject jObject = (org.json.simple.JSONObject) parser.parse(request.getParameter(FORM_DATA));
			@SuppressWarnings("unchecked")
			Map<String, Object> requestMap = (Map<String, Object>) jObject.get("obj");
			requestMap.put(INTERACTION.UID.text(), userId);
			return formularySetupSvc.deleteDrugDrugInteraction(requestMap, userId);
		}
		catch(Exception ex){
			EcwLog.AppendExceptionToLog(ex);
			StatusMap oStatus =  new StatusMap();
			oStatus.setFail("Exception ocurred to delete drug drug interaction in formulary setup");
			oStatus.setObject(false);
			return oStatus;
		} 
   	}
    
    /**
     * Gets the saved drug drug interaction.
     *
     * @param request the request
     * @param userId the user id
     * @return the saved drug drug interaction
     */
    @RequestMapping(value = "/formularysetupcontroller.go/getSavedDrugDrugInteraction",  method = {RequestMethod.POST})
   	@ResponseBody public StatusMap getSavedDrugDrugInteraction(HttpServletRequest request, @ModelAttribute("uid") Integer userId) 
   	{
    	StatusMap oStatus =  new StatusMap();
		try{
			Integer routedGenericItemId= NumberUtils.toInt(request.getParameter(ROUTED_GENERIC_ITEMID));
			return formularySetupSvc.getSavedDrugDrugInteraction(routedGenericItemId);
		}
		catch(Exception ex){
			EcwLog.AppendExceptionToLog(ex);
			oStatus.setFail("Exception ocurred to get saved drug drug interaction in formulary setup");
			oStatus.setObject(Collections.emptyList());
			return oStatus;
		}
   	}
    
    /**
     * Check duplicate interaction.
     *
     * @param request the request
     * @return the status map
     */
    @RequestMapping(value = "/formularysetupcontroller.go/checkDuplicateInteraction",  method = {RequestMethod.POST})
   	@ResponseBody public StatusMap checkDuplicateInteraction(HttpServletRequest request) 
   	{
		try{
			JSONParser parser = new JSONParser();
			org.json.simple.JSONObject jObject = (org.json.simple.JSONObject) parser.parse(request.getParameter(FORM_DATA));
			@SuppressWarnings("unchecked")
			Map<String, Object> requestMap = (Map<String, Object>) jObject.get("obj");
			return formularySetupSvc.checkDuplicateInteraction(requestMap);
		}
		catch(Exception ex){
			EcwLog.AppendExceptionToLog(ex);
			StatusMap oStatus =  new StatusMap();
			oStatus.setFail("Exception ocurred to check duplicate drug drug interaction in formulary setup");
			oStatus.setObject(null);
			return oStatus;
		}
   	}
    
    /**
     * Save or update enabled interaction.
     *
     * @param request the request
     * @return the status map
     */
    @RequestMapping(value = "/formularysetupcontroller.go/saveOrUpdateEnableInteraction",  method = {RequestMethod.POST})
   	@ResponseBody public StatusMap saveOrUpdateEnableInteraction(HttpServletRequest request, @ModelAttribute("uid") Integer userId) 
   	{
    	StatusMap oStatus =  new StatusMap();
		try{
			JSONParser parser = new JSONParser();
			org.json.simple.JSONObject jObject = (org.json.simple.JSONObject) parser.parse(request.getParameter(FORM_DATA));
			if(jObject.containsKey("obj")){
				@SuppressWarnings("unchecked")
				Map<String, Object> saveMap = (Map<String, Object>) jObject.get("obj");
				oStatus.put(STATUS,formularySetupSvc.saveOrUpdateEnableInteraction(saveMap, userId));
			}
			return oStatus;
		}
		catch(Exception ex){
			EcwLog.AppendExceptionToLog(ex);
			oStatus.setFail("Exception ocurred to save or update enabled interaction in formulary setup");
			oStatus.setObject(null);
			return oStatus;
		}
   	}
    
    /**
     * fetch saved enabled interaction.
     *
     * @param request the request
     * @return the status map
     */
    @RequestMapping(value = "/formularysetupcontroller.go/fetchEnableInteraction",  method = {RequestMethod.POST})
   	@ResponseBody public StatusMap fetchEnableInteraction(HttpServletRequest request) 
   	{
    	StatusMap oStatus =  new StatusMap();
		try{
			Integer routedGenericItemId = Integer.parseInt(request.getParameter(ROUTED_GENERIC_ITEMID));
			Map<String, Object> saveMap = new HashMap<>();
			saveMap.put(ROUTED_GENERIC_ITEMID, routedGenericItemId);
			return formularySetupSvc.fetchEnableInteraction(saveMap);
		}
		catch(Exception ex){
			EcwLog.AppendExceptionToLog(ex);
			oStatus.setFail("Exception ocurred to save or update enabled interaction in formulary setup");
			oStatus.setObject(null);
			return oStatus;
		}
   	}
    @RequestMapping(value="/formularysetupcontroller.go/getBillingInfo",method = {RequestMethod.POST},produces={MediaType.ALL_VALUE})
    public @ResponseBody StatusMap getBillingInfo(@RequestBody Map<String,Object> inputDataMap,@ModelAttribute("uid") int trUserId){
    	int cptcodeitemid = Util.getIntValue(inputDataMap, "cptcodeitemid");
    	StatusMap responseMap = new StatusMap();
		try {
				responseMap.put("billingData", formularySetupSvc.getBillingInfo(cptcodeitemid,trUserId));
				responseMap.put("Message", "success");
				responseMap.setSuccess();
		} catch (RuntimeException ex) {
			EcwLog.AppendExceptionToLog(ex);
			responseMap.setFail("Error in getBillingInfo");
		}
		return responseMap;
    } 
} 



