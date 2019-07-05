package inpatientWeb.pharmacy.drugdb.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;
import inpatientWeb.Global.items.dao.IPItemkeyDAO;
import inpatientWeb.Global.medicationhelper.msclinical.model.AHFSClassification;
import inpatientWeb.pharmacy.drugdb.constant.DrugDbConstant.DRUGDB_MESSAGES;
import inpatientWeb.pharmacy.drugdb.constant.DrugDbConstant.DRUGDB_URLS;
import inpatientWeb.pharmacy.drugdb.constant.DrugDbConstant.ID_TYPES;
import inpatientWeb.pharmacy.drugdb.core.model.impl.ResponseData;
import inpatientWeb.pharmacy.drugdb.model.DispensibleDrug;
import inpatientWeb.pharmacy.drugdb.model.DrugClassification;
import inpatientWeb.pharmacy.drugdb.model.DrugClassificationRequest;
import inpatientWeb.pharmacy.drugdb.model.DrugFormulation;
import inpatientWeb.pharmacy.drugdb.model.DrugRequest;
import inpatientWeb.pharmacy.drugdb.model.DrugRoute;
import inpatientWeb.pharmacy.drugdb.model.InteractionRequest;
import inpatientWeb.pharmacy.drugdb.model.Unit;
import inpatientWeb.pharmacy.drugdb.util.DrugDbException;
import inpatientWeb.pharmacy.drugdb.util.Utils;
import inpatientWeb.pharmacy.interaction.constants.InteractionConstants.INTERACTION_MESSAGE;
import inpatientWeb.pharmacy.interaction.service.InteractionService;

@Service
@Lazy
public class DrugDbService {
	
	private InteractionService interactionService;
	
	/**
	 * ******************************************************************************************
	 * 										Dictionary
	 * ******************************************************************************************.
	 *
	 * @param searchText the search text
	 * @return the AHFS classification list
	 */
	public Set<AHFSClassification> getAHFSClassificationList(String searchText){
		try {
			if(searchText == null || searchText.isEmpty()) {
				throw new DrugDbException(DRUGDB_MESSAGES.INVALID_SEARCH_PARAMS.message());
			}
			String responsetxt = Utils.getJsonGetResponseFromParams(new URI(IPItemkeyDAO.getIPItemKeyValueFromName(DRUGDB_URLS.AHFS_CLASSIFICATION_LIST.url(), "")), String.class, searchText);
			if (responsetxt.isEmpty()) {
				return Collections.emptySet();
			}
			Set<AHFSClassification> responseData = null;
			TypeReference<Set<AHFSClassification>> typeReference = new TypeReference<Set<AHFSClassification>>() {};
			responseData = new ObjectMapper().readValue(responsetxt, typeReference);
			return responseData;
		}catch (URISyntaxException ex) {
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException("Invalid URI for drug database");
		}catch(RuntimeException ex){
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException(ex);
		} catch (IOException ex) {
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException("Invalid AHFS Classification json found");
		} 
	}
	
	/**
	 * Gets the route list.
	 *
	 * @param searchText the search text
	 * @return the route list
	 */
	public List<DrugRoute> getRouteList(String searchText){
		try {
			if(searchText == null || searchText.isEmpty()) {
				throw new DrugDbException(DRUGDB_MESSAGES.INVALID_SEARCH_PARAMS.message());
			}
			String responsetxt = Utils.getJsonGetResponseFromParams(new URI(IPItemkeyDAO.getIPItemKeyValueFromName(DRUGDB_URLS.ROUTE_LIST.url(), "")), String.class, searchText);
			if (responsetxt.isEmpty()) {
				return Collections.emptyList();
			}
			List<DrugRoute> responseData = null;
			TypeReference<List<DrugRoute>> typeReference = new TypeReference<List<DrugRoute>>() {};
			responseData = new ObjectMapper().readValue(responsetxt, typeReference);
			return responseData;
		}catch (URISyntaxException ex) {
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException("Invalid URI for drug database");
		}catch(RuntimeException ex){
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException(ex);
		} catch (IOException ex) {
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException("Invalid route list json found");
		} 
	}
	
	/**
	 * Gets the formulation list.
	 *
	 * @param searchText the search text
	 * @return the formulation list
	 */
	public List<DrugFormulation> getFormulationList(String searchText){
		try {
			if(searchText == null || searchText.isEmpty()) {
				throw new DrugDbException(DRUGDB_MESSAGES.INVALID_SEARCH_PARAMS.message());
			}
			String responsetxt = Utils.getJsonGetResponseFromParams(new URI(IPItemkeyDAO.getIPItemKeyValueFromName(DRUGDB_URLS.FORMULATION_LIST.url(), "")), String.class, searchText);
			if (responsetxt.isEmpty()) {
				return Collections.emptyList();
			}
			List<DrugFormulation> responseData = null;
			TypeReference<List<DrugFormulation>> typeReference = new TypeReference<List<DrugFormulation>>() {};
			responseData = new ObjectMapper().readValue(responsetxt, typeReference);
			return responseData;
		}catch (URISyntaxException ex) {
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException("Invalid URI for drug database");
		}catch(RuntimeException ex){
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException(ex);
		} catch (IOException ex) {
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException("Invalid formulation list json found");
		} 
	}
	
