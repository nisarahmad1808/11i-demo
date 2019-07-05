package inpatientWeb.pharmacy.interfaces.inbound.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DispensingInterfaceInboundModel{
	
	/*												Custom Properties Start								*/
	@JsonProperty(value="MSGLOGID")
	private String msgLogId="0";
	@JsonProperty(value="TRANSACTIONTYPE") 
	private String transactionType="";
	@JsonProperty(value="DISPENSESTOCKID") 
	private String dispenseStockId=null;
	@JsonProperty(value="DISPENSESTOCKAREANAME") 
	private String dispenseStockAreaName="0";
	@JsonProperty(value="EXISTINGQTY") 
	private String existingQty="0";
	/*												Custom Properties End									*/
	
	/*                                Inbound Message Properties Start                                    */	
	// **  Application Name from were this message is generated
	@JsonProperty(value="OSENDAPP") 
	private String oSendApp=""; // s = "eCW"  Field 3
	// **  Facility Name from were this message is sent  Field 4
	@JsonProperty(value="RSENDFACNAME") 
	private String rSendFacName=""; 
	// ** Receiving Application Name were this message will be received
	@JsonProperty(value="RRECAPP")
	private String rRecApp = ""; 
	// ** Receiving FacilityName Name were this message will be received
	@JsonProperty(value="RRECFACNAME")
	private String rRecFacName = ""; 
	// Date time when message is created
	@JsonProperty(value="RDATETIMETRANSMISSION")
	private String rDateTimeTransmission="";
	// *** Standard HL7 code ie(RDE) for pharmacy dispensing order  Field 9.1
	@JsonProperty(value="RMESSAGETYPE")
	private String rMessageType=""; // eg = DFT ,ZPM
	// ** Unique id that will be generated during HL7 message creation  Field 10
	@JsonProperty(value="RMESSAGECODE")
	private String rMessageCode=""; // eg = DFT ,ZPM
	// ** Unique id that will be generated during HL7 message creation  Field 10
	@JsonProperty(value="RMESSAGECONTROLID")
	private String rMessageControlId="";
	// In case of Pyxis - P (Care Fusion) Aysent - P:Production D:Debugging ? ,MedDispense - ? Field 11
	@JsonProperty(value="RMESSAGEPROCESSINGID")
	private String rMessageProcessingId=""; // s = "P"
	// HL7 Version in which the message is created Field 12
	@JsonProperty(value="RMESSAGEVERSIONID")
	private String rMessageVersionId=""; // s = "2.0"


	/* 										ZPM Message Start													*/
	//** Pocket Code L-Load , U-Unload, V-Dispense Field 1
	@JsonProperty(value="POCKETCODE")
	private String pocketCode="";
	//** Name of the machine that was linked to stock area while setting up the stock area Field 3
	@JsonProperty(value="TRANSACTIONSTATIONNAME")
	private String transactionStationName="";
	//** Unique code configured to each drug formulary under Dispensing setup tab Field 6
	@JsonProperty(value="DRUGDISPENSEDCODE")
	private String drugDispensedCode="";
	//** Name of the dispensed drug Field 7
	@JsonProperty(value="DRUGDISPENSEDNAME")
	private String drugDispensedName="";
	//** Class of the dispensed drug Field 8
	@JsonProperty(value="DRUGDISPENSEDCLASS")
	private String drugDispensedClass="";
	//** Count of drug expected to be there in cabinet after each dispensing Field 9
	@JsonProperty(value="EXPECTEDBEGINCOUNT")
	private String expectedBeginCount="0";
	//** Count of drug that was present in cabinet before each dispensing Field 10
	@JsonProperty(value="ACTUALBEGINCOUNT")
	private String actualBeginCount="0";
	//** Count of drug that was actually dispensed for that transaction Field 11
	@JsonProperty(value="TRANSACTIONAMOUNT")
	private String transactionAmount="0";
	//** Id of user who performed the dispense transaction (This id might be same or different from our EHR) Field 12
	@JsonProperty(value="TRANSACTIONUSERID")
	private String transactionUserId="";
	//** Name of user who performed the dispense transaction (This id might be same or different from our EHR) Field 13
	@JsonProperty(value="TRANSACTIONUSERNAME")
	private String transactionUserName="";
	//** Total count of drug that is present in cabinet after each dispense Field 16
	@JsonProperty(value="TOTALDRUGCOUNT")
	private String totalDrugCount="0";
	//**Facility Id were this drug dispense transaction happened Field 18
	@JsonProperty(value="FACILITYID")
	private String facilityId="";
	//** Pocket full count (ie Max) Field 22
	@JsonProperty(value="FULLCOUNT")
	private String fullCount="0";
	//** Pocket par count (ie Min) Field 23
	@JsonProperty(value="PARCOUNT")
	private String parCount="0";
	
	/* 										ZPM Message End														*/

	/* 										DFT Message Segment Only														*/
	// ** Count of patientId 1..n
	@JsonProperty(value="RSETID")
	private String rSetId = ""; // s = "001"
	// Intenal PatientId
	@JsonProperty(value="RPATIENTID")
	private String rPatientId = "";
	// *** MRN Number
	@JsonProperty(value="OPATIENTMRN")
	private String oPatientMRN;
	// Patient Account Number
	@JsonProperty(value="RPATIENTACCOUNTNO")
	private String rPatientAccountNo = "";
	//Patient First Name
	@JsonProperty(value="RPATIENTFNAME")
	private String rPatientFName = "";
	//Patient Last Name
	@JsonProperty(value="RPATIENTLNAME")
	private String rPatientLName = "";
	//Patient MiddleInitial 
	@JsonProperty(value="RPATIENTMIDDLEINITIAL")
	private String rPatientMiddleInitial = "";
	//Patient DOB Format shoulf be "YYYYMMDD"
	@JsonProperty(value="RPATIENTDOB")
    private String rPatientDOB = "";
    //Patient Sex F-Female M-Male O-Others U-Unkown
	@JsonProperty(value="RPATIENTSEX")
    private String rPatientSex = "";
	
	/* PV1 Segment*/
	// **I- Inpatinet O-Outpatient
	@JsonProperty(value="RPATIENTCLASS")
    private String rPatientClass = "";
	//Patient Location
	@JsonProperty(value="RPATIENTUNITLOCATION")
	private String rPatientUnitLocation = "";
	@JsonProperty(value="RPATIENTROOMNO")
	private String rPatientRoomNo = "";
	@JsonProperty(value="RPATIENTBEDNO")
	private String rPatientBedNo = "";
	
	//This value will be used for both place order and filler order number
	@JsonProperty(value="RORDERCONTROLCODE")
	private String rOrderControlCode = ""; 
	//This value will be used for both place order and filler order number
	@JsonProperty(value="RRXORDERNUMBER")
	private String rRxOrderNumber = "";
	@JsonProperty(value="HL7MESSAGE")
	private String hl7message = "";
	
	private boolean isOverrideOrder=false;
	
	
	public String getDispenseStockId() {
		return dispenseStockId;
	}
	public void setDispenseStockId(String dispenseStockId) {
		this.dispenseStockId = dispenseStockId;
	}
	public String getDispenseStockAreaName() {
		return dispenseStockAreaName;
	}
	public void setDispenseStockAreaName(String dispenseStockAreaName) {
		this.dispenseStockAreaName = dispenseStockAreaName;
	}
	public String getExistingQty() {
		return existingQty;
	}
	public void setExistingQty(String existingQty) {
		this.existingQty = existingQty;
	}
	public String getoSendApp() {
		return oSendApp;
	}
	public void setoSendApp(String oSendApp) {
		this.oSendApp = oSendApp;
	}
	public String getrSendFacName() {
		return rSendFacName;
	}
	public void setrSendFacName(String rSendFacName) {
		this.rSendFacName = rSendFacName;
	}
	public String getrRecApp() {
		return rRecApp;
	}
	public void setrRecApp(String rRecApp) {
		this.rRecApp = rRecApp;
	}
	public String getrRecFacName() {
		return rRecFacName;
	}
	public void setrRecFacName(String rRecFacName) {
		this.rRecFacName = rRecFacName;
	}
	public String getrDateTimeTransmission() {
		return rDateTimeTransmission;
	}
	public void setrDateTimeTransmission(String rDateTimeTransmission) {
		this.rDateTimeTransmission = rDateTimeTransmission;
	}
	public String getrMessageType() {
		return rMessageType;
	}
	public void setrMessageType(String rMessageType) {
		this.rMessageType = rMessageType;
	}
	public String getrMessageCode() {
		return rMessageCode;
	}
	public void setrMessageCode(String rMessageCode) {
		this.rMessageCode = rMessageCode;
	}
	
	public String getrMessageControlId() {
		return rMessageControlId;
	}
	public void setrMessageControlId(String rMessageControlId) {
		this.rMessageControlId = rMessageControlId;
	}
	public String getrMessageProcessingId() {
		return rMessageProcessingId;
	}
	public void setrMessageProcessingId(String rMessageProcessingId) {
		this.rMessageProcessingId = rMessageProcessingId;
	}
	public String getrMessageVersionId() {
		return rMessageVersionId;
	}
	public void setrMessageVersionId(String rMessageVersionId) {
		this.rMessageVersionId = rMessageVersionId;
	}
	public String getPocketCode() {
		return pocketCode;
	}
	public void setPocketCode(String pocketCode) {
		this.pocketCode = pocketCode;
	}
	public String getTransactionStationName() {
		return transactionStationName;
	}
	public void setTransactionStationName(String transactionStationName) {
		this.transactionStationName = transactionStationName;
	}
	public String getDrugDispensedCode() {
		return drugDispensedCode;
	}
	public void setDrugDispensedCode(String drugDispensedCode) {
		this.drugDispensedCode = drugDispensedCode;
	}
	public String getDrugDispensedName() {
		return drugDispensedName;
	}
	public void setDrugDispensedName(String drugDispensedName) {
		this.drugDispensedName = drugDispensedName;
	}
	public String getDrugDispensedClass() {
		return drugDispensedClass;
	}
	public void setDrugDispensedClass(String drugDispensedClass) {
		this.drugDispensedClass = drugDispensedClass;
	}
	public String getExpectedBeginCount() {
		return expectedBeginCount;
	}
	public void setExpectedBeginCount(String expectedBeginCount) {
		this.expectedBeginCount = expectedBeginCount;
	}
	public String getActualBeginCount() {
		return actualBeginCount;
	}
	public void setActualBeginCount(String actualBeginCount) {
		this.actualBeginCount = actualBeginCount;
	}
	public String getTransactionAmount() {
		return transactionAmount;
	}
	public void setTransactionAmount(String transactionAmount) {
		this.transactionAmount = transactionAmount;
	}
	public String getTransactionUserId() {
		return transactionUserId;
	}
	public void setTransactionUserId(String transactionUserId) {
		this.transactionUserId = transactionUserId;
	}
	public String getTransactionUserName() {
		return transactionUserName;
	}
	public void setTransactionUserName(String transactionUserName) {
		this.transactionUserName = transactionUserName;
	}
	public String getTotalDrugCount() {
		return totalDrugCount;
	}
	public void setTotalDrugCount(String totalDrugCount) {
		this.totalDrugCount = totalDrugCount;
	}
	
	public String getFacilityId() {
		return facilityId;
	}
	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}
	public String getFullCount() {
		return fullCount;
	}
	public void setFullCount(String fullCount) {
		this.fullCount = fullCount;
	}
	public String getParCount() {
		return parCount;
	}
	public void setParCount(String parCount) {
		this.parCount = parCount;
	}
	public String getrSetId() {
		return rSetId;
	}
	public void setrSetId(String rSetId) {
		this.rSetId = rSetId;
	}
	public String getrPatientId() {
		return rPatientId;
	}
	public void setrPatientId(String rPatientId) {
		this.rPatientId = rPatientId;
	}
	public String getoPatientMRN() {
		return oPatientMRN;
	}
	public void setoPatientMRN(String oPatientMRN) {
		this.oPatientMRN = oPatientMRN;
	}
	public String getrPatientAccountNo() {
		return rPatientAccountNo;
	}
	public void setrPatientAccountNo(String rPatientAccountNo) {
		this.rPatientAccountNo = rPatientAccountNo;
	}
	public String getrPatientFName() {
		return rPatientFName;
	}
	public void setrPatientFName(String rPatientFName) {
		this.rPatientFName = rPatientFName;
	}
	public String getrPatientLName() {
		return rPatientLName;
	}
	public void setrPatientLName(String rPatientLName) {
		this.rPatientLName = rPatientLName;
	}
	public String getrPatientMiddleInitial() {
		return rPatientMiddleInitial;
	}
	public void setrPatientMiddleInitial(String rPatientMiddleInitial) {
		this.rPatientMiddleInitial = rPatientMiddleInitial;
	}
	public String getrPatientDOB() {
		return rPatientDOB;
	}
	public void setrPatientDOB(String rPatientDOB) {
		this.rPatientDOB = rPatientDOB;
	}
	public String getrPatientSex() {
		return rPatientSex;
	}
	public void setrPatientSex(String rPatientSex) {
		this.rPatientSex = rPatientSex;
	}
	public String getrPatientClass() {
		return rPatientClass;
	}
	public void setrPatientClass(String rPatientClass) {
		this.rPatientClass = rPatientClass;
	}
	public String getrPatientUnitLocation() {
		return rPatientUnitLocation;
	}
	public void setrPatientUnitLocation(String rPatientUnitLocation) {
		this.rPatientUnitLocation = rPatientUnitLocation;
	}
	public String getrPatientRoomNo() {
		return rPatientRoomNo;
	}
	public void setrPatientRoomNo(String rPatientRoomNo) {
		this.rPatientRoomNo = rPatientRoomNo;
	}
	public String getrPatientBedNo() {
		return rPatientBedNo;
	}
	public void setrPatientBedNo(String rPatientBedNo) {
		this.rPatientBedNo = rPatientBedNo;
	}
	public String getrOrderControlCode() {
		return rOrderControlCode;
	}
	public void setrOrderControlCode(String rOrderControlCode) {
		this.rOrderControlCode = rOrderControlCode;
	}
	public String getrRxOrderNumber() {
		return rRxOrderNumber;
	}
	public void setrRxOrderNumber(String rRxOrderNumber) {
		this.rRxOrderNumber = rRxOrderNumber;
	}
	public String getHl7message() {
		return hl7message;
	}
	public void setHl7message(String hl7message) {
		this.hl7message = hl7message;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public String getMsgLogId() {
		return msgLogId;
	}
	public void setMsgLogId(String msgLogId) {
		this.msgLogId = msgLogId;
	}
	/*                                Inbound Message Properties End                                         */
	public boolean isOverrideOrder() {
		return isOverrideOrder;
	}
	public void setOverrideOrder(boolean isOverrideOrder) {
		this.isOverrideOrder = isOverrideOrder;
	}	
}
