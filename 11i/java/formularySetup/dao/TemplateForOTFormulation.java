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

@XmlType(propOrder = {"orderTypeFormulationid","orderTypeFormulationCode","orderTypeFormulationDescr"})
@JsonPropertyOrder({"orderTypeFormulationid","orderTypeFormulationCode","orderTypeFormulationDescr"})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class TemplateForOTFormulation{  
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	@XmlElement(name="orderTypeFormulationid")
	@JsonProperty("orderTypeFormulationid")
    private String orderTypeFormulationid="0";
	
	@XmlElement(name="orderTypeFormulationCode")
	@JsonProperty("orderTypeFormulationCode")
    private String orderTypeFormulationCode="";
	
	@XmlElement(name="orderTypeFormulationDescr")
	@JsonProperty("orderTypeFormulationDescr")
    private String orderTypeFormulationDescr="";
	
	public String getOrderTypeFormulationid() {
		return orderTypeFormulationid;
	}

	public void setOrderTypeFormulationid(String orderTypeFormulationid) {
		this.orderTypeFormulationid = orderTypeFormulationid;
	}

	public String getOrderTypeFormulationCode() {
		return orderTypeFormulationCode;
	}

	public void setOrderTypeFormulationCode(String orderTypeFormulationCode) {
		this.orderTypeFormulationCode = orderTypeFormulationCode;
	}

	public String getOrderTypeFormulationDescr() {
		return orderTypeFormulationDescr;
	}

	public void setOrderTypeFormulationDescr(String orderTypeFormulationDescr) {
		this.orderTypeFormulationDescr = orderTypeFormulationDescr;
	}

	@Override
	public String toString() { 
		return "TemplateForOTFormulation [orderTypeFormulationid="+orderTypeFormulationid+",orderTypeFormulationCode="+orderTypeFormulationCode+",orderTypeFormulationDescr="+orderTypeFormulationDescr+"]";
	}
    
}
