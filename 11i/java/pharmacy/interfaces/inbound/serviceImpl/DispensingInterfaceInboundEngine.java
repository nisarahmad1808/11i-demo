package inpatientWeb.pharmacy.interfaces.inbound.serviceImpl;

import inpatientWeb.Global.service.EcwAppContext;
import inpatientWeb.pharmacy.interfaces.inbound.dao.DispensingInterfaceInBoundDAO;
import inpatientWeb.pharmacy.interfaces.service.DispensingInterfaceCacheService;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.DispensingMessageStatusCode;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.HL7MessageAckStatusCodes;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.Hl7MessageEnclosingCodes;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceUtil;
import inpatientWeb.utils.DateUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

public class DispensingInterfaceInboundEngine implements StatefulJob, InterruptableJob,Runnable {
	
	private static final Logger logger = LogManager.getLogger(DispensingInterfaceInboundEngine.class);
    
	private ServerSocket lServerSocket = null;
	private boolean cleanThreads=false;
	private int nPort;
	boolean dDebug=false;
	private char eOfMessageChar;
	private char crChar;
	private char sOfMessageChar;
	private int noTimeOut=0;
	private String hl7ProcessingId="P";
	private String hl7VersionId="2.3";
	private String messageControlIdPos="9";
	private String recApp="eCW";
	private String sendingApp="";
	

	@Autowired
	private DispensingInterfaceInBoundDAO dispensingInterfaceInBoundDAO;
	
	@Autowired
	private DispensingInterfaceCacheService dispensingInterfaceCacheService;
	
	@Autowired
	private DispensingInterfaceInBoundServiceImpl dispensingInterfaceInboundServiceImpl;
	
	DispensingInterfaceUtil dUtil;
	
	public  DispensingInterfaceInboundEngine(){
		if(null==dispensingInterfaceInBoundDAO)
			dispensingInterfaceInBoundDAO=(DispensingInterfaceInBoundDAO)EcwAppContext.getObject(DispensingInterfaceInBoundDAO.class);

		if(null==dispensingInterfaceCacheService)
			dispensingInterfaceCacheService=(DispensingInterfaceCacheService)EcwAppContext.getObject(DispensingInterfaceCacheService.class);
		
		if(null==dispensingInterfaceInboundServiceImpl)
			dispensingInterfaceInboundServiceImpl=(DispensingInterfaceInBoundServiceImpl)EcwAppContext.getObject(DispensingInterfaceInBoundServiceImpl.class);
		
		if(null==dUtil)
			dUtil=new DispensingInterfaceUtil();
	}
		
