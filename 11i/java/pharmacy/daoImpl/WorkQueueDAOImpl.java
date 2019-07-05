package inpatientWeb.pharmacy.daoImpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import inpatientWeb.Global.ecw.ambulatory.CwMobile.CwUtils;
import inpatientWeb.Global.ecw.ambulatory.catalog.Root;
import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;
import inpatientWeb.Global.medicationhelper.constant.MedicationConstants.MedOrderDtlTblColumn;
import inpatientWeb.Global.medicationhelper.constant.MedicationConstants.MedOrderTblColumn;
import inpatientWeb.Global.medicationhelper.util.RxUtil;
import inpatientWeb.Global.rxOrderRadixTree.RxOrderTreeServices;
import inpatientWeb.HomeMedication.dao.DrugNameDTO;
import inpatientWeb.admin.pharmacySettings.configureDictionary.constant.FormularySignatureConstants;
import inpatientWeb.admin.pharmacySettings.formularySetup.dao.FormularySetupDao;
import inpatientWeb.admin.pharmacySettings.intervensionReason.dao.IntervensionReasonDao;
import inpatientWeb.admin.pharmacySettings.pharmacyHelpers.PharmacyConstants;
import inpatientWeb.cpoe.orders.medication.iss.ISSActiveMedicationOrderService;
import inpatientWeb.cpoe.util.CPOEEnum;
import inpatientWeb.cpoe.util.CPOEEnum.ACTIVE_ORDER_TABLE;
import inpatientWeb.cpoe.util.CPOEEnum.ORDER_STATUS;
import inpatientWeb.cpoe.util.components.StatusUtil;
import inpatientWeb.pharmacy.beans.MedOrder;
import inpatientWeb.pharmacy.beans.MedOrderDetail;
import inpatientWeb.pharmacy.beans.OrderPriority;
import inpatientWeb.pharmacy.beans.WorkQueue;
import inpatientWeb.pharmacy.dao.WorkQueueDAO;
import inpatientWeb.pharmacy.util.PharmacyDateUtil;
import inpatientWeb.pharmacy.util.PharmacyQueryUtil;
import inpatientWeb.registration.appointment.dao.AppointmentUtil;
import inpatientWeb.registration.appointment.service.AppointmentService;
import inpatientWeb.utils.DateUtil;
import inpatientWeb.utils.IPTzUtils;
import inpatientWeb.utils.SQLUtil;
import inpatientWeb.utils.Util;

@Repository
public class WorkQueueDAOImpl implements WorkQueueDAO {

	public static final int PHARMACY_LAB_REVIEW_STATUS = 2;
	public static final int MEDICATION_ORDER_IS_SUBSTITUTE = 2;	

	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	AppointmentService appointmentService;

	@Autowired
	FormularySetupDao formularySetupDAO;

	@Autowired
	IntervensionReasonDao interventionReasonDAO;

	@Autowired
	RxOrderTreeServices rxOrderTreeServices;
	
	
	@Autowired
	private StatusUtil statusUtil;
	
	@Autowired
	ISSActiveMedicationOrderService issActiveMedicationOrderService;

