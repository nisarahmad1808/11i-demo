package inpatientWeb.pharmacy.interfaces.outbound.dto;
/**
@author diveshg
*/

import java.util.Date;

public class DispensingInterfaceModel {
	
	private int interfaceId;
	private String interfaceName;
	private String interfaceType;
	private String connectionIp;
	private int connectionPort;
	private String connectionUrl;
	private Date modifiedTime;
	private int modifiedBy;
	private String mandatoryFields;
	
	private static DispensingInterfaceModel instance;
	
	private DispensingInterfaceModel() {
		
	}
	
	public static DispensingInterfaceModel getInterfaceDetailInstance(){
		if(instance == null){
			synchronized (DispensingInterfaceModel.class) 
            {
				instance = new DispensingInterfaceModel();
            }
		}
		return instance;
	}
	
	public int getInterfaceId() {
		return interfaceId;
	}
	public void setInterfaceId(int interfaceId) {
		this.interfaceId = interfaceId;
	}
	public String getInterfaceName() {
		return interfaceName;
	}
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	public String getInterfaceType() {
		return interfaceType;
	}
	public void setInterfaceType(String interfaceType) {
		this.interfaceType = interfaceType;
	}
	public String getConnectionIp() {
		return connectionIp;
	}
	public void setConnectionIp(String connectionIp) {
		this.connectionIp = connectionIp;
	}
	public int getConnectionPort() {
		return connectionPort;
	}
	public void setConnectionPort(int connectionPort) {
		this.connectionPort = connectionPort;
	}
	public String getConnectionUrl() {
		return connectionUrl;
	}
	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}
	public Date getModifiedTime() {
		return modifiedTime;
	}
	public void setModifiedTime(Date modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
	public int getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(int modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getMandatoryFields() {
		return mandatoryFields;
	}

	public void setMandatoryFields(String mandatoryFields) {
		this.mandatoryFields = mandatoryFields;
	}

	public static DispensingInterfaceModel getInstance() {
		return instance;
	}

	public static void setInstance(DispensingInterfaceModel instance) {
		DispensingInterfaceModel.instance = instance;
	}
	

	
}
