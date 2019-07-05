package inpatientWeb.pharmacy.interfaces.outbound.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import inpatientWeb.Auth.exception.UnprocessableEntityException;
import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;
import inpatientWeb.pharmacy.interfaces.outbound.dao.DispensingInterfaceOutBoundDAO;
import inpatientWeb.pharmacy.interfaces.outbound.dto.DispensingInterfaceOutBoundModel;
import inpatientWeb.pharmacy.interfaces.outbound.service.DispensingInterfaceOutBoundService;
import inpatientWeb.pharmacy.interfaces.service.DispensingInterfaceCacheService;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.DispensingInterfaceKeyConstants;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.DispensingInterfaceStatus;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.HL7MessageComponents;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.MessageTypeCodes;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.OrderTypeCodes;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceCustomException;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceUtil;
import inpatientWeb.utils.DateUtil;
/**
@author arun.reddy diveshg
*/
@Service
@Lazy
@Scope("prototype")
public class DispensingInterfaceOutBoundServiceImpl implements DispensingInterfaceOutBoundService{
	
	private  Map<String,String> dispensingInterfaceMandatoryFieldsMap = null;
	
	private  Map<String,Object> responseMap=null;
	
	String regExPattern = "[{]+[\\w]+[}]+";
	Pattern pattern = Pattern.compile(regExPattern);
    Matcher matcher = null;
    private  static final String ENCODINGCHAR = "^~\\&";
    private  String zrxSegment = null;
    private  static final int QTYTIMINGORCSEGMENTFIELDPOSITION =7;
    private  static final int QTYTIMINGRXESEGMENTFIELDPOSITION=1;
    private  String dateFormat="yyyy-MM-dd HH:mm:ss";
    private  static final String ENCCHARS = "ENCCHARS";
    
	DispensingInterfaceUtil dispensingUtil = new DispensingInterfaceUtil();
	
	@Autowired
	private DispensingInterfaceOutBoundDAO dispensingIterfaceOutBoundDao;
	
	@Autowired
	private DispensingInterfaceCacheService dispensingInterfaceCacheService;
    
    public void getEnabledInterfaceDetails() throws DispensingInterfaceCustomException{
		getInterfaceDetails();
		getDispensingHl7SegmentDetails();
		getDispensingMessageComponentTypeSegment();
    }
	/**
	 * API validates Order type (e.g; NEW, HOLD , etc)
	 * @exception DispensingInterfaceCustomException - INVALIDORDERTYPE
	 * @param oTypeCode
	 * @return True  - If Order type detail found
	 *         False - If Order type detail not found
	 */
	private boolean validateOrderType(OrderTypeCodes oTypeCode) throws DispensingInterfaceCustomException{
		boolean bFlag=false;
		if(null!=oTypeCode){
			switch (oTypeCode){
				case CANCEL:
					bFlag=true;
					break;
				case DISCONTINUE:
					bFlag=true;
					break;
				case HOLD:
					bFlag=true;
					break;
				case NEW:
					bFlag=true;
					break;
				default:
					break;
			}
		}
		if(!bFlag)
			throw new DispensingInterfaceCustomException(DispensingInterfaceStatus.INVALIDORDERTYPE);
		
		return bFlag;
	}
	/**
	 * API validate the Message Type(e.g; FIRSTORDER, SCHEDULED , etc)
	 * @exception DispensingInterfaceCustomException - INVALIDMESSAGETYPE
	 * @param msgTypeCode
	 * @return True  - If Message Type detail found
	 *         False - If Message Type detail not found
	 */
	private boolean validateMessageType(MessageTypeCodes msgTypeCode) throws DispensingInterfaceCustomException{
		boolean bFlag=false;
		if(null!=msgTypeCode){
			switch(msgTypeCode){
				case FIRSTORDER:
					bFlag=true;
					break;
				case SCHEDULED:
					bFlag=true;
					break;
				case FIRSTORDERANDSCHEDULED:
					bFlag=true;
					break;
				default:
					break;				
			}
		}
		if(!bFlag)
			throw new DispensingInterfaceCustomException(DispensingInterfaceStatus.INVALIDMESSAGETYPE);
		
		return bFlag;
	}
	/**
	 * API to validate the Component Type
	 * @exception DispensingInterfaceCustomException - INVALIDMESSAGETYPE
	 * @param hl7MsgComponent
	 * @return True  - If Component Type detail found
	 *         False - If Component Type detail not found
	 */
	private boolean validateComponentType(HL7MessageComponents hl7MsgComponent)throws DispensingInterfaceCustomException{
		boolean bFlag=false;
		if(null!=hl7MsgComponent){
			switch(hl7MsgComponent){
				case SINGLE:
					bFlag=true;
					break;
				case MULTI:
					bFlag=true;
					break;
				case LINKEDORDER:
					bFlag=true;
					break;
				default:
					break;
			}
		}
		if(!bFlag)
			throw new DispensingInterfaceCustomException(DispensingInterfaceStatus.INVALIDCOMPONENTTYPE);
		
		return bFlag;
	}

	/**
	 * API to check if interface details are not null then parse the mandatory fields for interface
	 * @exception DispensingInterfaceCustomException - INACTIVEINTERFACE
	 * @return True  - If interface details are found
	 *         False - If interface details are not found
	 */
	private boolean getInterfaceDetails() throws DispensingInterfaceCustomException{
		boolean bFlag=false;
		if(null != dispensingInterfaceCacheService.getDispensingInterfaceModel()){
			parseMandatoryFieldsForInterface();
			bFlag=true;
		}else{
			throw new DispensingInterfaceCustomException(DispensingInterfaceStatus.INACTIVEINTERFACE);
		}
		return bFlag;
	}

