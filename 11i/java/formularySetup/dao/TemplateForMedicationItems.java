package inpatientWeb.admin.pharmacySettings.formularySetup.dao;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

//--------xml type---------------
@XmlType(propOrder = {"isPrimary","csa_schedule","itemId","itemName","drugNameID","genericDrugNameID","routedDrugID","routeID","routedGenericItemId"})
//--------json property order---------------
@JsonPropertyOrder({"isPrimary","csa_schedule","itemId","itemName","drugNameID","genericDrugNameID","routedDrugID","routeID","routedGenericItemId"})

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement

public class TemplateForMedicationItems {
	@JsonIgnoreProperties(ignoreUnknown = true)

//--------class level properties---------------
	@XmlElement(name = "isPrimary")
	@JsonProperty("isPrimary")
	private String isPrimary = "";
	
	@XmlElement(name = "csa_schedule")
	@JsonProperty("csa_schedule")
	private String csa_schedule = "";

	
	@XmlElement(name = "itemId")
	@JsonProperty("itemId")
	private String itemId = "";
	
	@XmlElement(name = "itemName")
	@JsonProperty("itemName")
	private String itemName = "";
	
	@XmlElement(name = "drugNameID")
	@JsonProperty("drugNameID")
	private String drugNameID = "";
	
	@XmlElement(name = "genericDrugNameID")
	@JsonProperty("genericDrugNameID")
	private String genericDrugNameID = "";
	
	@XmlElement(name = "routedDrugID")
	@JsonProperty("routedDrugID")
	private String routedDrugID = "";
	
	@XmlElement(name = "routeID")
	@JsonProperty("routeID")
	private String routeID = "";
	
	@XmlElement(name = "routedGenericItemId")
	@JsonProperty("routedGenericItemId")
	private String routedGenericItemId = "";
	

	public String getIsPrimary() {
		return isPrimary;
	}

	public void setIsPrimary(String isPrimary) {
		this.isPrimary = isPrimary;
	}

	public String getCsa_schedule() {
		return csa_schedule;
	}

	public void setCsa_schedule(String csa_schedule) {
		this.csa_schedule = csa_schedule;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getDrugNameID() {
		return drugNameID;
	}

	public void setDrugNameID(String drugNameID) {
		this.drugNameID = drugNameID;
	}

	public String getGenericDrugNameID() {
		return genericDrugNameID;
	}

	public void setGenericDrugNameID(String genericDrugNameID) {
		this.genericDrugNameID = genericDrugNameID;
	}

	public String getRoutedDrugID() {
		return routedDrugID;
	}

	public void setRoutedDrugID(String routedDrugID) {
		this.routedDrugID = routedDrugID;
	}

	public String getRouteID() {
		return routeID;
	}

	public void setRouteID(String routeID) {
		this.routeID = routeID;
	}

	public String getRoutedGenericItemId() {
		return routedGenericItemId;
	}

	public void setRoutedGenericItemId(String routedGenericItemId) {
		this.routedGenericItemId = routedGenericItemId;
	}
	//--------toString()---------------
	@Override
	public String toString() {
		return new StringBuilder("TemplateForMedicationItems [itemId=").append(itemId)
				.append(",isPrimary=").append(isPrimary)
				.append(",csa_schedule=").append(csa_schedule)
				.append(",itemId=").append(itemId)
				.append(",itemName=").append(itemName)
				.append(",drugNameID=").append(drugNameID)
				.append(",genericDrugNameID=").append(genericDrugNameID)
				.append(",routedDrugID=").append(routedDrugID)
				.append(",routeID=").append(routeID)
				.append(",routedGenericItemId=").append(routedGenericItemId)
				.append("]").toString();
	}

}