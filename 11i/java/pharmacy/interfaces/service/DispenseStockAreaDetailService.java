package inpatientWeb.pharmacy.interfaces.service;

import inpatientWeb.Auth.exception.UnprocessableEntityException;
import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;
import inpatientWeb.Global.ecw.ambulatory.com.ecw.security.DataValidation;
import inpatientWeb.pharmacy.interfaces.dao.DispenseStockAreaDetailDao;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.DispensingInterfaceKeyConstants;
import inpatientWeb.utils.IPTzUtils;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
@Lazy
@Scope("prototype")
public class DispenseStockAreaDetailService {
	
	@Autowired
	DispenseStockAreaDetailDao dispenseStockDetailDao;
	
	private Map<String,Object> stockDetailResponseMap;
	
	/**
	 * @param patientLocationMap
	 * @purpose API to get  stock area detail based on patient Location details
	 * @return Map<String,Object> with 'Data' key containing Stock Area Details map.
	 */
	public Map<String,Object> getStockAreaDetailBasedOnPatientLocation(Map<String,Object> patientLocationMap){
			stockDetailResponseMap=new HashMap<>();
		try{
			if(null==patientLocationMap || patientLocationMap.isEmpty())
				throw new UnprocessableEntityException("Invalid value.Please provide a valid value.");
			
			List<Map<String,Object>> stockDetailList=null;
			stockDetailResponseMap.put(DispensingInterfaceKeyConstants.STATUS.getKeyConstants(),DispensingInterfaceKeyConstants.FAILED.getKeyConstants());
			stockDetailResponseMap.put("Data", "");
			stockDetailResponseMap.put(DispensingInterfaceKeyConstants.MESSAGE.getKeyConstants(),"Failed to retrieve stock area details for given patient location");
			stockDetailList=dispenseStockDetailDao.getStockAreaDetailBasedOnPatientLocation(patientLocationMap);
			if(null!=stockDetailList && !stockDetailList.isEmpty()){
				stockDetailResponseMap.put(DispensingInterfaceKeyConstants.STATUS.getKeyConstants(),DispensingInterfaceKeyConstants.SUCCESS.getKeyConstants());
				stockDetailResponseMap.put("Data", stockDetailList);
				stockDetailResponseMap.put(DispensingInterfaceKeyConstants.MESSAGE.getKeyConstants(),"Data found successfully");
			}
			else{
				stockDetailResponseMap.put(DispensingInterfaceKeyConstants.MESSAGE.getKeyConstants(),"There is no stock area mapped for given patient location.");
			}
		}catch(UnprocessableEntityException ex){	
			stockDetailResponseMap.put(DispensingInterfaceKeyConstants.MESSAGE.getKeyConstants(),"Failed to retrieve stock area details for given patient location :Reason (Invalid value)");
			EcwLog.AppendExceptionToLog(ex);
		}catch(DataAccessException ex){	
			EcwLog.AppendExceptionToLog(ex);
		}
		return stockDetailResponseMap;
	}

	
	/**
	 * @param medicationLocationMap
	 * @purpose API to validate drug dispense code based on stock area 
	 * @return Map<String,Object> with 'Data' key containing success or failure
	 */
	public Map<String,Object> validateDrugDispenseCodeBasedOnStockArea(Map<String,Object> medicationLocationMap){
		stockDetailResponseMap=new HashMap<>();
		List<Map<String,Object>> stockDetailList=null;
		try{
			if(null==medicationLocationMap || "".equals(medicationLocationMap))
				throw new UnprocessableEntityException("Invalid value");
			
			stockDetailResponseMap.put(DispensingInterfaceKeyConstants.STATUS.getKeyConstants(),DispensingInterfaceKeyConstants.FAILED.getKeyConstants());
			stockDetailResponseMap.put("Data", "");
			stockDetailResponseMap.put(DispensingInterfaceKeyConstants.MESSAGE.getKeyConstants(),"Failed to validate drug dispense code available in given stock area location");
			stockDetailList=dispenseStockDetailDao.getStockAreaDetailBasedOnMedicationLocation(medicationLocationMap);
			if(null!=stockDetailList && !stockDetailList.isEmpty()){
				stockDetailResponseMap.put(DispensingInterfaceKeyConstants.STATUS.getKeyConstants(),DispensingInterfaceKeyConstants.SUCCESS.getKeyConstants());
				stockDetailResponseMap.put("Data", stockDetailList);
				stockDetailResponseMap.put(DispensingInterfaceKeyConstants.MESSAGE.getKeyConstants(),"Drug dispense code is available in given stock area location");
			}
			else{
				stockDetailResponseMap.put(DispensingInterfaceKeyConstants.MESSAGE.getKeyConstants(),"Drug dispense code is not mapped to a given stock area location");
			}
		}catch(UnprocessableEntityException ex){	
			stockDetailResponseMap.put(DispensingInterfaceKeyConstants.MESSAGE.getKeyConstants(),"Failed to validate drug dispense code available in given stock area location :Reason (Invalid value)");
			EcwLog.AppendExceptionToLog(ex);
		}catch(DataAccessException ex){	
			EcwLog.AppendExceptionToLog(ex);
		}
		return stockDetailResponseMap; 
	}
	
