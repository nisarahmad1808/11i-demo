package inpatientWeb.pharmacy.beans;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class PharmacyVerification implements RowMapper<PharmacyVerification>{

	private int id;
	private int orderId;
	private int signatureId;
	private int status;
	private int createdBy;
	private int createdByName;
	private String createdOn;
	private int modifiedBy;
	private int modifiedByName;
	private String modifiedOn;
	private int verifiedBy;
	private int verifiedByName;
	private String verifiedOn;
	private int assignedTo;
	private int assignedBy;
	private int assignedByName;
	private String assignedOn;
	private int reasonId;
	private int deleteFlag;
	private boolean generateBarcode;
	private boolean dualVerification;
	private String notes;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public int getSignatureId() {
		return signatureId;
	}
	public void setSignatureId(int signatureId) {
		this.signatureId = signatureId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}
	public int getCreatedByName() {
		return createdByName;
	}
	public void setCreatedByName(int createdByName) {
		this.createdByName = createdByName;
	}
	public String getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	public int getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(int modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public int getModifiedByName() {
		return modifiedByName;
	}
	public void setModifiedByName(int modifiedByName) {
		this.modifiedByName = modifiedByName;
	}
	public String getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	public int getVerifiedBy() {
		return verifiedBy;
	}
	public void setVerifiedBy(int verifiedBy) {
		this.verifiedBy = verifiedBy;
	}
	public int getVerifiedByName() {
		return verifiedByName;
	}
	public void setVerifiedByName(int verifiedByName) {
		this.verifiedByName = verifiedByName;
	}
	public String getVerifiedOn() {
		return verifiedOn;
	}
	public void setVerifiedOn(String verifiedOn) {
		this.verifiedOn = verifiedOn;
	}
	public int getAssignedTo() {
		return assignedTo;
	}
	public void setAssignedTo(int assignedTo) {
		this.assignedTo = assignedTo;
	}
	public int getAssignedBy() {
		return assignedBy;
	}
	public void setAssignedBy(int assignedBy) {
		this.assignedBy = assignedBy;
	}
	public int getAssignedByName() {
		return assignedByName;
	}
	public void setAssignedByName(int assignedByName) {
		this.assignedByName = assignedByName;
	}
	public String getAssignedOn() {
		return assignedOn;
	}
	public void setAssignedOn(String assignedOn) {
		this.assignedOn = assignedOn;
	}
	public int getReasonId() {
		return reasonId;
	}
	public void setReasonId(int reasonId) {
		this.reasonId = reasonId;
	}
	public int getDeleteFlag() {
		return deleteFlag;
	}
	public void setDeleteFlag(int deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	public boolean isGenerateBarcode() {
		return generateBarcode;
	}
	public void setGenerateBarcode(boolean generateBarcode) {
		this.generateBarcode = generateBarcode;
	}
	public boolean isDualVerification() {
		return dualVerification;
	}
	public void setDualVerification(boolean dualVerification) {
		this.dualVerification = dualVerification;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	@Override
	public PharmacyVerification mapRow(ResultSet rs, int arg1) throws SQLException {
		PharmacyVerification pharmacyVerification = new PharmacyVerification();
		pharmacyVerification.setId(rs.getInt("id"));
		pharmacyVerification.setOrderId(rs.getInt("orderid"));
		pharmacyVerification.setSignatureId(rs.getInt("signatureid"));
		pharmacyVerification.setStatus(rs.getInt("status"));
		pharmacyVerification.setCreatedBy(rs.getInt("createdBy"));
		pharmacyVerification.setCreatedOn(rs.getString("createdon"));
		pharmacyVerification.setModifiedBy(rs.getInt("modifiedBy"));
		pharmacyVerification.setModifiedOn(rs.getString("modifiedon"));
		pharmacyVerification.setVerifiedBy(rs.getInt("verifiedby"));
		pharmacyVerification.setVerifiedOn(rs.getString("verifiedon"));
		pharmacyVerification.setAssignedTo(rs.getInt("assignedto"));
		pharmacyVerification.setAssignedBy(rs.getInt("assignedby"));
		pharmacyVerification.setAssignedOn(rs.getString("assignedon"));
		pharmacyVerification.setDeleteFlag(rs.getInt("deleteflag"));
		pharmacyVerification.setNotes(rs.getString("notes"));
		pharmacyVerification.setReasonId(rs.getInt("reasonid"));
		pharmacyVerification.setGenerateBarcode(rs.getInt("generateBarcode") == 1);
		pharmacyVerification.setDualVerification(rs.getInt("dualVerification") == 1);
		return pharmacyVerification;
	}

}
