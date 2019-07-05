package inpatientWeb.admin.pharmacySettings.formularySetup.dao;


import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import inpatientWeb.admin.pharmacySettings.configureDictionary.dao.TemplatePNRIndication;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@XmlType(propOrder = {"drugDetails","drugToDrugInteraction","referenceRange","notes","clinical","reflexOrders","orgDrugDetails","prnIndicationData","savedPRNIndicationData"})

@JsonPropertyOrder({"drugDetails","drugToDrugInteraction","referenceRange","notes","clinical","reflexOrders","orgDrugDetails","prnIndicationData","savedPRNIndicationData"})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class TemplateForCommonSetting {
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	@XmlElement(name="drugDetails")
	@JsonProperty("drugDetails")
    private  List<TemplateForDrug> drugDetails = null;
	
	@XmlElement(name="orgDrugDetails")
	@JsonProperty("orgDrugDetails")
    private  List<TemplateForDrug> orgDrugDetails = null;
	
	@XmlElement(name="drugToDrugInteraction")
	@JsonProperty("drugToDrugInteraction")
    private String drugToDrugInteraction="";
	
	@XmlElement(name="referenceRange")
	@JsonProperty("referenceRange")
    private String referenceRange="";
	
	@XmlElement(name="notes")
	@JsonProperty("notes")
    private List<TemplateForNotes> notes = null;
	
	@XmlElement(name="clinical")
	@JsonProperty("clinical")
    private String clinical="";
	
	@XmlElement(name="reflexOrders")
	@JsonProperty("reflexOrders")
    private String reflexOrders="";
	
	@XmlElement(name="prnIndicationData")
	@JsonProperty("prnIndicationData")
    private List<TemplatePNRIndication> prnIndicationData = null;
	
	@XmlElement(name="savedPRNIndicationData")
	@JsonProperty("savedPRNIndicationData")
    private List<TemplatePNRIndication>  savedPRNIndicationData = null;
	
	public  List<TemplateForDrug> getDrugDetails() {
		return drugDetails;
	}

	public void setDrugDetails( List<TemplateForDrug> drugDetails) {
		this.drugDetails = drugDetails;
	}

	public String getDrugToDrugInteraction() {
		return drugToDrugInteraction;
	}

	public void setDrugToDrugInteraction(String drugToDrugInteraction) {
		this.drugToDrugInteraction = drugToDrugInteraction;
	}

	public String getReferenceRange() {
		return referenceRange;
	}


	public void setReferenceRange(String referenceRange) {
		this.referenceRange = referenceRange;
	}


	public List<TemplateForNotes> getNotes() {
		return notes;
	}

	public void setNotes(List<TemplateForNotes> notes) {
		this.notes = notes;
	}


	public String getClinical() {
		return clinical;
	}


	public void setClinical(String clinical) {
		this.clinical = clinical;
	}


	public String getReflexOrders() {
		return reflexOrders;
	}


	public void setReflexOrders(String reflexOrders) {
		this.reflexOrders = reflexOrders;
	}



	public List<TemplateForDrug> getOrgDrugDetails() {
		return orgDrugDetails;
	}

	public void setOrgDrugDetails( List<TemplateForDrug> orgDrugDetails) {
		this.orgDrugDetails = orgDrugDetails;
	}
	
	public List<TemplatePNRIndication> getPrnIndicationData() {
		return prnIndicationData;
	}

	public void setPrnIndicationData(List<TemplatePNRIndication> prnIndicationData) {
		this.prnIndicationData = prnIndicationData;
	}

	public List<TemplatePNRIndication>  getSavedPRNIndicationData() {
		return savedPRNIndicationData;
	}

	public void setSavedPRNIndicationData(List<TemplatePNRIndication>  savedPRNIndicationData) {
		this.savedPRNIndicationData = savedPRNIndicationData;
	}

	@Override
	public String toString() { 
		return new StringBuilder("TemplateForCommonSetting [ ")
				.append("drugDetails=").append(drugDetails)
				.append("orgDrugDetails=").append(orgDrugDetails)
				.append(",drugToDrugInteraction=").append(drugToDrugInteraction)
				.append(",referenceRange=").append(referenceRange)
			    .append(",notes=").append(notes)
			    .append(",clinical=").append(clinical)
			    .append(",reflexOrders=").append(reflexOrders)
			    .append(",prnIndicationData=").append(prnIndicationData)
			    .append(",savedPRNIndicationData=").append(savedPRNIndicationData)
			    .append("]").toString();
	}
    
}
