package inpatientWeb.pharmacy.serviceImpl;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import inpatientWeb.Auth.exception.InvalidParameterException;
import inpatientWeb.Global.ecw.ambulatory.catalog.Root;
import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;
import inpatientWeb.Global.medicationhelper.dao.RxDetailsDTO;
import inpatientWeb.Global.rxOrderRadixTree.RxOrderTreeServices;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.FormularySetupDao;
import inpatientWeb.admin.pharmacySettings.generalSettings.model.PharmacySettingEmarSetting;
import inpatientWeb.admin.pharmacySettings.generalSettings.service.PharmacySettingsService;
import inpatientWeb.admin.pharmacySettings.intervensionReason.dao.IntervensionReasonDao;
import inpatientWeb.admin.pharmacySettings.pharmacyHelpers.PharmacyConstants;
import inpatientWeb.cpoe.orders.medication.MedicationDispenseDetailDTO;
import inpatientWeb.cpoe.orders.medication.MedicationOrder;
import inpatientWeb.cpoe.orders.medication.iss.ISSActiveMedicationOrderService;
import inpatientWeb.cpoe.util.CPOEEnum;
import inpatientWeb.cpoe.util.CPOEException;
import inpatientWeb.cpoe.util.OrdersUtil;
import inpatientWeb.cpoe.util.components.StatusUtil;
import inpatientWeb.eMAR.dao.api.EMARDao;
import inpatientWeb.eMAR.service.api.EMARService;
import inpatientWeb.him.abstraction.dao.AbstractionHelperDAO;
import inpatientWeb.him.abstraction.utils.EpisodeType;
import inpatientWeb.pharmacy.beans.MedOrder;
import inpatientWeb.pharmacy.beans.MedOrderDetail;
import inpatientWeb.pharmacy.beans.WorkQueue;
import inpatientWeb.pharmacy.dao.WorkQueueDAO;
import inpatientWeb.pharmacy.dao.WorkQueueFormularyDAO;
import inpatientWeb.pharmacy.service.PharmacyMedOrderService;
import inpatientWeb.pharmacy.service.PharmacyOrderScheduleService;
import inpatientWeb.registration.appointment.dto.SubEncounterData;
import inpatientWeb.registration.appointment.service.AppointmentService;
import inpatientWeb.utils.IPTzUtils;
import inpatientWeb.utils.Util;

@Service
public class PharmacyMedOrderServiceImpl implements PharmacyMedOrderService {

	@Autowired
	public WorkQueueDAO workQueueDAO;

	@Autowired
	public WorkQueueFormularyDAO workQueueFormularyDAO;

	@Autowired
	public FormularySetupDao formularySetupDAO;

	@Autowired
	public EMARDao emarDAO;

	@Autowired
	AppointmentService appointmentService;

	@Autowired
	private AbstractionHelperDAO abstractionHelperDAO;

	@Autowired
	private OrdersUtil ordersUtil;	

	@Autowired
	PharmacyOrderScheduleService pharmacyOrderScheduleService;

	@Autowired
	PharmacySettingsService pharmacySettingsService;

	@Autowired
	EMARService emarService;

	@Autowired
	IntervensionReasonDao interventionReasonDAO;

	@Autowired
	RxOrderTreeServices rxOrderTreeServices;
	
	@Autowired
	ISSActiveMedicationOrderService issActiveMedicationOrderService;
	
	@Autowired
	private StatusUtil statusUtil;
	

	public void setAbstractionHelperDAO(AbstractionHelperDAO abstractionHelperDAO) {
		this.abstractionHelperDAO = abstractionHelperDAO;
	}
	
	public void setOrdersUtil(OrdersUtil ordersUtil) {
		this.ordersUtil = ordersUtil;
	}

