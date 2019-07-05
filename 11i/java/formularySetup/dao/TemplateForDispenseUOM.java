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
@XmlType(propOrder = { "id", "dispenseuom" })
// --------json property order---------------
@JsonPropertyOrder({ "id", "dispenseuom" })

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement

public class TemplateForDispenseUOM {
	@JsonIgnoreProperties(ignoreUnknown = true)

	// --------class level properties---------------
	@XmlElement(name = "id")
	@JsonProperty("id")
	private String id = "";

	@XmlElement(name = "dispenseuom")
	@JsonProperty("dispenseuom")
	private String dispenseuom = "";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDispenseuom() {
		return dispenseuom;
	}

	public void setDispenseuom(String dispenseuom) {
		this.dispenseuom = dispenseuom;
	}

	// --------toString()---------------
	@Override
	public String toString() {
		return new StringBuilder("TemplateForDispenseUOM [id=").append(id)
				.append(",dispenseuom=").append(dispenseuom)
				.append("]").toString();
	}

}