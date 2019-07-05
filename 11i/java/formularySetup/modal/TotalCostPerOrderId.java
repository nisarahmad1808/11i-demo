package inpatientWeb.admin.pharmacySettings.formularySetup.modal;

import java.util.List;

public class TotalCostPerOrderId {
	
	private long orderId;
	private double totalCost;
	private double totalHcpcsUnit;
	private List<CostPerFormulary> costProductList;
	private List<CostPerFormulary> ndcCodeList;
	private boolean isPrimaryNDCCost;
	
	public long getOrderId() {
		return orderId;
	}
	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	public double getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}
	public List<CostPerFormulary> getCostProductList() {
		return costProductList;
	}
	public void setCostProductList(List<CostPerFormulary> costProductList) {
		this.costProductList = costProductList;
	}
	public List<CostPerFormulary> getNdcCodeList() {
		return ndcCodeList;
	}
	public void setNdcCodeList(List<CostPerFormulary> ndcCodeList) {
		this.ndcCodeList = ndcCodeList;
	}
	public boolean isPrimaryNDCCost() {
		return isPrimaryNDCCost;
	}
	public void setPrimaryNDCCost(boolean isPrimaryNDCCost) {
		this.isPrimaryNDCCost = isPrimaryNDCCost;
	}
	public double getTotalHcpcsUnit() {
		return totalHcpcsUnit;
	}
	public void setTotalHcpcsUnit(double totalHcpcsUnit) {
		this.totalHcpcsUnit = totalHcpcsUnit;
	}
	
	
}
