package inpatientWeb.pharmacy.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;
import inpatientWeb.Global.medicationhelper.constant.MedicationConstants.MedOrderDtlTblColumn;
import inpatientWeb.Global.medicationhelper.constant.MedicationConstants.MedOrderTblColumn;
import inpatientWeb.Global.progressnotes.dao.pnorders.ResultQryFactory;
import inpatientWeb.admin.pharmacySettings.pharmacyHelpers.PharmacyConstants;
import inpatientWeb.cpoe.sql.QueryGenerator;
import inpatientWeb.cpoe.util.CPOEEnum.ACTIVE_ORDER_TABLE;
import inpatientWeb.cpoe.util.CPOEEnum.FREQ_DICT_TABLE;
import inpatientWeb.cpoe.util.CPOEEnum.IP_TABLE;
import inpatientWeb.cpoe.util.CPOEEnum.STAGING_ORDER_TABLE;
import inpatientWeb.registration.appointment.dao.AppointmentUtil;
import inpatientWeb.utils.Util;

public class PharmacyQueryUtil {

	public static final int PHARMACY_LAB_REVIEW_STATUS = 2;
	public static int ORDERED_DISCONTINUED_STATUS = 10;

	public static StringBuffer getWorkQueueDataQuery(Map<String, Object> filterMap){

		StringBuffer query = new StringBuffer();

		int recordsPerPage = Util.getIntValue(filterMap, "recordsPerPage");
		int selectedPage = Util.getIntValue(filterMap, "selectedPage");
		int start = -1;
		if(recordsPerPage != -1){
			start = selectedPage * recordsPerPage - recordsPerPage;
		}

		String orderBy = "  order by o.orderpriority, o.orderdatetime desc, pt.ulname,  pt.ufname";
		
		if(Util.getStrValue(filterMap, "selectedSortedBy").length() > 0){
			String strOrder = Util.getStrValue(filterMap, "sortedOrder");
			String strSortedBy = Util.getStrValue(filterMap, "selectedSortedBy");
			if("patientName".equalsIgnoreCase(strSortedBy))
				orderBy = " ORDER BY pt.ufname "+strOrder;
			else if("patientGender".equalsIgnoreCase(strSortedBy))
				orderBy = " ORDER BY pt.sex "+strOrder;
			else if("nurseName".equalsIgnoreCase(strSortedBy))
				orderBy = " ORDER BY an.ufname "+strOrder;
			else if("priorityName".equalsIgnoreCase(strSortedBy))
				orderBy = " ORDER BY ipm.priorityname "+strOrder;
			else if("orderingProviderName".equalsIgnoreCase(strSortedBy))
				orderBy = " ORDER BY pr.ufname "+strOrder;
			else if("orderDateTime".equalsIgnoreCase(strSortedBy))
				orderBy = " ORDER BY o.orderdatetime "+strOrder;			
		}

		query.append("SELECT result.* FROM (");
		query.append(" SELECT pv.id as pvid, pvs.id as pharmacyStatusId, pvs.status as pharmacystatus, pv.assignedTo, enc.encounterid, o.orderedbyid, o.orderdatetime, o.orderstatus,im.statusname as orderstatusname, o.orderpriority,ipm.priorityname, o.orderinstructions, o.episodeencid, ");
		query.append(" ip_items.itemname, ");
		query.append(" ipmo.").append(MedOrderTblColumn.ORDER_DOSE.getColumnName()).append(" as order_dose,");
		query.append(" ipmo.").append(MedOrderTblColumn.ORDER_DOSE_UOM.getColumnName()).append(" as order_dose_UOM,");
		query.append(" ipmo.").append(MedOrderTblColumn.ROUTE_DESC.getColumnName()).append(" as order_route,");
		query.append(" ipmo.").append(MedOrderTblColumn.FREQUECY_DESC.getColumnName()).append(" as order_frequency,");
		query.append(" ipmo.").append(MedOrderTblColumn.MED_ORDER_ID.getColumnName()).append(" as medOrderId,");
		query.append(" ipmo.").append(MedOrderTblColumn.ORDERID.getColumnName()).append(" as ptOrderId,");
		query.append(" ipmo.").append(MedOrderTblColumn.ORDER_TYPE.getColumnName()).append(" as orderType,");
		query.append(" ipmo.").append(MedOrderTblColumn.ISS_FLAG.getColumnName()).append(" as issflag,");
		query.append(" ipmo.").append(MedOrderTblColumn.CONFIGURED_ISS_FLAG.getColumnName()).append(" as configuredISSFlag,");
		query.append(" ipmo.").append(MedOrderTblColumn.POM_FLAG.getColumnName()).append(" as pomFlag,");
		query.append(" ipmo.").append(MedOrderTblColumn.IS_SUBSTITUTE.getColumnName()).append(" as isSubstitute,");	
		query.append(" ipmo.").append(MedOrderTblColumn.IS_PATIENT_SUPPLIED.getColumnName()).append(" as isPatientSupplied,");
		query.append(" ipmo.").append(MedOrderTblColumn.IP_ITEM_ID.getColumnName()).append(" as ipitemid,");
		query.append(" pr.ulname AS providerlname, pr.ufname AS providerfname,ef.id as facilityid, ef.name as facilityname, pt.uid as patientId, ");
		query.append(" pt.ulname AS ptlname, pt.ufname AS ptfname, pt.sex AS ptsex, pt.dob AS ptdob, patients.controlno as ptmrn, a.areaName, a.areaAbbr, b.bedname, b.bedabbr, u.unitname, u.unitabbr, d.deptname, d.deptabbr,");
		query.append(" enc.encType, ere.attn_nurse_id, an.ufname as anfname, an.ulname as anlname, encdt.chiefcomplaint, ");
		query.append(" ios.osname as ordersetname, ios.id as ordersetid, ");
		query.append(" drd.name as pendingReason, assignedPhar.ufname as assignedToFname, assignedPhar.ulname as assignedToLname, ");
		query.append(" e.deptId, e.serviceType, e.workFlowType, ");
		query.append(" ROW_NUMBER() OVER ("+orderBy+") as rowNum");
		query.append(" FROM ip_pharmacy_verification pv ");
		query.append(" LEFT OUTER JOIN ip_pharmacy_verification_status pvs on pvs.id = pv.status AND pv.deleteFlag = 0");
		query.append(" LEFT OUTER JOIN ip_drugFormulary_ReasonDictionaryItems drd ON  drd.id = pv.reasonid");
		query.append(" JOIN ip_ptorders o ON pv.orderid = o.orderid");
		query.append(" LEFT OUTER JOIN ip_stagingorders_source_details issd ON issd.stagingorderid = o.stagingorderid");
		query.append(" LEFT OUTER JOIN ip_stagingorders_source iss ON iss.id = issd.sourceid");
		query.append(" LEFT OUTER JOIN ip_orderset ios ON ios.id = iss.ordersetid");
		query.append(" JOIN ").append(ACTIVE_ORDER_TABLE.MEDICATION).append(" ipmo on o.orderid = ipmo.").append(MedOrderTblColumn.PT_ORDER_ID.getColumnName());
		query.append(" LEFT OUTER JOIN ip_orderstatus_master im on im.id = o.orderstatus");	
		query.append(" LEFT OUTER JOIN ip_order_priority_master ipm on ipm.priorityid = o.orderpriority");
		query.append(" JOIN ip_items on ip_items.itemid = ipmo.ipitemid");
		query.append(" inner join ip_enc_mappings encmap on encmap.masterEncId = o.episodeEncId and encMap.deleteFlag = 0");
		query.append(" inner join ip_encinfo e on e.encounterid = encMap.assocEncid and e.isActiveEncounter = 1 AND e.delFlag = 0");
		query.append(" inner join enc on enc.encounterid = e.encounterid AND enc.deleteFlag = 0");
		query.append(" LEFT OUTER JOIN encounterdata encdt ON encdt.encounterId = e.encounterid ");
		query.append(" LEFT OUTER JOIN ip_erenc_details ere on e.encounterid = ere.encounterid");
		query.append(" LEFT OUTER JOIN ip_loc_bed b ON e.currBedId = b.id");
		query.append(" LEFT OUTER JOIN ip_loc_area a ON a.id = b.areaid");
		query.append(" LEFT OUTER JOIN ip_loc_unit u ON u.id = e.unit");
		query.append(" LEFT OUTER JOIN ip_loc_fac_dept_mapping fdm ON fdm.id = u.facdeptmappid");
		query.append(" LEFT OUTER JOIN edi_facilities ef ON ef.id = enc.facilityId");
		query.append(" LEFT OUTER JOIN ip_loc_department d ON fdm.departmentid = d.id");
		query.append(" JOIN patients ON enc.patientid = patients.pid");
		query.append(" JOIN users pt ON pt.uid = enc.patientid");
		query.append(" JOIN users pr ON pr.uid = o.orderedbyid ");
		query.append(" LEFT OUTER JOIN users assignedPhar ON assignedPhar.uid = pv.assignedTo ");
		query.append(" LEFT OUTER JOIN users an ON an.uid = ere.attn_nurse_id ");

		query.append(getWorkQueueWhereClause(filterMap));

		query.append(") as result ");
		if(recordsPerPage != -1){
			query.append(" WHERE result.rowNum > " + start + " and result.rowNum <= " + ( start + recordsPerPage ) );
		}
		query.append(" UNION ALL ");
		query.append("SELECT result.* FROM (");
		query.append(" SELECT pv.id as pvid, pvs.id as pharmacyStatusId, pvs.status as pharmacystatus, pv.assignedTo, enc.encounterid, o.orderedbyid, o.orderdatetime, o.orderstatus,im.statusname as orderstatusname, o.orderpriority,ipm.priorityname, o.orderinstructions, o.episodeencid, ");
		query.append(" ip_items.itemname, ");
		query.append(" ipmo.").append(MedOrderTblColumn.ORDER_DOSE.getColumnName()).append(" as order_dose,");
		query.append(" ipmo.").append(MedOrderTblColumn.ORDER_DOSE_UOM.getColumnName()).append(" as order_dose_UOM,");
		query.append(" ipmo.").append(MedOrderTblColumn.ROUTE_DESC.getColumnName()).append(" as order_route,");
		query.append(" ipmo.").append(MedOrderTblColumn.FREQUECY_DESC.getColumnName()).append(" as order_frequency,");
		query.append(" ipmo.").append(MedOrderTblColumn.MED_ORDER_ID.getColumnName()).append(" as medOrderId,");
		query.append(" ipmo.").append(MedOrderTblColumn.ORDERID.getColumnName()).append(" as ptOrderId,");
		query.append(" ipmo.").append(MedOrderTblColumn.ORDER_TYPE.getColumnName()).append(" as orderType,");
		query.append(" ipmo.").append(MedOrderTblColumn.ISS_FLAG.getColumnName()).append(" as issflag,");
		query.append(" ipmo.").append(MedOrderTblColumn.CONFIGURED_ISS_FLAG.getColumnName()).append(" as configuredISSFlag,");
		query.append(" ipmo.").append(MedOrderTblColumn.POM_FLAG.getColumnName()).append(" as pomFlag,");
		query.append(" ipmo.").append(MedOrderTblColumn.IS_SUBSTITUTE.getColumnName()).append(" as isSubstitute,");	
		query.append(" ipmo.").append(MedOrderTblColumn.IS_PATIENT_SUPPLIED.getColumnName()).append(" as isPatientSupplied,");
		query.append(" ipmo.").append(MedOrderTblColumn.IP_ITEM_ID.getColumnName()).append(" as ipitemid,");
		query.append(" pr.ulname AS providerlname, pr.ufname AS providerfname,ef.id as facilityid, ef.name as facilityname, pt.uid as patientId, ");
		query.append(" pt.ulname AS ptlname, pt.ufname AS ptfname, pt.sex AS ptsex, pt.dob AS ptdob, patients.controlno as ptmrn, a.areaName, a.areaAbbr, b.bedname, b.bedabbr, u.unitname, u.unitabbr, d.deptname, d.deptabbr,");
		query.append(" enc.encType, ere.attn_nurse_id, an.ufname as anfname, an.ulname as anlname, encdt.chiefcomplaint, ");
		query.append(" ios.osname as ordersetname, ios.id as ordersetid, ");
		query.append(" drd.name as pendingReason, assignedPhar.ufname as assignedToFname, assignedPhar.ulname as assignedToLname, ");
		query.append(" e.deptId, e.serviceType, e.workFlowType, ");
		query.append(" ROW_NUMBER() OVER ("+orderBy+") as rowNum");
		query.append(" FROM ip_pharmacy_verification pv ");
		query.append(" LEFT OUTER JOIN ip_pharmacy_verification_status pvs on pvs.id = pv.status AND pv.deleteFlag = 0");
		query.append(" LEFT OUTER JOIN ip_drugFormulary_ReasonDictionaryItems drd ON  drd.id = pv.reasonid");
		query.append(" JOIN ip_ptorders o ON pv.orderid = o.orderid");
		query.append(" LEFT OUTER JOIN ip_stagingorders_source_details issd ON issd.stagingorderid = o.stagingorderid");
		query.append(" LEFT OUTER JOIN ip_stagingorders_source iss ON iss.id = issd.sourceid");
		query.append(" LEFT OUTER JOIN ip_orderset ios ON ios.id = iss.ordersetid");
		query.append(" JOIN ").append(ACTIVE_ORDER_TABLE.MEDICATION).append(" ipmo on o.orderid = ipmo.").append(MedOrderTblColumn.PT_ORDER_ID.getColumnName());
		query.append(" LEFT OUTER JOIN ip_orderstatus_master im on im.id = o.orderstatus");	
		query.append(" LEFT OUTER JOIN ip_order_priority_master ipm on ipm.priorityid = o.orderpriority");
		query.append(" JOIN ip_items on ip_items.itemid = ipmo.ipitemid");
		query.append(" inner join ip_enc_mappings encmap on encmap.masterEncId = o.episodeEncId and encMap.deleteFlag = 0");
		query.append(" inner join ip_encinfo e on e.encounterid = encmap.masterEncId and e.workFlowType = 2 AND e.delFlag = 0");
		query.append(" inner join enc on enc.encounterid = e.encounterid AND enc.deleteFlag = 0");
		query.append(" LEFT OUTER JOIN encounterdata encdt ON encdt.encounterId = e.encounterid ");
		query.append(" LEFT OUTER JOIN ip_erenc_details ere on e.encounterid = ere.encounterid");
		query.append(" LEFT OUTER JOIN ip_loc_bed b ON e.currBedId = b.id");
		query.append(" LEFT OUTER JOIN ip_loc_area a ON a.id = b.areaid");
		query.append(" LEFT OUTER JOIN ip_loc_unit u ON u.id = e.unit");
		query.append(" LEFT OUTER JOIN ip_loc_fac_dept_mapping fdm ON fdm.id = u.facdeptmappid");
		query.append(" LEFT OUTER JOIN edi_facilities ef ON ef.id = enc.facilityId");
		query.append(" LEFT OUTER JOIN ip_loc_department d ON fdm.departmentid = d.id");
		query.append(" JOIN patients ON enc.patientid = patients.pid");
		query.append(" JOIN users pt ON pt.uid = enc.patientid");
		query.append(" JOIN users pr ON pr.uid = o.orderedbyid ");
		query.append(" LEFT OUTER JOIN users assignedPhar ON assignedPhar.uid = pv.assignedTo ");
		query.append(" LEFT OUTER JOIN users an ON an.uid = ere.attn_nurse_id ");

		query.append(getWorkQueueWhereClause(filterMap));

		query.append(") as result ");
		if(recordsPerPage != -1){
			query.append(" WHERE result.rowNum > " + start + " and result.rowNum <= " + ( start + recordsPerPage ) );
		}

		return query;
	}

