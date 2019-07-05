package inpatientWeb.pharmacy.interfaces.outbound.serviceImpl;

import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.SqlTranslator;
import inpatientWeb.Global.service.EcwAppContext;
import inpatientWeb.pharmacy.interfaces.outbound.dao.DispensingInterfaceOutBoundDAO;
import inpatientWeb.pharmacy.interfaces.service.DispensingInterfaceCacheService;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.DispensingInterfaceACKMessageConstants;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.DispensingInterfaceKeyConstants;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.DispensingInterfaceStatus;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.HL7MessageAckStatusCodes;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.Hl7MessageEnclosingCodes;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceCustomException;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceUtil;
import inpatientWeb.utils.DateUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

public class DispensingInterfaceOutBoundEngine implements StatefulJob{

	private static final Logger logger = LogManager.getLogger(DispensingInterfaceOutBoundEngine.class);
	private String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
	private static final int DEFAULTSOCKETREADTIMEOUT = 30000;
	private int socketReadTimeOut =0;
	private boolean isPersistentMode= false;
	private Socket mSocket= null;
	private char eofMessageCHAR;
	private char crCHAR;
	private char sofMessageCHAR;
	private String newLine = "\n";

	@Autowired
	private DispensingInterfaceOutBoundDAO dispensingIterfaceOutBoundDao;
	
	@Autowired
	private DispensingInterfaceCacheService dispensingInterfaceCacheService;
	
	private DispensingInterfaceUtil dispensingInterfaceUtil;
	
    public DispensingInterfaceOutBoundEngine(){
	      super();
	      
	     if(null==dispensingIterfaceOutBoundDao)
	    	 dispensingIterfaceOutBoundDao=(DispensingInterfaceOutBoundDAO)EcwAppContext.getObject(DispensingInterfaceOutBoundDAO.class);
	    	 
	     if(null==dispensingInterfaceCacheService)
	    	 dispensingInterfaceCacheService=(DispensingInterfaceCacheService)EcwAppContext.getObject(DispensingInterfaceCacheService.class);
	     
	     if(null==dispensingInterfaceUtil)
	    	 dispensingInterfaceUtil=new DispensingInterfaceUtil();
	     
    }

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			logger.debug("[DispensingInterfaceOutBoundEngine] execute - Initialized Successfully : "+ new java.util.Date());

