package inpatientWeb.pharmacy.interaction.model;

public class DrugOrderDetails{

	String gpi;
	String strength;
	String strengthUOM;
	String formulation;
	Integer routeId;
	String drugName;
	String routedDrugId;
	String routedGenricId;
	Integer drugToFood;
	Integer drugToAlcohol;
	Integer duplicateTherapy;
	Integer ptOrderId;
	String orderDuration;
	String orderDurationType;
	String orderDose;
	String orderDoseUOM;
	String frequencyCode;
	Integer orderType;
	Integer homeMed;
	Integer customMed;
	String customDrugName;
	int ivDiluent;
	double ivRate = 0;
	String ivRateUOM;
	double totalVolume;
	double detailedDose;
	
	public String getGpi() {
		return gpi;
	}
	public void setGpi(String gpi) {
		this.gpi = gpi;
	}
	public String getStrength() {
		return strength;
	}
	public void setStrength(String strength) {
		this.strength = strength;
	}
	public String getStrengthUOM() {
		return strengthUOM;
	}
	public void setStrengthUOM(String strengthUOM) {
		this.strengthUOM = strengthUOM;
	}
	public String getFormulation() {
		return formulation;
	}
	public void setFormulation(String formulation) {
		this.formulation = formulation;
	}
	public Integer getRouteId() {
		return routeId;
	}
	public void setRouteId(Integer routeId) {
		this.routeId = routeId;
	}
	public String getDrugName() {
		return drugName;
	}
	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}
	public String getRoutedGenricId() {
		return routedGenricId;
	}
	public void setRoutedGenricId(String routedGenricId) {
		this.routedGenricId = routedGenricId;
	}
	public Integer getDrugToFood() {
		return drugToFood;
	}
	public void setDrugToFood(Integer drugToFood) {
		this.drugToFood = drugToFood;
	}
	public Integer getDrugToAlcohol() {
		return drugToAlcohol;
	}
	public void setDrugToAlcohol(Integer drugToAlcohol) {
		this.drugToAlcohol = drugToAlcohol;
	}
	public Integer getDuplicateTherapy() {
		return duplicateTherapy;
	}
	public void setDuplicateTherapy(Integer duplicateTherapy) {
		this.duplicateTherapy = duplicateTherapy;
	}
	public Integer getPtOrderId() {
		return ptOrderId;
	}
	public void setPtOrderId(Integer ptOrderId) {
		this.ptOrderId = ptOrderId;
	}
	public String getOrderDuration() {
		return orderDuration;
	}
	public void setOrderDuration(String orderDuration) {
		this.orderDuration = orderDuration;
	}
	public String getOrderDurationType() {
		return orderDurationType;
	}
	public void setOrderDurationType(String orderDurationType) {
		this.orderDurationType = orderDurationType;
	}
	public String getOrderDose() {
		return orderDose;
	}
	public void setOrderDose(String orderDose) {
		this.orderDose = orderDose;
	}
	public String getOrderDoseUOM() {
		return orderDoseUOM;
	}
	public void setOrderDoseUOM(String orderDoseUOM) {
		this.orderDoseUOM = orderDoseUOM;
	}
	public String getFrequencyCode() {
		return frequencyCode;
	}
	public void setFrequencyCode(String frequencyCode) {
		this.frequencyCode = frequencyCode;
	}
	public Integer getOrderType() {
		return orderType;
	}
	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}
	public Integer getHomeMed() {
		return homeMed;
	}
	public void setHomeMed(Integer homeMed) {
		this.homeMed = homeMed;
	}
	public String getRoutedDrugId() {
		return routedDrugId;
	}
	public void setRoutedDrugId(String routedDrugId) {
		this.routedDrugId = routedDrugId;
	}
	public Integer getCustomMed() {
		return customMed;
	}
	public void setCustomMed(Integer customMed) {
		this.customMed = customMed;
	}
	public String getCustomDrugName() {
		return customDrugName;
	}
	public void setCustomDrugName(String customDrugName) {
		this.customDrugName = customDrugName;
	}
	public int getIvDiluent() {
		return ivDiluent;
	}
	public void setIvDiluent(int ivDiluent) {
		this.ivDiluent = ivDiluent;
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
		return "DrugOrderDetails [gpi=" + gpi + ", strength=" + strength + ", strengthUOM=" + strengthUOM
				+ ", formulation=" + formulation + ", routeId=" + routeId + ", drugName=" + drugName + ", routedDrugId="
				+ routedDrugId + ", routedGenricId=" + routedGenricId + ", drugToFood=" + drugToFood
				+ ", drugToAlcohol=" + drugToAlcohol + ", duplicateTherapy=" + duplicateTherapy + ", ptOrderId="
				+ ptOrderId + ", orderDuration=" + orderDuration + ", orderDurationType=" + orderDurationType
				+ ", orderDose=" + orderDose + ", orderDoseUOM=" + orderDoseUOM + ", frequencyCode=" + frequencyCode
				+ ", orderType=" + orderType + ", homeMed=" + homeMed + ", customMed=" + customMed + ", customDrugName="
				+ customDrugName + ", ivDiluent=" + ivDiluent + ", ivRate=" + ivRate + ", ivRateUOM=" + ivRateUOM
				+ ", totalVolume=" + totalVolume + ", detailedDose=" + detailedDose + "]";
	}
}
