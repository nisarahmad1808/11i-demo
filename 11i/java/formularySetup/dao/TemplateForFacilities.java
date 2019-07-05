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

@XmlType(propOrder = {"id","facilitytype","name","facilityId"})
@JsonPropertyOrder({"id","facilitytype","name","facilityId"})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class TemplateForFacilities {
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	@XmlElement(name="id")
	@JsonProperty("id")
    private String id="-1";
	
	@XmlElementWrapper(name="facilitytype")
	@XmlElement(name="facilitytype")
	@JsonProperty("facilitytype")
    private String facilitytype="";
	
	@XmlElement(name="name")
	@JsonProperty("name")
    private String name="";
	
	@XmlElement(name="facilityId")
	@JsonProperty("facilityId")
    private String facilityId="";
	
	
	
	
	public String getFacilityId() {
		return facilityId;
	}


	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getFacilitytype() {
		return facilitytype;
	}


	public void setFacilitytype(String facilitytype) {
		this.facilitytype = facilitytype;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String toString() { 
		return new StringBuilder("TemplateForDrug [")
				.append("id=").append(id)
				.append(",facilitytype=").append(facilitytype)
				.append(",name=").append(name)
				.append(",facilityId=").append(facilityId)
				.append("]").toString();
	}
    
}
