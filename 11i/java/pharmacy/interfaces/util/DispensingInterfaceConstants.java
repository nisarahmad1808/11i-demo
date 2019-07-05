package inpatientWeb.pharmacy.interfaces.util;

/**
 * @author diveshg
 */
public class DispensingInterfaceConstants {
	
	public static final String SUCCESSMESSAGEPARSE = "Successfully parsed and saved the data to detail table";
	public enum MEDDISPENSINGINTERFACETABLES{
		
		TBL_IP_MEDDISPENSING_INTERFACES("ip_meddispensing_interfaces"),
		TBL_IP_MEDDISPENSING_INTERFACE_HL7SEGMENTDETAILS("ip_meddispensing_interface_hl7segmentdetails"),
		TBL_IP_MEDDISPENSING_INTERFACE_COMPONENTS("ip_meddispensing_interface_components"),    
		TBL_IP_MEDDISPENSING_INTERFACE_OUTBOUND_LOG("ip_meddispensing_interface_outbound_log"),
		TBL_IP_MEDDISPENSING_INTERFACE_ITEMKEYS("ip_meddispensing_interface_itemkeys"),
		TBL_IP_MEDDISPENSING_INTERFACE_DETAILS("ip_meddispensing_interface_details"),
		TBL_IP_MEDDISPENSING_INTERFACE_INBOUND_LOG("ip_meddispensing_interface_inbound_log"),
		TBL_IP_MEDDISPENSING_INTERFACE_ZPM_DETAIL("ip_meddispensing_interface_zpm_detail"),
		TBL_IP_MEDDISPENSING_INTERFACE_DFT_PROFILEORDER_DETAIL("ip_meddispensing_interface_dft_profileorder_detail"),
		TBL_IP_MEDDISPENSING_INTERFACE_DFT_OVERRIDEORDER_DETAIL("ip_meddispensing_interface_dft_overrideorder_detail");
	
		private String medDispensingTblName;
		
		MEDDISPENSINGINTERFACETABLES(String medDispensingTblName){
			this.medDispensingTblName = medDispensingTblName;
		}
		@Override
		public String toString(){
			return this.medDispensingTblName;
		}
	}
	
	public enum DISPENSINGSTOCKAREATABLES{		
		TBL_IP_DISPENSE_STOCKAREA("ip_dispense_stockarea"),
		TBL_IP_DISPENSE_STOCKAREA_LOC_MAPPING("ip_dispense_stockarea_loc_mapping"),
		TBL_IP_DISPENSE_STOCKAREA_EXTERNAL_MAPPING("ip_dispense_stockarea_external_mapping"),    
		TBL_IP_DISPENSE_STOCKAREA_FORMULARY_QTYMAPPING("ip_dispense_stockarea_formulary_qtymapping"),
		TBL_IP_DRUGFORMULARY("ip_drugformulary"),
		TBL_IP_ITEMS("ip_items");
		
		private String dispensingStockAreaTblName;
		
		DISPENSINGSTOCKAREATABLES(String dispensingStockAreaTblName){
			this.dispensingStockAreaTblName = dispensingStockAreaTblName;
		}
		@Override
		public String toString(){
			return this.dispensingStockAreaTblName;
		}
	}
	
	public enum HL7MessageAckStatusCodes{
		AA("Accept"),
		AE("Error"),
		AR("Reject");
		
		private String ackStatusCode;
		
		HL7MessageAckStatusCodes(String ackStatusCode) {
			this.ackStatusCode=ackStatusCode;
		}

		public String getAckStatusCode() {
			return ackStatusCode;
		}
	}
	
	public enum DispensingInterfaces{
		PYXIS("EnablePyxisInterface"),
		ASYENT("EnableAsyentInterface"),
		MEDDISPENSE("EnableMedDispenseInterface");
		
		private String dispensingInterface;
		
		DispensingInterfaces(String dispensingInterface) {
			this.dispensingInterface=dispensingInterface;
		}

		public String getDispensingInterface() {
			return dispensingInterface;
		}
	}
	
	public enum OrderTypeCodes {
		NEW("NW"),
		HOLD("HD"),
		CANCEL("CA"),
		COMPLETED("CM"),
		DISCONTINUE("DC");     

		private String orderTypeCode;

		OrderTypeCodes(String orderTypeCode) {
			this.orderTypeCode = orderTypeCode;
		}

