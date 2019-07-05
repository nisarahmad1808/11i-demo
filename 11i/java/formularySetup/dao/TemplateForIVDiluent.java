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

//--------xml type---------------
/**
 * @author nisara
 *
 */
@XmlType(propOrder={"id","formularyid","ivformularyid","ordertypesetupid","strength","strengthuom","formulation", "gpi", "ndc", "ddid", "createdby","createdon","modifiedby","modifiedon","userId","notes","itemname","org_strength","org_strengthuom","org_formulation","org_routename","org_volume", "dispenseSize", "dispenseSizeUOM", "ivVolume", "ivVolumeUOM", "isCalculation", "itemId", "isTitrationAllowed", "ingredientscount", "ingredient1value", "ingredient1_uom", "ingredient2value", "ingredient2_uom", "ingredient3value", "ingredient3_uom", "ingredient4value", "ingredient4_uom", "ahfsClassification", "isAdditive", "isDiluent", "formularyDose", "formularyDoseUOM","dose","doseuom","drugName"})
//--------json property order---------------
@JsonPropertyOrder({"id","formularyid","ivformularyid","ordertypesetupid","strength","strengthuom","formulation", "gpi", "ndc", "ddid", "createdby","createdon","modifiedby","modifiedon","userId","notes","itemname","org_strength","org_strengthuom","org_formulation","org_routename","org_volume", "dispenseSize", "dispenseSizeUOM", "ivVolume", "ivVolumeUOM", "isCalculation", "itemId", "isTitrationAllowed", "ingredientscount", "ingredient1value", "ingredient1_uom", "ingredient2value", "ingredient2_uom", "ingredient3value", "ingredient3_uom", "ingredient4value", "ingredient4_uom", "ahfsClassification", "isAdditive", "isDiluent", "formularyDose", "formularyDoseUOM","dose","doseuom","drugName"})

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement

public class TemplateForIVDiluent{
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	
	//--------class level properties---------------
	@XmlElement(name="id")
	@JsonProperty("id")
	private int id=-1;
	
	@XmlElement(name="formularyid")
	@JsonProperty("formularyid")
	private int formularyid=-1;
	
	@XmlElement(name="ordertypesetupid")
	@JsonProperty("ordertypesetupid")
	private int ordertypesetupid=-1;
	
	@XmlElement(name="strength")
	@JsonProperty("strength")
	private String strength=null;
	
	@XmlElement(name="drugName")
	@JsonProperty("drugName")
	private String drugName=null;
	
	@XmlElement(name="strengthuom")
	@JsonProperty("strengthuom")
	private String strengthuom=null;
	
	@XmlElement(name="formulation")
	@JsonProperty("formulation")
	private String formulation=null;
	
	@XmlElement(name="gpi")
	@JsonProperty("gpi")
	private String gpi=null;
	
	@XmlElement(name="ndc")
	@JsonProperty("ndc")
	private String ndc=null;
	
	@XmlElement(name="ddid")
	@JsonProperty("ddid")
	private String ddid=null;
	
	@XmlElement(name="createdby")
	@JsonProperty("createdby")
	private String createdby=null;
	
	@XmlElement(name="createdon")
	@JsonProperty("createdon")
	private String createdon=null;
	
	@XmlElement(name="modifiedby")
	@JsonProperty("modifiedby")
	private String modifiedby=null;
	
	@XmlElement(name="modifiedon")
	@JsonProperty("modifiedon")
	private String modifiedon=null;
	
	@XmlElement(name="userId")
	@JsonProperty("userId")
	private String userId=null;
	
	@XmlElement(name="notes")
	@JsonProperty("notes")
	private String notes=null;
	
	@XmlElement(name="itemname")
	@JsonProperty("itemname")
	private String itemname=null;
	
	@XmlElement(name="org_strength")
	@JsonProperty("org_strength")
	private String org_strength=null;
	
	@XmlElement(name="org_strengthuom")
	@JsonProperty("org_strengthuom")
	private String org_strengthuom =null;
	
	@XmlElement(name="org_routeName")
	@JsonProperty("org_routeName")
	private String org_routeName =null;
	
	@XmlElement(name="org_volume")
	@JsonProperty("org_volume")
	private String org_volume =null;
	
	@XmlElement(name="org_formulation")
	@JsonProperty("org_formulation")
	private String org_formulation =null;
	