	public static StringBuffer getWorkQueueCountQuery(Map<String, Object> filterMap){

		StringBuffer query = new StringBuffer();
		query.append(" SELECT count(pv.orderid) as workqueuecount");
		query.append(" FROM ip_pharmacy_verification pv ");
		query.append(" JOIN ip_ptorders o ON pv.orderid = o.orderid AND pv.deleteFlag = 0");
		query.append(" JOIN ").append(ACTIVE_ORDER_TABLE.MEDICATION).append(" pmo ON pmo.").append(MedOrderTblColumn.PT_ORDER_ID.getColumnName()).append(" = o.orderid");
		query.append(" inner join ip_enc_mappings encmap on encmap.masterEncId = o.episodeEncId and encMap.deleteFlag = 0");
		query.append(" inner join ip_encinfo e on ((e.encounterid = encMap.assocEncid and e.isActiveEncounter = 1) OR (e.encounterid = encmap.masterEncId and e.workFlowType = 2)) AND e.delFlag = 0");
		query.append(" inner join enc on enc.encounterid = e.encounterid AND enc.deleteFlag = 0");
		query.append(" LEFT OUTER JOIN ip_loc_bed b ON e.currBedId = b.id");
		query.append(" LEFT OUTER JOIN ip_loc_area a ON a.id = b.areaid");
		query.append(" LEFT OUTER JOIN ip_loc_unit u ON u.id = e.unit");
		query.append(" LEFT OUTER JOIN ip_loc_fac_dept_mapping fdm ON fdm.id = u.facdeptmappid");
		query.append(" LEFT OUTER JOIN ip_loc_department d ON fdm.departmentid = d.id");
		query.append(" JOIN patients ON enc.patientid = patients.pid");

		query.append(getWorkQueueWhereClause(filterMap));
		return query;
	}

