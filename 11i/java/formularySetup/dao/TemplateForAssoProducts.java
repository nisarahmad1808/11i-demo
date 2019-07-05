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
@XmlType(propOrder = { "id", "formularyid", "ppid", "packSize", "packSizeUnitCode", "packQuantity", "packType", "awp",
		"manufacturerName", "manufacturerIdentifier", "mvxCode", "cost_to_proc", "unitcost", "status", "isprimary",
		"marketEndDate", "createdby", "createdon", "modifiedby", "modifiedon", "userId", "notes", "ndc","itemId","drugName",
		"routedDrugId","lotDetails","productName","isVfc","isSingleDosagePack","rxnorm","upc","awup","ndc10"})
// --------json property order---------------
@JsonPropertyOrder({ "id", "formularyid", "ppid", "packSize", "packSizeUnitCode", "packQuantity", "packType", "awp",
		"manufacturerName", "manufacturerIdentifier", "mvxCode", "cost_to_proc", "unitcost", "status", "isprimary",
		"marketEndDate", "createdby", "createdon", "modifiedby", "modifiedon", "userId", "notes", "ndc","itemId","drugName",
		"routedDrugId","lotDetails","productName","isVfc","isSingleDosagePack","rxnorm","upc","awup","ndc10" })

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement

public class TemplateForAssoProducts {
	@JsonIgnoreProperties(ignoreUnknown = true)

	// --------class level properties---------------
	@XmlElement(name = "id")
	@JsonProperty("id")
	private int id = 0;

	@XmlElement(name = "formularyid")
	@JsonProperty("formularyid")
	private int formularyid = 0;

	@XmlElement(name = "ppid")
	@JsonProperty("ppid")
	private String ppid = "0";

	@XmlElement(name = "packSize")
	@JsonProperty("packSize")
	private String packSize = "";

	@XmlElement(name = "packSizeUnitCode")
	@JsonProperty("packSizeUnitCode")
	private String packSizeUnitCode = "";

	@XmlElement(name = "packQuantity")
	@JsonProperty("packQuantity")
	private String packQuantity = "";

	@XmlElement(name = "packType")
	@JsonProperty("packType")
	private String packType = "";

	@XmlElement(name = "awp")
	@JsonProperty("awp")
	private String awp = "";
	
	@XmlElement(name = "awup")
	@JsonProperty("awup")
	private String awup = "";
	
	@XmlElement(name = "ndc10")
	@JsonProperty("ndc10")
	private String ndc10 = "";

	@XmlElement(name = "manufacturerName")
	@JsonProperty("manufacturerName")
	private String manufacturerName = "";

	@XmlElement(name = "manufacturerIdentifier")
	@JsonProperty("manufacturerIdentifier")
	private String manufacturerIdentifier = "";

	@XmlElement(name = "mvxCode")
	@JsonProperty("mvxCode")
	private String mvxCode = "";
	
	@XmlElement(name = "rxnorm")
	@JsonProperty("rxnorm")
	private String rxnorm = "";
	
	@XmlElement(name = "upc")
	@JsonProperty("upc")
	private String upc = "";

	@XmlElement(name = "cost_to_proc")
	@JsonProperty("cost_to_proc")
	private double cost_to_proc = 0;

	@XmlElement(name = "unitcost")
	@JsonProperty("unitcost")
	private double unitcost = 0;

	@XmlElement(name = "status")
	@JsonProperty("status")
	private int status = 0;

	@XmlElement(name = "isprimary")
	@JsonProperty("isprimary")
	private int isprimary = 0;

	@XmlElement(name = "marketEndDate")
	@JsonProperty("marketEndDate")
	private String marketEndDate = "";

	@XmlElement(name = "createdby")
	@JsonProperty("createdby")
	private int createdby = 0;

	@XmlElement(name = "createdon")
	@JsonProperty("createdon")
	private String createdon = "";

	@XmlElement(name = "modifiedby")
	@JsonProperty("modifiedby")
	private int modifiedby = 0;

	@XmlElement(name = "modifiedon")
	@JsonProperty("modifiedon")
	private String modifiedon = "";

	@XmlElement(name = "userId")
	@JsonProperty("userId")
	private int userId = 0;

	@XmlElement(name = "notes")
	@JsonProperty("notes")
	private String notes = "";

	@XmlElement(name = "ndc")
	@JsonProperty("ndc")
	private String ndc = "";
	
	@XmlElement(name = "itemId")
	@JsonProperty("itemId")
	private String itemId = "";
	
	@XmlElement(name = "drugName")
	@JsonProperty("drugName")
	private String drugName = "";
	
	@XmlElement(name = "routedDrugId")
	@JsonProperty("routedDrugId")
	private String routedDrugId = "";
	
	@XmlElement(name = "lotDetails")
	@JsonProperty("lotDetails")
	private String lotDetails = "";
	
	@XmlElement(name = "productName")
	@JsonProperty("productName")
	private String productName = "";
	
	@XmlElement(name = "isVfc")
	@JsonProperty("isVfc")
	private int isVfc = 0;
	
	@XmlElement(name = "isSingleDosagePack")
	@JsonProperty("isSingleDosagePack")
	private int isSingleDosagePack = 0;
	
	
	
	public String getNdc10() {
		return ndc10;
	}

	public void setNdc10(String ndc10) {
		this.ndc10 = ndc10;
	}

	public String getAwup() {
		return awup;
	}

	public void setAwup(String awup) {
		this.awup = awup;
	}

	public String getRxnorm() {
		return rxnorm;
	}