			if(dispensingInterfaceCacheService.setDispensingInterfaceCache()){
				String readTimeOut=dispensingInterfaceCacheService.getDispensingInterfaceItemKeyValue("Read_Timeout");
				
				if("".equals(readTimeOut)){
					socketReadTimeOut=DEFAULTSOCKETREADTIMEOUT;
				}
				else {
					socketReadTimeOut = "0".equals(readTimeOut)?DEFAULTSOCKETREADTIMEOUT:Integer.parseInt(readTimeOut);
				}
				isPersistentMode="yes".equalsIgnoreCase(dispensingInterfaceCacheService.getDispensingInterfaceItemKeyValue("EnablePersistentConnection"));
				
				setConstantSpecialChars();
				processOutBoundMessages();
		    }
			else{
				logger.error("[DispensingInterfaceOutBoundEngine] execute - Failed to start the job. [Reason - Dispensing interface cache could not be initalized]: "+ new java.util.Date());
			}
			logger.debug("[DispensingInterfaceOutBoundEngine] execute - Completed Successfully : "+ new java.util.Date());
			
		} catch(Exception e) {
			logger.error("[DispensingInterfaceOutBoundEngine] execute -" + e.getMessage());
		}
	}
	/**
	 * API to convert decimal value to special characters and set it to respective references
	 * @return boolean
	 * @throws DispensingInterfaceCustomException
	 */
	private boolean setConstantSpecialChars() {
		boolean bFlag = false;

		int startOfMessageCharDecimalValue = null!=dispensingInterfaceCacheService.getDispensingInterfaceItemKeyValue("Start_Of_Message_Char")?dispensingInterfaceUtil.getOctToDecimalValue(dispensingInterfaceCacheService.getDispensingInterfaceItemKeyValue("Start_Of_Message_Char").trim(), Hl7MessageEnclosingCodes.STARTOFMESSAGECHAR.toString().trim()):dispensingInterfaceUtil.getOctToDecimalValue("", Hl7MessageEnclosingCodes.STARTOFMESSAGECHAR.toString().trim());
		sofMessageCHAR= (char)startOfMessageCharDecimalValue;

		int endOfMessageCharDecimalValue = null!=dispensingInterfaceCacheService.getDispensingInterfaceItemKeyValue("End_Of_Message_Char")?dispensingInterfaceUtil.getOctToDecimalValue(dispensingInterfaceCacheService.getDispensingInterfaceItemKeyValue("End_Of_Message_Char").trim(), Hl7MessageEnclosingCodes.ENDOFMESSAGECHAR.toString().trim()):dispensingInterfaceUtil.getOctToDecimalValue("", Hl7MessageEnclosingCodes.ENDOFMESSAGECHAR.toString().trim());
		eofMessageCHAR= (char)endOfMessageCharDecimalValue;
		
		int segmentSeperatorCharDecimalValue = null!=dispensingInterfaceCacheService.getDispensingInterfaceItemKeyValue("Segment_Seperator_Char")?dispensingInterfaceUtil.getOctToDecimalValue(dispensingInterfaceCacheService.getDispensingInterfaceItemKeyValue("Segment_Seperator_Char").trim(), Hl7MessageEnclosingCodes.CARRIAGERETURNCHAR.toString().trim()):dispensingInterfaceUtil.getOctToDecimalValue("", Hl7MessageEnclosingCodes.CARRIAGERETURNCHAR.toString().trim());
		crCHAR= (char)segmentSeperatorCharDecimalValue;
		
		if(sofMessageCHAR > 0 && eofMessageCHAR > 0 && crCHAR > 0) 
			bFlag = true;
		
		return bFlag;
	}
	
	/**
	 * This API works as engine - It gets the enabled medication interface and its details like connection ip and port,
	 * then it gets all the pending HL7 outbound message to transmit it to enabled medication interface and finally it logs the received Acknowledgment of all transmitted messages.
	 * @return boolean
	 */
	private boolean processOutBoundMessages() {
		boolean bFlag = false;
		Map<Integer, Object> hl7OutBoundMessagesOut = new TreeMap<>();
		List<Map<String, Object>> hl7OutBoundACKMessagesList = new ArrayList<>();
		List<Map<String, Object>> ackMessageParamList = null;
		try {
			//get pending HL7 outbound messages along with its id and store the same in list

			hl7OutBoundMessagesOut = getPendingOutBoundMessages();
			
			if((null != hl7OutBoundMessagesOut ) && (!hl7OutBoundMessagesOut.isEmpty())) {
				//transmit the list of pending HL7 outbound messages and get the ack/nack of each message
				for (Map.Entry<Integer, Object> entry : hl7OutBoundMessagesOut.entrySet())
				{
					logger.debug("[DispensingInterfaceOutBoundEngine] processOutBoundMessages - transmission of ("+hl7OutBoundMessagesOut.size()+") pending HL7 Outbound messages initiated.");
					Map<String, Object> hl7OutBoundACKMessageMap  = transmitOutboundMessage(entry);
					hl7OutBoundACKMessagesList.add(hl7OutBoundACKMessageMap);
				}
				if(!(hl7OutBoundACKMessagesList.isEmpty())) {
					ackMessageParamList = processHL7AckOutboundMessage(hl7OutBoundACKMessagesList);
				}
				//update received ACK/NACK messages in batch
				if((null != ackMessageParamList ) && (!ackMessageParamList.isEmpty())) {
					logOutboundMessageACK(ackMessageParamList);
					bFlag = true;
				}
			}else {
				bFlag = true;
			}
		}
		catch (Exception e) {
	    	logger.error("[DispensingInterfaceOutBoundEngine] processOutBoundMessages -" + e.getMessage());
		}
		finally {
			if (!isPersistentMode)
				closeSocket();
		}
		return bFlag;
	}

	/**
	 * This API returns all the pending HL7 Outbound Messages along with its id
	 * @return Map
	 */
	private Map<Integer, Object> getPendingOutBoundMessages() {
		Map<Integer, Object> hl7OutBoundMessages = null;
		try{
			hl7OutBoundMessages = dispensingIterfaceOutBoundDao.getOutBoundMessages(dispensingInterfaceCacheService.getDispensingInterfaceModel().getInterfaceId());
		}catch(DataAccessException ex){
			logger.error("[DispensingInterfaceOutBoundEngine] getPendingOutBoundMessages -" + ex.getMessage());
		}
		return hl7OutBoundMessages;
	}	
	/**
	 * This API transmits(write) HL7 Outbound message to enabled medication interface and reads the received ACK/NACK from interface which gets added to list along with its other details.
	 * @param hl7OutBoundMessages
	 * @return List<Map>
	 */
	private Map<String, Object> transmitOutboundMessage(Map.Entry<Integer, Object> entry) {
		Map<String, Object> map  = new HashMap<>();
		BufferedReader in = null;
		String ackMessage = "";
		String ackreceiveddatetime = "";
		ackreceiveddatetime = SqlTranslator.getDefaultDateTime();
		map.put("id", entry.getKey());
		map.put("status", DispensingInterfaceKeyConstants.PENDING.getKeyConstants());
		map.put("ack", DispensingInterfaceACKMessageConstants.ACKRECEIVEDFAIL.getAckConstants()); 
		map.put(DispensingInterfaceKeyConstants.ACKMESSAGE.getKeyConstants(), ackMessage);
		map.put(DispensingInterfaceKeyConstants.ACKSTATUS.getKeyConstants(), "");
		map.put(DispensingInterfaceKeyConstants.RECEIVEDDATETIME.getKeyConstants(), ackreceiveddatetime);
		map.put(DispensingInterfaceKeyConstants.ERRORMESSAGE.getKeyConstants(), "Error occured while adding block notation to pending HL7 Outbound Message - id : "+entry.getKey());
		map.put("sentdatetime", DateUtil.getTodaysDate(dateTimeFormat));

		try{
			if(!isPersistentMode)
				closeSocket();
				
			createSocketConn();
			mSocket.setSoTimeout(socketReadTimeOut);
			logger.debug("[DispensingInterfaceOutBoundEngine] transmitOutboundMessage - transmission of message id : "+entry.getKey()+" initiated.");

            if(writeMessage(entry)){
            	
            	in = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
    			ackreceiveddatetime = DateUtil.getTodaysDate(dateTimeFormat);
    			
    			ackMessage = readMessage(in);
    			
    			map.put(DispensingInterfaceKeyConstants.RECEIVEDDATETIME.getKeyConstants(), ackreceiveddatetime);
    			if((ackMessage.length() == 0 ) || ("null".equals(ackMessage))){
    				ackMessage = "";
    				map.put(DispensingInterfaceKeyConstants.ERRORMESSAGE.getKeyConstants(), "Received acknowledgement message is blank or null.");
    				logger.error("Error occurred while reading received acknowledgement message.");
    			}
    			map.put("ack", DispensingInterfaceACKMessageConstants.ACKRECEIVEDSUCCESS.getAckConstants());
    			map.put(DispensingInterfaceKeyConstants.ACKMESSAGE.getKeyConstants(), ackMessage);
            }
			logger.debug("[DispensingInterfaceOutBoundEngine] transmitOutboundMessage - transmission of message id : "+entry.getKey()+" completed successfully.");
		}catch (SocketTimeoutException e) {
			map.put(DispensingInterfaceKeyConstants.RECEIVEDDATETIME.getKeyConstants(), ackreceiveddatetime);
			map.put(DispensingInterfaceKeyConstants.ERRORMESSAGE.getKeyConstants(), "Acknowledgement message not received - Session time out.");
	    	logger.error("[DispensingInterfaceOutBoundEngine] transmitOutboundMessage -" + e.getMessage());
		}
		catch (SocketException e) {
			map.put(DispensingInterfaceKeyConstants.RECEIVEDDATETIME.getKeyConstants(), ackreceiveddatetime);
			map.put(DispensingInterfaceKeyConstants.ERRORMESSAGE.getKeyConstants(), e.getMessage());
			closeSocket();
		}
		catch (IOException  e){
			map.put(DispensingInterfaceKeyConstants.ERRORMESSAGE.getKeyConstants(), e.getMessage());
	    	logger.error("[DispensingInterfaceOutBoundEngine] transmitOutboundMessage -" + e.getMessage());
		}
		return map;
	}
	/**
	 * This API checks socket object and if it is not null then it closes the connection 
	 *  @return True  - if object is not null and socket is closed
	 *  	    False - if object is null
	 */
	private boolean closeSocket() {
		boolean bFlag = false;
		if(null != mSocket){
			try {
				mSocket.close();
				bFlag = true;
			} catch (IOException e) {
				logger.error(e);
			}
		}
		return bFlag;
	}

	/**
	 * This API writes(sends) HL7 Outbound message with required block notations(e.g; start character/end character) to medication interface.
	 * @param entry
	 * @return boolean
	 * @throws IOException
	 */
	private boolean writeMessage(Map.Entry<Integer, Object> entry ) throws IOException {
		boolean bFlag = false;
		
		PrintWriter out = new PrintWriter(mSocket.getOutputStream());
		String outboundMessageWithNotation =  addBlockNotationToFinalHL7OutBoundMessages(entry.getValue().toString().trim());
		if(null == outboundMessageWithNotation) {
			logger.error("[DispensingInterfaceOutBoundEngine] writeMessage - addBlockNotationToFinalHL7OutBoundMessages() returns null");
			return bFlag;
		}
		out.print(outboundMessageWithNotation);
		out.flush();
		bFlag = true;
		return bFlag;
	}
	
	/**
	 * This API reads the received acknowledge(ACK/NACK) message from enabled medication interface.
	 * @param br
	 * @return StringBuffer
	 * @throws SocketTimeoutException
	 * @throws IOException
	 */
	private String readMessage(BufferedReader br) throws IOException {
		boolean endOfStream=false;
		StringBuilder ackMessage = new StringBuilder();
		String ackMessageOut = "";
		while (!endOfStream){
			String ackMessageTemp =br.readLine();
			if( null != ackMessageTemp){
				ackMessage.append(ackMessageTemp+crCHAR);
				for(int ii=0 ; ii<ackMessageTemp.length();ii++){
					if(ackMessageTemp.charAt(ii)==eofMessageCHAR){
						endOfStream=true;
						break;
					}
				}
			}else {
				endOfStream=true;
			}
		}
		if(ackMessage.length() > 0) {
			String tempMessage = ackMessage.toString().trim();
			if(tempMessage.contains(Hl7MessageEnclosingCodes.STARTOFMESSAGECHAR.toString())) {
				ackMessageOut = ackMessageOut.replace(Hl7MessageEnclosingCodes.STARTOFMESSAGECHAR.toString(), "");
			}else {
				ackMessageOut = tempMessage;
			}
		}
		return ackMessageOut;
	}
	
	/**
	 * This API creates the connection with enabled medication interface on the basis of its IP and PORT 
	 * @throws IOException
	 */
	private synchronized void createSocketConn() throws IOException
	{
 		String serverIP= null;
		int serverPort= 0;
		serverIP = dispensingInterfaceCacheService.getDispensingInterfaceModel().getConnectionIp();
		serverPort = dispensingInterfaceCacheService.getDispensingInterfaceModel().getConnectionPort();
		
        try{
			if (isPersistentMode) {
				
				mSocket = (Socket) dispensingInterfaceCacheService.retrieveObject("globalsocket");
				
				if (null == mSocket || !mSocket.isConnected() || mSocket.isInputShutdown() || mSocket.isOutputShutdown() || mSocket.isClosed()) {
					mSocket = new Socket(serverIP, serverPort);
					mSocket.setKeepAlive(true);
					mSocket.setReuseAddress(true);
					dispensingInterfaceCacheService.storeObject("globalsocket", mSocket);
				}
			}
			else
			{
				if (null != mSocket)
					mSocket.close();

				mSocket = new Socket(serverIP, serverPort);
			}
        }catch (IOException | RuntimeException e) {
        	logger.error("[DispensingInterfaceOutBoundEngine] createSocketConn -" + e.getMessage());
        }
	}
    /**
     * This API process each received acknowledgement(ACK/NACK) message to parse all the required details from it.
     * @param hl7OutBoundMessagesAckList
     * @return List<Map>
     */
    public List<Map<String, Object>> processHL7AckOutboundMessage(List<Map<String, Object>> hl7OutBoundMessagesAckList) {
    	logger.debug("[DispensingInterfaceOutBoundEngine] processHL7AckOutboundMessage - parsing of received messages initiated.");
    	List<Map<String, Object>> outBoundMessageAckMapList = new LinkedList<>();
    	for (Map<String, Object> map : hl7OutBoundMessagesAckList) {
    		if(map.get(DispensingInterfaceKeyConstants.ACKMESSAGE.getKeyConstants()).toString().trim().length() > 0) {
    			outBoundMessageAckMapList.add(parseOutboundMessageACK(map));
    		}else {
    			outBoundMessageAckMapList.add(map);
    		}
    	}
    	logger.debug("[DispensingInterfaceOutBoundEngine] processHL7AckOutboundMessage - parsing of received messages completed successfully.");
    	return outBoundMessageAckMapList;
    }

    /**
     * This API parses ACK/NACK message
	 * @param ackHL7Message
	 * @return Map
	 * @throws DispensingInterfaceCustomException
	 */
	private Map<String,Object> parseOutboundMessageACK(Map<String, Object> ackMap){
		String ackStatus = "";
		String status = "";
		try {
				ackMap.put( DispensingInterfaceKeyConstants.ERRORMESSAGE.getKeyConstants(), "Error occured while parsing received ACk/NACK from interface.");
				String [] ackSegments = ackMap.get(DispensingInterfaceKeyConstants.ACKMESSAGE.getKeyConstants()).toString().trim().split(String.valueOf(crCHAR));
				
				for(String segment : ackSegments){
					switch (segment.trim().substring(0, DispensingInterfaceACKMessageConstants.ACKMSHINDEX.getAckConstants())) {
					case "MSH":
						ackMap.putAll(parseOutboundMessageAckMSH(segment));
						break;
					case "MSA":
						ackMap.putAll(parseOutboundMessageAckMSA(segment));
						break;
					default:
						throw new DispensingInterfaceCustomException(DispensingInterfaceStatus.INVALIDACKOUTBOUNDMESSAGE);
					}
				}
				
				if((DispensingInterfaceKeyConstants.FAILED.getKeyConstants().equals(DispensingInterfaceKeyConstants.MSHPARSESTATUS.getKeyConstants()))||(DispensingInterfaceKeyConstants.FAILED.getKeyConstants().equals(DispensingInterfaceKeyConstants.MSAPARSESTATUS.getKeyConstants()))) {
					throw new DispensingInterfaceCustomException(DispensingInterfaceStatus.INVALIDACKOUTBOUNDMESSAGE);
				}
				
				String ackMessageStatusCode = (!ackMap.isEmpty()) ? ackMap.get( DispensingInterfaceKeyConstants.ACKSTATUS.getKeyConstants()).toString() : "";
				validateOutBoundStatusCodeACK(HL7MessageAckStatusCodes.valueOf(ackMessageStatusCode));
				
				ackStatus = HL7MessageAckStatusCodes.valueOf(ackMessageStatusCode).getAckStatusCode();
				
				if("AA".equals(ackMessageStatusCode)) {
					status=DispensingInterfaceKeyConstants.SUCCESS.getKeyConstants(); 
				}else {
					status=("AE".equals(ackMessageStatusCode) || "AR".equals(ackMessageStatusCode))? DispensingInterfaceKeyConstants.FAILED.getKeyConstants(): "";
				}

				if(status.isEmpty()) {
					throw new DispensingInterfaceCustomException(DispensingInterfaceStatus.INVALIDACKSTATUSCODE);
				}
				ackMap.put("status", status);
				//modifying ackstatus with status value associated to status code
				ackMap.put(DispensingInterfaceKeyConstants.ACKSTATUS.getKeyConstants(), ackStatus);
				ackMap.put(DispensingInterfaceKeyConstants.ERRORMESSAGE.getKeyConstants(), "");

		}catch (DispensingInterfaceCustomException e) {
			logger.error("[DispensingInterfaceOutBoundEngine] parseOutboundMessageACK - " + e.getStatusMessage());
			ackMap.put(DispensingInterfaceKeyConstants.ERRORMESSAGE.getKeyConstants(), e.getStatusMessage());
		}
		catch (ArrayIndexOutOfBoundsException e) {
			logger.error("[DispensingInterfaceOutBoundEngine] parseOutboundMessageACK - " + e.getMessage());
			ackMap.put(DispensingInterfaceKeyConstants.ERRORMESSAGE.getKeyConstants(), e.getMessage());
		}
		return ackMap;
	}
	
	/**
	 * This API parse the MSH segment of received ACK/NACK message from medication interface.
	 * @param segment
	 * @return Map
	 */
	private Map<String, Object> parseOutboundMessageAckMSH(String segment) {
		Map<String, Object> mshMap = new LinkedHashMap<>();
		try{
			String [] mshSegment = segment.split("\\|");
			mshMap.put("messagetype", mshSegment[DispensingInterfaceACKMessageConstants.ACKMSHMESSAGETYPEPOSITION.getAckConstants()-1]);
			mshMap.put("receiveapp", mshSegment[DispensingInterfaceACKMessageConstants.ACKMSHRECEIVEAPPPOSITION.getAckConstants()-1]);
			mshMap.put(DispensingInterfaceKeyConstants.MSHPARSESTATUS.getKeyConstants(), DispensingInterfaceKeyConstants.SUCCESS.getKeyConstants());
		}catch (ArrayIndexOutOfBoundsException ex) {
			mshMap.put(DispensingInterfaceKeyConstants.MSHPARSESTATUS.getKeyConstants(), DispensingInterfaceKeyConstants.FAILED.getKeyConstants());
			logger.error("[DispensingInterfaceOutBoundEngine] parseOutboundMessageAckMSH - " + ex.getMessage());
		}
		return mshMap;
	}
	
	/**
	 * API to parse MSA segment of received ACK/NACK message from mdeication interface.
	 * @param segment
	 * @return Map
	 */
	private Map<String, Object> parseOutboundMessageAckMSA(String segment) {
		Map<String, Object> msaMap = new LinkedHashMap<>();
		try{
			String [] msaSegment= segment.split("\\|");
			msaMap.put(DispensingInterfaceKeyConstants.ACKSTATUS.getKeyConstants(), msaSegment[DispensingInterfaceACKMessageConstants.ACKMSASTATUSCODEPOSITION.getAckConstants()]); 
			msaMap.put("messagecontrolid", msaSegment[DispensingInterfaceACKMessageConstants.ACKMSAMESSAGECONTROLIDPOSITION.getAckConstants()]);
			msaMap.put(DispensingInterfaceKeyConstants.MSAPARSESTATUS.getKeyConstants(), DispensingInterfaceKeyConstants.SUCCESS.getKeyConstants());
		}catch (ArrayIndexOutOfBoundsException ex) {
			msaMap.put(DispensingInterfaceKeyConstants.MSAPARSESTATUS.getKeyConstants(), DispensingInterfaceKeyConstants.FAILED.getKeyConstants());
			logger.error("[DispensingInterfaceOutBoundEngine] parseOutboundMessageAckMSA - " + ex.getMessage());
		}
		return msaMap;
	}
	/**
	 * This API add the block notations to HL7 Outbound Messages e.g; start character of message, segment separator character and end of message character etc.
	 * @param outBoundMessage
	 * @return List
	 */
	 private String addBlockNotationToFinalHL7OutBoundMessages(String outBoundMessage){
		String message = null;
		try{
			StringBuilder sb = new StringBuilder();
			sb.append(sofMessageCHAR);
			String[] segmentsArray = outBoundMessage.split(newLine); 
			for(int i = 0; i < segmentsArray.length; i++){
				sb.append(segmentsArray[i].trim());
				if(i < (segmentsArray.length - 1)) {
					sb.append(crCHAR);
				}
			}
			sb.append(eofMessageCHAR).append(crCHAR);
			message = sb.toString();
		}catch (ArrayIndexOutOfBoundsException ex) {
			logger.error("[DispensingInterfaceOutBoundEngine] addBlockNotationToFinalHL7OutBoundMessages - " + ex.getMessage());
		}
		return message;
	} 
	/**
	 * API to log HL7 outbound message's ACK/NACK 
	 * @param ackMessageMap
	 * @return boolean
	 */
	private boolean logOutboundMessageACK(List<Map<String,Object>> ackMessageMapList){
		boolean bFlag = false;
		if(!ackMessageMapList.isEmpty() && dispensingIterfaceOutBoundDao.logOutboundMessageACK(getMapArray(ackMessageMapList))) {
			 bFlag = true;
		}
		return bFlag;
	}
	
	/**
	 * This API converts the List of Maps into Array of Maps
	 * @param list
	 * @return Map[]
	 */
	public static Map<String, Object>[] getMapArray(List<Map<String, Object>> list){
        @SuppressWarnings("unchecked")
        Map<String, Object>[] maps = new HashMap[list.size()];

        Iterator<Map<String, Object>> iterator = list.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Map<String,Object> map = iterator.next();
            maps[i++] = map;
        }
        return maps;
    }
	
	/**
	 * API to validate ACK status code
	 * @param statusCode
	 * @return
	 * @throws DispensingInterfaceCustomException
	 */
	private boolean validateOutBoundStatusCodeACK(HL7MessageAckStatusCodes statusCode)throws DispensingInterfaceCustomException{
		boolean bFlag=false;
		if(null!=statusCode){
			switch(statusCode){
				case AA:
					bFlag=true;
					break;
				case AE:
					bFlag=true;
					break;
				case AR:
					bFlag=true;
					break;
				default:
					break;
			}
		}
		if(!bFlag)
			throw new DispensingInterfaceCustomException(DispensingInterfaceStatus.INVALIDACKSTATUSCODE);
		
		return bFlag;
	}
}