	@SuppressWarnings("unchecked")
	public static StringBuffer getWorkQueueWhereClause(Map<String, Object> filterMap){
		StringBuffer whereClause = new StringBuffer();

		ArrayList<String> pharmacyStatuses =  (ArrayList<String>)filterMap.get("pharmacyStatuses");

		whereClause.append(" WHERE pv.deleteflag = 0");

		//APPLY FILTERS
		if(Util.getIntValue(filterMap, "priority") > 0){
			whereClause.append(" AND o.orderpriority = :priority");
		}

		if(pharmacyStatuses.size() > 0){
			whereClause.append(" AND pv.status in ( :pharmacyStatuses)");
		} else if(Util.getIntValue(filterMap, "pharmacyStatus") > 0){
			whereClause.append(" AND pv.status = :pharmacyStatus");
		}	
		
		if(Util.getIntValue(filterMap, "selectedFacility") > 0){
			whereClause.append(" AND enc.facilityId = :selectedFacility");
		}

		if(Util.getBooleanValue(filterMap, "isUnassigned")){
			whereClause.append(" AND pv.assignedTo = 0");
		}

		if(Util.getIntValue(filterMap, "deptId") > 0){
			whereClause.append(" AND fdm.id = :deptId");
		}

		if(Util.getIntValue(filterMap, "unitId") > 0){
			whereClause.append(" AND u.id = :unitId");
		}

		if(Util.getStrValue(filterMap, "startDate").length() > 0){
			whereClause.append(" AND o.startDateTime >= :startDate");
		}

		if(Util.getStrValue(filterMap, "endDate").length() > 0){
			whereClause.append(" AND o.startDateTime <= :endDate");
		}

		if(Util.getIntValue(filterMap, "assignedToId") > 0){
			whereClause.append(" AND pv.assignedTo = :assignedToId");
		}

		if(Util.getIntValue(filterMap, "patientId") > 0){
			whereClause.append(" AND enc.patientId = :patientId");
		}

		if(Util.getIntValue(filterMap, "medicationItemId") > 0 ){
			whereClause.append(" AND o.orderitemid = :medicationItemId");
		}
		if(Util.getIntValue(filterMap, "workFlowType") > 0 )
		{
			if(Util.getIntValue(filterMap, "workFlowType") == AppointmentUtil.OUTPATIENT_WORKFLOW_TYPE ){
				whereClause.append(" AND e.workFlowType = :workFlowType");
			} else if(Util.getIntValue(filterMap, "workFlowType") == AppointmentUtil.INPATIENT_WORKFLOW_TYPE ){
				whereClause.append(" AND e.workFlowType <> "+AppointmentUtil.OUTPATIENT_WORKFLOW_TYPE);
			}
		}
		return whereClause;
	}