	public MedOrder getMedicationOrder(int orderId, int patientId, int loggedInUserId){
		if(orderId <= 0){
			throw new InvalidParameterException("Invalid orderId");
		}
		if(patientId <= 0){
			throw new InvalidParameterException("Invalid patientId");
		}
		if(loggedInUserId <= 0){
			throw new InvalidParameterException("Invalid userId");
		}

		String userTimeZone = IPTzUtils.getTimeZoneForResource(loggedInUserId);

		MedOrder medOrder = workQueueDAO.getMedicationOrder(orderId, patientId, loggedInUserId);

		medOrder.setOrderEntryMethodName(this.getOrderEntryMethodName(medOrder.getOrderEntryMethod()));
		medOrder.setFrequencySchedule(pharmacyOrderScheduleService.getFrequencySchedule(medOrder.getOrderId(), medOrder.getFrequencyId()));	
		medOrder.setLastTaken(this.getLastTaken(medOrder.getMedOrderId() ,userTimeZone));
		medOrder.setEmarSchedule(emarService.getScheduleListByOrderId(medOrder.getMedOrderId(), userTimeZone));
		medOrder.setInterventionStatus(interventionReasonDAO.getInterventionStatusByOrderId(medOrder.getOrderId()));		
		medOrder.setDiscontinued(medOrder.getOrderStatus() == statusUtil.getOrderStatusId(CPOEEnum.ORDER_STATUS.DISCONTINUED.status()) || medOrder.getOrderStatus() == statusUtil.getOrderStatusId(CPOEEnum.ORDER_STATUS.AUTO_DISCONTINUED.status()));

		//medOrder.setIndication(indication); pull from formulary

		medOrder.setRoutedGenericItemId(workQueueFormularyDAO.getRoutedGenericItemIdByItemId(medOrder.getItemId()));

		int facilityId=emarDAO.getFacilityIdByEnc(medOrder.getEpisodeEncounterId());
		medOrder.setFacilityId(facilityId);
		medOrder.setPatientId(patientId);

		this.setPropertiesFromMedOrderDetail(medOrder);
		try {
		if(medOrder.isISSFlag())
			medOrder.setIssTemplateData(issActiveMedicationOrderService.getActiveMedicationISSTemplate(medOrder.getOrderId()));
		} catch(CPOEException e) {
			EcwLog.AppendExceptionToLog(e);
		}

		SubEncounterData subEnc = this.getSubEncounterData(medOrder.getEpisodeEncounterId(), medOrder.getOrderDateTime());
		if(subEnc != null){
			medOrder.setEncounterId(subEnc.getEncounterId());
			medOrder.setEncounterType(subEnc.getEncType());
			medOrder.setEncDeptId(Integer.parseInt(subEnc.getDepartMent()));
			medOrder.setEncPatientType(subEnc.getPatientType());
			medOrder.setEncServiceType(subEnc.getServiceType());
			medOrder.setEpisodeType(abstractionHelperDAO.getEpisodeType(medOrder.getEpisodeEncounterId()) == EpisodeType.INPATIENT ? "INPATIENT" : "OUTPATIENT");

			medOrder.setEncPracticeId(this.getPracticeId(subEnc.getEncounterId()));
			medOrder.setEncUnitId(Integer.parseInt(subEnc.getUnit() != null ? subEnc.getUnit() :  "0"));
			medOrder.setEncAreaId(subEnc.getCurrAreaId());
			medOrder.setEncBedId(subEnc.getCurrBedId());
		}

		return medOrder;

	}

	public String getOrderEntryMethodName(int orderEntryMethodId){
		String result = "";

		try{
			List<Map<String, String>> orderEntryMethodList = ordersUtil.getListFromGlobalList(CPOEEnum.GLOBAL_LIST_ORDER_PROTOCOL);
			for(Map<String, String> orderEntry : orderEntryMethodList){
				if(String.valueOf(orderEntry.get("Id")).equals(String.valueOf(orderEntryMethodId))){
					result = Root.TrimString(orderEntry.get("ItemDescription"));
					break;
				}
			}
		}catch(Exception e){
			EcwLog.AppendExceptionToLog(e);
		}

		return result;
	}

	public String getLastTaken(int medOrderId, String userTimeZone){
		
		if(medOrderId <= 0){
			throw new InvalidParameterException("Invalid medOrderId");
		}
		if(Util.isNullEmpty(userTimeZone)){
			throw new InvalidParameterException("Invalid userTimeZone");
		}
		
		String result = "";

		try{
			Date lastTaken = emarDAO.getLastTaken(medOrderId);
			if(lastTaken != null ){
				SimpleDateFormat df = new SimpleDateFormat(IPTzUtils.DEFAULT_DB_DT_FMT);
				result = IPTzUtils.convertDateStrInTz(df.format(lastTaken), IPTzUtils.DEFAULT_DB_DT_FMT , IPTzUtils.DEFAULT_DB_TIME_ZONE, IPTzUtils.DEFAULT_USER_DT_FMT, userTimeZone );
			}
		} catch (ParseException e){
			EcwLog.AppendExceptionToLog(e);
		}

		return result;
	}

