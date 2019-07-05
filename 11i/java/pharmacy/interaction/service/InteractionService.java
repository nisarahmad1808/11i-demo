package inpatientWeb.pharmacy.interaction.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;
import inpatientWeb.Global.items.dao.IPItemkeyDAO;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.FormularySetupDao;
import inpatientWeb.pharmacy.beans.PatientProfile;
import inpatientWeb.pharmacy.drugdb.model.InteractionRequest;
import inpatientWeb.pharmacy.drugdb.util.DrugDbException;
import inpatientWeb.pharmacy.drugdb.util.Utils;
import inpatientWeb.pharmacy.interaction.constants.InteractionConstants.INTERACTION;
import inpatientWeb.pharmacy.interaction.constants.InteractionConstants.INTERACTION_FORMAT;
import inpatientWeb.pharmacy.interaction.constants.InteractionConstants.INTERACTION_MESSAGE;
import inpatientWeb.pharmacy.interaction.constants.InteractionConstants.INTERACTION_TYPES;
import inpatientWeb.pharmacy.interaction.dao.InteractionDao;
import inpatientWeb.pharmacy.interaction.model.DrugInteraction;
import inpatientWeb.pharmacy.service.PatientProfileService;
import inpatientWeb.utils.IPTzUtils;
import inpatientWeb.utils.StatusMap;
import inpatientWeb.utils.Util;

@Service
@Scope("prototype")
@Lazy
public class InteractionService {

	private InteractionDao interactionDao;
	private FormularySetupDao formularySetupDao;
	private PatientProfileService patientProfileService;
	private InteractionDetailsService interactionDetailsService;
	
	/**
	 * This service is gets drug interactions.
	 *
	 * @param interactionRequest
	 * 							 This parameter contain  episodeEncounterId, moduleEncounterId, calledFrom and facilityId
	 * @return the drug interactions
	 * @throws ParseException the parse exception	
	 * @throws URISyntaxException the URI syntax exception
	 * @throws DrugDbException 
	 */
	public String getDrugInteractions(InteractionRequest interactionRequest) throws ParseException, URISyntaxException{
		/**
		 * Prepare request Map 
		 */
		validateInteractionRequest(interactionRequest);
		
		Map<String, Object> reqParams = new HashMap<>();
		reqParams.put(INTERACTION.EPISODE_ENC_ID.text(), interactionRequest.getEpisodeEncId());
		reqParams.put(INTERACTION.CALLED_FROM.text(), interactionRequest.getCalledFrom());
		reqParams.put(INTERACTION.SCREEN_FLAG.text(), interactionRequest.isScreenFlag());
		reqParams.put(INTERACTION.UID.text(), interactionRequest.getUserId());
		reqParams.put(INTERACTION.DRUG_LIST.text(), interactionRequest.getDrugList());
		reqParams.put(INTERACTION.MODULE_ENC_ID.text(), interactionRequest.getModuleEncId());
		reqParams.put(INTERACTION.ENC_TYPE.text(), interactionRequest.getEncType());
		reqParams.put(INTERACTION.STAGING_ORDER_IDS.text(), interactionRequest.getRecentOrderList());
		/**
		 * Fetch order details from episodeEncounterId
		 */
		Map<String, Object> interactionReqMap = interactionDetailsService.getMedOrderDetails(reqParams);
		reqParams.put(INTERACTION.STAGING_MED_ORDER_ID.text(), interactionReqMap.get(INTERACTION.STAGING_MED_ORDER_ID.text()));
		reqParams.put(INTERACTION.HOMEMED_ORDER_ID.text(), interactionReqMap.get(INTERACTION.HOMEMED_ORDER_ID.text()));
		reqParams.put(INTERACTION.CUSTOM_MED_ORDERS.text(), interactionReqMap.get(INTERACTION.CUSTOM_MED_ORDERS.text()));
		
		/**
		 * Convert Map to Json string and prepare request object
		 */
		JSONObject requestObj = getInteractionRequest(reqParams, interactionReqMap);

		/**
		 * Send request to DrugDb to fetch interaction
		 */
		String response = Utils.getJsonPostResponse(new URI(IPItemkeyDAO.getIPItemKeyValueFromName("GetDrugInteractions_URL", "")), requestObj, String.class);
		JSONParser parser = new JSONParser();
		JSONObject responseObj = (JSONObject) parser.parse(response);
		
		/**
		 * Post response process
		 */
		reqParams.put(INTERACTION.DOSE_CHECK_ERROR.text(), interactionReqMap.get(INTERACTION.DOSE_CHECK_ERROR.text()));
		responseObj = interactionDetailsService.postProcessResponse(reqParams, requestObj, responseObj, interactionRequest);
		return String.valueOf(Utils.getSuccessData(responseObj));
	}
		
