package inpatientWeb.pharmacy.daoImpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;
import inpatientWeb.Global.medicationhelper.constant.MedicationConstants.MedOrderTblColumn;
import inpatientWeb.cpoe.util.CPOEEnum.ACTIVE_ORDER_TABLE;
import inpatientWeb.pharmacy.beans.FlowsheetTemplate;
import inpatientWeb.pharmacy.beans.FlowsheetTemplateItem;
import inpatientWeb.pharmacy.dao.MyPatientsDAO;
import inpatientWeb.utils.IPTzUtils;
import inpatientWeb.utils.Util;

@Repository
public class MyPatientsDAOImpl implements MyPatientsDAO {

	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public ArrayList<FlowsheetTemplate> getFlowsheetTemplates(){

		Map<String,Object> paramMap = new HashMap<String,Object>();
		StringBuffer strSQL = new StringBuffer("SELECT id, name, deleteflag, episodeencid from ip_flowsheets where deleteflag = 0 and displayInPharmacy = 1");

		return (ArrayList<FlowsheetTemplate>) namedParameterJdbcTemplate.query(strSQL.toString(), paramMap, new RowMapper<FlowsheetTemplate>(){

			@Override
			public FlowsheetTemplate mapRow(ResultSet rs, int arg1) throws SQLException {
				FlowsheetTemplate template = new FlowsheetTemplate();
				template.setId(rs.getInt("id"));
				template.setName(rs.getString("name"));
				template.setDeleteFlag(rs.getInt("deleteFlag"));
				template.setEpisodeEncId(rs.getInt("episodeencid"));

				return template;
			}
		});

	}

	public FlowsheetTemplate getFlowsheetTemplateById(int id){

		Map<String,Object> paramMap = new HashMap<String,Object>();
		StringBuffer strSQL = new StringBuffer("SELECT id, name, deleteflag, episodeencid from ip_flowsheets where deleteflag = 0 and id = :id");
		paramMap.put("id", id);
		return (FlowsheetTemplate) namedParameterJdbcTemplate.queryForObject(strSQL.toString(), paramMap, new RowMapper<FlowsheetTemplate>(){

			@Override
			public FlowsheetTemplate mapRow(ResultSet rs, int arg1) throws SQLException {
				FlowsheetTemplate template = new FlowsheetTemplate();
				template.setId(rs.getInt("id"));
				template.setName(rs.getString("name"));
				template.setDeleteFlag(rs.getInt("deleteFlag"));
				template.setEpisodeEncId(rs.getInt("episodeencid"));

				return template;
			}
		});

	}

	public ArrayList<FlowsheetTemplateItem> getFlowsheetTemplateItemsByFlowsheetId(int flowsheetId){

		Map<String,Object> paramMap = new HashMap<String,Object>();
		StringBuffer strSQL = new StringBuffer("SELECT id, flowsheetid, type, itemid, itemName from ip_flowsheetitems where flowsheetid = :flowsheetId");
		paramMap.put("flowsheetId", flowsheetId);
		return (ArrayList<FlowsheetTemplateItem>) namedParameterJdbcTemplate.query(strSQL.toString(), paramMap, new RowMapper<FlowsheetTemplateItem>(){

			@Override
			public FlowsheetTemplateItem mapRow(ResultSet rs, int arg1) throws SQLException {
				FlowsheetTemplateItem templateItem = new FlowsheetTemplateItem();
				templateItem.setId(rs.getInt("id"));
				templateItem.setItemName(rs.getString("itemname"));
				templateItem.setItemId(rs.getInt("itemid"));
				templateItem.setType(rs.getInt("type"));
				return templateItem;
			}
		});
	}

