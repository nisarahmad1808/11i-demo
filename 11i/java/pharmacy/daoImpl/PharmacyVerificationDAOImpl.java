package inpatientWeb.pharmacy.daoImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;
import inpatientWeb.pharmacy.beans.PharmacyStatus;
import inpatientWeb.pharmacy.beans.PharmacyVerification;
import inpatientWeb.pharmacy.dao.PharmacyVerificationDAO;
import inpatientWeb.utils.IPTzUtils;
import inpatientWeb.utils.Util;

@Repository
public class PharmacyVerificationDAOImpl implements PharmacyVerificationDAO {
	
	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private static final int DELETEFLAG = 1;
	
	public ArrayList<PharmacyStatus> getPharmacyStatus() {

		Map<String,Object> paramMap = new HashMap<String,Object>();
		StringBuffer strSQL = new StringBuffer("SELECT id, status, createdby, createdon, modifiedby, modifiedon, userid, notes, deleteflag FROM ip_pharmacy_verification_status WHERE deleteflag = 0");

		return (ArrayList<PharmacyStatus>) namedParameterJdbcTemplate.query(strSQL.toString(), paramMap, new PharmacyStatus());
	}

	public PharmacyStatus getPharmacyStatusByName(String statusName) {

		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("status", statusName);

		StringBuffer strSQL = new StringBuffer("SELECT id, status, createdby, createdon, modifiedby, modifiedon, userid, notes, deleteflag FROM ip_pharmacy_verification_status WHERE deleteflag = 0 and lower(status) = :status");

		return namedParameterJdbcTemplate.queryForObject(strSQL.toString(), paramMap, new PharmacyStatus());

	}

