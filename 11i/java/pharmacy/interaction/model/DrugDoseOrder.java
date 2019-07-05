package inpatientWeb.pharmacy.interaction.model;

import java.util.List;
import java.util.Map;

public class DrugDoseOrder {
	private int orderId;
	private double duration;
	private String durationUOM;
	private double dose;
	private String doseUOM;
	private boolean doseCheck;
	private List<DrugData> drugList;
	private Map<String, Object> frequencyDetails; 
	private Integer homeMed;
	private boolean customDrug;
	private String customDrugName;
	private double ivRate;
	private String ivRateUOM;
	private double totalVolume;
	private double detailedDose;
	
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public double getDuration() {
		return duration;
	}
	public void setDuration(double duration) {
		this.duration = duration;
	}
	public String getDurationUOM() {
		return durationUOM;
	}
	public void setDurationUOM(String durationUOM) {
		this.durationUOM = durationUOM;
	}
	public double getDose() {
		return dose;
	}
	public void setDose(double dose) {
		this.dose = dose;
	}
	public String getDoseUOM() {
		return doseUOM;
	}
	public void setDoseUOM(String doseUOM) {
		this.doseUOM = doseUOM;
	}
	public boolean isDoseCheck() {
		return doseCheck;
	}
	public void setDoseCheck(boolean doseCheck) {
		this.doseCheck = doseCheck;
	}
	public List<DrugData> getDrugList() {
		return drugList;
	}
	public void setDrugList(List<DrugData> drugList) {
		this.drugList = drugList;
	}
	public Map<String, Object> getFrequencyDetails() {
		return frequencyDetails;
	}
	public void setFrequencyDetails(Map<String, Object> frequencyDetails) {
		this.frequencyDetails = frequencyDetails;
	}
	public Integer getHomeMed() {
		return homeMed;
	}
	public void setHomeMed(Integer homeMed) {
		this.homeMed = homeMed;
	}
	public boolean isCustomDrug() {
		return customDrug;
	}
	public void setCustomDrug(boolean customDrug) {
		this.customDrug = customDrug;
	}
	public String getCustomDrugName() {
		return customDrugName;
	}
	public void setCustomDrugName(String customDrugName) {
		this.customDrugName = customDrugName;
	}
	public double getIvRate() {
		return ivRate;
	}
	public void setIvRate(double ivRate) {
		this.ivRate = ivRate;
	}
	public String getIvRateUOM() {
		return ivRateUOM;
	}
	public void setIvRateUOM(String ivRateUOM) {
		this.ivRateUOM = ivRateUOM;
	}
	public double getTotalVolume() {
		return totalVolume;
	}
	public void setTotalVolume(double totalVolume) {
		this.totalVolume = totalVolume;
	}
	public double getDetailedDose() {
		return detailedDose;
	}
	public void setDetailedDose(double detailedDose) {
		this.detailedDose = detailedDose;
	}

	@Override
	public String toString() {
		return "DrugDoseOrder [orderId=" + orderId + ", duration=" + duration + ", durationUOM=" + durationUOM
				+ ", dose=" + dose + ", doseUOM=" + doseUOM + ", doseCheck=" + doseCheck + ", drugList=" + drugList
				+ ", frequencyDetails=" + frequencyDetails + ", homeMed=" + homeMed + ", customDrug=" + customDrug
				+ ", customDrugName=" + customDrugName + ", ivRate=" + ivRate + ", ivRateUOM=" + ivRateUOM
				+ ", totalVolume=" + totalVolume + ", detailedDose=" + detailedDose + "]";
	}
}
