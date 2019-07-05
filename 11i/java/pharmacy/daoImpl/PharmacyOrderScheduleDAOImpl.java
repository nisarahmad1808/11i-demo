package inpatientWeb.pharmacy.daoImpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;

import inpatientWeb.pharmacy.beans.PharmacyOrderScheduleOrg;
import inpatientWeb.pharmacy.beans.PharmacyOrderScheduleUpdated;
import inpatientWeb.pharmacy.dao.PharmacyOrderScheduleDAO;
import inpatientWeb.utils.IPTzUtils;

@Repository
public class PharmacyOrderScheduleDAOImpl implements PharmacyOrderScheduleDAO {

	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public boolean createPharmacyOrderScheduleOrg(PharmacyOrderScheduleOrg pharmacyOrderScheduleOrg) {
		Map<String,Object> paramMap = new HashMap<String,Object>();
		boolean result = false;

		StringBuffer strSQL = new StringBuffer("insert into ip_pharmacy_order_schedule_time_org (scheduleId, scheduleTime, frequencyId, orderId, medOrderId, verificationId, createdBy, createdOn)");
		strSQL.append(" values (:scheduleId, :scheduleTime, :frequencyId, :orderId, :medOrderId, :verificationId, :createdBy, :createdOn)");

		try{
			String currentDateTime = IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT);

			paramMap.put("scheduleId", pharmacyOrderScheduleOrg.getScheduleId());
			paramMap.put("scheduleTime", pharmacyOrderScheduleOrg.getScheduleTime());
			paramMap.put("frequencyId", pharmacyOrderScheduleOrg.getFrequencyId());
			paramMap.put("orderId", pharmacyOrderScheduleOrg.getOrderId());
			paramMap.put("medOrderId", pharmacyOrderScheduleOrg.getMedOrderId());
			paramMap.put("verificationId", pharmacyOrderScheduleOrg.getVerificationId());
			paramMap.put("createdBy", pharmacyOrderScheduleOrg.getCreatedBy());
			paramMap.put("createdOn", currentDateTime);

			namedParameterJdbcTemplate.update(strSQL.toString(), paramMap);

			result = true;

		} catch ( DataAccessException e ){
			EcwLog.AppendExceptionToLog(e);
		}
		return result;

	}

	@Override
	public boolean createPharmacyOrderScheduleUpdated(PharmacyOrderScheduleUpdated pharmacyOrderScheduleUpdated) {
		Map<String,Object> paramMap = new HashMap<String,Object>();
		boolean result = false;

		StringBuffer strSQL = new StringBuffer("insert into ip_pharmacy_order_schedule_time_updated ( orderId, medOrderId, verificationId, scheduleTime, createdBy, createdOn)");
		strSQL.append(" values (:orderId, :medOrderId, :verificationId, :scheduleTime, :createdBy, :createdOn)");

		try{
			String currentDateTime = IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT);

			paramMap.put("orderId", pharmacyOrderScheduleUpdated.getOrderId());
			paramMap.put("medOrderId", pharmacyOrderScheduleUpdated.getMedOrderId());
			paramMap.put("verificationId", pharmacyOrderScheduleUpdated.getVerificationId());
			paramMap.put("scheduleTime", pharmacyOrderScheduleUpdated.getScheduleTime());
			paramMap.put("createdBy", pharmacyOrderScheduleUpdated.getCreatedBy());
			paramMap.put("createdOn", currentDateTime);

			namedParameterJdbcTemplate.update(strSQL.toString(), paramMap);

			result = true;

		} catch ( DataAccessException e ){
			EcwLog.AppendExceptionToLog(e);
		}
		return result;

	}

	@Override
	public boolean deletePharmacyOrderScheduleUpdated(Map<String, Object> paramMap) {
		boolean result = false;

		StringBuffer strSQL = new StringBuffer("update ip_pharmacy_order_schedule_time_updated set deleteFlag = 1 where orderId = :orderId");

		try{
			namedParameterJdbcTemplate.update(strSQL.toString(), paramMap);

			result = true;

		} catch ( DataAccessException e ){
			EcwLog.AppendExceptionToLog(e);
		}
		return result;
	}

	public List<PharmacyOrderScheduleUpdated> getPharmacyOrderScheduleUpdated(Map<String, Object> paramMap) {

		StringBuffer strSQL = new StringBuffer("select orderId, medOrderId, verificationId, scheduleTime, createdBy, createdOn ");
		strSQL.append(" from ip_pharmacy_order_schedule_time_updated ");
		strSQL.append(" where orderId = :orderId and deleteflag = 0");

		return namedParameterJdbcTemplate.query(strSQL.toString(), paramMap, new PharmacyOrderScheduleUpdated());


	}

	public boolean isPharmacyOrderScheduleOrgExist(Map<String, Object> paramMap){

		StringBuffer strSQL = new StringBuffer("select id");
		strSQL.append(" from ip_pharmacy_order_schedule_time_org ");
		strSQL.append(" where orderId = :orderId and deleteflag = 0");

		return namedParameterJdbcTemplate.query(strSQL.toString(), paramMap, new ResultSetExtractor<Boolean>() {

			public Boolean extractData(ResultSet rs) throws SQLException {
				return rs.next();
			}
		});
	}

	public boolean isPharmacyOrderScheduleUpdatedExist(Map<String, Object> paramMap){

		StringBuffer strSQL = new StringBuffer("select id");
		strSQL.append(" from ip_pharmacy_order_schedule_time_updated ");
		strSQL.append(" where orderId = :orderId and deleteflag = 0");

		return namedParameterJdbcTemplate.query(strSQL.toString(), paramMap, new ResultSetExtractor<Boolean>() {

			public Boolean extractData(ResultSet rs) throws SQLException {
				return rs.next();
			}
		});
	}

}
