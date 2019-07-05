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

@XmlType(propOrder = {"id","formularyId","orderentry","emar","pharmacy","internal","instructions","createdby","createdon","modifiedby","modifiedon","delflag","userId","notes"})

@JsonPropertyOrder({"id","formularyId","orderentry","emar","pharmacy","internal","instructions","createdby","createdon","modifiedby","modifiedon","delflag","userId","notes"})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class TemplateForNotes {
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	@XmlElement(name="id")
	@JsonProperty("id")
    private String id="-1";
	
	@XmlElement(name="formularyId")
	@JsonProperty("formularyId")
    private String formularyId="-1";
	
	@XmlElement(name="orderentry")
	@JsonProperty("orderentry")
    private String orderentry="-1";
	
	@XmlElement(name="emar")
	@JsonProperty("emar")
    private String emar="-1";
	
	@XmlElement(name="pharmacy")
	@JsonProperty("pharmacy")
    private String pharmacy="-1";
	
	@XmlElement(name="internal")
	@JsonProperty("internal")
    private String internal="-1";
	
	@XmlElement(name="instructions")
	@JsonProperty("instructions")
    private String instructions="-1";
	
	@XmlElement(name="createdby")
	@JsonProperty("createdby")
	private String createdby="";
	
	@XmlElement(name="createdon")
	@JsonProperty("createdon")
	private String createdon="";
	
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
	
	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getFormularyId() {
		return formularyId;
	}



	public void setFormularyId(String formularyId) {
		this.formularyId = formularyId;
	}



	public String getOrderentry() {
		return orderentry;
	}



	public void setOrderentry(String orderentry) {
		this.orderentry = orderentry;
	}



	public String getEmar() {
		return emar;
	}



	public void setEmar(String emar) {
		this.emar = emar;
	}



	public String getPharmacy() {
		return pharmacy;
	}



	public void setPharmacy(String pharmacy) {
		this.pharmacy = pharmacy;
	}



	public String getInternal() {
		return internal;
	}



	public void setInternal(String internal) {
		this.internal = internal;
	}



	public String getInstructions() {
		return instructions;
	}



	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}



	public String getCreatedby() {
		return createdby;
	}



	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}



	public String getCreatedon() {
		return createdon;
	}



	public void setCreatedon(String createdon) {
		this.createdon = createdon;
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



	@XmlElement(name="notes")
	@JsonProperty("notes")
	private String notes="";

	
	
	@Override
	public String toString() { 
		return "TemplateForNotes [id="+id+",formularyId="+formularyId+",orderentry="+orderentry+",emar="+emar+",pharmacy="+pharmacy+",internal="+internal
				+",instructions="+instructions+",createdby="+createdby+",createdon="+createdon+",modifiedby="+modifiedby+",modifiedon="+modifiedon+",delflag="+delflag+",userId="+userId+",notes="+notes+"]";
	}
    
}