	public ArrayList<WorkQueue> getWorkQueueData(Map<String, Object> filterMap) {

		ArrayList<WorkQueue> result  = new ArrayList<WorkQueue>();

		try{
			final int loggedInUserId = Util.getIntValue(filterMap, "loggedInUserId");

			result = (ArrayList<WorkQueue>) namedParameterJdbcTemplate.query(PharmacyQueryUtil.getWorkQueueDataQuery(filterMap).toString(), filterMap, new RowMapper<WorkQueue>(){

				@Override
				public WorkQueue mapRow(ResultSet rs, int arg1) throws SQLException {
					WorkQueue workQueue = new WorkQueue();
					try{

						workQueue.setId(rs.getInt("pvid"));
						workQueue.setPatientName(rs.getString("ptfname") +" "+rs.getString("ptlname"));
						workQueue.setPatientGender(Root.TrimString(rs.getString("ptsex")).length() > 0 ? Root.TrimString(rs.getString("ptsex")).substring(0,1).toUpperCase() : "");
						workQueue.setPatientAge( Root.TrimString(rs.getString("ptdob")).length() > 0 ? CwUtils.CalculateAge(CwUtils.ConvertDateFormat(Root.TrimString(rs.getString("ptdob")),"MM/dd/yyyy","yyyy-MM-dd"), DateUtil.getTodaysDate("yyyy-MM-dd")) : "");
						workQueue.setPharmacyStatusId(rs.getInt("pharmacyStatusId"));
						workQueue.setPharmacyStatus(Root.TrimString(rs.getString("pharmacystatus")));
						workQueue.setOrderStatusId(rs.getInt("orderstatus"));
						workQueue.setOrderStatusName(Root.TrimString(rs.getString("orderstatusname")));
						workQueue.setOrderSetId(rs.getInt("ordersetid"));
						workQueue.setOrderSetName(Root.TrimString(rs.getString("ordersetname")));
						int masterEncId = appointmentService.getLatestSubEncounterId(rs.getInt("episodeencid"));	
						workQueue.setEncounterId(masterEncId);
						workQueue.setEncounterType(rs.getInt("encType"));
						workQueue.setEpisodeEncounterId(rs.getInt("episodeencid"));
						workQueue.setPatientId(rs.getInt("patientId"));
						workQueue.setPatientMRN(Root.TrimString(rs.getString("ptmrn")));
						workQueue.setChiefComplaint(rs.getString("chiefcomplaint"));
						workQueue.setNurseId(rs.getInt("attn_nurse_id"));
						workQueue.setNurseName(Root.TrimString(rs.getString("anfname"))+" "+Root.TrimString(rs.getString("anlname")));
						workQueue.setOrderingProviderId(rs.getInt("orderedbyid"));
						workQueue.setOrderingProviderName(Root.TrimString(rs.getString("providerfname")) +" "+ Root.TrimString(rs.getString("providerlname")));
						workQueue.setOrderDateTime(PharmacyDateUtil.getValidDate(rs.getString("orderdatetime"), loggedInUserId));
						workQueue.setPriorityId(rs.getInt("orderpriority"));
						workQueue.setPriorityName(Root.TrimString(rs.getString("priorityname")));						
						workQueue.setFacilityId(rs.getInt("facilityid"));
						workQueue.setFacilityName(Root.TrimString(rs.getString("facilityname")));
						workQueue.setAreaName(Root.TrimString(rs.getString("areaName")));
						workQueue.setUnitName(Root.TrimString(rs.getString("unitname")));
						workQueue.setBedName(Root.TrimString(rs.getString("bedname")));
						workQueue.setPendingReason(Root.TrimString(rs.getString("pendingReason")));
						workQueue.setAssignedTo(rs.getInt("assignedTo"));
						workQueue.setAssignedToName(Root.TrimString(rs.getString("assignedtofname")) +" "+ Root.TrimString(rs.getString("assignedtolname")));
						workQueue.setServiceType(rs.getInt("serviceType"));						
						workQueue.setWorkFlowType(rs.getInt("workFlowType"));
						setDepartemt(rs, workQueue);
						String orderFormulation = getOrderFormulationByMedOrderId(rs.getInt("medOrderId"));
						String orderDetails = Root.TrimString(rs.getString("itemname")) 
								+" "+Root.TrimString(rs.getString("order_dose")) + Root.TrimString(rs.getString("order_dose_UOM"))
								+" "+orderFormulation
								+" "+Root.TrimString(rs.getString("order_route"))
								+" "+Root.TrimString(rs.getString("order_frequency"));

						workQueue.setOrderDetail(orderDetails);
						workQueue.setISSFlag(rs.getInt("issflag")==1);
						workQueue.setConfiguredISSFlag(rs.getInt("configuredISSFlag") == 1);
						workQueue.setPatientSuppliedFlag(rs.getInt("isPatientSupplied")==1);
						workQueue.setSubstituteFlag(rs.getInt("isSubstitute")==MEDICATION_ORDER_IS_SUBSTITUTE);
						DrugNameDTO itemsObj = new DrugNameDTO();
						itemsObj.setItemID(rs.getInt("ipitemid"));
						itemsObj.setDrugName(itemsObj.getItemID(), true, true);
						workQueue.setItemsObj(itemsObj);

						Map<String,Object> reqParam = new HashMap<>();
						reqParam.put("EncId", rs.getInt("encounterid"));
						workQueue.setAdmittingDiagnosis(PharmacyQueryUtil.getComaSepratedStringFromList(appointmentService.getDiagnosisList(reqParam),"itemName"));
						workQueue.setIssTemplateData(issActiveMedicationOrderService.getActiveMedicationISSTemplate(rs.getInt("ptOrderId")));

					}catch(Exception e){
						EcwLog.AppendExceptionToLog(e);
					}
					return workQueue;
				}

			});

		}catch(Exception e){
			EcwLog.AppendExceptionToLog(e);
		}

		return result;
	}
	private void setDepartemt(ResultSet rs, WorkQueue workQueue) throws Exception {
		if(rs.getInt("workFlowType") == AppointmentUtil.OUTPATIENT_WORKFLOW_TYPE) {
			int encdeptId = 0;	
			Map<String, Object> encDept = getDeptIdbasedonepisodeEncId(rs.getInt("episodeencid"));
			if(!encDept.isEmpty()){
				encdeptId = Integer.parseInt(String.valueOf(encDept.get("deptId")));
				workQueue.setDepartmentId(encdeptId);								
				Map<String, Object> deptObj = appointmentService.getMasterDepartment(encdeptId);
				workQueue.setDepartmentName(Root.TrimString(String.valueOf(deptObj.get("departmentName"))));							
			}
									
		} else {													
			workQueue.setDepartmentId(rs.getInt("deptId"));
			workQueue.setDepartmentName(Root.TrimString(rs.getString("deptname")));
		}
	}

	public int getWorkQueueCount(Map<String, Object> filterMap) {
		return namedParameterJdbcTemplate.queryForObject(PharmacyQueryUtil.getWorkQueueCountQuery(filterMap).toString(), filterMap, Integer.class);
	}

	public ArrayList<OrderPriority> getOrderPriority() {

		Map<String,Object> paramMap = new HashMap<String,Object>();
		StringBuffer strSQL = new StringBuffer("SELECT ");
		strSQL.append(" priority_master.priorityId, ");
		strSQL.append(" priority_master.priorityName, ");
		strSQL.append(" priority_master.deleteFlag ");
		strSQL.append(" FROM ");
		strSQL.append(" ip_order_priority_master priority_master ");
		strSQL.append(" INNER JOIN ip_order_priority_rules priorityRules ON priority_master.priorityId = priorityRules.priorityId  AND priorityRules.deleteFlag = 0 ");
		strSQL.append(" WHERE ");
		strSQL.append(" priorityRules.orderTypeId=(SELECT itemID FROM ip_items WHERE itemName='Medications' AND itemCode = 'MEDICATION' AND itemType = 'C' AND deleteFlag = 0)");	

		return (ArrayList<OrderPriority>) namedParameterJdbcTemplate.query(strSQL.toString(), paramMap, new OrderPriority());

	}