		public String getOrderTypeCode() {
			return orderTypeCode;
		}
	}

	public enum MessageTypeCodes {
		
		FIRSTORDER("N"),
		SCHEDULED("O"),
		FIRSTORDERANDSCHEDULED("NO");

		private String msgTypeCode;

		MessageTypeCodes(String msgTypeCode) {
			this.msgTypeCode = msgTypeCode;
		}

		public String getMsgTypeCode() {
			return msgTypeCode;
		}

	}

	public enum HL7MessageComponents {
		//messagecomponents
		
		SINGLE("S"),
		MULTI("M"), 
		LINKEDORDER("LO"),
		LOAD("L"),
		UNLOAD("U"),
		DISPENSE("V");

		private String hl7MsgComponent;

		HL7MessageComponents(String hl7MsgComponent) {
			this.hl7MsgComponent = hl7MsgComponent;
		}

		public String getHL7MsgComponent() {
			return hl7MsgComponent;
		}
	}
	
	//ENUM for RXC's
	public enum DispenseDrugComponentType {
		BASE("B"),
		ADDITIVE("A");

		private String drugComponentType;

		DispenseDrugComponentType(String dispenseDrugComponentType) {
			this.drugComponentType = dispenseDrugComponentType;
		}

		public String getDispenseDrugComponentType() {
			return drugComponentType;
		}
	}
	
	//there will be always one primary drug - RXC segment (if one then it will be part of RXC {cz RXC also have drug info}, )
	public enum IsPrimaryDrug{
		YES("Y"),
		NO("N");

		private String isPrimary;

		IsPrimaryDrug(String isPrimaryDrug) {
			this.isPrimary = isPrimaryDrug;
		}

		public String getIsPrimaryDrug() {
			return isPrimary;
		}
	}
	
	public enum DispensingInterfaceKeyConstants{
		STATUS("Status"),
		MESSAGE("Message"),
		ACTIVE("active"),
		DISPENSESTOCKAREANAME("dispenseStockAreaName"),
		DISPENSESTOCKID("dispenseStockId"),
		HL7MESSAGE("hl7Message"),
		FAILED("failed"),
		SUCCESS("success"),
		PENDING("pending"),
		DISPENSERXDETAILS("dipsenseRXDetails"),
		RXFREQDETAILS("rxFreqDetail"),
		ISPRIMARY("isPrimary"),
		RECEIVEDDATETIME("receiveddatetime"),
		ERRORMESSAGE("errormessage"),
		ACKSTATUS("ackstatus"),
		ACKMESSAGE("ackmessage"),
		MSAPARSESTATUS("msaparsestatus"),
		MSHPARSESTATUS("mshparsestatus"),
		QUANTITY("quantity");
		
		private String keyConstants;

		public String getKeyConstants() {
			return keyConstants;
		}

		DispensingInterfaceKeyConstants(String keyConstants) {
			this.keyConstants = keyConstants;
		}
	}
	
	public enum DispensingInterfaceACKMessageConstants{
		ACKMSASTATUSCODEPOSITION(1),
		ACKMSAMESSAGECONTROLIDPOSITION(2),
		ACKMSHMESSAGETYPEPOSITION(9),
	    ACKMSHRECEIVEAPPPOSITION(5),
		ACKMSHINDEX(3),
		ACKRECEIVEDFAIL(0),
		ACKRECEIVEDSUCCESS(1);
		
		private int ackMsgConstants;

		public int getAckConstants() {
			return ackMsgConstants;
		}

		DispensingInterfaceACKMessageConstants(int ackConstants) {
			this.ackMsgConstants = ackConstants;
		}
	}
	
