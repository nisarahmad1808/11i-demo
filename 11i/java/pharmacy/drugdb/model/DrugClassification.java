package inpatientWeb.pharmacy.drugdb.model;

public class DrugClassification{

	String drugClassName ;
	String drugClassID ;
	String drugClassType ;
	
	public String getDrugClassName() {
		return drugClassName;
	}

	public void setDrugClassName(String drugClassName) {
		this.drugClassName = drugClassName;
	}

	public String getDrugClassID() {
		return drugClassID;
	}

	public void setDrugClassID(String drugClassID) {
		this.drugClassID = drugClassID;
	}

	public String getDrugClassType() {
		return drugClassType;
	}

	public void setDrugClassType(String drugClassType) {
		this.drugClassType = drugClassType;
	}

	@Override
	public String toString() {
		return "DrugClassification [drugClassName=" + drugClassName + ", drugClassID=" + drugClassID
				+ ", drugClassType=" + drugClassType + "]";
	}
}