	/**
	 * Gets the ms clinical request.
	 *
	 * @param request the request
	 * @return the ms clinical request
	 * @throws DrugDbException 
	 */
	@SuppressWarnings("unchecked")
	private JSONObject getInteractionRequest(Map<String, Object> reqParams, Map<String, Object> ordersMap){
		
		JSONObject requestObj = new JSONObject();
		if(ordersMap.isEmpty()){
			return requestObj;
		}
		JSONParser parser = new JSONParser();
		Gson gson = new Gson(); 
		String json = gson.toJson(ordersMap.get(INTERACTION.DRUG_LIST.text()));
		JSONArray paramArray;
		try {
			paramArray = (JSONArray) parser.parse(json);
		} catch (ParseException e) {
			throw new DrugDbException(INTERACTION_MESSAGE.INTERACTION_ERROR.text() + INTERACTION_MESSAGE.INTERACTION_REQUEST_INVALID.text());
		}
		
		String requestedFor = Util.getStrValue(reqParams, "requestedFor");
		if("getDrugDrugInteraction".equalsIgnoreCase(requestedFor)){
			/* Get drug List */
			if (reqParams.isEmpty()) {
				requestObj.put(INTERACTION.DRUG_LIST.text(), new JSONArray());
				return requestObj;
			}	
			requestObj.put(INTERACTION.DRUG_LIST.text(), paramArray);
			return requestObj;
		}
		/* Get Order List */
		if (StringUtils.isBlank(String.valueOf(paramArray))) {
			requestObj.put(INTERACTION.ORDER_LIST.text(), new JSONArray());
			return requestObj;
		}
		
		Long episodeEncId = Util.getLongValue(reqParams, INTERACTION.EPISODE_ENC_ID.text()); 
		Long moduleEncId = Util.getLongValue(reqParams, INTERACTION.MODULE_ENC_ID.text());
		JSONObject patientProfileObj = (JSONObject) JSONValue.parse(new Gson().toJson(patientProfileService.getPatientProfile(episodeEncId, moduleEncId), new TypeToken<PatientProfile>() {}.getType()));
		requestObj.put(INTERACTION.PATIENT_PROFILE.text(), patientProfileObj);
		requestObj.put(INTERACTION.ORDER_LIST.text(), paramArray);
		return requestObj;
	}
	
