package inpatientWeb.admin.pharmacySettings.formularySetup.dao;


import java.util.ArrayList;
import java.util.List;

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

import inpatientWeb.admin.pharmacySettings.formularySetup.modal.FormularyBrands;
import inpatientWeb.admin.pharmacySettings.formularySetup.modal.FormularyImmunization;

@XmlType(propOrder = {"id","drugNameID","genericDrugName","genericDrugNameID","controlSubstanceCode","itemId","ddid","gpi","rxnorm","genericDrugItemID","maxDose24Hours","maxDose24HoursUOM",
		"primaryClassification","secondaryClassification","tertiaryClassification","medIdCode","isgenericavailable","isactive",
		"formulation","strength","strengthuom","volume","volumeuom","hasdrugalias","chargecode","csa_schedule","ndc","upc","stockarea",
		"lookAlike","titlePreview","titleStyle","isRT","varianceLimit","varianceLimitUnit","varianceLimitAfter","varianceLimitAfterUnit","dualverifyreqd","issearchable","isNonFormulary",
		"isdrugtypebulk","ischargeableatdispense","isImmunization","vis","cvx","isavailableforall","isrestrictedoutsideOS","isMedication","isTPN",
		"isIV","isIVPGB","autosubstitute","createdby","createdon","modifiedby","modifiedon","delflag","userId","notes","drugAliasArr","medicationRoutes","otsData","maxDuration","maxDurationUnit","facilityId"
		,"serviceTypeId","isSingleDosagePackage","hcpcsCodeRange","hcpcsCodeUnit","hcpcsCodeType","isAllowToAdminWithoutApproval","isrestricted","isIVDiluent",
		"assigned_brand_itemid","assigned_brand_itemname","brandNames","itemName","dispenseForm","dispenseSize","dispenseSizeUom","routedGenericItemId","visData",
		"isppd","isAdditive","ahfsClassification","ahfsClassID","ahfsClassificationList","isMandatoryForOE","isRenewInExpiringTab","cptcodeitemid","dispenseuomlist","routeID","routeName"
		,"immItemId","immItemName","drugDataFromCloud","doseUom","doseSize","isCalculate","notesData","facilityList","serviceTypeList","isserviceforall","iscustommed","primaryppid"
		,"drugAlias","routedDrugId","mappedMedicationData","drugName","isDisplayPkgSize","isDisplayPkgType","packsize","packsizeunitcode","packType"})