	public Hashtable<String, Integer> getRecordCountForTabs() {

		Hashtable<String, Integer> result = new Hashtable<String, Integer>();

		try{
			Map<String,Object> paramMap = new HashMap<String,Object>();

			result = (Hashtable<String, Integer>) namedParameterJdbcTemplate.query(PharmacyQueryUtil.getWorkQueueRecordCountQuery().toString(), paramMap, new ResultSetExtractor<Hashtable<String, Integer>>(){

				@Override
				public Hashtable<String, Integer> extractData(ResultSet rs) throws SQLException {
					Hashtable<String, Integer> result = new Hashtable<String, Integer>();
					int allCount = 0;
					while(rs.next()){
						try {
							String status = Root.TrimString(rs.getString("status")).toUpperCase();
							int recCount = rs.getInt("reccount");
							
							if(PharmacyConstants.PharmacyVerificationStatus.PENDING.getName().equals(status.toLowerCase())
								|| PharmacyConstants.PharmacyVerificationStatus.UNVERIFIED.getName().equals(status.toLowerCase())
								|| PharmacyConstants.PharmacyVerificationStatus.EXPIRED.getName().equals(status.toLowerCase())
							){
								allCount += recCount;
							}
							result.put(status, recCount);
						} catch (Exception e) {
							EcwLog.AppendExceptionToLog(e);
						}
					}

					result.put("ALL", allCount);
					
					Map<String,Object> oRequestMap = new HashMap<String,Object>();
					oRequestMap.put("received", 1);
					oRequestMap.put("labType", CPOEEnum.LAB_TYPE.LABS.getType());
					oRequestMap.put("DisplayLabResultsInPharmacyQueue", true);
					oRequestMap.put("deleteFlag", 0);
					Map<String, Object> labResult = getTotalLabCounts(oRequestMap);
					int totalLabCount = Util.getIntValue(labResult, "recordCnt");
					result.put("LABCOUNT", totalLabCount);
					
					return result;
				}

			});

		}catch(Exception e){
			EcwLog.AppendExceptionToLog(e);
		}
		return result;
	}

	public ArrayList<WorkQueue> getMedicationProfileData(int patientId, int pharmacyStatus, int orderType, final int loggedInUserId){

		ArrayList<WorkQueue> result = new ArrayList<WorkQueue>();
		try {

			Map<String,Object> paramMap = new HashMap<String,Object>();
			paramMap.put("patientId", patientId);
			paramMap.put("orderType", orderType);
			paramMap.put("pharmacyStatus", pharmacyStatus);

			result = (ArrayList<WorkQueue>) namedParameterJdbcTemplate.query(PharmacyQueryUtil.getMedicationProfileDataQuery(patientId, pharmacyStatus, orderType).toString(), paramMap, new RowMapper<WorkQueue>(){

				@Override
				public WorkQueue mapRow(ResultSet rs, int arg1) throws SQLException {
					WorkQueue workQueue = new WorkQueue();
					try{

						workQueue.setOrderId(rs.getInt("orderid"));
						workQueue.setMedOrderId(rs.getInt("medorderid"));
						workQueue.setPriorityId(rs.getInt("priorityid"));
						workQueue.setPriorityName(rs.getString("priorityName"));
						workQueue.setOrderType(rs.getInt("orderType"));
						workQueue.setPharmacyStatusId(rs.getInt("pharmacyStatusId"));
						workQueue.setPharmacyStatus(Root.TrimString(rs.getString("pharmacystatus")));
						workQueue.setOrderStatusId(rs.getInt("orderstatus"));
						workQueue.setOrderStatusName(rs.getString("orderstatusname"));
						workQueue.setStartDateTime(PharmacyDateUtil.getValidDate(rs.getString("startdatetime"), loggedInUserId));
						workQueue.setEndDateTime(PharmacyDateUtil.getValidDate(rs.getString("enddatetime"), loggedInUserId));
						workQueue.setOrderDateTime(PharmacyDateUtil.getValidDate(rs.getString("orderdatetime"), loggedInUserId));
						workQueue.setItemName(Root.TrimString(rs.getString("itemname")) );
						workQueue.setEpisodeEncounterId(rs.getInt("episodeencid"));
						workQueue.setPatientId(rs.getInt("patientId"));
						workQueue.setOrderingProviderId(rs.getInt("orderedbyid"));
						workQueue.setOrderingProviderName(Root.TrimString(rs.getString("orderedByFName")) +" "+ Root.TrimString(rs.getString("orderedByLName")));
						
						workQueue.setVerifiedById(rs.getInt("verifiedby"));
						workQueue.setVerifiedByName(Root.TrimString(rs.getString("verifiedByFName")) +" "+ Root.TrimString(rs.getString("verifiedByLName")));
						workQueue.setVerifiedOn(PharmacyDateUtil.getValidDate(rs.getString("verifiedOn"), loggedInUserId));
						
						workQueue.setTag(Root.TrimString(StringEscapeUtils.unescapeHtml(rs.getString("tagname"))));

						String orderFormulation = getOrderFormulationByMedOrderId(rs.getInt("medOrderId"));
						String orderDetails = Root.TrimString(rs.getString("order_dose")) + Root.TrimString(rs.getString("order_dose_UOM"))
						+" "+orderFormulation
						+" "+Root.TrimString(rs.getString("order_route"))
						+" "+Root.TrimString(rs.getString("order_frequency"));

						workQueue.setOrderDetail(orderDetails);

						workQueue.setInterventionStatus(interventionReasonDAO.getInterventionStatusByOrderId(rs.getInt("orderid")));

					}catch(Exception e){
						EcwLog.AppendExceptionToLog(e);
					}
					return workQueue;
				}
			});
		} catch (Exception e) {
			EcwLog.AppendExceptionToLog(e);
		}

		return result;

	}

