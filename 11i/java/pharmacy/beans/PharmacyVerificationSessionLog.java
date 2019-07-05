package inpatientWeb.pharmacy.beans;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class PharmacyVerificationSessionLog implements RowMapper<PharmacyVerificationSessionLog> {
	
	public static final int SCREEN_MEDICATIONPROFILE = 1;
	
	private int id;
	private int patientId;
	private String patientName;
	private int orderId;
	private int verificationId;
	private int userId;
	private String userName;
	private String lockedTime;
	private int screenId;
	private int deleteFlag;
	private boolean allowContinue;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPatientId() {
		return patientId;
	}
	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public int getVerificationId() {
		return verificationId;
	}
	public void setVerificationId(int verificationId) {
		this.verificationId = verificationId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getLockedTime() {
		return lockedTime;
	}
	public void setLockedTime(String lockedTime) {
		this.lockedTime = lockedTime;
	}
	public int getScreenId() {
		return screenId;
	}
	public void setScreenId(int screenId) {
		this.screenId = screenId;
	}
	public int getDeleteFlag() {
		return deleteFlag;
	}
	public void setDeleteFlag(int deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	public boolean isAllowContinue() {
		return allowContinue;
	}
	public void setAllowContinue(boolean allowContinue) {
		this.allowContinue = allowContinue;
	}
	
	@Override
	public PharmacyVerificationSessionLog mapRow(ResultSet rs, int arg1) throws SQLException {
		PharmacyVerificationSessionLog pharmacyVerificationSessionLog = new PharmacyVerificationSessionLog();
		pharmacyVerificationSessionLog.setId(rs.getInt("id"));
		pharmacyVerificationSessionLog.setPatientId(rs.getInt("patientid"));
		pharmacyVerificationSessionLog.setOrderId(rs.getInt("orderid"));
		pharmacyVerificationSessionLog.setVerificationId(rs.getInt("verificationid"));
		pharmacyVerificationSessionLog.setUserId(rs.getInt("userid"));
		pharmacyVerificationSessionLog.setLockedTime(rs.getString("lockedtime"));
		pharmacyVerificationSessionLog.setScreenId(rs.getInt("screenid"));
		pharmacyVerificationSessionLog.setDeleteFlag(rs.getInt("deleteflag"));
		pharmacyVerificationSessionLog.setUserName(rs.getString("username"));
		return pharmacyVerificationSessionLog;
	}

}