	/**
	 * @param dispenseStockId
	 * @purpose API to get List of drug dispense formulary detail based on stock area  
	 * @return Map<String,Object> with 'Data' key containing List of Drug Dispense Formulary Details map.
	 * @throws Exception 
	 */
	public Map<String,Object> getListOfDrugDispenseFormularyBasedOnStockArea(Map<String,Object> reqStockAreaMap){
		stockDetailResponseMap=new HashMap<>();
		int dispenseStockId=0;
		int userId=0;
		try{
			if(null==reqStockAreaMap || reqStockAreaMap.isEmpty())
				throw new UnprocessableEntityException("Invalid value");
            
			List<Map<String,Object>> stockDetailList=null;
			stockDetailResponseMap.put(DispensingInterfaceKeyConstants.STATUS.getKeyConstants(),DispensingInterfaceKeyConstants.FAILED.getKeyConstants());
			stockDetailResponseMap.put("Data", "");
			stockDetailResponseMap.put(DispensingInterfaceKeyConstants.MESSAGE.getKeyConstants(),"Failed to retrieve drug dispense formulary details for given stock area");
			
			dispenseStockId = Integer.parseInt(DataValidation.sanitizeInt(reqStockAreaMap.get("dispenseStockId").toString()));
			userId = Integer.parseInt(DataValidation.sanitizeInt(reqStockAreaMap.get("userId").toString()));				
			
			stockDetailList=dispenseStockDetailDao.getListOfDrugDispenseFormularyBasedOnStockArea(dispenseStockId);
			stockDetailList=formatDateForStockAreaDetails(stockDetailList,userId);
			if(null!=stockDetailList && !stockDetailList.isEmpty()){
				stockDetailResponseMap.put(DispensingInterfaceKeyConstants.STATUS.getKeyConstants(),DispensingInterfaceKeyConstants.SUCCESS.getKeyConstants());
				stockDetailResponseMap.put("Data", stockDetailList);
				stockDetailResponseMap.put(DispensingInterfaceKeyConstants.MESSAGE.getKeyConstants(),"Data found successfully");
			}
			else{
				stockDetailResponseMap.put(DispensingInterfaceKeyConstants.MESSAGE.getKeyConstants(),"No drug dispense formulary details found for given stock area");
			}
		}catch(DataAccessException ex){	
			EcwLog.AppendExceptionToLog(ex);
		}catch(Exception runEx){	
			EcwLog.AppendExceptionToLog(runEx);
		}
		return stockDetailResponseMap;
	}
	private List<Map<String, Object>> formatDateForStockAreaDetails(List<Map<String, Object>> stockDetailList,int userId) {
		try{
			String userTimeZone=IPTzUtils.getTimeZoneForResource(userId);
			if(stockDetailList!=null && !stockDetailList.isEmpty()) {
				for (Map<String, Object> stockMapObject : stockDetailList) {	
					stockMapObject.put("modifieddatetime",IPTzUtils.convertDateStrInTz(stockMapObject.get("modifieddatetime").toString(),  "yyyy-MM-dd hh:mm:ss" , IPTzUtils.DEFAULT_DB_TIME_ZONE, "MM/dd/yyyy hh:mm:ss", userTimeZone ));
				}
			}
		}catch(ParseException ex){	
			EcwLog.AppendExceptionToLog(ex);
		}
		return stockDetailList;
	}
}