@JsonPropertyOrder({"id","drugNameID","genericDrugName","genericDrugNameID","controlSubstanceCode","itemId","ddid","gpi","rxnorm","genericDrugItemID","maxDose24Hours","maxDose24HoursUOM",
	"primaryClassification","secondaryClassification","tertiaryClassification","medIdCode","isgenericavailable","isactive",
	"formulation","strength","strengthuom","volume","volumeuom","hasdrugalias","chargecode","csa_schedule","ndc","upc","stockarea",
	"lookAlike","titlePreview","titleStyle","isRT","varianceLimit","varianceLimitUnit","varianceLimitAfter","varianceLimitAfterUnit","dualverifyreqd","issearchable","isNonFormulary",
	"isdrugtypebulk","ischargeableatdispense","isImmunization","vis","cvx","isavailableforall","isrestrictedoutsideOS","isMedication","isTPN",
	"isIV","isIVPGB","autosubstitute","createdby","createdon","modifiedby","modifiedon","delflag","userId","notes","drugAliasArr","medicationRoutes","otsData","maxDuration","maxDurationUnit","facilityId","serviceTypeId",
	"isSingleDosagePackage","hcpcsCodeRange","hcpcsCodeUnit","hcpcsCodeType","isAllowToAdminWithoutApproval","isrestricted","isIVDiluent",
	"assigned_brand_itemid","assigned_brand_itemname","brandNames","itemName","dispenseForm","dispenseSize","dispenseSizeUom","routedGenericItemId","visData",
	"isppd","isAdditive","ahfsClassification","ahfsClassID","ahfsClassificationList","isMandatoryForOE","isRenewInExpiringTab","cptcodeitemid","dispenseuomlist","routeID","routeName",
	"immItemId","immItemName","drugDataFromCloud","doseUom","doseSize","isCalculate","notesData","facilityList","serviceTypeList","isserviceforall","iscustommed","primaryppid",
	"drugAlias","routedDrugId","mappedMedicationData","drugName","isDisplayPkgSize","isDisplayPkgType","packsize","packsizeunitcode","packType"})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class TemplateForDrug {
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	@XmlElement(name="id")
	@JsonProperty("id")
    private String id="-1";
	
	@XmlElementWrapper(name="genericDrugName")
	@XmlElement(name="genericDrugName")
	@JsonProperty("genericDrugName")
    private String genericDrugName="";
	
	@XmlElementWrapper(name="drugName")
	@XmlElement(name="drugName")
	@JsonProperty("drugName")
    private String drugName="";


	@XmlElementWrapper(name="controlSubstanceCode")
	@XmlElement(name="controlSubstanceCode")
	@JsonProperty("controlSubstanceCode")
    private String controlSubstanceCode="";
	
	@XmlElementWrapper(name="maxDuration")
	@XmlElement(name="maxDuration")
	@JsonProperty("maxDuration")
    private String maxDuration="";	
	
	@XmlElementWrapper(name="maxDurationUnit")
	@XmlElement(name="maxDurationUnit")
	@JsonProperty("maxDurationUnit")
    private String maxDurationUnit="";	
	
	@XmlElementWrapper(name="genericDrugNameID")
	@XmlElement(name="genericDrugNameID")
	@JsonProperty("genericDrugNameID")
    private String genericDrugNameID="";

	@XmlElementWrapper(name="drugNameID")
	@XmlElement(name="drugNameID")
	@JsonProperty("drugNameID")
    private String drugNameID="";
	
	@XmlElementWrapper(name="mappedMedicationData")
	@XmlElement(name="mappedMedicationData")
	@JsonProperty("mappedMedicationData")
    private List<TemplateForMedicationItems> mappedMedicationData = null;
	
	@XmlElement(name="itemId")
	@JsonProperty("itemId")
    private String itemId="";
	
	@XmlElement(name="itemName")
	@JsonProperty("itemName")
    private String itemName="";
	
	@XmlElement(name="routeID")
	@JsonProperty("routeID")
    private String routeID="";
	
	@XmlElement(name="routeName")
	@JsonProperty("routeName")
    private String routeName="";	
	
	@XmlElement(name="ddid")
	@JsonProperty("ddid")
    private String ddid="";
	
	@XmlElement(name="gpi")
	@JsonProperty("gpi")
    private String gpi="";
	
	@XmlElement(name="rxnorm")
	@JsonProperty("rxnorm")
	private String rxnorm="";

	@XmlElement(name="genericDrugItemID")
	@JsonProperty("genericDrugItemID")
	private String genericDrugItemID="";
	
	@XmlElement(name="maxDose24Hours")
	@JsonProperty("maxDose24Hours")
	private String maxDose24Hours="";
	
	@XmlElement(name="maxDose24HoursUOM")
	@JsonProperty("maxDose24HoursUOM")
	private String maxDose24HoursUOM="";
	
	@XmlElement(name="primaryClassification")
	@JsonProperty("primaryClassification")
	private String primaryClassification="";
	
	@XmlElement(name="secondaryClassification")
	@JsonProperty("secondaryClassification")
	private String secondaryClassification="";
	
	@XmlElement(name="tertiaryClassification")
	@JsonProperty("tertiaryClassification")
	private String tertiaryClassification="";
	
	@XmlElement(name="medIdCode")
	@JsonProperty("medIdCode")
	private String medIdCode="";
	
	@XmlElement(name="isgenericavailable")
	@JsonProperty("isgenericavailable")
	private String isgenericavailable="";
	
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
	
	@XmlElement(name="isactive")
	@JsonProperty("isactive")
	private String isactive="0";
	
	@XmlElement(name="formulation")
	@JsonProperty("formulation")
	private String formulation="";
	
	@XmlElement(name="strength")
	@JsonProperty("strength")
	private String strength="";
	
	@XmlElement(name="strengthuom")
	@JsonProperty("strengthuom")
	private String strengthuom="";
	
	@XmlElement(name="volume")
	@JsonProperty("volume")
	private String volume="";
	
	@XmlElement(name="volumeuom")
	@JsonProperty("volumeuom")
	private String volumeuom="";
	
	@XmlElement(name="hasdrugalias")
	@JsonProperty("hasdrugalias")
	private String hasdrugalias="";
	
	@XmlElement(name="chargecode")
	@JsonProperty("chargecode")
	private String chargecode="";
	
	@XmlElement(name="csa_schedule")
	@JsonProperty("csa_schedule")
	private String csa_schedule="";
	
	@XmlElement(name="ndc")
	@JsonProperty("ndc")
	private String ndc="";
	
	@XmlElement(name="upc")
	@JsonProperty("upc")
	private String upc="";
	
	@XmlElement(name="isppd")
	@JsonProperty("isppd")
	private String isppd="";
	
	@XmlElement(name="isMandatoryForOE")
	@JsonProperty("isMandatoryForOE")
	private String isMandatoryForOE="";
	
	@XmlElement(name="stockarea")
	@JsonProperty("stockarea")
	private String stockarea="";
	
	@XmlElement(name="lookAlike")
	@JsonProperty("lookAlike")
	private String lookAlike="";
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	@XmlElement(name="titlePreview")
	@JsonProperty("titlePreview")
	private String titlePreview="";
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	@XmlElement(name="titleStyle")
	@JsonProperty("titleStyle")
	private String titleStyle="";
	
	@XmlElement(name="isRT")
	@JsonProperty("isRT")
	private String isRT="";
	
	@XmlElement(name="iscustommed")
	@JsonProperty("iscustommed")
	private String iscustommed="0";
	
	@XmlElement(name="primaryppid")
	@JsonProperty("primaryppid")
	private String primaryppid="";
	
	@XmlElement(name="drugAlias")
	@JsonProperty("drugAlias")
	private String drugAlias="";
	
	@XmlElement(name="varianceLimit")
	@JsonProperty("varianceLimit")
	private String varianceLimit="";
	
	@XmlElement(name="varianceLimitUnit")
	@JsonProperty("varianceLimitUnit")
	private String varianceLimitUnit="";
	
	@XmlElement(name="varianceLimitAfter")
	@JsonProperty("varianceLimitAfter")
	private String varianceLimitAfter="";
	
	@XmlElement(name="varianceLimitAfterUnit")
	@JsonProperty("varianceLimitAfterUnit")
	private String varianceLimitAfterUnit="";
	

	@XmlElement(name="dualverifyreqd")
	@JsonProperty("dualverifyreqd")
	private String dualverifyreqd="";
	
	@XmlElement(name="issearchable")
	@JsonProperty("issearchable")
	private String issearchable="1";
	
	@XmlElement(name="isNonFormulary")
	@JsonProperty("isNonFormulary")
	private String isNonFormulary="";
	
	@XmlElement(name="isdrugtypebulk")
	@JsonProperty("isdrugtypebulk")
	private String isdrugtypebulk="";
	
	@XmlElement(name="ischargeableatdispense")
	@JsonProperty("ischargeableatdispense")
	private String ischargeableatdispense="";
	
	@XmlElement(name="isImmunization")
	@JsonProperty("isImmunization")
	private String isImmunization="";
	
	@XmlElement(name="vis")
	@JsonProperty("vis")
	private String vis="";
	
	@XmlElement(name="cvx")
	@JsonProperty("cvx")
	private String cvx="";
	
	@XmlElement(name="isavailableforall")
	@JsonProperty("isavailableforall")
	private String isavailableforall="";
	
	@XmlElement(name="isserviceforall")
	@JsonProperty("isserviceforall")
	private String isserviceforall="";
	
	
	
	@XmlElement(name="isrestrictedoutsideOS")
	@JsonProperty("isrestrictedoutsideOS")
	private String isrestrictedoutsideOS="";
	
	@XmlElement(name="isMedication")
	@JsonProperty("isMedication")
	private String isMedication="";
	
	@XmlElement(name="isTPN")
	@JsonProperty("isTPN")
	private String isTPN="";
	
	@XmlElement(name="isIV")
	@JsonProperty("isIV")
	private String isIV="";
	
	@XmlElement(name="isIVPGB")
	@JsonProperty("isIVPGB")
	private String isIVPGB="";
	
	@XmlElement(name="autosubstitute")
	@JsonProperty("autosubstitute")
	private String autosubstitute="";
	
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

	@XmlElement(name="drugAliasArr")
	@JsonProperty("drugAliasArr")
	private String drugAliasArr="";
	
	@XmlElement(name="medicationRoutes")
	@JsonProperty("medicationRoutes")
	private String medicationRoutes="";
	
	@XmlElement(name="otsData")
	@JsonProperty("otsData")
	private List<TemplateForOTS> otsData =  null;
	
	@XmlElement(name="facilityId")
	@JsonProperty("facilityId")
    private String facilityId = "";
	
	@XmlElement(name="serviceTypeId")
	@JsonProperty("serviceTypeId")
    private String serviceTypeId = "";
	
	@XmlElement(name="facilityList")
	@JsonProperty("facilityList") 
    private List<TemplateForFacilities> facilityList = null;
	
	@XmlElement(name="serviceTypeList")
	@JsonProperty("serviceTypeList")
    private List<TemplateForServiceTypes> serviceTypeList = null;
	
	@XmlElement(name="isSingleDosagePackage")
	@JsonProperty("isSingleDosagePackage")
    private String isSingleDosagePackage="";
	
	@XmlElement(name="isAllowToAdminWithoutApproval")
	@JsonProperty("isAllowToAdminWithoutApproval")
    private String isAllowToAdminWithoutApproval="";
	
	@XmlElement(name="hcpcsCodeRange")
	@JsonProperty("hcpcsCodeRange")
    private String hcpcsCodeRange="";
	
	@XmlElement(name="hcpcsCodeUnit")
	@JsonProperty("hcpcsCodeUnit")
    private String hcpcsCodeUnit="";
	
	@XmlElement(name="hcpcsCodeType")
	@JsonProperty("hcpcsCodeType")
    private String hcpcsCodeType="";
	
	
	@XmlElement(name="isrestricted")
	@JsonProperty("isrestricted")
    private String isrestricted="";
	
	@XmlElement(name="isIVDiluent")
	@JsonProperty("isIVDiluent")
    private String isIVDiluent="";
	
	@XmlElement(name="assigned_brand_itemid")
	@JsonProperty("assigned_brand_itemid")
    private String assigned_brand_itemid="";
	
	@XmlElement(name="assigned_brand_itemname")
	@JsonProperty("assigned_brand_itemname")
    private String assigned_brand_itemname="";
	
	@XmlElement(name="dispenseForm")
	@JsonProperty("dispenseForm")
    private String dispenseForm="";
	
	@XmlElement(name="dispenseSize")
	@JsonProperty("dispenseSize")
    private String dispenseSize="";
	
	@XmlElement(name="dispenseSizeUom")
	@JsonProperty("dispenseSizeUom")
    private String dispenseSizeUom="";
	
	@XmlElement(name="routedGenericItemId")
	@JsonProperty("routedGenericItemId")
    private String routedGenericItemId="";
	
	@XmlElement(name="isAdditive")
	@JsonProperty("isAdditive")
    private String isAdditive="";
	
	@XmlElement(name="ahfsClassification")
	@JsonProperty("ahfsClassification")
    private String ahfsClassification="";
	
	@XmlElement(name="ahfsClassID")
	@JsonProperty("ahfsClassID")
    private String ahfsClassID="";
	
	@XmlElement(name="ahfsClassificationList")
	@JsonProperty("ahfsClassificationList")
    private String ahfsClassificationList="";
	
	@XmlElement(name="isRenewInExpiringTab")
	@JsonProperty("isRenewInExpiringTab")
    private String isRenewInExpiringTab="";
	
	@XmlElement(name="cptcodeitemid")
	@JsonProperty("cptcodeitemid")
    private String cptcodeitemid="";
	
	@XmlElement(name="dispenseuomlist")
	@JsonProperty("dispenseuomlist")
    private String dispenseuomlist="";
	
	@XmlElement(name="immItemId")
	@JsonProperty("immItemId")
    private String immItemId="";
	
	@XmlElement(name="immItemName")
	@JsonProperty("immItemName")
    private String immItemName="";
	
	@XmlElement(name="drugDataFromCloud")
	@JsonProperty("drugDataFromCloud")
    private String drugDataFromCloud="";
	
	@XmlElement(name="doseUom")
	@JsonProperty("doseUom")
    private String doseUom="";
	
	@XmlElement(name="doseSize")
	@JsonProperty("doseSize")
    private String doseSize="";
		
	@XmlElement(name="isCalculate")
	@JsonProperty("isCalculate")
    private String isCalculate="";
	
	@XmlElement(name="notesData")
	@JsonProperty("notesData")
    private List<TemplateForNotes> notesData = null;
	
	@XmlElement(name="routedDrugId")
	@JsonProperty("routedDrugId")
	private String routedDrugId="";	
	
	
	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public String getServiceTypeId() {
		return serviceTypeId;
	}

	public void setServiceTypeId(String serviceTypeId) {
		this.serviceTypeId = serviceTypeId;
	}

	public List<TemplateForNotes> getNotesData() {
		return notesData;
	}

	public void setNotesData(List<TemplateForNotes> notesData) {
		this.notesData = notesData;
	}

	public String getIsCalculate() {
		return isCalculate;
	}

	public void setIsCalculate(String isCalculate) {
		this.isCalculate = isCalculate;
	}

	public String getDoseSize() {
		return doseSize;
	}

	public void setDoseSize(String doseSize) {
		this.doseSize = doseSize;
	}

	public String getDoseUom() {
		return doseUom;
	}

	public void setDoseUom(String doseUom) {
		this.doseUom = doseUom;
	}

	public String getDrugDataFromCloud() {
		return drugDataFromCloud;
	}

	public void setDrugDataFromCloud(String drugDataFromCloud) {
		this.drugDataFromCloud = drugDataFromCloud;
	}

	public String getImmItemName() {
		return immItemName;
	}

	public void setImmItemName(String immItemName) {
		this.immItemName = immItemName;
	}

	public String getImmItemId() {
		return immItemId;
	}

	public void setImmItemId(String immItemId) {
		this.immItemId = immItemId;
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

	public String getDispenseuomlist() {
		return dispenseuomlist;
	}

	public void setDispenseuomlist(String dispenseuomlist) {
		this.dispenseuomlist = dispenseuomlist;
	}

	public String getCptcodeitemid() {
		return cptcodeitemid;
	}

	public void setCptcodeitemid(String cptcodeitemid) {
		this.cptcodeitemid = cptcodeitemid;
	}

	public String getIsRenewInExpiringTab() {
		return isRenewInExpiringTab;
	}

	public void setIsRenewInExpiringTab(String isRenewInExpiringTab) {
		this.isRenewInExpiringTab = isRenewInExpiringTab;
	}

	public String getIsppd() {
		return isppd;
	}

	public void setIsppd(String isppd) {
		this.isppd = isppd;
	}

	public String getDispenseForm() {
		return dispenseForm;
	}

	public void setDispenseForm(String dispenseForm) {
		this.dispenseForm = dispenseForm;
	}

	public String getDispenseSize() {
		return dispenseSize;
	}

	public void setDispenseSize(String dispenseSize) {
		this.dispenseSize = dispenseSize;
	}

	public String getDispenseSizeUom() {
		return dispenseSizeUom;
	}

	public void setDispenseSizeUom(String dispenseSizeUom) {
		this.dispenseSizeUom = dispenseSizeUom;
	}

	public String getIsrestricted() {
		return isrestricted;
	}

	public void setIsrestricted(String isrestricted) {
		this.isrestricted = isrestricted;
	}

	public String getIsIVDiluent() {
		return isIVDiluent;
	}

	public void setIsIVDiluent(String isIVDiluent) {
		this.isIVDiluent = isIVDiluent;
	}

	public String getIsAllowToAdminWithoutApproval() {
		return isAllowToAdminWithoutApproval;
	}

	public void setIsAllowToAdminWithoutApproval(String isAllowToAdminWithoutApproval) {
		this.isAllowToAdminWithoutApproval = isAllowToAdminWithoutApproval;
	}

	public List<TemplateForServiceTypes> getServiceTypeList() {
		return serviceTypeList;
	}
	public void setServiceTypeList( List<TemplateForServiceTypes>  serviceTypeList) {
		this.serviceTypeList = serviceTypeList;
	}

	public List<TemplateForFacilities> getFacilityList() {
		return facilityList;
	}

	public void setFacilityList(List<TemplateForFacilities> facilityList) {
		this.facilityList = facilityList;
	}

	public String getMaxDuration() {
		return maxDuration;
	}

	public void setMaxDuration(String maxDuration) {
		this.maxDuration = maxDuration;
	}

	public String getMaxDurationUnit() {
		return maxDurationUnit;
	}

	public void setMaxDurationUnit(String maxDurationUnit) {
		this.maxDurationUnit = maxDurationUnit;
	}

	public String getControlSubstanceCode() {
		return controlSubstanceCode;
	}

	public void setControlSubstanceCode(String controlSubstanceCode) {
		this.controlSubstanceCode = controlSubstanceCode;
	}

	public String getGenericDrugNameID() {
		return genericDrugNameID;
	}

	public void setGenericDrugNameID(String genericDrugNameID) {
		this.genericDrugNameID = genericDrugNameID;
	}

	public String getGenericDrugName() {
		return genericDrugName;
	}

	public void setGenericDrugName(String genericDrugName) {
		this.genericDrugName = genericDrugName;
	}

	public List<TemplateForOTS> getOtsData() {
		return otsData;
	}

	public void setOtsData(List<TemplateForOTS> otsData) {
		this.otsData = otsData;
	}

	public String getMedicationRoutes() {
		return medicationRoutes;
	}

	public void setMedicationRoutes(String medicationRoutes) {
		this.medicationRoutes = medicationRoutes;
	}

	public String getDrugAliasArr() {
		return drugAliasArr;
	}

	public void setDrugAliasArr(String drugAliasArr) {
		this.drugAliasArr = drugAliasArr;
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



	public String getId() {
		return id;
	}



	public void setId(String id) {
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



	public String getDdid() {
		return ddid;
	}



	public void setDdid(String ddid) {
		this.ddid = ddid;
	}



	public String getGpi() {
		return gpi;
	}



	public void setGpi(String gpi) {
		this.gpi = gpi;
	}



	public String getRxnorm() {
		return rxnorm;
	}



	public void setRxnorm(String rxnorm) {
		this.rxnorm = rxnorm;
	}



	public String getGenericDrugItemID() {
		return genericDrugItemID;
	}



	public void setGenericDrugItemID(String genericDrugItemID) {
		this.genericDrugItemID = genericDrugItemID;
	}



	public String getMaxDose24Hours() {
		return maxDose24Hours;
	}



	public void setMaxDose24Hours(String maxDose24Hours) {
		this.maxDose24Hours = maxDose24Hours;
	}



	public String getMaxDose24HoursUOM() {
		return maxDose24HoursUOM;
	}



	public void setMaxDose24HoursUOM(String maxDose24HoursUOM) {
		this.maxDose24HoursUOM = maxDose24HoursUOM;
	}



	public String getPrimaryClassification() {
		return primaryClassification;
	}



	public void setPrimaryClassification(String primaryClassification) {
		this.primaryClassification = primaryClassification;
	}



	public String getSecondaryClassification() {
		return secondaryClassification;
	}



	public void setSecondaryClassification(String secondaryClassification) {
		this.secondaryClassification = secondaryClassification;
	}



	public String getTertiaryClassification() {
		return tertiaryClassification;
	}



	public void setTertiaryClassification(String tertiaryClassification) {
		this.tertiaryClassification = tertiaryClassification;
	}



	public String getMedIdCode() {
		return medIdCode;
	}



	public void setMedIdCode(String medIdCode) {
		this.medIdCode = medIdCode;
	}



	public String getIsgenericavailable() {
		return isgenericavailable;
	}



	public void setIsgenericavailable(String isgenericavailable) {
		this.isgenericavailable = isgenericavailable;
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

	public String getIsactive() {
		return isactive;
	}



	public void setIsactive(String isactive) {
		this.isactive = isactive;
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



	public String getStrengthuom() {
		return strengthuom;
	}



	public void setStrengthuom(String strengthuom) {
		this.strengthuom = strengthuom;
	}



	public String getVolume() {
		return volume;
	}



	public void setVolume(String volume) {
		this.volume = volume;
	}



	public String getVolumeuom() {
		return volumeuom;
	}



	public void setVolumeuom(String volumeuom) {
		this.volumeuom = volumeuom;
	}



	public String getHasdrugalias() {
		return hasdrugalias;
	}



	public void setHasdrugalias(String hasdrugalias) {
		this.hasdrugalias = hasdrugalias;
	}



	public String getChargecode() {
		return chargecode;
	}



	public void setChargecode(String chargecode) {
		this.chargecode = chargecode;
	}



	public String getCsa_schedule() {
		return csa_schedule;
	}



	public void setCsa_schedule(String csa_schedule) {
		this.csa_schedule = csa_schedule;
	}



	public String getNdc() {
		return ndc;
	}



	public void setNdc(String ndc) {
		this.ndc = ndc;
	}

	


	public String getUpc() {
		return upc;
	}

	public void setUpc(String upc) {
		this.upc = upc;
	}

	public String getStockarea() {
		return stockarea;
	}



	public void setStockarea(String stockarea) {
		this.stockarea = stockarea;
	}



	public String getLookAlike() {
		return lookAlike;
	}



	public void setLookAlike(String lookAlike) {
		this.lookAlike = lookAlike;
	}



	public String getTitlePreview() {
		return titlePreview;
	}



	public void setTitlePreview(String titlePreview) {
		this.titlePreview = titlePreview;
	}



	public String getTitleStyle() {
		return titleStyle;
	}



	public void setTitleStyle(String titleStyle) {
		this.titleStyle = titleStyle;
	}



	public String getIsRT() {
		return isRT;
	}



	public void setIsRT(String isRT) {
		this.isRT = isRT;
	}



	public String getVarianceLimit() {
		return varianceLimit;
	}



	public void setVarianceLimit(String varianceLimit) {
		this.varianceLimit = varianceLimit;
	}



	public String getVarianceLimitUnit() {
		return varianceLimitUnit;
	}



	public void setVarianceLimitUnit(String varianceLimitUnit) {
		this.varianceLimitUnit = varianceLimitUnit;
	}
	

	public String getVarianceLimitAfter() {
		return varianceLimitAfter;
	}



	public void setVarianceLimitAfter(String varianceLimitAfter) {
		this.varianceLimitAfter = varianceLimitAfter;
	}



	public String getVarianceLimitAfterUnit() {
		return varianceLimitAfterUnit;
	}



	public void setVarianceLimitAfterUnit(String varianceLimitAfterUnit) {
		this.varianceLimitAfterUnit = varianceLimitAfterUnit;
	}
	
	
	public String getDualverifyreqd() {
		return dualverifyreqd;
	}



	public void setDualverifyreqd(String dualverifyreqd) {
		this.dualverifyreqd = dualverifyreqd;
	}



	public String getIssearchable() {
		return issearchable;
	}



	public void setIssearchable(String issearchable) {
		this.issearchable = issearchable;
	}



	public String getIsNonFormulary() {
		return isNonFormulary;
	}



	public void setIsNonFormulary(String isNonFormulary) {
		this.isNonFormulary = isNonFormulary;
	}



	public String getIsdrugtypebulk() {
		return isdrugtypebulk;
	}



	public void setIsdrugtypebulk(String isdrugtypebulk) {
		this.isdrugtypebulk = isdrugtypebulk;
	}



	public String getIschargeableatdispense() {
		return ischargeableatdispense;
	}



	public void setIschargeableatdispense(String ischargeableatdispense) {
		this.ischargeableatdispense = ischargeableatdispense;
	}



	public String getIsImmunization() {
		return isImmunization;
	}



	public void setIsImmunization(String isImmunization) {
		this.isImmunization = isImmunization;
	}



	public String getVis() {
		return vis;
	}



	public void setVis(String vis) {
		this.vis = vis;
	}



	public String getCvx() {
		return cvx;
	}



	public void setCvx(String cvx) {
		this.cvx = cvx;
	}



	public String getIsserviceforall() {
		return isserviceforall;
	}

	public void setIsserviceforall(String isserviceforall) {
		this.isserviceforall = isserviceforall;
	}

	public String getIsavailableforall() {
		return isavailableforall;
	}



	public void setIsavailableforall(String isavailableforall) {
		this.isavailableforall = isavailableforall;
	}



	public String getIsrestrictedoutsideOS() {
		return isrestrictedoutsideOS;
	}



	public void setIsrestrictedoutsideOS(String isrestrictedoutsideOS) {
		this.isrestrictedoutsideOS = isrestrictedoutsideOS;
	}



	public String getIsMedication() {
		return isMedication;
	}



	public void setIsMedication(String isMedication) {
		this.isMedication = isMedication;
	}



	public String getIsTPN() {
		return isTPN;
	}



	public void setIsTPN(String isTPN) {
		this.isTPN = isTPN;
	}



	public String getIsIV() {
		return isIV;
	}



	public void setIsIV(String isIV) {
		this.isIV = isIV;
	}



	public String getIsIVPGB() {
		return isIVPGB;
	}



	public void setIsIVPGB(String isIVPGB) {
		this.isIVPGB = isIVPGB;
	}



	public String getAutosubstitute() {
		return autosubstitute;
	}



	public void setAutosubstitute(String autosubstitute) {
		this.autosubstitute = autosubstitute;
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
	
	public String getisSingleDosagePackage() {
		return isSingleDosagePackage;
	}

	public void setisSingleDosagePackage(String isSingleDosagePackage) {
		this.isSingleDosagePackage = isSingleDosagePackage;
	}

	public String getHcpcsCodeRange() {
		return hcpcsCodeRange;
	}

	public void setHcpcsCodeRange(String hCPCSCodeRange) {
		hcpcsCodeRange = hCPCSCodeRange;
	}

	public String getHcpcsCodeUnit() {
		return hcpcsCodeUnit;
	}

	public void setHcpcsCodeUnit(String hCPCSCodeUnit) {
		hcpcsCodeUnit = hCPCSCodeUnit;
	}

	public String getHcpcsCodeType() {
		return hcpcsCodeType;
	}

	public void setHcpcsCodeType(String hCPCSCodeType) {
		hcpcsCodeType = hCPCSCodeType;
	}
	


	public String getAssigned_brand_itemid() {
		return assigned_brand_itemid;
	}

	public void setAssigned_brand_itemid(String assigned_brand_itemid) {
		this.assigned_brand_itemid = assigned_brand_itemid;
	}
	
	
	
	public String getAssigned_brand_itemname() {
		return assigned_brand_itemname;
	}

	public void setAssigned_brand_itemname(String assigned_brand_itemname) {
		this.assigned_brand_itemname = assigned_brand_itemname;
	}



	private ArrayList<FormularyBrands> brandNames=null;

	public ArrayList<FormularyBrands> getBrandNames() {
	return brandNames;
	}
	public void setBrandNames(ArrayList<FormularyBrands> brandNames){
		this.brandNames = brandNames;
	}
	
	private ArrayList<FormularyImmunization> visData=null;

	public ArrayList<FormularyImmunization> getVisData() {
	return visData;
	}
	public void setVisData(ArrayList<FormularyImmunization> visData){
		this.visData = visData;
	}
	
	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getRoutedGenericItemId() {
		return routedGenericItemId;
	}

	public void setRoutedGenericItemId(String routedGenericItemId) {
		this.routedGenericItemId = routedGenericItemId;
	}
	

	public String getIsAdditive() {
		return isAdditive;
	}

	public void setIsAdditive(String isAdditive) {
		this.isAdditive = isAdditive;
	}
	
	

	public String getAhfsClassID() {
		return ahfsClassID;
	}

	public void setAhfsClassID(String ahfsClassID) {
		this.ahfsClassID = ahfsClassID;
	}

	public String getAhfsClassification() {
		return ahfsClassification;
	}

	public void setAhfsClassification(String ahfsClassification) {
		this.ahfsClassification = ahfsClassification;
	}

	public String getAhfsClassificationList() {
		return ahfsClassificationList;
	}

	public void setAhfsClassificationList(String ahfsClassificationList) {
		this.ahfsClassificationList = ahfsClassificationList;
	}
	

	public String getIsMandatoryForOE() {
		return isMandatoryForOE;
	}

	public void setIsMandatoryForOE(String isMandatoryForOE) {
		this.isMandatoryForOE = isMandatoryForOE;
	}
	
	

	public String getIscustommed() {
		return iscustommed;
	}

	public void setIscustommed(String iscustommed) {
		this.iscustommed = iscustommed;
	}

	public String getPrimaryppid() {
		return primaryppid;
	}

	public void setPrimaryppid(String primaryppid) {
		this.primaryppid = primaryppid;
	}
	
	public String getDrugAlias() {
		return drugAlias;
	}

	public void setDrugAlias(String drugAlias) {
		this.drugAlias = drugAlias;
	}
	
	public String getRoutedDrugId() {
		return routedDrugId;
	}

	public void setRoutedDrugId(String routedDrugId) {
		this.routedDrugId = routedDrugId;
	}

	public List<TemplateForMedicationItems> getMappedMedicationData() {
		return mappedMedicationData;
	}

	public void setMappedMedicationData(List<TemplateForMedicationItems> mappedMedicationData) {
		this.mappedMedicationData = mappedMedicationData;
	}

	public String getDrugName() {
		return drugName;
	}

	public void setDrugName(String drugName) {
		this.drugName = drugName;
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

	@Override
	public String toString() { 
		return new StringBuilder("TemplateForDrug [id=").append(id).append(",drugNameID=").append(drugNameID).append(",genericDrugName=").append(genericDrugName).append(",genericDrugNameID=")
				.append(genericDrugNameID).append(",itemId=").append(itemId).append(",ddid=").append(ddid).append(",gpi=").append(gpi).append(",rxnorm=").append(rxnorm).append(",genericDrugItemID=")
				.append(genericDrugItemID).append(",maxDose24Hours=").append(maxDose24Hours).append(",maxDose24HoursUOM=").append(maxDose24HoursUOM).append(",primaryClassification=")
				.append(primaryClassification).append(",secondaryClassification=").append(secondaryClassification).append(",tertiaryClassification=").append(tertiaryClassification)
				.append(",medIdCode=").append(medIdCode).append(",isgenericavailable=").append(isgenericavailable).append(",isactive=").append(isactive).append(",formulation=")
				.append(formulation).append(",strength=").append(strength).append(",strengthuom=").append(strengthuom).append(",volume=").append(volume).append(",volumeuom=")
				.append(volumeuom).append(",hasdrugalias=").append(hasdrugalias).append(",chargecode=").append(chargecode).append(",csa_schedule=").append(csa_schedule)
				.append(",ndc=").append(ndc).append(",upc=").append(upc)
				.append(",stockarea=").append(stockarea).append(",lookAlike=").append(lookAlike).append(",titlePreview=").append(titlePreview)
				.append(",titleStyle=").append(titleStyle).append(",isRT=").append(isRT).append(",varianceLimit=").append(varianceLimit).append(",varianceLimitUnit=")
				.append(varianceLimitUnit).append(",varianceLimit=").append(varianceLimit).append(",varianceLimitUnit=").append(varianceLimitUnit).append(",dualverifyreqd=")
				.append(dualverifyreqd).append(",issearchable=").append(issearchable).append(",isNonFormulary=").append(isNonFormulary).append(",isdrugtypebulk=")
				.append(isdrugtypebulk).append(",ischargeableatdispense=").append(ischargeableatdispense).append(",isImmunization=").append(isImmunization).append(",vis=")
				.append(vis).append(",cvx=").append(cvx).append(",isavailableforall=").append(isavailableforall).append(",isrestrictedoutsideOS=").append(isrestrictedoutsideOS)
				.append(",isMedication=").append(isMedication).append(",isTPN=").append(isTPN).append(",isIV").append(isIV).append(",isIVPGB=").append(isIVPGB)
				.append(",autosubstitute=").append(autosubstitute).append(",createdby=").append(createdby).append(",createdon=").append(createdon).append(",modifiedby=")
				.append(modifiedby).append(",modifiedon=").append(modifiedon).append(",delflag=").append(delflag).append(",userId=").append(userId).append(",notes=")
				.append(notes).append(",drugAliasArr=").append(drugAliasArr).append(",medicationRoutes=").append(medicationRoutes).append(",otsData=").append(otsData)
				.append(",maxDuration=").append(maxDuration).append(",maxDurationUnit=").append(maxDurationUnit).append(",facilityId=").append(facilityId)
				.append(",serviceTypeId=").append(serviceTypeId).append(",isSingleDosagePackage=").append(isSingleDosagePackage).append(",hcpcsCodeRange=").append(hcpcsCodeRange)
				.append(",hcpcsCodeUnit=").append(hcpcsCodeUnit).append(",hcpcsCodeType=").append(hcpcsCodeType).append(",isAllowToAdminWithoutApproval=")
				.append(isAllowToAdminWithoutApproval).append(",isrestricted=").append(isrestricted).append(",isIVDiluent=").append(isIVDiluent).append(",assigned_brand_itemid=")
				.append(assigned_brand_itemid).append(",assigned_brand_itemname=").append(assigned_brand_itemname).append(",brandNames=").append(brandNames)
				.append(",itemName=").append(itemName)
				.append(",dispenseForm=").append(dispenseForm)
				.append(",dispenseSize=").append(dispenseSize)
				.append(",dispenseSizeUom=").append(dispenseSizeUom)
				.append(",routedGenericItemId=").append(routedGenericItemId)
				.append(",visData=").append(visData)
				.append(",isppd=").append(isppd)
				.append(",isAdditive=").append(isAdditive)
				.append(",ahfsClassification=").append(ahfsClassification)
				.append(",ahfsClassID=").append(ahfsClassID)
				.append(",ahfsClassificationList=").append(ahfsClassificationList)
				.append(",isMandatoryForOE=").append(isMandatoryForOE)
				.append(",isRenewInExpiringTab=").append(isRenewInExpiringTab)
				.append(",cptcodeitemid=").append(cptcodeitemid)
				.append(",dispenseuomlist=").append(dispenseuomlist)
				.append(",routeID=").append(routeID)
				.append(",routeName=").append(routeName)
				.append(",immItemId=").append(immItemId)
				.append(",immItemName=").append(immItemName)
				.append(",drugDataFromCloud=").append(drugDataFromCloud)
				.append(",doseUom=").append(doseUom)
				.append(",doseSize=").append(doseSize)
				.append(",isCalculate=").append(isCalculate)
				.append(",notesData=").append(notesData)
				.append(",facilityList=").append(facilityList)
				.append(",serviceTypeList=").append(serviceTypeList)
				.append(",isserviceforall=").append(isserviceforall)
				.append(",iscustommed=").append(iscustommed)
				.append(",primaryppid=").append(primaryppid)
				.append(",drugAlias=").append(drugAlias)
				.append(",routedDrugId=").append(routedDrugId)
				.append(",mappedMedicationData=").append(mappedMedicationData)
				.append(",drugName=").append(drugName)
				.append(",isDisplayPkgSize=").append(isDisplayPkgSize)
				.append(",isDisplayPkgType=").append(isDisplayPkgType)
				.append(",packsize=").append(packsize)
				.append(",packsizeunitcode=").append(packsizeunitcode)
				.append(",packType=").append(packType)
				.append("]").toString();
	}
}
