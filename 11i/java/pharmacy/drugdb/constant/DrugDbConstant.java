package inpatientWeb.pharmacy.drugdb.constant;

public class DrugDbConstant {

	public enum DRUGDB_URLS{
		DISPENSIBLE_DRUG_FROM_TYPE("GetDispensibleDrugFromType_URL"),
		DRUG_CLASSIFICATIONS("GetDrugClassifications_URL"),
		PACKAGE_SIZE_UOM_LIST("GetPackageSizeUOMList_URL"),
		PACKAGE_TYPE_UOM_LIST("GetPackageTypeUOMList_URL"),
		STRENGTH_UOM_LIST("GetStrengthUOMList_URL"),
		AHFS_CLASSIFICATION_LIST("EnableMSCLINICALAHFSUrl"),
		ROUTE_LIST("EnableMSCLINICALRouteUrl"),
		FORMULATION_LIST("EnableMSCLINICALFormulationUrl"),
		DISPENSIBLE_DRUG_LIST("GetdispDrugList_URL"),
		UNIT_LIST("GetUnitList_URL")
		;

		private String url;	
		
		DRUGDB_URLS(String url) {
			this.url=url;
		}
		public String url(){
			return url;
		}
	}
	
	public enum ID_TYPES{
		
		NDC("NDC"),
		GENERIC_DISP_DRUG_ID("GPI"),
		DISP_DRUG_ID("DDID"),
		ALL("All")
		;

		private String type;	
		
		ID_TYPES(String type) {
			this.type=type;
		}
		public String type(){
			return type;
		}
		
		public static ID_TYPES findByName(String type)
		{
			for(ID_TYPES e : ID_TYPES.values()){
	            if(e.type.equals(type)){ 
	            	return e;
	            }
	        }
			return null;
		}
	}
	
	public enum RESULT_CODES{
		/**  **********************************************************   Success response code range 200-299   								  **********************************************************. */
		/** The Constant SUCCESS. */
		SUCCESS(200,"Success"),
		
		/**  **********************************************************   MODULE specific result codes range 400 + moduleId + section incremental  ***********************************************************. */
		ON_OFF_DAYS_FAILURE(4000101, "On days and off days have not been setup for frequency: "),
		REPEAT_FREQUENCY_FAILURE(4000111,"Repeat cycle has not been setup for frequency: "),
		WEEKELY_DAYS_FAILURE(4000121, "Scheduled days have not been setup for frequnecy: "),
		PRN_FREQUNECY_FAILURE(4000131,"Schedule cycle has not been setup for frequency: "),
		RECCURANCE_REPEAT_FREQUENCY_FAILURE(4000141,"Repeat cycle has not been setup for frequency: "),
		NO_OF_TIMES_FREQUENCY_FAILURE(4000151,"Number of times per day has not been setup for frequency: "),
		
		FREQUENCY_FAILURE(4000201, "Frequency has not been setup ");
		
		private int resultCode;
		private String message;
		
		private RESULT_CODES(int resultCode, String message) {
			this.resultCode = resultCode;
			this.message = message;
		}
		public int getResultCode() {
			return resultCode;
		}
		public String getMessage() {
			return message;
		}
	}
	
	public enum DRUGDB_MESSAGES{
		INVALID_SEARCH_PARAMS("Invalid search parameter"),
		;

		private String message;	
		
		DRUGDB_MESSAGES(String message) {
			this.message=message;
		}
		public String message(){
			return message;
		}
	}
}

