package inpatientWeb.pharmacy.interfaces.outbound.dto;

public class DispensingInterfaceLogModel {
	
	private int id;
	private String meOrderType;
	private String status;
	private String provider;
	private String patientName;
	private String location;
	private String approvedBy;
	private String approvedDate;
	private String facility;
	private String drugDesc;
	private int patientId;
	private String data;
	private String hl7Message;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getMeOrderType() {
		return meOrderType;
	}

	public void setMeOrderType(String meOrderType) {
		this.meOrderType = meOrderType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	public String getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(String approvedDate) {
		this.approvedDate = approvedDate;
	}

	public String getFacility() {
		return facility;
	}

	public void setFacility(String facility) {
		this.facility = facility;
	}

	public String getDrugDesc() {
		return drugDesc;
	}

	public void setDrugDesc(String drugDesc) {
		this.drugDesc = drugDesc;
	}

	public int getPatientId() {
		return patientId;
	}

	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getHl7Message() {
		return hl7Message;
	}

	public void setHl7Message(String hl7Message) {
		this.hl7Message = hl7Message;
	}
}
