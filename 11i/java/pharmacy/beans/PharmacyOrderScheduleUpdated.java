package inpatientWeb.pharmacy.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

public class PharmacyOrderScheduleUpdated implements RowMapper<PharmacyOrderScheduleUpdated>{

	private int id;
	private String scheduleTime;
	private int frequencyId;
	private int orderId;
	private int medOrderId;
	private int verificationId;
	private int deleteFlag;
	private int createdBy;
	private String createdByName;
	private Date createdOn;
	private String strCreatedOn;
	private int modifiedBy;
	private String modifiedByName;
	private Date modifiedOn;
	private String strModifiedOn;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getScheduleTime() {
		return scheduleTime;
	}
	public void setScheduleTime(String scheduleTime) {
		this.scheduleTime = scheduleTime;
	}
	public int getFrequencyId() {
		return frequencyId;
	}
	public void setFrequencyId(int frequencyId) {
		this.frequencyId = frequencyId;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public int getMedOrderId() {
		return medOrderId;
	}
	public void setMedOrderId(int medOrderId) {
		this.medOrderId = medOrderId;
	}
	public int getVerificationId() {
		return verificationId;
	}
	public void setVerificationId(int verificationId) {
		this.verificationId = verificationId;
	}
	public int getDeleteFlag() {
		return deleteFlag;
	}
	public void setDeleteFlag(int deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	public int getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedByName() {
		return createdByName;
	}
	public void setCreatedByName(String createdByName) {
		this.createdByName = createdByName;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public String getStrCreatedOn() {
		return strCreatedOn;
	}
	public void setStrCreatedOn(String strCreatedOn) {
		this.strCreatedOn = strCreatedOn;
	}
	public int getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(int modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public String getModifiedByName() {
		return modifiedByName;
	}
	public void setModifiedByName(String modifiedByName) {
		this.modifiedByName = modifiedByName;
	}
	public Date getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	public String getStrModifiedOn() {
		return strModifiedOn;
	}
	public void setStrModifiedOn(String strModifiedOn) {
		this.strModifiedOn = strModifiedOn;
	}
	@Override
	public PharmacyOrderScheduleUpdated mapRow(ResultSet rs, int arg1) throws SQLException {
		PharmacyOrderScheduleUpdated pharmacyOrderScheduleUpdated = new PharmacyOrderScheduleUpdated();
		pharmacyOrderScheduleUpdated.setOrderId(rs.getInt("orderId"));
		pharmacyOrderScheduleUpdated.setMedOrderId(rs.getInt("medOrderId"));
		pharmacyOrderScheduleUpdated.setVerificationId(rs.getInt("verificationId"));
		pharmacyOrderScheduleUpdated.setScheduleTime(rs.getString("scheduleTime"));
		pharmacyOrderScheduleUpdated.setDeleteFlag(rs.getInt("orderId"));
		pharmacyOrderScheduleUpdated.setCreatedBy(rs.getInt("createdBy"));
		pharmacyOrderScheduleUpdated.setCreatedOn(rs.getDate("createdOn"));
		
		return pharmacyOrderScheduleUpdated;
	}
	
}
