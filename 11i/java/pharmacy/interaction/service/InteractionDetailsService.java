package inpatientWeb.pharmacy.interaction.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import inpatientWeb.Auth.exception.InternalServerException;
import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;
import inpatientWeb.Global.items.dao.IPItemkeyDAO;
import inpatientWeb.Global.medicationhelper.constant.MedicationConstants;
import inpatientWeb.Global.medicationhelper.constant.MedicationConstants.ReasonDictionaryType;
import inpatientWeb.Global.medicationhelper.constant.MedicationConstants.RxType;
import inpatientWeb.admin.pharmacySettings.configureDictionary.constant.FormularySignatureConstants;
import inpatientWeb.admin.pharmacySettings.configureDictionary.dao.TemplateReasonDictionaryItems;
import inpatientWeb.admin.pharmacySettings.configureDictionary.service.ConfigurationDictionaryService;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.FormularySetupDao;
import inpatientWeb.admin.usermanagement.securitysettings.service.SecuritySettingService;
import inpatientWeb.cpoe.util.OrdersUtil;
import inpatientWeb.eMAR.util.EMAREnum;
import inpatientWeb.pharmacy.drugdb.constant.DrugDbConstant.RESULT_CODES;
import inpatientWeb.pharmacy.drugdb.core.model.impl.ResponseData;
import inpatientWeb.pharmacy.drugdb.model.InteractionRequest;
import inpatientWeb.pharmacy.drugdb.util.DrugDbException;
import inpatientWeb.pharmacy.drugdb.util.Utils;
import inpatientWeb.pharmacy.interaction.constants.InteractionConstants.INTERACTION;
import inpatientWeb.pharmacy.interaction.constants.InteractionConstants.INTERACTION_KEY_ID;
import inpatientWeb.pharmacy.interaction.constants.InteractionConstants.INTERACTION_MESSAGE;
import inpatientWeb.pharmacy.interaction.constants.InteractionConstants.INTERACTION_SEVERITY_TYPES;
import inpatientWeb.pharmacy.interaction.constants.InteractionConstants.INTERACTION_TYPES;
import inpatientWeb.pharmacy.interaction.constants.InteractionConstants.ORDERS_SCREEN;
import inpatientWeb.pharmacy.interaction.dao.InteractionDao;
import inpatientWeb.pharmacy.interaction.model.DrugData;
import inpatientWeb.pharmacy.interaction.model.DrugDoseOrder;
import inpatientWeb.pharmacy.interaction.model.DrugOrderDetails;
import inpatientWeb.pharmacy.interaction.model.FrequencyDetails;
import inpatientWeb.utils.StatusMap;
import inpatientWeb.utils.Util;

@Service
@Scope("prototype")
@Lazy
public class InteractionDetailsService {
	
	static final double NUMERIC_CONSTS_1000 = 1000.0;

	private InteractionDao interactionDao;
	private ConfigurationDictionaryService configurationDictionaryService;
	private OrdersUtil ordersUtil;
	private SecuritySettingService securitySettingService;
	private FormularySetupDao formularySetupDao;
	
	/* Constant declaration */
	static final int MAX_SEVERITY = 3;	
	
	/**
	 * Gets the med order details.
	 *
	 * @param reqParams the req params
	 * @return the med order details
	 * @throws DrugDbException 
	 */
	public Map<String, Object> getMedOrderDetails(Map<String, Object> reqParams){
		List<DrugDoseOrder> dataList = new ArrayList<>();
		Map<String, Object> oRequestMap = null;
		String calledFrom = Util.getStrValue(reqParams,INTERACTION.CALLED_FROM.text());
		Boolean screenFlag = Util.getBooleanValue(reqParams,INTERACTION.SCREEN_FLAG.text());
		List<Long> stagingMedOrderIds = new ArrayList<>();
		List<Long> homeMedOrderIds = new ArrayList<>();
		List<DrugDoseOrder> customMedOrders = new ArrayList<>();
		
		/********************************************
		 * Get Staging Medication order and details *
		 ********************************************/
		if("ALL".equalsIgnoreCase(ORDERS_SCREEN.findByScreen(calledFrom).ordersCategory())){
			stagingMedOrderIds = getStaggingMedicationOrderDetails(reqParams);
			if(!stagingMedOrderIds.isEmpty()){
				oRequestMap = new HashMap<>();
				oRequestMap.put(INTERACTION.STAGING_MED_ORDER_ID.text(), stagingMedOrderIds);
				dataList = prepareInteractionDrugList(interactionDao.getStagingMedOrderDetails(oRequestMap), screenFlag, true, customMedOrders);
			}
		}
		
		for(DrugDoseOrder order : dataList){
			if(order.getHomeMed() == 1){
				homeMedOrderIds.add((long) order.getOrderId());
			}
		}
		/*******************************************
		 * Get Active Medication order and details *
		 *******************************************/
		if("ACTIVE".equalsIgnoreCase(ORDERS_SCREEN.findByScreen(calledFrom).ordersCategory()) || "ALL".equalsIgnoreCase(ORDERS_SCREEN.findByScreen(calledFrom).ordersCategory())){
			List<Long> activeMedOrderIds = getActiveMedicationOrders(reqParams);
			if(!activeMedOrderIds.isEmpty()){
				oRequestMap = new HashMap<>();
				oRequestMap.put("activeMedOrderIds", activeMedOrderIds);
				dataList.addAll(prepareInteractionDrugList(interactionDao.getActiveMedOrderDetails(oRequestMap), screenFlag, false, customMedOrders));
			}
		}

		/*******************************************
		 * Get Home Medication order details *
		 *******************************************/
		if("HOME".equalsIgnoreCase(ORDERS_SCREEN.findByScreen(calledFrom).ordersCategory())){
			Map<String, Object> responseObj = new HashMap<>();
			responseObj.put(INTERACTION.DRUG_LIST.text(), reqParams.get(INTERACTION.DRUG_LIST.text()));
			responseObj.put(INTERACTION.STAGING_MED_ORDER_ID.text(), stagingMedOrderIds);
			responseObj.put(INTERACTION.HOMEMED_ORDER_ID.text(), homeMedOrderIds);
			responseObj.put(INTERACTION.CUSTOM_MED_ORDERS.text(), customMedOrders);
			return responseObj;
		}

		/*********************************************
		 * Process dose check
		 ********************************************/
		List<ResponseData<DrugDoseOrder>> doseCheckErros = processDoseCheck(reqParams, dataList);
		
		Map<String, Object> responseObj = new HashMap<>();
		responseObj.put(INTERACTION.DRUG_LIST.text(), dataList);
		responseObj.put(INTERACTION.STAGING_MED_ORDER_ID.text(), stagingMedOrderIds);
		responseObj.put(INTERACTION.HOMEMED_ORDER_ID.text(), homeMedOrderIds);
		responseObj.put(INTERACTION.DOSE_CHECK_ERROR.text(), doseCheckErros);
		responseObj.put(INTERACTION.CUSTOM_MED_ORDERS.text(), customMedOrders);
		return responseObj;
	}


