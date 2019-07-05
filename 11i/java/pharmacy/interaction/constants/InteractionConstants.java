package inpatientWeb.pharmacy.interaction.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * DrugInteractionConstants : 
 * 
 * @author dishagnab
 * @since 28th August,2017
 * @copyrights: eClinicalWorks LLC. 2016
 * 
 */

public class InteractionConstants {
	
	static final String ACTIVE = "ACTIVE";
	
	public enum INTERACTION{
		
		ACKNOWLEDGE_BY("acknowledgeBy"),
		ACKNOWLEDGE_DATE_TIME("acknowledgeDateTime"),
		ACKNOWLEDGE_FLAG("acknowledgeFlag"),
		ACTION("action"),
		ALLERGEN_TYPE_DRUG("drug"),
		AUTO_POPUP("autoPopup"),
		CALLED_FROM("calledFrom"),
		DATA("data"),
		DOSE_CHECK_ERROR("doseCheckErrors"),
		DOSE_CHECK_RESPONSE("doseCheckResponse"),
		DOSE_CHECK_FLAG("doseCheck"),
		DRUG_DETAILS("drugDetails"),
		DRUG_INTERACTION_HTML("drugInterHtml"),
		DRUG_LIST("drugList"),
		DRUG_NAME("drugName"),
		DRUGTOALLERGY("ALLERGY"),
		DRUGTOALCOHOL("ALCOHOL"),
		DRUGTODISEASE("DISEASE"),
		DRUGTODRUG("DRUG"),
		DRUGTOFOOD("FOOD"),
		DUPLICATETHERAPY("DUPLICATETHERAPY"),
		ENC_PARAM_TYPE("encounter"),
		ENC_TYPE("encType"),
		EPISODE_ENC_ID("episodeEncId"),
		FORMULARY_LIST("formularyList"),
		HOMEMED_ORDER_ID("homeMedOrderIds"),
		INTERACTION("interaction"),
		INTERACTIONS("interactions"),
		INTERACTION_ID("interactionId"),
		INTERACTION_SEVERITY_CODE("interactionSeverityCode"),
		ID("id"),
		ID_TYPE("idType"),
		IS_STAGING_ORDER("isStagingOrder"),
		ITEM_ID("itemID"),
		ITEM_NAME("itemName"),
		MSG("msg"),
		MAJOR_COUNT("majorCount"),
		MESSAGE("message"),
		MINOR_COUNT("minorCount"),
		MODERATE_COUNT("moderateCount"),
		MODULE_ENC_ID("moduleEncId"),
		MULTIPLE_MEDICATION("multipleMedication"),
		ORDER_LIST("orderList"),
		ORDER_ID("orderId"),
		OVERRIDE_REASON("overrideReason"),
		PATIENT_PROFILE("ecwPatientProfile"),
		PREV_OVERRIDE_REASON("prevOverrideReason"),
		ROUTE_ID("routeID"),
		SCREEN_FLAG("screenFlag"),
		SEARCH_TYPE("searchType"),
		STAGING_MED_ORDER_ID("stagingMedOrderIds"),
		STAGING_ORDER_IDS("stagingOrderIds"),
		CUSTOM_MED_ORDERS("customMedOrders"),
		STATUS_CODE("statusCode"),
		UID("uid"),
		USER_TYPE("UserType"),
		FACILITY_ID("facilityId"),
		CONDITION_TYPE_ID("conditionTypeId"),
		PASSES_SCREENING("passesScreening"),
		;
		
		private String text;
		
		INTERACTION(String text) {
			this.text=text;
		}
		public String text(){
			return text;
		}
	}
	
	
	/**
	 * These is used to fetch id on order type 
	 * @author dishagnab
	 *
	 */
	public enum ORDER_TYPES {
		
		DEFAULT("Default",0),
		STAGING("Staging",1),
		ACTIVE("Active",2);
		
		private String value;
		private int id;
		
		private static Map<Integer, ORDER_TYPES> valueMap = new HashMap<>();
		
		static {
			for (ORDER_TYPES stagingOrderTypes : values()) {
				valueMap.put(stagingOrderTypes.id(), stagingOrderTypes);
			}
		}
		
		ORDER_TYPES(String value,int id){
			this.value = value;
			this.id = id;
		}
		
		public String value(){
			return value;
		}
		public int id(){
			return id;
		}
		
		public static ORDER_TYPES findByid(int id) {
			ORDER_TYPES types = valueMap.get(id);
			if (types != null)
				return types;

			throw new IllegalArgumentException(" No such order type exist -" + id);
		}
	}
	
	
	/**
	 * The Enum RENAL_TYPES.
	 */
	public enum RENAL_TYPES {
		
