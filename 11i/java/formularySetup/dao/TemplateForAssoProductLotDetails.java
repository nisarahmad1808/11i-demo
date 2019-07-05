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
@XmlType(propOrder = { "id", "assoProductId", "formularyId", "lotno","lotType", "expiryDate", "delflag" })
// --------json property order---------------
@JsonPropertyOrder({ "id", "assoProductId", "formularyId", "lotno","lotType", "expiryDate", "delflag" })

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement

public class TemplateForAssoProductLotDetails {
	@JsonIgnoreProperties(ignoreUnknown = true)

	// --------class level properties---------------
	@XmlElement(name = "id")
	@JsonProperty("id")
	private String id = "";

	@XmlElement(name = "assoProductId")
	@JsonProperty("assoProductId")
	private String assoProductId = "";

	@XmlElement(name = "formularyId")
	@JsonProperty("formularyId")
	private String formularyId = "";

	@XmlElement(name = "lotno")
	@JsonProperty("lotno")
	private String lotno = "";
	
	@XmlElement(name = "lotType")
	@JsonProperty("lotType")
	private String lotType = "";

	@XmlElement(name = "expiryDate")
	@JsonProperty("expiryDate")
	private String expiryDate = "";

	@XmlElement(name = "delflag")
	@JsonProperty("delflag")
	private String delflag = "";
	
	

	public String getLotType() {
		return lotType;
	}

	public void setLotType(String lotType) {
		this.lotType = lotType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAssoProductId() {
		return assoProductId;
	}

	public void setAssoProductId(String assoProductId) {
		this.assoProductId = assoProductId;
	}

	public String getFormularyId() {
		return formularyId;
	}

	public void setFormularyId(String formularyId) {
		this.formularyId = formularyId;
	}

	public String getLotno() {
		return lotno;
	}

	public void setLotno(String lotno) {
		this.lotno = lotno;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getDelflag() {
		return delflag;
	}

	public void setDelflag(String delflag) {
		this.delflag = delflag;
	}

	// --------toString()---------------
	@Override
	public String toString() {
		return new StringBuilder("TemplateForAssoProductLotDetails [id=").append(id).append(",assoProductId=")
				.append(assoProductId)
				.append(",formularyId=").append(formularyId)
				.append(",lotno=").append(lotno)
				.append(",lotType=").append(lotType)
				.append(",expiryDate=").append(expiryDate)
				.append(",delflag=").append(delflag)
				.append("]").toString();
	}

}