	private List<ResponseData<DrugDoseOrder>> processDoseCheck(Map<String, Object> reqParams, List<DrugDoseOrder> dataList){
		List<ResponseData<DrugDoseOrder>> doseCheckErros = new ArrayList<>();
		if(!dataList.isEmpty()){
			int facilityId = interactionDao.getFacilityIdByEnc(Util.getLongValue(reqParams,INTERACTION.EPISODE_ENC_ID.text()));
			if(facilityId <= 0){
				throw new DrugDbException(INTERACTION_MESSAGE.INTERACTION_ERROR.text() + INTERACTION_MESSAGE.ENCOUNTER_FACILITY_NOT_FOUND.text());
			}
			for(DrugDoseOrder doseOrder: dataList){
				fetchDoseCheck(doseCheckErros, facilityId, doseOrder);
			}
		}
		return doseCheckErros;
	}


	/**
	 * @param doseCheckErros
	 * @param facilityId
	 * @param doseOrder
	 */
	private void fetchDoseCheck(List<ResponseData<DrugDoseOrder>> doseCheckErros, int facilityId, DrugDoseOrder doseOrder) {
		ResponseData<DrugDoseOrder> error;
		Map<String, Object> freqDetails;
		if(doseOrder.isDoseCheck()){
			try {
				String frequencyCode = String.valueOf(doseOrder.getFrequencyDetails().get("frequencyCode"));
				if(StringUtils.isEmpty(frequencyCode)){
					throw new DrugDbException(INTERACTION_MESSAGE.DOSE_CHECK_ERROR.text() + "Frequnecy not selected for " + doseOrder.getDrugList().get(0).getDrugName());
				}
				FrequencyDetails frequencyDetails= configurationDictionaryService.getFrequencyDetail(frequencyCode, facilityId);
				if(frequencyDetails.getFrequencyType() == FormularySignatureConstants.FREQUENCY_DICTIONARY_SCHEDULE_CONTINUOUS){
					doseOrder.setDose(calculateDose(doseOrder, doseCheckErros));
				}
				if(frequencyDetails.getResultCode() != RESULT_CODES.SUCCESS.getResultCode()){
					String errorMessage = frequencyDetails.getMessage();
					throw new DrugDbException(INTERACTION_MESSAGE.DOSE_CHECK_ERROR.text() + errorMessage + frequencyCode);
				}
				freqDetails = new HashMap<>();
				freqDetails.put("frequency", frequencyDetails.getFrequency());
				freqDetails.put("frequencyUOM", frequencyDetails.getFrequnecyUOM());
				freqDetails.put("frequencyDoses", frequencyDetails.getFrequnecyDoses());
				doseOrder.setFrequencyDetails(freqDetails);
			}catch(DrugDbException e) {
				doseOrder.setDoseCheck(false);
				error = new ResponseData<>();
				error.setMessage(e.getMessage());
				error.setData(doseOrder);
				doseCheckErros.add(error);
			}
		}
	}
	
	private double calculateDose(DrugDoseOrder doseOrder, List<ResponseData<DrugDoseOrder>> doseCheckErros){
			
		if(doseOrder == null || doseOrder.getIvRateUOM().toLowerCase() == null){
			ResponseData<DrugDoseOrder> error;
			error = new ResponseData<>();
			error.setMessage(INTERACTION_MESSAGE.DOSE_CHECK_ERROR.text() + " Invalid drug Details or IV rate UOM.");
			error.setData(doseOrder);
			doseCheckErros.add(error);
			return 0;
		}
		
		if(doseOrder.getIvRate() <= 0){
			doseOrder.setDoseCheck(false);
			ResponseData<DrugDoseOrder> error;
			error = new ResponseData<>();
			error.setMessage(INTERACTION_MESSAGE.DOSE_CHECK_ERROR.text() + " Invalid IV rate for this order.");
			error.setData(doseOrder);
			doseCheckErros.add(error);
			return 0;
		}
		
		if(doseOrder.getTotalVolume() <= 0){
			doseOrder.setDoseCheck(false);
			ResponseData<DrugDoseOrder> error;
			error = new ResponseData<>();
			error.setMessage(INTERACTION_MESSAGE.DOSE_CHECK_ERROR.text() + " Invalid total volume for this order.");
			error.setData(doseOrder);
			doseCheckErros.add(error);
			return 0;
		}
		
		if(doseOrder.getDetailedDose() <= 0){
			doseOrder.setDoseCheck(false);
			ResponseData<DrugDoseOrder> error;
			error = new ResponseData<>();
			error.setMessage(INTERACTION_MESSAGE.DOSE_CHECK_ERROR.text() + " Invalid individual doses for this order.");
			error.setData(doseOrder);
			doseCheckErros.add(error);
			return 0;
		}
		
		double calculatedDose=0;
		switch(EMAREnum.IVRateUOM.getIVRateUOM(doseOrder.getIvRateUOM().toLowerCase())){
			case ML_HR:
				calculatedDose= (doseOrder.getDetailedDose() * doseOrder.getIvRate() * 24)/doseOrder.getTotalVolume();
				break;
			case ML_MIN:
				calculatedDose= (doseOrder.getDetailedDose() * doseOrder.getIvRate() * 24 * 60)/doseOrder.getTotalVolume();
				break;
			default:
				ResponseData<DrugDoseOrder> error;
				error = new ResponseData<>();
				error.setMessage(INTERACTION_MESSAGE.DOSE_CHECK_ERROR.text() + " Invalid IV rate UOM for this order.");
				error.setData(doseOrder);
				doseCheckErros.add(error);
				break;
		}
		return getRoundOffValue(calculatedDose);
	}
	
	
	/**
	 * Prepare interaction drug list.
	 *
	 * @param orderDetailsList the order details list
	 * @param screenFlag the screen flag
	 * @param isStagingOrder the is staging order
	 * @return the list
	 */
	private List<DrugDoseOrder> prepareInteractionDrugList(List<DrugOrderDetails> orderDetailsList, boolean screenFlag, boolean isStagingOrder, List<DrugDoseOrder> customMedOrders){
		List<DrugDoseOrder> orderList = new ArrayList<>();
		Map<Integer,List<DrugData>> drugListMap = new HashMap<>();
		Map<Integer,DrugDoseOrder> orderListMap = new HashMap<>();
		DrugData drugData = null;
		DrugDoseOrder order = null;
		List<DrugData> drugList =  null;
		for(DrugOrderDetails orderDetails : orderDetailsList){
			if(orderDetails.getCustomMed() == 1 && isStagingOrder){
				prepareInteractionDrugListForStagingCustomDrug(orderDetailsList, orderList, screenFlag, isStagingOrder, customMedOrders);
			}else if(orderDetails.getCustomMed() == 1 && !isStagingOrder){
				prepareInteractionDrugListForActiveCustomDrug(orderDetailsList, orderList, screenFlag, isStagingOrder, customMedOrders);
			}else{
				drugData = setDrugData(orderDetails, INTERACTION_KEY_ID.GPI.text(), screenFlag, isStagingOrder);
				drugList = new ArrayList<>();
				drugListMap.put(orderDetails.getPtOrderId(), drugList);
				drugList.add(drugData);
				order = setOrderObject(isStagingOrder, drugList, orderDetails, false);
				orderListMap.put(orderDetails.getPtOrderId(),order);
				orderList.add(order);
			}
		}
		return orderList;
	}
	
