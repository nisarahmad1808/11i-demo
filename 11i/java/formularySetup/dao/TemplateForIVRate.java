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
@XmlType(propOrder={"id", "formularyid","ordertypesetupid","strength","strengthuom","duration","durationuom"})
//--------json property order---------------
@JsonPropertyOrder({"id","formularyid","ordertypesetupid","strength","strengthuom","duration","durationuom"})

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement

public class TemplateForIVRate{ 
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
			
			@XmlElement(name="strengthuom")
			@JsonProperty("strengthuom")
			private String strengthuom=null;
			
			@XmlElement(name="duration")
			@JsonProperty("duration")
			private String duration=null;
			
			@XmlElement(name="durationuom")
			@JsonProperty("durationuom")
			private String durationuom=null;
			
			
			public int getId() {
				return id;
			}

			public void setId(int id) {
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
			
			public String getDuration() {
			return duration; }
			
			public void setDuration(String duration){
			this.duration = duration;
			
			}
			
			public String getDurationuom() {
			return durationuom; }
			
			public void setDurationuom(String durationuom){
			this.durationuom = durationuom;

}


//--------toString()---------------
@Override
public String toString(){
return new StringBuilder("TemplateForIVRate [formularyid=").append(formularyid).append(",id=").append(id).append(",ordertypesetupid=").append(ordertypesetupid).append(",strength=").append(strength).append(",strengthuom=").append(strengthuom).append(",duration=").append(duration).append(",durationuom=").append(durationuom).append("]").toString();
}
}