	public void setRxnorm(String rxnorm) {
		this.rxnorm = rxnorm;
	}

	public String getUpc() {
		return upc;
	}

	public void setUpc(String upc) {
		this.upc = upc;
	}

	public int getIsSingleDosagePack() {
		return isSingleDosagePack;
	}

	public void setIsSingleDosagePack(int isSingleDosagePack) {
		this.isSingleDosagePack = isSingleDosagePack;
	}

	public int getIsVfc() {
		return isVfc;
	}

	public void setIsVfc(int isVfc) {
		this.isVfc = isVfc;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getRoutedDrugId() {
		return routedDrugId;
	}

	public void setRoutedDrugId(String routedDrugId) {
		this.routedDrugId = routedDrugId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getDrugName() {
		return drugName;
	}

	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	public String getPackType() {
		return packType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFormularyid() {
		return formularyid;
	}

	public void setFormularyid(int formularyid) {
		this.formularyid = formularyid;
	}

	public String getPpid() {
		return ppid;
	}

	public void setPpid(String ppid) {
		this.ppid = ppid;
	}

	public String getPackSize() {
		return packSize;
	}

	public void setPackSize(String packSize) {
		this.packSize = packSize;
	}

	public String getPackSizeUnitCode() {
		return packSizeUnitCode;
	}

	public void setPackSizeUnitCode(String packSizeUnitCode) {
		this.packSizeUnitCode = packSizeUnitCode;
	}

	public String getPackQuantity() {
		return packQuantity;
	}

	public void setPackQuantity(String packQuantity) {
		this.packQuantity = packQuantity;
	}

	public String getPackype() {
		return packType;
	}

	public void setPackType(String packType) {
		this.packType = packType;
	}

	public String getAwp() {
		return awp;
	}

	public void setAwp(String awp) {
		this.awp = awp;
	}

	public String getManufacturerName() {
		return manufacturerName;
	}

	public void setManufacturerName(String manufacturerName) {
		this.manufacturerName = manufacturerName;
	}

	public String getManufacturerIdentifier() {
		return manufacturerIdentifier;
	}

	public void setManufacturerIdentifier(String manufacturerIdentifier) {
		this.manufacturerIdentifier = manufacturerIdentifier;
	}

	public String getMvxCode() {
		return mvxCode;
	}

	public void setMvxCode(String mvxCode) {
		this.mvxCode = mvxCode;
	}

	public double getCost_to_proc() {
		return cost_to_proc;
	}

	public void setCost_to_proc(double cost_to_proc) {
		this.cost_to_proc = cost_to_proc;
	}

	public double getUnitcost() {
		return unitcost;
	}

	public void setUnitcost(double unitcost) {
		this.unitcost = unitcost;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getIsprimary() {
		return isprimary;
	}

	public void setIsprimary(int isprimary) {
		this.isprimary = isprimary;
	}

	public String getMarketEndDate() {
		return marketEndDate;
	}

	public void setMarketEndDate(String marketEndDate) {
		this.marketEndDate = marketEndDate;
	}

	public int getCreatedby() {
		return createdby;
	}

	public void setCreatedby(int createdby) {
		this.createdby = createdby;
	}

	public String getCreatedon() {
		return createdon;
	}

	public void setCreatedon(String createdon) {
		this.createdon = createdon;
	}

	public int getModifiedby() {
		return modifiedby;
	}

	public void setModifiedby(int modifiedby) {
		this.modifiedby = modifiedby;
	}

	public String getModifiedon() {
		return modifiedon;
	}

	public void setModifiedon(String modifiedon) {
		this.modifiedon = modifiedon;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getNdc() {
		return ndc;
	}

	public void setNdc(String ndc) {
		this.ndc = ndc;
	}
	

	public String getLotDetails() {
		return lotDetails;
	}

	public void setLotDetails(String lotDetails) {
		this.lotDetails = lotDetails;
	}

	// --------toString()---------------
	@Override
	public String toString() {
		return new StringBuilder("TemplateForAssoProducts [id=").append(id).append(",formularyid=").append(formularyid)
				.append(",ppid=").append(ppid).append(",packSize=").append(packSize).append(",packSizeUnitCode=")
				.append(packSizeUnitCode).append(",packQuantity=").append(packQuantity).append(",packType=")
				.append(packType).append(",awp=").append(awp).append(",manufacturerName=").append(manufacturerName)
				.append(",manufacturerIdentifier=").append(manufacturerIdentifier).append(",mvxCode=").append(mvxCode)
				.append(",cost_to_proc=").append(cost_to_proc).append(",unitcost=").append(unitcost).append(",status=")
				.append(status).append(",isprimary=").append(isprimary).append(",marketEndDate=").append(marketEndDate)
				.append(",createdby=").append(createdby).append(",createdon=").append(createdon).append(",modifiedby=")
				.append(modifiedby).append(",modifiedon=").append(modifiedon).append(",userId=").append(userId)
				.append(",notes=").append(notes)
				.append(",ndc=").append(ndc)
				.append(",itemId=").append(itemId)
				.append(",drugName=").append(drugName)
				.append(",routedDrugId=").append(routedDrugId)
				.append(",lotDetails=").append(lotDetails)
				.append(",productName=").append(productName)
				.append(",isVfc=").append(isVfc)
				.append(",rxnorm=").append(rxnorm)
				.append(",upc=").append(upc)
				.append(",awup=").append(awup)
				.append(",ndc10=").append(ndc10)
				.append(",isSingleDosagePack=").append(isSingleDosagePack)
				.append("]").toString();
	}

}