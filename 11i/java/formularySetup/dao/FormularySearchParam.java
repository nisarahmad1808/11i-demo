package inpatientWeb.admin.pharmacySettings.formularySetup.dao;

public class FormularySearchParam 
{
	private String searchBy = "name";
	private String searchValue = "";
	private String status = "";
	private String sortOrder = "asc";
	private String sortBy = "";
	private int recordsPerPage = 20;
	private int selectedPage = 1;
	
	public FormularySearchParam(){}
	public String getSearchBy() {
		return searchBy;
	}
	public void setSearchBy(String searchBy) {
		this.searchBy = searchBy;
	}
	public String getSearchValue() { 
		return searchValue;
	}
	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}
	public String getSortBy() {
		return sortBy;
	}
	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}
	public int getRecordsPerPage() {
		return recordsPerPage;
	}
	public void setRecordsPerPage(int recordsPerPage) {
		this.recordsPerPage = recordsPerPage;
	}
	public int getSelectedPage() {
		return selectedPage;
	}
	public void setSelectedPage(int selectedPage) {
		this.selectedPage = selectedPage;
	}
	
}
