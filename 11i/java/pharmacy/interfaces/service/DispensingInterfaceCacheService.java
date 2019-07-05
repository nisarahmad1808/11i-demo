package inpatientWeb.pharmacy.interfaces.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;
import inpatientWeb.Global.items.dao.IPItemkeyDAO;
import inpatientWeb.Global.service.EcwAppContext;
import inpatientWeb.interfaces.utils.InteropUtility;
import inpatientWeb.pharmacy.interfaces.dao.DispensingInterfaceCacheDao;
import inpatientWeb.pharmacy.interfaces.outbound.dto.DispensingInterfaceModel;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.DispensingInterfaces;

@Service
public class DispensingInterfaceCacheService {

	private  boolean isDispensignInterfaceCacheSet=false;
	private  Map<String,Object> dispensingInterfaceItemKeysMap=null;
	private  String enabledInterface="";
	private  DispensingInterfaceModel dispensingInterfaceModel=null;
	private  Map<String,Object> dispensingHl7SegmentDetailsMap=null;
	private  Map<String,Object> dispensingComponentTypeSegmentMap=null;
	private  boolean isDispensingInterfaceEnabled=false;
	private  Integer interfaceUserId=null;
	private static Map<String,Object> outboundSocketMap	= null;
	
	@Autowired
	private DispensingInterfaceCacheDao dispensingInterfaceCacheDao;
	
	@Autowired
	private InteropUtility interopUtil;
	
	public DispensingInterfaceCacheService(){
		if(null==dispensingInterfaceCacheDao)
			dispensingInterfaceCacheDao = (DispensingInterfaceCacheDao)EcwAppContext.getObject(DispensingInterfaceCacheDao.class);
		
		outboundSocketMap	= new HashMap<>();
	}
	
	/**
	 * API to set the static reference data. 
	 * @return boolean
	 */
	public  boolean setDispensingInterfaceCache()
	{   boolean bFlag=false;
		try{
			if(!isDispensignInterfaceCacheSet){
				bFlag=setEnabledDispensingInterface();
				bFlag=setInterfaceDetails();
				bFlag=setDispensingInterfaceItemKeys();
				bFlag=setDispensingHl7SegmentDetails();
				bFlag=setDispensingMessageComponentTypeSegment();
				isDispensignInterfaceCacheSet=bFlag;
			}
		}catch(RuntimeException ex){
			isDispensignInterfaceCacheSet=bFlag;
			EcwLog.AppendExceptionToLog(ex);
		}
		return isDispensignInterfaceCacheSet;
	}
	