	/**
	 * Save interaction action.
	 *
	 * @param requestMap the request map
	 * @return the status map
	 */
	@SuppressWarnings("unchecked")
	public StatusMap saveInteractionAction(Map<String, Object> requestMap){
		int episodeEncId = 0;
		int interactionId = 0;
		Map<String, Object> interactionMap = null;
		StatusMap response = new StatusMap();
		try {
			List<DrugInteraction> insertList = new ArrayList<>();
			episodeEncId = Util.getIntValue(requestMap, INTERACTION.EPISODE_ENC_ID.text(), 0);
			interactionMap = (Map<String, Object>) requestMap.get(INTERACTION.INTERACTIONS.text());
			String drugIntHtml = (String) requestMap.get(INTERACTION.DRUG_INTERACTION_HTML.text());
			String providerAction = (String) requestMap.get(INTERACTION.ACTION.text());
			int userId = Util.getIntValue(requestMap, INTERACTION.UID.text(),0);
			
			/************************************
			 * Fetch interaction details to save*
			 ************************************/
			insertList.addAll(prepareInteractionDataList((Map<String, Object>) interactionMap.get("drugDrugInteractions"), userId, episodeEncId, INTERACTION_TYPES.DRUG_DRUG_INTERACTION));
			insertList.addAll(prepareInteractionDataList((Map<String, Object>) interactionMap.get("drugFoodInteractions"), userId, episodeEncId, INTERACTION_TYPES.DRUG_FOOD_INTERACTION));
			insertList.addAll(prepareInteractionDataList((Map<String, Object>) interactionMap.get("drugAlcoholInteractions"), userId, episodeEncId, INTERACTION_TYPES.DRUG_ALCOHOL_INTERACTION));
			insertList.addAll(prepareInteractionDataList((Map<String, Object>) interactionMap.get("drugAllergyInteractions"), userId, episodeEncId, INTERACTION_TYPES.DRUG_ALLERGY_INTERACTION));
			insertList.addAll(prepareInteractionDataList((Map<String, Object>) interactionMap.get("drugDiseaseInteractions"), userId, episodeEncId, INTERACTION_TYPES.DRUG_DISEASE_INTERACTION));
			insertList.addAll(prepareInteractionDataList((Map<String, Object>) interactionMap.get("duplicateTherapyInteractions"), userId, episodeEncId, INTERACTION_TYPES.DUPLICATE_THERAPY_INTERACTION));
			insertList.addAll(prepareInteractionDataList((Map<String, Object>) interactionMap.get("doseCheckResponse"), userId, episodeEncId, INTERACTION_TYPES.DOSE_CHECK));
			insertList.addAll(prepareInteractionDataList((Map<String, Object>) interactionMap.get("pregnancyWarning"), userId, episodeEncId, INTERACTION_TYPES.PREGNANCY_WARNING));
			insertList.addAll(prepareInteractionDataList((Map<String, Object>) interactionMap.get("lactatingWarning"), userId, episodeEncId, INTERACTION_TYPES.LACTATING_WARNING));
			insertList.addAll(prepareInteractionDataList((Map<String, Object>) interactionMap.get("ageBasedWarning"), userId, episodeEncId, INTERACTION_TYPES.AGE_BASED_WARNING));
			insertList.addAll(prepareInteractionDataList((Map<String, Object>) interactionMap.get("genderBasedWarning"), userId, episodeEncId, INTERACTION_TYPES.GENDER_BASED_WARNING));
			if(insertList.isEmpty()){
				response.setSuccess();
				return response;
			}
			/**********************************************
			 * Save provider action with interaction HTML *
			 **********************************************/
			interactionId = interactionDao.insertDrugInteraction(insertList,userId);
			if(interactionId <= 0){
				response.setFail(INTERACTION_MESSAGE.OVERRIDE_INSERT_FAILURE.text());
				return response;
			}
			
			if(!interactionDao.logDrugInteraction(episodeEncId,interactionId,providerAction,drugIntHtml,userId)){
				response.setFail(INTERACTION_MESSAGE.ACTION_LOG_INSERT_FAILURE.text());
				return response;
			}
			response.setSuccess();
			return response;
		} catch (RuntimeException e) {
			EcwLog.AppendExceptionToLog(e);
			response.setFail(INTERACTION_MESSAGE.INTERACTION_OVERRIDE_EXCEPTION.text());
			return response;
		}
	}
	
	/**
	 * Update interaction action.
	 *
	 * @param requestMap the request map
	 * @return the status map
	 */
	public StatusMap updateInteractionAction(Map<String, Object> requestMap){
		boolean updateStatus = false;
		StatusMap response = new StatusMap();
		try {
			/**********************************************
			 * Update interaction override acknowledgement *
			 **********************************************/
			if (requestMap == null || requestMap.isEmpty() || Util.getIntValue(requestMap, INTERACTION.INTERACTION_ID.text(), 0) <= 0) {
				response.setFail(INTERACTION_MESSAGE.INVALID_INTERACTION_ID.text());
				return response;
			}
			updateStatus = interactionDao.updateInteractionAction(requestMap);
			if(!updateStatus){
				response.setFail(INTERACTION_MESSAGE.INTERACTION_ACKNOWLEDGE_EXCEPTION.text());
				return response;
			}
			response.put("isAcknowledge", Util.getIntValue(requestMap, INTERACTION.ACKNOWLEDGE_FLAG.text(), 0));
			response.setSuccess();
			
			return response;
		} catch (RuntimeException e) {
			EcwLog.AppendExceptionToLog(e);
			response.setFail(INTERACTION_MESSAGE.INTERACTION_ACKNOWLEDGE_EXCEPTION.text());
			return response;
		}
	}
	
