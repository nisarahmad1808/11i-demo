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
@XmlType(propOrder = { "ahfsClassID", "ahfsClassName","id" })
// --------json property order---------------
@JsonPropertyOrder({ "ahfsClassID", "ahfsClassName","id" })

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement

public class TemplateForAhfs {
	@JsonIgnoreProperties(ignoreUnknown = true)

	// --------class level properties---------------
	@XmlElement(name = "ahfsClassID")
	@JsonProperty("ahfsClassID")
	private String ahfsClassID = "";

	@XmlElement(name = "ahfsClassName")
	@JsonProperty("ahfsClassName")
	private String ahfsClassName = "";
	
	@XmlElement(name = "id")
	@JsonProperty("id")
	private String id = "";

	public String getAhfsClassID() {
		return ahfsClassID;
	}

	public void setAhfsClassID(String ahfsClassID) {
		this.ahfsClassID = ahfsClassID;
	}

	public String getAhfsClassName() {
		return ahfsClassName;
	}

	public void setAhfsClassName(String ahfsClassName) {
		this.ahfsClassName = ahfsClassName;
	}
	
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	// --------toString()---------------
	@Override
	public String toString() {
		return new StringBuilder("TemplateForAhfs [ahfsClassID=")
				.append(ahfsClassID)
				.append(",ahfsClassName=")
				.append(ahfsClassName)
				.append(",id=")
				.append(id)
				.append("]").toString();
	}

}