		SERUM_CREATININE("scr"),
		CREATININE_CLEARANCE("crcl")
		;
		private String type;

		RENAL_TYPES(String type){
			this.type = type;
		}
		public String type() {
			return type;
		}
	}
	
	/**
	 * These constant includes database table name used for interaction
	 * @author dishagnab
	 *
	 */
	public enum INTERACTION_TABLES {
		
		DRUG_INTERACTION("ip_druginteraction"),
		DRUG_INTERACTION_DETAILS("ip_druginteraction_details"),
		DRUG_INTERACTION_LOG("ip_logdruginteraction"),
		PHARMACY_GEN_ORDER_SETTING("ip_pharmacy_gen_order_setting");

		private String table;

		INTERACTION_TABLES(String table){
			this.table = table;
		}
		public String tabel() {
			return table;
		}
	}
	
	/**
	 * These constant described different interaction and their respective numbers, which are used to store in database 
	 * @author dishagnab
	 *
	 */
	public enum INTERACTION_TYPES {
		
		DRUG_DRUG_INTERACTION("drugDrugInteractions",1, "interactionDescription"),
		DRUG_ALLERGY_INTERACTION("drugAllergyInteractions",2, "reactionMessage"),
		DRUG_FOOD_INTERACTION("drugFoodInteractions",3, "reactionMessage"),
		DRUG_ALCOHOL_INTERACTION("drugAlcoholInteractions",4, "alcoholInteractionMessage"),
		DRUG_DISEASE_INTERACTION("drugDiseaseInteractions",5, "contraindicationDescription"),
		DUPLICATE_THERAPY_INTERACTION("duplicateTherapyInteractions",6, "message"),
		DOSE_CHECK("doseCheckResponse",7, ""),
		PREGNANCY_WARNING("pregnancyWarning",8, "contraindicationDescription"),
		LACTATING_WARNING("lactatingWarning",9, "contraindicationDescription"),
		AGE_BASED_WARNING("ageBasedWarning",10, "contraindicationDescription"),
		GENDER_BASED_WARNING("genderBasedWarning",11, "contraindicationDescription")

		;
		
		private String type;
		private int typeId;
		private String descriptionName;
		
		INTERACTION_TYPES(String type,int id, String descriptionName){
			this.type = type;
			this.typeId = id;
			this.descriptionName = descriptionName;
		}
		
		public String type(){
			return type;
		}
		public int typeId(){
			return typeId;
		}
		public String descriptionName(){
			return descriptionName;
		}
		
		public static INTERACTION_TYPES findByValue(String value)
		{
		    for(INTERACTION_TYPES interactionTypes : values())
		    {
		        if( interactionTypes.type.equals(value))
		        	return interactionTypes;
		    }
		    throw new IllegalArgumentException(" No such interaction type exist -"+ value);
		}
		
		public static INTERACTION_TYPES findById(int id)
		{
		    for(INTERACTION_TYPES interactionTypes : values())
		    {
		        if( interactionTypes.typeId == id)
		        	return interactionTypes;
		    }
		    throw new IllegalArgumentException(" No such interaction id exist -"+ id);
		}
	}

	/**
	 * These constant describes the common messages which is used in the interaction process 
	 * @author dishagnab
	 *
	 */
	public enum INTERACTION_MESSAGE{
		INTERACTION_ERROR("Interaction error: "),
		DOSE_CHECK_ERROR("Dose check error: "),
		EPISODE_ENC_NOT_FOUND("Invalid encounter"),
		USER_ID_NOT_FOUND("Invalid user found "),
		USER_FACILITY_NOT_FOUND("Facility has not been setup for the user"),
		ENCOUNTER_FACILITY_NOT_FOUND("Invalid facility found for selected encounter"),
		ENCOUNTER_TYPE_NOT_FOUND("Invalid encounter type"),
		MEDICATION_ORDER_LOAD_EXCEPTION("Exception occured while loading medication order details"),
		OVERRIDE_INSERT_FAILURE("Exception occured while override inserting override action"),
		ACTION_LOG_INSERT_FAILURE("Exception occured while logging interaction"),
		INTERACTION_OVERRIDE_EXCEPTION("Exception occured while overriding interactions"),
		OVERRIDDEN_DETAILS_EXCEPTION("Exception occured while loading override details"),
		INTERACTION_ACKNOWLEDGE_EXCEPTION("Exception occured while acknowledging the interaction"),
		DRUG_INTERACTION_SETTING_EXCEPTION("Exception occured in drug interaction admin settings"),
		INVALID_INTERACTION_ID("Invalid interaction"),
		FORMULARY_DRUGINTERACTION_FETCH_EXCEPTION("Exception occured while loading interaction data from formulary"),
		OVERRIDE_REASON_LOAD_EXCEPTION("Exception occured while loading override reasons"),
		INVALID_FACILITY_ID("Invalid facility"),
		INTERACTION_LOADING_EXCEPTION("Exception occured while loading interaction"),
		INTERACTION_REQUEST_INVALID("Invalid interaction request"),
		INVALID_FREQUENCY_DURATION("Invalid frequency duration, No proper mapping value found for frequnency"),
		INTERACTION_EXCEPTION("Error occured while fetching interaction")
		;
		private String text;
		
