package inpatientWeb.pharmacy.daoImpl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;

import inpatientWeb.pharmacy.beans.PharmacyVerificationSessionLog;
import inpatientWeb.pharmacy.dao.PharmacyConcurrencyDAO;
import inpatientWeb.utils.IPTzUtils;
import inpatientWeb.utils.Util;

@Repository
public class PharmacyConcurrencyDAOImpl implements PharmacyConcurrencyDAO {

	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public boolean createPharmacyVerificationSessionLog(PharmacyVerificationSessionLog pharmacyVerificationSessionLog){

		Map<String,Object> paramMap = new HashMap<String,Object>();
		boolean result = false;

		StringBuffer strSQL = new StringBuffer("insert into ip_pharmacy_verification_sessionlog (patientid, userid, screenid, orderid, lockedtime) values (:patientId, :userId, :screenId, :orderId, :lockedTime)");

		try{
			String currentDateTime = IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT);

			paramMap.put("patientId", pharmacyVerificationSessionLog.getPatientId());
			paramMap.put("userId", pharmacyVerificationSessionLog.getUserId());
			paramMap.put("screenId", pharmacyVerificationSessionLog.getScreenId());
			paramMap.put("orderId", pharmacyVerificationSessionLog.getOrderId());
			paramMap.put("lockedTime", currentDateTime);

			namedParameterJdbcTemplate.update(strSQL.toString(), paramMap);

			result = true;

		} catch ( DataAccessException e ){
			EcwLog.AppendExceptionToLog(e);
		}
		return result;

	}

	public boolean deletePharmacyVerificationSessionLog(Map<String, Object> filterMap){

		boolean result = false;

		StringBuffer strSQL = new StringBuffer("update ip_pharmacy_verification_sessionlog set deleteflag = 1 where orderId = :orderId and patientId = :patientId and userId = :userId");

		try{
			namedParameterJdbcTemplate.update(strSQL.toString(), filterMap);
			result = true;

		} catch ( DataAccessException e ){
			EcwLog.AppendExceptionToLog(e);
		}
		return result;

	}

	public PharmacyVerificationSessionLog readPharmacyVerificationSessionLog(Map<String, Object> filterMap){

		StringBuffer strSQL = new StringBuffer("select id, patientid, orderid, verificationid, userid, lockedtime, screenid, deleteflag,");
		strSQL.append(" concat (users.ufname,' ' , users.ulname ) as username");
		strSQL.append(" from ip_pharmacy_verification_sessionlog");
		strSQL.append(" inner join users on users.uid = userid");
		strSQL.append(" WHERE deleteflag = 0");
		if(Util.getIntValue(filterMap, "orderId") > 0){
			strSQL.append(" and orderId = :orderId");
		}
		if(Util.getIntValue(filterMap, "patientId") > 0){
			strSQL.append(" and patientId = :patientId");
		}
		if(Util.getIntValue(filterMap, "userId") > 0){
			strSQL.append(" and userId <> :userId");
		}
		return (PharmacyVerificationSessionLog)namedParameterJdbcTemplate.queryForObject(strSQL.toString(), filterMap, new PharmacyVerificationSessionLog());

	}

	public int getPharmacyVerificationSessionLogCount(Map<String, Object> filterMap){

		StringBuffer strSQL = new StringBuffer("select count(1) from ip_pharmacy_verification_sessionlog WHERE deleteflag = 0 ");

		if(Util.getIntValue(filterMap, "orderId") > 0){
			strSQL.append(" and orderId = :orderId");
		}

		if(Util.getIntValue(filterMap, "patientId") > 0){
			strSQL.append(" and patientId = :patientId");
		}

		if(Util.getIntValue(filterMap, "userId") > 0){
			strSQL.append(" and userId <> :userId");
		}

		return namedParameterJdbcTemplate.queryForObject(strSQL.toString(), filterMap, Integer.class);

	}

}