	/**
	 * Prepare interaction data list.
	 *
	 * @param interactionMaps the interaction maps
	 * @param userId the user id
	 * @param episodeEncId the episode enc id
	 * @param interactionType the interaction type
	 * @return the list
	 */
	private List<DrugInteraction> prepareInteractionDataList(Map<String , Object> interactions ,int userId, int episodeEncId, INTERACTION_TYPES interactionType){
		List<Map<String , Object>> interactionMaps = (List<Map<String, Object>>) interactions.get("data");
		List<DrugInteraction> interactionList = new ArrayList<>(interactionMaps.size());
		for(Map<String, Object> interaction : interactionMaps){
			interaction.put(INTERACTION.UID.text(), userId);
			if(Util.getIntValue(interaction, INTERACTION.OVERRIDE_REASON.text(), 0) > 0 && Util.getIntValue(interaction, INTERACTION.OVERRIDE_REASON.text(), 0) != Util.getIntValue(interaction, INTERACTION.PREV_OVERRIDE_REASON.text(), 0)){				
				interactionList.add(setInteractionData(interaction , episodeEncId, interactionType));
			}
		}
		return interactionList;
	}

		
	/**
	 * Sets the interactino data.
	 *
	 * @param reqParams the req params
	 * @param episodeEncId the episode enc id
	 * @param interactionType the interaction type
	 * @return the drug interaction
	 */
	@SuppressWarnings("unchecked")
	private DrugInteraction setInteractionData(Map<String, Object> reqParams, int episodeEncId, INTERACTION_TYPES interactionType){
		DrugInteraction interactionObj = null;
		interactionObj = new DrugInteraction();
		// --------------mandatory fields start--------
		interactionObj.setEncounterId(episodeEncId);
		interactionObj.setSeverityCode(Util.getIntValue(reqParams, INTERACTION.INTERACTION_SEVERITY_CODE.text(), 0));
		interactionObj.setDescription(Util.getStrValue(reqParams, INTERACTION_TYPES.findById(interactionType.typeId()).descriptionName() , ""));
		interactionObj.setOverrideReason(Util.getIntValue(reqParams, INTERACTION.OVERRIDE_REASON.text(), 0));
		interactionObj.setInteractionCategory(interactionType.typeId());
		interactionObj.setOverrideBy(Util.getIntValue(reqParams, INTERACTION.UID.text()));
		String formatedDate = IPTzUtils.getDateTimeStrForUser(Util.getIntValue(reqParams, "userId"),INTERACTION_FORMAT.DATE_TIME_FORMAT.format());
		interactionObj.setOverrideDateTime(formatedDate);
		interactionObj.setConditionTypeId(Util.getLongValue(reqParams, INTERACTION.CONDITION_TYPE_ID.text(),0));
		List<Map<String, Object>> drugsDetails =  (List<Map<String, Object>>) reqParams.get(INTERACTION.DRUG_DETAILS.text());
		interactionObj.setOrderIdList(drugsDetails);
		
		// --------------mandatory fields end--------
		return interactionObj;
	}
	
	private void validateInteractionRequest(InteractionRequest interactionRequest){
		if(interactionRequest.getEpisodeEncId() <= 0){
			throw new DrugDbException(INTERACTION_MESSAGE.INTERACTION_ERROR.text() + INTERACTION_MESSAGE.EPISODE_ENC_NOT_FOUND.text());
		}
		
		if(interactionRequest.getFacilityId() <= 0){
			throw new DrugDbException(INTERACTION_MESSAGE.DOSE_CHECK_ERROR.text() + INTERACTION_MESSAGE.USER_FACILITY_NOT_FOUND.text());
		}
		
		if(interactionRequest.getUserId() <= 0){
			throw new DrugDbException(INTERACTION_MESSAGE.INTERACTION_ERROR.text() + INTERACTION_MESSAGE.USER_ID_NOT_FOUND.text());
		}
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

	public FormularySetupDao getFormularySetupDao() {
		return formularySetupDao;
	}

	@Autowired
	public void setFormularySetupDao(FormularySetupDao formularySetupDao) {
		this.formularySetupDao = formularySetupDao;
	}

	public PatientProfileService getPatientProfileService() {
		return patientProfileService;
	}

	@Autowired
	public void setPatientProfileService(PatientProfileService patientProfileService) {
		this.patientProfileService = patientProfileService;
	}

	public InteractionDetailsService getInteractionDetailsService() {
		return interactionDetailsService;
	}

	@Autowired
	public void setInteractionDetailsService(InteractionDetailsService interactionDetailsService) {
		this.interactionDetailsService = interactionDetailsService;
	}

	
	
}
