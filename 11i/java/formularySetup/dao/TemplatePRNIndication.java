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

@XmlType(propOrder = {"id", "routedgenericitemid", "prnIndicationId"})
@JsonPropertyOrder({"id", "routedgenericitemid", "prnIndicationId"})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class TemplatePRNIndication {
	
	@XmlElement(name="id")
	@JsonProperty("id")
    private int id=-1;
	
	@XmlElement(name="routedgenericitemid")
	@JsonProperty("routedgenericitemid")
    private String routedgenericitemid="";
	
	@XmlElement(name="prnIndicationId")
	@JsonProperty("prnIndicationId")
    private String prnIndicationId="";
	
	
	
	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public String getRoutedgenericitemid() {
		return routedgenericitemid;
	}



	public void setRoutedgenericitemid(String routedgenericitemid) {
		this.routedgenericitemid = routedgenericitemid;
	}



	public String getPrnIndicationId() {
		return prnIndicationId;
	}



	public void setPrnIndicationId(String prnIndicationId) {
		this.prnIndicationId = prnIndicationId;
	}



	@Override
	public String toString() {
		return new StringBuilder("TemplatePRNIndication [id=").append(id)
				.append(", routedgenericitemid=").append(routedgenericitemid)
				.append(", prnIndicationId=").append(prnIndicationId)
				.append("]").toString();
	}
    
}