	@XmlElement(name="dispenseSize")
	@JsonProperty("dispenseSize")
    private String dispenseSize=null;
	
	@XmlElement(name="dispenseSizeUOM")
	@JsonProperty("dispenseSizeUOM")
    private String dispenseSizeUOM=null;
	
	@XmlElement(name="dose")
	@JsonProperty("dose")
    private String dose=null;
	
	@XmlElement(name="doseuom")
	@JsonProperty("doseuom")
    private String doseuom=null;
	
	@XmlElement(name="ivVolume")
	@JsonProperty("ivVolume")
    private String ivVolume=null;	
	
	@XmlElement(name="ivVolumeUOM")
	@JsonProperty("ivVolumeUOM")
    private String ivVolumeUOM=null;	
	
	@XmlElement(name="isCalculation")
	@JsonProperty("isCalculation")
    private int isCalculation;	
	
	@XmlElement(name="ivformularyid")
	@JsonProperty("ivformularyid")
	private int ivformularyid=-1;
	
	@XmlElement(name="isTitrationAllowed")
	@JsonProperty("isTitrationAllowed")
	private int isTitrationAllowed=-1;	
	
	@XmlElement(name="itemId")
	@JsonProperty("itemId")
	private int itemId=-1;
	
	@XmlElement(name="ingredientscount")
	@JsonProperty("ingredientscount")
    private int ingredientscount=-1;
	
	@XmlElement(name="ingredient1value")
	@JsonProperty("ingredient1value")
    private String ingredient1value=null;
	
	@XmlElement(name="ingredient1_uom")
	@JsonProperty("ingredient1_uom")
    private String ingredient1_uom=null;
	
	@XmlElement(name="ingredient2value")
	@JsonProperty("ingredient2value")
    private String ingredient2value=null;
	
	@XmlElement(name="ingredient2_uom")
	@JsonProperty("ingredient2_uom")
    private String ingredient2_uom=null;
	
	@XmlElement(name="ingredient3value")
	@JsonProperty("ingredient3value")
    private String ingredient3value=null;
	
	@XmlElement(name="ingredient3_uom")
	@JsonProperty("ingredient3_uom")
    private String ingredient3_uom=null;
	
	@XmlElement(name="ingredient4value")
	@JsonProperty("ingredient4value")
    private String ingredient4value=null;
	
	@XmlElement(name="ingredient4_uom")
	@JsonProperty("ingredient4_uom")
    private String ingredient4_uom=null;
	
	@XmlElement(name="ahfsClassification")
	@JsonProperty("ahfsClassification")
    private String ahfsClassification=null;	
	
	@XmlElement(name="isAdditive")
	@JsonProperty("isAdditive")
	private int isAdditive=-1;
	
	@XmlElement(name="isDiluent")
	@JsonProperty("isDiluent")
	private int isDiluent=-1;
	
	@XmlElement(name="formularyDose")
	@JsonProperty("formularyDose")
	private String formularyDose="";
	
	@XmlElement(name="formularyDoseUOM")
	@JsonProperty("formularyDoseUOM")
	private String formularyDoseUOM="";

	
	
	public String getDose() {
		return dose;
	}

	public void setDose(String dose) {
		this.dose = dose;
	}

	public String getDoseuom() {
		return doseuom;
	}

	public void setDoseuom(String doseuom) {
		this.doseuom = doseuom;
	}

	public String getDispenseSize() {
		return dispenseSize;
	}

	public void setDispenseSize(String dispenseSize) {
		this.dispenseSize = dispenseSize;
	}

	public String getDispenseSizeUOM() {
		return dispenseSizeUOM;
	}

	public void setDispenseSizeUOM(String dispenseSizeUOM) {
		this.dispenseSizeUOM = dispenseSizeUOM;
	}

	public String getIvVolume() {
		return ivVolume;
	}

	public void setIvVolume(String ivVolume) {
		this.ivVolume = ivVolume;
	}

	public String getIvVolumeUOM() {
		return ivVolumeUOM;
	}

	public void setIvVolumeUOM(String ivVolumeUOM) {
		this.ivVolumeUOM = ivVolumeUOM;
	}

	public int getIsCalculation() {
		return isCalculation;
	}

	public void setIsCalculation(int isCalculation) {
		this.isCalculation = isCalculation;
	}	
	
