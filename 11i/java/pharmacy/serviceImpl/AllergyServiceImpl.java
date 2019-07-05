package inpatientWeb.pharmacy.serviceImpl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;

import inpatientWeb.pharmacy.beans.Allergy;
import inpatientWeb.pharmacy.dao.AllergyDao;
import inpatientWeb.pharmacy.service.AllergyService;

/**
 * @author Dishagna Bhavsar
 * The Class AllergyServiceImpl.
 */
@Service
public class AllergyServiceImpl implements AllergyService {
	
	/** The allergy dao. */
	@Autowired
	AllergyDao allergyDao;

	/* (non-Javadoc)
	 * @see inpatientWeb.pharmacy.service.AllergyService#getAllergens(java.lang.String, int, java.lang.String)
	 */
	@Override
	public List<Allergy> getAllergens(String paramType, int paramId, String allergenType) {
		
		List<Allergy> allergenList = null;
		try{
			allergenList = allergyDao.getAllergens(paramType, paramId, allergenType);
		}catch(Exception e){
			EcwLog.AppendExceptionToLog(e);
		}
		return allergenList;
	}

	@Override
	public List<Map<String, Object>> getAllergensForInteraction(String paramType, long paramId, String allergenType) {
		List<Map<String,Object>> allergenList = null;
		try{
			allergenList = allergyDao.getAllergensForInteraction(paramType, paramId, allergenType);
		}catch(Exception e){
			EcwLog.AppendExceptionToLog(e);
		}
		return allergenList;
	}
}