	public List<Map<String, Object>> getPatientsByFilter(Map<String, Object> filterMap){

		int assignedToId = Util.getIntValue(filterMap, "loggedInUserId");
		String userTimeZone = IPTzUtils.getTimeZoneForResource(assignedToId);

		int flowsheetId = Util.getIntValue(filterMap,"flowsheetId");
		int facilityId = Util.getIntValue(filterMap,"selectedFacility");
		int deptId = Util.getIntValue(filterMap,"deptId");
		String startDate = "";
		String endDate = "";
		try{
			startDate = Util.getStrValue(filterMap, "startDate").length() > 0 ? IPTzUtils.convertDateStrInTz(Util.getStrValue(filterMap, "startDate"), "yyyy-MM-dd", userTimeZone, IPTzUtils.DEFAULT_DB_DT_FMT, IPTzUtils.DEFAULT_DB_TIME_ZONE  ) : "";
			endDate = Util.getStrValue(filterMap, "endDate").length() > 0 ? IPTzUtils.convertDateStrInTz(Util.getStrValue(filterMap, "endDate"), "yyyy-MM-dd", userTimeZone, IPTzUtils.DEFAULT_DB_DT_FMT, IPTzUtils.DEFAULT_DB_TIME_ZONE  ) : "";
		}catch(ParseException e){
			EcwLog.AppendExceptionToLog(e);
		}
		
		StringBuffer strSQL = new StringBuffer("");

		int recordsPerPage = Util.getIntValue(filterMap, "recordsPerPage");
		int selectedPage = Util.getIntValue(filterMap, "selectedPage");
		int start = -1;
		if(recordsPerPage != -1){
			start = selectedPage * recordsPerPage - recordsPerPage;
		}
		Map<String,Object> paramMap = new HashMap<String,Object>();
		String orderBy = "  ORDER BY enc.patientId desc";
		strSQL.append("SELECT result.* FROM (");
		strSQL.append(" SELECT DISTINCT enc.patientId, enc.encType, o.episodeEncId as episodeEncounterId, enc.encounterId, doc.uid as assignedTo, doc.ufname as assignedToFname, doc.ulname as assignedToLname, ");
		strSQL.append(" e.deptId, e.serviceType, ");
		strSQL.append(" ROW_NUMBER() OVER ("+orderBy+") as rowNum");
		strSQL.append(" FROM ip_flowsheetitems fi ");
		strSQL.append(" join ip_ptorders o on o.orderitemid = fi.itemid ");
		strSQL.append(" join ip_pharmacy_verification pv on pv.orderid = o.orderid ");
		strSQL.append(" inner join ip_enc_mappings encmap on encmap.masterEncId = o.episodeEncId and encMap.deleteFlag = 0");
		strSQL.append(" inner join ip_encinfo e on e.encounterid = encMap.assocEncid and e.isActiveEncounter = 1");
		strSQL.append(" inner join enc on enc.encounterid = e.encounterid ");
		strSQL.append(" left outer join users doc ON doc.uid = pv.assignedto");
		strSQL.append(" WHERE fi.flowsheetid = :flowsheetId AND fi.type = 4 AND o.orderstatus <> 10");

		if(facilityId > 0){
			strSQL.append(" AND enc.facilityId = :facilityId");
		}
		if(deptId > 0){
			strSQL.append(" AND e.deptId = :deptId");
		}
		if(startDate.length() > 0){
			strSQL.append(" AND o.orderDateTime >= :startDateTime");
		}
		if(endDate.length() > 0){
			strSQL.append(" AND o.orderDateTime <= :endDateTime");
			paramMap.put("endDateTime", endDate);
		}
		if(assignedToId > 0){
			strSQL.append(" AND pv.assignedTo = :assignedTo");
		}

		strSQL.append(" UNION ");

		strSQL.append(" SELECT DISTINCT enc.patientId, enc.encType, enc.encounterId, o.episodeEncId as episodeEncounterId, doc.uid as assignedTo, doc.ufname as assignedToFname, doc.ulname as assignedToLname, ");
		strSQL.append(" e.deptId, e.serviceType, ");
		strSQL.append(" ROW_NUMBER() OVER ("+orderBy+") as rowNum");
		strSQL.append(" FROM ip_flowsheetitems fi ");
		strSQL.append(" join ip_configureRxGroup rxg on fi.itemid = rxg.groupid");
		strSQL.append(" join ip_configureRxGroupMember rxgm on rxg.groupid = rxgm.groupid");
		strSQL.append(" join ip_ptorders o on o.orderitemid = rxgm.itemid ");
		strSQL.append(" join ip_pharmacy_verification pv on pv.orderid = o.orderid ");
		strSQL.append(" inner join ip_enc_mappings encmap on encmap.masterEncId = o.episodeEncId and encMap.deleteFlag = 0");
		strSQL.append(" inner join ip_encinfo e on e.encounterid = encMap.assocEncid and e.isActiveEncounter = 1");
		strSQL.append(" inner join enc on enc.encounterid = e.encounterid ");
		strSQL.append(" left outer join users doc ON doc.uid = pv.assignedto");
		strSQL.append(" WHERE fi.flowsheetid = :flowsheetId AND fi.type = 8 AND o.orderstatus <> 10");

		if(facilityId > 0){
			strSQL.append(" AND enc.facilityId = :facilityId");
		}
		if(deptId > 0){
			strSQL.append(" AND e.deptId = :deptId");
		}
		if(startDate.length() > 0){
			strSQL.append(" AND o.orderDateTime >= :startDateTime");
		}
		if(endDate.length() > 0){
			strSQL.append(" AND o.orderDateTime <= :endDateTime");
		}
		if(assignedToId > 0){
			strSQL.append(" AND pv.assignedTo = :assignedTo");
		}

		paramMap.put("flowsheetId", flowsheetId);
		paramMap.put("facilityId", facilityId);
		paramMap.put("deptId", deptId);
		paramMap.put("startDateTime", startDate);
		paramMap.put("endDateTime", endDate);
		paramMap.put("assignedTo", assignedToId);
		
		strSQL.append(") as result ");
		if(recordsPerPage != -1){
			strSQL.append(" WHERE result.rowNum > " + start + " and result.rowNum <= " + ( start + recordsPerPage ) );
		}
		return namedParameterJdbcTemplate.queryForList(strSQL.toString(), paramMap);

	}	
	/**
	 * get count of my patient
	 * @param filterMap
	 * @return
	 */
	public int getMyPatientCount(Map<String, Object> filterMap) {
		List<Integer> tmplList = null;
		int totalCount = 0;
		Map<String,Object> paramMap = new HashMap<String,Object>();
		int assignedToId = Util.getIntValue(filterMap, "loggedInUserId");
		String userTimeZone = IPTzUtils.getTimeZoneForResource(assignedToId);		
		int facilityId = Util.getIntValue(filterMap,"selectedFacility");
		int deptId = Util.getIntValue(filterMap,"deptId");
		int flowsheetId = Util.getIntValue(filterMap,"flowsheetId");
		String startDate = "";
		String endDate = "";
		try{
			startDate = Util.getStrValue(filterMap, "startDate").length() > 0 ? IPTzUtils.convertDateStrInTz(Util.getStrValue(filterMap, "startDate"), "yyyy-MM-dd", userTimeZone, IPTzUtils.DEFAULT_DB_DT_FMT, IPTzUtils.DEFAULT_DB_TIME_ZONE  ) : "";
			endDate = Util.getStrValue(filterMap, "endDate").length() > 0 ? IPTzUtils.convertDateStrInTz(Util.getStrValue(filterMap, "endDate"), "yyyy-MM-dd", userTimeZone, IPTzUtils.DEFAULT_DB_DT_FMT, IPTzUtils.DEFAULT_DB_TIME_ZONE  ) : "";
		}catch(ParseException e){
			EcwLog.AppendExceptionToLog(e);
		}
		StringBuffer strSQL = new StringBuffer();
		String orderBy = "  ORDER BY enc.patientId desc";
		strSQL.append("SELECT COUNT(*) as mypatientcount FROM (");
		strSQL.append(" SELECT DISTINCT enc.patientId, enc.encType, o.episodeEncId as episodeEncounterId, enc.encounterId, doc.uid as assignedTo, doc.ufname as assignedToFname, doc.ulname as assignedToLname, ");
		strSQL.append(" e.deptId, e.serviceType, ");
		strSQL.append(" ROW_NUMBER() OVER ("+orderBy+") as rowNum");
		strSQL.append(" FROM ip_flowsheetitems fi ");
		strSQL.append(" join ip_ptorders o on o.orderitemid = fi.itemid ");
		strSQL.append(" join ip_pharmacy_verification pv on pv.orderid = o.orderid ");
		strSQL.append(" inner join ip_enc_mappings encmap on encmap.masterEncId = o.episodeEncId and encMap.deleteFlag = 0");
		strSQL.append(" inner join ip_encinfo e on e.encounterid = encMap.assocEncid and e.isActiveEncounter = 1");
		strSQL.append(" inner join enc on enc.encounterid = e.encounterid ");
		strSQL.append(" left outer join users doc ON doc.uid = pv.assignedto");
		strSQL.append(" WHERE fi.flowsheetid = :flowsheetId AND fi.type = 4 AND o.orderstatus <> 10");
		
		if(facilityId > 0){
			strSQL.append(" AND enc.facilityId = :facilityId");
		}
		if(deptId > 0){
			strSQL.append(" AND e.deptId = :deptId");
		}
		if(startDate.length() > 0){
			strSQL.append(" AND o.orderDateTime >= :startDateTime");
		}
		if(endDate.length() > 0){
			strSQL.append(" AND o.orderDateTime <= :endDateTime");
		}
		if(assignedToId > 0){
			strSQL.append(" AND pv.assignedTo = :assignedTo");
		}	

		strSQL.append(" UNION ");

		strSQL.append(" SELECT DISTINCT enc.patientId, enc.encType, enc.encounterId, o.episodeEncId as episodeEncounterId, doc.uid as assignedTo, doc.ufname as assignedToFname, doc.ulname as assignedToLname, ");
		strSQL.append(" e.deptId, e.serviceType, ");
		strSQL.append(" ROW_NUMBER() OVER ("+orderBy+") as rowNum");
		strSQL.append(" FROM ip_flowsheetitems fi ");
		strSQL.append(" join ip_configureRxGroup rxg on fi.itemid = rxg.groupid");
		strSQL.append(" join ip_configureRxGroupMember rxgm on rxg.groupid = rxgm.groupid");
		strSQL.append(" join ip_ptorders o on o.orderitemid = rxgm.itemid ");
		strSQL.append(" join ip_pharmacy_verification pv on pv.orderid = o.orderid ");
		strSQL.append(" inner join ip_enc_mappings encmap on encmap.masterEncId = o.episodeEncId and encMap.deleteFlag = 0");
		strSQL.append(" inner join ip_encinfo e on e.encounterid = encMap.assocEncid and e.isActiveEncounter = 1");
		strSQL.append(" inner join enc on enc.encounterid = e.encounterid ");
		strSQL.append(" left outer join users doc ON doc.uid = pv.assignedto");
		strSQL.append(" WHERE fi.flowsheetid = :flowsheetId AND fi.type = 8 AND o.orderstatus <> 10");
			
		if(facilityId > 0){
			strSQL.append(" AND enc.facilityId = :facilityId");
		}
		if(deptId > 0){
			strSQL.append(" AND e.deptId = :deptId");
		}
		if(startDate.length() > 0){
			strSQL.append(" AND o.orderDateTime >= :startDateTime");
		}
		if(endDate.length() > 0){
			strSQL.append(" AND o.orderDateTime <= :endDateTime");
		}
		if(assignedToId > 0){
			strSQL.append(" AND pv.assignedTo = :assignedTo");
		}
		paramMap.put("flowsheetId", flowsheetId);	
		paramMap.put("facilityId", facilityId);
		paramMap.put("deptId", deptId);
		paramMap.put("startDateTime", startDate);
		paramMap.put("endDateTime", endDate);
		paramMap.put("assignedTo", assignedToId);
		strSQL.append(") as result ");		
		totalCount = namedParameterJdbcTemplate.queryForObject(strSQL.toString(), paramMap, new RowMapper<Integer>() {
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException{
				return rs.getInt("mypatientcount");
			}
		});		
		return totalCount;
	}
	public ArrayList<HashMap<String, Object>> getPatientsByFlowsheetId(int flowsheetId){

		Map<String,Object> paramMap = new HashMap<String,Object>();
		StringBuffer strSQL = new StringBuffer("");
		strSQL.append(" select distinct e.patientid, e.enctype, o.encounterid, o.episodeEncId");
		strSQL.append(" from ip_flowsheetitems fi ");
		strSQL.append(" join ip_ptorders o on o.orderitemid = fi.itemid ");
		strSQL.append(" inner join ip_enc_mappings encmap on encmap.masterEncId = o.episodeEncId and encMap.deleteFlag = 0");
		strSQL.append(" inner join ip_encinfo e on e.encounterid = encMap.assocEncid and e.isActiveEncounter = 1");
		strSQL.append(" inner join enc on enc.encounterid = e.encounterid ");
		strSQL.append(" where fi.flowsheetid = :flowsheetId and fi.type = 4 and o.orderstatus <> 10");
		strSQL.append(" union ");
		strSQL.append(" select distinct e.patientid, e.enctype, o.encounterid, o.episodeEncId");
		strSQL.append(" from ip_flowsheetitems fi ");
		strSQL.append(" join ip_configureRxGroup rxg on fi.itemid = rxg.groupid");
		strSQL.append(" join ip_configureRxGroupMember rxgm on rxg.groupid = rxgm.groupid");
		strSQL.append(" join ip_ptorders o on o.orderitemid = rxgm.itemid ");
		strSQL.append(" inner join ip_enc_mappings encmap on encmap.masterEncId = o.episodeEncId and encMap.deleteFlag = 0");
		strSQL.append(" inner join ip_encinfo e on e.encounterid = encMap.assocEncid and e.isActiveEncounter = 1");
		strSQL.append(" inner join enc on enc.encounterid = e.encounterid ");
		strSQL.append(" where fi.flowsheetid = :flowsheetId and fi.type = 8 and o.orderstatus <> 10");

		paramMap.put("flowsheetId", flowsheetId);

		return ( ArrayList<HashMap<String, Object>>) namedParameterJdbcTemplate.query(strSQL.toString(), paramMap, new RowMapper<HashMap<String, Object>>(){

			@Override
			public HashMap<String, Object> mapRow(ResultSet rs, int arg1) throws SQLException {
				HashMap<String, Object> userMap = new HashMap<String, Object>();
				userMap.put("patientId", rs.getInt("patientid"));
				userMap.put("encounterId", rs.getInt("encounterid"));
				userMap.put("encType", rs.getInt("enctype"));
				userMap.put("episodeEncounterId", rs.getInt("episodeEncId"));
				return userMap;
			}
		});

	}