	public MedOrder getMedicationOrder(int orderId, final int patientId, final int loggedInUserId){

		MedOrder result = new MedOrder();
		try {

			Map<String,Object> paramMap = new HashMap<String,Object>();
			paramMap.put("orderId", orderId);
			result = (MedOrder) namedParameterJdbcTemplate.queryForObject(PharmacyQueryUtil.getMedicationOrderQuery().toString(), paramMap, new RowMapper<MedOrder>(){

				@Override
				public MedOrder mapRow(ResultSet rs, int arg1) throws SQLException {
					MedOrder medOrder = new MedOrder();
					try{
						medOrder.setPharmacyVerificationId(rs.getInt("verificationId"));
						medOrder.setPharmacyStatus(rs.getInt("pharmacyStatus"));
						medOrder.setOrderId(rs.getInt("orderid"));
						medOrder.setStagingOrderId(rs.getInt("stagingorderid"));
						medOrder.setEpisodeEncounterId(rs.getInt("episodeEncId"));
						medOrder.setMedOrderId(rs.getInt("medOrderId"));
						medOrder.setOrderEntryMethod(rs.getInt("orderentrymethodid"));
						medOrder.setRxNumber(rs.getString("medOrderId"));
						medOrder.setItemId(rs.getInt("ipitemid"));

						medOrder.setOrderingProviderId(rs.getInt("orderedbyid"));

						medOrder.setOrderDateTime(rs.getString("orderdatetime"));
						medOrder.setOrderingProvider(Root.TrimString(rs.getString("orderedByFName")) +" "+ Root.TrimString(rs.getString("orderedByLName")));

						medOrder.setDocumentedById(rs.getInt("documentedbyid"));
						medOrder.setDocumentedByName(Root.TrimString(rs.getString("documentedByFName")) +" "+ Root.TrimString(rs.getString("documentedByLName")));
						medOrder.setDocumentedDateTime(PharmacyDateUtil.getValidDate(rs.getString("documenteddatetime"), loggedInUserId));

						medOrder.setPriorityId(rs.getInt("orderpriority"));
						medOrder.setPriorityName(rs.getString("priorityName"));
						medOrder.setAssignedToId(rs.getInt("assignedto"));
						medOrder.setAssignedToName(Root.TrimString(rs.getString("assignedToFName")) +" "+ Root.TrimString(rs.getString("assignedToLName")));
						medOrder.setGenerateBarcode(rs.getInt("generateBarcode") == 1);
						medOrder.setGenerateBarcodeDisp(rs.getInt("generateBarcode") == 1 ? "Yes" : "No");
						medOrder.setOrderEntryInstruction(Root.TrimString(rs.getString("orderinstructions")));
						medOrder.setDrugName(Root.TrimString(rs.getString("drugName")));
						medOrder.setDose(Root.TrimString(rs.getString("order_dose")));
						medOrder.setDoseUnit(Root.TrimString(rs.getString("order_dose_UOM")));
						medOrder.setRoute(Root.TrimString(rs.getString("order_route")));
						medOrder.setFrequencyId(rs.getInt("frequencyId"));
						medOrder.setFrequency(Root.TrimString(rs.getString("order_frequency")));
						medOrder.setOwnMed(rs.getInt("pomflag") == 1 || rs.getInt("ispatientsupplied") == 1);
						medOrder.setSubstitute(rs.getInt("isSubstitute") == MEDICATION_ORDER_IS_SUBSTITUTE);
						medOrder.setRequestToRenew(rs.getInt("isRenewInExpiringTab") == 1);
						medOrder.setPRN(rs.getInt("prn") == 1);
						medOrder.setPrnIndication(Root.TrimString(rs.getString("prnIndication")));
						medOrder.setDuration(Root.TrimString(rs.getString("order_duration")));
						medOrder.setDurationUnit(Root.TrimString(rs.getString("order_duration_type")));
						medOrder.setOrderType(rs.getInt("orderType") == WorkQueue.ORDER_TYPE_MED ? WorkQueue.ORDER_TYPE_MED_STR : (rs.getInt("orderType") == WorkQueue.ORDER_TYPE_IV ? WorkQueue.ORDER_TYPE_IV_STR : (rs.getInt("orderType") == WorkQueue.ORDER_TYPE_COMPLEX_IV ? WorkQueue.ORDER_TYPE_COMPLEX_IV_STR : "")));
						medOrder.setOrderTypeInt(rs.getInt("orderType"));
						medOrder.setTitrationAllowed(rs.getInt("isTitrationAllowed") == 1);

						medOrder.setStartDateTime(PharmacyDateUtil.getValidDate(rs.getString("startdatetime"), loggedInUserId));

						medOrder.setStopDateTime(PharmacyDateUtil.getValidDate(rs.getString("enddatetime"), loggedInUserId));

						medOrder.setDualVerification(rs.getInt("dualVerification") == 1);

						medOrder.setIncludeNow(rs.getInt("includeNow") == 1);
						medOrder.setIvRate(rs.getString("ivrate"));
						medOrder.setIvRateUOM(rs.getString("ivrateuom"));
						medOrder.setTotalVolume(rs.getString("totalVolume"));
						medOrder.setOrderStatus(rs.getInt("orderStatus"));
						medOrder.setOrderStatusName(rs.getString("orderStatusName"));

						medOrder.setRphInstruction(Root.TrimString(rs.getString("rphInstructions")));
						medOrder.setEmarInstruction(Root.TrimString(rs.getString("eMarEntryInstructions")));
						medOrder.setOrderEntryInstruction(Root.TrimString(rs.getString("orderEntryInstructions")));
						medOrder.setInternalNotes(Root.TrimString(rs.getString("internalNotes")));
						medOrder.setISSFlag(rs.getInt("issflag")==1);
						medOrder.setConfiguredISSFlag(rs.getInt("configuredISSFlag") == 1);
						
						medOrder.setVerifiedBy(rs.getInt("verifiedBy"));
						medOrder.setVerifiedByName(Root.TrimString(rs.getString("verifiedByFName")) +" "+ Root.TrimString(rs.getString("verifiedByLName")));
						medOrder.setVerifiedDateTime(PharmacyDateUtil.getValidDate(rs.getString("verifiedon"), loggedInUserId));

						medOrder.setContinuousFrequency(rs.getInt("scheduleType") == FormularySignatureConstants.FREQUENCY_DICTIONARY_SCHEDULE_CONTINUOUS);

					}catch(Exception e){
						EcwLog.AppendExceptionToLog(e);
					}
					return medOrder;
				}
			});
		} catch (Exception e) {
			EcwLog.AppendExceptionToLog(e);
		}

		return result;
	}

	public String getOrderFormulationByMedOrderId(int medOrderId){

		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("medOrderId", medOrderId);

		StringBuffer strSQL = new StringBuffer("select top 1 ").append(MedOrderDtlTblColumn.FORMULATION.getColumnName());
		strSQL.append(" from ").append(ACTIVE_ORDER_TABLE.MEDICATION_DTLS);
		strSQL.append(" where ").append(MedOrderDtlTblColumn.PT_MED_ORDER_ID.getColumnName()).append(" = :medOrderId");
		return  namedParameterJdbcTemplate.queryForObject(strSQL.toString(), paramMap, String.class);
	}