	public int getIsTitrationAllowed() {
		return isTitrationAllowed;
	}

	public void setIsTitrationAllowed(int isTitrationAllowed) {
		this.isTitrationAllowed = isTitrationAllowed;
	}

	public String getOrg_routeName() {
		return org_routeName;
	}

	public void setOrg_routeName(String org_routeName) {
		this.org_routeName = org_routeName;
	}

	public String getOrg_volume() {
		return org_volume;
	}

	public void setOrg_volume(String org_volume) {
		this.org_volume = org_volume;
	}

	public String getOrg_formulation() {
		return org_formulation;
	}

	public void setOrg_formulation(String org_formulation) {
		this.org_formulation = org_formulation;
	}

	public int getIvformularyid() {
	return ivformularyid; }

	public void setIvformularyid(int ivformularyid){
	this.ivformularyid = ivformularyid;
	}
	
	public int getId() {
	return id; }
	
	public void setId(int id){
	this.id = id;
	
	}
	
	public int getFormularyid() {
	return formularyid; }
	
	public void setFormularyid(int formularyid){
	this.formularyid = formularyid;
	
	}
	
	public int getOrdertypesetupid() {
	return ordertypesetupid; }
	
	public void setOrdertypesetupid(int ordertypesetupid){
	this.ordertypesetupid = ordertypesetupid;
	
	}
	
	public String getStrength() {
	return strength; }
	
	public void setStrength(String strength){
	this.strength = strength;
	
	}
	
	public String getStrengthuom() {
	return strengthuom; }
	
	public void setStrengthuom(String strengthuom){
	this.strengthuom = strengthuom;
	
	}
	
	public String getFormulation() {
		return formulation;
	}

	public void setFormulation(String formulation) {
		this.formulation = formulation;
	}
	
	public String getGpi() {
		return gpi;
	}

	public void setGpi(String gpi) {
		this.gpi = gpi;
	}

	public String getNdc() {
		return ndc;
	}

	public void setNdc(String ndc) {
		this.ndc = ndc;
	}

	public String getDdid() {
		return ddid;
	}

	public void setDdid(String ddid) {
		this.ddid = ddid;
	}

	public String getCreatedby() {
	return createdby; }
	
	public void setCreatedby(String createdby){
	this.createdby = createdby;
	
	}
	
	public String getCreatedon() {
	return createdon; }
	
	public void setCreatedon(String createdon){
	this.createdon = createdon;
	
	}
	
	public String getModifiedby() {
	return modifiedby; }
	
	public void setModifiedby(String modifiedby){
	this.modifiedby = modifiedby;
	
	}
	
	public String getModifiedon() {
	return modifiedon; }
	
	public void setModifiedon(String modifiedon){
	this.modifiedon = modifiedon;
	
	}
	
	public String getUserId() {
	return userId; }
	
	public void setUserId(String userId){
	this.userId = userId;
	
	}
	
	public String getNotes() {
	return notes; }
	
	public void setNotes(String notes){
	this.notes = notes;
	
	}
	public String getItemname() {
		return itemname;
	}

	public void setItemname(String itemname) {
		this.itemname = itemname;
	}
	
	

	public String getOrg_strength() {
		return org_strength;
	}

	public void setOrg_strength(String org_strength) {
		this.org_strength = org_strength;
	}

	public String getOrg_strengthuom() {
		return org_strengthuom;
	}

	public void setOrg_strengthuom(String org_strengthuom) {
		this.org_strengthuom = org_strengthuom;
	}	
	
	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	
	

	public int getIngredientscount() {
		return ingredientscount;
	}

	public void setIngredientscount(int ingredientscount) {
		this.ingredientscount = ingredientscount;
	}

	public String getIngredient1value() {
		return ingredient1value;
	}

	public void setIngredient1value(String ingredient1value) {
		this.ingredient1value = ingredient1value;
	}

	public String getIngredient1_uom() {
		return ingredient1_uom;
	}

	public void setIngredient1_uom(String ingredient1_uom) {
		this.ingredient1_uom = ingredient1_uom;
	}

	public String getIngredient2value() {
		return ingredient2value;
	}

	public void setIngredient2value(String ingredient2value) {
		this.ingredient2value = ingredient2value;
	}

	public String getIngredient2_uom() {
		return ingredient2_uom;
	}

