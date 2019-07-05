package inpatientWeb.pharmacy.drugdb.model;

public class DrugFormulation {
	
	private String doseFormId;
	private String formulationName;
	
	public String getDoseFormId() {
		return doseFormId;
	}
	public void setDoseFormId(String doseFormId) {
		this.doseFormId = doseFormId;
	}
	public String getFormulationName() {
		return formulationName;
	}
	public void setFormulationName(String formulationName) {
		this.formulationName = formulationName;
	}
	@Override
	public String toString() {
		return "DrugFormulation [doseFormId=" + doseFormId + ", formulationName=" + formulationName + "]";
	}
}
