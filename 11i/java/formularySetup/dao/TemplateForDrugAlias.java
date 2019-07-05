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

@XmlType(propOrder = {"id","formularyid","code","createdby","createdon","modifiedby","modifiedon","delflag","userId","notes","routedGenericItemId"})
@JsonPropertyOrder({"id","formularyid","code","createdby","createdon","modifiedby","modifiedon","delflag","userId","notes","routedGenericItemId"})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class TemplateForDrugAlias {  
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	@XmlElement(name="id")
	@JsonProperty("id")
    private String id="-1";
	
	@XmlElement(name="formularyid")
	@JsonProperty("formularyid")
    private String formularyid="";
	
	@XmlElement(name="routedGenericItemId")
	@JsonProperty("routedGenericItemId")
    private String routedGenericItemId="";
	
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
	public String getFormularyid() {
		return formularyid;
	}
	public void setFormularyid(String formularyid) {
		this.formularyid = formularyid;
	}
	
	public String getRoutedGenericItemId() {
		return routedGenericItemId;
	}
	public void setRoutedGenericItemId(String routedGenericItemId) {
		this.routedGenericItemId = routedGenericItemId;
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
		return new StringBuilder("TemplateForDrugAlias [id=").append(id)
				.append(",formularyid=").append(formularyid).append(",code=").append(code)
				.append(",createdby=").append(createdby).append(",createdon=").append(createdon)
				.append(",modifiedby=").append(modifiedby).append(",modifiedon=").append(modifiedon)
				.append(",delflag=").append(delflag)
				.append(",userId=").append(userId)
				.append(",notes=").append(notes)
				.append(",routedGenericItemId=").append(routedGenericItemId)
				.append("]").toString();
	}
    
}
