package inpatientWeb.pharmacy.billingdata.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

/**
 * @author Bharat Tulsiyani
 * @Copyright ©eClinicalWorks LLC.
 * @Date Feb 10, 2018
 */
public class PharmacyBillingDetail implements Serializable, RowMapper<PharmacyBillingDetail> {
	
	private static final long serialVersionUID = -7229041279059154952L;
	
	private int id;
	private Integer billingDataId;
	private Integer formularyId;
	private Long orderId;
	private String ndcCode;
	private long eMARDrugAdminScheduleId;
	private String action;
	private int successFlag;
	private double dispense;
	private int createdBy;
	private int modifiedBy;
	private Date createdDate;
	private Date modifiedDate;
	private int deleteFlag;
	private String moduleType;
	private double costToProcure;
	private double awup;
	private Integer priceRuleId;
	private double fee;
	private Integer cptItemId;
	
	public PharmacyBillingDetail() {
		super();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Integer getBillingDataId() {
		return billingDataId;
	}
	public void setBillingDataId(Integer billingDataId) {
		this.billingDataId = billingDataId;
	}
	public Integer getFormularyId() {
		return formularyId;
	}
	public void setFormularyId(Integer formularyId) {
		this.formularyId = formularyId;
	}
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public String getNdcCode() {
		return ndcCode;
	}
	public void setNdcCode(String ndcCode) {
		this.ndcCode = ndcCode;
	}
	public long geteMARDrugAdminScheduleId() {
		return eMARDrugAdminScheduleId;
	}
	public void seteMARDrugAdminScheduleId(long eMARDrugAdminScheduleId) {
		this.eMARDrugAdminScheduleId = eMARDrugAdminScheduleId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public int getSuccessFlag() {
		return successFlag;
	}
	public void setSuccessFlag(int successFlag) {
		this.successFlag = successFlag;
	}
	public double getDispense() {
		return dispense;
	}
	public void setDispense(double dispense) {
		this.dispense = dispense;
	}
	public int getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}
	public int getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(int modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Date getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public int getDeleteFlag() {
		return deleteFlag;
	}
	public void setDeleteFlag(int deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	
	public String getModuleType() {
		return moduleType;
	}
	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}
	public double getAwup() {
		return awup;
	}
	public void setAwup(double awup) {
		this.awup = awup;
	}
	public double getFee() {
		return fee;
	}
	public void setFee(double fee) {
		this.fee = fee;
	}
	public double getCostToProcure() {
		return costToProcure;
	}
	public void setCostToProcure(double costToProcure) {
		this.costToProcure = costToProcure;
	}
	public Integer getPriceRuleId() {
		return priceRuleId;
	}
	public void setPriceRuleId(Integer priceRuleId) {
		this.priceRuleId = priceRuleId;
	}
	public Integer getCptItemId() {
		return cptItemId;
	}
	public void setCptItemId(Integer cptItemId) {
		this.cptItemId = cptItemId;
	}
	@Override
	public String toString() {
		return "PharmacyBillingDetail [id=" + id + ", billingDataId=" + billingDataId + ", formularyId=" + formularyId
				+ ", orderId=" + orderId + ", ndcCode=" + ndcCode + ", eMARDrugAdminScheduleId="
				+ eMARDrugAdminScheduleId + ", action=" + action + ", createdBy=" + createdBy + ", modifiedBy="
				+ modifiedBy + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + ", deleteFlag="
				+ deleteFlag + "]";
	}
	@Override
	public PharmacyBillingDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
		PharmacyBillingDetail pharmacyBillingDetails = new PharmacyBillingDetail();
		pharmacyBillingDetails.setId(rs.getInt("id"));
		pharmacyBillingDetails.setBillingDataId(rs.getInt("billingDataId"));
		pharmacyBillingDetails.setFormularyId(rs.getInt("formularyId"));
		pharmacyBillingDetails.setOrderId(rs.getLong("orderId"));
		pharmacyBillingDetails.setNdcCode(rs.getString("ndcCode"));
		pharmacyBillingDetails.seteMARDrugAdminScheduleId(rs.getInt("emarDrugScheduleId"));
		pharmacyBillingDetails.setAction(rs.getString("action"));
		pharmacyBillingDetails.setSuccessFlag(rs.getInt("successFlag"));
		pharmacyBillingDetails.setDispense(rs.getDouble("dispense"));
		pharmacyBillingDetails.setCreatedBy(rs.getInt("createdBy"));
		pharmacyBillingDetails.setCreatedDate(rs.getDate("createdDate"));
		pharmacyBillingDetails.setModifiedBy(rs.getInt("modifiedBy"));
		pharmacyBillingDetails.setModifiedDate(rs.getDate("modifiedDate"));
		pharmacyBillingDetails.setDeleteFlag(rs.getInt("deleteFlag"));
		return pharmacyBillingDetails;
	}	
}