	public static StringBuffer getMedicationProfileDataQuery(int patientId, int pharmacyStatus, int orderType){
		StringBuffer query = new StringBuffer();
		query.append("select o.orderid, o.orderpriority as priorityid, opm.priorityname, o.orderstatus, o.orderdatetime, o.startdatetime, o.enddatetime, o.orderedbyid, o.episodeencid, ");
		query.append(" pv.verifiedby, pv.verifiedon, pv.status as pharmacyStatusId, pvs.status as pharmacystatus, i.itemname,");
		query.append(" ver.ufname as verifiedByFName, ver.ulname as verifiedByLName,");
		query.append(" u.ufname as orderedByFName, u.ulname as orderedByLName,");
		query.append(" e.patientId, e.facilityid, ");
		query.append(" im.statusname as orderstatusname, ");
		query.append(" pmo.").append(MedOrderTblColumn.ORDER_DOSE.getColumnName()).append(" as order_dose,");
		query.append(" pmo.").append(MedOrderTblColumn.ORDER_DOSE_UOM.getColumnName()).append(" as order_dose_UOM,");
		query.append(" pmo.").append(MedOrderTblColumn.ROUTE_DESC.getColumnName()).append(" as order_route,");
		query.append(" pmo.").append(MedOrderTblColumn.FREQUECY_DESC.getColumnName()).append(" as order_frequency,");
		query.append(" pmo.").append(MedOrderTblColumn.MED_ORDER_ID.getColumnName()).append(" as medOrderId,");
		query.append(" pmo.").append(MedOrderTblColumn.ORDER_TYPE.getColumnName()).append(" as orderType,");
		query.append(" ptp.name as tagname");
		query.append(" from ip_ptorders o ");
		query.append(" LEFT OUTER JOIN ip_orderstatus_master im on im.id = o.orderstatus");	
		query.append(" LEFT OUTER JOIN ip_ptStagingOrders_treatmentplan ptp on ptp.treatmentplanid = o.treatmentplanid");
		query.append(" join ip_order_priority_master opm on opm.priorityid = o.orderpriority"); 
		query.append(" join ip_items i on i.itemid = o.orderitemid"); 
		query.append(" join ip_pharmacy_verification pv on pv.orderid = o.orderid AND pv.deleteFlag = 0"); 
		query.append(" join ip_pharmacy_verification_status pvs on pvs.id = pv.status");
		query.append(" JOIN ").append(ACTIVE_ORDER_TABLE.MEDICATION).append(" pmo ON pmo.").append(MedOrderTblColumn.PT_ORDER_ID.getColumnName()).append(" = o.orderid");
		query.append(" inner join enc e on e.encounterid = o.episodeEncId ");
		query.append(" join users u on o.orderedbyid = u.uid");
		query.append(" left outer join users ver on pv.verifiedby = ver.uid");

		query.append(PharmacyQueryUtil.getMedicationProfileWhereClause(patientId, pharmacyStatus, orderType));

		return query;
	}