	private void prepareInteractionDrugListForStagingCustomDrug(List<DrugOrderDetails> orderDetailsList, List<DrugDoseOrder> dataList, boolean screenFlag, boolean isStagingOrder, List<DrugDoseOrder> customMedOrders){
		DrugDoseOrder customMedOrder = null;
		for(DrugOrderDetails drugOrderDetails : orderDetailsList){
			customMedOrder = new DrugDoseOrder();
			customMedOrder.setOrderId(drugOrderDetails.getPtOrderId());
			customMedOrder.setCustomDrugName(drugOrderDetails.getDrugName());
			customMedOrders.add(customMedOrder);
			Map<String, Object>  oRequestMap = new HashMap<>();
			oRequestMap.put(INTERACTION.STAGING_MED_ORDER_ID.text(), drugOrderDetails.getPtOrderId());
			List<DrugOrderDetails> customMedOrderDetails = interactionDao.getStagingCustomMedOrderDetails(oRequestMap);
			Map<Integer,List<DrugData>> drugListMap = new HashMap<>();
			Map<Integer,DrugDoseOrder> orderListMap = new HashMap<>();
			DrugData drugData = null;
			DrugDoseOrder order = null;
			List<DrugData> drugList =  null;
			for(DrugOrderDetails orderDetails : customMedOrderDetails){
				orderDetails.setCustomDrugName(drugOrderDetails.getDrugName());
				drugData = setDrugData(orderDetails, INTERACTION_KEY_ID.ROUTED_DRUG_ID.text(), screenFlag, isStagingOrder);
				drugList = new ArrayList<>();
				if(drugListMap.containsKey(orderDetails.getPtOrderId()) && orderDetails.getOrderType() == RxType.MEDICATION.getType()){
					drugList = drugListMap.get(orderDetails.getPtOrderId());
					orderListMap.get(orderDetails.getPtOrderId()).setDoseCheck(false);
					drugList.add(drugData);
					continue;
				}
				drugListMap.put(orderDetails.getPtOrderId(), drugList);
				drugList.add(drugData);
				order = setOrderObject(isStagingOrder, drugList, orderDetails, true);
				order.setCustomDrug(true);
				orderListMap.put(orderDetails.getPtOrderId(),order);
				dataList.add(order);
			}
		}
	}
	
	private void prepareInteractionDrugListForActiveCustomDrug(List<DrugOrderDetails> orderDetailsList, List<DrugDoseOrder> dataList, boolean screenFlag, boolean isStagingOrder, List<DrugDoseOrder> customMedOrders){
		DrugDoseOrder customMedOrder = null;
		for(DrugOrderDetails drugOrderDetails : orderDetailsList){
			customMedOrder = new DrugDoseOrder();
			customMedOrder.setOrderId(drugOrderDetails.getPtOrderId());
			customMedOrder.setCustomDrugName(drugOrderDetails.getDrugName());
			customMedOrders.add(customMedOrder);
			Map<String, Object>  oRequestMap = new HashMap<>();
			oRequestMap.put("activeMedOrderIds", drugOrderDetails.getPtOrderId());
			List<DrugOrderDetails> customMedOrderDetails = interactionDao.getActiveCustomMedOrderDetails(oRequestMap);
			Map<Integer,List<DrugData>> drugListMap = new HashMap<>();
			Map<Integer,DrugDoseOrder> orderListMap = new HashMap<>();
			DrugData drugData = null;
			DrugDoseOrder order = null;
			List<DrugData> drugList =  null;
			for(DrugOrderDetails orderDetails : customMedOrderDetails){
				drugData = setDrugData(orderDetails, INTERACTION_KEY_ID.ROUTED_DRUG_ID.text(), screenFlag, isStagingOrder);
				drugList = new ArrayList<>();
				if(drugListMap.containsKey(orderDetails.getPtOrderId()) && orderDetails.getOrderType() == RxType.MEDICATION.getType()){
					drugList = drugListMap.get(orderDetails.getPtOrderId());
					orderListMap.get(orderDetails.getPtOrderId()).setDoseCheck(false);
					drugList.add(drugData);
					continue;
				}
				drugListMap.put(orderDetails.getPtOrderId(), drugList);
				drugList.add(drugData);
				order = setOrderObject(isStagingOrder, drugList, orderDetails, true);
				order.setCustomDrug(true);
				orderListMap.put(orderDetails.getPtOrderId(),order);
				dataList.add(order);
			}
		}
	}

	/**
	 * Sets the order object.
	 *
	 * @param isStagingOrder the is staging order
	 * @param drugList the drug list
	 * @param orderDetails the order details
	 * @return the drug dose order
	 */
	private DrugDoseOrder setOrderObject(boolean isStagingOrder, List<DrugData> drugList, DrugOrderDetails orderDetails, boolean customDrug) {
		DrugDoseOrder order = new DrugDoseOrder();
		order.setOrderId(orderDetails.getPtOrderId());
		order.setDuration(handleDoubleCast(orderDetails.getOrderDuration()));
		order.setDurationUOM(orderDetails.getOrderDurationType());
		order.setDose(handleDoubleCast(orderDetails.getOrderDose()));
		order.setDoseUOM(orderDetails.getOrderDoseUOM());
		if(isStagingOrder){
			order.setHomeMed(orderDetails.getHomeMed());
			if(orderDetails.getIvDiluent()!=1){
				order.setDoseCheck(true);
			}
		}
		order.setCustomDrug(customDrug);
		order.setDrugList(drugList);
		order.setIvRate(orderDetails.getIvRate());
		order.setIvRateUOM(orderDetails.getIvRateUOM());
		order.setTotalVolume(orderDetails.getTotalVolume());
		order.setDetailedDose(orderDetails.getDetailedDose());
		Map<String, Object> frequencyMap = new HashMap<>();
		frequencyMap.put("frequencyCode", orderDetails.getFrequencyCode());
		order.setFrequencyDetails(frequencyMap);
		return order;
	}
	
	/**
	 * Sets the drug data.
	 *
	 * @param orderDetails the order details
	 * @param keyId the key id
	 * @param screenFlag the screen flag
	 * @param isStagingOrder the is staging order
	 * @return the drug data
	 */
	private DrugData setDrugData(DrugOrderDetails orderDetails, String keyId, boolean screenFlag, boolean isStagingOrder){
		DrugData drugData = new DrugData();
		drugData.setIdType(keyId);
		
		switch(keyId){
			case "GPI":
				drugData.setId(orderDetails.getGpi());
				break;
			case "routedDrugId":
				drugData.setId(orderDetails.getRoutedDrugId());
				break;
			default:
				drugData.setId(orderDetails.getGpi());
				break;
		}
		drugData.setStrength(orderDetails.getStrength());
		drugData.setStrengthUOM(orderDetails.getStrengthUOM());
		drugData.setFormulation(orderDetails.getFormulation());
		drugData.setRouteId(orderDetails.getRouteId());
		drugData.setDrugName(orderDetails.getDrugName());
		drugData.setRoutedGenericId(orderDetails.getRoutedGenricId());
		if(orderDetails.getCustomMed() == 1){
			drugData.setCustomDrugName(orderDetails.getCustomDrugName());
		}
		drugData.setScreen(true);
		
		/**
		 * check for screenFlag (True: New Drugs with active drugs , False: All active drugs) and if orders are active than set screen flag as false 
		 */
		if(screenFlag && !isStagingOrder){
			drugData.setScreen(false);
		}
		/**
		 * Set interaction enabled params from formulary setting
		 */
		List<String> interactionList = new ArrayList<>();
		interactionList.add(INTERACTION.DRUGTODRUG.text());
		interactionList.add(INTERACTION.DRUGTOALLERGY.text());
		interactionList.add(INTERACTION.DRUGTODISEASE.text());
		
		/**
		 * Check for formulary setting exclude interaction
		 */
		checkForExcludeParams(interactionList, orderDetails);
		drugData.setInteractions(interactionList);
		return drugData;
	}