	public int getPracticeId(int encounterId){
		
		if(encounterId <= 0){
			throw new InvalidParameterException("Invalid encounterId");
		}

		Map<String, Object> encDetails = workQueueDAO.getEncounterDetails(encounterId);
		return Util.getIntValue( encDetails, "practiceId");

	}

	public SubEncounterData getSubEncounterData(int episodeEncounterId, String orderDateTime){
		
		if(episodeEncounterId <= 0){
			throw new InvalidParameterException("Invalid episodeEncounterId");
		}
		if(Util.isNullEmpty(orderDateTime)){
			throw new InvalidParameterException("Invalid orderDateTime");
		}
		SubEncounterData result = null;
		try{
			inpatientWeb.Global.ecw.ambulatory.json.JSONObject encounterData = appointmentService.getActiveApptTrailDetail(episodeEncounterId, orderDateTime, false);

			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("subEncounterId", encounterData.getInt("subEncId"));

			List<SubEncounterData> encDataList = appointmentService.getSubEncData(paramMap);

			result = encDataList.get(0);
		}catch(Exception e){
			EcwLog.AppendExceptionToLog(e);
		}
		return result;
	}

	public boolean isMedAvailableForFacility(MedOrder medOrder){
		if(medOrder==null){
			throw new InvalidParameterException("Invalid MedOrder Details");
		}
		List<MedOrderDetail> medOrderDetailList = medOrder.getMedOrderDetailList();
		List<Integer> formularyIds = new ArrayList<Integer>();
		for(MedOrderDetail medOrderDetail : medOrderDetailList){
			formularyIds.add(medOrderDetail.getDrugFormularyId());
		}
		int facilityId = emarDAO.getFacilityIdByEnc(medOrder.getEpisodeEncounterId());
		return workQueueFormularyDAO.isMedsAvailableForFacility(formularyIds, facilityId);
	}

	public MedicationDispenseDetailDTO getMedOrderDetailCPOEObj(MedOrder medOrder, MedOrderDetail medOrderDetail) throws Exception {
		if(medOrder==null){
			throw new InvalidParameterException("Invalid MedOrder");
		}
		if(medOrderDetail==null){
			throw new InvalidParameterException("Invalid MedOrder Details");
		}
		MedicationDispenseDetailDTO medOrderDetailCPOE = new MedicationDispenseDetailDTO();

		medOrderDetailCPOE.setMedOrderId(medOrder.getMedOrderId());
		medOrderDetailCPOE.setIpItemId(medOrderDetail.getIpItemId());
		medOrderDetailCPOE.setStrength(medOrderDetail.getOrderStrength());
		medOrderDetailCPOE.setStrengthUom("");
		medOrderDetailCPOE.setFormulation(medOrderDetail.getOrderFormulation());
		medOrderDetailCPOE.setDispense(medOrderDetail.getDispense());
		medOrderDetailCPOE.setNdcCode(medOrderDetail.getOrderNDCCode());
		medOrderDetailCPOE.setDdid(String.valueOf(medOrderDetail.getOrderDDID()));
		medOrderDetailCPOE.setFormularyId(medOrderDetail.getDrugFormularyId());
		medOrderDetailCPOE.setNonBillable(medOrderDetail.isNonBillable() ? 1 : 0);
		if(medOrderDetail.isIvDiluent() || medOrderDetail.isAdditive()){
			medOrderDetailCPOE.setIvVolume(Double.parseDouble(Root.TrimInteger(medOrderDetail.getOrderDose())));
			medOrderDetailCPOE.setIvVolumeUom(medOrderDetail.getFormularyDoseUOM());
		}
		medOrderDetailCPOE.setIvDiluentId(medOrderDetail.getIvDiluentId());
		medOrderDetailCPOE.setDispenseUom(medOrderDetail.getFormularyDispenseUOM());
		medOrderDetailCPOE.setDispenseQty(medOrderDetail.getDispenseQty());
		medOrderDetailCPOE.setIsDiluent(medOrderDetail.isIvDiluent() ? 1 : 0);
		medOrderDetailCPOE.setIsAdditive(medOrderDetail.isAdditive() ? 1 : 0);
		medOrderDetailCPOE.setActualDispense(medOrderDetail.getActualDispense());
		medOrderDetailCPOE.setDisplayIndex(medOrderDetail.getDisplayIndex());

		medOrderDetailCPOE.setDispenseSize(medOrderDetail.getFormularyDispenseSize());
		medOrderDetailCPOE.setOrderDose(medOrderDetail.getOrderDose());
		medOrderDetailCPOE.setFormularyDose(medOrderDetail.getFormularyDoseSize());
		medOrderDetailCPOE.setFormularyDoseUom(medOrderDetail.getFormularyDoseUOM());
		medOrderDetailCPOE.setRxNorm(medOrderDetail.getRxNorm());
		return medOrderDetailCPOE;
	}