	/* (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext context)throws JobExecutionException {
		try{
		//Get all itemkey values for this job here
		logger.debug("[DispensingInterfaceInboundEngine] execute - Initialized Successfully : "+ new java.util.Date());
		
		if(dispensingInterfaceCacheService.setDispensingInterfaceCache()){
			dispensingInterfaceCacheService.setInterfaceUserId();
			getInterfaceItemKeyValues();
			nPort= "".equals(dispensingInterfaceCacheService.getDispensingInterfaceItemKeyValue("Listener_Port"))?0: Integer.parseInt(dispensingInterfaceCacheService.getDispensingInterfaceItemKeyValue("Listener_Port"));
			attachListener();
			processInboundMessages();
		}
		else{
			logger.error("[DispensingInterfaceInboundEngine] execute - Failed to start the job. [Reason - Dispensing interface cache could not be initalized]: "+ new java.util.Date());
		}
		logger.debug("[DispensingInterfaceInboundEngine] execute - Completed Successfully : "+ new java.util.Date());
		}
		catch(NumberFormatException ex){
			logger.error("[DispensingInterfaceInboundEngine] execute - Exception occurred while exceuting job - " + ex.getMessage());
			interrupt();
		}finally{
			//If job fails what need to be done code go here
		}
	}
	/* (non-Javadoc)
	 * @see org.quartz.InterruptableJob#interrupt()
	 */
	@Override
	public void interrupt(){
		try {
			logger.debug("[DispensingInterfaceInboundEngine] interrupt - Stopping the job and closing socket...");
			if(null != lServerSocket)
			{
				logger.debug("[DispensingInterfaceInboundEngine] interrupt - Socket closing in interrupted method");
				lServerSocket.close();
			}
			cleanThreads = true;
		}
		catch(IOException ex){
			logger.error("[DispensingInterfaceInboundEngine] interrupt - Exception while stopping and closing socket... : " + ex.getMessage());
		}
	}
	/* (non-Javadoc)
	 * @see java.lang.Runnable#)
	 */
	@Override
	public void run() {
		Socket lSocket= null;
		boolean endOfMessage=false;
		boolean endOfStream=false;
		boolean isNewListener=false;
		StringBuilder strBuilder=null;
		String hl7Message="";
		String hl7NewMessage="";
		try {
			do{
				if(cleanThreads)
					break;
				
				logger.debug("[DispensingInterfaceInboundEngine] run - Waiting for client connection");
				try{
					lSocket = lServerSocket.accept();
				}catch(IOException ex){
					logger.error("[DispensingInterfaceInboundEngine] run - Exception occurred while accepting client connection : "+ex.getMessage());
					break;
			    }
				if(!isNewListener){
					logger.debug("[DispensingInterfaceInboundEngine] run - Adding new listner");
					addListener();
					isNewListener=true;
				}
				logger.debug("[DispensingInterfaceInboundEngine] run - Client connection accepted successfully connection details are : " + lSocket);
		        BufferedReader in = new BufferedReader(new InputStreamReader(lSocket.getInputStream()));		        
		        logger.debug("[DispensingInterfaceInboundEngine] run - Created buffer reader for client input stream successfully");
		        strBuilder= new StringBuilder();
	            while (!endOfMessage)
	            {
	            	if(hl7NewMessage !=null && hl7NewMessage.length()>0){
	            		hl7Message=hl7NewMessage;
	            		hl7NewMessage="";
	            	}
	            	else{
	            		hl7Message = in.readLine();
	            	}
	            	logger.debug("[DispensingInterfaceInboundEngine] run - Read a line of message :" + hl7Message);
	        		if(null != hl7Message && hl7Message.length()>0){
	        			strBuilder.append(hl7Message);
	        			strBuilder.append(crChar);
	        			logger.debug("[DispensingInterfaceInboundEngine] run - Append message to buffer ");	        				
						for(int ii=0 ; ii<hl7Message.length();ii++){
							if(hl7Message.charAt(ii)==eOfMessageChar){
								logger.debug("[DispensingInterfaceInboundEngine] run - Reached end of message");
								//Generate ack message
								String ackMsg = "";
								ackMsg=generateAckMessage(strBuilder.toString());
								//Write back the ack
								PrintWriter out = new PrintWriter(lSocket.getOutputStream(), true);
				    	        out.print(ackMsg);
				    	        out.flush();
				    	        //Log the incoming message and ack message
				    	        logInboundMessage(strBuilder.toString(),ackMsg);
				    	        logger.debug("[DispensingInterfaceInboundEngine] run - Ack message sent on : " + new java.util.Date() + " and message is : " + ackMsg);
				    	        logger.debug("[DispensingInterfaceInboundEngine] run - Waiting for incoming data");
				    	        hl7NewMessage = in.readLine();
				    	        if(hl7NewMessage==null){
				    	        	logger.debug("[DispensingInterfaceInboundEngine] run - Reached end of stream");
				    	        	endOfMessage=true;
				    	        	endOfStream=true;
				                }else{
				                	logger.debug("[DispensingInterfaceInboundEngine] run - Received new incoming message");
				                }
				    	        strBuilder= new StringBuilder();
							}
						}
					}else{
						endOfMessage=true;
	    	        	endOfStream=true;
					}
				}
			}while(!endOfStream);
		}catch(SocketException sockex){
			logger.error("[DispensingInterfaceInboundEngine] run - Exception occurred : " + sockex.getMessage());
		}
		catch(IOException ioex){
			logger.error("[DispensingInterfaceInboundEngine] run - Exception occurred : " + ioex.getMessage());
		}finally{			 
			if(null != lSocket) {
				try {
					lSocket.close();
				} catch (IOException e) {
					logger.error("[DispensingInterfaceInboundEngine] run : "+e.getMessage());
				}	
			}
		}
	}
	/**
	 * API will validate and attach a listener on specified port
	 * @exception IOException
	 * @return True 	- If listener was attached successfully on specified port
	 *         False 	- If failed to attach listener on specified port
	 */
	private boolean attachListener(){
		boolean bPortListenerFlag=false;
		try {
				if(!validatePort())
					return bPortListenerFlag;
				
				logger.debug("[DispensingInterfaceInboundEngine] attachListener - Starting to listern on port : " + nPort);
				lServerSocket= new ServerSocket(nPort);
				lServerSocket.setSoTimeout(noTimeOut);				
				addListener();
				bPortListenerFlag=true;
		} catch (IOException e) {
			logger.error("[DispensingInterfaceInboundEngine] attachListener : " + e.getMessage());
		}
		return bPortListenerFlag;
	}
	/**
	 * API will validate if the specified port is valid and log the appropriate message after validating 
	 * @exception IOException
	 * @return True 	- If port is already in use
	 *         False 	- If port is not in use
	 */
	private boolean validatePort(){
		ServerSocket serverSok = null;
		boolean bFlag = false;
		try {
			if(nPort>0){
				serverSok = new ServerSocket(nPort);
				serverSok.close();
				bFlag = true;
			}else{
				logger.error("[DispensingInterfaceInboundEngine] validatePort - Invalid port number : " + nPort);
			}
		}
		catch (SocketException bind){
			logger.error("[DispensingInterfaceInboundEngine] validatePort - Already listening on port : " + nPort);
		}
		catch (IOException | RuntimeException ex){
			logger.error("[DispensingInterfaceInboundEngine] validatePort : "+ ex.getMessage());
		}
		return bFlag;
	}
	