	/**
	 * API to get HL7 Outbound message segment details of the medication interface.
	 * @exception DispensingInterfaceCustomException - INVALIDSEGMENTS
	 * @return True  - If Hl7 Segment details are found
	 *         False - If Hl7 Segment details are not found
	 */
	private boolean getDispensingHl7SegmentDetails() throws DispensingInterfaceCustomException {
		boolean bFlag=false;
		if(null == dispensingInterfaceCacheService.getDispensingHl7SegmentDetailsMap()){
			throw new DispensingInterfaceCustomException(DispensingInterfaceStatus.INVALIDSEGMENTS);
		}else{
			bFlag=true;
		}
		return bFlag;
	}	
	
	/**
	 * API gets the component details of medication interface.
	 * @exception DispensingInterfaceCustomException - INVALIDSEGMENTS
	 * @return True  - If Hl7 Component Type Segment details are found
	 *         False - If Hl7 Component Type Segment details are not found
	 */
	private boolean getDispensingMessageComponentTypeSegment() throws DispensingInterfaceCustomException{
	    boolean bFlag=false;
		if(null==dispensingInterfaceCacheService.getDispensingComponentTypeSegmentMap()){
			throw new DispensingInterfaceCustomException(DispensingInterfaceStatus.INVALIDCOMPONENTS);
		}else{
			bFlag=true;
		}
		return bFlag;
	}
	
