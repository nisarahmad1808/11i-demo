package inpatientWeb.pharmacy.interfaces.outbound.dto;

import java.util.List;

import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.HL7MessageComponents;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.MessageTypeCodes;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.OrderTypeCodes;

public class DispensingInterfaceOutBoundModel {

	public DispensingInterfaceOutBoundModel(){
		this.oSendApp = "eCW";
		this.rMessageType = "RDE";
		this.rMessageProcessingId = "P";
		this.rMessageVersionId = "2.0";
	}
	
	private MessageTypeCodes messageType;
	private HL7MessageComponents componentType;
	private OrderTypeCodes orderType;


	//	public 
	private String rLoggedInUser = "";
	private String substitutionStatus;
	private String facilityId;
	/* 													MSG SEGMENT 											*/
	
	// **  Application Name from were this message is generated
	private String oSendApp; // s = "eCW"
	// **  Facility Name from were this message is sent 
	private String rSendFacName = ""; 
	// ** Receiving Application Name were this message will be received
	private String rRecApp = ""; 
	// ** Receiving FacilityName Name were this message will be received
	private String rRecFacName = ""; 
	// Date time when message is created
	private String rDateTimeTransmission = "";
	// *** Standard HL7 code ie(RDE) for pharmacy dispensing order
	private String rMessageType = ""; // s = "RDE"
	// *** Standard HL7 code ie(O03 - First Dose Order, O01- Order Only(Decentralized) & Single Component, O11 - Multiple Component
	private String rMessageCode = ""; 
	// ** Unique id that will be generated during HL7 message creation
	private String rMessageControlId = "";
	// In case of Pyxis - P (Care Fusion) Aysent - P:Production D:Debugging ? ,MedDispense - ?
	private String rMessageProcessingId = ""; // s = "P"
	// HL7 Version in which the message is created
	private String rMessageVersionId = ""; // s = "2.0"

	/* 											PID segment 														*/
	// ** Count of patientId 1..n
	private String rSetId = ""; // s = "001"
	// Intenal PatientId
	private String rPatientId = "";
	// *** MRN Number
	private String oPatientMRN;
	// Patient Account Number
	private String rPatientAccountNo = "";
	//Patient First Name
	private String rPatientFName = "";
	//Patient Last Name
	private String rPatientLName = "";
	//Patient MiddleInitial 
	private String rPatientMiddleInitial = "";
	//Patient DOB Format shoulf be "YYYYMMDD"
    private String rPatientDOB = "";
    //Patient Sex F-Female M-Male O-Others U-Unkown
    private String rPatientSex = "";
    //Patient Master Account Number
    private String masterAccNo = "";
	/* 														PV1 Segment 											*/
	// ** I- Inpatinet O-Outpatient 
	private String rPatientClass = "";
	//Patient Location
	private String rPatientUnitLocation = "";
	private String rPatientRoomNo = "";
	private String rPatientBedNo = "";
	
	private String oAdmissionType;
	// ***
	private String oPatientPreAdmitNum;
	// ** Attending Doctor DEA Number
	private String rAttendingDocDEA = "";
	// ** Attending Doctor First Name
	private String rAttendingDocFName = "";
	// ** Attending Doctor Last Name
	private String rAttendingDocLName = "";
	// ** Attending Doctor Middle Initial Name
	private String rAttendingDocMiddleInitial = "";
	// ** Attending Doctor Suffix 
	private String oAttendingDocSuffix;
	// ** Attending Doctor Prefix 
	private String oAttendingDocPrefix;
	// ** Attending Doctor Degree 
	private String oAttendingDocDegree;

