package inpatientWeb.pharmacy.interaction.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;
import inpatientWeb.Global.medicationhelper.constant.MedicationConstants;
import inpatientWeb.Global.medicationhelper.constant.MedicationConstants.CPOEMedicationUserMsg;
import inpatientWeb.Global.medicationhelper.util.RxUtil;
import inpatientWeb.admin.pharmacySettings.generalSettings.service.PharmacySettingsService;
import inpatientWeb.admin.usermanagement.securitysettings.annotation.CheckForUserAccess;
import inpatientWeb.cpoe.orders.core.Order;
import inpatientWeb.cpoe.orders.medication.MedicationOrderService;
import inpatientWeb.cpoe.orders.medication.stagingorder.StagingMedicationOrderService;
import inpatientWeb.cpoe.util.CPOEException;
import inpatientWeb.cpoe.util.OrderRequestParam;
import inpatientWeb.cpoe.util.OrderResponse;
import inpatientWeb.pharmacy.interaction.constants.InteractionConstants.INTERACTION;
import inpatientWeb.pharmacy.interaction.constants.InteractionConstants.INTERACTION_FORMAT;
import inpatientWeb.pharmacy.interaction.constants.InteractionConstants.INTERACTION_MESSAGE;
import inpatientWeb.pharmacy.interaction.service.InteractionDetailsService;
import inpatientWeb.pharmacy.interaction.service.InteractionService;
import inpatientWeb.utils.IPTzUtils;
import inpatientWeb.utils.StatusMap;
import inpatientWeb.utils.Util;

/**
 * @author Disahgan Bhavsar
 * @Copyright: eClinicalWorks LLC.
 * @Date: "July 27, 2017"
 */

@Controller
@SessionAttributes({"uid", "UserType"})
public class InteractionController {
	
	private InteractionService interactionService;
	private InteractionDetailsService interactionDetailsService;  
	private PharmacySettingsService pharmacySettingService;
	private StagingMedicationOrderService stagingMedicationOrderService;
	private MedicationOrderService medicationOrderService;
	
	public InteractionService getInteractionService() {
		return interactionService;
	}

	/**
	 * 
	 * @param request
	 * @param requestMap
	 * @param userId
	 * @return
	 * @throws CPOEException 
	 */
//	@CheckForUserAccess(securitySettingKeys = MedicationConstants.ALLOW_TO_OVERRIDE_INTERACTION_KEY)
	@RequestMapping(value = "IPOrders.go/NewOrders/getStagingOrder",  method = {RequestMethod.POST})
	@ResponseBody public Order getStagingOrder(@RequestBody Map<String, Object> requestMap) throws CPOEException{
		OrderRequestParam orderRequestParam = stagingMedicationOrderService.getRequestParamToGetStagingOrderFromStagingOrderIds(RxUtil.addToGenericList(new ArrayList<Integer>(1), Util.getIntValue(requestMap,"stagingOrderId")));
		OrderResponse orderResponse = medicationOrderService.getOrders(orderRequestParam);
		if (orderResponse == null || Util.getSize(orderResponse.getOrderList()) < 1) {
			throw new CPOEException(CPOEMedicationUserMsg.STAGING_MED_ORDER_NOT_FOUND);
		}
		return orderResponse.getOrderList().get(0);
	}
	
	/**
	 * 
	 * @param request
	 * @param requestMap
	 * @param userId
	 * @return
	 */
	@CheckForUserAccess(securitySettingKeys = MedicationConstants.ALLOW_TO_OVERRIDE_INTERACTION_KEY)
	@RequestMapping(value = "IPOrders.go/NewOrders/saveInteractionAction",  method = {RequestMethod.POST})
	@ResponseBody public StatusMap saveInteractionAction(HttpServletRequest request, @RequestBody Map<String, Object> requestMap,@ModelAttribute("uid") Integer userId){
		StatusMap oStatus =  new StatusMap();
		try{	
			requestMap.put(INTERACTION.UID.text(), userId);
			return interactionService.saveInteractionAction(requestMap);
		}
		catch(RuntimeException ex){
			EcwLog.AppendExceptionToLog(ex);
			oStatus.setFail(INTERACTION_MESSAGE.INTERACTION_OVERRIDE_EXCEPTION.text());
			oStatus.setObject(null);
			return oStatus;
		}
	}
	
