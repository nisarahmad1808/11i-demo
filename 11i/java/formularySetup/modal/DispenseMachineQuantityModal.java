package inpatientWeb.admin.pharmacySettings.formularySetup.modal;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;


public class DispenseMachineQuantityModal implements Serializable,Cloneable,RowMapper<DispenseMachineQuantityModal>{  

	private static final long serialVersionUID = 1L;
	private String id;
    private String formularyId;
    private String disFomularyMapId;
    private String name;
    private String location;
    private String drugDispenseCode;
    private String dispenseMachineId;
    private String quantity;
    private String quantityCreatedBy;
	private String quantityCreatedTime;
	private String quantityModifiedBy;
	private String quantityModifiedTime;
	private String quantityModifiedDate;
	private String deleteFlag;
	private String  loginUserTimeZone;
	
	public String getDisFomularyMapId() {
		return disFomularyMapId;
	}
	public void setDisFomularyMapId(String disFomularyMapId) {
		this.disFomularyMapId = disFomularyMapId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getDrugDispenseCode() {
		return drugDispenseCode;
	}
	public void setDrugDispenseCode(String drugDispenseCode) {
		this.drugDispenseCode = drugDispenseCode;
	}
	public String getDispenseMachineId() {
		return dispenseMachineId;
	}
	public void setDispenseMachineId(String dispenseMachineId) {
		this.dispenseMachineId = dispenseMachineId;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getQuantityCreatedBy() {
		return quantityCreatedBy;
	}
	public void setQuantityCreatedBy(String quantityCreatedBy) {
		this.quantityCreatedBy = quantityCreatedBy;
	}
	public String getQuantityCreatedTime() {
		return quantityCreatedTime;
	}
	public void setQuantityCreatedTime(String quantityCreatedTime) {
		this.quantityCreatedTime = quantityCreatedTime;
	}
	public String getQuantityModifiedBy() {
		return quantityModifiedBy;
	}
	public void setQuantityModifiedBy(String quantityModifiedBy) {
		this.quantityModifiedBy = quantityModifiedBy;
	}
	public String getQuantityModifiedTime() {
		return quantityModifiedTime;
	}
	public void setQuantityModifiedTime(String quantityModifiedTime) {
		this.quantityModifiedTime = quantityModifiedTime;
	}
	
	public String getDeleteFlag() {
		return deleteFlag;
	}
	public void setDeleteFlag(String deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFormularyId() {
		return formularyId;
	}
	public void setFormularyId(String formularyId) {
		this.formularyId = formularyId;
	}
	public String getLoginUserTimeZone() {
		return loginUserTimeZone;
	}
	public void setLoginUserTimeZone(String loginUserTimeZone) {
		this.loginUserTimeZone = loginUserTimeZone;
	}
	public String getQuantityModifiedDate() {
		return quantityModifiedDate;
	}
	public void setQuantityModifiedDate(String quantityModifiedDate) {
		this.quantityModifiedDate = quantityModifiedDate;
	}
	@Override
	public DispenseMachineQuantityModal mapRow(ResultSet rs, int rowNum) throws SQLException {
		DispenseMachineQuantityModal templateForDispenseMachineAvailQuantity=new DispenseMachineQuantityModal();	
		/* templateForDispenseMachineAvailQuantity.setLoginUserTimeZone(rs.getString("loginUserTimeZone"));					     
		 templateForDispenseMachineAvailQuantity.setId(rs.getString("id"));
		 templateForDispenseMachineAvailQuantity.setFormularyId(rs.getString("formularyid"));
		 templateForDispenseMachineAvailQuantity.setDispenseMachineId(rs.getString("dispensemachineid"));
		 templateForDispenseMachineAvailQuantity.setName(rs.getString("name"));
		 templateForDispenseMachineAvailQuantity.setLocation(rs.getString("location"));
		 templateForDispenseMachineAvailQuantity.setDrugDispenseCode(rs.getString("drugdispensecode"));
		 templateForDispenseMachineAvailQuantity.setQuantity(rs.getString("quantity"));
		 templateForDispenseMachineAvailQuantity.setDeleteFlag(rs.getString("deleteflag"));
		 templateForDispenseMachineAvailQuantity.setQuantityCreatedBy(rs.getString("quantitycreatedby"));
		 templateForDispenseMachineAvailQuantity.setQuantityModifiedBy(rs.getString("quantitymodifiedby"));*/
		return templateForDispenseMachineAvailQuantity;
	}
}