	// ** MED- Medical Service , SU 
	private String rHospitalServiceType = "";
	// ** Patient Encounter ID Number
	private String rPatientVisitNumber = "";
	// ***Patient Sub Encounter Number
	private String rPatientAlternateVisitNumber = "";
	// ** Admitting Doctor DEA Number
	private String oAdmittingDocDEA;
	// ** Admitting Doctor First Name
	private String oAdmittingDocFName;
	// ** Admitting Doctor Last Name
	private String oAdmittingDocLName;
	// ** Admitting Doctor Middle Initial Name
	private String oAdmittingDocMiddleInitial;
	// ** Admitting Doctor Suffix Number
	private String oAdmittingDocSuffix;
	// ** Admitting Doctor Prefix Number
	private String oAdmittingDocPrefix;
	// ** Admitting Doctor Degree Number
	private String oAdmittingDocDegree;
	
	// **Date Time when patient is admitted YYYYMMDD
	private String rPatientAdmissionTime = "";
	
	/*  					ORC Segment & RXE Segment & ZRX Segment												*/
	
	// Value will be NEW, DISCONTINUE,HOLD,UPDATE,CANCEL
	
	private String rOrderControlCode = ""; 
	
	
	//This value will be used for both place order and filler order number
	private String rRxOrderNumber = ""; 
    //Date Time when the transaction was verified  
	private String rDateTimeofTransaction = "";
	// ** Name of a Person who keyed the request into the application
	private String oEnteredBy;
	//** Name of a Person who verified the request
	private String oVerifiedBy;
	// ** Ordering Provider ID 
	private String rOrderingProviderID = "";
	// ** Ordering Provider ID Number
	private String rOrderingProviderDEA = "";
	// ** Ordering Provider First Name
	private String rOrderingProviderFName = "";
	// ** Ordering Provider Last Name
	private String rOrderingProviderLName = "";
	// ** Ordering Provider Middle Initial Number
	private String rOrderingProviderMiddleInitial = "";
	// ** Ordering Provider Suffix Number
	private String oOrderingProviderSuffix;
	// ** Ordering Provider Prefix Number
	private String oOrderingProviderPrefix;
	// **
	private String 	rIncludeNow = "";
	// **
	private String 	rDispenseOrderInstructions="";
	// **
	private String 	rDispenseOrderDateTime="";
	// **
	private String 	rDispenseOrderStartDateTime="";
	// **
	private String 	rDispenseOrderEndDateTime="";
	// **
	private String 	rDispenseOrderDiscontinueDateTime="";	
	// **
	private String 	oDispenseOrderExpireDateTime="";
	// **
	private String  oDispenseOrderExpiryReminderHrs="";
	// **
	private String  oDispenseOrderExpiresInMinutes="";
	// **
	private String 	oDispenseOrderPriorityType="";
	// **
	private List<DispensingRxFreqDetailsModel> rxFreqDetail;
	// **
	private String  rDispenseOrderGiveDoseAmountMin = "";
	// ** 
	private String  oDispenseOrderGiveDoseAmountMax = "";
	// **
	private String  rDispenseOrderGiveDoseUnit = "";
	// **
	private String  rDispenseOrderDuration = "";
	// **
	private String  rDispenseOrderDurationType = "";
	// **
	private String 	rDispenseOrderIVRate = "";
	// **
	private String  rDispenseOrderIVRateUom = "";
	// ** 1-Med,2-IV,3-Complex
	private String  rDispenseOrderType = "";
	// **
	private String  rDispenseRPHInstructions = "";
	// **
	private String  rDispenseOrderEntryInstructions="";
	// **
	private String  rDispenseEMarEntryInstructions="";
	// ** Phramacist  ID Number
	private String oPhramacistID;
	// ** Phramacist First Name
	private String oPhramacistFName;
	// ** Phramacist Last Name
	private String oPhramacistLName;
	// ** Phramacist  Middle Initial Number
	private String oPhramacistMiddleInitial;
	
	/* RXR segment */
	private String 	rDispenseOrderRouteCode = "";
	private String  rDispenseOrderRouteDesc = "";
	
	/* RXC Segment */
	private List<DispensingRxDetailsModel> dipsenseRXDetails;
	