		INTERACTION_MESSAGE(String text) {
			this.text=text;
		}
		public String text(){
			return text;
		}
	}
	
	/**
	 * These constant is used to describe the date format 
	 * @author dishagnab
	 *
	 */
	public enum INTERACTION_FORMAT{
		
		DATE_TIME_FORMAT("yyyy-M-dd hh:mm:ss");
		
		private String format;
		
		INTERACTION_FORMAT(String format) {
			this.format=format;
		}
		public String format(){
			return format;
		}
	}

	/**
	 * These is used to fetch id based on key type 
	 * @author dishagnab
	 *
	 */
	public enum INTERACTION_KEY_ID{
		
		ROUTED_DRUG_ID("routedDrugId"),
		NDC("NDC"),
		DRUG_NAME_ID("drugNameId"),
		GPI("GPI");
		
		private String text;
		
		INTERACTION_KEY_ID(String text) {
			this.text=text;
		}
		public String text(){
			return text;
		}
	}
	
	/**
	 * These is used to fetch value based on interaction type 
	 * @author dishagnab
	 *
	 */
	public enum INTERACTION_SEVERITY_TYPES {
		
		MINOR("Minor",1),
		MODERATE("Moderate",2),
		MAJOR("Major",3);
		
		private String value;
		private int id;
		
		private static Map<Integer, INTERACTION_SEVERITY_TYPES> valueMap = new HashMap<>();
		
		static {
			for (INTERACTION_SEVERITY_TYPES interactionSeveiryTypes : values()) {
				valueMap.put(interactionSeveiryTypes.id(), interactionSeveiryTypes);
			}
		}
		
		INTERACTION_SEVERITY_TYPES(String value,int id){
			this.value = value;
			this.id = id;
		}
		
		public String value(){
			return value;
		}
		public int id(){
			return id;
		}
		
		public static INTERACTION_SEVERITY_TYPES findByid(int id) {
			INTERACTION_SEVERITY_TYPES types = valueMap.get(id);
			if (types != null)
				return types;

			throw new IllegalArgumentException(" No such interaction type exist -" + id);
		}
	}
	
	/**
	 * These constant use to fetch screen name for comparison
	 * @author dishagnab
	 *
	 */
	public enum ORDERS_SCREEN {
		
		NEW_ORDERS					("NewOrders","ALL","W",true),
		MED_RECONCILIATION_ADMIT	("MedReconciliationAdmit","ALL","W",true),
		EMAR						("eMAR",ACTIVE,"R",false),
		MED_RECONCILIATION_DISCHARGE("MedReconciliationDischarge",ACTIVE,"W",false),
		PHARMACY					("Pharmacy",ACTIVE,"W",false),
		HOME_MEDICATION				("HomeMedication","HOME","",false),
		DEFAULT						("",ACTIVE,"R",false)
		;

		private String screenName;
		private String ordersCategory;
		private String accessRights;
		private boolean newDrugWithActiveFlag;

		ORDERS_SCREEN(String screenName,String ordersCategory,String accessRights,boolean newDrugWithActiveFlag){
			this.screenName = screenName;
			this.ordersCategory = ordersCategory;
			this.accessRights = accessRights;
			this.newDrugWithActiveFlag = newDrugWithActiveFlag;
		}
		public String screenName() {
			return screenName;
		}
		
		public String ordersCategory() {
			return ordersCategory;
		}
		
		public String accessRights() {
			return accessRights;
		}
		
		public boolean newDrugWithActiveFlag() {
			return newDrugWithActiveFlag;
		}
		
		public static ORDERS_SCREEN findByScreen(String screen)
		{
			for(ORDERS_SCREEN e : ORDERS_SCREEN.values()){
	            if(e.screenName.equals(screen)){ 
	            	return e;
	            }
	        }
	        return null;
		}
	}
}
