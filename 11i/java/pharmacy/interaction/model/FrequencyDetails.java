package inpatientWeb.pharmacy.interaction.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import inpatientWeb.pharmacy.drugdb.constant.DrugDbConstant.RESULT_CODES;

/**
 * The Class FrequencyDetails.
 * @author Dishagna Bhavsar
 */

public class FrequencyDetails {

	private String frequency;
	private String frequnecyUOM;
	private int frequnecyDoses;
	private String frequencyCode;
	private int resultCode;
	private String message;
	private int frequencyType;
	
	
	public FrequencyDetails(String frequencyCode) {
		this.frequencyCode = frequencyCode;
		this.resultCode = RESULT_CODES.SUCCESS.getResultCode();
	}
	
	@JsonIgnore
	public String getFrequencyCode() {
		return frequencyCode;
	}

	public void setFrequencyCode(String frequencyCode) {
		this.frequencyCode = frequencyCode;
	}

	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	public String getFrequnecyUOM() {
		return frequnecyUOM;
	}
	public void setFrequnecyUOM(String frequnecyUOM) {
		this.frequnecyUOM = frequnecyUOM;
	}
	public int getFrequnecyDoses() {
		return frequnecyDoses;
	}
	public void setFrequnecyDoses(int frequnecyDoses) {
		this.frequnecyDoses = frequnecyDoses;
	}
	@JsonIgnore
	public int getResultCode() {
		return resultCode;
	}
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	@JsonIgnore
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getFrequencyType() {
		return frequencyType;
	}
	public void setFrequencyType(int frequencyType) {
		this.frequencyType = frequencyType;
	}

	@Override
	public String toString() {
		return "FrequencyDetails [frequency=" + frequency + ", frequnecyUOM=" + frequnecyUOM + ", frequnecyDoses="
				+ frequnecyDoses + ", frequencyCode=" + frequencyCode + ", resultCode=" + resultCode + ", message="
				+ message + ", frequencyType=" + frequencyType + "]";
	}
}