	public enum DispensingInterfaceStatus{
		NULLRECOMMENDEDFIELDS(DispensingInterfaceKeyConstants.FAILED.getKeyConstants(),"Required fields information are missing",0),
		INVALIDORDERTYPE(DispensingInterfaceKeyConstants.FAILED.getKeyConstants() ,"Invalid dispensing Order Type.Order types should be(eg: New,Discontinue,Cancel)",0),
		INVALIDMESSAGETYPE(DispensingInterfaceKeyConstants.FAILED.getKeyConstants() ,"Invalid dispensing Message Type.Message types should be(eg: First Dose,Scheduled).",0),
		INVALIDCOMPONENTTYPE(DispensingInterfaceKeyConstants.FAILED.getKeyConstants() ,"Invalid dispensing Component Type.Component types should be(eg: Single,Multi,IV).",0),
		INACTIVEINTERFACE(DispensingInterfaceKeyConstants.FAILED.getKeyConstants() ,"Medication dispensing interface is not setup in the practice.Please contact your administrator",0),
		INVALIDSEGMENTS(DispensingInterfaceKeyConstants.FAILED.getKeyConstants(), "Dispensing Interface segments are not configured.",0),
		INVALIDCOMPONENTS(DispensingInterfaceKeyConstants.FAILED.getKeyConstants(), "Dispensing Interface component type are not configured.",0),
		NOPRIMARYDRUGDETAIL(DispensingInterfaceKeyConstants.FAILED.getKeyConstants(), "Primary drug information is missing.",0),
		INVALIDACKSTATUSCODE(DispensingInterfaceKeyConstants.FAILED.getKeyConstants(), "Invalid HL7 Status Code",0),
		INVALIDACKDATE(DispensingInterfaceKeyConstants.FAILED.getKeyConstants(), "Unpraseable date",0),
		INVALIDACKOUTBOUNDMESSAGE(DispensingInterfaceKeyConstants.FAILED.getKeyConstants(),"Invalid response/acknowledgement received from Medication interface.",1),
		INTERFACECACHENOTSET(DispensingInterfaceKeyConstants.FAILED.getKeyConstants(),"Dispensing interface cache could not be initalized",1),
        INTERFACELOGFAILED(DispensingInterfaceKeyConstants.FAILED.getKeyConstants(),"Failed to Log Message Content into Dispensing Interface Log",1),
        INTERFACEDATANULL(DispensingInterfaceKeyConstants.FAILED.getKeyConstants(),"Interface data received is blank or null",1),
		DBERRORMESSAGE(DispensingInterfaceKeyConstants.FAILED.getKeyConstants(),"Failed to save Dispensing Interface Message",0);

		private final String status;
		private final String message;
		private final int reconcile;
		
		DispensingInterfaceStatus(String status, String message,int reconcile){

			this.status = status;
			this.message = message;
			this.reconcile=reconcile;
		}

		public String getStatus(){
			return status;
		}
		public String getMessage(){
			return message;
		}
		public int getReconcile(){
			return reconcile;
		}
	}
	