	public MedicationOrder getMedOrderCPOEObj(MedOrder medOrder) {
		if(medOrder==null){
			throw new InvalidParameterException("Invalid MedOrder");
		}
		MedicationOrder medOrderCPOE = new MedicationOrder();
		medOrderCPOE.setPriorityId(medOrder.getPriorityId());
		medOrderCPOE.setPtOrderId(medOrder.getOrderId());
		medOrderCPOE.setMedOrderId(medOrder.getMedOrderId());

		RxDetailsDTO rxDetailsObj = new RxDetailsDTO();
		rxDetailsObj.setOrderEntryInstructions(medOrder.getOrderEntryInstruction());
		rxDetailsObj.setInternalNotes(medOrder.getInternalNotes());
		medOrderCPOE.setRxDetailsObj(rxDetailsObj);
		return medOrderCPOE;
	}

	public List<WorkQueue> getOrderLogs(int patientId, int loggedInUserId){

		if(patientId <= 0){
			throw new InvalidParameterException("Invalid patientId");
		}
		if(loggedInUserId <= 0){
			throw new InvalidParameterException("Invalid loggedInUserId");
		}

		return workQueueDAO.getMedicationProfileData(patientId, PharmacyConstants.PharmacyVerificationStatus.VERIFIED.getId(), 0, loggedInUserId);
	}

	private boolean isDualVerification(boolean dualVerification, boolean dualVerificationFormulary, int facilityId, int patientId){
		boolean result = dualVerification;

		if(!result){
			PharmacySettingEmarSetting eMARSetting = pharmacySettingsService.getPharmacyEmarSettings(facilityId);
			result = emarService.getdualVerifyByAgeFlag(eMARSetting, patientId);

			if(!result){
				result = dualVerificationFormulary;
			}
		}

		return result;
	}

	private void setPropertiesFromMedOrderDetail(MedOrder medOrder) {

		ArrayList<MedOrderDetail> orderDetailList = workQueueDAO.getMedOrderDetailsByMedOrderId(medOrder.getMedOrderId(), medOrder.getFacilityId(), medOrder.getOrderTypeInt());
		medOrder.setMedOrderDetailList(orderDetailList);

		boolean billable = false;
		boolean floorStock = true;
		boolean isPCA = false; 
		double totalCost = 0.0;
		double totalHcpcsUnit = 0.0;
		double costCalculatedWithChargeType = 0.0;
		int mainProductFormularyId = -1;
		boolean drugdispenseCode = true;
		boolean chargeAtDispense = false;
		boolean drugTypeBulk = false;
		boolean dualVerificationFormulary = false;

		ArrayList<MedOrderDetail> availableDiluents = new ArrayList<MedOrderDetail>();

		TreeSet<Integer> diluentRoutetedIdSet = new TreeSet<Integer>();

		for(MedOrderDetail medOrderDetail : orderDetailList){
			if(!medOrderDetail.isNonBillable()){
				billable = true;
			}

			if(medOrderDetail.getTotalCostCalculatedWithChargeType() > 0){
				totalCost += medOrderDetail.getTotalCostCalculatedWithChargeType() ;
			}
			
			if(medOrderDetail.getToTalHCPCSUnit() > 0){
				totalHcpcsUnit = medOrderDetail.getToTalHCPCSUnit();
			}
			
			if(medOrderDetail.getCostCalculatedWithChargeType() > 0) {
				costCalculatedWithChargeType = medOrderDetail.getCostCalculatedWithChargeType();
			}

			if(medOrderDetail.isPCA()){
				isPCA = true;
			}

			if(!medOrderDetail.isFloorStock()){
				floorStock = false;
			}

			if(this.isMainProduct(medOrderDetail)){
				mainProductFormularyId = medOrderDetail.getDrugFormularyId();
			} else {
				setAvailableDiluents(medOrder.getOrderTypeInt(), medOrder.getFacilityId(), availableDiluents, diluentRoutetedIdSet, medOrderDetail);
			}

			if(Util.isNullEmpty(medOrderDetail.getDrugDispenseCode())){
				drugdispenseCode = false;
			}

			Map<String, Object> commonSettingsMap = workQueueFormularyDAO.getFormularyCommonSettingsByRoutedGenericItemId(mainProductFormularyId);
			if(commonSettingsMap !=null) {
				drugTypeBulk = this.setDrugTypeBulk(drugTypeBulk, commonSettingsMap);
				chargeAtDispense = this.setChargeAtDispense(chargeAtDispense, commonSettingsMap);
				dualVerificationFormulary = this.setDualVerification(dualVerificationFormulary, commonSettingsMap);
			}			
		}

		DecimalFormat df = new DecimalFormat("#.###");
		totalCost = Double.parseDouble(df.format(totalCost));
		medOrder.setToTalHcpcsUnit(totalHcpcsUnit);
		medOrder.setAvailableDiluents(availableDiluents);
		medOrder.setAvailableProducts(this.getAvailableProducts(medOrder.getOrderTypeInt(), medOrder.getRoutedGenericItemId(), medOrder.getFacilityId(), mainProductFormularyId));

		medOrder.setBrandName(rxOrderTreeServices.getAssignedBrandName(mainProductFormularyId, medOrder.getDrugName(), true, true));
		medOrder.setDrugDispenseCode(drugdispenseCode);
		medOrder.setFloorStock(floorStock);
		medOrder.setBillable(billable && !medOrder.isOwnMed());
		medOrder.setDrugTypeBulk(drugTypeBulk);
		medOrder.setChargeAtDispense(chargeAtDispense);
		medOrder.setDualVerification(this.isDualVerification(medOrder.isDualVerification(), dualVerificationFormulary, medOrder.getFacilityId(), medOrder.getPatientId()));
		medOrder.setPCA(isPCA);
		medOrder.setTotalPrice(totalCost);
		medOrder.setCostCalculatedWithChargeType(costCalculatedWithChargeType);
	}

