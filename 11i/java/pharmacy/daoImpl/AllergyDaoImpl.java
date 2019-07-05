package inpatientWeb.pharmacy.daoImpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;

import inpatientWeb.pharmacy.beans.Allergy;
import inpatientWeb.pharmacy.dao.AllergyDao;

/**
 * @author Dishagna Bhavsar
 * The Class AllergyDaoImpl.
 */
@Component
public class AllergyDaoImpl implements AllergyDao {
	
	/** The named parameter jdbc template. */
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/** The allergy. */
	@Autowired
	Allergy allergy;
	
	/* Constants */
	private static final String ALLERGEN_TYPE_DRUG = "drug";
	private static final String ALLERGEN_TYPE_FOOD = "food";
	private static final String STATUS_ACTIVE = "active";
	private static final String KEY_ENCOUNTER = "encounter";
	private static final String KEY_PATIENT = "patient";

	/* Constructors */
	public AllergyDaoImpl() {
		// Duplicate Code
		//namedParameterJdbcTemplate = (NamedParameterJdbcTemplate) EcwAppContext.getObject("namedParameterJdbcTemplate");
	}

	/* Methods*/
	
	/* (non-Javadoc)
	 * @see inpatientWeb.pharmacy.dao.AllergyDao#getAllergens(java.lang.String, int, java.lang.String)
	 */
	@Override
	public List<Allergy> getAllergens(String paramType, int paramId, String allergenType){
		List<Allergy> allergenList = null;
		try {
			StringBuilder strSql = new StringBuilder();
			strSql.append("select a.patientID,a.drug,a.allergy,a.encounterId,a.itemID,i.DrugNameId,a.ndc_code,a.allergytype,a.status from ip_allergies a ");  
			strSql.append(" INNER JOIN items i on a.itemID=i.itemID ");
			strSql.append(" where LOWER(status) = :status ");
			if(KEY_ENCOUNTER.equalsIgnoreCase(paramType)){
				strSql.append(" AND encounterId = :paramId ");
			}else if(KEY_PATIENT.equalsIgnoreCase(paramType)){
				strSql.append(" AND  patientID = :paramId ");
			}
			
			if(ALLERGEN_TYPE_DRUG.equals(allergenType)){
				strSql.append(" AND type = 1 ");
			}else if(ALLERGEN_TYPE_FOOD.equals(allergenType)){
				strSql.append(" AND type = 2 ");
			}
			strSql.append(" AND a.deleteFlag = 0 ");
			Map<String, Object> mapParam = new HashMap<>();
			mapParam.put("paramId", paramId);
			mapParam.put("status", STATUS_ACTIVE);
			allergenList = namedParameterJdbcTemplate.query(strSql.toString(), mapParam,new ResultSetExtractor<List<Allergy>>() {
				@Override
				public List<Allergy> extractData(ResultSet rs) throws SQLException, DataAccessException {
					Allergy allergyObj = null;
					List<Allergy>allergies = new ArrayList<>();
					if (rs.next()) {
						allergyObj = new Allergy();
						allergyObj.setEncounterId(rs.getInt("encounterId"));
						allergyObj.setPatientId(rs.getString("patientID"));
						allergyObj.setItemId(rs.getInt("itemID"));
						allergyObj.setNdcCode(rs.getString("ndc_code"));
						allergyObj.setAllergyType(rs.getInt("allergytype"));
						allergyObj.setAllergyName(rs.getString("allergy"));
						allergyObj.setDrug(rs.getString("drug"));
						allergyObj.setDrugNameId(rs.getString("DrugNameId"));
						allergies.add(allergyObj);
					}
					return allergies;
				}
			});
			
		} catch (Exception ex) {
			EcwLog.AppendToLog(" Exception in AllergyDaoImpl.getAllergens");
			EcwLog.AppendExceptionToLog(ex);
		}
		return allergenList;
	}

	@Override
	public List<Map<String, Object>> getAllergensForInteraction(String paramType, long paramId, String allergenType) {
		List<Map<String,Object>> allergenList = null;
		try {
			StringBuilder strSql = new StringBuilder();
			strSql.append("select a.patientID,a.drug,a.allergy,a.encounterId,a.itemID,i.DrugNameId,a.ndc_code,a.allergytype,a.status from ip_allergies a ");  
			strSql.append(" INNER JOIN items i on a.itemID=i.itemID ");
			strSql.append(" where LOWER(status) = :status ");
			if(KEY_ENCOUNTER.equalsIgnoreCase(paramType)){
				strSql.append(" AND encounterId = :paramId ");
			}else if(KEY_PATIENT.equalsIgnoreCase(paramType)){
				strSql.append(" AND  patientID = :paramId ");
			}
			
			if(ALLERGEN_TYPE_DRUG.equals(allergenType)){
				strSql.append(" AND type = 1 ");
			}else if(ALLERGEN_TYPE_FOOD.equals(allergenType)){
				strSql.append(" AND type = 2 ");
			}
			strSql.append(" AND a.deleteFlag = 0 ");
			Map<String, Object> mapParam = new HashMap<>();
			mapParam.put("paramId", paramId);
			mapParam.put("status", STATUS_ACTIVE);
			allergenList = namedParameterJdbcTemplate.query(strSql.toString(), mapParam,new ResultSetExtractor<List<Map<String,Object>>>() {
				@Override
				public List<Map<String,Object>> extractData(ResultSet rs) throws SQLException, DataAccessException {
					Map<String,Object> allergyObj = null;
					List<Map<String,Object>>allergies = new ArrayList<>();
					while (rs.next()) {
						allergyObj = new HashMap<>();
						allergyObj.put("id", rs.getInt("DrugNameId"));
						allergyObj.put("idType", "drugNameId");
						allergies.add(allergyObj);
					}
					return allergies;
				}
			});
			
		} catch (Exception ex) {
			EcwLog.AppendToLog(" Exception in AllergyDaoImpl.getAllergens");
			EcwLog.AppendExceptionToLog(ex);
		}
		return allergenList;
	}
}

