package inpatientWeb.pharmacy.interfaces.outbound.dto;

public class DispensingRxFreqDetailsModel {
	private String  rDispenseOrderFrequencyCode = "";
	// **
	private String  rDispenseOrderFrequencyDesc = "";
	// **
	private String  rDispenseOrderFrequencyTime = "";
	
	private String oPrn;
	private String oSecondaryFrequency;
	
	public String getrDispenseOrderFrequencyCode() {
		return rDispenseOrderFrequencyCode;
	}
	public void setrDispenseOrderFrequencyCode(String rDispenseOrderFrequencyCode) {
		this.rDispenseOrderFrequencyCode = rDispenseOrderFrequencyCode;
	}
	public String getrDispenseOrderFrequencyDesc() {
		return rDispenseOrderFrequencyDesc;
	}
	public void setrDispenseOrderFrequencyDesc(String rDispenseOrderFrequencyDesc) {
		this.rDispenseOrderFrequencyDesc = rDispenseOrderFrequencyDesc;
	}
	public String getrDispenseOrderFrequencyTime() {
		return rDispenseOrderFrequencyTime;
	}
	public void setrDispenseOrderFrequencyTime(String rDispenseOrderFrequencyTime) {
		this.rDispenseOrderFrequencyTime = rDispenseOrderFrequencyTime;
	}
	public String getoPrn() {
		return oPrn;
	}
	public void setoPrn(String oPrn) {
		this.oPrn = oPrn;
	}
	public String getoSecondaryFrequency() {
		return oSecondaryFrequency;
	}
	public void setoSecondaryFrequency(String oSecondaryFrequency) {
		this.oSecondaryFrequency = oSecondaryFrequency;
	}
}
