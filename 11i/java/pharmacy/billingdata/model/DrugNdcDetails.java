package inpatientWeb.pharmacy.billingdata.model;

/**
 * @author Bharat Tulsiyani
 * @Copyright ©eClinicalWorks LLC.
 * @Date Feb 15, 2018
 */
public class DrugNdcDetails {
	
	private String ndc;
	
	private double dispense;

	public String getNdc() {
		return ndc;
	}

	public void setNdc(String ndc) {
		this.ndc = ndc;
	}

	public double getDispense() {
		return dispense;
	}

	public void setDispense(double dispense) {
		this.dispense = dispense;
	}

	@Override
	public String toString() {
		return "DrugNdcDetails [ndc=" + ndc + ", dispense=" + dispense + "]";
	}
	
}