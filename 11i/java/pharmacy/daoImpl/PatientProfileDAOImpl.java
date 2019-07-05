package inpatientWeb.pharmacy.daoImpl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import inpatientWeb.pharmacy.dao.PatientProfileDAO;

/**
 * @author Dishagna Bhavsar
 * The Class PatientProfileDAOImpl.
 */
@Repository
@Scope("prototype")
@Lazy
public class PatientProfileDAOImpl implements PatientProfileDAO {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Override
	public String getPatientSexFromEncounterId(Long encId){

		String sex = "";
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("encounterId", encId);
		
		StringBuilder strSelectQuery = new StringBuilder();
		strSelectQuery.append("SELECT usr.sex ");
		strSelectQuery.append(" FROM  users AS usr ");
		strSelectQuery.append(" INNER JOIN enc encounter ON encounter.patientID = usr.uid ");   
		strSelectQuery.append(" WHERE encounter.encounterID = :encounterId ");
		strSelectQuery.append(" AND usr.delFlag =0 ");
		sex = namedParameterJdbcTemplate.queryForObject(strSelectQuery.toString(), paramMap, String.class);
		
		return sex;
	}
}