	/**
	 * @param interactionList
	 * @param orderDetails
	 */
	private void checkForExcludeParams(List<String> interactionList, DrugOrderDetails orderDetails) {
		if(orderDetails.getDrugToFood() == 1){
			interactionList.add(INTERACTION.DRUGTOFOOD.text());
		}
		
		if(orderDetails.getDrugToAlcohol() == 1){
			interactionList.add(INTERACTION.DRUGTOALCOHOL.text());
		}

		if(orderDetails.getDuplicateTherapy() == 1){
			interactionList.add(INTERACTION.DUPLICATETHERAPY.text());
		}
	}
	
	/**
	 * Gets the stagging medication order details.
	 *
	 * @param reqParams the req params
	 * @return the stagging medication order details
	 */
	private List<Long> getStaggingMedicationOrderDetails(final Map<String, Object> reqParams){
		reqParams.put("orderStatus",ordersUtil.getAllStagingOrderStatusIdList());
		List<Long> staggingOrderList = interactionDao.getStaggingMedOrders(reqParams);
		if (!staggingOrderList.isEmpty()) {
			return staggingOrderList;
		}
		return Collections.emptyList();	
	}

	/**
	 * Gets the active medication orders.
	 *
	 * @param reqParams the req params
	 * @return the active medication orders
	 */
	private List<Long> getActiveMedicationOrders(Map<String, Object> reqParams){
		reqParams.put("orderStatuss",ordersUtil.getAllActiveOrderStatusIdList());
		reqParams.put("parentId",IPItemkeyDAO.getIPItemKeyItemIdFromName("OrderType_Items", 0));
		List<Long> activeOrderList = interactionDao.getActiveMedOrders(reqParams);
		if (!activeOrderList.isEmpty()) {
			return activeOrderList;
		}
		return Collections.emptyList();	
	}

	/**
	 * Process post response.
	 *
	 * @param responseObj the response obj
	 * @param stagingMedOrderIds the staging med order ids
	 * @return the JSON object
	 */
	@SuppressWarnings("unchecked")
	public JSONObject postProcessResponse(Map<String, Object> reqParams, JSONObject requestObj, JSONObject responseObj, InteractionRequest interactionRequest){
		
		/**
		 * Check for drug drug interaction from formulary setup
		 */
		responseObj = checkFormularyInteraction(requestObj,responseObj);
		
		/**
		 * Fetch pharmacy setting
		 */
		String calledFrom = interactionRequest.getCalledFrom();
		int userId = interactionRequest.getUserId();
		Map<String, Object> interactionSettings = getPharmacyInteractionSettings(interactionRequest.getFacilityId());
		interactionSettings.put("newDrugWithActiveFlag", ORDERS_SCREEN.findByScreen(calledFrom).newDrugWithActiveFlag());
		responseObj.put("interactionSetting", interactionSettings);

		/**
		 * Check for security setting
		 */
		String overrideAccess = getOverrideAccessRights(userId, calledFrom, Util.getBooleanValue(interactionSettings, "isAllowOverride"));
		responseObj.put("interactionOverrideAccess", overrideAccess);
		responseObj.put("interactionAcknowledgeAccess", getAcknowledgeAccessRights(userId, calledFrom));
		responseObj.put("overrideReasons", getConfiguredOverrideReasons());
		
		/**
		 * Check for override interaction
		 */
		mergeOverrideInteractions(reqParams,responseObj);
		
		
		Map<String, Object> interactions = (Map<String, Object>) responseObj.get(INTERACTION.INTERACTION.text());
		Map<String, Object> summaryMap = new HashMap<>();
		List<Long> interactionOrderIds = new ArrayList<>();
		summaryMap.put(INTERACTION.AUTO_POPUP.text(), true);
		
		setStagingOrderFlag((Map<String, Object>) interactions.get(INTERACTION_TYPES.DRUG_DRUG_INTERACTION.type()), reqParams, summaryMap, interactionOrderIds, false);
		setStagingOrderFlag((Map<String, Object>) interactions.get(INTERACTION_TYPES.DRUG_FOOD_INTERACTION.type()), reqParams, summaryMap, interactionOrderIds, false);
		setStagingOrderFlag((Map<String, Object>) interactions.get(INTERACTION_TYPES.DRUG_ALCOHOL_INTERACTION.type()), reqParams,summaryMap, interactionOrderIds, false);
		setStagingOrderFlag((Map<String, Object>) interactions.get(INTERACTION_TYPES.DRUG_ALLERGY_INTERACTION.type()), reqParams,summaryMap, interactionOrderIds, false);
		setStagingOrderFlag((Map<String, Object>) interactions.get(INTERACTION_TYPES.DRUG_DISEASE_INTERACTION.type()), reqParams,summaryMap, interactionOrderIds, false);
		setStagingOrderFlag((Map<String, Object>) interactions.get(INTERACTION_TYPES.DUPLICATE_THERAPY_INTERACTION.type()), reqParams,summaryMap, interactionOrderIds, true);
		prepareDoseCheckResponse((Map<String, Object>) interactions.get(INTERACTION_TYPES.DOSE_CHECK.type()), reqParams,summaryMap, interactionOrderIds, false); // For DoseCheck need to check "passesScreening" instead of isAllMajor flag, So it is set as false
		setStagingOrderFlag((Map<String, Object>) interactions.get(INTERACTION_TYPES.AGE_BASED_WARNING.type()), reqParams,summaryMap, interactionOrderIds, true);
		setStagingOrderFlag((Map<String, Object>) interactions.get(INTERACTION_TYPES.GENDER_BASED_WARNING.type()), reqParams,summaryMap, interactionOrderIds, true);
		setStagingOrderFlag((Map<String, Object>) interactions.get(INTERACTION_TYPES.PREGNANCY_WARNING.type()), reqParams,summaryMap, interactionOrderIds, true);
		setStagingOrderFlag((Map<String, Object>) interactions.get(INTERACTION_TYPES.LACTATING_WARNING.type()), reqParams,summaryMap, interactionOrderIds, true);
		
		List<DrugDoseOrder> customMedOrders =  new ArrayList<>();
		customMedOrders.addAll((List<DrugDoseOrder>) reqParams.get(INTERACTION.CUSTOM_MED_ORDERS.text()));
		if(!customMedOrders.isEmpty()){		
			setCustomOrderFlag((Map<String, Object>) interactions.get(INTERACTION_TYPES.DRUG_DRUG_INTERACTION.type()), customMedOrders);
			setCustomOrderFlag((Map<String, Object>) interactions.get(INTERACTION_TYPES.DRUG_FOOD_INTERACTION.type()), customMedOrders);
			setCustomOrderFlag((Map<String, Object>) interactions.get(INTERACTION_TYPES.DRUG_ALCOHOL_INTERACTION.type()), customMedOrders);
			setCustomOrderFlag((Map<String, Object>) interactions.get(INTERACTION_TYPES.DRUG_ALLERGY_INTERACTION.type()), customMedOrders);
			setCustomOrderFlag((Map<String, Object>) interactions.get(INTERACTION_TYPES.DRUG_DISEASE_INTERACTION.type()), customMedOrders);
			setCustomOrderFlag((Map<String, Object>) interactions.get(INTERACTION_TYPES.DUPLICATE_THERAPY_INTERACTION.type()), customMedOrders);
			setCustomOrderFlag((Map<String, Object>) interactions.get(INTERACTION_TYPES.DOSE_CHECK.type()), customMedOrders);
			setCustomOrderFlag((Map<String, Object>) interactions.get(INTERACTION_TYPES.AGE_BASED_WARNING.type()), customMedOrders);
			setCustomOrderFlag((Map<String, Object>) interactions.get(INTERACTION_TYPES.GENDER_BASED_WARNING.type()), customMedOrders);
			setCustomOrderFlag((Map<String, Object>) interactions.get(INTERACTION_TYPES.PREGNANCY_WARNING.type()), customMedOrders);
			setCustomOrderFlag((Map<String, Object>) interactions.get(INTERACTION_TYPES.LACTATING_WARNING.type()), customMedOrders);
		}
		
		/**
		 * Check for dose check client error
		 */
		checkForDoseCheckErrors(reqParams, responseObj);

		/**
		 * Set severityCode
		 */
		responseObj.put("severityCode", getInteractionSeverity(interactions,Util.getIntValue(responseObj, "severityCode", 0)));

		summaryMap.put(INTERACTION.AUTO_POPUP.text(), Utils.containAny(interactionRequest.getRecentOrderList(), interactionOrderIds));
		
		responseObj.put(INTERACTION.MAJOR_COUNT.text(),Util.getIntValue(summaryMap, INTERACTION.MAJOR_COUNT.text(), 0));
		responseObj.put(INTERACTION.MODERATE_COUNT.text(),Util.getIntValue(summaryMap, INTERACTION.MODERATE_COUNT.text(), 0));
		responseObj.put(INTERACTION.MINOR_COUNT.text(),Util.getIntValue(summaryMap, INTERACTION.MINOR_COUNT.text(), 0));
		responseObj.put(INTERACTION.AUTO_POPUP.text(),Util.getBooleanValue(summaryMap, INTERACTION.AUTO_POPUP.text(), false));
		
		return responseObj;
	}
	