	public static StringBuffer getLeftPanelOrdersQuery(int patientId, int pharmacyStatus, int orderType){
		StringBuffer query = new StringBuffer();
		query.append("select o.orderid, pv.id as verificationId, pv.status as pharmacyStatusId, pvs.status as pharmacystatus, i.itemname, opm.priorityid as priorityid, "); 
		query.append(" pmo.").append(MedOrderTblColumn.MED_ORDER_ID.getColumnName()).append(",");
		query.append(" pmo.").append(MedOrderTblColumn.POM_FLAG.getColumnName()).append(",");
		query.append(" pmo.").append(MedOrderTblColumn.IS_PATIENT_SUPPLIED.getColumnName());
		query.append(" from ip_ptorders o ");
		query.append(" join ip_items i on i.itemid = o.orderitemid"); 
		query.append(" join ip_pharmacy_verification pv on pv.orderid = o.orderid AND pv.deleteFlag = 0"); 
		query.append(" join ip_pharmacy_verification_status pvs on pvs.id = pv.status");
		query.append(" join ip_order_priority_master opm on opm.priorityid = o.orderpriority");
		query.append(" JOIN ").append(ACTIVE_ORDER_TABLE.MEDICATION).append(" pmo ON pmo.").append(MedOrderTblColumn.PT_ORDER_ID.getColumnName()).append(" = o.orderid");
		query.append(" join enc e on e.encounterid = o.episodeEncId ");

		query.append(PharmacyQueryUtil.getMedicationProfileWhereClause(patientId, pharmacyStatus, orderType));

		return query;
	}

	public static StringBuffer getMedicationProfileWhereClause(int patientId, int pharmacyStatus, int orderType){

		StringBuffer whereClause = new StringBuffer();
		whereClause.append(" where e.patientid = :patientId");

		if(orderType > 0 ){
			whereClause.append(" and pmo.ordertype = :orderType");
		}

		if(pharmacyStatus > 0){
			whereClause.append(" and pv.status = :pharmacyStatus");
		} else if(pharmacyStatus == -1){
			whereClause.append(" and pv.status in ("+PharmacyConstants.PharmacyVerificationStatus.UNVERIFIED.getId()+","+PharmacyConstants.PharmacyVerificationStatus.PENDING.getId()+")");
		}

		whereClause.append(" order by priorityid, o.startdatetime desc");

		return whereClause;
	}

	public static StringBuffer getWorkQueueRecordCountQuery(){
		StringBuffer query = new StringBuffer();
		query.append(" SELECT count(pv.orderid) as reccount, pvs.status");
		query.append(" FROM ip_pharmacy_verification pv ");
		query.append(" JOIN ip_pharmacy_verification_status pvs ON pv.status = pvs.id AND pv.deleteFlag = 0");
		query.append(" JOIN ip_ptorders o ON pv.orderid = o.orderid");
		query.append(" JOIN ").append(ACTIVE_ORDER_TABLE.MEDICATION).append(" pmo ON pmo.").append(MedOrderTblColumn.PT_ORDER_ID.getColumnName()).append(" = o.orderid");
		query.append(" inner join ip_enc_mappings encmap on encmap.masterEncId = o.episodeEncId and encMap.deleteFlag = 0");
		query.append(" inner join ip_encinfo e on ((e.encounterid = encMap.assocEncid and e.isActiveEncounter = 1) OR (e.encounterid = encmap.masterEncId and e.workFlowType = 2)) AND e.delFlag = 0");
		query.append(" inner join enc on enc.encounterid = e.encounterid AND enc.deleteFlag = 0");
		query.append(" WHERE pv.deleteflag = 0");
		query.append(" GROUP BY pvs.id, pvs.status");
		return query;
	}