	/**
	 * 
	 * @param request
	 * @param requestMap
	 * @param userId
	 * @return
	 */
	@CheckForUserAccess(securitySettingKeys = MedicationConstants.ALLOW_TO_ACKNOWLEDGE_INTERACTION_KEY)
	@RequestMapping(value = "IPOrders.go/NewOrders/acknowledgeInteraction",  method = {RequestMethod.POST})
	@ResponseBody public StatusMap updateInteractionAction(HttpServletRequest request, @RequestBody Map<String, Object> requestMap,@ModelAttribute("uid") Integer userId){
		StatusMap oStatus =  new StatusMap();
		try{	
			String formatedDate = IPTzUtils.getDateTimeStrForUser(userId, INTERACTION_FORMAT.DATE_TIME_FORMAT.format());
			requestMap.put(INTERACTION.ACKNOWLEDGE_FLAG.text(), 1);
			requestMap.put(INTERACTION.ACKNOWLEDGE_BY.text(), userId);
			requestMap.put(INTERACTION.ACKNOWLEDGE_DATE_TIME.text(), formatedDate);
			return interactionService.updateInteractionAction(requestMap);
		}
		catch(RuntimeException ex){
			EcwLog.AppendExceptionToLog(ex);
			oStatus.setFail(INTERACTION_MESSAGE.INTERACTION_ACKNOWLEDGE_EXCEPTION.text());
			oStatus.setObject(null);
			return oStatus;
		}
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "Interaction.go/AdminSetting/getFormularyInteraction",  method = {RequestMethod.POST})
	@ResponseBody public StatusMap getFormularyInteraction(HttpServletRequest request, @RequestBody Map<String, Object> requestMap){
		StatusMap oStatus =  new StatusMap();
		try		
		{	
			List<Map<String, Object>> drugDoseOrders = (List<Map<String, Object>>) requestMap.get("dataList");
			List<Map<String, Object>> respMap = interactionDetailsService.getFormularyInteraction(drugDoseOrders);
			oStatus.setObject(respMap);
			oStatus.setSuccess();
			return oStatus;
		}
		catch(RuntimeException ex){
			EcwLog.AppendExceptionToLog(ex);
			oStatus.setObject(null);
			oStatus.setFail(INTERACTION_MESSAGE.MEDICATION_ORDER_LOAD_EXCEPTION.text());
			return oStatus;
		}
	}
	
	
	@Autowired
	public void setInteractionService(InteractionService interactionService) {
		this.interactionService = interactionService;
	}

	public PharmacySettingsService getPharmacySettingService() {
		return pharmacySettingService;
	}

	@Autowired
	public void setPharmacySettingService(PharmacySettingsService pharmacySettingService) {
		this.pharmacySettingService = pharmacySettingService;
	}

	public StagingMedicationOrderService getStagingMedicationOrderService() {
		return stagingMedicationOrderService;
	}

	@Autowired
	public void setStagingMedicationOrderService(StagingMedicationOrderService stagingMedicationOrderService) {
		this.stagingMedicationOrderService = stagingMedicationOrderService;
	}

	public MedicationOrderService getMedicationOrderService() {
		return medicationOrderService;
	}

	@Autowired
	public void setMedicationOrderService(MedicationOrderService medicationOrderService) {
		this.medicationOrderService = medicationOrderService;
	}

	public InteractionDetailsService getInteractionDetailsService() {
		return interactionDetailsService;
	}

	@Autowired
	public void setInteractionDetailsService(InteractionDetailsService interactionDetailsService) {
		this.interactionDetailsService = interactionDetailsService;
	}
}
