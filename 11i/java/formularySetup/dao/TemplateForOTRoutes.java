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

@XmlType(propOrder = {"extMappingId","orderTypeRouteid","orderTypeRouteCode","orderTypeRouteDescr"})
@JsonPropertyOrder({"extMappingId","orderTypeRouteid","orderTypeRouteCode","orderTypeRouteDescr"})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class TemplateForOTRoutes{  
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	@XmlElement(name="orderTypeRouteid")
	@JsonProperty("orderTypeRouteid")
    private String orderTypeRouteid="0";
	
	@XmlElement(name="extMappingId")
	@JsonProperty("extMappingId")
    private String extMappingId="0";
	
	@XmlElement(name="orderTypeRouteCode")
	@JsonProperty("orderTypeRouteCode")
    private String orderTypeRouteCode="";
	
	@XmlElement(name="orderTypeRouteDescr")
	@JsonProperty("orderTypeRouteDescr")
    private String orderTypeRouteDescr="";
	
	private int isDefault;
	
	public String getOrderTypeRouteid() {
		return orderTypeRouteid;
	}

	public void setOrderTypeRouteid(String orderTypeRouteid) {
		this.orderTypeRouteid = orderTypeRouteid;
	}

	public String getOrderTypeRouteCode() {
		return orderTypeRouteCode;
	}

	public void setOrderTypeRouteCode(String orderTypeRouteCode) {
		this.orderTypeRouteCode = orderTypeRouteCode;
	}

	public String getOrderTypeRouteDescr() {
		return orderTypeRouteDescr;
	}

	public void setOrderTypeRouteDescr(String orderTypeRouteDescr) {
		this.orderTypeRouteDescr = orderTypeRouteDescr;
	}

	public int getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(int isDefault) {
		this.isDefault = isDefault;
	}
	
	public String getExtMappingId() {
		return extMappingId;
	}

	public void setExtMappingId(String extMappingId) {
		this.extMappingId = extMappingId;
	}

	@Override
	public String toString() { 
		return new StringBuilder("TemplateForOTRoutes [ ")
				.append("extMappingId=").append(extMappingId)
				.append("orderTypeRouteid=").append(orderTypeRouteid)
				.append(",orderTypeRouteCode=").append(orderTypeRouteCode)
				.append(",orderTypeRouteDescr=").append(orderTypeRouteDescr)
				.append("]").toString();
	}
    
}
