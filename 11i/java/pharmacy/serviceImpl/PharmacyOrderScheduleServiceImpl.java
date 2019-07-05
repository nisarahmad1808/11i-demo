package inpatientWeb.pharmacy.serviceImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import inpatientWeb.Auth.exception.InvalidParameterException;
import inpatientWeb.Global.ecw.ambulatory.catalog.Root;
import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;
import inpatientWeb.admin.pharmacySettings.configureDictionary.dao.TemplateForTimeSchedule;
import inpatientWeb.admin.pharmacySettings.configureDictionary.service.ConfigurationDictionaryService;
import inpatientWeb.cpoe.orders.medication.administrationSchedule.service.api.AdministrationScheduleService;
import inpatientWeb.pharmacy.beans.MedOrder;
import inpatientWeb.pharmacy.beans.PharmacyOrderScheduleOrg;
import inpatientWeb.pharmacy.beans.PharmacyOrderScheduleUpdated;
import inpatientWeb.pharmacy.dao.PharmacyOrderScheduleDAO;
import inpatientWeb.pharmacy.service.PharmacyOrderScheduleService;
import inpatientWeb.pharmacy.service.WorkQueueService;
import inpatientWeb.utils.IPTzUtils;

@Service
public class PharmacyOrderScheduleServiceImpl implements PharmacyOrderScheduleService {

	@Autowired
	ConfigurationDictionaryService freqDictionaryService;

	@Autowired
	AdministrationScheduleService administrationScheduleService;

	@Autowired
	PharmacyOrderScheduleDAO pharmacyOrderScheduleDAO;
	
	@Autowired
	WorkQueueService workQueueService;

	public boolean createPharmacyOrderScheduleOrg(PharmacyOrderScheduleOrg pharmacyOrderScheduleOrg) {
		return pharmacyOrderScheduleDAO.createPharmacyOrderScheduleOrg(pharmacyOrderScheduleOrg);
	}

	public boolean createPharmacyOrderScheduleUpdated(PharmacyOrderScheduleUpdated pharmacyOrderScheduleUpdated) {
		return pharmacyOrderScheduleDAO.createPharmacyOrderScheduleUpdated(pharmacyOrderScheduleUpdated);
	}

	public boolean deletePharmacyOrderScheduleUpdated(Map<String, Object> paramMap) {
		if(paramMap == null || paramMap.isEmpty()){
			throw new InvalidParameterException("paramMap cannot be null or empty");
		}
		
		return pharmacyOrderScheduleDAO.deletePharmacyOrderScheduleUpdated(paramMap);
	}

	public List<PharmacyOrderScheduleUpdated> getPharmacyOrderScheduleUpdated(Map<String, Object> paramMap) {
		if(paramMap == null || paramMap.isEmpty()){
			throw new InvalidParameterException("paramMap cannot be null or empty");
		}
		return pharmacyOrderScheduleDAO.getPharmacyOrderScheduleUpdated(paramMap);
	}

	public boolean isPharmacyOrderScheduleOrgExist(Map<String, Object> paramMap){
		if(paramMap == null || paramMap.isEmpty()){
			throw new InvalidParameterException("paramMap cannot be null or empty");
		}
		return pharmacyOrderScheduleDAO.isPharmacyOrderScheduleOrgExist(paramMap);
	}