	public static StringBuffer getMedicationOrderQuery(){

		StringBuffer query = new StringBuffer();
		query.append(" SELECT o.orderid, o.stagingorderid, ipv.id as verificationId, ipv.assignedto, ipv.status as pharmacyStatus, ipv.generateBarcode, ipv.dualVerification, ipv.verifiedby, ipv.verifiedon, o.orderentrymethodid, o.orderedbyid, o.orderdatetime, o.documentedbyid, o.documenteddatetime, o.episodeencid,");
		query.append(" o.orderpriority, opm.priorityname, o.orderinstructions, o.startdatetime, o.enddatetime, o.orderstatus, ");
		query.append(" ip_items.itemname as drugName,");
		query.append(" ipmo.").append(MedOrderTblColumn.IP_ITEM_ID.getColumnName()).append(" as ipitemid,");
		query.append(" ipmo.").append(MedOrderTblColumn.MED_ORDER_ID.getColumnName()).append(" as medOrderId,");
		query.append(" ipmo.").append(MedOrderTblColumn.ORDER_TYPE.getColumnName()).append(" as orderType,");
		query.append(" ipmo.").append(MedOrderTblColumn.ORDER_DOSE.getColumnName()).append(" as order_dose,");
		query.append(" ipmo.").append(MedOrderTblColumn.ORDER_DOSE_UOM.getColumnName()).append(" as order_dose_UOM,");
		query.append(" ipmo.").append(MedOrderTblColumn.DURATION.getColumnName()).append(" as order_duration,");
		query.append(" ipmo.").append(MedOrderTblColumn.DURATION_TYPE.getColumnName()).append(" as order_duration_type,");
		query.append(" ipmo.").append(MedOrderTblColumn.RX_ORDER_NO.getColumnName()).append(" as rxOrderNo,");
		query.append(" ipmo.").append(MedOrderTblColumn.ROUTE_DESC.getColumnName()).append(" as order_route,");
		query.append(" ipmo.").append(MedOrderTblColumn.FREQUECY_DESC.getColumnName()).append(" as order_frequency,");
		query.append(" ipmo.").append(MedOrderTblColumn.FREQUECY_ID.getColumnName()).append(" as FrequencyId,");
		query.append(" ipmo.").append(MedOrderTblColumn.PRN.getColumnName()).append(" as PRN,");
		query.append(" ipmo.").append(MedOrderTblColumn.PRN_INDICATION.getColumnName()).append(" as PRNIndication,");
		query.append(" ipmo.").append(MedOrderTblColumn.POM_FLAG.getColumnName()).append(" as pomFlag,");
		query.append(" ipmo.").append(MedOrderTblColumn.IS_PATIENT_SUPPLIED.getColumnName()).append(" as isPatientSupplied,");
		query.append(" ipmo.").append(MedOrderTblColumn.IS_SUBSTITUTE.getColumnName()).append(" as isSubstitute,");
		query.append(" ipmo.").append(MedOrderTblColumn.RPH_INSTRUCTIONS.getColumnName()).append(" as rphInstructions,");
		query.append(" ipmo.").append(MedOrderTblColumn.ORDER_ENTRY_INSTRUCTION.getColumnName()).append(" as orderEntryInstructions,");
		query.append(" ipmo.").append(MedOrderTblColumn.EMAR_ENTRY_INSTRUCTIONS.getColumnName()).append(" as eMarEntryInstructions,");
		query.append(" ipmo.").append(MedOrderTblColumn.INTERNAL_NOTES.getColumnName()).append(" as internalNotes,");
		query.append(" ipmo.").append(MedOrderTblColumn.IV_RATE.getColumnName()).append(" as ivRate,");
		query.append(" ipmo.").append(MedOrderTblColumn.IV_RATE_UOM.getColumnName()).append(" as ivRateUom,");
		query.append(" ipmo.").append(MedOrderTblColumn.TOTALVOLUME.getColumnName()).append(" as totalVolume,");
		query.append(" ipmo.").append(MedOrderTblColumn.IS_RENEW_IN_EXPRIRING_TAB.getColumnName()).append(" as isRenewInExpiringTab,");
		query.append(" ipmo.").append(MedOrderTblColumn.IS_TITRATION_ALLOWED.getColumnName()).append(" as isTitrationAllowed,");
		query.append(" ipmo.").append(MedOrderTblColumn.ISS_FLAG.getColumnName()).append(" as issflag,");
		query.append(" ipmo.").append(MedOrderTblColumn.CONFIGURED_ISS_FLAG.getColumnName()).append(" as configuredISSFlag,");
		query.append(" u.ufname as orderedByFName, u.ulname as orderedByLName,");
		query.append(" d.ufname as documentedByFName, d.ulname as documentedByLName,");
		query.append(" a.ufname as assignedToFName, a.ulname as assignedToLName,");
		query.append(" ver.ufname as verifiedByFName, ver.ulname as verifiedByLName,");
		query.append(" pso.includeNow, ");
		query.append(" im.statusname as orderStatusName, ");
		query.append(" freq.scheduleType ");
		query.append(" from ip_ptorders o");
		query.append(" join ip_ptstagingorders pso on pso.stagingorderid = o.stagingorderid");
		query.append(" join ip_order_priority_master opm on opm.priorityid = o.orderpriority");
		query.append(" JOIN ").append(ACTIVE_ORDER_TABLE.MEDICATION).append(" ipmo on o.orderid = ipmo.").append(MedOrderTblColumn.PT_ORDER_ID.getColumnName());
		query.append(" left outer join ip_drugformulary_frequency freq on freq.id = ipmo.frequencyId");
		query.append(" LEFT OUTER JOIN ip_orderstatus_master im on im.id = o.orderstatus");	
		query.append(" join ip_items on ip_items.itemid = ipmo.ipitemid");
		query.append(" join ip_pharmacy_verification ipv on ipv.orderId = o.orderid AND ipv.deleteFlag = 0");
		query.append(" join users u on o.orderedbyid = u.uid");
		query.append(" join users d on o.documentedbyid = d.uid");
		query.append(" left outer join users a on ipv.assignedto = a.uid");
		query.append(" left outer join users ver on ipv.verifiedby = ver.uid");
		query.append(" where o.orderid = :orderId");

		return query;
	}