	/**
	 * This overridden API acts as an engine - where it accepts all the required data to create HL7 Outbound Messages in the form Pojo object - that pojo gets parsed and then validated with mandatory fields which are required for the creation of message and then final HL7 message get logged.   
	 * @exception DispensingInterfaceCustomException
	 */
	@Override
	public Map<String, Object> processDispensingOutBoundHl7Message(DispensingInterfaceOutBoundModel dispensingInterfaceOutBoundModel){
		List<String> dispensingHl7OutBoundMessageList = null;
		int logId = 0;
		String messageLogStatus = DispensingInterfaceKeyConstants.FAILED.getKeyConstants();
		try{
			responseMap=new HashMap<>();
	    	responseMap.put(DispensingInterfaceKeyConstants.STATUS.getKeyConstants(), DispensingInterfaceKeyConstants.FAILED.getKeyConstants());
	    	responseMap.put(DispensingInterfaceKeyConstants.MESSAGE.getKeyConstants(), "");
	    	dispensingInterfaceCacheService =new DispensingInterfaceCacheService();
	    	String messageContent = null;
	    	
	    	if(null == dispensingInterfaceOutBoundModel) {
	    		EcwLog.AppendToLog("[DispensingInterfaceOutBoundServiceImpl] processDispensingOutBoundHl7Message - [Reason - Received dispensing interface outbound message data is null]: "+ new java.util.Date());
				throw new DispensingInterfaceCustomException(DispensingInterfaceStatus.INTERFACEDATANULL);
	    	}
			Gson gson = new GsonBuilder().create();
			messageContent = gson.toJson(dispensingInterfaceOutBoundModel);
			logId = logDispensingInterfaceMsgContent(dispensingInterfaceOutBoundModel , messageContent);

			if(dispensingInterfaceCacheService.setDispensingInterfaceCache()){
				getEnabledInterfaceDetails();
			}else{
				EcwLog.AppendToLog("[DispensingInterfaceOutBoundServiceImpl] processDispensingOutBoundHl7Message - [Reason - Dispensing interface cache could not be initalized]: "+ new java.util.Date());
				throw new DispensingInterfaceCustomException(DispensingInterfaceStatus.INTERFACECACHENOTSET);
			}
			validateOrderType(dispensingInterfaceOutBoundModel.getOrderType());
		    validateMessageType(dispensingInterfaceOutBoundModel.getMessageType());
			validateComponentType(dispensingInterfaceOutBoundModel.getComponentType());
			dispensingInterfaceOutBoundModel.setrOrderControlCode(dispensingInterfaceOutBoundModel.getOrderType().getOrderTypeCode());
			dispensingInterfaceOutBoundModel.setrRecApp(dispensingInterfaceCacheService.getEnabledInterface());
			
			Map<String, Object> hl7OutBoundDataMap = new ObjectMapper().readValue(messageContent, LinkedHashMap.class);
			Map<String, Object> dispensingInterfaceOutBoundModelMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
			dispensingInterfaceOutBoundModelMap.putAll(hl7OutBoundDataMap);	
			
			if(this.dispensingInterfaceMandatoryFieldsMap.size() > 0){
				validateMandatoryFields(dispensingInterfaceOutBoundModelMap);
			}
			// creating segment key list
			String[] segmentKeyList = null;
			if(dispensingInterfaceCacheService.getDispensingComponentTypeSegmentMap().containsKey(dispensingInterfaceOutBoundModel.getComponentType().toString())){
				segmentKeyList = dispensingInterfaceCacheService.getDispensingComponentTypeSegmentMap().get(dispensingInterfaceOutBoundModel.getComponentType().toString()).toString().split(",");
			}else{
				throw new DispensingInterfaceCustomException(DispensingInterfaceStatus.INVALIDCOMPONENTS);
			}
			
			//final outbound message creation 
			dispensingHl7OutBoundMessageList = prepareDispensingHl7OutBoundMessage(segmentKeyList, dispensingInterfaceOutBoundModelMap);
			if(!dispensingHl7OutBoundMessageList.isEmpty()){
				logDispensingHl7OutBoundMessage(dispensingHl7OutBoundMessageList, dispensingInterfaceOutBoundModel, logId);
			}
		}catch (DispensingInterfaceCustomException ex) {
			responseMap.put(DispensingInterfaceKeyConstants.STATUS.getKeyConstants(),ex.getStatus());
	    	responseMap.put(DispensingInterfaceKeyConstants.MESSAGE.getKeyConstants(),ex.getStatusMessage());
	    	if(logId > 0)
	    		updateLogDispensingInterfaceMsgContent(logId , messageLogStatus,ex.getStatusMessage());
		}catch (IOException | RuntimeException ex) {
			responseMap.put(DispensingInterfaceKeyConstants.STATUS.getKeyConstants(), DispensingInterfaceKeyConstants.FAILED.getKeyConstants());
    		responseMap.put(DispensingInterfaceKeyConstants.MESSAGE.getKeyConstants(),ex.getMessage());
			EcwLog.AppendExceptionToLog(ex);
			if(logId > 0)
				updateLogDispensingInterfaceMsgContent(logId , messageLogStatus,ex.getMessage());
		} 
		return responseMap;
	}
	/**
	 * API will parse all mandatory fields for dispensing interface to create HL7 message and communicate with interface
	 * @return True  - If Mandatory Fields are parsed successfully
	 *         False - If Mandatory Fields are not parsed successfully
	 */
	private boolean parseMandatoryFieldsForInterface(){
		boolean bFlag = false;
		String  mandatoryFieldsJson = dispensingInterfaceCacheService.getDispensingInterfaceModel().getMandatoryFields();
		dispensingInterfaceMandatoryFieldsMap =  new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		if(mandatoryFieldsJson!= null){
			HashMap<String,String> mandatoryFieldsMap = new Gson().fromJson(mandatoryFieldsJson, new TypeToken<HashMap<String, String>>(){}.getType());	
			if(mandatoryFieldsMap.size() > 0) {
				this.dispensingInterfaceMandatoryFieldsMap.putAll(mandatoryFieldsMap);	
				bFlag = true;
			}
		}
		return bFlag;
	}	
	/**
	 * API to validate the mandatory fields as these cannot be blank or null 
	 * @throws DispensingInterfaceCustomException
	 * @param dispensingInterfaceOutBoundModelMap
	 * @return True  - If Mandatory Fields are validated successfully
	 *         False - If Mandatory Fields are not validated successfully
	 */
	private boolean validateMandatoryFields(Map<String, Object> dispensingInterfaceOutBoundModelMap) throws DispensingInterfaceCustomException{
		boolean bFlag = true;
		if(null==dispensingInterfaceOutBoundModelMap || dispensingInterfaceOutBoundModelMap.isEmpty()) {
			throw new UnprocessableEntityException("Invalid Value");
		}
		TreeSet<String> listOfNullFields = new TreeSet<>();  
		for (Map.Entry<String, Object> entry : dispensingInterfaceOutBoundModelMap.entrySet())
		{
			String key = entry.getKey();
			if(this.dispensingInterfaceMandatoryFieldsMap.containsKey(key)) {
				String value = entry.getValue() != null ? entry.getValue().toString() : "";
				if((value.isEmpty()) || (value == null)){
					listOfNullFields.add(this.dispensingInterfaceMandatoryFieldsMap.get(key));
			    }
			}
		}
		List<Map<String, Object>> rxFreqDetails = ((List<Map<String, Object>>) dispensingInterfaceOutBoundModelMap.get(DispensingInterfaceKeyConstants.RXFREQDETAILS.getKeyConstants()));
		validateDispensingOutBoundModelPropertyListMandatoryFields(rxFreqDetails,listOfNullFields);
		
		List<Map<String, Object>> dispenseRXDetails =  ((List<Map<String, Object>>) dispensingInterfaceOutBoundModelMap.get(DispensingInterfaceKeyConstants.DISPENSERXDETAILS.getKeyConstants()));
		validateDispensingOutBoundModelPropertyListMandatoryFields(dispenseRXDetails,listOfNullFields);

		if(!listOfNullFields.isEmpty()){
			StringBuilder customExceptionMessage = new StringBuilder();
			customExceptionMessage.append("<b>Following required data are missing: </b> ");
			for(String value : listOfNullFields) {
				customExceptionMessage.append("<br/>"+value.trim());
			}
			String message = customExceptionMessage.toString();
			throw new DispensingInterfaceCustomException("failed",message.trim(),0);
		}	
		return bFlag;
	}	
	/**
	 * API to validate the mandatory fields of sub property fields as these cannot blank or null  
	 * @param subPropertyList
	 * @return TreeSet
	 */
	private TreeSet<String> validateDispensingOutBoundModelPropertyListMandatoryFields(List<Map<String, Object>> subPropertyList,TreeSet<String> listOfNullFields){
		TreeSet<String> list=new TreeSet<>();  
		for(Map<String, Object> map : subPropertyList){
			for (Map.Entry<String, Object> entry : map.entrySet())
			{
				String key = entry.getKey();
				if(this.dispensingInterfaceMandatoryFieldsMap.containsKey(key)) {
					String value = entry.getValue() != null ? entry.getValue().toString() : "";
					if(value.isEmpty()){
						list.add(this.dispensingInterfaceMandatoryFieldsMap.get(key));
				    }
				}
			}
		}
		if(!list.isEmpty()) {
			listOfNullFields.addAll(list);
		}
		return listOfNullFields;
	}	
	/**
	 * API to go through each segment name and prepares the segment message.
	 * @throws DispensingInterfaceCustomException
	 * @param segmentKeyList
	 * @param dispensingInterfaceOutBoundModelMap
	 * @return List<String>
	 */
	private List<String> prepareDispensingHl7OutBoundMessage(String[] segmentKeyList, Map<String, Object> dispensingInterfaceOutBoundModelMap) throws DispensingInterfaceCustomException{
		StringBuilder finalHL7OutboundMsgBuf = new StringBuilder();
		
		for(int i = 0; i < segmentKeyList.length; i++){
			
			switch (segmentKeyList[i]) {
			case "MSH":
			case "PID":
			case "PV1":
			case "RXR":
				finalHL7OutboundMsgBuf.append(prepareHL7OutBoundMessage(dispensingInterfaceOutBoundModelMap, segmentKeyList[i])+"\n");
				break;
			case "ORC":
				finalHL7OutboundMsgBuf.append(prepareORCSegmentOutBoundMessage(dispensingInterfaceOutBoundModelMap,segmentKeyList[i], QTYTIMINGORCSEGMENTFIELDPOSITION)+"\n");
				break;
			case "RXE":
				finalHL7OutboundMsgBuf.append(prepareRXESegmentOutBoundMessage(dispensingInterfaceOutBoundModelMap,segmentKeyList[i], QTYTIMINGRXESEGMENTFIELDPOSITION)+"\n");
				break;
			case "RXC":
				finalHL7OutboundMsgBuf.append(prepareRXCSegmentOutBoundMessage(dispensingInterfaceOutBoundModelMap,segmentKeyList[i])+"\n");
				break;
			case "ZRX":
				zrxSegment =  dispensingInterfaceCacheService.getDispensingHl7SegmentDetailsMap().get(segmentKeyList[i]).toString();
				break;
			default:
				break;
			}
		}
		return generateFinalHl7OutBoundMessage(finalHL7OutboundMsgBuf,dispensingInterfaceOutBoundModelMap);
	}	
	/**
	 * This API finalizes the preparation of HL7 Outbound message on the basis of medication interface type.
	 * @param hL7OutBoundMessage
	 * @param dispensingInterfaceOutBoundModelMap
	 * @return List<String> 
	 */
	private List<String> generateFinalHl7OutBoundMessage(StringBuilder hL7OutBoundMessage, Map<String, Object> dispensingInterfaceOutBoundModelMap) {
		List<String> outBoundMessages = new ArrayList<>();
		if(("Aesynt").equalsIgnoreCase(dispensingInterfaceCacheService.getEnabledInterface())){
			List<String> zrxMessageOut = processZRXSegment(zrxSegment, dispensingInterfaceOutBoundModelMap);
			String hL7OutBoundMessageTemp =  hL7OutBoundMessage.toString();
			for(String zrxMessage : zrxMessageOut){
				outBoundMessages.add(hL7OutBoundMessageTemp.concat(zrxMessage).replace("{", "").replace("}", "").replace(ENCCHARS, ENCODINGCHAR).trim());
			}
		}
		if(("Pyxis").equalsIgnoreCase(dispensingInterfaceCacheService.getEnabledInterface())){
			outBoundMessages.add(hL7OutBoundMessage.toString().replace("{", "").replace("}", "").replace(ENCCHARS, ENCODINGCHAR).trim());
		}
		if(("MedDispense").equalsIgnoreCase(dispensingInterfaceCacheService.getEnabledInterface())){
			outBoundMessages.add(hL7OutBoundMessage.toString().replace("{", "").replace("}", "").replace(ENCCHARS, ENCODINGCHAR).trim());
		}

		return outBoundMessages;
	}
	/**
	 * This API process the formation ZRX segment message on the basis of order type.
	 * @param zrxSegment
	 * @param dispensingInterfaceOutBoundModelMap
	 * @return List<String>
	 */
	private List<String> processZRXSegment(String zrxSegment, Map<String, Object> dispensingInterfaceOutBoundModelMap ) {
		List<String> zrxMessage = new ArrayList();
		String messageType="";		
		messageType=dispensingInterfaceOutBoundModelMap.get("messageType").toString();
		matcher = pattern.matcher(zrxSegment);
		StringBuilder sb = new StringBuilder();
	    while(matcher.find()){
	    	sb.append(matcher.group()+",");
	    }
	    String[] keyList = sb.toString().replace("{", "").replace("}", "").split(",");
	    
		if((MessageTypeCodes.FIRSTORDERANDSCHEDULED.toString()).equalsIgnoreCase(messageType)){
			zrxMessage.add(prepareZRXSegment(keyList, MessageTypeCodes.FIRSTORDER.getMsgTypeCode(), dispensingInterfaceOutBoundModelMap));
			zrxMessage.add(prepareZRXSegment(keyList, MessageTypeCodes.SCHEDULED.getMsgTypeCode(), dispensingInterfaceOutBoundModelMap));
		}else if((MessageTypeCodes.FIRSTORDER.toString()).equalsIgnoreCase(messageType)){
			zrxMessage.add(prepareZRXSegment(keyList, MessageTypeCodes.FIRSTORDER.getMsgTypeCode(), dispensingInterfaceOutBoundModelMap));
		}
		else if((MessageTypeCodes.SCHEDULED.toString()).equalsIgnoreCase(messageType)){
			zrxMessage.add(prepareZRXSegment(keyList, MessageTypeCodes.SCHEDULED.getMsgTypeCode(), dispensingInterfaceOutBoundModelMap));

		}
		return zrxMessage;
	}	
	/**
	 * This API preapares ZRX segment message.
	 * @param keyList
	 * @param messageTypeCode
	 * @param dispensingInterfaceOutBoundModelMap
	 * @return String
	 */
	private String prepareZRXSegment(String[] keyList,String messageTypeCode, Map<String, Object> dispensingInterfaceOutBoundModelMap){
		StringBuilder message = new StringBuilder();
		message.append(zrxSegment);
		for(String key : keyList){
	    	if(("MESSAGETYPE").equalsIgnoreCase(key)){
		    	message.replace(message.indexOf(key), message.indexOf(key) + key.length(), messageTypeCode);
	    	}else{
	    		String value = dispensingInterfaceOutBoundModelMap.get(key) != null ? dispensingInterfaceOutBoundModelMap.get(key).toString() : "";
		    	message.replace(message.indexOf(key), message.indexOf(key) + key.length(), value);
	    	}
	    }	
		return message.toString();
	}
	/**
	 * API to parse and create the segment of HL7 Outbound messages for MSH, PID, PV1, and RXR segments
	 * @throws DispensingInterfaceCustomException
	 * @param dispensingInterfaceOutBoundModelMap
	 * @param segmentName
	 * @return String
	 */
	private String prepareHL7OutBoundMessage(Map<String, Object> dispensingInterfaceOutBoundModelMap, String segmentName) throws DispensingInterfaceCustomException{
		StringBuilder message = new StringBuilder();
		if(dispensingInterfaceCacheService.getDispensingHl7SegmentDetailsMap().containsKey(segmentName)){
			message.append(dispensingInterfaceCacheService.getDispensingHl7SegmentDetailsMap().get(segmentName));
		}else{
			throw new DispensingInterfaceCustomException(DispensingInterfaceStatus.INVALIDSEGMENTS);
		}		
		matcher = pattern.matcher(dispensingInterfaceCacheService.getDispensingHl7SegmentDetailsMap().get(segmentName).toString());
		StringBuilder sb = new StringBuilder();
	    while(matcher.find()){
	    	sb.append(matcher.group()+",");
	    }
	    String[] keyList = sb.toString().replace("{", "").replace("}", "").split(",");
	    
	    for(String key : keyList){
	    	String value = dispensingInterfaceOutBoundModelMap.get(key) != null ? dispensingInterfaceOutBoundModelMap.get(key).toString() : "";
	    	message.replace(message.indexOf(key), message.indexOf(key) + key.length(), value);
	    }
	    return message.toString();
	}	
	/**
	 * API to parse and create the segment of HL7 Outbound messages for ORC segment
	 * @param dispensingInterfaceOutBoundModelMap
	 * @param segmentName
	 * @param index
	 * @return String
	 */
	private String prepareORCSegmentOutBoundMessage(Map<String, Object> dispensingInterfaceOutBoundModelMap,String segmentName, int index){		
		List<Map<Object, Object>> repeatedElementDataList = (List<Map<Object, Object>>)dispensingInterfaceOutBoundModelMap.get(DispensingInterfaceKeyConstants.RXFREQDETAILS.getKeyConstants());		
		StringBuilder message = new StringBuilder();
		message.append(dispensingInterfaceCacheService.getDispensingHl7SegmentDetailsMap().get(segmentName).toString().replace("{", "").replace("}", ""));
		String[] segmentPipeArray = message.toString().trim().split("\\|");       
        for(int i=1; i< segmentPipeArray.length;i++){
        	if(i==index){
        		String indexMessage = generateQtyTimingFieldSegment(dispensingInterfaceOutBoundModelMap,repeatedElementDataList,segmentPipeArray[index]);
        		message.replace(message.indexOf(segmentPipeArray[index]),message.indexOf(segmentPipeArray[index]) +segmentPipeArray[index].length(), indexMessage);
        	}
        	else{
        		if(segmentPipeArray[i].contains("^")){
        			String capSeperatedSegmentFieldData=createORCSegmentCapSeperatedFieldData(segmentPipeArray[i],dispensingInterfaceOutBoundModelMap);
        			message.replace(message.indexOf(segmentPipeArray[i]), message.indexOf(segmentPipeArray[i]) + segmentPipeArray[i].length(), capSeperatedSegmentFieldData);
        		}
        		else{
        			String key = segmentPipeArray[i];
        			String value = dispensingInterfaceOutBoundModelMap.get(key) != null ? dispensingInterfaceOutBoundModelMap.get(key).toString() : "";
        			message.replace(message.indexOf(key), message.indexOf(key) + key.length(), value);
				}
        	}
        }
        return message.toString();
	}
	/**
	 * API to parse and create the segment of HL7 Outbound messages for Qty Timing Field Segment
	 * @param dispensingInterfaceOutBoundModelMap
	 * @param indexOfRepeatedValue
	 * @param repeatedElementDataList
	 * @param indexedElementTemplate
	 * @return String
	 */
	private String generateQtyTimingFieldSegment(Map<String, Object> dispensingInterfaceOutBoundModelMap,List<Map<Object, Object>> repeatedElementDataList,String indexedElementTemplate){		
		StringBuilder indexedPipeTemplate = new StringBuilder();
		indexedPipeTemplate.append(indexedElementTemplate);
		for(int j=0; j< repeatedElementDataList.size();j++){

			String [] qtyTimingCapArray = null;
			if(j == 0){
				 qtyTimingCapArray =  indexedPipeTemplate.toString().split("\\^|\\&");
			}else{
				String [] appendedCapArray =  indexedPipeTemplate.toString().split("~");
				qtyTimingCapArray =  appendedCapArray[j].split("\\^|\\&");
			}
			for(int k = 1; k < qtyTimingCapArray.length; k++){
				String key = qtyTimingCapArray[k];
				String value = dispensingInterfaceOutBoundModelMap.get(key)!= null ? dispensingInterfaceOutBoundModelMap.get(key).toString():getRxFrequencyDetails(repeatedElementDataList, key, j);
				indexedPipeTemplate.replace(indexedPipeTemplate.indexOf(key), indexedPipeTemplate.indexOf(key) + key.length(), value);
			}
			if(j <  repeatedElementDataList.size()-1){
				indexedPipeTemplate.append("~"+indexedElementTemplate);
			}			
		}
		return indexedPipeTemplate.toString();
	}	
	/**
	 * API to get the Rx Frequency details.
	 * @param rxFreqDetail
	 * @param key
	 * @param index
	 * @return String
	 */
	private String getRxFrequencyDetails(List<Map<Object, Object>> rxFreqDetail, String key, int index){
		String value= "";
		Map<Object, Object> obj  = rxFreqDetail.get(index);
	    for (Map.Entry<Object, Object> entry : obj.entrySet()) {
	    	if(key.equalsIgnoreCase((String) entry.getKey())){
	    		value = entry.getValue().toString();
	    		break;
	    	}
	    }
		return value;
	}	
	/**
	 * API to parse and create the segment of HL7 Outbound messages for RXE Segment 
	 * @throws DispensingInterfaceCustomException
	 * @param dispensingInterfaceOutBoundModelMap
	 * @param segmentName
	 * @param index
	 * @return String
	 */
	public String prepareRXESegmentOutBoundMessage(Map<String, Object> dispensingInterfaceOutBoundModelMap,String segmentName, int index) throws DispensingInterfaceCustomException{		
		List<Map<Object, Object>> repeatedElementDataList = (List<Map<Object, Object>>) dispensingInterfaceOutBoundModelMap.get(DispensingInterfaceKeyConstants.RXFREQDETAILS.getKeyConstants());
		Map<String, Object> primaryDrugDetailMap=getPrimaryDrugDetails(dispensingInterfaceOutBoundModelMap);	
		StringBuilder message = new StringBuilder();
		message.append(dispensingInterfaceCacheService.getDispensingHl7SegmentDetailsMap().get(segmentName).toString().replace("{", "").replace("}", ""));
		String[] segmentPipeArray = message.toString().trim().split("\\|");
       
        for(int i=1; i< segmentPipeArray.length;i++){
        	if(i==index){
        		String indexMessage = generateQtyTimingFieldSegment(dispensingInterfaceOutBoundModelMap,repeatedElementDataList,segmentPipeArray[index]);
        		message.replace(message.indexOf(segmentPipeArray[index]),message.indexOf(segmentPipeArray[index]) +segmentPipeArray[index].length(), indexMessage);
        	}
        	else{
    			String capSeperatedSegmentFieldData=getRXESegmentCapSeperatedFieldData(segmentPipeArray[i],dispensingInterfaceOutBoundModelMap,primaryDrugDetailMap);
    			message.replace(message.indexOf(segmentPipeArray[i]), message.indexOf(segmentPipeArray[i]) + segmentPipeArray[i].length(), capSeperatedSegmentFieldData);
        	}
        }
        return message.toString();
	}	
	/**
	 * API to get Primary drug details.
	 * @throws DispensingInterfaceCustomException
	 * @param dispensingInterfaceOutBoundModelMap
	 * @return Map
	 */
	private Map<String, Object> getPrimaryDrugDetails(Map<String, Object> dispensingInterfaceOutBoundModelMap) throws DispensingInterfaceCustomException{
		Map<String, Object> primaryDrugDetailMap= new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		List<TreeMap<String, Object>> repeatedElementDataList = null;
		int mapIndex = -1;
		repeatedElementDataList = (List<TreeMap<String, Object>>) dispensingInterfaceOutBoundModelMap.get(DispensingInterfaceKeyConstants.DISPENSERXDETAILS.getKeyConstants());
		if(repeatedElementDataList != null){
			for(Map<String, Object> map: repeatedElementDataList){
				if(map.containsKey(DispensingInterfaceKeyConstants.ISPRIMARY.getKeyConstants())){
					mapIndex++;
					if(("yes").equalsIgnoreCase(map.get(DispensingInterfaceKeyConstants.ISPRIMARY.getKeyConstants()).toString())){
						primaryDrugDetailMap.putAll(repeatedElementDataList.get(mapIndex));
						break;
					}
				}
			}
			}else{
				throw new DispensingInterfaceCustomException(DispensingInterfaceStatus.NOPRIMARYDRUGDETAIL);
			}
		return primaryDrugDetailMap;
	}	
	