	public ArrayList<MedOrderDetail> getMedOrderDetailsByMedOrderId(int medOrderId, final int facilityId, int orderType){

		ArrayList<MedOrderDetail> result = new ArrayList<MedOrderDetail>();
		try{
			Map<String,Object> paramMap = new HashMap<String,Object>();
			final HashMap<String,Object> paramMapHCPCS = new HashMap<String,Object>();
			paramMap.put("ptMedOrderId", medOrderId);
			paramMap.put("orderType", orderType);
			result = (ArrayList<MedOrderDetail>) namedParameterJdbcTemplate.query(PharmacyQueryUtil.getMedicationOrderDetailQuery().toString(), paramMap, new RowMapper<MedOrderDetail>(){

				public MedOrderDetail mapRow(ResultSet rs, int arg1) throws SQLException {
					MedOrderDetail medOrderDetail = new MedOrderDetail();
					medOrderDetail.setMedOrderDetailId(rs.getInt("id"));
					medOrderDetail.setMedOrderId(rs.getInt(MedOrderDtlTblColumn.PT_MED_ORDER_ID.getColumnName()));
					medOrderDetail.setIpItemId(rs.getInt(MedOrderDtlTblColumn.IP_ITEM_ID.getColumnName()));
					medOrderDetail.setItemName(rs.getString("itemName"));
					medOrderDetail.setGenericName(rs.getString("genericName"));
					medOrderDetail.setBrandName(rxOrderTreeServices.getAssignedBrandName(rs.getInt(MedOrderDtlTblColumn.FORMULARY_ID.getColumnName())) );
					medOrderDetail.setOrderStrength(rs.getString(MedOrderDtlTblColumn.STRENGTH.getColumnName()));
					medOrderDetail.setOrderFormulation(rs.getInt(MedOrderDtlTblColumn.IV_DILUENT_ID.getColumnName()) > 0 ? rs.getString("formulation") : rs.getString(MedOrderDtlTblColumn.FORMULATION.getColumnName()));
					medOrderDetail.setDispenseQty(rs.getString(MedOrderDtlTblColumn.DISPENSE_QTY.getColumnName()));
					medOrderDetail.setOrderNDCCode(rs.getString(MedOrderDtlTblColumn.NDC_CODE.getColumnName()));
					medOrderDetail.setChargeCode(rs.getString("chargeCode"));
					medOrderDetail.setOrderDDID(rs.getInt(MedOrderDtlTblColumn.DDID.getColumnName()));
					medOrderDetail.setDrugFormularyId(rs.getInt(MedOrderDtlTblColumn.FORMULARY_ID.getColumnName()));
					medOrderDetail.setFloorStock(rs.getInt("stockarea") == 1);
					medOrderDetail.setDispense(rs.getString("dispense"));
					medOrderDetail.setIvVolumeUnit(rs.getString(MedOrderDtlTblColumn.IV_VOLUME_UOM.getColumnName()));
					medOrderDetail.setNonBillable(rs.getInt("isnonbillable") == 1);
					medOrderDetail.setIvDiluentId(rs.getInt(MedOrderDtlTblColumn.IV_DILUENT_ID.getColumnName()));

					medOrderDetail.setOrderDose(RxUtil.sanitizeStringAsDouble(rs.getString(MedOrderDtlTblColumn.ORDER_DOSE.getColumnName()), true));
					medOrderDetail.setActualDispense(RxUtil.sanitizeStringAsDouble(rs.getString(MedOrderDtlTblColumn.ACTUAL_DISPENSE.getColumnName()), true));
					medOrderDetail.setDisplayIndex(rs.getInt(MedOrderDtlTblColumn.DISPLAY_INDEX.getColumnName()));

					medOrderDetail.setFormularyStrength(rs.getString("formStrength"));
					medOrderDetail.setFormularyDispenseSize(RxUtil.sanitizeStringAsDouble(rs.getString("formDispenseSize"), true));
					medOrderDetail.setFormularyDispenseUOM(rs.getString("formDispenseUOM"));
					medOrderDetail.setFormularyDoseSize(RxUtil.sanitizeStringAsDouble(rs.getString("formDoseSize"), true));
					medOrderDetail.setFormularyDoseUOM(rs.getString("formDoseUOM"));
					
					medOrderDetail.setFormularyHCPCSCodeRange(rs.getString("HCPCSCodeRange"));
					medOrderDetail.setFormularyHCPCSCodeType(rs.getString("HCPCSCodeType"));
					medOrderDetail.setFormularyHCPCSCodeUnit(rs.getString("HCPCSCodeUnit"));

					medOrderDetail.setFormularyItemId(rs.getInt("formularyItemId"));
					medOrderDetail.setDrugDispenseCode(rs.getString("drugdispenseCode"));

					medOrderDetail.setIvDiluent(rs.getInt(MedOrderDtlTblColumn.IS_DILUENT.getColumnName()) == 1);
					medOrderDetail.setAdditive(rs.getInt(MedOrderDtlTblColumn.IS_ADDITIVE.getColumnName()) == 1);

					medOrderDetail.seteMARInstructions(rs.getString("emar_instr"));

					medOrderDetail.setCalculate(rs.getInt("iscalculate") == 1);
					medOrderDetail.setRxNorm(rs.getString("rxnorm"));
					medOrderDetail.setRoutedGenericItemId(rs.getInt("routedGenericItemId"));

					medOrderDetail.setPCA(rs.getInt("isPCA") == 1);
					medOrderDetail.setSlidingScale(rs.getInt("isSlidingScale") == 1);
					medOrderDetail.setSlidingScaleValue(rs.getString("slidingScale"));
					medOrderDetail.setBolusLoadingDose(rs.getString("bolus_loadingdose"));
					medOrderDetail.setBolusLoadingDoseUOM(rs.getString("bolus_loadingdose_uom"));
					medOrderDetail.setIntermittenDose(rs.getString("intermitten_dose"));
					medOrderDetail.setIntermittenDoseUOM(rs.getString("intermitten_dose_uom"));
					medOrderDetail.setLockoutIntervalDose(rs.getString("lockout_interval_dose"));
					medOrderDetail.setLockoutIntervalDoseUOM(rs.getString("lockout_interval_uom"));
					medOrderDetail.setFourHourLimit(rs.getString("four_hr_limit"));
					medOrderDetail.setFourHourLimitUOM(rs.getString("four_hr_limit_uom"));
					medOrderDetail.setPackSizeUnitcode(rs.getString("packsizeunitcode"));
					medOrderDetail.setPackSize(rs.getString("packsize"));
					medOrderDetail.setDrugTypeBulk(rs.getInt("isdrugtypebulk_formulary") == 1);

					int chargeTypeId = rs.getInt("chargeTypeId");
					double awup = rs.getDouble("awup");					
					medOrderDetail.setChargeTypeId(chargeTypeId);
					medOrderDetail.setAwup(awup);
					
					medOrderDetail.setISSFlag(rs.getInt("issFlag") == 1);
					medOrderDetail.setConfigureISSFlag(rs.getInt("configuredISSFlag") == 1);
					String OrderDose = "";
					if(medOrderDetail.isISSFlag()) {
						OrderDose = Util.ifNullEmpty(rs.getString("maxDose"), "0");
						medOrderDetail.setMaxDose(OrderDose);
						if("0".equals(OrderDose)) {
							//EcwLog.AppendToLog("[WorkQueueDAOimp] - Setting orderDose 0 if maxDose is blank : WorkQueueDAOimpl.java");
						}
					} else {
						OrderDose = rs.getString(MedOrderDtlTblColumn.ORDER_DOSE.getColumnName());
					}					
					paramMapHCPCS.put("orderDose", OrderDose);
					paramMapHCPCS.put("formularyDose", rs.getString("formDoseSize"));
					paramMapHCPCS.put("formularyDoseUOM", rs.getString("formDoseUOM"));
					paramMapHCPCS.put("orderDispense", rs.getString("dispense"));
					paramMapHCPCS.put("packSize", rs.getString("packsize"));				
					paramMapHCPCS.put("isCalcuted", medOrderDetail.isCalculate());
					paramMapHCPCS.put("isBulk", medOrderDetail.isDrugTypeBulk());
					paramMapHCPCS.put("hcpcsCodeRange", rs.getString("HCPCSCodeRange"));
					paramMapHCPCS.put("hcpcsCodeType", rs.getString("HCPCSCodeType"));
					paramMapHCPCS.put("hcpcsCodeUnit", rs.getString("HCPCSCodeUnit"));					
					double totalhcpcsUnit = getTotalHCPCSUnitCalculated(paramMapHCPCS);
					medOrderDetail.setToTalHCPCSUnit(totalhcpcsUnit);
					double costToProc = 0;
					double dispense = 0;
					double orderDispense = 0;
					if(!medOrderDetail.isNonBillable()){
						dispense = Double.parseDouble("".equals(medOrderDetail.getActualDispense()) ? "1" : medOrderDetail.getActualDispense());//As per the Dr. Simon actual dispense should be used not the order dispense
						if(medOrderDetail.isDrugTypeBulk() && convertUOM(rs.getString("formDispenseUOM")).equalsIgnoreCase(convertUOM(rs.getString("packsizeunitcode")))) {
							String strPackSize =  RxUtil.sanitizeStringAsDouble(rs.getString("packsize"), true);
							orderDispense = Double.parseDouble(rs.getString("dispense"));	
							double packSizeUnit = Math.ceil(orderDispense / Double.parseDouble(strPackSize));
							dispense = (Double.parseDouble(strPackSize) * packSizeUnit);
						}						
						costToProc = rs.getDouble("cost_to_proc");						
					} else {
						costToProc = 0;
						dispense = 0;
					}
					double costCalculatedWithChargeType = formularySetupDAO.getCostCalculatedWithChargeType(chargeTypeId, awup, costToProc, dispense, facilityId);
					medOrderDetail.setCostCalculatedWithChargeType(costCalculatedWithChargeType);						
					medOrderDetail.setTotalCostCalculatedWithChargeType(costCalculatedWithChargeType);
					double costCalculatedPerUnitWithChargeType = 0;						
					medOrderDetail.setUnitCostCalculatedWithChargeType(costCalculatedPerUnitWithChargeType);
					return medOrderDetail;
				}

			});
		} catch (DataAccessException e) {
			EcwLog.AppendExceptionToLog(e);
		}
		return result;

	}