	public static StringBuffer getMedicationOrderDetailQuery(){
		StringBuffer query = new StringBuffer();
		query.append("SELECT ");
		query.append(" ipmd.id,");
		query.append(" ipmd.").append(MedOrderDtlTblColumn.PT_MED_ORDER_ID.getColumnName()).append(",");
		query.append(" ipmd.").append(MedOrderDtlTblColumn.IP_ITEM_ID.getColumnName()).append(",");
		query.append(" ipmd.").append(MedOrderDtlTblColumn.STRENGTH.getColumnName()).append(",");
		query.append(" ipmd.").append(MedOrderDtlTblColumn.FORMULATION.getColumnName()).append(",");
		query.append(" ipmd.").append(MedOrderDtlTblColumn.DISPENSE_QTY.getColumnName()).append(",");
		query.append(" ipmd.").append(MedOrderDtlTblColumn.FORMULARY_DISPENSE_UOM.getColumnName()).append(" as formDispenseUOM,");
		query.append(" ipmd.").append(MedOrderDtlTblColumn.NDC_CODE.getColumnName()).append(",");
		query.append(" ipmd.").append(MedOrderDtlTblColumn.DDID.getColumnName()).append(",");
		query.append(" ipmd.").append(MedOrderDtlTblColumn.DELETEFLAG.getColumnName()).append(",");
		query.append(" ipmd.").append(MedOrderDtlTblColumn.FORMULARY_ID.getColumnName()).append(",");
		query.append(" ipmd.").append(MedOrderDtlTblColumn.IV_DILUENT_ID.getColumnName()).append(",");
		query.append(" ipmd.").append(MedOrderDtlTblColumn.IV_VOLUME_UOM.getColumnName()).append(",");
		query.append(" ipmd.").append(MedOrderDtlTblColumn.IS_ADDITIVE.getColumnName()).append(",");
		query.append(" ipmd.").append(MedOrderDtlTblColumn.IS_DILUENT.getColumnName()).append(",");
		query.append(" ipmd.").append(MedOrderDtlTblColumn.ORDER_DISPENSE.getColumnName()).append(" as dispense,");
		query.append(" ipmd.").append(MedOrderDtlTblColumn.FORMULARY_DOSE.getColumnName()).append(" as formDoseSize,");
		query.append(" ipmd.").append(MedOrderDtlTblColumn.FORMULARY_DOSE_UOM.getColumnName()).append(" as formDoseUOM,");
		query.append(" ipmd.").append(MedOrderDtlTblColumn.FORMULARY_DISPENSE_SIZE.getColumnName()).append(" as formDispenseSize,");
		query.append(" ipmd.").append(MedOrderDtlTblColumn.ORDER_DOSE.getColumnName()).append(",");
		query.append(" ipmd.").append(MedOrderDtlTblColumn.ACTUAL_DISPENSE.getColumnName()).append(",");
		query.append(" ipmd.").append(MedOrderDtlTblColumn.DISPLAY_INDEX.getColumnName()).append(",");
		query.append(" i.itemname, i.itemid as formularyItemId,");
		query.append(" idf.stockarea, idf.formulation, idf.strength as formStrength,idf.drugdispenseCode,idf.isCalculate, idf.rxnorm, idf.routedGenericItemId, idf.genericDrugName as genericName,");
		query.append(" dfap.awup, dfap.cost_to_proc,");
		query.append(" idfo.isnonbillable, idfo.chargeTypeId, idfo.isPCA, idfo.isSlidingScale, idfo.slidingScale, idfo. bolus_loadingdose, idfo.bolus_loadingdose_uom,");
		query.append(" idfo.intermitten_dose, idfo.intermitten_dose_uom, idfo.lockout_interval_dose, idfo.lockout_interval_uom, idfo.four_hr_limit, idfo.four_hr_limit_uom, ");
		query.append(" dfn.orderentry_instr, dfn.emar_instr,");
		query.append(" cpt.chargeCode, ");
		query.append(" cpt.HCPCSCodeRange as HCPCSCodeRange, ");
		query.append(" cpt.HCPCSCodeType as HCPCSCodeType, ");
		query.append(" cpt.HCPCSCodeUnit as  HCPCSCodeUnit, ");
		query.append(" ipptmed.issFlag as issFlag, ");
		query.append(" ipptmed.configuredISSFlag as configuredISSFlag, ");
		query.append(" ipptmed.minDose as minDose, ");
		query.append(" ipptmed.maxDose as maxDose, ");
		query.append(" dfap.packsizeunitcode as  packsizeunitcode, ");
		query.append(" dfap.packsize as packsize, ");
		query.append(" dfap.packType as packType, ");
		query.append(" idf.isdrugtypebulk_formulary as  isdrugtypebulk_formulary ");
		query.append(" FROM ");
		query.append(ACTIVE_ORDER_TABLE.MEDICATION).append( " ipptmed");
		query.append(" INNER JOIN ");
		query.append(ACTIVE_ORDER_TABLE.MEDICATION_DTLS).append( " ipmd");
		query.append(" ON ipptmed.medorderid = ipmd.ptMedOrderId ");		
		query.append(" LEFT OUTER JOIN ip_drugformulary idf on ipmd.drugformularyid = idf.id");
		query.append(" LEFT OUTER JOIN ip_items i on i.itemid = idf.itemid");
		query.append(" LEFT OUTER JOIN ip_drugformulary_OrderTypeSetup idfo on idfo.formularyid = idf.id and idfo.ordertype = :orderType AND idfo.delflag = 0");
		query.append(" LEFT OUTER JOIN ip_drugformulary_associatedProducts dfap on idf.id = dfap.formularyid and idf.ndc = dfap.ndc");
		query.append(" LEFT OUTER JOIN ip_drugformulary_notes dfn on idf.id = dfn.formularyid");
		query.append(" LEFT OUTER JOIN edi_cptcodes cpt on cpt.itemid = idf.cptcodeitemid");
		query.append(" WHERE ipmd.").append(MedOrderDtlTblColumn.PT_MED_ORDER_ID.getColumnName()).append(" = :ptMedOrderId");

		return query;
	}

	public static StringBuffer getActiveOrdersQuery(){
		StringBuffer query = new StringBuffer();
		query.append(" select o.orderid,pmo.medOrderId, i.itemname,o.orderItemId,drug.genericdrugname,drug.assigned_brand_itemname,drug.routeName ");
		query.append(" from ip_ptorders o  join ip_items i on i.itemid = o.orderitemid JOIN ip_ptMedicationOrders pmo ");
		query.append(" ON pmo.ptOrderId = o.orderid join enc on enc.encounterid = o.episodeEncId AND enc.deleteFlag = 0 ");
		query.append(" inner join ip_ptmedicationorders_detail pmod on pmo.medOrderId = pmod.ptmedorderId and pmod.deleteflag = 0 ");
		query.append(" inner join ip_drugformulary drug on pmod.DrugFormularyId = drug.id and drug.delflag = 0 ");
		query.append(" where o.orderstatus <> ").append(ORDERED_DISCONTINUED_STATUS).append(" and enc.patientid = :patientId ");
		return query;
	}

	public static StringBuffer getReflexAlertsQuery(){
		StringBuffer query = new StringBuffer();
		query.append("SELECT reflexLog.triggerId AS reflexLogId, reflexLog.triggeredItemId AS itemId, I.itemName, reflexLog.actionStatus, reflexLog.isAutoOrder, reflexLog.reflexItemId,");
		query.append(" reflexLog.stagingOrderId, reflexLog.discontinueOrder, I.parentId, I.keyName, PO.episodeEncId, PO.orderItemId, reflexLog.reflexType, item.itemCode, freq.freqCode, ");
		query.append(" \"action\" = CASE WHEN reflexLog.actionStatus = 0 THEN  'Do Not Order'");
		query.append(" WHEN reflexLog.actionStatus = 1 THEN  'Order'");
		query.append(" WHEN reflexLog.actionStatus = 2 THEN  'Do Not Discontinue'");
		query.append(" WHEN reflexLog.actionStatus = 3 THEN  'Discontinue'");
		query.append(" WHEN reflexLog.actionStatus = 4 THEN  'Modify'");
		query.append(" WHEN reflexLog.actionStatus = 5 THEN  'Keep'");
		query.append(" WHEN reflexLog.actionStatus = 6 THEN  'Auto Discarded'");
		query.append(" ELSE  ''");
		query.append(" END ");
		query.append(String.format(" FROM %s reflexLog ", STAGING_ORDER_TABLE.REFLEX_LOG));
		query.append(String.format(" INNER JOIN %s PO ON PO.stagingOrderId = reflexLog.stagingOrderId AND PO.deleteFlag = 0 ", STAGING_ORDER_TABLE.ORDER));
		query.append(String.format(" INNER JOIN %s freq ON freq.frequencyId  = PO.orderFrequencyId AND freq.deleteFlag = 0 ", FREQ_DICT_TABLE.FREQ_LIST.table()));		
		query.append(String.format(" INNER JOIN %s I  ON I.itemId = reflexLog.triggeredItemId ", IP_TABLE.ITEMS.table()));
		query.append(String.format(" INNER JOIN %s item ON item.itemId = I.parentId ", IP_TABLE.ITEMS.table()));
		query.append("WHERE reflexLog.stagingOrderId = (:stagingOrderId) ");

		return query;
	}

