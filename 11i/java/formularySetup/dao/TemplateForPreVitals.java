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

@XmlType(propOrder = {"id","formularyid","leftsideid","rightsideid","itemname","createdby","createdon","modifiedby","modifiedon","delflag","userId","notes","routedgenericitemid"})
@JsonPropertyOrder({"id","formularyid","leftsideid","rightsideid","itemname","createdby","createdon","modifiedby","modifiedon","delflag","userId","notes","routedgenericitemid"})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class TemplateForPreVitals {  
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	@XmlElement(name="id")
	@JsonProperty("id")
    private String id="-1";
	
	@XmlElement(name="formularyid")
	@JsonProperty("formularyid")
    private String formularyid="";
	
	@XmlElement(name="leftsideid")
	@JsonProperty("leftsideid")
    private String leftsideid="";
	
	@XmlElement(name="rightsideid")
	@JsonProperty("rightsideid")
    private String rightsideid="";
	
	@XmlElement(name="createdon")
	@JsonProperty("createdon")
	private String createdon="";
	
	@XmlElement(name="createdby")
	@JsonProperty("createdby")
	private String createdby="";
	
	@XmlElement(name="modifiedby")
	@JsonProperty("modifiedby")
	private String modifiedby="";
	
	@XmlElement(name="modifiedon")
	@JsonProperty("modifiedon")
	private String modifiedon="";
	
	@XmlElement(name="delflag")
	@JsonProperty("delflag")
	private String delflag="";
	
	@XmlElement(name="userId")
	@JsonProperty("userId")
	private String userId="";
	
	@XmlElement(name="notes")
	@JsonProperty("notes")
	private String notes="";
	
	@XmlElement(name="itemname")
	@JsonProperty("itemname")
	private String itemname="";
	
	@XmlElement(name="routedgenericitemid")
	@JsonProperty("routedgenericitemid")
	private String routedgenericitemid="";
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFormularyid() {
		return formularyid;
	}
	public void setFormularyid(String formularyid) {
		this.formularyid = formularyid;
	}
	
	public String getLeftsideid() {
		return leftsideid;
	}
	public void setLeftsideid(String leftsideid) {
		this.leftsideid = leftsideid;
	}
	public String getRightsideid() {
		return rightsideid;
	}
	public void setRightsideid(String rightsideid) {
		this.rightsideid = rightsideid;
	}
	public String getCreatedon() {
		return createdon;
	}
	public void setCreatedon(String createdon) {
		this.createdon = createdon;
	}
	public String getCreatedby() {
		return createdby;
	}

	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}

	public String getModifiedby() {
		return modifiedby;
	}

	public void setModifiedby(String modifiedby) {
		this.modifiedby = modifiedby;
	}

	public String getModifiedon() {
		return modifiedon;
	}

	public void setModifiedon(String modifiedon) {
		this.modifiedon = modifiedon;
	}

	public String getDelflag() {
		return delflag;
	}

	public void setDelflag(String delflag) {
		this.delflag = delflag;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getItemname() {
		return itemname;
	}
	public void setItemname(String itemname) {
		this.itemname = itemname;
	}
	
	public String getRoutedgenericitemid() {
		return routedgenericitemid;
	}
	public void setRoutedgenericitemid(String routedgenericitemid) {
		this.routedgenericitemid = routedgenericitemid;
	}
	@Override
	public String toString() { 
		return "TemplateForPreVitals [id="+id+",formularyid="+formularyid+",leftsideid="+leftsideid+",rightsideid="+rightsideid+",itemname="+itemname+",createdby="+createdby
				+",createdon="+createdon+",modifiedby="+modifiedby+",modifiedon="+modifiedon+",delflag="+delflag+",userId="+userId+",notes="+notes+",routedgenericitemid="+routedgenericitemid+"]";
	}
    
}
