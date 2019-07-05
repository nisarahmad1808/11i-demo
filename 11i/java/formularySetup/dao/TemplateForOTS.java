package inpatientWeb.admin.pharmacySettings.formularySetup.dao;


import java.util.ArrayList;

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

@XmlType(propOrder = {"id","formularyid","orderType","routeList","frequencyList","formulationList","autoStop","autoStopUOM","chargeType","nonBillable","createdby","createdon","modifiedby","modifiedon","delflag","userId","notes","ivDiluentDrugList","drugListForComplexOrder",
		"changeRate","titAllowed","refri","expiresOn","expiresOnUom","pca","bolusLoadingdose","bolusLoadingdoseuom","intermitten_dose","intermitten_dose_uom","lockout_interval_dose",
		"lockout_interval_uom","fourhrs_limit","fourhrs_limit_uom","slidingscale","sliding_scale_notes","diluent","iv_rate","ivdiluentsaveddata","ivratesaveddata","tpn_ivdiluentsaveddata","tpn_ivratesaveddata","chargeTypeId","recommendedRoutes","recommendedFrequency"})
@JsonPropertyOrder({"id","formularyid","orderType","routeList","frequencyList","formulationList","autoStop","autoStopUOM","chargeType","nonBillable","createdby","createdon","modifiedby","modifiedon","delflag","userId","notes","ivDiluentDrugList","drugListForComplexOrder",
	"changeRate","titAllowed","refri","expiresOn","expiresOnUom","pca","bolusLoadingdose","bolusLoadingdoseuom","intermitten_dose","intermitten_dose_uom","lockout_interval_dose",
	"lockout_interval_uom","fourhrs_limit","fourhrs_limit_uom","slidingscale","sliding_scale_notes","diluent","iv_rate","ivdiluentsaveddata","ivratesaveddata","tpn_ivdiluentsaveddata","tpn_ivratesaveddata"
	,"chargeTypeId","recommendedRoutes","recommendedFrequency","isChecked"})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class TemplateForOTS{  
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	
	@XmlElement(name="id")
	@JsonProperty("id")
    private String id="0";
	
	@XmlElement(name="formularyid")
	@JsonProperty("formularyid")
    private String formularyid="0";
	
	@XmlElement(name="isChecked")
	@JsonProperty("isChecked")
    private String isChecked="";
	
	@XmlElement(name="orderType")
	@JsonProperty("orderType")
    private String orderType="0";
	
	@XmlElement(name="routeList")
	@JsonProperty("routeList")
    private String routeList="";
	
	@XmlElement(name="frequencyList")
	@JsonProperty("frequencyList")
    private String frequencyList="";
	
	@XmlElement(name="formulationList")
	@JsonProperty("formulationList")
    private String formulationList="";
	
	@XmlElement(name="recommendedRoutes")
	@JsonProperty("recommendedRoutes")
    private String recommendedRoutes="";
	
	@XmlElement(name="recommendedFrequency")
	@JsonProperty("recommendedFrequency")
    private String recommendedFrequency="";
	
	@XmlElement(name="autoStop")
	@JsonProperty("autoStop")
    private String autoStop="";
	
	@XmlElement(name="autoStopUOM")
	@JsonProperty("autoStopUOM")
    private String autoStopUOM="";
	
	@XmlElement(name="chargeType")
	@JsonProperty("chargeType")
    private String chargeType="";
	
	@XmlElement(name="chargeTypeId")
	@JsonProperty("chargeTypeId")
    private String chargeTypeId="";
	
	
	@XmlElement(name="nonBillable")
	@JsonProperty("nonBillable")
    private String nonBillable="";
	
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
	
	@XmlElement(name="notes")
	@JsonProperty("notes")
	private String notes="";
	
	@XmlElement(name="ivDiluentDrugList")
	@JsonProperty("ivDiluentDrugList")
	private String ivDiluentDrugList="";
	
	@XmlElement(name="drugListForComplexOrder")
	@JsonProperty("drugListForComplexOrder")
	private String drugListForComplexOrder="";
	
	
	@XmlElement(name="changeRate")
	@JsonProperty("changeRate")
	private String changeRate="";
			
	@XmlElement(name="titAllowed")
	@JsonProperty("titAllowed")
	private String titAllowed = "";
	
	@XmlElement(name="refri")
	@JsonProperty("refri")
	private String refri = "";
	
		
	
	@XmlElement(name="expiresOn")
	@JsonProperty("expiresOn")
	private String expiresOn = "";
	
	@XmlElement(name="expiresOnUom")
	@JsonProperty("expiresOnUom")
	private String expiresOnUom = "";
	
	@XmlElement(name="pca")
	@JsonProperty("pca")
	private String pca = "";
	
	@XmlElement(name="bolusLoadingdose")
	@JsonProperty("bolusLoadingdose")
	private String bolusLoadingdose = "";
	
	@XmlElement(name="bolusLoadingdoseuom")
	@JsonProperty("bolusLoadingdoseuom")
	private String bolusLoadingdoseuom = "";
	
	@XmlElement(name="intermitten_dose")
	@JsonProperty("intermitten_dose")
	private String intermitten_dose = "";
	
	@XmlElement(name="intermitten_dose_uom")
	@JsonProperty("intermitten_dose_uom")
	private String intermitten_dose_uom = "";
	
	@XmlElement(name="lockout_interval_dose")
	@JsonProperty("lockout_interval_dose")
	private String lockout_interval_dose = "";
	
	@XmlElement(name="lockout_interval_uom")
	@JsonProperty("lockout_interval_uom")
	private String lockout_interval_uom = "";
	
	@XmlElement(name="fourhrs_limit")
	@JsonProperty("fourhrs_limit")
	private String fourhrs_limit = "";
	
	@XmlElement(name="fourhrs_limit_uom")
	@JsonProperty("fourhrs_limit_uom")
	private String fourhrs_limit_uom = "";
	
	@XmlElement(name="slidingscale")
	@JsonProperty("slidingscale")
	private String slidingscale = "";
	
	@XmlElement(name="sliding_scale_notes")
	@JsonProperty("sliding_scale_notes")
	private String sliding_scale_notes = "";
	
	@XmlElement(name="diluent")
	@JsonProperty("diluent")
	private ArrayList<String> diluent = null;
	
	@XmlElement(name="iv_rate")
	@JsonProperty("iv_rate")
	private ArrayList<String> iv_rate = null;
	
	@XmlElement(name="ivdiluentsaveddata")
	@JsonProperty("ivdiluentsaveddata")
	private String ivdiluentsaveddata=null;

	@XmlElement(name="ivratesaveddata")
	@JsonProperty("ivratesaveddata")
	private String ivratesaveddata=null;
	
	@XmlElement(name="tpn_ivdiluentsaveddata")
	@JsonProperty("tpn_ivdiluentsaveddata")
	private String tpn_ivdiluentsaveddata=null;

	@XmlElement(name="tpn_ivratesaveddata")
	@JsonProperty("tpn_ivratesaveddata")
	private String tpn_ivratesaveddata=null;

	public String getIvdiluentsaveddata() {
	return ivdiluentsaveddata; }
	
	

	public String getIsChecked() {
		return isChecked;
	}



	public void setIsChecked(String isChecked) {
		this.isChecked = isChecked;
	}



	public void setIvdiluentsaveddata(String ivdiluentsaveddata){
	this.ivdiluentsaveddata = ivdiluentsaveddata;

	}

	public String getIvratesaveddata() {
	return ivratesaveddata; }

	public void setIvratesaveddata(String ivratesaveddata){
	this.ivratesaveddata = ivratesaveddata;

	}
	public String getTpn_ivdiluentsaveddata() {
		return tpn_ivdiluentsaveddata;
	}

	public void setTpn_ivdiluentsaveddata(String tpn_ivdiluentsaveddata) {
		this.tpn_ivdiluentsaveddata = tpn_ivdiluentsaveddata;
	}

	public String getTpn_ivratesaveddata() {
		return tpn_ivratesaveddata;
	}

	public void setTpn_ivratesaveddata(String tpn_ivratesaveddata) {
		this.tpn_ivratesaveddata = tpn_ivratesaveddata;
	}

	public String getChangeRate() {
		return changeRate;
	}

	public void setChangeRate(String changeRate) {
		this.changeRate = changeRate;
	}

	public String getTitAllowed() {
		return titAllowed;
	}



	public void setTitAllowed(String titAllowed) {
		this.titAllowed = titAllowed;
	}



	public String getRefri() {
		return refri;
	}



	public void setRefri(String refri) {
		this.refri = refri;
	}



	public String getExpiresOn() {
		return expiresOn;
	}



	public void setExpiresOn(String expiresOn) {
		this.expiresOn = expiresOn;
	}



	public String getExpiresOnUom() {
		return expiresOnUom;
	}



	public void setExpiresOnUom(String expiresOnUom) {
		this.expiresOnUom = expiresOnUom;
	}



	public String getPca() {
		return pca;
	}



	public void setPca(String pca) {
		this.pca = pca;
	}



	public String getBolusLoadingdose() {
		return bolusLoadingdose;
	}



	public void setBolusLoadingdose(String bolusLoadingdose) {
		this.bolusLoadingdose = bolusLoadingdose;
	}



	public String getBolusLoadingdoseuom() {
		return bolusLoadingdoseuom;
	}



	public void setBolusLoadingdoseuom(String bolusLoadingdoseuom) {
		this.bolusLoadingdoseuom = bolusLoadingdoseuom;
	}



	public String getIntermitten_dose() {
		return intermitten_dose;
	}



	public void setIntermitten_dose(String intermitten_dose) {
		this.intermitten_dose = intermitten_dose;
	}



	public String getIntermitten_dose_uom() {
		return intermitten_dose_uom;
	}



	public void setIntermitten_dose_uom(String intermitten_dose_uom) {
		this.intermitten_dose_uom = intermitten_dose_uom;
	}



	public String getLockout_interval_dose() {
		return lockout_interval_dose;
	}



	public void setLockout_interval_dose(String lockout_interval_dose) {
		this.lockout_interval_dose = lockout_interval_dose;
	}



	public String getLockout_interval_uom() {
		return lockout_interval_uom;
	}



	public void setLockout_interval_uom(String lockout_interval_uom) {
		this.lockout_interval_uom = lockout_interval_uom;
	}



	public String getFourhrs_limit() {
		return fourhrs_limit;
	}



	public void setFourhrs_limit(String fourhrs_limit) {
		this.fourhrs_limit = fourhrs_limit;
	}



	public String getFourhrs_limit_uom() {
		return fourhrs_limit_uom;
	}



	public void setFourhrs_limit_uom(String fourhrs_limit_uom) {
		this.fourhrs_limit_uom = fourhrs_limit_uom;
	}



	public String getSlidingscale() {
		return slidingscale;
	}



	public void setSlidingscale(String slidingscale) {
		this.slidingscale = slidingscale;
	}



	public String getSliding_scale_notes() {
		return sliding_scale_notes;
	}



	public void setSliding_scale_notes(String sliding_scale_notes) {
		this.sliding_scale_notes = sliding_scale_notes;
	}



	public ArrayList<String> getDiluent() {
		return diluent;
	}



	public void setDiluent(ArrayList<String> diluent) {
		this.diluent = diluent;
	}



	public ArrayList<String> getIv_rate() {
		return iv_rate;
	}



	public void setIv_rate(ArrayList<String> iv_rate) {
		this.iv_rate = iv_rate;
	}



	public String getIvDiluentDrugList() {
		return ivDiluentDrugList;
	}



	public void setIvDiluentDrugList(String ivDiluentDrugList) {
		this.ivDiluentDrugList = ivDiluentDrugList;
	}



	public String getDrugListForComplexOrder() {
		return drugListForComplexOrder;
	}



	public void setDrugListForComplexOrder(String drugListForComplexOrder) {
		this.drugListForComplexOrder = drugListForComplexOrder;
	}



	public String getFormularyid() {
		return formularyid;
	}



	public void setFormularyid(String formularyid) {
		this.formularyid = formularyid;
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
	
	
	

	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getNotes() {
		return notes;
	}



	public void setNotes(String notes) {
		this.notes = notes;
	}



	public String getOrderType() {
		return orderType;
	}



	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}



	public String getRouteList() {
		return routeList;
	}



	public void setRouteList(String routeList) {
		this.routeList = routeList;
	}



	public String getFrequencyList() {
		return frequencyList;
	}



	public void setFrequencyList(String frequencyList) {
		this.frequencyList = frequencyList;
	}



	public String getFormulationList() {
		return formulationList;
	}



	public void setFormulationList(String formulationList) {
		this.formulationList = formulationList;
	}



	public String getAutoStop() {
		return autoStop;
	}



	public void setAutoStop(String autoStop) {
		this.autoStop = autoStop;
	}



	public String getAutoStopUOM() {
		return autoStopUOM;
	}



	public void setAutoStopUOM(String autoStopUOM) {
		this.autoStopUOM = autoStopUOM;
	}



	public String getChargeType() {
		return chargeType;
	}



	public void setChargeType(String chargeType) {
		this.chargeType = chargeType;
	}



	public String getNonBillable() {
		return nonBillable;
	}



	public void setNonBillable(String nonBillable) {
		this.nonBillable = nonBillable;
	}

	
	
	public String getChargeTypeId() {
		return chargeTypeId;
	}

	public void setChargeTypeId(String chargeTypeId) {
		this.chargeTypeId = chargeTypeId;
	}
	

	public String getRecommendedRoutes() {
		return recommendedRoutes;
	}

	public void setRecommendedRoutes(String recommendedRoutes) {
		this.recommendedRoutes = recommendedRoutes;
	}

	public String getRecommendedFrequency() {
		return recommendedFrequency;
	}

	public void setRecommendedFrequency(String recommendedFrequency) {
		this.recommendedFrequency = recommendedFrequency;
	}

	@Override
	public String toString() { 
		return new StringBuilder("TemplateForOTS [id=").append(id)
				.append(",formularyid=").append(formularyid)
				.append(",orderType=").append(orderType)
				.append(",routeList=").append(routeList)
				.append(",formulationList=").append(formulationList)
				.append(",frequencyList=").append(frequencyList)
				.append(",autoStop=").append(autoStop)
				.append(",autoStopUOM=").append(autoStopUOM)
				.append(",chargeType=").append(chargeType)
				.append(",nonBillable=").append(nonBillable)
				.append(",notes=").append(notes)
				.append(",ivDiluentDrugList=").append(ivDiluentDrugList)
				.append(",drugListForComplexOrder=").append(drugListForComplexOrder)
				.append(",changeRate=").append(changeRate)
				.append(",titAllowed=").append(titAllowed)
				.append(",refri=").append(refri)
				.append(",expiresOn=").append(expiresOn)
				.append(",expiresOnUom=").append(expiresOnUom)
				.append(",pca=").append(pca)
				.append(",bolusLoadingdose=").append(bolusLoadingdose)
				.append(",bolusLoadingdoseuom=").append(bolusLoadingdoseuom)
				.append(",intermitten_dose=").append(intermitten_dose)
				.append(",intermitten_dose_uom=").append(intermitten_dose_uom)
				.append(",lockout_interval_dose=").append(lockout_interval_dose)
				.append(",lockout_interval_uom=").append(lockout_interval_uom)
				.append(",fourhrs_limit=").append(fourhrs_limit)
				.append(",fourhrs_limit_uom=").append(fourhrs_limit_uom)
				.append(",slidingscale=").append(slidingscale)
				.append(",sliding_scale_notes=").append(sliding_scale_notes)
				.append(",diluent=").append(diluent)
				.append(",iv_rate=").append(iv_rate)
				.append(",createdby=").append(createdby)
				.append(",createdon=").append(createdon)
				.append(",modifiedby=").append(modifiedby)
				.append(",modifiedon=").append(modifiedon)
				.append(",ivdiluentsaveddata=").append(ivdiluentsaveddata)
				.append(",ivratesaveddata=").append(ivratesaveddata)
				.append(",tpn_ivdiluentsaveddata=").append(tpn_ivdiluentsaveddata)
				.append(",tpn_ivratesaveddata=").append(tpn_ivratesaveddata)
				.append(",chargeTypeId=").append(chargeTypeId)
				.append(",recommendedFrequency=").append(recommendedFrequency)
				.append(",isChecked=").append(isChecked)
				.append("]").toString();
	}
    
}
