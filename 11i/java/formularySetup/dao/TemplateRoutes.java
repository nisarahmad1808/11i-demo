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

@XmlType(propOrder = {"id", "routeID", "routeName", "routeDesc","extMappingId", "isRecommended"})
@JsonPropertyOrder({"id", "routeID", "routeName",  "routeDesc","extMappingId", "isRecommended"})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class TemplateRoutes {
	
	@XmlElement(name="id")
	@JsonProperty("id")
    private int id=-1;
	
	@XmlElement(name="routeID")
	@JsonProperty("routeID")
    private String routeID="";
	
	@XmlElement(name="routeName")
	@JsonProperty("routeName")
    private String routeName="";
	
	@XmlElement(name="routeDesc")
	@JsonProperty("routeDesc")
    private String routeDesc="";
	
	@XmlElement(name="extMappingId")
	@JsonProperty("extMappingId")
    private String extMappingId="";
	
	@XmlElement(name="isRecommended")
	@JsonProperty("isRecommended")
    private String isRecommended="";
	
	public String getExtMappingId() {
		return extMappingId;
	}

	public void setExtMappingId(String extMappingId) {
		this.extMappingId = extMappingId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
	
	public String getRouteDesc() {
		return routeDesc;
	}

	public void setRouteDesc(String routeDesc) {
		this.routeDesc = routeDesc;
	}
	
	
	public String getIsRecommended() {
		return isRecommended;
	}

	public void setIsRecommended(String isRecommended) {
		this.isRecommended = isRecommended;
	}

	@Override
	public String toString() {
		return new StringBuilder("TemplateRoutes [id=").append(id)
				.append(", routeID=").append(routeID)
				.append(", routeName=").append(routeName)
				.append(", routeDesc=").append(routeDesc)
				.append(", extMappingId=").append(extMappingId)
				.append(", isRecommended=").append(isRecommended)
				.append("]").toString();
	}
    
}