	public ArrayList<MedOrder> getActiveOrders(int patientId){

		ArrayList<MedOrder> result = new ArrayList<MedOrder>();
		try {
			PharmacyQueryUtil.ORDERED_DISCONTINUED_STATUS  = statusUtil.getOrderStatusId(ORDER_STATUS.DISCONTINUED.status()); 
			Map<String,Object> paramMap = new HashMap<String,Object>();
			paramMap.put("patientId", patientId);
			result = (ArrayList<MedOrder>) namedParameterJdbcTemplate.query(PharmacyQueryUtil.getActiveOrdersQuery().toString(), paramMap, new RowMapper<MedOrder>(){

				@Override
				public MedOrder mapRow(ResultSet rs, int arg1) throws SQLException {
					MedOrder medOrder = new MedOrder();
					try{
						medOrder.setOrderId(rs.getInt("orderid"));
						medOrder.setMedOrderId(rs.getInt("medOrderId"));
						String strDrugName = Root.TrimString(rs.getString("genericdrugname"));
						if(!"".equals(Root.TrimString(rs.getString("assigned_brand_itemname"))))
						{
							strDrugName += " ("+Root.TrimString(rs.getString("assigned_brand_itemname"))+")";
						}
						if(!"".equals(Root.TrimString(rs.getString("routeName"))))
						{
							strDrugName += " "+Root.TrimString(rs.getString("routeName"))+"";
						}
						medOrder.setDrugName(strDrugName);
					}catch(Exception e){
						EcwLog.AppendExceptionToLog(e);
					}
					return medOrder;
				}
			});
		} catch (Exception e) {
			EcwLog.AppendExceptionToLog(e);
		}

		return result;

	}

