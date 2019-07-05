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
@XmlType(propOrder = { "hcpcscoderange", "hcpcscodetype", "hcpcscodeunit" })
// --------json property order---------------
@JsonPropertyOrder({ "hcpcscoderange", "hcpcscodetype", "hcpcscodeunit" })
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class TemplateForBillingInfo {
	@JsonIgnoreProperties(ignoreUnknown = true)
	// --------class level properties---------------
	@XmlElement(name = "hcpcscoderange")
	@JsonProperty("hcpcscoderange")
	private String hcpcscoderange = "";

	@XmlElement(name = "hcpcscodetype")
	@JsonProperty("hcpcscodetype")
	private String hcpcscodetype = "";

	@XmlElement(name = "hcpcscodeunit")
	@JsonProperty("hcpcscodeunit")
	private int hcpcscodeunit = 0;

	public String getHcpcscoderange() {
		return hcpcscoderange;
	}

	public void setHcpcscoderange(String hcpcscoderange) {
		this.hcpcscoderange = hcpcscoderange;
	}

	public String getHcpcscodetype() {
		return hcpcscodetype;
	}

	public void setHcpcscodetype(String hcpcscodetype) {
		this.hcpcscodetype = hcpcscodetype;
	}

	public int getHcpcscodeunit() {
		return hcpcscodeunit;
	}

	public void setHcpcscodeunit(int hcpcscodeunit) {
		this.hcpcscodeunit = hcpcscodeunit;
	}

	// --------toString()---------------
	@Override
	public String toString() {
		return new StringBuilder("TemplateForBillingInfo [hcpcscoderange=").append(hcpcscoderange)
				.append(",hcpcscodetype=").append(hcpcscodetype)
				.append(",hcpcscodeunit=").append(hcpcscodeunit)
				.append("]").toString();
	}

}