package inpatientWeb.pharmacy.dao;

import java.util.List;
import java.util.Map;

import inpatientWeb.pharmacy.beans.Allergy;

/**
 * @author Dishagna Bhavsar
 * The Interface AllergyDao.
 */
public interface AllergyDao {
	
	/**
	 * Gets the allergens.
	 *
	 * @param paramType the param type
	 * @param paramId the param id
	 * @param allergenType the allergen type
	 * @return the allergens
	 */
	public List<Allergy> getAllergens(String paramType, int paramId,String allergenType);
	
	List<Map<String,Object>> getAllergensForInteraction(String paramType, long paramId,String allergenType);

}