	public boolean updateOrderSchedule(MedOrder medOrder, int loggedInUserId){
		
		if(medOrder == null ){
			throw new InvalidParameterException("medOrder cannot be null");
		}
		if(loggedInUserId <= 0){
			throw new InvalidParameterException("Invalid loggedInUserId");
		}
		
		boolean result = false;
		try{
			boolean scheduleUpdated = false;

			List<TemplateForTimeSchedule> orgScheduleList = this.getFrequencySchedule(medOrder.getOrderId(), medOrder.getFrequencyId());
			ArrayList<String> orgScheduleTimes = new ArrayList<String>();
			for(TemplateForTimeSchedule orgSchedule : orgScheduleList){
				orgScheduleTimes.add(Root.TrimString(orgSchedule.getScheduledTime()));
			}
			
			List<TemplateForTimeSchedule> updatedScheduleList = medOrder.getFrequencySchedule();
			ArrayList<String> updatedScheduleTimes = new ArrayList<String>();
			for(TemplateForTimeSchedule updatedSchedule : updatedScheduleList){
				updatedScheduleTimes.add(Root.TrimString(updatedSchedule.getScheduledTime()));
			}
			
			if(!orgScheduleTimes.containsAll(updatedScheduleTimes)){
				scheduleUpdated = true;
			}

			if(scheduleUpdated){

				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("orderId", medOrder.getOrderId());
				if(!this.isPharmacyOrderScheduleOrgExist(paramMap)){

					for(TemplateForTimeSchedule orgSchedule : orgScheduleList){
						PharmacyOrderScheduleOrg pharmacyOrderScheduleOrg = new PharmacyOrderScheduleOrg();
						pharmacyOrderScheduleOrg.setOrderId(medOrder.getOrderId());
						pharmacyOrderScheduleOrg.setMedOrderId(medOrder.getMedOrderId());
						pharmacyOrderScheduleOrg.setVerificationId(medOrder.getPharmacyVerificationId());
						pharmacyOrderScheduleOrg.setFrequencyId(medOrder.getFrequencyId());
						pharmacyOrderScheduleOrg.setScheduleId(Integer.parseInt(orgSchedule.getId()));
						pharmacyOrderScheduleOrg.setScheduleTime(orgSchedule.getScheduledTime());
						pharmacyOrderScheduleOrg.setCreatedBy(loggedInUserId);
						this.createPharmacyOrderScheduleOrg(pharmacyOrderScheduleOrg);
					}
				}

				this.deletePharmacyOrderScheduleUpdated(paramMap);

				List<String> updatedTimes = new ArrayList<String>();

				for(TemplateForTimeSchedule updatedSchedule : updatedScheduleList){
					updatedTimes.add(updatedSchedule.getScheduledTime());

					PharmacyOrderScheduleUpdated pharmacyOrderScheduleUpdated = new PharmacyOrderScheduleUpdated();
					pharmacyOrderScheduleUpdated.setOrderId(medOrder.getOrderId());
					pharmacyOrderScheduleUpdated.setMedOrderId(medOrder.getMedOrderId());
					pharmacyOrderScheduleUpdated.setVerificationId(medOrder.getPharmacyVerificationId());
					pharmacyOrderScheduleUpdated.setFrequencyId(medOrder.getFrequencyId());
					pharmacyOrderScheduleUpdated.setScheduleTime(updatedSchedule.getScheduledTime());
					pharmacyOrderScheduleUpdated.setCreatedBy(loggedInUserId);

					this.createPharmacyOrderScheduleUpdated(pharmacyOrderScheduleUpdated);
				}

				SimpleDateFormat df = new SimpleDateFormat(IPTzUtils.DEFAULT_USER_DT_FMT);
				administrationScheduleService.updateScheduleTime(medOrder.getMedOrderId(), df.parse(medOrder.getStartDateTime()), updatedTimes, loggedInUserId);
				
				result = true;
			}
		}catch (Exception e){
			EcwLog.AppendExceptionToLog(e);
		}

		return result;
	}

	@Override
	public boolean isPharmacyOrderScheduleUpdatedExist(Map<String, Object> paramMap) {
		
		if(paramMap == null || paramMap.isEmpty()){
			throw new InvalidParameterException("paramMap cannot be null or empty");
		}
		
		return pharmacyOrderScheduleDAO.isPharmacyOrderScheduleUpdatedExist(paramMap);
	}
	
	public List<TemplateForTimeSchedule> getFrequencySchedule(int orderId, int frequencyId){
		
		if(orderId <= 0){
			throw new InvalidParameterException("Invalid orderId");
		}
		if(frequencyId <= 0){
			throw new InvalidParameterException("Invalid frequencyId");
		}
		
		List<TemplateForTimeSchedule> result = null;

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("orderId", orderId);
		if(this.isPharmacyOrderScheduleUpdatedExist(paramMap)){

			result = new ArrayList<TemplateForTimeSchedule>();
			List<PharmacyOrderScheduleUpdated> pharmacyOrderScheduleUpdatedList =  this.getPharmacyOrderScheduleUpdated(paramMap);
			for(PharmacyOrderScheduleUpdated pharmacyOrderScheduleUpdated : pharmacyOrderScheduleUpdatedList){
				TemplateForTimeSchedule timeSchedule = new TemplateForTimeSchedule();
				timeSchedule.setScheduledTime(pharmacyOrderScheduleUpdated.getScheduleTime());
				timeSchedule.setId("0");
				result.add(timeSchedule);
			}

		} else {
			result = freqDictionaryService.getFrequencyScheduledTimes(frequencyId);
		}


		return result;
	}
}
