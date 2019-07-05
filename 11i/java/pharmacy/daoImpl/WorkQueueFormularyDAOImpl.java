package inpatientWeb.pharmacy.daoImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;
import inpatientWeb.pharmacy.dao.WorkQueueFormularyDAO;

@Repository
public class WorkQueueFormularyDAOImpl implements WorkQueueFormularyDAO {
	
	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public int getAvailableMedCountForFacility(List<Integer> formularyIds, int facilityId){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("facilityId", facilityId);
		paramMap.put("formularyIds", formularyIds);

		StringBuilder query = new StringBuilder();

		query.append(" SELECT count(1) ");
		query.append(" FROM ip_drugformulary_facilities ");
		query.append(" WHERE delflag = 0 and formularyid in (:formularyIds) and facilityid = :facilityId ");

		return namedParameterJdbcTemplate.queryForObject(query.toString(), paramMap, Integer.class);
	}

	public boolean isMedsAvailableForFacility(List<Integer> formularyIds, int facilityId){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("facilityId", facilityId);
		paramMap.put("formularyIds", formularyIds);

		StringBuilder query = new StringBuilder();

		query.append(" select case when (count(1) > 0) then 'true' else 'false' end from ");
		query.append(" (SELECT (case when (df.isavailableforall = 1 or  dff.formularyid is not null ) then 1 else 0 end) as isavailable");
		query.append(" FROM ip_drugformulary df");
		query.append(" left outer join ip_drugformulary_facilities dff on dff.formularyid = df.id and  dff.delflag = 0 and facilityid = :facilityId");
		query.append(" WHERE df.id in (:formularyIds)) temp");
		query.append(" where temp.isavailable = 1");

		return namedParameterJdbcTemplate.queryForObject(query.toString(), paramMap, Boolean.class);
	}
	
	public Map<String, Object> getFormularyNotesByRoutedGenericItemId(int routedGenericItemId){

		StringBuilder query = new StringBuilder();
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("routedGenericItemId", routedGenericItemId);

		query.append(" select n.orderentry_instr, n.emar_instr, n.pharmacy_instr, n.internal_notes");
		query.append(" from ip_drugformulary_notes n ");
		query.append(" where n.routedgenericitemid = :routedGenericItemId");	
		return namedParameterJdbcTemplate.queryForMap(query.toString(), paramMap);

	}

	public Map<String, Object> getFormularyCommonSettingsByRoutedGenericItemId(long drugFormularyId){

		Map<String, Object> result = null;
		try{
			StringBuilder query = new StringBuilder();
			Map<String,Object> paramMap = new HashMap<String,Object>();
			paramMap.put("drugFormularyId", drugFormularyId);

			 query.append(" SELECT top 1 cs.dualverifyreqd, cs.dualverifyreqd, drug.isdrugtypebulk_formulary as isdrugtypebulk, ");
             query.append(" drug.ischargeableatdispense_formulary as ischargeableatdispense ");
             query.append(" FROM ip_drugformulary_common_settings cs ");
             query.append(" INNER JOIN ip_drugformulary drug on cs.routedgenericitemid = drug.routedgenericitemid and cs.delflag=0 ");
             query.append(" WHERE drug.id = :drugFormularyId AND drug.delflag = 0");

			result = namedParameterJdbcTemplate.queryForMap(query.toString(), paramMap);
		} catch (DataAccessException e){
			EcwLog.AppendExceptionToLog(e);
		}
		return result;
	}

	public int getRoutedGenericItemIdByItemId(int itemId){
		int result = 0;

		StringBuilder query = new StringBuilder();
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("itemId", itemId);
		try
		{
			if(itemId > 0) {
				query.append("select i1.itemid from ip_items i1 inner join  ip_items i2 on i1.routedgenericid = i2.routedgenericid where i2.itemid = :itemId and i1.routedgenericid = i1.routeddrugid");	
				result = namedParameterJdbcTemplate.queryForObject(query.toString(),paramMap,Integer.class);
			}		
		} catch (DataAccessException e) {
			EcwLog.AppendExceptionToLog(e);	
		}

		return result;
	}
}
