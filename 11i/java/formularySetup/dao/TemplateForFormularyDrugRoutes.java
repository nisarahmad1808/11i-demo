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

@XmlType(propOrder = {"id","ordertypeid","routeid","formularyid","createdby","createdon","modifiedby","modifiedon","delflag","userId","notes"})
@JsonPropertyOrder({"id","ordertypeid","routeid","formularyid","createdby","createdon","modifiedby","modifiedon","delflag","userId","notes"})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class TemplateForFormularyDrugRoutes {  
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	@XmlElement(name="id")
	@JsonProperty("id")
    private String id="-1";
	
	@XmlElement(name="ordertypeid")
	@JsonProperty("ordertypeid")
    private String ordertypeid="";
	
	@XmlElement(name="routeid")
	@JsonProperty("routeid")
    private String routeid="";
	
	@XmlElement(name="formularyid")
	@JsonProperty("formularyid")
    private String formularyid="";
	
	@XmlElement(name="code")
	@JsonProperty("code")
    private String code="";
	
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
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getOrdertypeid() {
		return ordertypeid;
	}
	public void setOrdertypeid(String ordertypeid) {
		this.ordertypeid = ordertypeid;
	}
	public String getRouteid() {
		return routeid;
	}
	public void setRouteid(String routeid) {
		this.routeid = routeid;
	}
	public String getFormularyid() {
		return formularyid;
	}
	public void setFormularyid(String formularyid) {
		this.formularyid = formularyid;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
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


	@Override
	public String toString() { 
		return "TemplateForFormularyDrugRoutes [id="+id+",ordertypeid="+ordertypeid+",routeid="+routeid+",formularyid="+formularyid+",code="+code+",createdby="+createdby
				+",createdon="+createdon+",modifiedby="+modifiedby+",modifiedon="+modifiedon+",delflag="+delflag+",userId="+userId+",notes="+notes+"]";
	}
    
}
