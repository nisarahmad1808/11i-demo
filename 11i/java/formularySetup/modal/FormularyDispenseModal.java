package inpatientWeb.admin.pharmacySettings.formularySetup.modal;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;


public class FormularyDispenseModal implements Serializable,Cloneable,RowMapper<FormularyDispenseModal>{  
	
	private static final long serialVersionUID = 1L;
    private long dispenseFormularyMappingId;
    private String formularyId="";
    private String drugdispenseCode="";
    private String quantity="";
    private String parValue="";
	private String createdTime="";
	private String createdBy="";
	private String modifiedBy="";
	private String modifiedTime="";
	private String deleteFlag;
	private String status;
	private String modifiedDate="";
	private List<DispenseMachineQuantityModal> dispenseMachineAvailQuantities;
	private List<DispenseMachineModal> forDispenseMachineInfos;
	private String loginUserTimeZone="";
	
	public long getDispenseFormularyMappingId() {
		return dispenseFormularyMappingId;
	}
	
	public String getFormularyId() {
		return formularyId;
	}

	public void setFormularyId(String formularyId) {
		this.formularyId = formularyId;
	}

	public String getDrugdispenseCode() {
		return drugdispenseCode;
	}

	public void setDrugdispenseCode(String drugdispenseCode) {
		this.drugdispenseCode = drugdispenseCode;
	}

	public String getParValue() {
		return parValue;
	}

	public void setParValue(String parValue) {
		this.parValue = parValue;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(String modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public String getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(String deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public List<DispenseMachineQuantityModal> getDispenseMachineAvailQuantities() {
		return dispenseMachineAvailQuantities;
	}

	public void setDispenseMachineAvailQuantities(List<DispenseMachineQuantityModal> dispenseMachineAvailQuantities) {
		this.dispenseMachineAvailQuantities = dispenseMachineAvailQuantities;
	}

	public List<DispenseMachineModal> getForDispenseMachineInfos() {
		return forDispenseMachineInfos;
	}

	public void setForDispenseMachineInfos(List<DispenseMachineModal> forDispenseMachineInfos) {
		this.forDispenseMachineInfos = forDispenseMachineInfos;
	}

	public String getLoginUserTimeZone() {
		return loginUserTimeZone;
	}

	public void setLoginUserTimeZone(String loginUserTimeZone) {
		this.loginUserTimeZone = loginUserTimeZone;
	}

	public void setDispenseFormularyMappingId(long dispenseFormularyMappingId) {
		this.dispenseFormularyMappingId = dispenseFormularyMappingId;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public FormularyDispenseModal mapRow(ResultSet rs, int rowNum) throws SQLException {
		FormularyDispenseModal tmpl=new FormularyDispenseModal();
		tmpl.setDispenseFormularyMappingId(rs.getLong("id"));
		tmpl.setFormularyId(rs.getString("formularyid"));
		tmpl.setDrugdispenseCode(rs.getString("drugdispensecode"));
		tmpl.setQuantity(rs.getString("quantity"));
		tmpl.setCreatedBy(rs.getString("createdby"));
		return tmpl;
	}

}
