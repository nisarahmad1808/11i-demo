package inpatientWeb.pharmacy.service;

import java.util.Map;

import inpatientWeb.pharmacy.beans.PatientProfile;

/**
 * @author Dishagna Bhavsar
 * The Interface PatientProfileService.
 */
public interface PatientProfileService {
	
	/**
	 * Gets the patient profile based on encounter id.
	 *
	 * @param encounterId the encounter id
	 * @return the patient profile
	 * @throws Exception the exception
	 */
	public PatientProfile getPatientProfile(long episodeEncId, long moduleEncId);
	
	public Map<String, Object> getFomattedPatientVitals(int episodeEncId);
	
	public enum WeightUnit {
		/**
		 * 1 KG = ? otherUnit (compareToKG)
		 * EX: 1 KG = 2.20462 LBS
		 */
		KG(1), 
		LBS(2.20462),
		GRAM(1000);
		
		private double compareToKG;
		
		WeightUnit(double compareToKG) {
			this.compareToKG = compareToKG;
		}
		
		public double getCompareToKG() {
			return this.compareToKG;
		}
	}
}
