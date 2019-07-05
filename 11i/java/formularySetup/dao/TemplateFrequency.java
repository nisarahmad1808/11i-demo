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

@XmlType(propOrder = {"ext_mapping_id","ext_mapping_code","ext_mapping_desc"})
@JsonPropertyOrder({"ext_mapping_id","ext_mapping_code","ext_mapping_desc"})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class TemplateFrequency {
	
	@XmlElement(name="ext_mapping_id")
	@JsonProperty("ext_mapping_id")
    private String ext_mapping_id="";
	
	@XmlElement(name="ext_mapping_code")
	@JsonProperty("ext_mapping_code")
    private String ext_mapping_code="";
	
	@XmlElement(name="ext_mapping_desc")
	@JsonProperty("ext_mapping_desc")
    private String ext_mapping_desc="";

	
	public String getExt_mapping_code() {
		return ext_mapping_code;
	}


	public void setExt_mapping_code(String ext_mapping_code) {
		this.ext_mapping_code = ext_mapping_code;
	}


	public String getExt_mapping_desc() {
		return ext_mapping_desc;
	}


	public void setExt_mapping_desc(String ext_mapping_desc) {
		this.ext_mapping_desc = ext_mapping_desc;
	}

	

	public String getExt_mapping_id() {
		return ext_mapping_id;
	}


	public void setExt_mapping_id(String ext_mapping_id) {
		this.ext_mapping_id = ext_mapping_id;
	}


	@Override
	public String toString() {
		return "TemplateFrequency [ext_mapping_id="+ext_mapping_id+",ext_mapping_code=" + ext_mapping_code + ",ext_mapping_desc="+ext_mapping_desc+"]";
	}
}