	/**
	 * API to reset the static which are already set, in case of any changes in database values.
	 * @return boolean
	 */
	public boolean resetDispensingInterfaceCache(){
		boolean bFlag=false;
		try{
			isDispensignInterfaceCacheSet=false;
			setDispensingInterfaceCache();
			isDispensignInterfaceCacheSet=false;
			bFlag=true;
		}catch(RuntimeException ex){
			bFlag=false;
			EcwLog.AppendExceptionToLog(ex);
		}
		return bFlag;
	}
	/**
	 * API will get and set the dispensingInterfaceItemKeysMap with all the item keys configured for a given medication dispensing interface id
	 * @exception DataAccessException
	 * @return True  - If item keys were configured
	 *         False - If no item keys are configured
	 */
	private boolean setDispensingInterfaceItemKeys(){
		boolean bFlag=false;
	    try{
	    	dispensingInterfaceItemKeysMap=dispensingInterfaceCacheDao.getDispensingInterfaceItemKeys(dispensingInterfaceModel.getInterfaceId());
	    	bFlag=true;
	    	if(null==dispensingInterfaceItemKeysMap) {
				EcwLog.AppendToLog("[DispensingInterfaceCacheService] setDispensingInterfaceItemKeys - No item keys configured for a given intefaceId :" + dispensingInterfaceModel.getInterfaceId());
	    	}
	    }catch(DataAccessException ex){
	    	bFlag=false;
	    	EcwLog.AppendExceptionToLog(ex);
	    }
	    return bFlag;
	}
	/**
	 * API will get the itemkey value for a provided itemkey name
	 * @exception ClassCastException
	 * @param itemKeyname
	 * @return String - Itemkey value or blank 
	 */
	public String getDispensingInterfaceItemKeyValue(String itemKeyname){
		String itemKeyValue="";
		try{
			 if(null==itemKeyname || "".equals(itemKeyname))
				 EcwLog.AppendToLog("Itemkey name cannot be null or blank");
				
			 itemKeyValue=dispensingInterfaceItemKeysMap.get(itemKeyname).toString();
		     
		}catch(ClassCastException ex){
	    	EcwLog.AppendExceptionToLog(ex);
		}
		return itemKeyValue;
	}
	/**
	 * API will checks and sets if an medication dispensing interface is enabled in practice
	 * @return True  - If medication dispensing interface is enabled
	 *         False - If no medication dispensing interface is enabled
	 */
	private boolean setEnabledDispensingInterface(){
		String value="";
		int count=0;
		value=IPItemkeyDAO.getIPItemKeyValueFromName(DispensingInterfaces.PYXIS.getDispensingInterface(),"");
		if("yes".equalsIgnoreCase(value)){
			enabledInterface="Pyxis";
			count++;
		}
		value=IPItemkeyDAO.getIPItemKeyValueFromName(DispensingInterfaces.ASYENT.getDispensingInterface(),"");
		if("yes".equalsIgnoreCase(value)){
			enabledInterface="Asyent";
			count++;
		}
		value=IPItemkeyDAO.getIPItemKeyValueFromName(DispensingInterfaces.MEDDISPENSE.getDispensingInterface(),"");
		if("yes".equalsIgnoreCase(value)){
			enabledInterface="Meddispense";
			count++;
		}
		if(count==1)
			isDispensingInterfaceEnabled=true;
		else
			EcwLog.AppendToLog("[DispensingInterfaceCacheService] setEnabledDispensingInterface - Medication dispensing interface is not setup in the practice.Please contact your administrator");
	
		return isDispensingInterfaceEnabled;
	} 
	/**
	 * API will get and set the interface details for a given medication dispensing interface
	 * @exception DataAccessException
	 * @return True  - If dispensing interface details found
	 *         False - If no dispensing interface details found
	 */
	private  boolean setInterfaceDetails(){
		boolean bFlag=false;
		try{
			if(!"".equals(enabledInterface)){
				dispensingInterfaceModel = dispensingInterfaceCacheDao.getInterfaceDetailObject(enabledInterface);
				bFlag=true;
				if(null==dispensingInterfaceModel){
					bFlag=false;
					EcwLog.AppendToLog("[DispensingInterfaceCacheService] setInterfaceDetails - Could not find interface details for given interface : " + enabledInterface);
				}
			}
		}catch(DataAccessException ex){
			EcwLog.AppendExceptionToLog(ex);
		}
		return bFlag;
	}
	/**
	 * API will get and set the interface user id
	 * @exception DataAccessException
	 * @return True  - If interface user is setup
	 *         False - If no interface user is setup
	 */
	public boolean setInterfaceUserId(){
		boolean bFlag=false;
		try{
			interfaceUserId = interopUtil.getInterfaceUserId();
			bFlag=true;
			if(null==interfaceUserId || 0==interfaceUserId){
				bFlag=false;
				EcwLog.AppendToLog("[DispensingInterfaceCacheService] setInterfaceUserId - No interface user is setup in the system");
			}
		}catch(DataAccessException ex){
			bFlag=false;
			EcwLog.AppendExceptionToLog(ex);
		}
		return bFlag;
	}
	/**
	 * API will get and set the HL7 message segment details for the given medication dispensing interface id
	 * @exception DataAccessException
	 * @return True  - If HL7 message segment details mapped
	 *         False - If HL7 message segment details not mapped
	 */
	private boolean setDispensingHl7SegmentDetails(){
		boolean bFlag=false;
		try{
			dispensingHl7SegmentDetailsMap= dispensingInterfaceCacheDao.getDispensingHl7SegmentDetail(dispensingInterfaceModel.getInterfaceId());
			bFlag=true;
			if(null==dispensingHl7SegmentDetailsMap){
				bFlag=false;
				EcwLog.AppendToLog("[DispensingInterfaceCacheService] setDispensingHl7SegmentDetails - Dispensing Interface HL7 segments are not configured for a given interface id : " +dispensingInterfaceModel.getInterfaceId());
			}
		}catch(DataAccessException ex){
			bFlag=false;
			EcwLog.AppendExceptionToLog(ex);
		}
		return bFlag;
	}
	/**
	 * API will get and set the message component type details for the given medication dispensing interface id
	 * @exception DataAccessException
	 * @return True  - If message component type details mapped
	 *         False - If message component type details not mapped
	 */
	private  boolean setDispensingMessageComponentTypeSegment(){
		boolean bFlag=false;
		try{
			dispensingComponentTypeSegmentMap= dispensingInterfaceCacheDao.getDispensingMessageComponentTypeSegment(dispensingInterfaceModel.getInterfaceId());
			bFlag=true;
			if(null==dispensingComponentTypeSegmentMap){
				bFlag=false;
				EcwLog.AppendToLog("[DispensingInterfaceCacheService] getDispensingMessageComponentTypeSegment -Dispensing Interface component type are not configured for a given interface id : " +dispensingInterfaceModel.getInterfaceId());
			}
		}catch(DataAccessException ex){
			bFlag=false;
			EcwLog.AppendExceptionToLog(ex);
		}
		return bFlag;
	}
	public DispensingInterfaceModel getDispensingInterfaceModel() {
		return dispensingInterfaceModel;
	}
	public String getEnabledInterface() {
		return enabledInterface;
	}
	public void setEnabledInterface(String enabledInterface) {
		this.enabledInterface = enabledInterface;
	}

	public Map<String, Object> getDispensingHl7SegmentDetailsMap() {
		return dispensingHl7SegmentDetailsMap;
	}
	public Map<String, Object> getDispensingComponentTypeSegmentMap() {
		return dispensingComponentTypeSegmentMap;
	}
	public int getInterfaceUserId() {
		return interfaceUserId;
	}
	public void storeObject(String objectKeyName, Object socketInstance)
	{
		if (null != outboundSocketMap.get(objectKeyName))
			outboundSocketMap.remove(objectKeyName);
		outboundSocketMap.put(objectKeyName, socketInstance);
	}

	public Object retrieveObject(String objectKeyName)
	{
		if (null != outboundSocketMap.get(objectKeyName)){
				return outboundSocketMap.get(objectKeyName);
		}
		return null;
	}
}