	public boolean createPharmacyVerification(PharmacyVerification pharmacyVerification) {
		Map<String,Object> paramMap = new HashMap<String,Object>();
		boolean result = false;

		StringBuffer strSQL = new StringBuffer("insert into ip_pharmacy_verification (orderid, status, createdby, createdon) values (:orderId, :status, :createdBy, :createdOn)");

		try{
			String currentDateTime = IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT);

			paramMap.put("orderId", pharmacyVerification.getOrderId());
			paramMap.put("status", pharmacyVerification.getStatus());
			paramMap.put("createdBy", pharmacyVerification.getCreatedBy());
			paramMap.put("createdOn", currentDateTime);

			namedParameterJdbcTemplate.update(strSQL.toString(), paramMap);

			result = true;

		} catch ( DataAccessException e ){
			EcwLog.AppendExceptionToLog(e);
		}
		return result;
	}
	
	public boolean resetToPharmacyVerificationStatusByOrderId(PharmacyVerification pharmacyVerification) {
		Map<String,Object> paramMap = new HashMap<String,Object>();
		boolean result = false;

		StringBuffer strSQL = new StringBuffer("UPDATE ip_pharmacy_verification SET deleteFlag = :delflag, modifiedby = :modifiedBy, modifiedOn = :modifiedOn WHERE orderId = :orderId AND deleteFlag = 0");

		try{
			String currentDateTime = IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT);
			
			paramMap.put("delflag", DELETEFLAG);
			paramMap.put("modifiedBy", pharmacyVerification.getModifiedBy());
			paramMap.put("modifiedOn", currentDateTime);
			paramMap.put("orderId", pharmacyVerification.getOrderId());

			namedParameterJdbcTemplate.update(strSQL.toString(), paramMap);

			result = true;

		} catch ( DataAccessException e ){
			EcwLog.AppendExceptionToLog(e);
		}
		return result;
	}

	public boolean updateStatus(Map<String, Object> paramMap ) {

		boolean result = false;
		StringBuffer strSQL = new StringBuffer("UPDATE ip_pharmacy_verification SET status = :status, verifiedby = :verifiedBy, modifiedby = :modifiedBy, reasonId = :reasonId, verifiedOn = :verifiedOn, modifiedOn = :modifiedOn WHERE id = :verificationId");

		try{
			
			MapSqlParameterSource params = new MapSqlParameterSource();
			params.addValue("status", Util.getIntValue(paramMap, "status"));
			params.addValue("verifiedBy", Util.getIntValue(paramMap, "verifiedBy") == 0 ? null : Util.getIntValue(paramMap, "verifiedBy"));
			params.addValue("modifiedBy", Util.getIntValue(paramMap, "modifiedBy") == 0 ? null : Util.getIntValue(paramMap, "modifiedBy"));
			params.addValue("reasonId", Util.getIntValue(paramMap, "reasonId") == 0 ? null : Util.getIntValue(paramMap, "reasonId"));
			params.addValue("verifiedOn", IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT));
			params.addValue("modifiedOn", IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT));
			params.addValue("verificationId", Util.getIntValue(paramMap, "verificationId"));
			
			namedParameterJdbcTemplate.update(strSQL.toString(), params);
			result = true;

		} catch ( DataAccessException e ){
			EcwLog.AppendExceptionToLog(e);
		}
		return result;
	}

	public boolean assignOrders(String[] verificationIds, int assignTo, int assignedBy, int modifiedBy ) {

		boolean result = false;

		StringBuffer strSQL = new StringBuffer("UPDATE ip_pharmacy_verification SET assignedTo = :assignTo, modifiedby = :modifiedBy, assignedBy = :assignedBy, assignedOn = :assignedOn, modifiedOn = :modifiedOn WHERE id = :verificationId");

		try{
			String currentDateTime = IPTzUtils.getCurrentUTCDateTime(IPTzUtils.DEFAULT_DB_DT_FMT);
			for (String verificationId : verificationIds){
				
				MapSqlParameterSource params = new MapSqlParameterSource();
				params.addValue("assignTo", assignTo == 0 ? null : assignTo);
				params.addValue("assignedBy", assignedBy == 0 ? null : assignedBy);
				params.addValue("modifiedBy", modifiedBy == 0 ? null : modifiedBy);
				params.addValue("assignedOn", currentDateTime);
				params.addValue("modifiedOn", currentDateTime);
				params.addValue("verificationId", verificationId);
				namedParameterJdbcTemplate.update(strSQL.toString(), params);
			}

			result = true;

		} catch ( DataAccessException e ){
			EcwLog.AppendExceptionToLog(e);
		}

		return result;
	}
	
	public PharmacyVerification readPharmacyVerificationById(int pharmacyVerificationId){
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("pharmacyVerificationId", pharmacyVerificationId);

		StringBuffer strSQL = new StringBuffer("select id, orderid, signatureid, status, createdby, createdon, modifiedby, modifiedon, verifiedby, verifiedon, assignedto, assignedby, assignedon, deleteflag, notes, reasonid, generateBarcode, dualVerification from ip_pharmacy_verification WHERE id = :pharmacyVerificationId");
		return namedParameterJdbcTemplate.queryForObject(strSQL.toString(), paramMap, new PharmacyVerification());

	}

	public boolean updatePharmacyVerification(PharmacyVerification pharmacyVerification){
		
		boolean result = false;

		StringBuffer strSQL = new StringBuffer("UPDATE ip_pharmacy_verification SET status = :status, assignedTo = :assignedTo, assignedBy = :assignedBy, assignedOn = :assignedOn, modifiedby = :modifiedBy, modifiedOn = :modifiedOn, verifiedBy = :verifiedBy, verifiedOn = :verifiedOn, reasonid = :reasonId, generateBarcode = :generateBarcode, dualVerification = :dualVerification  WHERE id = :verificationId");

		try{
			MapSqlParameterSource params = new MapSqlParameterSource();
			params.addValue("status", pharmacyVerification.getStatus());
			params.addValue("assignedTo", pharmacyVerification.getAssignedTo() == 0 ? null : pharmacyVerification.getAssignedTo() );
			params.addValue("assignedBy", pharmacyVerification.getAssignedBy() == 0 ? null : pharmacyVerification.getAssignedBy() );
			params.addValue("assignedOn", pharmacyVerification.getAssignedOn());
			params.addValue("modifiedBy", pharmacyVerification.getModifiedBy() == 0 ? null : pharmacyVerification.getModifiedBy() );
			params.addValue("modifiedOn", pharmacyVerification.getModifiedOn());
			params.addValue("verifiedBy", pharmacyVerification.getModifiedBy() == 0 ? null : pharmacyVerification.getModifiedBy() );
			params.addValue("verifiedOn", pharmacyVerification.getModifiedOn());
			params.addValue("generateBarcode", pharmacyVerification.isGenerateBarcode() ? 1 : 0);
			params.addValue("dualVerification", pharmacyVerification.isDualVerification() ? 1 : 0);
			params.addValue("reasonId", pharmacyVerification.getReasonId() == 0 ? null : pharmacyVerification.getReasonId() );
			params.addValue("verificationId", pharmacyVerification.getId());

			namedParameterJdbcTemplate.update(strSQL.toString(), params);

			result = true;

		} catch ( DataAccessException e ){
			EcwLog.AppendExceptionToLog(e);
		}
		return result;
	}

}