	public List<Map<String,Object>> getMedicationItemIdsForRxGroup(int rxGroupId){
		Map<String,Object> paramMap = new HashMap<String,Object>();
		StringBuffer strSQL = new StringBuffer("");

		strSQL.append(" select rxg.groupid, rxg.groupname, rxgm.itemid, i.itemName  ");
		strSQL.append(" from ip_configureRxGroup rxg ");
		strSQL.append(" join ip_configureRxGroupMember rxgm on rxgm.groupid = rxg.groupid ");
		strSQL.append(" join ip_items i on i.itemid = rxgm.itemid ");
		strSQL.append(" where rxg.groupid = :rxGroupId and rxgm.delflag = 0");

		paramMap.put("rxGroupId", rxGroupId);

		return namedParameterJdbcTemplate.queryForList(strSQL.toString(), paramMap);
	}

	public HashMap<String, ArrayList<String>> getImmunizationDatesPatient(int patientId, int itemId, String startDate, String endDate, int loggedInUserId){

		final String userTimeZone = IPTzUtils.getTimeZoneForResource(loggedInUserId);

		Map<String,Object> paramMap = new HashMap<String,Object>();
		StringBuffer strSQL = new StringBuffer("");

		strSQL.append(" select distinct format(imm.givendate, 'yyyy-MM-dd') as givendate, ");
		strSQL.append(" STUFF((select ', ' + format(im.giventime, 'HH:mm:ss') from immunizations im ");
		strSQL.append(" where im.itemid = :itemId and im.patientid = :patientId and im.givendate = imm.givendate FOR XML PATH('')), 1, 1, '') [giventimes]");
		strSQL.append(" from immunizations imm ");
		strSQL.append(" where imm.itemId = :itemId and imm.patientid = :patientId");
		strSQL.append(" and imm.givendate is not null and imm.deleteflag = 0");

		if(!"".equals(startDate)){
			strSQL.append(" and imm.givendate >= :startDate");
		}

		if(!"".equals(endDate)){
			strSQL.append(" and imm.givendate <= :endDate");
		}

		strSQL.append(" order by givendate asc");

		paramMap.put("itemId", itemId);
		paramMap.put("patientId", patientId);

		if(!"".equals(startDate)){
			paramMap.put("startDate", startDate);
		}

		if(!"".equals(endDate)){
			paramMap.put("endDate", endDate);
		}

		return ( HashMap<String, ArrayList<String>>) namedParameterJdbcTemplate.query(strSQL.toString(), paramMap, new ResultSetExtractor<HashMap<String, ArrayList<String>>>() {

			@Override
			public HashMap<String, ArrayList<String>> extractData(ResultSet rs) throws SQLException {

				HashMap<String, ArrayList<String>> result = new HashMap<String, ArrayList<String>>();
				while(rs.next()){

					ArrayList<String> timeMap = new ArrayList<String>();

					String givenDate = rs.getString("givenDate");
					String givenTimes = rs.getString("givenTimes");

					String arrGivenTimes[] = givenTimes.split(",");
					for(String givenTime : arrGivenTimes){
						timeMap.add(givenTime);
					}
					try{
						givenDate = IPTzUtils.convertDateStrInTz(givenDate, "yyyy-MM-dd" , IPTzUtils.DEFAULT_DB_TIME_ZONE, "MM/dd/yyyy", userTimeZone );
					}catch(ParseException e){
						EcwLog.AppendExceptionToLog(e);
					}
					result.put(givenDate, timeMap);
				}

				return result;
			}						
		});

	}

