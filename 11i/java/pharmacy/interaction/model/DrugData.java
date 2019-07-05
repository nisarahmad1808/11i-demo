package inpatientWeb.pharmacy.interaction.model;

import java.util.List;

public class DrugData {
	
	private String idType;
	private String id;
	private String strength;
	private String strengthUOM;
	private String formulation;
	private int routeId;
	private String drugName;
	private String customDrugName;
	private String routedGenericId;
	private boolean screen;
	private List<String> interactions;
	private String routedDrugId;
	
	public String getIdType() {
		return idType;
	}
	public void setIdType(String idType) {
		this.idType = idType;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public int getRouteId() {
		return routeId;
	}
	public void setRouteId(int routeId) {
		this.routeId = routeId;
	}
	public String getDrugName() {
		return drugName;
	}
	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	public String getRoutedGenericId() {
		return routedGenericId;
	}
	public void setRoutedGenericId(String routedGenericId) {
		this.routedGenericId = routedGenericId;
	}
	public boolean isScreen() {
		return screen;
	}
	public void setScreen(boolean screen) {
		this.screen = screen;
	}
	public List<String> getInteractions() {
		return interactions;
	}
	public void setInteractions(List<String> interactions) {
		this.interactions = interactions;
	}
	public String getRoutedDrugId() {
		return routedDrugId;
	}
	public void setRoutedDrugId(String routedDrugId) {
		this.routedDrugId = routedDrugId;
	}
	public String getCustomDrugName() {
		return customDrugName;
	}
	public void setCustomDrugName(String customDrugName) {
		this.customDrugName = customDrugName;
	}
	
	@Override
	public String toString() {
		return "DrugData [idType=" + idType + ", id=" + id + ", strength=" + strength + ", strengthUOM=" + strengthUOM
				+ ", formulation=" + formulation + ", routeId=" + routeId + ", drugName=" + drugName
				+ ", customDrugName=" + customDrugName + ", routedGenericId=" + routedGenericId + ", screen=" + screen
				+ ", interactions=" + interactions + ", routedDrugId=" + routedDrugId + "]";
	}
}