	public void setIngredient2_uom(String ingredient2_uom) {
		this.ingredient2_uom = ingredient2_uom;
	}

	public String getIngredient3value() {
		return ingredient3value;
	}

	public void setIngredient3value(String ingredient3value) {
		this.ingredient3value = ingredient3value;
	}

	public String getIngredient3_uom() {
		return ingredient3_uom;
	}

	public void setIngredient3_uom(String ingredient3_uom) {
		this.ingredient3_uom = ingredient3_uom;
	}

	public String getIngredient4value() {
		return ingredient4value;
	}

	public void setIngredient4value(String ingredient4value) {
		this.ingredient4value = ingredient4value;
	}

	public String getIngredient4_uom() {
		return ingredient4_uom;
	}

	public void setIngredient4_uom(String ingredient4_uom) {
		this.ingredient4_uom = ingredient4_uom;
	}

	public String getAhfsClassification() {
		return ahfsClassification;
	}

	public void setAhfsClassification(String ahfsClassification) {
		this.ahfsClassification = ahfsClassification;
	}	
	
	public int getIsAdditive() {
		return isAdditive;
	}

	public void setIsAdditive(int isAdditive) {
		this.isAdditive = isAdditive;
	}

	public int getIsDiluent() {
		return isDiluent;
	}

	public void setIsDiluent(int isDiluent) {
		this.isDiluent = isDiluent;
	}		
	
	public String getFormularyDose() {
		return formularyDose;
	}

	public void setFormularyDose(String formularyDose) {
		this.formularyDose = formularyDose;
	}

	public String getFormularyDoseUOM() {
		return formularyDoseUOM;
	}

	public void setFormularyDoseUOM(String formularyDoseUOM) {
		this.formularyDoseUOM = formularyDoseUOM;
	}
	

	public String getDrugName() {
		return drugName;
	}

	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	//--------toString()---------------
	@Override
	public String toString(){
		return new StringBuilder("TemplateForIVDiluent [id=").append(id).append(",formularyid=").append(formularyid)
				.append(",ivformularyid=").append(ivformularyid).append(",ordertypesetupid=").append(ordertypesetupid)
				.append(",strength=").append(strength).append(",strengthuom=").append(strengthuom)
				.append(",formulation=").append(formulation).append(",createdby=").append(createdby).append(",ndc=")
				.append(ndc).append(",gpi=").append(gpi).append(",ddid=").append(ddid).append(",createdon=")
				.append(createdon).append(",modifiedby=").append(modifiedby).append(",modifiedon=").append(modifiedon)
				.append(",userId=").append(userId).append(",notes=").append(notes).append(",itemname=").append(itemname)
				.append(",org_strength=").append(strength).append(",itemId=").append(itemId).append(",org_strengthuom=")
				.append(strengthuom).append(",ingredientscount=").append(ingredientscount).append(",ingredient1value=")
				.append(ingredient1value).append(",ingredient1_uom=").append(ingredient1_uom)
				.append(",ingredient2value=").append(ingredient2value).append(",ingredient2_uom=")
				.append(ingredient2_uom).append(",ingredient3value=").append(ingredient3value)
				.append(",ingredient3_uom=").append(ingredient3_uom).append(",ingredient4value=")
				.append(ingredient4value)
				.append(",ingredient4_uom=").append(ingredient4_uom)
				.append(",org_formulation=").append(org_formulation)
				.append(",org_routeName=").append(org_routeName)
				.append(",org_volume=").append(org_volume)
				.append(",dispenseSize=").append(dispenseSize)
				.append(",dispenseSizeUOM=").append(dispenseSizeUOM)
				.append(",ivVolume=").append(ivVolume)
				.append(",isAdditive=").append(isAdditive)
				.append(",isDiluent=").append(isDiluent)				
				.append(",isTitrationAllowed=").append(isTitrationAllowed)
				.append(",ivVolumeUOM=").append(ivVolumeUOM)
				.append(",isCalculation=").append(isCalculation)
				.append(",formularyDose=").append(formularyDose)
				.append(",formularyDoseUOM=").append(formularyDoseUOM)
				.append(",ahfsClassification=").append(ahfsClassification)
				.append(",dose=").append(dose)
				.append(",doseuom=").append(doseuom)
				.append(",drugName=").append(drugName)
				.append("]").toString();
	}
}