	public ArrayList<WorkQueue> getOrdersForLeftPanel(int patientId, int pharmacyStatus, int orderType){

		ArrayList<WorkQueue> result = new ArrayList<WorkQueue>();

		try {
			Map<String,Object> paramMap = new HashMap<String,Object>();
			paramMap.put("orderType", orderType);
			paramMap.put("patientId", patientId);
			paramMap.put("pharmacyStatus", pharmacyStatus);

			result = (ArrayList<WorkQueue>) namedParameterJdbcTemplate.query(PharmacyQueryUtil.getLeftPanelOrdersQuery(patientId, pharmacyStatus, orderType) .toString(), paramMap, new RowMapper<WorkQueue>(){

				@Override
				public WorkQueue mapRow(ResultSet rs, int arg1) throws SQLException {
					WorkQueue workQueue = new WorkQueue();
					try{
						workQueue.setId(rs.getInt("verificationId"));
						workQueue.setOrderId(rs.getInt("orderid"));
						workQueue.setMedOrderId(rs.getInt(MedOrderTblColumn.MED_ORDER_ID.getColumnName()));
						workQueue.setPharmacyStatusId(rs.getInt("pharmacyStatusId"));
						workQueue.setPharmacyStatus(Root.TrimString(rs.getString("pharmacystatus")));
						workQueue.setItemName(Root.TrimString(rs.getString("itemname")) );
						workQueue.setInterventionStatus(interventionReasonDAO.getInterventionStatusByOrderId(rs.getInt("orderid")));
						workQueue.setPOM(rs.getInt(MedOrderTblColumn.POM_FLAG.getColumnName()) == 1 || rs.getInt(MedOrderTblColumn.IS_PATIENT_SUPPLIED.getColumnName()) == 1);

					}catch(Exception e){
						EcwLog.AppendExceptionToLog(e);
					}
					return workQueue;
				}
			});
		} catch (Exception e) {
			EcwLog.AppendExceptionToLog(e);
		}

		return result;
	}

	public Map<String, Object> getLabs(Map<String, Object> oRequestMap){
		Map<String, Object> labDetails = new HashMap<String, Object>();
		try {
			int iLabType = Util.getIntValue(oRequestMap, "labType");
			labDetails.put("labs", namedParameterJdbcTemplate.queryForList(PharmacyQueryUtil.getLabDetailsQuery(oRequestMap, iLabType).toString(), oRequestMap));
		} catch (DataAccessException e) {
			EcwLog.AppendExceptionToLog(e);
		}
		return labDetails;
	}

	public Map<String, Object> getTotalLabCounts(Map<String, Object> oRequestMap){
		Map<String, Object> labDetails = new HashMap<String, Object>();
		try {
			int iLabType = Util.getIntValue(oRequestMap, "labType");
			oRequestMap.put("returnCount", Boolean.TRUE); 
			labDetails.putAll(namedParameterJdbcTemplate.queryForMap(PharmacyQueryUtil.getLabCountQuery(oRequestMap, iLabType).toString(), oRequestMap));
			oRequestMap.remove("returnCount");
		} catch (DataAccessException e) {
			EcwLog.AppendExceptionToLog(e);
		}
		
		return labDetails;
	}