	private boolean setDualVerification(boolean dualVerificationFormulary, Map<String, Object> commonSettingsMap) {
		if(((Integer)commonSettingsMap.get("dualverifyreqd")) == 1){
			dualVerificationFormulary = true;
		}
		return dualVerificationFormulary;
	}

	private boolean setChargeAtDispense(boolean chargeAtDispense, Map<String, Object> commonSettingsMap) {
		if(Util.getBooleanValue(commonSettingsMap, "ischargeableatdispense")){
			chargeAtDispense = true;
		}
		return chargeAtDispense;
	}

	private boolean setDrugTypeBulk(boolean drugTypeBulk, Map<String, Object> commonSettingsMap) {
		if(Util.getBooleanValue(commonSettingsMap, "isdrugtypebulk")){
			drugTypeBulk = true;
		}
		return drugTypeBulk;
	}

	private boolean isMainProduct(MedOrderDetail medOrderDetail){
		return  !medOrderDetail.isIvDiluent() && !medOrderDetail.isAdditive() && medOrderDetail.getIvDiluentId() == 0 ;
	}

	private void setAvailableDiluents(int orderType, int facilityId, ArrayList<MedOrderDetail> availableDiluents, TreeSet<Integer> diluentRoutetedIdSet, MedOrderDetail medOrderDetail) {
		int diluentRoutedGenericItemId =  workQueueFormularyDAO.getRoutedGenericItemIdByItemId(medOrderDetail.getFormularyItemId());
		if(!diluentRoutetedIdSet.contains(diluentRoutedGenericItemId)){
			diluentRoutetedIdSet.add(diluentRoutedGenericItemId);
			availableDiluents.addAll(formularySetupDAO.getAvailableProductsByItemId(diluentRoutedGenericItemId, orderType, formularySetupDAO.getFormulationExternalByFormularyId(medOrderDetail.getDrugFormularyId()), facilityId));
		}
	}

	private List<MedOrderDetail> getAvailableProducts(int orderType, int routedGenericItemId, int facilityId, int mainProductFormularyId) {
		String formulation = "";
		if(mainProductFormularyId != -1){
			formulation = formularySetupDAO.getFormulationExternalByFormularyId(mainProductFormularyId);
		}
		return formularySetupDAO.getAvailableProductsByItemId(routedGenericItemId, orderType, formulation, facilityId);
	}

}