	/**
	 * API will initiate a new thread to start listening for incoming connection 
	 * @exception IllegalThreadStateException
	 * @return True 	- If new thread started successfully
	 *         False 	- If failed to start new thread
	 */
	private boolean addListener(){
		boolean bFlag=false;
		try{
			(new Thread(this)).start();
			 bFlag=true;
		}catch(IllegalThreadStateException  ex){
			logger.error("[DispensingInterfaceInboundEngine] addListener - Exception occurred while starting new thread : "+ex.getMessage());
		}		
		return bFlag;
	}	
    /**
     * API will process inbound messages by getting list of all pending messages to be processed and iterate over each message 
     * and invoke process api to parse and log the details
     * @return True  - Successfully processed message 
     * 	       False - Failed to process the message
     */
    private boolean processInboundMessages() {
		boolean bFlag=false;
		List<Map<String, Object>> hl7InBoundMessagesList = null;
		try {
			hl7InBoundMessagesList = getPendingInBoundMessages();
			if(hl7InBoundMessagesList!=null && !hl7InBoundMessagesList.isEmpty()) {
				for (Map<String, Object> hl7InboundMessageMap : hl7InBoundMessagesList) {
					logger.debug("[DispensingInterfaceInboundEngine] processInboundMessages - processing message id : "+ hl7InboundMessageMap.get("id").toString());
					dispensingInterfaceInboundServiceImpl.processInboundMessage(hl7InboundMessageMap);
				}
			}
			bFlag=true;
		}
		catch (RuntimeException e) {
	    	logger.error("[DispensingInterfaceInboundEngine] processInboundMessages : " + e.getMessage());
		}
		return bFlag;
	}
	/**
	 * API to log the received inbound hl7 message and acknowledgement message sent for that message
	 * @param hl7Msg - HL7 message sent by the dispensing medication interface
	 * @param ackMsg - ACK message generated for the received hl7 message
	 * @return True	 - Successfully logged the details
	 *         False - Failed to logged the details
	 */
	private boolean logInboundMessage(String hl7Msg,String ackMsg) {
        Map<String, Object> dispenseInboundMessageMapObject=new HashMap<>();
		int inboundMsgLogId=0;
		boolean bFlag=false;
		try {
			if(hl7Msg.length() > 0) {
				hl7Msg = hl7Msg.trim();
				if(hl7Msg.contains(Hl7MessageEnclosingCodes.STARTOFMESSAGECHAR.toString())) {
					hl7Msg = hl7Msg.replace(Hl7MessageEnclosingCodes.STARTOFMESSAGECHAR.toString(), "");
				}
			}	
			dispenseInboundMessageMapObject.put("hl7Message", hl7Msg);
			dispenseInboundMessageMapObject.put("status", DispensingMessageStatusCode.PENDING.getDispensingMessageStatusCode());
			dispenseInboundMessageMapObject.put("dateReceived", DateUtil.getTodaysDate("yyyy-MM-dd HH:mm:ss") );
			dispenseInboundMessageMapObject.put("ack",1);
			dispenseInboundMessageMapObject.put("ackMessage", ackMsg);
			dispenseInboundMessageMapObject.put("dateAckSent", DateUtil.getTodaysDate("yyyy-MM-dd HH:mm:ss"));
			dispenseInboundMessageMapObject.put("createdBy", dispensingInterfaceCacheService.getInterfaceUserId());
			inboundMsgLogId=dispensingInterfaceInBoundDAO.logMessageFromInboundEngine(dispenseInboundMessageMapObject);
			if(inboundMsgLogId>0)
				bFlag=true;
			
		}catch (RuntimeException e) {
			logger.error("[DispensingInterfaceInboundEngine] logInboundMessage - Failed to log the inbound message : "+ e.getMessage());
		}
		return bFlag;
	}
	/**
	 * API will get list of all pending HL7 inbound message that were received from medication dispensing interface
	 * @return List<Map<String, Object>>
	 */
	private List<Map<String, Object>> getPendingInBoundMessages() {
		List<Map<String, Object>> hl7InBoundMessagesList = null;
		try {
			hl7InBoundMessagesList = dispensingInterfaceInBoundDAO.getPendingInboundMessages();
		} catch (DataAccessException e) {
			logger.error("[DispensingInterfaceInboundEngine] getPendingInBoundMessages - Error ocurred while getting pending inbound message : " +e.getMessage());
		}
		return hl7InBoundMessagesList;
	}
	/**
	 * API will validate if received message is blank,if blank returns an empty response else extracts individual segments and invokes parse api 
	 * to extract message control id to create HL7 ack message that will be sent back as response for the received message
	 * @param recHL7Message - Hl7 message sent by the dispensing medication interface
	 * @return String - ACK message in HL7 format
	 */
	private String generateAckMessage(String recHL7Message) {
		String ackMessage = "";
		String msgControlId;
		String mshSegmentData;
		String [] hl7MessageArray;
		try{
			if(DispensingInterfaceUtil.validateNullObject(recHL7Message)){
				hl7MessageArray=recHL7Message.split(DispensingInterfaceConstants.Hl7MessageEnclosingCodes.CARRIAGERETURNCHAR.toString());
				if(null!=hl7MessageArray && hl7MessageArray.length>0){
					mshSegmentData =hl7MessageArray[0];
					msgControlId=parseMSHSegment(mshSegmentData);
					ackMessage=createAckMessage(msgControlId);
				}
			}
		}catch(RuntimeException e){
			logger.error("[DispensingInterfaceInboundEngine] generateAckMessage - Error ocurred while generating ack message : " +e.getMessage());
		}
		return ackMessage;
	}
	/**
	 * API will create HL7 formatted ACK message that will be sent back as response for a received inbound message 
	 * @param messageControlId - Message control id that was sent in MSH segment of received inbound message  
	 * @return String : ACK message in HL7 format
	 */
	private String createAckMessage(String messageControlId) {
		StringBuilder strAckMsgBuilder = null;
		String ackMessage="";
		try{
			if(null!=messageControlId && !"".equals(messageControlId)){
				strAckMsgBuilder = new StringBuilder();
				strAckMsgBuilder.append(sOfMessageChar);
				strAckMsgBuilder.append("MSH|^~\\&|");
				strAckMsgBuilder.append(sendingApp);
				strAckMsgBuilder.append("||");
				strAckMsgBuilder.append(recApp);
				strAckMsgBuilder.append("||");
				strAckMsgBuilder.append(DateUtil.getTodaysDate("yyyyMMddHHmmss"));
				strAckMsgBuilder.append("||ACK|");
				strAckMsgBuilder.append(dUtil.generateMessageId());
				strAckMsgBuilder.append("|");
				strAckMsgBuilder.append(hl7ProcessingId);
				strAckMsgBuilder.append("|");
				strAckMsgBuilder.append(hl7VersionId);
				strAckMsgBuilder.append("|");
				strAckMsgBuilder.append(crChar);
				strAckMsgBuilder.append("MSA|");
				strAckMsgBuilder.append(HL7MessageAckStatusCodes.AA.toString());
				strAckMsgBuilder.append("|");
				strAckMsgBuilder.append(messageControlId);
				strAckMsgBuilder.append(eOfMessageChar);
				strAckMsgBuilder.append(crChar);
				ackMessage=strAckMsgBuilder.toString();
			}
		}catch(IllegalArgumentException e){
			logger.error("[DispensingInterfaceInboundEngine] createAckMessage - Error ocurred while creating ack message : " +e.getMessage());
		}
		return ackMessage;
	}
	/**
	 * API to parse the given HL7 MSH segment data to get message control number that needs to sent as part of MSA segment in HL7 formatted ACK message
	 * @param mshSegment - MSH segment data from the received inbound message 
	 * @return String : Message control number 
	 */
	private String parseMSHSegment(String mshSegment) {
		String messageCtrlId=null;
		String [] mshSegmentArray;
		try{
			if(null!=mshSegment && !("").equals(mshSegment)){
				mshSegmentArray=mshSegment.split("\\|");
				if(mshSegmentArray!=null && mshSegmentArray.length>0){
					messageCtrlId=mshSegmentArray[Integer.parseInt(messageControlIdPos)];
				}
			}
		}catch(ArrayIndexOutOfBoundsException | PatternSyntaxException ex){
			logger.error("[DispensingInterfaceInboundEngine] parseMSHSegment - Error ocurred while parsing MSH segment data : " +ex.getMessage());
		}
		return messageCtrlId;
	}
	/**
	 * API to get item key values required for creating ack message,parsing the received inbound message
	 * @return True or False
	 */
	private boolean getInterfaceItemKeyValues() {
		boolean bFlag = false;
        try{
        	String lRecApp="";
        	String lSendApp="";
        	
			int startOfMessageCharDecimalValue = null!=dispensingInterfaceCacheService.getDispensingInterfaceItemKeyValue("Start_Of_Message_Char")?dUtil.getOctToDecimalValue(dispensingInterfaceCacheService.getDispensingInterfaceItemKeyValue("Start_Of_Message_Char").trim(), Hl7MessageEnclosingCodes.STARTOFMESSAGECHAR.toString().trim()):dUtil.getOctToDecimalValue("", Hl7MessageEnclosingCodes.STARTOFMESSAGECHAR.toString().trim());
			sOfMessageChar= (char)startOfMessageCharDecimalValue;
	
			int endOfMessageCharDecimalValue = null!=dispensingInterfaceCacheService.getDispensingInterfaceItemKeyValue("End_Of_Message_Char")?dUtil.getOctToDecimalValue(dispensingInterfaceCacheService.getDispensingInterfaceItemKeyValue("End_Of_Message_Char").trim(), Hl7MessageEnclosingCodes.ENDOFMESSAGECHAR.toString().trim()):dUtil.getOctToDecimalValue("", Hl7MessageEnclosingCodes.ENDOFMESSAGECHAR.toString().trim());
			eOfMessageChar	= (char)endOfMessageCharDecimalValue;
			
			int segmentSeperatorCharDecimalValue = null!=dispensingInterfaceCacheService.getDispensingInterfaceItemKeyValue("Segment_Seperator_Char")?dUtil.getOctToDecimalValue(dispensingInterfaceCacheService.getDispensingInterfaceItemKeyValue("Segment_Seperator_Char").trim(), Hl7MessageEnclosingCodes.CARRIAGERETURNCHAR.toString().trim()):dUtil.getOctToDecimalValue("", Hl7MessageEnclosingCodes.CARRIAGERETURNCHAR.toString().trim());
			crChar= (char)segmentSeperatorCharDecimalValue;
			
			lRecApp  = dispensingInterfaceCacheService.getDispensingInterfaceItemKeyValue("ReceivingApp");
			lSendApp = dispensingInterfaceCacheService.getDispensingInterfaceItemKeyValue("SendingApp");
					
			if(null!=lRecApp && !"".equals(lRecApp)) {
				recApp=lRecApp;
			}
			if(null!=lSendApp && !"".equals(lSendApp)) {
				sendingApp=lSendApp;
			}else {
				sendingApp = dispensingInterfaceCacheService.getDispensingInterfaceModel().getInterfaceName();
			}
			if(sOfMessageChar > 0 && eOfMessageChar > 0 && crChar > 0) 
				bFlag = true;
			
        }catch(ClassCastException ex){
        	logger.error("[DispensingInterfaceInboundEngine] getInterfaceItemKeysValues - Error ocurred while getting itemKey values : " +ex.getMessage());
        }
		return bFlag;
	}
}
