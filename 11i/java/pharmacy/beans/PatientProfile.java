package inpatientWeb.pharmacy.beans;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
/**
 * The Class PatientProfile.
 */
@Component
public class PatientProfile {

	/** The age in days. */
	private int ageInDays;
	
	/** The body surface area. */
	private double bodySurfaceArea;
	
	/** The gender. */
	private String gender;
	
	/** The height in cms. */
	private double heightInCms;
	
	/** The height in inches. */
	private double heightInInches;
	
	/** The is lactating. */
	private boolean lactating=false;
	
	/** The is pregnant. */
	private boolean pregnant=false;
	
	/** The patient problems. */
	private List<Map<String, String>> patientProblems;
	
	/** The weight in kgs. */
	private double weightInKgs;
	
	/** The weight in lbs. */
	private double weightInLbs;
	
	private List<Map<String,Object>> allergyList;
	
	private List<RenalFunction> renalFunctionList;
	/**
	 * Gets the age in days.
	 *
	 * @return the age in days
	 */
	public int getAgeInDays() {
		return ageInDays;
	}
	
	/**
	 * Sets the age in days.
	 *
	 * @param ageInDays the new age in days
	 */
	public void setAgeInDays(int ageInDays) {
		this.ageInDays = ageInDays;
	}
	
	/**
	 * Gets the body surface area.
	 *
	 * @return the body surface area
	 */
	public double getBodySurfaceArea() {
		return bodySurfaceArea;
	}
	
	/**
	 * Sets the body surface area.
	 *
	 * @param bodySurfaceArea the new body surface area
	 */
	public void setBodySurfaceArea(double bodySurfaceArea) {
		this.bodySurfaceArea = bodySurfaceArea;
	}
	
	/**
	 * Gets the gender.
	 *
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}
	
	/**
	 * Sets the gender.
	 *
	 * @param gender the new gender
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	/**
	 * Gets the height in cms.
	 *
	 * @return the height in cms
	 */
	public double getHeightInCms() {
		return heightInCms;
	}
	
	/**
	 * Sets the height in cms.
	 *
	 * @param heightInCms the new height in cms
	 */
	public void setHeightInCms(double heightInCms) {
		this.heightInCms = heightInCms;
	}
	
	/**
	 * Gets the height in inches.
	 *
	 * @return the height in inches
	 */
	public double getHeightInInches() {
		return heightInInches;
	}
	
	/**
	 * Sets the height in inches.
	 *
	 * @param heightInInches the new height in inches
	 */
	public void setHeightInInches(double heightInInches) {
		this.heightInInches = heightInInches;
	}
	
	
	
	public boolean isLactating() {
		return lactating;
	}

	public void setLactating(boolean lactating) {
		this.lactating = lactating;
	}

	public boolean isPregnant() {
		return pregnant;
	}

	public void setPregnant(boolean pregnant) {
		this.pregnant = pregnant;
	}

	/**
	 * Gets the patient problems.
	 *
	 * @return the patient problems
	 */
	public List<Map<String, String>> getPatientProblems() {
		return patientProblems;
	}
	
	/**
	 * Sets the patient problems.
	 *
	 * @param patientProblems the patient problems
	 */
	public void setPatientProblems(List<Map<String, String>> patientProblems) {
		this.patientProblems = patientProblems;
	}
	
	/**
	 * Gets the weight in kgs.
	 *
	 * @return the weight in kgs
	 */
	public double getWeightInKgs() {
		return weightInKgs;
	}
	
	/**
	 * Sets the weight in kgs.
	 *
	 * @param weightInKgs the new weight in kgs
	 */
	public void setWeightInKgs(double weightInKgs) {
		this.weightInKgs = weightInKgs;
	}
	
	/**
	 * Gets the weight in lbs.
	 *
	 * @return the weight in lbs
	 */
	public double getWeightInLbs() {
		return weightInLbs;
	}
	
	/**
	 * Sets the weight in lbs.
	 *
	 * @param weightInLbs the new weight in lbs
	 */
	public void setWeightInLbs(double weightInLbs) {
		this.weightInLbs = weightInLbs;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	
	public List<Map<String, Object>> getAllergyList() {
		return allergyList;
	}

	public void setAllergyList(List<Map<String, Object>> allergyList) {
		this.allergyList = allergyList;
	}

	public List<RenalFunction> getRenalFunctionList() {
		return renalFunctionList;
	}

	public void setRenalFunctionList(List<RenalFunction> renalFunctionList) {
		this.renalFunctionList = renalFunctionList;
	}
	
}
