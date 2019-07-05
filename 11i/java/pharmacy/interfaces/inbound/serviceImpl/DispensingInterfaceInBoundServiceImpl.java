package inpatientWeb.pharmacy.interfaces.inbound.serviceImpl;

import inpatientWeb.Auth.exception.UnprocessableEntityException;
import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;
import inpatientWeb.pharmacy.interfaces.inbound.dao.DispensingInterfaceInBoundDAO;
import inpatientWeb.pharmacy.interfaces.inbound.dto.DispensingInterfaceInboundModel;
import inpatientWeb.pharmacy.interfaces.inbound.service.DispensingInterfaceInBoundService;
import inpatientWeb.pharmacy.interfaces.service.DispensingInterfaceCacheService;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.DispensingInterfaceInboundStatus;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.DispensingInterfaceKeyConstants;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.DispensingMessageStatusCode;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.HL7MessageComponents;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.Hl7MessageEnclosingCodes;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.InboundMessageConstants;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceCustomException;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceUtil;
import inpatientWeb.utils.DateUtil;
import inpatientWeb.utils.validator.ValidatorUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
/**
@author arun.reddy vijay.yadav
*/
@Service
public class DispensingInterfaceInBoundServiceImpl implements DispensingInterfaceInBoundService{

	@Autowired
	private DispensingInterfaceInBoundDAO  dispensingInterfaceInBoundDAO;
	
	private DispensingInterfaceUtil dispensingInterfaceUtil=new DispensingInterfaceUtil();
	private String inboundOverride="override";
	private char eOfChar;
	private char crChar;

	@Autowired
	private DispensingInterfaceCacheService dispensingInterfaceCacheService;
	/**
	 * API to gets the details of Medication Dispensing Interface enabled in practice
	 * @return boolean - True Or False on Success or Failure
	 * @throws DispensingInterfaceCustomException
	 */
	private boolean getInterfaceDetails() throws DispensingInterfaceCustomException{
		boolean bFlag=false;
		if(null == dispensingInterfaceCacheService.getDispensingInterfaceModel()){
			throw new DispensingInterfaceCustomException(DispensingInterfaceInboundStatus.INACTIVEINTERFACE);
		}else{
			bFlag=true;
		}
		return bFlag;
	}
	/**
	 * API to process received HL7 message sent by the dispensing interface
  	 * @param Map<String,Object> requestMap - HL7 message and Inbound Message log id
  	 * @return boolean - True or False
  	 */
	public boolean processInboundMessage(Map<String,Object> requestMap) {
		Map<String,Object> updateInboundLogStatusMap= new HashMap<>();
		String strHL7Message="";
		String msgLogId="0";
		boolean bFlag=false;
		try{
			updateInboundLogStatusMap.put(DispensingInterfaceKeyConstants.STATUS.getKeyConstants(), DispensingInterfaceKeyConstants.FAILED.getKeyConstants());
			updateInboundLogStatusMap.put(DispensingInterfaceKeyConstants.MESSAGE.getKeyConstants(),  DispensingInterfaceInboundStatus.INBOUNDDETAILLOGFAILED.getLogStatusMessage());
			 msgLogId	= null==requestMap.get("id")?msgLogId:requestMap.get("id").toString();
			 updateInboundLogStatusMap.put("logId", msgLogId);
			 if(!setConstantSpecialChars()) {
				 updateInboundLogStatusMap.put(DispensingInterfaceKeyConstants.STATUS.getKeyConstants(), DispensingInterfaceKeyConstants.PENDING.getKeyConstants());
				 updateInboundLogStatusMap.put(DispensingInterfaceKeyConstants.MESSAGE.getKeyConstants(), "Failed to get message seperator");
			 }  
			 if(!dispensingInterfaceCacheService.setDispensingInterfaceCache()){
		    	   EcwLog.AppendToLog("[DispensingInterfaceInBoundServiceImpl] processInboundMessage - [Reason - Dispensing interface cache could not be initalized]: "+ new java.util.Date());
				   throw new DispensingInterfaceCustomException(DispensingInterfaceInboundStatus.INTERFACECACHENOTSET);
		     }
		     getInterfaceDetails();
	         strHL7Message = null==requestMap.get(DispensingInterfaceKeyConstants.HL7MESSAGE.getKeyConstants())?strHL7Message:requestMap.get(DispensingInterfaceKeyConstants.HL7MESSAGE.getKeyConstants()).toString();
			 if(null!=strHL7Message && !"".equals(strHL7Message)) {
				 bFlag=processDispensingHL7InboundMessage(strHL7Message,msgLogId,updateInboundLogStatusMap);
				 if(bFlag) {
					 updateInboundLogStatusMap.put(DispensingInterfaceKeyConstants.STATUS.getKeyConstants(),  DispensingInterfaceKeyConstants.SUCCESS.getKeyConstants());
					 updateInboundLogStatusMap.put(DispensingInterfaceKeyConstants.MESSAGE.getKeyConstants(), DispensingInterfaceInboundStatus.INBOUNDDETAILLOGSUCCESS.getLogStatusMessage());
				 } 
			 }
		}catch(DispensingInterfaceCustomException customEx) {
			updateInboundLogStatusMap.put(DispensingInterfaceKeyConstants.STATUS.getKeyConstants(), customEx.getLogStatus());
			updateInboundLogStatusMap.put(DispensingInterfaceKeyConstants.MESSAGE.getKeyConstants(), customEx.getLogStatusMessage());
			EcwLog.AppendToLog(customEx.getStatusMessage());
		}catch(ArrayStoreException | ClassCastException | UnsupportedOperationException | JsonSyntaxException | JsonIOException | IllegalArgumentException |IndexOutOfBoundsException | UnprocessableEntityException | DataAccessException ex){
			updateInboundLogStatusMap.put(DispensingInterfaceKeyConstants.STATUS.getKeyConstants(), DispensingInterfaceKeyConstants.FAILED.getKeyConstants());
			updateInboundLogStatusMap.put(DispensingInterfaceKeyConstants.MESSAGE.getKeyConstants(), ex.getMessage());
			EcwLog.AppendExceptionToLog(ex);
		}catch(RuntimeException runEx){
			updateInboundLogStatusMap.put(DispensingInterfaceKeyConstants.STATUS.getKeyConstants(), DispensingInterfaceKeyConstants.FAILED.getKeyConstants());
			updateInboundLogStatusMap.put(DispensingInterfaceKeyConstants.MESSAGE.getKeyConstants(), runEx.getMessage());
			EcwLog.AppendExceptionToLog(runEx);
		}
		updateInboundLogStatusMap.put("updatedOn", DateUtil.getTodaysDate("yyyy-MM-dd HH:mm:ss"));
		dispensingInterfaceInBoundDAO.updateInboundMessage(updateInboundLogStatusMap);
		return bFlag;
	}
	/** 
	 * API to convert decimal value to special characters and set it to respective references
	 * @return boolean - True or False
	 */
	private boolean setConstantSpecialChars() {
		boolean bFlag = false;
		try {
			int endOfMessageCharDecimalValue = null!=dispensingInterfaceCacheService.getDispensingInterfaceItemKeyValue("End_Of_Message_Char")?dispensingInterfaceUtil.getOctToDecimalValue(dispensingInterfaceCacheService.getDispensingInterfaceItemKeyValue("End_Of_Message_Char").trim(), Hl7MessageEnclosingCodes.ENDOFMESSAGECHAR.toString().trim()):dispensingInterfaceUtil.getOctToDecimalValue("", Hl7MessageEnclosingCodes.ENDOFMESSAGECHAR.toString().trim());
			eOfChar= (char)endOfMessageCharDecimalValue;
			
			int segmentSeperatorCharDecimalValue = null!=dispensingInterfaceCacheService.getDispensingInterfaceItemKeyValue("Segment_Seperator_Char")?dispensingInterfaceUtil.getOctToDecimalValue(dispensingInterfaceCacheService.getDispensingInterfaceItemKeyValue("Segment_Seperator_Char").trim(), Hl7MessageEnclosingCodes.CARRIAGERETURNCHAR.toString().trim()):dispensingInterfaceUtil.getOctToDecimalValue("", Hl7MessageEnclosingCodes.CARRIAGERETURNCHAR.toString().trim());
			crChar= (char)segmentSeperatorCharDecimalValue;
			
			if(eOfChar > 0 && crChar > 0) 
				bFlag = true;
			
		}catch(RuntimeException ex){
           EcwLog.AppendExceptionToLog(ex);
		}
		return bFlag;
	} 

