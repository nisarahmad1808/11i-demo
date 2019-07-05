package inpatientWeb.pharmacy.service;

import java.util.List;
import java.util.Map;

import inpatientWeb.pharmacy.beans.Allergy;

/**
 * @author Dishagna Bhavsar
 * The Interface AllergyService.
 */
public interface AllergyService {

	/**
	 * Gets the Allergen based on paramter type.
	 *
	 * @param paramType the param type can be "patientId" or "encounterId"
	 * @param paramId the param id is parameter based on paramType
	 * @param allergnType the allergen type can be "DRUG","FOOD" and etc.
	 * @return the Lsit of Alergy object
	 */
	public List<Allergy> getAllergens(String paramType, int paramId, String allergnType);

	List<Map<String,Object>> getAllergensForInteraction(String keyEncounter, long moduleEncId, String allergenTypeDrug);

}