	public enum DispensingInterfaceInboundStatus{
        INVALIDDRUGDISPENSECODE(DispensingInterfaceKeyConstants.SUCCESS.getKeyConstants(),SUCCESSMESSAGEPARSE,DispensingInterfaceKeyConstants.PENDING.getKeyConstants(),"Invalid drug dispensed code",1),
		NOSTOCKAREAMAPPEDTOSTATIONNAME(DispensingInterfaceKeyConstants.SUCCESS.getKeyConstants(),SUCCESSMESSAGEPARSE,DispensingInterfaceKeyConstants.PENDING.getKeyConstants(),"No stock area details found mapped to received station name",1),
		NOFORMULARYIDMAPPEDTODRUGCODE(DispensingInterfaceKeyConstants.SUCCESS.getKeyConstants(),SUCCESSMESSAGEPARSE,DispensingInterfaceKeyConstants.PENDING.getKeyConstants(),"No Formulary details found mapped to received drug dispense code",1),
		INVALIDSTATIONNAME(DispensingInterfaceKeyConstants.SUCCESS.getKeyConstants(),SUCCESSMESSAGEPARSE,DispensingInterfaceKeyConstants.PENDING.getKeyConstants(),"Invalid station name",1),
		INVALIDDRUGDISPENSESTATIONNAME(DispensingInterfaceKeyConstants.SUCCESS.getKeyConstants(),SUCCESSMESSAGEPARSE,DispensingInterfaceKeyConstants.PENDING.getKeyConstants(),"Invalid station name and drug dispense code",1),
		QTYUPDATED(DispensingInterfaceKeyConstants.SUCCESS.getKeyConstants(),SUCCESSMESSAGEPARSE,DispensingInterfaceKeyConstants.SUCCESS.getKeyConstants(),"Quantity information updated successfully",0),
		FORMULARYMAPPEDTODRUGDIPSENSEDCODE(DispensingInterfaceKeyConstants.SUCCESS.getKeyConstants(),SUCCESSMESSAGEPARSE,DispensingInterfaceKeyConstants.SUCCESS.getKeyConstants(),"Formulary mapped to received drug dispense code",0),
		QTYUPDATEFAILED(DispensingInterfaceKeyConstants.SUCCESS.getKeyConstants(),SUCCESSMESSAGEPARSE,DispensingInterfaceKeyConstants.FAILED.getKeyConstants(),"Failed to update the quantity",1),
		INBOUNDDETAILLOGSUCCESS(DispensingInterfaceKeyConstants.SUCCESS.getKeyConstants(),SUCCESSMESSAGEPARSE,"","",0),
		INBOUNDUNSOLICITEDMESSAGE(DispensingInterfaceKeyConstants.SUCCESS.getKeyConstants(),SUCCESSMESSAGEPARSE,DispensingInterfaceKeyConstants.PENDING.getKeyConstants(),"Override",1),
		QTYUPDATEDWITHSTATUSINACTIVE(DispensingInterfaceKeyConstants.SUCCESS.getKeyConstants(),SUCCESSMESSAGEPARSE,DispensingInterfaceKeyConstants.SUCCESS.getKeyConstants(),"Quantity information updated and marked the drug dispense code status inactive in received station name",1),
		INVALIDPOCKETCODE(DispensingInterfaceKeyConstants.FAILED.getKeyConstants(),"Invalid pocket code","","",1),
		UNRECONGNIZABLEPOCKETCODE(DispensingInterfaceKeyConstants.FAILED.getKeyConstants(),"Unprocessable pocket code","","",1),
		INVALIDINBOUNDMESSAGETYPE(DispensingInterfaceKeyConstants.FAILED.getKeyConstants(),"Invalid message type","","",1),
		INBOUNDDETAILLOGFAILED(DispensingInterfaceKeyConstants.PENDING.getKeyConstants(),"Failed to log the details","","",0),
		INACTIVEINTERFACE(DispensingInterfaceKeyConstants.PENDING.getKeyConstants() ,"Medication dispensing interface is not setup in the practice.Please contact your administrator","","",0),
		INTERFACECACHENOTSET(DispensingInterfaceKeyConstants.PENDING.getKeyConstants(),"Dispensing interface cache could not be initalized","","",1);

		private  String logStatus;
		private  String logStatusMessage;
		private  String status;
		private  String statusMessage;
		private  int reconcile;
		
		DispensingInterfaceInboundStatus(String logStatus, String logStatusMessage,String status,String statusMessage,int reconcile){

			this.logStatus = logStatus;
			this.logStatusMessage = logStatusMessage;
			this.status = status;
			this.statusMessage = statusMessage;
			this.reconcile=reconcile;
		}

		public String getLogStatus(){
			return logStatus;
		}
		public String getLogStatusMessage(){
			return logStatusMessage;
		}
		public String getStatus(){
			return status;
		}
		public String getStatusMessage(){
			return statusMessage;
		}
		public int getReconcile(){
			return reconcile;
		}
	}
	
	public enum Hl7MessageEnclosingCodes{
		STARTOFMESSAGECHAR("013"),
		ENDOFMESSAGECHAR("034"),
		CARRIAGERETURNCHAR("015"),
		NEWLINECHAR("012");
		
		private String blockNotation;

		Hl7MessageEnclosingCodes(String blockNotation) {
			this.blockNotation = blockNotation;
		}
		@Override
		public String toString() {
			return blockNotation;
		}
	}

	public enum InboundMessageConstants{
		INBOUNDMSGSEGMENTDATASTARTPOSITION(9),
		INBOUNDMSGSEGMENTTEMPLATESTARTPOSITION(13),
		INBOUNDSEGMENTSSTARTPOSITION(4);
		private int constVal;

		InboundMessageConstants(int constVal) {
			this.constVal = constVal;
		}
		
		public int getConstVal() {
			return constVal;
		}
	}
	public enum DispensingMessageStatusCode{
		PENDING("pending");
		
		private String messageStatusCode;
		
		DispensingMessageStatusCode(String dispensingMessageStatusCode) {
			this.messageStatusCode=dispensingMessageStatusCode;
		}

		public String getDispensingMessageStatusCode() {
			return messageStatusCode;
		}
	}
}
