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

@XmlType(propOrder = {"extMappingId","orderTypeFrequencyid","orderTypeFrequencyCode","orderTypeFrequencyDescr", "isRecommended","scheduleType"})
@JsonPropertyOrder({"extMappingId","orderTypeFrequencyid","orderTypeFrequencyCode","orderTypeFrequencyDescr", "isRecommended","scheduleType"})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class TemplateForOTFrequency{  
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	@XmlElement(name="orderTypeFrequencyid")
	@JsonProperty("orderTypeFrequencyid")
    private String orderTypeFrequencyid="0";
	
	@XmlElement(name="extMappingId")
	@JsonProperty("extMappingId")
    private String extMappingId="0";
	
	@XmlElement(name="orderTypeFrequencyCode")
	@JsonProperty("orderTypeFrequencyCode")
    private String orderTypeFrequencyCode="";
	
	@XmlElement(name="orderTypeFrequencyDescr")
	@JsonProperty("orderTypeFrequencyDescr")
    private String orderTypeFrequencyDescr="";
	
	@XmlElement(name="scheduleType")
	@JsonProperty("scheduleType")
    private String scheduleType="";
	
	@XmlElement(name="isRecommended")
	@JsonProperty("isRecommended")
    private String isRecommended="";
	
	public String getOrderTypeFrequencyid() {
		return orderTypeFrequencyid;
	}

	public void setOrderTypeFrequencyid(String orderTypeFrequencyid) {
		this.orderTypeFrequencyid = orderTypeFrequencyid;
	}

	public String getOrderTypeFrequencyCode() {
		return orderTypeFrequencyCode;
	}

	public void setOrderTypeFrequencyCode(String orderTypeFrequencyCode) {
		this.orderTypeFrequencyCode = orderTypeFrequencyCode;
	}

	public String getOrderTypeFrequencyDescr() {
		return orderTypeFrequencyDescr;
	}

	public void setOrderTypeFrequencyDescr(String orderTypeFrequencyDescr) {
		this.orderTypeFrequencyDescr = orderTypeFrequencyDescr;
	}
	public String getExtMappingId() {
		return extMappingId;
	}

	public void setExtMappingId(String extMappingId) {
		this.extMappingId = extMappingId;
	}
	
	
	public String getIsRecommended() {
		return isRecommended;
	}

	public void setIsRecommended(String isRecommended) {
		this.isRecommended = isRecommended;
	}
	
	

	public String getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}

	@Override
	public String toString() { 
		return new StringBuilder("TemplateForOTFrequency [")
				.append("extMappingId=").append(extMappingId)
				.append("orderTypeFrequencyid=").append(orderTypeFrequencyid)
				.append(",orderTypeFrequencyCode=").append(orderTypeFrequencyCode)
				.append(",orderTypeFrequencyDescr=").append(orderTypeFrequencyDescr)
				.append(",isRecommended=").append(isRecommended)
				.append(",scheduleType=").append(scheduleType)
				.append("]").toString();
	}
    
}
