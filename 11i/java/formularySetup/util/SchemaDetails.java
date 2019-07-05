package inpatientWeb.admin.pharmacySettings.formularySetup.util;

public class SchemaDetails {
	private String tableName;
	private String columnName;
	private String deleteColumnName;
	
	public SchemaDetails(String tableName,String columnName,String deleteColumnName)
	{
		this.tableName = tableName;
		this.columnName = columnName;
		this.deleteColumnName = deleteColumnName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getDeleteColumnName() {
		return deleteColumnName;
	}

	public void setDeleteColumnName(String deleteColumnName) {
		this.deleteColumnName = deleteColumnName;
	}
}
