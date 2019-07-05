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
@XmlType(propOrder = { "itemId", "itemName", "routedDrugId" })
//--------json property order---------------
@JsonPropertyOrder({ "itemId", "itemName", "routedDrugId" })

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement

public class TemplateForItems {
	@JsonIgnoreProperties(ignoreUnknown = true)

//--------class level properties---------------
	@XmlElement(name = "itemId")
	@JsonProperty("itemId")
	private int itemId = 0;

	@XmlElement(name = "itemName")
	@JsonProperty("itemName")
	private String itemName = "";

	@XmlElement(name = "routedDrugId")
	@JsonProperty("routedDrugId")
	private int routedDrugId = 0;

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public int getRoutedDrugId() {
		return routedDrugId;
	}

	public void setRoutedDrugId(int routedDrugId) {
		this.routedDrugId = routedDrugId;
	}

//--------toString()---------------
	@Override
	public String toString() {
		return new StringBuilder("TemplateForItems [itemId=").append(itemId).append(",itemName=").append(itemName)
				.append(",routedDrugId=").append(routedDrugId).append("]").toString();
	}

}