	/**
	 * Gets the dispensable drug from type.
	 *
	 * @param requestList the request list
	 * @return the dispensable drug from type
	 */
	public List<DispensibleDrug> getDispensableDrugFromType(List<DrugRequest> requestList){
		try {
			for(DrugRequest request: requestList){
				if(ID_TYPES.findByName(request.getIdType()) == null){
					throw new IllegalArgumentException("Invalid id type for the request");
				}
			}
			String responsetxt = Utils.getJsonPostResponseFromObject(new URI(IPItemkeyDAO.getIPItemKeyValueFromName(DRUGDB_URLS.DISPENSIBLE_DRUG_FROM_TYPE.url(), "")), Utils.getResponseDataWrapper(requestList), String.class);
			if (responsetxt.isEmpty()) {
				return Collections.emptyList();
			}
			ResponseData<List<DispensibleDrug>> responseData = null;
			TypeReference<ResponseData<List<DispensibleDrug>>> typeReference = new TypeReference<ResponseData<List<DispensibleDrug>>>() {};
			responseData = new ObjectMapper().readValue(responsetxt, typeReference);
			return responseData.getData();
		}catch (URISyntaxException ex) {
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException("Invalid URI for drug database");
		}catch(RuntimeException ex){
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException(ex);
		} catch (IOException ex) {
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException("Invalid dispensibleDrug json found");
		} 
	}
	
	/**
	 * Gets the dispensable drug list.
	 *
	 * @param searchText the search text ( * = for All and "DrugnameStart" = for specific drug) 
	 * @return the dispensable drug list
	 */
	public List<DispensibleDrug> getDispensableDrugList(String searchText){
		try {
			if(searchText == null || searchText.isEmpty()) {
				throw new DrugDbException(DRUGDB_MESSAGES.INVALID_SEARCH_PARAMS.message());
			}
			String responsetxt = Utils.getJsonGetResponseFromParams(new URI(IPItemkeyDAO.getIPItemKeyValueFromName(DRUGDB_URLS.DISPENSIBLE_DRUG_LIST.url(), "").concat("/")), String.class, searchText);
			if (responsetxt.isEmpty()) {
				return Collections.emptyList();
			}
			List<DispensibleDrug> responseData = null;
			TypeReference<List<DispensibleDrug>> typeReference = new TypeReference<List<DispensibleDrug>>() {};
			responseData = new ObjectMapper().readValue(responsetxt, typeReference);
			return responseData;
		}catch (URISyntaxException ex) {
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException("Invalid URI for drug database");
		}catch(RuntimeException ex){
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException(ex);
		} catch (IOException ex) {
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException("Invalid route list json found");
		} 
	}
	
	/**
	 * Gets Drug classification based on class type.
	 *
	 * @param request the DrugClassification request (Set TC2, TC4, TC6 or * as classType)
	 * @return List of DrugClassification
	 */
	public List<DrugClassification> getDrugClassifications(DrugClassificationRequest request){
		try {
			String responsetxt = Utils.getJsonPostResponseFromObject(new URI(IPItemkeyDAO.getIPItemKeyValueFromName(DRUGDB_URLS.DRUG_CLASSIFICATIONS.url(), "")), Utils.getResponseDataWrapper(request), String.class);
			if (responsetxt.isEmpty()) {
				return Collections.emptyList();
			}
			ResponseData<List<DrugClassification>> responseData = null;
			TypeReference<ResponseData<List<DrugClassification>>> typeReference = new TypeReference<ResponseData<List<DrugClassification>>>() {};
			responseData = new ObjectMapper().readValue(responsetxt, typeReference);
			return responseData.getData();
		}catch (URISyntaxException ex) {
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException("Invalid URI for drug database");
		}catch(RuntimeException ex){
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException(ex);
		} catch (IOException ex) {
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException("Invalid dispensibleDrug json found");
		} 
	}
	
	/**
	 * Gets the package size UOM list.
	 *
	 * @return the package size UOM list
	 */
	public Set<String> getPackageSizeUOMList(){
		try {
			String responsetxt = Utils.getJsonGetResponse(new URI(IPItemkeyDAO.getIPItemKeyValueFromName(DRUGDB_URLS.PACKAGE_SIZE_UOM_LIST.url(), "")), String.class);
			if (responsetxt.isEmpty()) {
				return Collections.emptySet();
			}
			ResponseData<Set<String>> responseData = null;
			TypeReference<ResponseData<Set<String>>> typeReference = new TypeReference<ResponseData<Set<String>>>() {};
			responseData = new ObjectMapper().readValue(responsetxt, typeReference);
			return responseData.getData();
		}catch (URISyntaxException ex) {
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException("Invalid URI for drug database");
		}catch(RuntimeException ex){
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException(ex);
		} catch (IOException ex) {
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException("Invalid package size UOM json found");
		} 
	}
	
