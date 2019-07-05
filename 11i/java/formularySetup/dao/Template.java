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

@XmlType(propOrder = { "id", "drugNameID", "itemId", "itemName", "ndc", "upc", "status", "route", "formulation",
		"strength", "strengthuom", "autosubstitute", "isGenericDrug", "genericName", "brandName", "chargecode",
		"routedGenericId", "routedGenericItemId", "volume", "genericDrugName", "routeID", "routeName", "drugName",
		"dispensesizeuom", "dispensesize", "dose", "doseuom", "isCalculate","iscustommed","isDisplayPkgSize","isDisplayPkgType",
		"packsize","packsizeunitcode","packType","rxnorm","csa_schedule" })
@JsonPropertyOrder({ "id", "drugNameID", "itemId", "itemName", "ndc", "upc", "status", "route", "formulation",
		"strength", "strengthuom", "autosubstitute", "isGenericDrug", "genericName", "brandName", "chargecode",
		"routedGenericId", "routedGenericItemId", "volume", "genericDrugName", "routeID", "routeName", "drugName",
		"dispensesizeuom", "dispensesize", "dose", "doseuom", "isCalculate","iscustommed","isDisplayPkgSize",
		"isDisplayPkgType","packsize","packsizeunitcode","packType","modifiedby","modifiedon","createdbyusername","modifiedbyusername","rxnorm","csa_schedule" })
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Template {

	@XmlElement(name = "id")
	@JsonProperty("id")
	private int id = -1;

	@XmlElement(name = "drugNameID")
	@JsonProperty("drugNameID")
	private String drugNameID = "";

	@XmlElement(name = "drugName")
	@JsonProperty("drugName")
	private String drugName = "";

	@XmlElement(name = "itemId")
	@JsonProperty("itemId")
	private String itemId = "";

	@XmlElement(name="isDisplayPkgSize")
	@JsonProperty("isDisplayPkgSize")
	private String isDisplayPkgSize="";
	
	@XmlElement(name="isDisplayPkgType")
	@JsonProperty("isDisplayPkgType")
	private String isDisplayPkgType="";
	
	@XmlElement(name="packsize")
	@JsonProperty("packsize")
	private String packsize="";
	
	@XmlElement(name="packsizeunitcode")
	@JsonProperty("packsizeunitcode")
	private String packsizeunitcode="";
	
	@XmlElement(name="packType")
	@JsonProperty("packType")
	private String packType="";
	
	@XmlElement(name="modifiedby")
	@JsonProperty("modifiedby")
	private String modifiedby="";
	
	@XmlElement(name="modifiedon")
	@JsonProperty("modifiedon")
	private String modifiedon="";
	
	@XmlElement(name = "itemName")
	@JsonProperty("itemName")
	private String itemName = "";

	@XmlElement(name = "genericName")
	@JsonProperty("genericName")
	private String genericName = "";

	@XmlElement(name = "genericDrugName")
	@JsonProperty("genericDrugName")
	private String genericDrugName = "";

	@XmlElement(name = "brandName")
	@JsonProperty("brandName")
	private String brandName = "";

	@XmlElement(name = "chargecode")
	@JsonProperty("chargecode")
	private String chargecode = "";

	@XmlElement(name = "ndc")
	@JsonProperty("ndc")
	private String ndc = "";
	
	@XmlElement(name = "rxnorm")
	@JsonProperty("rxnorm")
	private String rxnorm = "";

	@XmlElement(name = "upc")
	@JsonProperty("upc")
	private String upc = "";

	@XmlElement(name = "status")
	@JsonProperty("status")
	private String status = "";

	@XmlElement(name = "isCalculate")
	@JsonProperty("isCalculate")
	private String isCalculate = "";

	@XmlElement(name = "dispensesize")
	@JsonProperty("dispensesize")
	private String dispensesize = "";

	@XmlElement(name = "dispensesizeuom")
	@JsonProperty("dispensesizeuom")
	private String dispensesizeuom = "";

	@XmlElement(name = "dose")
	@JsonProperty("dose")
	private String dose = "";

	@XmlElement(name = "doseuom")
	@JsonProperty("doseuom")
	private String doseuom = "";

	@XmlElement(name = "route")
	@JsonProperty("route")
	private String route = "";

	@XmlElement(name = "formulation")
	@JsonProperty("formulation")
	private String formulation = "";

	@XmlElement(name = "strength")
	@JsonProperty("strength")
	private String strength = "";

	@XmlElement(name = "routeID")
	@JsonProperty("routeID")
	private String routeID = "";

	@XmlElement(name = "routeName")
	@JsonProperty("routeName")
	private String routeName = "";

	@XmlElement(name = "volume")
	@JsonProperty("volume")
	private String volume = "";

	@XmlElement(name = "strengthuom")
	@JsonProperty("strengthuom")
	private String strengthuom = "";

	@XmlElement(name = "autosubstitute")
	@JsonProperty("autosubstitute")
	private String autosubstitute = "";

	@XmlElement(name = "isGenericDrug")
	@JsonProperty("isGenericDrug")
	private String isGenericDrug = "";

	@XmlElement(name = "routedGenericId")
	@JsonProperty("routedGenericId")
	private String routedGenericId = "";

	@XmlElement(name = "routedGenericItemId")
	@JsonProperty("routedGenericItemId")
	private String routedGenericItemId = "";
	
	@XmlElement(name = "iscustommed")
	@JsonProperty("iscustommed")
	private String iscustommed = "";
	
	@XmlElement(name = "createdbyusername")
	@JsonProperty("createdbyusername")
	private String createdbyusername = "";
	
	@XmlElement(name = "modifiedbyusername")
	@JsonProperty("modifiedbyusername")
	private String modifiedbyusername = "";
	
	@XmlElement(name = "csa_schedule")
	@JsonProperty("csa_schedule")
	private String csa_schedule = "";
	

	public String getRxnorm() {
		return rxnorm;
	}

	public void setRxnorm(String rxnorm) {
		this.rxnorm = rxnorm;
	}

	public String getIsCalculate() {
		return isCalculate;
	}

	public void setIsCalculate(String isCalculate) {
		this.isCalculate = isCalculate;
	}
	public String getDispensesize() {
		return dispensesize;
	}
	public void setDispensesize(String dispensesize) {
		this.dispensesize = dispensesize;
	}
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
	public String getDispensesizeuom() {
		return dispensesizeuom;
	}
	public void setDispensesizeuom(String dispensesizeuom) {
		this.dispensesizeuom = dispensesizeuom;
	}
	public String getDrugName() {
		return drugName;
	}
	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}
	public String getUpc() {
		return upc;
	}
	public void setUpc(String upc) {
		this.upc = upc;
	}
	public String getGenericDrugName() {
		return genericDrugName;
	}
	public void setGenericDrugName(String genericDrugName) {
		this.genericDrugName = genericDrugName;
	}
	public String getRoutedGenericItemId() {
		return routedGenericItemId;
	}
	public void setRoutedGenericItemId(String routedGenericItemId) {
		this.routedGenericItemId = routedGenericItemId;
	}
	public String getGenericName() {
		return genericName;
	}
	public void setGenericName(String genericName) {
		this.genericName = genericName;
	}
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDrugNameID() {
		return drugNameID;
	}
	public void setDrugNameID(String drugNameID) {
		this.drugNameID = drugNameID;
	}
	
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
	public String getNdc() {
		return ndc;
	}
	public void setNdc(String ndc) {
		this.ndc = ndc;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getRoute() {
		return route;
	}
	public void setRoute(String route) {
		this.route = route;
	}
	public String getFormulation() {
		return formulation;
	}
	public void setFormulation(String formulation) {
		this.formulation = formulation;
	}
	public String getStrength() {
		return strength;
	}
	public void setStrength(String strength) {
		this.strength = strength;
	}
	
	public String getStrengthUom() {
		return strengthuom;
	}
	public void setStrengthUom(String strengthuom) {
		this.strengthuom = strengthuom;
	}
	public String getAutosubstitute() {
		return autosubstitute;
	}
	public void setAutosubstitute(String autosubstitute) {
		this.autosubstitute = autosubstitute;
	}
	
	public String getIsGenericDrug() {
		return isGenericDrug;
	}
	public void setIsGenericDrug(String isGenericDrug) {
		this.isGenericDrug = isGenericDrug;
	}
	
	
	public String getChargecode() {
		return chargecode;
	}
	public void setChargecode(String chargecode) {
		this.chargecode = chargecode;
	}
	
	
	public String getRoutedGenericId() {
		return routedGenericId;
	}
	public void setRoutedGenericId(String routedGenericId) {
		this.routedGenericId = routedGenericId;
	}
	
	public String getVolume() {
		return volume;
	}
	public void setVolume(String volume) {
		this.volume = volume;
	}
	
	
	public String getRouteID() {
		return routeID;
	}
	public void setRouteID(String routeID) {
		this.routeID = routeID;
	}
	public String getRouteName() {
		return routeName;
	}
	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}
	
	public String getIscustommed() {
		return iscustommed;
	}

	public void setIscustommed(String iscustommed) {
		this.iscustommed = iscustommed;
	}
	
	

	public String getIsDisplayPkgSize() {
		return isDisplayPkgSize;
	}

	public void setIsDisplayPkgSize(String isDisplayPkgSize) {
		this.isDisplayPkgSize = isDisplayPkgSize;
	}

	public String getIsDisplayPkgType() {
		return isDisplayPkgType;
	}

	public void setIsDisplayPkgType(String isDisplayPkgType) {
		this.isDisplayPkgType = isDisplayPkgType;
	}

	public String getPacksize() {
		return packsize;
	}

	public void setPacksize(String packsize) {
		this.packsize = packsize;
	}

	public String getPacksizeunitcode() {
		return packsizeunitcode;
	}

	public void setPacksizeunitcode(String packsizeunitcode) {
		this.packsizeunitcode = packsizeunitcode;
	}

	public String getPackType() {
		return packType;
	}

	public void setPackType(String packType) {
		this.packType = packType;
	}

	public String getStrengthuom() {
		return strengthuom;
	}

	public void setStrengthuom(String strengthuom) {
		this.strengthuom = strengthuom;
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
	
	public String getCreatedbyusername() {
		return createdbyusername;
	}

	public void setCreatedbyusername(String createdbyusername) {
		this.createdbyusername = createdbyusername;
	}

	public String getModifiedbyusername() {
		return modifiedbyusername;
	}

	public void setModifiedbyusername(String modifiedbyusername) {
		this.modifiedbyusername = modifiedbyusername;
	}
	
	public String getCsa_schedule() {
		return csa_schedule;
	}

	public void setCsa_schedule(String csa_schedule) {
		this.csa_schedule = csa_schedule;
	}

	@Override
	public String toString() {
		return new StringBuilder("Template [id=").append(id).append(",drugNameID=").append(drugNameID)
				.append(",itemId=").append(itemId).append(",itemName=").append(itemName).append(",ndc=").append(ndc)
				.append(",upc=").append(upc).append(",status=").append(status).append(",route=").append(route)
				.append("formulation=").append(formulation).append(",strength=").append(strength)
				.append(",strengthuom=").append(strengthuom).append(",autosubstitute=").append(autosubstitute)
				.append(",isGenericDrug=").append(isGenericDrug).append(",genericName=").append(genericName)
				.append(",brandName=").append(brandName).append(",chargecode=").append(chargecode)
				.append(",routedGenericId=").append(routedGenericId).append(",routedGenericItemId=")
				.append(routedGenericItemId).append(",volume=").append(volume).append(",routeID=").append(routeID)
				.append(",routeName=").append(routeName).append(",drugName=").append(drugName).append(",dispensesize=")
				.append(dispensesize).append(",dispensesizeuom=").append(dispensesizeuom).append(",dose=").append(dose)
				.append(",doseuom=").append(doseuom)
				.append(",isCalculate=").append(isCalculate)
				.append(",iscustommed=").append(iscustommed)
				.append(",isDisplayPkgSize=").append(isDisplayPkgSize)
				.append(",isDisplayPkgType=").append(isDisplayPkgType)
				.append(",packsize=").append(packsize)
				.append(",rxnorm=").append(rxnorm)
				.append(",csa_schedule=").append(csa_schedule)
				.append(",packsizeunitcode=").append(packsizeunitcode)
				.append(",packType=").append(packType)
				.append(",modifiedby=").append(modifiedby)
				.append(",modifiedbyusername=").append(modifiedbyusername)
				.append(",createdbyusername=").append(createdbyusername)
				.append(",modifiedon=").append(modifiedon)
				.append("]").toString();
	}

}
