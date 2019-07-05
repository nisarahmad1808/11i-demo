package inpatientWeb.pharmacy.beans;

import java.util.Date;

public class PharmacyOrderScheduleOrg {
	
	private int id;
	private int scheduleId;
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
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getScheduleId() {
		return scheduleId;
	}
	public void setScheduleId(int scheduleId) {
		this.scheduleId = scheduleId;
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

}