	public List<Map<String, Object>> getRxDataForPatient(int patientId, int drugItemId, String startDate, String endDate){

		Map<String,Object> paramMap = new HashMap<String,Object>();
		StringBuffer strSQL = new StringBuffer("");

		strSQL.append(" SELECT idas.administereddatetime, CAST(idas.administereddatetime AS time) as administeredtime , idas.administrationstatus, idas.userid, idas.username ");
		strSQL.append(" FROM IP_Drug_Admin_Schedule idas ");
		strSQL.append(" INNER JOIN IP_ptmedicationorders ipmo on ipmo.medorderid = idas.orderid ");
		strSQL.append(" inner join ip_ptorders ipo on ipo.orderid = ipmo.ptorderid ");
		strSQL.append(" join enc on ipo.episodeEncId = enc.encounterid ");
		strSQL.append(" where ipmo.ipitemid = :drugItemId and enc.patientid = :patientId");

		if(!"".equals(startDate)){
			strSQL.append(" and idas.administereddatetime >= :startDate");
			paramMap.put("startDate", startDate);
		}

		if(!"".equals(endDate)){
			strSQL.append(" and idas.administereddatetime <= :endDate");
			paramMap.put("endDate", endDate);
		}

		strSQL.append(" order by administereddatetime asc");

		paramMap.put("drugItemId", drugItemId);
		paramMap.put("patientId", patientId);

		return namedParameterJdbcTemplate.queryForList(strSQL.toString(), paramMap);

	}

	public List<Map<String, Object>> getMyPatientsList(int loggedInUserId){
		Map<String, Object> paramMap = new HashMap<String, Object>();

		StringBuffer strSQL = new StringBuffer("");
		strSQL.append(" SELECT distinct enc.patientId, enc.encounterId, enc.encType, o.episodeEncId as episodeEncounterId, ");
		strSQL.append(" e.deptId, e.serviceType ");
		strSQL.append(" from ip_pharmacy_verification pv ");
		strSQL.append(" inner join ip_ptorders o on o.orderid = pv.orderid ");
		strSQL.append(" inner join ip_enc_mappings encmap on encmap.masterEncId = o.episodeEncId and encMap.deleteFlag = 0");
		strSQL.append(" inner join ip_encinfo e on e.encounterid = encMap.assocEncid and e.isActiveEncounter = 1");
		strSQL.append(" inner join enc on enc.encounterid = e.encounterid ");
		strSQL.append(" where pv.assignedTo = :assignedTo and pv.deleteflag = 0 ");
		paramMap.put("assignedTo", loggedInUserId);

		return namedParameterJdbcTemplate.queryForList(strSQL.toString(), paramMap);
	}

}