	/**
	 * Gets the formulary interaction.
	 *
	 * @param drugDoseOrders the drug dose orders
	 * @return the formulary interaction
	 */
	public List<Map<String, Object>> getFormularyInteraction(List<Map<String, Object>> drugDoseOrders){
		if(drugDoseOrders.isEmpty()){
			return Collections.emptyList();
		}
		List<String> ids = new ArrayList<>();
		String id = null;
		for(Map<String, Object> drugDose : drugDoseOrders){
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> drugDataList =  (List<Map<String, Object>>) drugDose.get(INTERACTION.DRUG_LIST.text());
			if(drugDataList != null){
				for(Map<String, Object> drugData : drugDataList){
					id=Util.getStrValue(drugData, "id");
					ids.add(id.replace("-", ""));
				}
			}
		}
		
		List<Integer> routedGenericItemIds = new ArrayList<>();
		Map<String,Integer> routedGenericItemIdMap = formularySetupDao.getRoutedGenricItemIds(ids);
		for (Integer value : routedGenericItemIdMap.values()) {
			routedGenericItemIds.add(value);
		}
		return formularySetupDao.getSavedDrugDrugInteraction(routedGenericItemIds);
	}
	
	/**
	 * Merge override interactions.
	 *
	 * @param requestObj the request obj
	 * @param responseObj the response obj
	 * @param userId the user id
	 * @return the JSON object
	 */
	@SuppressWarnings("unchecked")
	private JSONObject mergeOverrideInteractions(Map<String, Object> reqParams,JSONObject responseObj){
		List<Map<String, Object>> overriddenInteractions = fetchOverrideInteractionData(reqParams);
		Map<String, Object> interactions = (Map<String, Object>) responseObj.get(INTERACTION.INTERACTION.text());
		Map<String, Object> moduleInteractions = null;
		List<Map<String, Object>> interactionData = null;
		boolean isOverrideStatus = false;
		List<Long> overrideOrderIds = null;
		for(Map<String, Object> overrideInteraction : overriddenInteractions){
			overrideOrderIds = (List<Long>) overrideInteraction.get(INTERACTION.ORDER_LIST.text());
			moduleInteractions = (Map<String, Object>) interactions.get(INTERACTION_TYPES.findById(Util.getIntValue(overrideInteraction, "interactionCategory")).type());
			interactionData = (List<Map<String, Object>>) moduleInteractions.get("data");
			
			for(Map<String, Object> inter : interactionData){
				isOverrideStatus = true;
				for(Map<String, Object> drugDetail : (List<Map<String, Object>>) inter.get(INTERACTION.DRUG_DETAILS.text())){
					if(!overrideOrderIds.contains(Util.getLongValue(drugDetail, INTERACTION.ORDER_ID.text()))){
						isOverrideStatus = false;
					}
				}
				if(isOverrideStatus){
					inter.put("isOverride", isOverrideStatus);
					inter.put("overrideBy", Util.getStrValue(overrideInteraction,"overrideBy"));
					inter.put(INTERACTION.OVERRIDE_REASON.text(), Util.getIntValue(overrideInteraction,"overrideReason"));
					inter.put(INTERACTION.PREV_OVERRIDE_REASON.text(), Util.getIntValue(overrideInteraction,"overrideReason"));
					inter.put("isAcknowledge", Util.getIntValue(overrideInteraction,"isAcknowledge"));
					inter.put("acknowledgeBy", Util.getStrValue(overrideInteraction,"acknowledgeBy"));
					inter.put(INTERACTION.INTERACTION_ID.text(), Util.getIntValue(overrideInteraction,INTERACTION.INTERACTION_ID.text()));
					
				}
			}
			
		}
		return responseObj;
	}
	
	/**
	 * Check dose check error.
	 *
	 * @param reqParams the request obj
	 * @param responseObj the response obj
	 */
	@SuppressWarnings("unchecked")
	private JSONObject checkForDoseCheckErrors(Map<String, Object> reqParams,JSONObject responseObj){
		StatusMap oStatus =  new StatusMap();
		try		
		{	
			List<ResponseData<DrugDoseOrder>> doseChecksErrors = (List<ResponseData<DrugDoseOrder>>) reqParams.get(INTERACTION.DOSE_CHECK_ERROR.text());
			if(!doseChecksErrors.isEmpty()) {
				Map<String,Object> doseCheckObject = null;
				DrugDoseOrder drugDoseOrder = null;
				List<Map<String, Object>> drugDetails = null;
				List<String> messages = null;
				Map<String, Object> drugDetail = null;
				Map<String, Object> interaction = (Map<String, Object>) responseObj.get(INTERACTION.INTERACTION.text());
				Map<String, Object> doseCheckResponse = (Map<String, Object>) interaction.get(INTERACTION.DOSE_CHECK_RESPONSE.text());
				List<Map<String,Object>> doseCheckData = (List<Map<String,Object>>) doseCheckResponse.get("data");
				for(ResponseData<DrugDoseOrder> doseCheckError : doseChecksErrors) {
					doseCheckObject = new HashMap<>();
					drugDoseOrder = doseCheckError.getData();
					doseCheckObject.put(INTERACTION.PASSES_SCREENING.text(), false);
					doseCheckObject.put("isError", true);
					drugDetails = new ArrayList<>();
					drugDetail = new HashMap<>();
					drugDetail.put(INTERACTION.ORDER_ID.text(), drugDoseOrder.getOrderId());
					drugDetail.put("drugdescription", drugDoseOrder.getDrugList().get(0).getDrugName());
					drugDetail.put(INTERACTION.IS_STAGING_ORDER.text(), true);
					drugDetails.add(drugDetail);
					doseCheckObject.put(INTERACTION.DRUG_DETAILS.text(), drugDetails);
					doseCheckObject.put(INTERACTION.INTERACTION_SEVERITY_CODE.text(), INTERACTION_SEVERITY_TYPES.MAJOR.id());
					doseCheckObject.put("hasErrorMessages", false);
					messages = new ArrayList<>();
					messages.add(doseCheckError.getMessage());
					doseCheckObject.put(INTERACTION.MESSAGE.text(), messages);
					doseCheckData.add(doseCheckObject);
				}
			}
			return responseObj;
		}
		catch(RuntimeException ex){
			EcwLog.AppendExceptionToLog(ex);
			oStatus.setObject(null);
			oStatus.setFail(INTERACTION_MESSAGE.INTERACTION_LOADING_EXCEPTION.text());
			return responseObj;
		}
	}
	
