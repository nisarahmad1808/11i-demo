package inpatientWeb.pharmacy.beans;

import org.springframework.stereotype.Component;

/**
 * The Class Allergy.
 */
@Component
public class Allergy {

	/** The patient id. */
	String patientId;
	
	/** The drug. */
	String drug;
	
	/** The allergy name. */
	String allergyName;
	
	/** The encounter id. */
	int encounterId;
	
	/** The item id. */
	int itemId;
	
	/** The ndc code. */
	String ndcCode;
	
	/** The allergy type. */
	int allergyType;
	
	/** The status. */
	String status;
	
	/** The drug name id. */
	String drugNameId;
	
	/**
	 * Gets the patient id.
	 *
	 * @return the patient id
	 */
	public String getPatientId() {
		return patientId;
	}
	
	/**
	 * Sets the patient id.
	 *
	 * @param patientId the new patient id
	 */
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	
	/**
	 * Gets the drug.
	 *
	 * @return the drug
	 */
	public String getDrug() {
		return drug;
	}
	
	/**
	 * Sets the drug.
	 *
	 * @param drug the new drug
	 */
	public void setDrug(String drug) {
		this.drug = drug;
	}
	
	/**
	 * Gets the allergy name.
	 *
	 * @return the allergy name
	 */
	public String getAllergyName() {
		return allergyName;
	}
	
	/**
	 * Sets the allergy name.
	 *
	 * @param allergyName the new allergy name
	 */
	public void setAllergyName(String allergyName) {
		this.allergyName = allergyName;
	}
	
	/**
	 * Gets the encounter id.
	 *
	 * @return the encounter id
	 */
	public int getEncounterId() {
		return encounterId;
	}
	
	/**
	 * Sets the encounter id.
	 *
	 * @param encounterId the new encounter id
	 */
	public void setEncounterId(int encounterId) {
		this.encounterId = encounterId;
	}
	
	/**
	 * Gets the item id.
	 *
	 * @return the item id
	 */
	public int getItemId() {
		return itemId;
	}
	
	/**
	 * Sets the item id.
	 *
	 * @param itemId the new item id
	 */
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	
	/**
	 * Gets the ndc code.
	 *
	 * @return the ndc code
	 */
	public String getNdcCode() {
		return ndcCode;
	}
	
	/**
	 * Sets the ndc code.
	 *
	 * @param ndcCode the new ndc code
	 */
	public void setNdcCode(String ndcCode) {
		this.ndcCode = ndcCode;
	}
	
	/**
	 * Gets the allergy type.
	 *
	 * @return the allergy type
	 */
	public int getAllergyType() {
		return allergyType;
	}
	
	/**
	 * Sets the allergy type.
	 *
	 * @param allergyType the new allergy type
	 */
	public void setAllergyType(int allergyType) {
		this.allergyType = allergyType;
	}
	
	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	
	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * Gets the drug name id.
	 *
	 * @return the drug name id
	 */
	public String getDrugNameId() {
		return drugNameId;
	}
	
	/**
	 * Sets the drug name id.
	 *
	 * @param drugNameId the new drug name id
	 */
	public void setDrugNameId(String drugNameId) {
		this.drugNameId = drugNameId;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Allergy [patientId=" + patientId + ", drug=" + drug + ", allergyName=" + allergyName + ", encounterId=" + encounterId + ", itemId=" + itemId + ", ndcCode=" + ndcCode + ", allergyType=" + allergyType + ", status=" + status + ", drugNameId=" + drugNameId + "]";
	}
}
