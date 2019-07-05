package inpatientWeb.admin.pharmacySettings.formularySetup.dao;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@XmlType(propOrder = {"id","serviceType","name","serviceTypeId"})
@JsonPropertyOrder({"id","serviceType","name","serviceTypeId"})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class TemplateForServiceTypes {
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	@XmlElement(name="id")
	@JsonProperty("id")
    private String id="-1";
	
	@XmlElementWrapper(name="serviceType")
	@XmlElement(name="serviceType")
	@JsonProperty("serviceType")
    private String serviceType="";
	
	@XmlElement(name="name")
	@JsonProperty("name")
    private String name="";
	
	@XmlElement(name="serviceTypeId")
	@JsonProperty("serviceTypeId")
    private String serviceTypeId="";
	
	

	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getServiceType() {
		return serviceType;
	}



	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getServiceTypeId() {
		return serviceTypeId;
	}



	public void setServiceTypeId(String serviceTypeId) {
		this.serviceTypeId = serviceTypeId;
	}



	public String toString() { 
		return new StringBuilder("TemplateForDrug [")
				.append("id=").append(id)
				.append(",serviceType=").append(serviceType)
				.append(",name=").append(name)
				.append(",serviceTypeId=").append(serviceTypeId)
				.append("]").toString();
	}
    
}