	/**
	 * Gets the package type UOM list.
	 *
	 * @return the package type UOM list
	 */
	public Set<String> getPackageTypeUOMList(){
		try {
			String responsetxt = Utils.getJsonGetResponse(new URI(IPItemkeyDAO.getIPItemKeyValueFromName(DRUGDB_URLS.PACKAGE_TYPE_UOM_LIST.url(), "")), String.class);
			if (responsetxt.isEmpty()) {
				return Collections.emptySet();
			}
			ResponseData<Set<String>> responseData = null;
			TypeReference<ResponseData<Set<String>>> typeReference = new TypeReference<ResponseData<Set<String>>>() {};
			responseData = new ObjectMapper().readValue(responsetxt, typeReference);
			return responseData.getData();
		}catch (URISyntaxException ex) {
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException("Invalid URI for drug database");
		}catch(RuntimeException ex){
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException(ex);
		} catch (IOException ex) {
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException("Invalid package type UOM json found");
		} 
	}
	
	/**
	 * Gets the strength UOM list.
	 *
	 * @return the strength UOM list
	 */
	public Set<String> getStrengthUOMList(){
		try {
			String responsetxt = Utils.getJsonGetResponse(new URI(IPItemkeyDAO.getIPItemKeyValueFromName(DRUGDB_URLS.STRENGTH_UOM_LIST.url(), "")), String.class);
			if (responsetxt.isEmpty()) {
				return Collections.emptySet();
			}
			ResponseData<Set<String>> responseData = null;
			TypeReference<ResponseData<Set<String>>> typeReference = new TypeReference<ResponseData<Set<String>>>() {};
			responseData = new ObjectMapper().readValue(responsetxt, typeReference);
			return responseData.getData();
		}catch (URISyntaxException ex) {
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException("Invalid URI for drug database");
		}catch(RuntimeException ex){
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException(ex);
		} catch (IOException ex) {
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException("Invalid strength UOM json found");
		} 
	}
	
	/**
	 * Gets the unit list.
	 *
	 * @param request the request
	 * @return the unit list
	 */
	public List<Unit> getUnitList(DrugRequest request){
		try {
			if(ID_TYPES.findByName(request.getIdType()) == null){
					throw new IllegalArgumentException("Invalid id type for the request");
			}
			
			String responsetxt = Utils.getJsonPostResponseFromObject(new URI(IPItemkeyDAO.getIPItemKeyValueFromName(DRUGDB_URLS.UNIT_LIST.url(), "")), Utils.getResponseDataWrapper(request), String.class);
			if (responsetxt.isEmpty()) {
				return Collections.emptyList();
			}
			ResponseData<List<Unit>> responseData = null;
			TypeReference<ResponseData<List<Unit>>> typeReference = new TypeReference<ResponseData<List<Unit>>>() {};
			responseData = new ObjectMapper().readValue(responsetxt, typeReference);
			return responseData.getData();
		}catch (URISyntaxException ex) {
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException("Invalid URI for drug database");
		}catch(RuntimeException ex){
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException(ex);
		} catch (IOException ex) {
			EcwLog.AppendExceptionToLog(ex);
			throw new IllegalArgumentException("Invalid unit list json found");
		} 
	}
	/********************************************************************************************
	 * 										Interaction
	 ********************************************************************************************/
	
	/**
	 * This service is used to get the interactions.
	 *
	 * @param interactionRequest
	 * 							This parameter contain  episodeEncounterId, moduleEncounterId, calledFrom and facilityId
	 * @param userId the user id
	 * @return the interactions
	 */
	public String getInteractions(InteractionRequest interactionRequest, int userId){
		try {
			interactionRequest.setUserId(userId);
			return interactionService.getDrugInteractions(interactionRequest);
		}catch (DrugDbException ex){
			EcwLog.AppendExceptionToLog(ex);
			return String.valueOf(Utils.getErrorData(ex.getMessage()));
		}catch (ParseException | URISyntaxException | RuntimeException ex){
			EcwLog.AppendExceptionToLog(ex);
			return String.valueOf(Utils.getErrorData(INTERACTION_MESSAGE.INTERACTION_EXCEPTION.text()));
		} 
	}

	/********************************************************************************************
	 * 										Autowired
	 ********************************************************************************************/
	public InteractionService getInteractionService() {
		return interactionService;
	}

	@Autowired
	public void setInteractionService(InteractionService interactionService) {
		this.interactionService = interactionService;
	}
	
	
	
}