	/**
	 * API to skip Primary drug details.
	 * @throws DispensingInterfaceCustomException
	 * @param drugDetailMapList
	 * @return 
	 */
	private List<Map<Object, Object>> skipPrimaryDrugDetails(List<Map<Object, Object>> drugDetailMapList) throws DispensingInterfaceCustomException{
		int mapIndex = -1;
		if(drugDetailMapList != null){
		for(Map<Object, Object> map: drugDetailMapList){
			if(map.containsKey(DispensingInterfaceKeyConstants.ISPRIMARY.getKeyConstants())){
				mapIndex++;
				if(("yes").equalsIgnoreCase(map.get(DispensingInterfaceKeyConstants.ISPRIMARY.getKeyConstants()).toString())){
					drugDetailMapList.remove(mapIndex);
					break;
				}
			}
		}
		}else{
			throw new DispensingInterfaceCustomException(DispensingInterfaceStatus.NOPRIMARYDRUGDETAIL);
		}
		return drugDetailMapList;
	}
	/**
	 * API to parse and create the segment of HL7 Outbound messages for RXC Segment 
	 * @throws DispensingInterfaceCustomException - NOPRIMARYDRUGDETAIL
	 * @param dispensingInterfaceOutBoundModelMap
	 * @param segmentName
	 * @return String
	 */
	private String prepareRXCSegmentOutBoundMessage(Map<String, Object> dispensingInterfaceOutBoundModelMap,String segmentName) throws DispensingInterfaceCustomException{		
		List<Map<Object, Object>> drugDetailMapList = null; 	
		if(("1").equals(dispensingInterfaceOutBoundModelMap.get("rDispenseOrderType").toString())){
			drugDetailMapList = (List<Map<Object, Object>>) dispensingInterfaceOutBoundModelMap.get(DispensingInterfaceKeyConstants.DISPENSERXDETAILS.getKeyConstants());
			skipPrimaryDrugDetails(drugDetailMapList);
		}
		else{
			drugDetailMapList = (List<Map<Object, Object>>) dispensingInterfaceOutBoundModelMap.get(DispensingInterfaceKeyConstants.DISPENSERXDETAILS.getKeyConstants());
		}
		StringBuilder finalMessage = new StringBuilder();		
		String[] segmentPipeArray = dispensingInterfaceCacheService.getDispensingHl7SegmentDetailsMap().get(segmentName).toString().replace("{", "").replace("}", "").trim().split("\\|");		
		for(int i=0; i< drugDetailMapList.size();i++){
			StringBuilder message = new StringBuilder();
			message.append(dispensingInterfaceCacheService.getDispensingHl7SegmentDetailsMap().get(segmentName).toString().replace("{", "").replace("}", ""));			
			for(int j = 1; j< segmentPipeArray.length; j++){
				String capSeperatedSegmentFieldData=createRXCSegmentCapSeperatedFieldData(i,segmentPipeArray[j],drugDetailMapList,dispensingInterfaceOutBoundModelMap);
				if("ADDITIVE".equalsIgnoreCase(capSeperatedSegmentFieldData)) {
					capSeperatedSegmentFieldData ="A" ;
				}else {
					capSeperatedSegmentFieldData=("BASE").equalsIgnoreCase(capSeperatedSegmentFieldData)? "B":capSeperatedSegmentFieldData;
				}
				message.replace(message.indexOf(segmentPipeArray[j]), message.indexOf(segmentPipeArray[j]) + segmentPipeArray[j].length(), capSeperatedSegmentFieldData);
			}
			finalMessage.append(message+"\n");
		}
	return finalMessage.toString();
	}
	/**
	 * API to get Rx drug details.
	 * @param rxDrugDetail
	 * @param key
	 * @param index
	 * @return String
	 */
	private String getRxDrugDetails(List<Map<Object, Object>> rxDrugDetail, String key, int index){
		String value= "";
		Map<Object, Object> obj  = rxDrugDetail.get(index);
		for (Map.Entry<Object, Object> entry : obj.entrySet()) {
			if(key.equalsIgnoreCase((String) entry.getKey())){
				value = entry.getValue().toString();
				break;
			}
		}
		return value;
	}
	/**
	 * API to parse and create the segment of HL7 Outbound messages for ORC Segment 
	 * @param capSeperatedSegmentField
	 * @param dispensingInterfaceOutBoundModelMap
	 * @return String
	 */
	private String createORCSegmentCapSeperatedFieldData(String capSeperatedSegmentField,Map<String, Object> dispensingInterfaceOutBoundModelMap)
	{
		StringBuilder capSeperatedSegmentFieldBuffer = new StringBuilder();
		capSeperatedSegmentFieldBuffer.append(capSeperatedSegmentField);
		String[] segmentCapSepArray = capSeperatedSegmentField.split("\\^");
		for(int k=0; k < segmentCapSepArray.length; k++)
		{
			String key = segmentCapSepArray[k];
			String value = dispensingInterfaceOutBoundModelMap.get(key) != null ? dispensingInterfaceOutBoundModelMap.get(key).toString() : "";					
			capSeperatedSegmentFieldBuffer.replace(capSeperatedSegmentFieldBuffer.indexOf(key), capSeperatedSegmentFieldBuffer.indexOf(key) + key.length(), value);
		}
		return capSeperatedSegmentFieldBuffer.toString();
	}	
	/**
	 * API to parse and get the segment of HL7 Outbound messages for RXE Segment 
	 * @param capSeperatedSegmentField
	 * @param dispensingInterfaceOutBoundModelMap
	 * @param primaryDrugDetailMap
	 * @return String
	 */
	private String getRXESegmentCapSeperatedFieldData(String capSeperatedSegmentField,Map<String, Object> dispensingInterfaceOutBoundModelMap,Map<String, Object> primaryDrugDetailMap) {
	    String segmemtData="";
		if(capSeperatedSegmentField.contains("^")){
			segmemtData=createRXESegmentCapSeperatedFieldData(capSeperatedSegmentField,dispensingInterfaceOutBoundModelMap,primaryDrugDetailMap);
		}
		else{
			String key = capSeperatedSegmentField;	
			if(null!=dispensingInterfaceOutBoundModelMap.get(key)) {
				segmemtData=dispensingInterfaceOutBoundModelMap.get(key).toString();
			}else {
				segmemtData=primaryDrugDetailMap.get(key)!= null ? primaryDrugDetailMap.get(key).toString(): "";
			}
		}
		return segmemtData;
	}
	 /**
	 API to parse and create the segment of HL7 Outbound messages for RXE Segment 
	 * @param capSeperatedSegmentField
	 * @param dispensingInterfaceOutBoundModelMap
	 * @param primaryDrugDetailMap
	 * @return String
	 */
	private String createRXESegmentCapSeperatedFieldData(String capSeperatedSegmentField,Map<String, Object> dispensingInterfaceOutBoundModelMap,Map<String, Object> primaryDrugDetailMap) {
	    String segmemtData="";
		if(capSeperatedSegmentField.contains("^")){
			StringBuilder capSeperatedSegmentFieldBuffer = new StringBuilder();
			capSeperatedSegmentFieldBuffer.append(capSeperatedSegmentField);
			String[] segmentCapSepArray = capSeperatedSegmentField.split("\\^");
			for(int k=0; k < segmentCapSepArray.length; k++)
			{
				String key = segmentCapSepArray[k];
				String value =""; 
				if(null != dispensingInterfaceOutBoundModelMap.get(key)) {
					 value = dispensingInterfaceOutBoundModelMap.get(key).toString();
				}else {
					value=primaryDrugDetailMap.get(key)!= null ? primaryDrugDetailMap.get(key).toString(): "";
				}
				capSeperatedSegmentFieldBuffer.replace(capSeperatedSegmentFieldBuffer.indexOf(key), capSeperatedSegmentFieldBuffer.indexOf(key) + key.length(), value);
			}
			segmemtData=capSeperatedSegmentFieldBuffer.toString();
		}
		return segmemtData;
	}
	/**
	 * API to parse and create the segment of HL7 Outbound messages for RXC Segment
	 * @param index
	 * @param capSeperatedSegmentField
	 * @param drugDetailMapList
	 * @return String
	 */
	private String createRXCSegmentCapSeperatedFieldData(int index,String capSeperatedSegmentField, List<Map<Object, Object>> drugDetailMapList,Map<String, Object> dispensingInterfaceOutBoundModelMap)
	{	
		StringBuilder capSeperatedSegmentFieldBuffer = new StringBuilder();
		capSeperatedSegmentFieldBuffer.append(capSeperatedSegmentField);
		if(capSeperatedSegmentField.contains("^")){
			
			String[] segmentCapSepArray = capSeperatedSegmentField.split("\\^");		
			for(int k=0; k < segmentCapSepArray.length; k++){
				String key = segmentCapSepArray[k];
				String value = dispensingInterfaceOutBoundModelMap.get(key)!= null ? dispensingInterfaceOutBoundModelMap.get(key).toString():getRxDrugDetails(drugDetailMapList, key, index);			
				capSeperatedSegmentFieldBuffer.replace(capSeperatedSegmentFieldBuffer.indexOf(key), capSeperatedSegmentFieldBuffer.indexOf(key) + key.length(), value);
			}
		}
		else{
			String key = capSeperatedSegmentField;
			String value = dispensingInterfaceOutBoundModelMap.get(key)!= null ? dispensingInterfaceOutBoundModelMap.get(key).toString():getRxDrugDetails(drugDetailMapList, key, index);
			capSeperatedSegmentFieldBuffer.replace(capSeperatedSegmentFieldBuffer.indexOf(key), capSeperatedSegmentFieldBuffer.indexOf(key) + key.length(), value);
		}
		return capSeperatedSegmentFieldBuffer.toString();
	}	
	/**
	 * API to log the final HL7 Outbound Message
	 * @param dispensingHl7OutBoundMessageList
	 * @param dispensingInterfaceOutBoundModel
	 */
	private boolean logDispensingHl7OutBoundMessage(List<String> dispensingHl7OutBoundMessageList, DispensingInterfaceOutBoundModel dispensingInterfaceOutBoundModel, int logId) throws DispensingInterfaceCustomException{		
			boolean bFlag = false;
			Map<String, Object> paramMap = null;
			String messageType = null;
			
			for(String dispensingHl7OutBoundMessage : dispensingHl7OutBoundMessageList){
				String messageUniqueId=dispensingUtil.generateMessageId();
				paramMap = new LinkedHashMap<>();
				paramMap.put("messageid", messageUniqueId);
				paramMap.put("dispensinginterfaceid", dispensingInterfaceCacheService.getDispensingInterfaceModel().getInterfaceId());
				paramMap.put("dispenseordertype",dispensingInterfaceOutBoundModel.getOrderType().toString());
				if((("FIRSTORDERANDSCHEDULED").equalsIgnoreCase(dispensingInterfaceOutBoundModel.getMessageType().toString())) && ("Aesynt").equalsIgnoreCase(dispensingInterfaceCacheService.getEnabledInterface())){
					if(null == messageType)
						messageType = "FIRSTORDER";
					else{
						messageType=("FIRSTORDER").equalsIgnoreCase(messageType)? "SCHEDULED":"";
					}
					paramMap.put("dispensemsgtype", messageType);
				}else{
					paramMap.put("dispensemsgtype", dispensingInterfaceOutBoundModel.getMessageType().toString());
				}
				paramMap.put("dispensecomponenttype", dispensingInterfaceOutBoundModel.getComponentType().toString());
				paramMap.put("hl7message", dispensingHl7OutBoundMessage.replace("RMESSAGECONTROLID", messageUniqueId));
				paramMap.put("status", "pending");
				paramMap.put("createddatetime", DateUtil.getTodaysDate(dateFormat));
				paramMap.put("loggedInUser", dispensingInterfaceOutBoundModel.getrLoggedInUser());
				paramMap.put("patientid", dispensingInterfaceOutBoundModel.getrPatientId());
				paramMap.put("rxorderid", dispensingInterfaceOutBoundModel.getrRxOrderNumber());
				paramMap.put("encounterid", dispensingInterfaceOutBoundModel.getrPatientVisitNumber());
				paramMap.put("facilityid", dispensingInterfaceOutBoundModel.getFacilityId());
				paramMap.put("orderingproviderid", dispensingInterfaceOutBoundModel.getrOrderingProviderID());
				paramMap.put("dispensinginterfacedetailid", logId);
				
				if(dispensingIterfaceOutBoundDao.logDispensingHl7OutBoundMessage(paramMap)){
					bFlag = true;
					String messageLogStatus = DispensingInterfaceKeyConstants.SUCCESS.getKeyConstants();
					updateLogDispensingInterfaceMsgContent(logId , messageLogStatus,"");
					responseMap.put(DispensingInterfaceKeyConstants.STATUS.getKeyConstants(), "success");
			    	responseMap.put(DispensingInterfaceKeyConstants.MESSAGE.getKeyConstants(), "Generate HL7 outbound message successfully");
				}
			}
			return bFlag;	
	}
	