	/*ZRX Segment */
	// This value will be overwritten by dispensing interface based on provided MessageTypeCodes property value
	private String rDispsensingMessageTypeControlCode = "";
	
	public MessageTypeCodes getMessageType() {
		return messageType;
	}
	public void setMessageType(MessageTypeCodes messageType) {
		this.messageType = messageType;
	}
	public HL7MessageComponents getComponentType() {
		return componentType;
	}
	public void setComponentType(HL7MessageComponents componentType) {
		this.componentType = componentType;
	}
	public String getrLoggedInUser() {
		return rLoggedInUser;
	}
	public void setrLoggedInUser(String rLoggedInUser) {
		this.rLoggedInUser = rLoggedInUser;
	}
	public String getoSendApp() {
		return oSendApp;
	}
	public String getrSendFacName() {
		return rSendFacName;
	}
	public void setrSendFacName(String rSendFacName) {
		this.rSendFacName = rSendFacName;
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
	public String getrMessageVersionId() {
		return rMessageVersionId;
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
	public String getrRecApp() {
		return rRecApp;
	}
	public void setrRecApp(String rRecApp) {
		this.rRecApp = rRecApp;
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
	public String getoAdmissionType() {
		return oAdmissionType;
	}
	public void setoAdmissionType(String oAdmissionType) {
		this.oAdmissionType = oAdmissionType;
	}
	public String getoPatientPreAdmitNum() {
		return oPatientPreAdmitNum;
	}
	public void setoPatientPreAdmitNum(String oPatientPreAdmitNum) {
		this.oPatientPreAdmitNum = oPatientPreAdmitNum;
	}
	public String getrAttendingDocDEA() {
		return rAttendingDocDEA;
	}
	public void setrAttendingDocDEA(String rAttendingDocDEA) {
		this.rAttendingDocDEA = rAttendingDocDEA;
	}
	public String getrAttendingDocFName() {
		return rAttendingDocFName;
	}
	public void setrAttendingDocFName(String rAttendingDocFName) {
		this.rAttendingDocFName = rAttendingDocFName;
	}
	public String getrAttendingDocLName() {
		return rAttendingDocLName;
	}
	public void setrAttendingDocLName(String rAttendingDocLName) {
		this.rAttendingDocLName = rAttendingDocLName;
	}
	public String getrAttendingDocMiddleInitial() {
		return rAttendingDocMiddleInitial;
	}
	public void setrAttendingDocMiddleInitial(String rAttendingDocMiddleInitial) {
		this.rAttendingDocMiddleInitial = rAttendingDocMiddleInitial;
	}
	public String getoAttendingDocSuffix() {
		return oAttendingDocSuffix;
	}
	public void setoAttendingDocSuffix(String oAttendingDocSuffix) {
		this.oAttendingDocSuffix = oAttendingDocSuffix;
	}
	public String getoAttendingDocPrefix() {
		return oAttendingDocPrefix;
	}
	public void setoAttendingDocPrefix(String oAttendingDocPrefix) {
		this.oAttendingDocPrefix = oAttendingDocPrefix;
	}
	public String getoAttendingDocDegree() {
		return oAttendingDocDegree;
	}
	public void setoAttendingDocDegree(String oAttendingDocDegree) {
		this.oAttendingDocDegree = oAttendingDocDegree;
	}
	public String getrHospitalServiceType() {
		return rHospitalServiceType;
	}
	public void setrHospitalServiceType(String rHospitalServiceType) {
		this.rHospitalServiceType = rHospitalServiceType;
	}
	public String getrPatientVisitNumber() {
		return rPatientVisitNumber;
	}
	public void setrPatientVisitNumber(String rPatientVisitNumber) {
		this.rPatientVisitNumber = rPatientVisitNumber;
	}
	public String getrPatientAlternateVisitNumber() {
		return rPatientAlternateVisitNumber;
	}
	public void setrPatientAlternateVisitNumber(String rPatientAlternateVisitNumber) {
		this.rPatientAlternateVisitNumber = rPatientAlternateVisitNumber;
	}
	public String getoAdmittingDocDEA() {
		return oAdmittingDocDEA;
	}
	public void setoAdmittingDocDEA(String oAdmittingDocDEA) {
		this.oAdmittingDocDEA = oAdmittingDocDEA;
	}
	public String getoAdmittingDocFName() {
		return oAdmittingDocFName;
	}
	public void setoAdmittingDocFName(String oAdmittingDocFName) {
		this.oAdmittingDocFName = oAdmittingDocFName;
	}
	public String getoAdmittingDocLName() {
		return oAdmittingDocLName;
	}
	public void setoAdmittingDocLName(String oAdmittingDocLName) {
		this.oAdmittingDocLName = oAdmittingDocLName;
	}
	public String getoAdmittingDocMiddleInitial() {
		return oAdmittingDocMiddleInitial;
	}
	public void setoAdmittingDocMiddleInitial(String oAdmittingDocMiddleInitial) {
		this.oAdmittingDocMiddleInitial = oAdmittingDocMiddleInitial;
	}
	public String getoAdmittingDocSuffix() {
		return oAdmittingDocSuffix;
	}
	public void setoAdmittingDocSuffix(String oAdmittingDocSuffix) {
		this.oAdmittingDocSuffix = oAdmittingDocSuffix;
	}
	public String getoAdmittingDocPrefix() {
		return oAdmittingDocPrefix;
	}
	public void setoAdmittingDocPrefix(String oAdmittingDocPrefix) {
		this.oAdmittingDocPrefix = oAdmittingDocPrefix;
	}
	public String getoAdmittingDocDegree() {
		return oAdmittingDocDegree;
	}
	public void setoAdmittingDocDegree(String oAdmittingDocDegree) {
		this.oAdmittingDocDegree = oAdmittingDocDegree;
	}
	public String getrPatientAdmissionTime() {
		return rPatientAdmissionTime;
	}
	public void setrPatientAdmissionTime(String rPatientAdmissionTime) {
		this.rPatientAdmissionTime = rPatientAdmissionTime;
	}
	
	public String getrRxOrderNumber() {
		return rRxOrderNumber;
	}
	public void setrRxOrderNumber(String rRxOrderNumber) {
		this.rRxOrderNumber = rRxOrderNumber;
	}
	public String getrDateTimeofTransaction() {
		return rDateTimeofTransaction;
	}
	public void setrDateTimeofTransaction(String rDateTimeofTransaction) {
		this.rDateTimeofTransaction = rDateTimeofTransaction;
	}
	public String getoEnteredBy() {
		return oEnteredBy;
	}
	public void setoEnteredBy(String oEnteredBy) {
		this.oEnteredBy = oEnteredBy;
	}
	public String getoVerifiedBy() {
		return oVerifiedBy;
	}
	public void setoVerifiedBy(String oVerifiedBy) {
		this.oVerifiedBy = oVerifiedBy;
	}
	public String getrOrderingProviderFName() {
		return rOrderingProviderFName;
	}
	public void setrOrderingProviderFName(String rOrderingProviderFName) {
		this.rOrderingProviderFName = rOrderingProviderFName;
	}
	public String getrOrderingProviderLName() {
		return rOrderingProviderLName;
	}
	public void setrOrderingProviderLName(String rOrderingProviderLName) {
		this.rOrderingProviderLName = rOrderingProviderLName;
	}
	public String getrOrderingProviderMiddleInitial() {
		return rOrderingProviderMiddleInitial;
	}
	public void setrOrderingProviderMiddleInitial(
			String rOrderingProviderMiddleInitial) {
		this.rOrderingProviderMiddleInitial = rOrderingProviderMiddleInitial;
	}
	public String getoOrderingProviderSuffix() {
		return oOrderingProviderSuffix;
	}
	public void setoOrderingProviderSuffix(String oOrderingProviderSuffix) {
		this.oOrderingProviderSuffix = oOrderingProviderSuffix;
	}
	public String getoOrderingProviderPrefix() {
		return oOrderingProviderPrefix;
	}
	public void setoOrderingProviderPrefix(String oOrderingProviderPrefix) {
		this.oOrderingProviderPrefix = oOrderingProviderPrefix;
	}
	public String getrIncludeNow() {
		return rIncludeNow;
	}
	public void setrIncludeNow(String rIncludeNow) {
		this.rIncludeNow = rIncludeNow;
	}
	public String getrDispenseOrderInstructions() {
		return rDispenseOrderInstructions;
	}
	public void setrDispenseOrderInstructions(String rDispenseOrderInstructions) {
		this.rDispenseOrderInstructions = rDispenseOrderInstructions;
	}

	public String getrDispenseOrderEndDateTime() {
		return rDispenseOrderEndDateTime;
	}
	public void setrDispenseOrderEndDateTime(String rDispenseOrderEndDateTime) {
		this.rDispenseOrderEndDateTime = rDispenseOrderEndDateTime;
	}
	public String getrDispenseOrderDiscontinueDateTime() {
		return rDispenseOrderDiscontinueDateTime;
	}
	public void setrDispenseOrderDiscontinueDateTime(
			String rDispenseOrderDiscontinueDateTime) {
		this.rDispenseOrderDiscontinueDateTime = rDispenseOrderDiscontinueDateTime;
	}
	public String getoDispenseOrderExpireDateTime() {
		return oDispenseOrderExpireDateTime;
	}
	public void setoDispenseOrderExpireDateTime(String oDispenseOrderExpireDateTime) {
		this.oDispenseOrderExpireDateTime = oDispenseOrderExpireDateTime;
	}
	public String getoDispenseOrderExpiryReminderHrs() {
		return oDispenseOrderExpiryReminderHrs;
	}
	public void setoDispenseOrderExpiryReminderHrs(
			String oDispenseOrderExpiryReminderHrs) {
		this.oDispenseOrderExpiryReminderHrs = oDispenseOrderExpiryReminderHrs;
	}
	public String getoDispenseOrderExpiresInMinutes() {
		return oDispenseOrderExpiresInMinutes;
	}
	public void setoDispenseOrderExpiresInMinutes(
			String oDispenseOrderExpiresInMinutes) {
		this.oDispenseOrderExpiresInMinutes = oDispenseOrderExpiresInMinutes;
	}
	
	
	public String getrDispenseOrderGiveDoseAmountMin() {
		return rDispenseOrderGiveDoseAmountMin;
	}
	public void setrDispenseOrderGiveDoseAmountMin(
			String rDispenseOrderGiveDoseAmountMin) {
		this.rDispenseOrderGiveDoseAmountMin = rDispenseOrderGiveDoseAmountMin;
	}
	public String getoDispenseOrderGiveDoseAmountMax() {
		return oDispenseOrderGiveDoseAmountMax;
	}
	public void setoDispenseOrderGiveDoseAmountMax(
			String oDispenseOrderGiveDoseAmountMax) {
		this.oDispenseOrderGiveDoseAmountMax = oDispenseOrderGiveDoseAmountMax;
	}
	public String getrDispenseOrderGiveDoseUnit() {
		return rDispenseOrderGiveDoseUnit;
	}
	public void setrDispenseOrderGiveDoseUnit(String rDispenseOrderGiveDoseUnit) {
		this.rDispenseOrderGiveDoseUnit = rDispenseOrderGiveDoseUnit;
	}
	public String getrDispenseOrderDuration() {
		return rDispenseOrderDuration;
	}
	public void setrDispenseOrderDuration(String rDispenseOrderDuration) {
		this.rDispenseOrderDuration = rDispenseOrderDuration;
	}
	public String getrDispenseOrderDurationType() {
		return rDispenseOrderDurationType;
	}
	public void setrDispenseOrderDurationType(String rDispenseOrderDurationType) {
		this.rDispenseOrderDurationType = rDispenseOrderDurationType;
	}
	public String getrDispenseOrderIVRate() {
		return rDispenseOrderIVRate;
	}
	public void setrDispenseOrderIVRate(String rDispenseOrderIVRate) {
		this.rDispenseOrderIVRate = rDispenseOrderIVRate;
	}
	public String getrDispenseOrderIVRateUom() {
		return rDispenseOrderIVRateUom;
	}
	public void setrDispenseOrderIVRateUom(String rDispenseOrderIVRateUom) {
		this.rDispenseOrderIVRateUom = rDispenseOrderIVRateUom;
	}
	public String getrDispenseOrderType() {
		return rDispenseOrderType;
	}
	public void setrDispenseOrderType(String rDispenseOrderType) {
		this.rDispenseOrderType = rDispenseOrderType;
	}
	public String getrDispenseRPHInstructions() {
		return rDispenseRPHInstructions;
	}
	public void setrDispenseRPHInstructions(String rDispenseRPHInstructions) {
		this.rDispenseRPHInstructions = rDispenseRPHInstructions;
	}
	public String getrDispenseOrderEntryInstructions() {
		return rDispenseOrderEntryInstructions;
	}
	public void setrDispenseOrderEntryInstructions(
			String rDispenseOrderEntryInstructions) {
		this.rDispenseOrderEntryInstructions = rDispenseOrderEntryInstructions;
	}
	public String getrDispenseEMarEntryInstructions() {
		return rDispenseEMarEntryInstructions;
	}
	public void setrDispenseEMarEntryInstructions(
			String rDispenseEMarEntryInstructions) {
		this.rDispenseEMarEntryInstructions = rDispenseEMarEntryInstructions;
	}
	public String getoPhramacistID() {
		return oPhramacistID;
	}
	public void setoPhramacistID(String oPhramacistID) {
		this.oPhramacistID = oPhramacistID;
	}
	public String getoPhramacistFName() {
		return oPhramacistFName;
	}
	public void setoPhramacistFName(String oPhramacistFName) {
		this.oPhramacistFName = oPhramacistFName;
	}
	public String getoPhramacistLName() {
		return oPhramacistLName;
	}
	public void setoPhramacistLName(String oPhramacistLName) {
		this.oPhramacistLName = oPhramacistLName;
	}
	public String getoPhramacistMiddleInitial() {
		return oPhramacistMiddleInitial;
	}
	public void setoPhramacistMiddleInitial(String oPhramacistMiddleInitial) {
		this.oPhramacistMiddleInitial = oPhramacistMiddleInitial;
	}
	public String getrDispenseOrderRouteCode() {
		return rDispenseOrderRouteCode;
	}
	public void setrDispenseOrderRouteCode(String rDispenseOrderRouteCode) {
		this.rDispenseOrderRouteCode = rDispenseOrderRouteCode;
	}
	
	public List<DispensingRxDetailsModel> getDipsenseRXDetails() {
		return dipsenseRXDetails;
	}
	public void setDipsenseRXDetails(
			List<DispensingRxDetailsModel> dipsenseRXDetails) {
		this.dipsenseRXDetails = dipsenseRXDetails;
	}
	/**
	 * @return the rxFreqDetail
	 */
	public List<DispensingRxFreqDetailsModel> getRxFreqDetail() {
		return rxFreqDetail;
	}
	/**
	 * @param rxFreqDetail the rxFreqDetail to set
	 */
	public void setRxFreqDetail(List<DispensingRxFreqDetailsModel> rxFreqDetail) {
		this.rxFreqDetail = rxFreqDetail;
	}
	/**
	 * @return the rDispenseOrderDateTime
	 */
	public String getrDispenseOrderDateTime() {
		return rDispenseOrderDateTime;
	}
	/**
	 * @param rDispenseOrderDateTime the rDispenseOrderDateTime to set
	 */
	public void setrDispenseOrderDateTime(String rDispenseOrderDateTime) {
		this.rDispenseOrderDateTime = rDispenseOrderDateTime;
	}
	/**
	 * @return the rDispenseOrderStartDateTime
	 */
	public String getrDispenseOrderStartDateTime() {
		return rDispenseOrderStartDateTime;
	}
	/**
	 * @param rDispenseOrderStartDateTime the rDispenseOrderStartDateTime to set
	 */
	public void setrDispenseOrderStartDateTime(
			String rDispenseOrderStartDateTime) {
		this.rDispenseOrderStartDateTime = rDispenseOrderStartDateTime;
	}
	/**
	 * @return the oDispenseOrderPriorityType
	 */
	public String getoDispenseOrderPriorityType() {
		return oDispenseOrderPriorityType;
	}
	/**
	 * @param oDispenseOrderPriorityType the oDispenseOrderPriorityType to set
	 */
	public void setoDispenseOrderPriorityType(String oDispenseOrderPriorityType) {
		this.oDispenseOrderPriorityType = oDispenseOrderPriorityType;
	}
	/**
	 * @return the rDispenseOrderRouteDesc
	 */
	public String getrDispenseOrderRouteDesc() {
		return rDispenseOrderRouteDesc;
	}
	/**
	 * @param rDispenseOrderRouteDesc the rDispenseOrderRouteDesc to set
	 */
	public void setrDispenseOrderRouteDesc(String rDispenseOrderRouteDesc) {
		this.rDispenseOrderRouteDesc = rDispenseOrderRouteDesc;
	}
	/**
	 * @return the rOrderControlCode
	 */
	public String getrOrderControlCode() {
		return rOrderControlCode;
	}
	/**
	 * @param rOrderControlCode the rOrderControlCode to set
	 */
	public void setrOrderControlCode(String rOrderControlCode) {
		this.rOrderControlCode = rOrderControlCode;
	}
	
	public OrderTypeCodes getOrderType() {
		return orderType;
	}
	public void setOrderType(OrderTypeCodes orderType) {
		this.orderType = orderType;
	}
	public String getSubstitutionStatus() {
		return substitutionStatus;
	}
	public void setSubstitutionStatus(String substitutionStatus) {
		this.substitutionStatus = substitutionStatus;
	}
	public String getrOrderingProviderID() {
		return rOrderingProviderID;
	}
	public void setrOrderingProviderID(String rOrderingProviderID) {
		this.rOrderingProviderID = rOrderingProviderID;
	}
	public String getrOrderingProviderDEA() {
		return rOrderingProviderDEA;
	}
	public void setrOrderingProviderDEA(String rOrderingProviderDEA) {
		this.rOrderingProviderDEA = rOrderingProviderDEA;
	}
	public String getrDispsensingMessageTypeControlCode() {
		return rDispsensingMessageTypeControlCode;
	}
	public void setrDispsensingMessageTypeControlCode(String rDispsensingMessageTypeControlCode) {
		this.rDispsensingMessageTypeControlCode = rDispsensingMessageTypeControlCode;
	}
	public void setoSendApp(String oSendApp) {
		this.oSendApp = oSendApp;
	}
	public void setrMessageType(String rMessageType) {
		this.rMessageType = rMessageType;
	}
	public void setrMessageProcessingId(String rMessageProcessingId) {
		this.rMessageProcessingId = rMessageProcessingId;
	}
	public void setrMessageVersionId(String rMessageVersionId) {
		this.rMessageVersionId = rMessageVersionId;
	}
	public String getFacilityId() {
		return facilityId;
	}
	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}
	public String getMasterAccNo() {
		return masterAccNo;
	}
	public void setMasterAccNo(String masterAccNo) {
		this.masterAccNo = masterAccNo;
	}
}