	/**
	 * Check formulary interaction.
	 *
	 * @param requestObj the request obj
	 * @param responseObj the response obj
	 * @param userId the user id
	 * @param calledFrom the called from
	 * @return the JSON object
	 */
	@SuppressWarnings("unchecked")
	private JSONObject checkFormularyInteraction(JSONObject requestObj,JSONObject responseObj){
		StatusMap oStatus =  new StatusMap();
		try		
		{	
			List<Map<String, Object>> drugDoseOrders = (List<Map<String, Object>>) requestObj.get(INTERACTION.ORDER_LIST.text());
			List<Map<String, Object>> formularyInters = getFormularyInteraction(drugDoseOrders);
			List<String> gpiList = null;
			List<Map<String, Object>> drugDetails = null;
			Map<String, Object> interResponse = null;
			List<Map<String, Object>> orderDrugsInter = null;
			for(Map<String, Object> fomularyInter : formularyInters){
				Integer formularyRoutedGenericItemId = Util.getIntValue(fomularyInter,"formularyRoutedGenericItemId");
				Integer interactingRoutedGenericItemId = Util.getIntValue(fomularyInter,"interactingRoutedGenericItemId");
				int formuInterSeverityId = (int) fomularyInter.get("interactionlevelModified");
				String formuInterSeverityValue = INTERACTION_SEVERITY_TYPES.findByid(formuInterSeverityId).value();
				interResponse = (Map<String, Object>) responseObj.get("drugDrugInteractions");
				orderDrugsInter = (List<Map<String, Object>>) interResponse.get("data");
				if(orderDrugsInter == null){
					continue;
				}
				for(Map<String, Object> intercation : orderDrugsInter){
					drugDetails = (List<Map<String, Object>>) intercation.get(INTERACTION.DRUG_DETAILS.text());
					String formularyGPI = Util.getStrValue(drugDetails.get(0),"gpi").replace("-","");
					String interactingGPI = Util.getStrValue(drugDetails.get(1),"gpi").replace("-","");
					gpiList = new ArrayList<>();
					gpiList.add(formularyGPI);
					gpiList.add(interactingGPI);
					Map<String,Integer> routedGenericItemIdMap = formularySetupDao.getRoutedGenricItemIds(gpiList);
					if((Util.getIntValue(routedGenericItemIdMap, formularyGPI) == formularyRoutedGenericItemId && Util.getIntValue(routedGenericItemIdMap, interactingGPI) == interactingRoutedGenericItemId)
						||(Util.getIntValue(routedGenericItemIdMap, formularyGPI) == interactingRoutedGenericItemId && Util.getIntValue(routedGenericItemIdMap, interactingGPI) == formularyRoutedGenericItemId)){ 
						intercation.put("fromInteractionSeverityCode", Util.getStrValue(intercation,INTERACTION.INTERACTION_SEVERITY_CODE.text()));
						intercation.put(INTERACTION.INTERACTION_SEVERITY_CODE.text(), formuInterSeverityId);
						intercation.put("fromInteractionSeverityDescription", Util.getStrValue(intercation,"interactionSeverityDescription"));
						intercation.put("interactionSeverityDescription", formuInterSeverityValue);
						intercation.put("sourceFormulary", true);
					}
				}
			}
			
			JSONObject interactionObj = new JSONObject();
			interactionObj.put("interaction", responseObj);
			return interactionObj;
		}
		catch(RuntimeException ex){
			EcwLog.AppendExceptionToLog(ex);
			oStatus.setObject(null);
			oStatus.setFail(INTERACTION_MESSAGE.FORMULARY_DRUGINTERACTION_FETCH_EXCEPTION.text());
			return responseObj;
		}
	}
	
	/**
	 * Get all configured <b>active</b> override reasons for interaction.
	 *
	 * @return list of configured override reasons for interaction
	 * @throws InternalServerException if any error occurred while getting formulary reason dictionary items from database
	 */
	private  List<Map<String, Object>> getConfiguredOverrideReasons() {
		List<TemplateReasonDictionaryItems> reasonDictionaries = configurationDictionaryService.getFormularyReasonDictionaryActiveItems(ReasonDictionaryType.OVERRIDE_REASONS);
		List<Map<String, Object>> reasonsList = new ArrayList<>();
		Map<String, Object> reasonMap = null;
		for(TemplateReasonDictionaryItems reasonDictionary : reasonDictionaries){
			reasonMap = new HashMap<>();
			reasonMap.put("id", reasonDictionary.getId());
			reasonMap.put("reasonName", reasonDictionary.getReasonName());
			reasonsList.add(reasonMap);
		}
		return reasonsList;
	}
	
	/**
	 * Methods used to fetch access rights for override and acknowledge interactions.
	 */
	
	/**
	 * @param userId the user id
	 * @param calledFrom the called from
	 * @return the override access rights
	 */
	
	private String getOverrideAccessRights(int userId, String calledFrom, boolean isAllowOverride){
		if(securitySettingService.hasAccess(userId, MedicationConstants.ALLOW_TO_OVERRIDE_INTERACTION_KEY) && isAllowOverride){
			return getReadWriteAccess(calledFrom);
		}
		return "";
	}
	
	/**
	 * Gets the acknowledge access rights.
	 *
	 * @param userId the user id
	 * @param calledFrom the called from
	 * @return the acknowledge access rights
	 */
	private String getAcknowledgeAccessRights(int userId, String calledFrom){
		if(securitySettingService.hasAccess(userId, MedicationConstants.ALLOW_TO_ACKNOWLEDGE_INTERACTION_KEY)){
			return getReadWriteAccess(calledFrom);
		}
		return "";
	}
	
	/**
	 * Gets the read write access.
	 *
	 * @param calledFrom the called from
	 * @return the read write access
	 */
	private String getReadWriteAccess(String calledFrom){
		if(ORDERS_SCREEN.findByScreen(calledFrom) == null){
			return "";
		}
		return ORDERS_SCREEN.findByScreen(calledFrom).accessRights();
	}
	