	/**
	 * Logs the received Message from pharmacy
	 * @param dispensingInterfaceOutBoundModel
	 * @param messageContent
	 * @return int  - 0 if data not inserted
	 * 				- inserted logid, if data is inserted   
	 * @throws DispensingInterfaceCustomException 
	 */
	private int logDispensingInterfaceMsgContent(DispensingInterfaceOutBoundModel dispensingInterfaceOutBoundModel, String messageContent) throws DispensingInterfaceCustomException {
		int logId = 0;
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("context","PVQ");
		paramMap.put("data", messageContent);
		paramMap.put(DispensingInterfaceKeyConstants.STATUS.getKeyConstants(), "received");
		paramMap.put("createddate",  DateUtil.getTodaysDate(dateFormat));
		paramMap.put("modifieddate", DateUtil.getTodaysDate(dateFormat) );
		paramMap.put("ishl7msgcreated", 0);
		paramMap.put("medorderid", dispensingInterfaceOutBoundModel.getrRxOrderNumber());
		logId = dispensingIterfaceOutBoundDao.logDispensingInterfaceMsgContent(paramMap);
		if(logId == 0) {
			throw new DispensingInterfaceCustomException(DispensingInterfaceStatus.INTERFACELOGFAILED);
		}
		return logId;
	}
	
	/**
	 * Update the hl7 message log status whether received message is processed or not - status can be failed or success
	 * @param logId
	 * @param messageLogStatus
	 * @return true   - if status is updated
	 * 		   false  - if status is not updated
	 */
	private boolean updateLogDispensingInterfaceMsgContent(int logId, String messageLogStatus,String errorMessage) {
		boolean status = false;
		Map<String, Object> paramMap = new LinkedHashMap<>();
		int ishl7msgCreated = 0;
		if(DispensingInterfaceKeyConstants.SUCCESS.getKeyConstants().equalsIgnoreCase(messageLogStatus))
			ishl7msgCreated = 1;
		
		paramMap.put(DispensingInterfaceKeyConstants.STATUS.getKeyConstants(), messageLogStatus);
		paramMap.put("modifieddate", DateUtil.getTodaysDate(dateFormat));
		paramMap.put("ishl7msgcreated", ishl7msgCreated);
		paramMap.put("errormessage", errorMessage);
		paramMap.put("id", logId);
		if(dispensingIterfaceOutBoundDao.updateLogDispensingInterfaceMsgContent(paramMap)) {
			status = true;
			// logic
		}
		return status;
	}

}