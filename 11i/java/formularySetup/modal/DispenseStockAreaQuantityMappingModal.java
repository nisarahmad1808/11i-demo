package inpatientWeb.admin.pharmacySettings.formularySetup.modal;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;


public class DispenseStockAreaQuantityMappingModal implements Serializable,Cloneable,RowMapper<DispenseStockAreaQuantityMappingModal>{  

	private static final long serialVersionUID = 1L;
	private String dispenseQtyId;
    private String formularyId;
    private String dispenseFomularyId;
    private String dispenseStockId;
    private String name;
    private String location;
    private String drugDispenseCode;
    private String quantity;
    private String createdBy;
	private String createdTime;
	private String modifiedby;
	private String modifiedTime;
	private String modifiedDate;
	private String deleteFlag;
	private String  loginUserTimeZone;
	
	
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
	
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	
	
	public String getDeleteFlag() {
		return deleteFlag;
	}
	public void setDeleteFlag(String deleteFlag) {
		this.deleteFlag = deleteFlag;
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
	
	public String getDispenseQtyId() {
		return dispenseQtyId;
	}
	public void setDispenseQtyId(String dispenseQtyId) {
		this.dispenseQtyId = dispenseQtyId;
	}
	public String getDispenseFomularyId() {
		return dispenseFomularyId;
	}
	public void setDispenseFomularyId(String dispenseFomularyId) {
		this.dispenseFomularyId = dispenseFomularyId;
	}
	public String getDispenseStockId() {
		return dispenseStockId;
	}
	public void setDispenseStockId(String dispenseStockId) {
		this.dispenseStockId = dispenseStockId;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}
	public String getModifiedby() {
		return modifiedby;
	}
	public void setModifiedby(String modifiedby) {
		this.modifiedby = modifiedby;
	}
	public String getModifiedTime() {
		return modifiedTime;
	}
	public void setModifiedTime(String modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
	public String getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	@Override
	public DispenseStockAreaQuantityMappingModal mapRow(ResultSet rs, int rowNum) throws SQLException {
		DispenseStockAreaQuantityMappingModal templateForDispenseMachineAvailQuantity=new DispenseStockAreaQuantityMappingModal();	
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