	private Map<String, Object> getPharmacyInteractionSettings(int facilityId){
		if (facilityId != 0) {
			Map<String, Object> settingMap = new HashMap<>();
			settingMap.put("interactionAlertLevel", MAX_SEVERITY);
			settingMap.put("interactionAlertLevelValue", INTERACTION_SEVERITY_TYPES.findByid(MAX_SEVERITY).value());
			settingMap.put("isAllowOverride", true);
			settingMap.put("isMandatoryOverride", true);
			return settingMap;
		}
		return Collections.emptyMap();
	}
	
	
	/**
	 * Sets the staging order flag.
	 *
	 * @param interactionObj the interaction obj
	 * @param reqParams the req params
	 * @param summaryMap the summary map
	 * @param interactionOrderIds the interaction order ids
	 * @param isAllMajor the isAllMajor flag is using to consider all warning and interaction as major severity
	 */
	@SuppressWarnings("unchecked")
	private void setStagingOrderFlag(Map<String, Object> interactionObj, Map<String, Object> reqParams, Map<String, Object> summaryMap, List<Long> interactionOrderIds, boolean isAllMajor){
		List<Long> stagingMedOrderIds = (List<Long>) reqParams.get(INTERACTION.STAGING_MED_ORDER_ID.text());
		List<Long> homeMedOrderIds = (List<Long>) reqParams.get(INTERACTION.HOMEMED_ORDER_ID.text());
		List<Map<String, Object>> interaction = (List<Map<String, Object>>) interactionObj.get("data");
		for(Map<String, Object> inter : interaction){
			for(Map<String, Object> drugDetail : (List<Map<String, Object>>) inter.get(INTERACTION.DRUG_DETAILS.text())){
				if(stagingMedOrderIds.contains(Util.getLongValue(drugDetail, INTERACTION.ORDER_ID.text(), 0)) && !homeMedOrderIds.contains(Util.getLongValue(drugDetail, INTERACTION.ORDER_ID.text(), 0))){
					interactionOrderIds.add(Util.getLongValue(drugDetail, INTERACTION.ORDER_ID.text()));
					drugDetail.put(INTERACTION.IS_STAGING_ORDER.text(), true);
				}
			}
			
			// "passesScreening" condition is only for doseCheck warning for other isAllMajor is consider
			if(isAllMajor || (inter.containsKey(INTERACTION.PASSES_SCREENING.text()) && !Util.getBooleanValue(inter, INTERACTION.PASSES_SCREENING.text()))){
				inter.put(INTERACTION.INTERACTION_SEVERITY_CODE.text(),INTERACTION_SEVERITY_TYPES.MAJOR.id());
			}
			setInteractionSummary(Util.getIntValue(inter, INTERACTION.INTERACTION_SEVERITY_CODE.text(), 0), summaryMap);
		}
	}
	
	private void prepareDoseCheckResponse(Map<String, Object> interactionObj, Map<String, Object> reqParams, Map<String, Object> summaryMap, List<Long> interactionOrderIds, boolean isAllMajor){
		List<Long> stagingMedOrderIds = (List<Long>) reqParams.get(INTERACTION.STAGING_MED_ORDER_ID.text());
		List<Long> homeMedOrderIds = (List<Long>) reqParams.get(INTERACTION.HOMEMED_ORDER_ID.text());
		Map<Long,Map<String, Object>> modifiedInteractionMap = new HashMap<>();
		Map<String, Object> modifiedInteraction = null;
		for(Map<String, Object> inter : (List<Map<String, Object>>) interactionObj.get("data")){
			for(Map<String, Object> drugDetail : (List<Map<String, Object>>) inter.get(INTERACTION.DRUG_DETAILS.text())){
				if(stagingMedOrderIds.contains(Util.getLongValue(drugDetail, INTERACTION.ORDER_ID.text(), 0)) && !homeMedOrderIds.contains(Util.getLongValue(drugDetail, INTERACTION.ORDER_ID.text(), 0))){
					interactionOrderIds.add(Util.getLongValue(drugDetail, INTERACTION.ORDER_ID.text()));
					drugDetail.put(INTERACTION.IS_STAGING_ORDER.text(), true);
				}
				
				if(!modifiedInteractionMap.containsKey(Util.getLongValue(drugDetail, INTERACTION.ORDER_ID.text()))){
					modifiedInteractionMap.put(Util.getLongValue(drugDetail, INTERACTION.ORDER_ID.text()),inter);
				}
					
				modifiedInteraction = modifiedInteractionMap.get(Util.getLongValue(drugDetail, INTERACTION.ORDER_ID.text()));
				List<Map<String,Object>> subOrderDetails = new ArrayList<>();
				if(modifiedInteraction.get(INTERACTION.MULTIPLE_MEDICATION.text()) != null){
					subOrderDetails = (List<Map<String, Object>>) modifiedInteraction.get(INTERACTION.MULTIPLE_MEDICATION.text());
				}
				Map<String, Object> subOrderDetail = new HashMap<>();
				subOrderDetail.put("drug",drugDetail.get("drugdescription"));
				subOrderDetail.put(INTERACTION.IS_STAGING_ORDER.text(),drugDetail.get(INTERACTION.IS_STAGING_ORDER.text()));
				subOrderDetail.put(INTERACTION.ORDER_ID.text(),drugDetail.get(INTERACTION.ORDER_ID.text()));
				subOrderDetail.put(INTERACTION.MESSAGE.text(), inter.get(INTERACTION.MESSAGE.text()));
				subOrderDetails.add(subOrderDetail);
				modifiedInteraction.put(INTERACTION.MULTIPLE_MEDICATION.text(), subOrderDetails);
			}
			
			// "passesScreening" condition is only for doseCheck warning for other isAllMajor is consider
			if(isAllMajor || (inter.containsKey(INTERACTION.PASSES_SCREENING.text()) && !Util.getBooleanValue(inter, INTERACTION.PASSES_SCREENING.text()))){
				inter.put(INTERACTION.INTERACTION_SEVERITY_CODE.text(),INTERACTION_SEVERITY_TYPES.MAJOR.id());
			}
			setInteractionSummary(Util.getIntValue(inter, INTERACTION.INTERACTION_SEVERITY_CODE.text(), 0), summaryMap);
		}
		interactionObj.put("data",new ArrayList<>(modifiedInteractionMap.values()));
	}
	
	/**
	 * Sets the staging order flag.
	 *
	 * @param interactionObj the interaction obj
	 * @param reqParams the req params
	 * @param summaryMap the summary map
	 * @param interactionOrderIds the interaction order ids
	 * @param isAllMajor the isAllMajor flag is using to consider all warning and interaction as major severity
	 */
	@SuppressWarnings("unchecked")
	private void setCustomOrderFlag(Map<String, Object> interactionObj, List<DrugDoseOrder> customMedOrders){
		
		List<Map<String, Object>> interaction = (List<Map<String, Object>>) interactionObj.get("data");
		for(Map<String, Object> inter : interaction){
			for(Map<String, Object> drugDetail : (List<Map<String, Object>>) inter.get(INTERACTION.DRUG_DETAILS.text())){
				for(DrugDoseOrder orders: customMedOrders){
					if(orders.getOrderId() == Util.getLongValue(drugDetail, INTERACTION.ORDER_ID.text(), 0)){
						drugDetail.put("customDrugName", orders.getCustomDrugName());
					}
				}
			}
		}
	}
	
