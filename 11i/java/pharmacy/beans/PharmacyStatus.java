package inpatientWeb.pharmacy.beans;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class PharmacyStatus implements RowMapper<PharmacyStatus>{

	private int id;
	private String status;
	private String createdBy;
	private String createdOn;
	private String modifiedBy;
	private String modifiedOn;
	private int userId;
	private String notes;
	private int deleteFlag;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public String getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public int getDeleteFlag() {
		return deleteFlag;
	}
	public void setDeleteFlag(int deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	@Override
	public PharmacyStatus mapRow(ResultSet rs, int arg1) throws SQLException {
		PharmacyStatus pharmacyStatus = new PharmacyStatus();
		pharmacyStatus.setId(rs.getInt("id"));
		pharmacyStatus.setStatus(rs.getString("status"));
		pharmacyStatus.setCreatedBy(rs.getString("createdBy"));
		pharmacyStatus.setCreatedOn(rs.getString("createdOn"));
		pharmacyStatus.setModifiedBy(rs.getString("modifiedBy"));
		pharmacyStatus.setModifiedOn(rs.getString("modifiedOn"));
		pharmacyStatus.setUserId(rs.getInt("userId"));
		pharmacyStatus.setNotes(rs.getString("notes"));
		pharmacyStatus.setDeleteFlag(rs.getInt("deleteFlag"));
		return pharmacyStatus;
	}

}
