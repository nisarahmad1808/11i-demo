package inpatientWeb.admin.pharmacySettings.formularySetup.modal;


import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;



public class DispenseMachineModal implements Serializable,Cloneable,RowMapper<DispenseMachineModal>{
	
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String name;
	private String location;
	private List<String> machineNameList;
	private String departmentId;
	private String departmentName;
	private String createdDate;
	private String modifiedby;
	private String modifiedDate;
	private Character chargeOnDispense;
	private Character updateInvtentory;
	private Character useCharge;
	private String deleteflag;
	private Character status;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getMachineNameList() {
		return machineNameList;
	}
	public void setMachineNameList(List<String> machineNameList) {
		this.machineNameList = machineNameList;
	}
	public String getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getModifiedby() {
		return modifiedby;
	}
	public void setModifiedby(String modifiedby) {
		this.modifiedby = modifiedby;
	}
	public String getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public Character getChargeOnDispense() {
		return chargeOnDispense;
	}
	public void setChargeOnDispense(Character chargeOnDispense) {
		this.chargeOnDispense = chargeOnDispense;
	}
	public Character getUpdateInvtentory() {
		return updateInvtentory;
	}
	public void setUpdateInvtentory(Character updateInvtentory) {
		this.updateInvtentory = updateInvtentory;
	}
	public Character getUseCharge() {
		return useCharge;
	}
	public void setUseCharge(Character useCharge) {
		this.useCharge = useCharge;
	}
	public String getDeleteflag() {
		return deleteflag;
	}
	public void setDeleteflag(String deleteflag) {
		this.deleteflag = deleteflag;
	}
	public Character getStatus() {
		return status;
	}
	public void setStatus(Character status) {
		this.status = status;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	@Override
	public DispenseMachineModal mapRow(ResultSet rs, int rowNum) throws SQLException {
		DispenseMachineModal dispenseMachineInfo=new DispenseMachineModal();
		dispenseMachineInfo.setId(rs.getLong("id"));
		dispenseMachineInfo.setName(rs.getString("name"));
		dispenseMachineInfo.setLocation(rs.getString("location"));
		dispenseMachineInfo.setModifiedby(rs.getString("modifiedby"));		
		dispenseMachineInfo.setStatus(rs.getString("status").charAt(0));		
		return dispenseMachineInfo;
	}
}