	private void setInteractionSummary(int severityCode, Map<String, Object> summaryMap){
		switch(severityCode){
		case 3:
			Integer majorCnt = Util.getIntValue(summaryMap, INTERACTION.MAJOR_COUNT.text(), 0);
			summaryMap.put(INTERACTION.MAJOR_COUNT.text(), ++majorCnt);
			break;
		case 2:
			Integer moderateCount = Util.getIntValue(summaryMap, INTERACTION.MODERATE_COUNT.text(), 0);
			summaryMap.put(INTERACTION.MODERATE_COUNT.text(), ++moderateCount);
			break;
		case 1:
			Integer minorCount = Util.getIntValue(summaryMap, INTERACTION.MINOR_COUNT.text(), 0);
			summaryMap.put(INTERACTION.MINOR_COUNT.text(), ++minorCount);
			break;
		default:
			break;	
		}
		
		
	}
	
	/**
	 * Fetch override interaction data.
	 *
	 * @param reqParams the req params
	 * @return the map
	 */
	private List<Map<String, Object>> fetchOverrideInteractionData(Map<String, Object> reqParams){
		List<Long> orderList = null;
		Map<String, Object> detailReqMap= null;
		try{
			List<Map<String, Object>> overriddenInteraction = interactionDao.fetchOverriddenInteraction(reqParams);
			for(Map<String, Object> mapObj : overriddenInteraction){
				detailReqMap = new HashMap<>();
				detailReqMap.put(INTERACTION.INTERACTION_ID.text(), Util.getIntValue(mapObj, INTERACTION.INTERACTION_ID.text(), 0));
				orderList = interactionDao.fetchOverrideInterDetails(detailReqMap);
				mapObj.put(INTERACTION.ORDER_LIST.text(), orderList);
			}
			return overriddenInteraction;
		} catch (RuntimeException e) {
			EcwLog.AppendExceptionToLog(e);
			return Collections.emptyList();
		}

	}
	
	/**
	 * Gets the interaction severity.
	 *
	 * @param responseObj the response obj
	 * @param maxseverityCode the maxseverity code
	 * @return the interaction severity
	 */
	@SuppressWarnings("unchecked")
	private int getInteractionSeverity(Map<String, Object> responseObj,int maxseverityCode){
		int severityCode = 0;
		Map<String, Object> response = (Map<String, Object>) responseObj.get("doseCheckResponse");
		severityCode = checkForMaxInteractionSeverity((List<Map<String, Object>>) response.get("data"));
		maxseverityCode = severityCode > maxseverityCode ? severityCode :maxseverityCode;
		
		response = (Map<String, Object>) responseObj.get("drugDrugInteractions");
		severityCode = checkForMaxInteractionSeverity((List<Map<String, Object>>) response.get("data"));
		maxseverityCode = (severityCode > maxseverityCode ? severityCode :maxseverityCode);
		
		response = (Map<String, Object>) responseObj.get("drugAllergyInteractions");
		severityCode = checkForMaxInteractionSeverity((List<Map<String, Object>>) response.get("data"));
		maxseverityCode = ( severityCode > maxseverityCode ? severityCode :maxseverityCode);
		
		response = (Map<String, Object>) responseObj.get("drugAlcoholInteractions");
		severityCode = checkForMaxInteractionSeverity((List<Map<String, Object>>) response.get("data"));
		maxseverityCode = ( severityCode > maxseverityCode ? severityCode :maxseverityCode);
		
		response = (Map<String, Object>) responseObj.get("drugFoodInteractions");
		severityCode = checkForMaxInteractionSeverity((List<Map<String, Object>>) response.get("data"));
		maxseverityCode = ( severityCode > maxseverityCode ? severityCode :maxseverityCode);
		
		response = (Map<String, Object>) responseObj.get("drugDiseaseInteractions");
		severityCode = checkForMaxInteractionSeverity((List<Map<String, Object>>) response.get("data"));
		maxseverityCode = ( severityCode > maxseverityCode ? severityCode :maxseverityCode);			
		
		response = (Map<String, Object>) responseObj.get("duplicateTherapyInteractions");
		severityCode = checkForMaxInteractionSeverity((List<Map<String, Object>>) response.get("data"));
		maxseverityCode = ( severityCode > maxseverityCode ? severityCode :maxseverityCode);
		
		response = (Map<String, Object>) responseObj.get("ageBasedWarning");
		severityCode = checkForMaxInteractionSeverity((List<Map<String, Object>>) response.get("data"));
		maxseverityCode = ( severityCode > maxseverityCode ? severityCode :maxseverityCode);
		
		response = (Map<String, Object>) responseObj.get("genderBasedWarning");
		severityCode = checkForMaxInteractionSeverity((List<Map<String, Object>>) response.get("data"));
		maxseverityCode = ( severityCode > maxseverityCode ? severityCode :maxseverityCode);
		
		response = (Map<String, Object>) responseObj.get("pregnancyWarning");
		severityCode = checkForMaxInteractionSeverity((List<Map<String, Object>>) response.get("data"));
		maxseverityCode = ( severityCode > maxseverityCode ? severityCode :maxseverityCode);
		
		response = (Map<String, Object>) responseObj.get("lactatingWarning");
		severityCode = checkForMaxInteractionSeverity((List<Map<String, Object>>) response.get("data"));
		maxseverityCode = ( severityCode > maxseverityCode ? severityCode :maxseverityCode);
		
		return maxseverityCode;
	}
	
	/**
	 * Check for max interaction severity.
	 *
	 * @param interactions the interactions
	 * @return the int
	 */
	private int checkForMaxInteractionSeverity(List<Map<String, Object>> interactions){
		int severity=0;
		if(interactions != null){
			for(Map<String, Object> intercation : interactions){
				severity =	Util.getIntValue(intercation , INTERACTION.INTERACTION_SEVERITY_CODE.text()) > severity ? Util.getIntValue(intercation , INTERACTION.INTERACTION_SEVERITY_CODE.text()) : severity;
			}
		}
		return severity;
	}
	
	/* This method has been written to handle data issue for strength, dose and duration for interaction*/
	private double handleDoubleCast(String data){
		double oDefault = 0;
		try{
			if(data == null || "".equals(data.trim())){
				return oDefault;
			}else{
				return Double.parseDouble(data);
			}
		}catch(NumberFormatException e){
			return oDefault;
		}
	}
	
	private double getRoundOffValue(double value) {
		return Math.round(value * NUMERIC_CONSTS_1000) / NUMERIC_CONSTS_1000;
	} 

	/**
	 * Method based autowired
	 */
	public InteractionDao getInteractionDao() {
		return interactionDao;
	}

	@Autowired
	public void setInteractionDao(InteractionDao interactionDao) {
		this.interactionDao = interactionDao;
	}

	public ConfigurationDictionaryService getConfigurationDictionaryService() {
		return configurationDictionaryService;
	}

	@Autowired
	public void setConfigurationDictionaryService(ConfigurationDictionaryService configurationDictionaryService) {
		this.configurationDictionaryService = configurationDictionaryService;
	}

	public OrdersUtil getOrdersUtil() {
		return ordersUtil;
	}

	@Autowired
	public void setOrdersUtil(OrdersUtil ordersUtil) {
		this.ordersUtil = ordersUtil;
	}

	public SecuritySettingService getSecuritySettingService() {
		return securitySettingService;
	}

	@Autowired
	public void setSecuritySettingService(SecuritySettingService securitySettingService) {
		this.securitySettingService = securitySettingService;
	}

	public FormularySetupDao getFormularySetupDao() {
		return formularySetupDao;
	}

	@Autowired
	public void setFormularySetupDao(FormularySetupDao formularySetupDao) {
		this.formularySetupDao = formularySetupDao;
	}
	
	

}
