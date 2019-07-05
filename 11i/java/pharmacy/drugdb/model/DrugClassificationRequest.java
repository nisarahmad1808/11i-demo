package inpatientWeb.pharmacy.drugdb.model;

public class DrugClassificationRequest{

	private String classType;

	public String getClassType() {
		return classType;
	}

	public void setClassType(String classType) {
		this.classType = classType;
	}

	@Override
	public String toString() {
		return "DrugClassificationRequest [classType=" + classType + "]";
	}
}