	/**
	 * API to process inbound hl7 message received from Dispensing Interface enabled in practice
	 * @param strHL7Message
	 * @param msgLogId
	 * @return boolean - True Or False on Success or Failure
	 */
	private boolean processDispensingHL7InboundMessage(String strHL7Message,String msgLogId,Map<String,Object> updateInboundLogStatusMap) throws DispensingInterfaceCustomException{
		boolean bFlag=false;
		List<Map<String,String>> dispensingInboundMessageDataListOut=parseDispensingHL7InboundMessage(strHL7Message);
		if(!dispensingInboundMessageDataListOut.isEmpty()){
			List<DispensingInterfaceInboundModel> dispensingInBoundModelObjListOut=generateDispensingInboundModelObjList(dispensingInboundMessageDataListOut,strHL7Message);
			if(!dispensingInBoundModelObjListOut.isEmpty())
				bFlag=processInboundHL7DataModelBasedOnMessageType(dispensingInBoundModelObjListOut,msgLogId,updateInboundLogStatusMap);
		}
		return bFlag;
	}
	/**
	 * API to parse,create dispensing inbound map for each message in inbound hl7 request and added into the list.
	 * @param strHL7Message
	 * @return List<Map<String,String>> - List of inbound hl7 message map object with 'Key' as segment name and 'Value' as associated segment data
	 */
	private List<Map<String,String>> parseDispensingHL7InboundMessage(String strHL7Message) {		
		List<Map<String,String>> dispensingInboundMessageDataList= new ArrayList<>();
		Map<String,String> dispensingInboundMessageDataMap=null;
		String [] hL7MessageArr = strHL7Message.split(String.valueOf(eOfChar));
		for (String hl7Data : hL7MessageArr) {
			String [] hl7SegmentList = hl7Data.trim().split(String.valueOf(crChar));//list of segments
			dispensingInboundMessageDataMap=new HashMap<>();
			for (int j = 0; j < hl7SegmentList.length; j++) {
				String [] segmentData= hl7SegmentList[j].split("\\|");
				dispensingInboundMessageDataMap.put(segmentData[0], hl7SegmentList[j]);
			}
			dispensingInboundMessageDataList.add(dispensingInboundMessageDataMap);
		}
 		return dispensingInboundMessageDataList;
	}
	/**
	 * API to generate list of dispensing inbound pojo based on list of inbound hl7 message map object 
	 * @param dispensingInboundMessageDataListOut
	 * @return List<DispensingInterfaceInboundModel> - List of pojo contain properties associated to each field on the inbound hl7 message segment data
	 */
	private List<DispensingInterfaceInboundModel> generateDispensingInboundModelObjList(List<Map<String,String>> dispensingInboundMessageDataListOut,String strHL7Message){
		List<DispensingInterfaceInboundModel> dispensingInBoundModelObjList= null;						
		Map<String,Object> inboundMessageExtractedFieldValueMap=null;
		String segmentTemplate="";
		String segmentData="";
		String segment="";
		StringBuilder hl7MessageBlock=new StringBuilder();
	    Map<String,Object> hl7SegmentMap=null;
		hl7SegmentMap=dispensingInterfaceCacheService.getDispensingHl7SegmentDetailsMap();
		dispensingInBoundModelObjList=new ArrayList<>();
		inboundMessageExtractedFieldValueMap=new HashMap<>();
		for(Map<String,String> map : dispensingInboundMessageDataListOut)
		{	
 			for (Map.Entry<String,String> entryKey : map.entrySet()) {
				switch(entryKey.getKey().trim()){
				case "MSH":
					 	 segment= hl7SegmentMap.get(entryKey.getKey()).toString();
						 segmentTemplate=segment.substring(InboundMessageConstants.INBOUNDMSGSEGMENTTEMPLATESTARTPOSITION.getConstVal(),segment.length()).replace("{", "").replace("}", "");
						 segmentData=entryKey.getValue().substring(InboundMessageConstants.INBOUNDMSGSEGMENTDATASTARTPOSITION.getConstVal(),entryKey.getValue().length());
						 hl7MessageBlock.append(entryKey.getValue());
						 parseInBoundSegmentData(segmentData,segmentTemplate,inboundMessageExtractedFieldValueMap);
						 break;
				case "ZPM":
				case "PID":
				case "PV1":
				case "FT1":
						 segment= hl7SegmentMap.get(entryKey.getKey()).toString();
						 segmentTemplate=segment.substring(InboundMessageConstants.INBOUNDSEGMENTSSTARTPOSITION.getConstVal(),segment.length()).replace("{", "").replace("}", "");
						 segmentData=entryKey.getValue().substring(InboundMessageConstants.INBOUNDSEGMENTSSTARTPOSITION.getConstVal(),entryKey.getValue().length());
						 hl7MessageBlock.append(entryKey.getValue());
						 parseInBoundSegmentData(segmentData,segmentTemplate,inboundMessageExtractedFieldValueMap);
						 break;
				default:
						break;
				}
			}
			inboundMessageExtractedFieldValueMap.put("HL7MESSAGE", strHL7Message);
			dispensingInBoundModelObjList.add(convertMapToModelObj(inboundMessageExtractedFieldValueMap));
		}
		return dispensingInBoundModelObjList;
	}
	/**
	 * API to parse individual hl7 segment data by matching it against the equivalent segmentTemplate  
	 * @param segmentData
	 * @param segmentTemplate
	 * @param inboundMessageExtactedFieldMap
	 * @return  Map<String,Object> - 'Key' as field name associated to that position from segmentTemplate and 'Value' as data associated to that position from segmentData
	 */
	private Map<String,Object> parseInBoundSegmentData(String segmentData,String segmentTemplate,Map<String,Object> inboundMessageExtactedFieldMap) {
		String[] fieldArrMSHSegment= segmentTemplate.split("\\|");
		if(null==segmentData || "".equals(segmentData))
			throw new UnprocessableEntityException("Invalid Value");
		
		String[]  segmentArr = segmentData.split("\\|");
		for(int i=0;i<segmentArr.length;i++){
			if(i<fieldArrMSHSegment.length){
				String extractedFieldValue=segmentArr[i];
				String extractedFieldName=fieldArrMSHSegment[i];
				if(extractedFieldValue.contains("^") || extractedFieldName.contains("^")){
					capSeperatedIterator(extractedFieldValue,extractedFieldName,inboundMessageExtactedFieldMap); 
				}
				else{
		    	  if(DispensingInterfaceUtil.validateNullObject(extractedFieldValue) && DispensingInterfaceUtil.validateNullObject(extractedFieldName))
		    		  inboundMessageExtactedFieldMap.put(extractedFieldName, extractedFieldValue);
				}
			}
		}
		return inboundMessageExtactedFieldMap;
	}
	/**
	 * API to process hl7 segment data individual field value separated with '^' character
	 * @param capSeperatedFieldValue
	 * @param capSeperatedFieldName
	 * @param inboundMessageExtactedFieldMap
	 * @return Map<String,Object> - 'Key' as field name associated to that position from segmentTemplate and 'Value' as data associated to that position from segmentData
	 */
	private Map<String,Object> capSeperatedIterator(String capSeperatedFieldValue,String capSeperatedFieldName,Map<String,Object> inboundMessageExtactedFieldMap){
		String [] capSeperateFieldValArr=capSeperatedFieldValue.split("\\^");
  	  	String [] capSeperateFieldNameArr=capSeperatedFieldName.split("\\^");
        for(int j=0;j<capSeperateFieldValArr.length;j++){
      	  if(j<capSeperateFieldNameArr.length)
      		  inboundMessageExtactedFieldMap.put(capSeperateFieldNameArr[j], capSeperateFieldValArr[j]);
        }
        return inboundMessageExtactedFieldMap;
	}
	/**
	 * API to convert the inbound hl7 message map into equivalent pojo 
	 * @param inboundMessageExtractedFieldValueMap
	 * @return DispensingInterfaceInboundModel - Pojo
	 */
	private DispensingInterfaceInboundModel convertMapToModelObj(Map<String,Object> inboundMessageExtractedFieldValueMap){
		DispensingInterfaceInboundModel modelObj=null;
		modelObj=ValidatorUtil.getObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).convertValue(inboundMessageExtractedFieldValueMap, DispensingInterfaceInboundModel.class);
		return modelObj;
	}
	/**
	 * API to process inbound hl7 message pojo based on type of message (ie ZPM or DFT etc) 
	 * @param dispensingInBoundModelObjList
	 * @param msgLogId
	 * @param updateInboundLogStatusMap
	 * @return boolean - True Or False on Success or Failure
	 */