	public boolean markLabAsReviewed(List<Integer> reportIdList, int userId){
		boolean result = false;

		try{
			String currentDateTime = IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT);
			for (Integer reportId : reportIdList) {
				Map<String,Object> paramMap = new HashMap<String,Object>();

				StringBuffer query = new StringBuffer(); 
				query.append("SELECT count(bl.labReportId) as labCount");
				query.append(" FROM ip_pharmacyReviewLabResults bl");
				query.append(" WHERE bl.labReportId =:labReportId ");

				paramMap.put("labReportId", reportId);
				paramMap.put("reviewStatus", 1);
				paramMap.put("reviewedBy", userId);
				paramMap.put("reviewedDate", currentDateTime);
				paramMap.put("createdby", userId);
				paramMap.put("createdon", currentDateTime);

				int count  = 0;
				count = namedParameterJdbcTemplate.queryForObject(query.toString(), paramMap, Integer.class);

				if(count == 0){
					StringBuffer strSQL = new StringBuffer();
					strSQL.append("INSERT INTO ip_pharmacyReviewLabResults (labReportId, reviewStatus, reviewedBy, reviewedDate,createdby,createdon)");
					strSQL.append(" values (:labReportId, :reviewStatus, :reviewedBy, :reviewedDate,:createdby,:createdon)");
					namedParameterJdbcTemplate.update(strSQL.toString(), paramMap);
				}
				result = true;
			}
		} catch ( DataAccessException e ){
			EcwLog.AppendExceptionToLog(e);
		}
		return result;
	}

	public List<Map<String, Object>> getAllReflexAlerts(int stagingOrderId)
	{
		final Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("stagingOrderId", stagingOrderId);

		return namedParameterJdbcTemplate.queryForList(PharmacyQueryUtil.getReflexAlertsQuery().toString(), paramMap);
	}

	public Map<String, Object> getEncounterDetails(int encounterId){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("encounterId", encounterId);

		StringBuilder query = new StringBuilder();

		query.append(" SELECT enc.practiceId, enc.doctorId, info.encType, info.patientType, info.serviceType, info.deptId, enc.facilityId, ").append(SQLUtil.setNullToZero("info.unit")).append(" AS unit ");
		query.append(" FROM enc ");
		query.append(" INNER JOIN ip_encinfo info on info.encounterid = enc.encounterid  ");
		query.append(" WHERE enc.encounterId = :encounterId ");

		return namedParameterJdbcTemplate.queryForMap(query.toString(), paramMap);
	}

	public int getFacilityIdByUserId(int userId){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);

		StringBuilder query = new StringBuilder();
		query.append(" SELECT primaryservicelocation from users where uid = :userId ");

		return namedParameterJdbcTemplate.queryForObject(query.toString(), paramMap, Integer.class);
	}
	
	/**
	 * Return module encounter for episodeEncounterId id 
	 * @param episodeEncounterId
	 * @return int
	 */

	public Map<String,Object> getDeptIdbasedonepisodeEncId (int episodeEncounterId) {		
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("episodeEncounterId", episodeEncounterId);
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("SELECT encmap.assocEncId as subEncounterId, e.deptId as deptId ")
    	  .append("FROM ip_enc_mappings encmap INNER JOIN  ip_encinfo e ")
    	  .append("ON encmap.assocEncId = e.encounterId AND e.delFlag = 0 AND encmap.deleteFlag = 0 ")
    	  .append("WHERE encmap.masterEncId = :episodeEncounterId");
    	
    	return namedParameterJdbcTemplate.queryForMap(sb.toString(), paramSource);
	}	
	
	
	/**
	 * Return Lab Loinc code  form facilityid 
	 * @param facilityid
	 * @return Map
	 */

	public Map<String,Object> getLoincCodeBasedOnFacilityId (int facilityId) {		
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("facilityId", facilityId);
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("SELECT ipgen.scrLoincId as scrLoincId,  ipgen.crclLoincId as crclLoincId ")
    	  .append("FROM ip_pharmacy_gen_order_setting ipgen ")    	 
    	  .append("WHERE ipgen.facilityId = :facilityId");    	
    	return namedParameterJdbcTemplate.queryForMap(sb.toString(), paramSource);
	}
	/**
	 * This API return total calculated unit based on the order dose and hcpcs unit
	 * if hcpcscode, hcpcstype and hcpcsrange not blanck and order dose uom and hcpcstype uom is same then API 
	 * will return number of unit = Math.ceil(orderDose/hcpcs) unit and if hcpcs configured/setup in formulary but orderDose uom and 
	 * hcpcscode type not match the total unit calculated by unit = Math.ceil(orderDose/ formularyDose)
	 * if no hcpcs configured/setup in the formulary then number of unit calculated unit  Math.ceil(orderDose/formularydose)
	 * @param HashMap<String,Object> hcpcsMap	
	 * @return
	 */
	public double getTotalHCPCSUnitCalculated(HashMap<String,Object> hcpcsMap) {
		double totalUnit = 0;
		double order_dose = 0;
		double order_Dispense = 0;
		double pack_Size = 0;
		double unit = 0;
		double hcpcsunit = 0;
		boolean hasHCPCS = false;	
		try 
		{
			String  hcpcsCodeRange = Util.getStrValue(hcpcsMap, "hcpcsCodeRange");
			String  hcpcsCodeType = Util.getStrValue(hcpcsMap, "hcpcsCodeType");
			String  hcpcsCodeUnit = Util.getStrValue(hcpcsMap, "hcpcsCodeUnit");
			String  formularyDoseUOM = Util.getStrValue(hcpcsMap, "formularyDoseUOM");
			String  orderDose = Util.getStrValue(hcpcsMap, "orderDose");
			String  formularyDose = Util.getStrValue(hcpcsMap, "formularyDose");
			String  orderDispense = Util.getStrValue(hcpcsMap, "orderDispense");
			String  packSize = Util.getStrValue(hcpcsMap, "packSize");
			boolean isBulk = Util.getBooleanValue(hcpcsMap, "isBulk");
			//boolean isCalculation = Util.getBooleanValue(hcpcsMap, "isCalcuted");
			if(Util.trimStr(hcpcsCodeRange) !="" && Util.trimStr(hcpcsCodeType) !="" && Util.trimStr(hcpcsCodeUnit) !="" && convertUOM(formularyDoseUOM).equalsIgnoreCase(convertUOM(hcpcsCodeType))) {			
				hasHCPCS = true;
			}		
			if(Util.trimStr(orderDose) !="" && Util.trimStr(formularyDose) !="")
			{
				order_dose = Double.parseDouble(orderDose);
				unit = Double.parseDouble(formularyDose);
				totalUnit = Math.ceil(order_dose / unit);
			}		
			if(isBulk) 
			{
				order_Dispense = Double.parseDouble(orderDispense);
				pack_Size = Double.parseDouble(packSize);
				totalUnit = Math.ceil(order_Dispense / pack_Size);
			}
			else if(hasHCPCS) 
			{			
				if("equalsto".equalsIgnoreCase(hcpcsCodeRange) || ("EQUAL TO".equalsIgnoreCase(hcpcsCodeRange)) || ("EQUALS TO".equalsIgnoreCase(hcpcsCodeRange)) || ("upto".equalsIgnoreCase(hcpcsCodeRange))) 
				{
					hcpcsunit = Double.parseDouble(hcpcsCodeUnit);
					totalUnit = Math.ceil(order_dose / hcpcsunit);
				}								
			} 
		}
		catch(NumberFormatException ex){
			EcwLog.AppendExceptionToLog(ex);			
		}
		
		return totalUnit;
	}
	/**
	 * This API return converted unit of measurement
	 * @param uom
	 * @return
	 */
	public static String convertUOM(String uom) {
		String strUom = "";
		if(uom == null){
			uom = "";
		}
		uom = uom.toLowerCase();
		if("mg".equalsIgnoreCase(uom) || "milligram".equalsIgnoreCase(uom) || "milligrams".equalsIgnoreCase(uom)){
			return "mg";
		} else if("gram".equalsIgnoreCase(uom) || "grams".equalsIgnoreCase(uom) || "g".equalsIgnoreCase(uom) || "gm".equalsIgnoreCase(uom) || "gms".equalsIgnoreCase(uom)){
			return "gm";
		} else if("ml".equalsIgnoreCase(uom) || "cc".equalsIgnoreCase(uom) || "milliliter".equalsIgnoreCase(uom) || "milliliters".equalsIgnoreCase(uom)){
			return "ml";
		}	
		strUom = uom;
		return strUom;
	}

}