	public static StringBuilder getLabCountQuery(Map<String, Object> oRequestMap, int iLabType){
		StringBuilder countQuery       = new StringBuilder();
		try {
			QueryGenerator qryGeneratorCount = ResultQryFactory.getInstanceForQuery(iLabType, oRequestMap);
			countQuery.append(" SELECT ")
			.append(qryGeneratorCount.getSelectClause())
			.append(" FROM ").append(qryGeneratorCount.getFromClause())
			.append(qryGeneratorCount.getJoinClause()).append(" LEFT JOIN users pt ON pt.uid = enc.patientId ")
			.append(" LEFT JOIN ip_pharmacyReviewLabResults prl ON prl.labReportId = labdata.labReportId  ");
	
			StringBuilder sbWhereCls	= qryGeneratorCount.getWhereClause();
	
			if(Util.getIntValue(oRequestMap, "facilityId") > 0)
				sbWhereCls.append(" AND enc.facilityId=:facilityId ");
	
			if(Util.getIntValue(oRequestMap, "pharmacyReviewStatus") == PHARMACY_LAB_REVIEW_STATUS){
				sbWhereCls.append(" AND prl.reviewStatus IS NULL ");
			}else if(Util.getIntValue(oRequestMap, "pharmacyReviewStatus") > 0)
				sbWhereCls.append(" AND prl.reviewStatus=:pharmacyReviewStatus ");
	
			if (Util.getSize(sbWhereCls) > 0)
				countQuery.append(" WHERE ").append(sbWhereCls);
		} catch (Exception e) {
			EcwLog.AppendExceptionToLog(e);
		}
		return countQuery;
	}

	public static StringBuilder getLabDetailsQuery(Map<String, Object> oRequestMap, int iLabType){

		StringBuilder sbQuery = new StringBuilder();
		try {
			int iPageSize = Util.getIntValue(oRequestMap, "recordsPerPage"); 
			int iPageNo = Util.getIntValue(oRequestMap, "selectedPage"); 

			int start = iPageNo * iPageSize - iPageSize;

			QueryGenerator qryGenerator = ResultQryFactory.getInstanceForQuery(iLabType, oRequestMap);

		StringBuilder orderBy       = new StringBuilder();
		StringBuilder sbOrderBy  = qryGenerator.getOrderByClause();
		if (Util.getSize(sbOrderBy) > 0){
			orderBy.append(" ORDER BY ").append(sbOrderBy);
		}	

		sbQuery.append("SELECT result.* FROM (");
		sbQuery.append(" SELECT labdata.processedxml,pt.ufname as ptfname,pt.ulname as ptlname,pt.sex AS gender,pt.dob AS age, ")
		.append(" prl.reviewStatus,enc.patientId,ROW_NUMBER() OVER ("+orderBy+") as rowNum, ")
		.append(qryGenerator.getSelectClause())
		.append(" ,enc.encounterId as episodeEncID ")
		.append(" FROM ").append(qryGenerator.getFromClause())
		.append(qryGenerator.getJoinClause()).append(" LEFT JOIN users pt ON pt.uid = enc.patientId ")
		.append(" LEFT JOIN ip_pharmacyReviewLabResults prl ON prl.labReportId = labdata.labReportId  ");

		StringBuilder sbWhereCls	= qryGenerator.getWhereClause();

		if(Util.getIntValue(oRequestMap, "facilityId") > 0)
			sbWhereCls.append(" AND enc.facilityId=:facilityId ");

		if(Util.getIntValue(oRequestMap, "pharmacyReviewStatus") == PHARMACY_LAB_REVIEW_STATUS){
			sbWhereCls.append(" AND prl.reviewStatus IS NULL ");
		}else if(Util.getIntValue(oRequestMap, "pharmacyReviewStatus") > 0)
			sbWhereCls.append(" AND prl.reviewStatus=:pharmacyReviewStatus ");

		if (Util.getSize(sbWhereCls) > 0)
			sbQuery.append(" WHERE ").append(sbWhereCls);

		StringBuilder sbGrpByCls    = qryGenerator.getGroupByClause();
		if (Util.getSize(sbGrpByCls) > 0)
			sbQuery.append(" GROUP BY ").append(sbGrpByCls);

		sbQuery.append(" ) as result");
		sbQuery.append(" WHERE result.rowNum > " + start + " AND result.rowNum <= " + ( start + iPageSize ) + " ");
		} catch (Exception e) {
			EcwLog.AppendExceptionToLog(e);
		}

		return sbQuery;
	}

	@SuppressWarnings("unchecked")
	public static String getComaSepratedStringFromList (@SuppressWarnings("rawtypes") List<Map> list,String key) {
		StringBuilder comaSepratedSb= new StringBuilder();
		int size = list.size();
		Map<String, Object> param;
		for(int i=0;i<size;i++){
			param = (Map<String, Object>)list.get(i);
			if( i == size-1 ){
				comaSepratedSb.append(Util.getStrValue(param, key));
			} else {
				comaSepratedSb.append(Util.getStrValue(param, key)).append(",");
			}
		}
		return comaSepratedSb.toString();
	}

}