private boolean processInboundHL7DataModelBasedOnMessageType(List<DispensingInterfaceInboundModel> dispensingInBoundModelObjList,String msgLogId,Map<String,Object> updateInboundLogStatusMap) throws DispensingInterfaceCustomException{		
		boolean bFlag=false;
		DispensingInterfaceInboundModel inboundModelObj=null;
		for (DispensingInterfaceInboundModel inboundModel: dispensingInBoundModelObjList) {
			inboundModelObj=inboundModel;
			inboundModelObj.setMsgLogId(msgLogId);
			String messageType=DispensingInterfaceUtil.validateNullObject(inboundModelObj.getrMessageCode())?(inboundModelObj.getrMessageType()+"^"+inboundModelObj.getrMessageCode()):inboundModelObj.getrMessageType();
			updateInboundLogStatusMap.put("messageType", messageType);
			switch(inboundModelObj.getrMessageType().trim()){
			case "ZPM":
				bFlag=processInboundHL7DataModelBasedOnPocketCode(inboundModelObj);
				break;
			case "DFT":
				bFlag=processInboundHL7DataModelBasedOnPocketCode(inboundModelObj);
				break;
			default:
				break;
			}
			if(!bFlag)
				throw new DispensingInterfaceCustomException(DispensingInterfaceInboundStatus.INVALIDINBOUNDMESSAGETYPE);
		}
		return bFlag;
	}
	/**
	 * API to process inbound hl7 message pojo based on pocket code message(ie L- LOAD , U-UNLOAD etc)
	 * @param inboundModelObj
	 * @return boolean - True Or False on Success or Failure
	 * @throws DispensingInterfaceCustomException
	 */
	private boolean processInboundHL7DataModelBasedOnPocketCode(DispensingInterfaceInboundModel inboundModelObj) throws DispensingInterfaceCustomException {
 		boolean bFlag=false;
		if(!DispensingInterfaceUtil.validateNullObject(inboundModelObj.getPocketCode()))
			throw new DispensingInterfaceCustomException(DispensingInterfaceInboundStatus.UNRECONGNIZABLEPOCKETCODE);
		
		switch (inboundModelObj.getPocketCode().trim()) {
			case "L":
					try {
						inboundModelObj.setTransactionType(HL7MessageComponents.LOAD.getHL7MsgComponent());
						bFlag=processInboundLoadMessage(inboundModelObj);
					}catch(DispensingInterfaceCustomException ex) {
						bFlag=logInBoundZPMMessageDetail(inboundModelObj, ex.getReconcile(), ex.getStatus(), ex.getStatusMessage());
					}
					break;
			case "U":
					try {
						inboundModelObj.setTransactionType(HL7MessageComponents.UNLOAD.getHL7MsgComponent());
						bFlag=processInboundUnLoadMessage(inboundModelObj);
					}catch(DispensingInterfaceCustomException ex) {
						bFlag=logInBoundZPMMessageDetail(inboundModelObj, ex.getReconcile(), ex.getStatus(), ex.getStatusMessage());
					}
					break;
			case "V":
					try {
						inboundModelObj.setTransactionType(HL7MessageComponents.DISPENSE.getHL7MsgComponent());		
						if(DispensingInterfaceUtil.validateNullObject(inboundModelObj.getrRxOrderNumber()) && inboundOverride.equalsIgnoreCase(inboundModelObj.getrRxOrderNumber())){
							inboundModelObj.setrRxOrderNumber(null);
							inboundModelObj.setOverrideOrder(true);
						}	
						bFlag=processInboundDispenseMessage(inboundModelObj);
					}catch(DispensingInterfaceCustomException ex) {
						bFlag=logInBoundDFTMessageDetail(inboundModelObj, ex.getReconcile(), ex.getStatus(), ex.getStatusMessage());
					}
					break;
			default:
					throw new DispensingInterfaceCustomException(DispensingInterfaceInboundStatus.INVALIDPOCKETCODE);
			}
		return bFlag;
	}
	/**
	 * API to process inbound 'Load' hl7 pocket code message.
	 * @param inboundModelObj
	 * @return boolean - True Or False on Success or Failure
	 * @throws DispensingInterfaceCustomException
	 */
	private boolean processInboundLoadMessage(DispensingInterfaceInboundModel inboundModelObj) throws DispensingInterfaceCustomException{
		boolean bFlag=false;
		if(!DispensingInterfaceUtil.validateNullObject(inboundModelObj.getDrugDispensedCode()) && !DispensingInterfaceUtil.validateNullObject(inboundModelObj.getTransactionStationName()))
		    throw new DispensingInterfaceCustomException(DispensingInterfaceInboundStatus.INVALIDDRUGDISPENSESTATIONNAME);
			
		bFlag=updateQtyBasedOnLoadMessage(inboundModelObj);

		return bFlag;
	}
	/**
	 * API to process inbound 'Unload' hl7 pocket code message.
	 * @param inboundModelObj
	 * @return boolean - True Or False on Success or Failure
	 * @throws DispensingInterfaceCustomException
	 */
	private boolean processInboundUnLoadMessage(DispensingInterfaceInboundModel inboundModelObj) throws DispensingInterfaceCustomException{
		boolean bFlag=false;
		if(!DispensingInterfaceUtil.validateNullObject(inboundModelObj.getDrugDispensedCode()) && !DispensingInterfaceUtil.validateNullObject(inboundModelObj.getTransactionStationName()))
			  throw new DispensingInterfaceCustomException(DispensingInterfaceInboundStatus.INVALIDDRUGDISPENSESTATIONNAME);
	    
		bFlag=updateQtyBasedOnUnLoadMessage(inboundModelObj);
		
	    return bFlag;
	}
	
	/**
	 * API to process inbound 'Dispense' hl7 pocket code message.
	 * @param inboundModelObj
	 * @return boolean - True Or False on Success or Failure
	 * @throws DispensingInterfaceCustomException
	 */
	private boolean processInboundDispenseMessage(DispensingInterfaceInboundModel inboundModelObj) throws DispensingInterfaceCustomException{
		boolean bFlag=false;
		if(!DispensingInterfaceUtil.validateNullObject(inboundModelObj.getDrugDispensedCode()) && !DispensingInterfaceUtil.validateNullObject(inboundModelObj.getTransactionStationName()))
			  throw new DispensingInterfaceCustomException(DispensingInterfaceInboundStatus.INVALIDDRUGDISPENSESTATIONNAME);
			  
	    bFlag=updateQtyBasedOnDispenseMessage(inboundModelObj);
	    
	    return bFlag;
	}
	
	/**
	 * API validates if the inbound hl7 message pojo properties like dispensing transaction station name and drugDispenseCode are valid and 
	 * checks if there is a stock area associated, if yes - update the quantity to total count value sent by dispensed transaction station for that drug 
	 * else checks if the drugDispenseCode is mapped to formulary, if yes- make an entry to map that drug to retrieved stock area and formulary id and 
	 * and log the request with the appropriate status and reason else reconcile with the appropriate reason 
	 * @param inboundModelObj
	 * @return boolean - True Or False on Success or Failure
	 * @throws DispensingInterfaceCustomException
	 */
	private boolean updateQtyBasedOnLoadMessage(DispensingInterfaceInboundModel inboundModelObj) throws DispensingInterfaceCustomException{
		Map<String, Object> stockAreaQtyDetailMap = null;
		Map<String,Object> stockAreaDetailMap = null;
		int dispenseFormularyQtyMapId=0;
		int transactionTotalAmt=0;
		int existingQty=0;
		String stockStatus=DispensingInterfaceKeyConstants.ACTIVE.getKeyConstants();
		boolean bFlag=false;
		
		stockAreaDetailMap=getStockAreaMappedToStationName(inboundModelObj);
		inboundModelObj.setDispenseStockId(stockAreaDetailMap.get(DispensingInterfaceKeyConstants.DISPENSESTOCKID.getKeyConstants()).toString());
		inboundModelObj.setDispenseStockAreaName(stockAreaDetailMap.get(DispensingInterfaceKeyConstants.DISPENSESTOCKAREANAME.getKeyConstants()).toString());
				
		if(!DispensingInterfaceUtil.validateNullObject(inboundModelObj.getDrugDispensedCode()))
			throw new DispensingInterfaceCustomException(DispensingInterfaceInboundStatus.INVALIDDRUGDISPENSECODE);
					
		stockAreaQtyDetailMap=dispensingInterfaceInBoundDAO.getDrugDispenseCodeQtyDetailMap(inboundModelObj.getDrugDispensedCode(),inboundModelObj.getDispenseStockId());
		if(null!=stockAreaQtyDetailMap && !stockAreaQtyDetailMap.isEmpty()){
			dispenseFormularyQtyMapId = Integer.parseInt(stockAreaQtyDetailMap.get("id").toString());
			existingQty= "".equals(stockAreaQtyDetailMap.get(DispensingInterfaceKeyConstants.QUANTITY.getKeyConstants()).toString())? 0 : Integer.parseInt(stockAreaQtyDetailMap.get(DispensingInterfaceKeyConstants.QUANTITY.getKeyConstants()).toString());
			inboundModelObj.setExistingQty(String.valueOf(existingQty));
			
			if(DispensingInterfaceUtil.validateNullObject(inboundModelObj.getTotalDrugCount())){
				transactionTotalAmt= Integer.parseInt(inboundModelObj.getTotalDrugCount());
	  		}
			if(dispensingInterfaceInBoundDAO.updateQtyDetail(transactionTotalAmt,dispenseFormularyQtyMapId,stockStatus,dispensingInterfaceCacheService.getInterfaceUserId())==1){
				logInBoundZPMMessageDetail(inboundModelObj,DispensingInterfaceInboundStatus.QTYUPDATED.getReconcile(),DispensingInterfaceInboundStatus.QTYUPDATED.getStatus(),DispensingInterfaceInboundStatus.QTYUPDATED.getStatusMessage()); 
				bFlag=true;
			}
			else
			    throw new DispensingInterfaceCustomException(DispensingInterfaceInboundStatus.QTYUPDATEFAILED);
		}else{
			bFlag=setFormualryForGivenDrugDispenseCode(inboundModelObj,stockStatus);
		}
		return bFlag;
	}
	/**
	 * API validates if the inbound hl7 message pojo properties like dispensing transaction station name and drugDispenseCode are valid and 
	 * checks if there is a stock area associated, if yes - check if the total count value and transaction count value are both '0', 
	 * if yes - update the status of that drug as 'inactive' for that stock area associate to that dispensing transaction station name else 
	 * update the quantity to total count value sent by dispensed transaction station for that drug 
	 * else log the request with the appropriate status and reason else reconcile with the appropriate reason 
	 * @param inboundModelObj
	 * @return boolean - True Or False on Success or Failure
	 * @throws DispensingInterfaceCustomException
	 */
	private boolean updateQtyBasedOnUnLoadMessage(DispensingInterfaceInboundModel inboundModelObj) throws DispensingInterfaceCustomException{
		Map<String, Object> stockAreaQtyDetailMap = null;
		Map<String,Object> stockAreaDetailMap=null;
		int dispenseFormularyQtyMapId=0;
		int transactionTotalAmt=0;
		int transactionAmt=0;
		int existingQty=0;
		String stockStatus="active";
		boolean bFlag=false;
		
		stockAreaDetailMap=getStockAreaMappedToStationName(inboundModelObj);
		inboundModelObj.setDispenseStockId(stockAreaDetailMap.get(DispensingInterfaceKeyConstants.DISPENSESTOCKID.getKeyConstants()).toString());
		inboundModelObj.setDispenseStockAreaName(stockAreaDetailMap.get(DispensingInterfaceKeyConstants.DISPENSESTOCKAREANAME.getKeyConstants()).toString());
		
		stockAreaQtyDetailMap=getDrugDispenseCodeQtyDetails(inboundModelObj);
		dispenseFormularyQtyMapId = Integer.parseInt(stockAreaQtyDetailMap.get("id").toString());
		existingQty= "".equals(stockAreaQtyDetailMap.get(DispensingInterfaceKeyConstants.QUANTITY.getKeyConstants()).toString())? 0 : Integer.parseInt(stockAreaQtyDetailMap.get(DispensingInterfaceKeyConstants.QUANTITY.getKeyConstants()).toString());
		inboundModelObj.setExistingQty(String.valueOf(existingQty));
		
		if(DispensingInterfaceUtil.validateNullObject(inboundModelObj.getTotalDrugCount())){
			transactionTotalAmt= Integer.parseInt(inboundModelObj.getTotalDrugCount());
  		}
		if(DispensingInterfaceUtil.validateNullObject(inboundModelObj.getTotalDrugCount())){
			transactionAmt= Integer.parseInt(inboundModelObj.getTransactionAmount());
  		}
		if(transactionAmt==0 && transactionTotalAmt==0)
			stockStatus="inactive";
			
		if(dispensingInterfaceInBoundDAO.updateQtyDetail(transactionTotalAmt,dispenseFormularyQtyMapId,stockStatus,dispensingInterfaceCacheService.getInterfaceUserId())==1){
			if("inactive".equalsIgnoreCase(stockStatus))
				bFlag=logInBoundZPMMessageDetail(inboundModelObj,DispensingInterfaceInboundStatus.QTYUPDATEDWITHSTATUSINACTIVE.getReconcile(),DispensingInterfaceInboundStatus.QTYUPDATEDWITHSTATUSINACTIVE.getStatus(),DispensingInterfaceInboundStatus.QTYUPDATEDWITHSTATUSINACTIVE.getStatusMessage()); 
			else
				bFlag=logInBoundZPMMessageDetail(inboundModelObj,DispensingInterfaceInboundStatus.QTYUPDATED.getReconcile(),DispensingInterfaceInboundStatus.QTYUPDATED.getStatus(),DispensingInterfaceInboundStatus.QTYUPDATED.getStatusMessage()); 
		}
		else
			throw new DispensingInterfaceCustomException(DispensingInterfaceInboundStatus.QTYUPDATEFAILED);

		return bFlag;
	}
	/**
	 * API validates if the inbound hl7 message pojo properties like dispensing transaction station name and drugDispenseCode are valid and 
	 * checks if there is a stock area associated, if yes - check if the received order number is 'OVERRIDE',if yes - set order number to '0' and 
	 * update the quantity to total count value sent by dispensed transaction station for that drug 
	 * and log the request with the appropriate status and mark it to reconcile else log the request with the appropriate status and reason
	 * @param inboundModelObj
	 * @return boolean - True Or False on Success or Failure
	 * @throws DispensingInterfaceCustomException 
	 */
	private boolean updateQtyBasedOnDispenseMessage(DispensingInterfaceInboundModel inboundModelObj) throws DispensingInterfaceCustomException{
		Map<String, Object> stockAreaQtyDetailMap = null;
		Map<String,Object> stockAreaDetailMap=null;
		int dispenseFormularyQtyMapId=0;
		int transactionTotalAmt=0;
		int existingQty=0;
		String stockStatus="active";
		boolean bFlag=false;
		if(DispensingInterfaceUtil.validateNullObject(inboundModelObj.getTotalDrugCount())){
			transactionTotalAmt= Integer.parseInt(inboundModelObj.getTotalDrugCount());
  		}
		stockAreaDetailMap=getStockAreaMappedToStationName(inboundModelObj);
		inboundModelObj.setDispenseStockId(stockAreaDetailMap.get("dispenseStockId").toString());
		inboundModelObj.setDispenseStockAreaName(stockAreaDetailMap.get("dispenseStockAreaName").toString());
		
		stockAreaQtyDetailMap=getDrugDispenseCodeQtyDetails(inboundModelObj);
		dispenseFormularyQtyMapId = Integer.parseInt(stockAreaQtyDetailMap.get("id").toString());
		existingQty= "".equals(stockAreaQtyDetailMap.get("quantity").toString())? 0 : Integer.parseInt(stockAreaQtyDetailMap.get("quantity").toString());
		inboundModelObj.setExistingQty(String.valueOf(existingQty));
		
		if(dispensingInterfaceInBoundDAO.updateQtyDetail(transactionTotalAmt,dispenseFormularyQtyMapId,stockStatus,dispensingInterfaceCacheService.getInterfaceUserId())==1)
			if(inboundModelObj.isOverrideOrder())
				bFlag=logInBoundDFTMessageDetail(inboundModelObj,DispensingInterfaceInboundStatus.INBOUNDUNSOLICITEDMESSAGE.getReconcile(),DispensingInterfaceInboundStatus.INBOUNDUNSOLICITEDMESSAGE.getStatus(),DispensingInterfaceInboundStatus.INBOUNDUNSOLICITEDMESSAGE.getStatusMessage()); 
			else
				bFlag=logInBoundDFTMessageDetail(inboundModelObj,DispensingInterfaceInboundStatus.QTYUPDATED.getReconcile(),DispensingInterfaceInboundStatus.QTYUPDATED.getStatus(),DispensingInterfaceInboundStatus.QTYUPDATED.getStatusMessage()); 
		else
			throw new DispensingInterfaceCustomException(DispensingInterfaceInboundStatus.QTYUPDATEFAILED);

		return bFlag;
	}
	/**
	 * API to get stock area's mapped to received station name
	 * @param inboundModelObj
	 * @return Map<String,Object> - Stock Area details 
	 * @throws DispensingInterfaceCustomException
	 */
	private Map<String,Object> getStockAreaMappedToStationName(DispensingInterfaceInboundModel inboundModelObj) throws DispensingInterfaceCustomException{
		Map<String,Object> stockAreaDetailMap = null;
		if(DispensingInterfaceUtil.validateNullObject(inboundModelObj.getTransactionStationName())){
			stockAreaDetailMap=dispensingInterfaceInBoundDAO.getStockAreaDetailBasedOnExternalMachine(inboundModelObj.getTransactionStationName());
			if(null==stockAreaDetailMap || stockAreaDetailMap.isEmpty()){
				throw new DispensingInterfaceCustomException(DispensingInterfaceInboundStatus.NOSTOCKAREAMAPPEDTOSTATIONNAME);
			}
		}else{
			throw new DispensingInterfaceCustomException(DispensingInterfaceInboundStatus.INVALIDSTATIONNAME);
		}
		return stockAreaDetailMap;
	}
	/**
	 * API to get quantity mapping details associated to received drug dispense code and stock id
	 * @param inboundModelObj
	 * @return Map<String,Object> - Quantity Mapping details for a drug dispense code and stock id
	 * @throws DispensingInterfaceCustomException
	 */
	private Map<String,Object> getDrugDispenseCodeQtyDetails(DispensingInterfaceInboundModel inboundModelObj)throws DispensingInterfaceCustomException{
		Map<String, Object> stockAreaQtyDetailMap = null;
		if(DispensingInterfaceUtil.validateNullObject(inboundModelObj.getDrugDispensedCode())){
			stockAreaQtyDetailMap=dispensingInterfaceInBoundDAO.getDrugDispenseCodeQtyDetailMap(inboundModelObj.getDrugDispensedCode(),inboundModelObj.getDispenseStockId());
			if(null==stockAreaQtyDetailMap || stockAreaQtyDetailMap.isEmpty()){
			  throw new DispensingInterfaceCustomException(DispensingInterfaceInboundStatus.NOFORMULARYIDMAPPEDTODRUGCODE);
			}
		}
		else{
			throw new DispensingInterfaceCustomException(DispensingInterfaceInboundStatus.INVALIDDRUGDISPENSECODE);
		}
		return stockAreaQtyDetailMap;
	}
	/**
	 * API to map quantity details ,if received drug dispense code is mapped to any formulary id and stockstatus
	 * @param inboundModelObj
	 * @param stockStatus
	 * @return True or False - Successful mapping or Failed to Map
	 * @throws DispensingInterfaceCustomException
	 */
	private boolean setFormualryForGivenDrugDispenseCode(DispensingInterfaceInboundModel inboundModelObj,String stockStatus) throws DispensingInterfaceCustomException{
		boolean bFlag=false;
		int dispenseFormularyId=0;
		dispenseFormularyId=dispensingInterfaceInBoundDAO.getFormularyIdForGivenDrugDispenseCode(inboundModelObj.getDrugDispensedCode());
		if(dispenseFormularyId!=0){
			dispensingInterfaceInBoundDAO.insertQtyDetail(inboundModelObj.getDispenseStockId(),dispenseFormularyId,inboundModelObj.getDrugDispensedCode(),inboundModelObj.getTotalDrugCount(),stockStatus,dispensingInterfaceCacheService.getInterfaceUserId());
			bFlag=logInBoundZPMMessageDetail(inboundModelObj,0,DispensingInterfaceInboundStatus.FORMULARYMAPPEDTODRUGDIPSENSEDCODE.getStatus(),DispensingInterfaceInboundStatus.FORMULARYMAPPEDTODRUGDIPSENSEDCODE.getStatusMessage());
		}else{
			throw new DispensingInterfaceCustomException(DispensingInterfaceInboundStatus.NOFORMULARYIDMAPPEDTODRUGCODE);
		}
		return bFlag;
	}	
	/**
	 * API to log the inbound ZPM transaction hl7 message details
	 * @param inboundModelObj
	 * @param reconcileStatus - '1' if the message needs to be flagged as reconcile else '0'
	 * @param status-check in which state the message :failure,pending,success
	 * @param reason-reason for failure 
	 * @param inboundStatusCode - Appropriate status codes if the request was success or failed or to be reconciled
	 * @return boolean - True Or False
	 */
	private boolean logInBoundZPMMessageDetail(DispensingInterfaceInboundModel inboundModelObj,int reconcile,String status,String statusMessage){
		boolean bFlag=false;
		Map<String,Object> dispenseInboundMessageMapObject=new LinkedHashMap<>();
		dispensingInterfaceUtil =new DispensingInterfaceUtil();
		dispenseInboundMessageMapObject.put("logId", Integer.parseInt(inboundModelObj.getMsgLogId()));
		dispenseInboundMessageMapObject.put("messageId", inboundModelObj.getrMessageControlId());
		dispenseInboundMessageMapObject.put("dispensingInterfaceId", dispensingInterfaceCacheService.getDispensingInterfaceModel().getInterfaceId()==0?null:dispensingInterfaceCacheService.getDispensingInterfaceModel().getInterfaceId());
		dispenseInboundMessageMapObject.put("dispenseStockId", inboundModelObj.getDispenseStockId());
		dispenseInboundMessageMapObject.put("hl7Message", inboundModelObj.getHl7message());
		dispenseInboundMessageMapObject.put("existingQty", inboundModelObj.getExistingQty());
		dispenseInboundMessageMapObject.put("transactionDrugDispenseCode", inboundModelObj.getDrugDispensedCode());
		dispenseInboundMessageMapObject.put("transactionDrugDescription", inboundModelObj.getDrugDispensedName());
		dispenseInboundMessageMapObject.put("transactionStationName",inboundModelObj.getTransactionStationName());
		dispenseInboundMessageMapObject.put("transactionType", inboundModelObj.getTransactionType());
		dispenseInboundMessageMapObject.put("transactionAmount", inboundModelObj.getTransactionAmount());
		dispenseInboundMessageMapObject.put("transactionTotalDrugCount", inboundModelObj.getTotalDrugCount());
		dispenseInboundMessageMapObject.put("transactionUserId", inboundModelObj.getTransactionUserId());
		dispenseInboundMessageMapObject.put("transactionUserName", inboundModelObj.getTransactionUserName());
		dispenseInboundMessageMapObject.put("transactionDateTime", dispensingInterfaceUtil.convertDateFormat(inboundModelObj.getrDateTimeTransmission(),"yyyyMMddhhmmss","yyyy-MM-dd HH:mm:ss"));
		dispenseInboundMessageMapObject.put("createdOn", DateUtil.getTodaysDate("yyyy-MM-dd HH:mm:ss"));
		dispenseInboundMessageMapObject.put("updatedOn", DateUtil.getTodaysDate("yyyy-MM-dd HH:mm:ss"));
		dispenseInboundMessageMapObject.put("updatedBy", dispensingInterfaceCacheService.getInterfaceUserId());
		dispenseInboundMessageMapObject.put("reconcile", reconcile);
		dispenseInboundMessageMapObject.put("statusMessage",statusMessage);
		if(reconcile==1)
			dispenseInboundMessageMapObject.put("status",DispensingMessageStatusCode.PENDING.getDispensingMessageStatusCode());
		else
			dispenseInboundMessageMapObject.put("status",DispensingInterfaceKeyConstants.SUCCESS.getKeyConstants());
		
		int isLogged=dispensingInterfaceInBoundDAO.logInBoundZPMMessageDetail(dispenseInboundMessageMapObject);
		if(isLogged>0)
			bFlag=true;

		return bFlag;
	}
	/**
	 * API to log the inbound DFT transaction hl7 message order details
	 * @param inboundModelObj
	 * @param reconcileStatus - '1' if the message needs to be flagged as reconcile else '0'
	 * @param status-check in which state the message :failure,pending,success
	 * @param reason-reason for failure 
	 * @param inboundStatusCode - Appropriate status codes if the request was success or failed or to be reconciled
	 * @return boolean - True Or False
	 */
	private boolean logInBoundDFTMessageDetail(DispensingInterfaceInboundModel inboundModelObj,int reconcile,String status,String statusMessage) {
		boolean bFlag=false;
		int isLogged=0;
		Map<String,Object> dispenseInboundMessageMapObject=new LinkedHashMap<>();
		dispensingInterfaceUtil =new DispensingInterfaceUtil();
		dispenseInboundMessageMapObject.put("logId", Integer.parseInt(inboundModelObj.getMsgLogId()));
		dispenseInboundMessageMapObject.put("messageId", inboundModelObj.getrMessageControlId());
		dispenseInboundMessageMapObject.put("dispensingInterfaceId", dispensingInterfaceCacheService.getDispensingInterfaceModel().getInterfaceId()==0?null:dispensingInterfaceCacheService.getDispensingInterfaceModel().getInterfaceId());
		dispenseInboundMessageMapObject.put("dispenseStockId", inboundModelObj.getDispenseStockId());
		dispenseInboundMessageMapObject.put("patientId", inboundModelObj.getrPatientId());
		dispenseInboundMessageMapObject.put("medOrderId", inboundModelObj.getrRxOrderNumber());
		dispenseInboundMessageMapObject.put("hl7Message", inboundModelObj.getHl7message());
		dispenseInboundMessageMapObject.put("existingQty", inboundModelObj.getExistingQty());
		dispenseInboundMessageMapObject.put("transactionDrugDispenseCode", inboundModelObj.getDrugDispensedCode());
		dispenseInboundMessageMapObject.put("transactionDrugDescription", inboundModelObj.getDrugDispensedName());
		dispenseInboundMessageMapObject.put("transactionStationName",inboundModelObj.getTransactionStationName());
		dispenseInboundMessageMapObject.put("transactionType", inboundModelObj.getTransactionType());
		dispenseInboundMessageMapObject.put("transactionAmount", inboundModelObj.getTransactionAmount());
		dispenseInboundMessageMapObject.put("transactionTotalDrugCount", inboundModelObj.getTotalDrugCount());
		dispenseInboundMessageMapObject.put("transactionUserId", inboundModelObj.getTransactionUserId());
		dispenseInboundMessageMapObject.put("transactionUserName", inboundModelObj.getTransactionUserName());
		dispenseInboundMessageMapObject.put("transactionDateTime", dispensingInterfaceUtil.convertDateFormat(inboundModelObj.getrDateTimeTransmission(),"yyyyMMddhhmmss","yyyy-MM-dd HH:mm:ss"));
		dispenseInboundMessageMapObject.put("createdOn", DateUtil.getTodaysDate("yyyy-MM-dd HH:mm:ss") );
		dispenseInboundMessageMapObject.put("updatedOn", DateUtil.getTodaysDate("yyyy-MM-dd HH:mm:ss") );
		dispenseInboundMessageMapObject.put("updatedBy", dispensingInterfaceCacheService.getInterfaceUserId());
		dispenseInboundMessageMapObject.put("reconcile", reconcile);
		dispenseInboundMessageMapObject.put("statusMessage",statusMessage);
		if(reconcile==1)
			dispenseInboundMessageMapObject.put("status",DispensingMessageStatusCode.PENDING.getDispensingMessageStatusCode());
		else
			dispenseInboundMessageMapObject.put("status",DispensingInterfaceKeyConstants.SUCCESS.getKeyConstants());
		
		if(inboundModelObj.isOverrideOrder()) {
			isLogged=dispensingInterfaceInBoundDAO.logInBoundDFTMessageOverridOrderDetail(dispenseInboundMessageMapObject);
		}else {
		    isLogged=dispensingInterfaceInBoundDAO.logInBoundDFTMessageProfileOrderDetail(dispenseInboundMessageMapObject);
		}
		if(isLogged>0)
			bFlag=true;
		
		return bFlag;
	}
}

