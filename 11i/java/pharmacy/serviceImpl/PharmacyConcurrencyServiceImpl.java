package inpatientWeb.pharmacy.serviceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import inpatientWeb.Auth.exception.InvalidParameterException;
import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;

import inpatientWeb.pharmacy.beans.PharmacyVerificationSessionLog;
import inpatientWeb.pharmacy.dao.PharmacyConcurrencyDAO;
import inpatientWeb.pharmacy.service.PharmacyConcurrencyService;
import inpatientWeb.utils.IPTzUtils;

@Service
public class PharmacyConcurrencyServiceImpl implements PharmacyConcurrencyService {
	
	public static final int THREE_HOURS_MILLISECONDS = 10800000;

	@Autowired
	PharmacyConcurrencyDAO pharmacyConcurrencyDAO;

	public PharmacyVerificationSessionLog getConcurrentUserLog(int orderId, int patientId, int userId){
		
		if(orderId <= 0){
			throw new InvalidParameterException("Invalid orderId");
		}
		if(patientId <= 0){
			throw new InvalidParameterException("Invalid patientId");
		}
		if(userId <= 0){
			throw new InvalidParameterException("Invalid userId");
		}

		PharmacyVerificationSessionLog phSessionLog = new PharmacyVerificationSessionLog();
		try{
			Map<String, Object> filterMap = new HashMap<String, Object>();
			filterMap.put("orderId", orderId);
			filterMap.put("patientId", patientId);
			filterMap.put("userId", userId);

			if(pharmacyConcurrencyDAO.getPharmacyVerificationSessionLogCount(filterMap) > 0){
				phSessionLog = pharmacyConcurrencyDAO.readPharmacyVerificationSessionLog(filterMap);

				SimpleDateFormat dateFormat  = new SimpleDateFormat( IPTzUtils.DEFAULT_DB_DT_FMT);
				Date lockedTime = dateFormat.parse(phSessionLog.getLockedTime());

				Date currentTime = IPTzUtils.getUTCNow();

				if(currentTime.getTime() - lockedTime.getTime() > THREE_HOURS_MILLISECONDS ){
					phSessionLog.setAllowContinue(true);
				}

			}
		}catch(ParseException e){
			EcwLog.AppendExceptionToLog(e);
		}
		return phSessionLog;
	}

	public boolean createPharmacyVerificationSessionLog(int orderId, int patientId, int loggedInUserId){
		
		if(orderId <= 0){
			throw new InvalidParameterException("Invalid orderId");
		}
		if(patientId <= 0){
			throw new InvalidParameterException("Invalid patientId");
		}
		if(loggedInUserId <= 0){
			throw new InvalidParameterException("Invalid userId");
		}
		
		PharmacyVerificationSessionLog sessionLog = new PharmacyVerificationSessionLog();
		sessionLog.setOrderId(orderId);
		sessionLog.setPatientId(patientId);
		sessionLog.setUserId(loggedInUserId);

		return pharmacyConcurrencyDAO.createPharmacyVerificationSessionLog(sessionLog);

	}

	public boolean clearSessionLog(int orderId, int patientId, int userId){
		
		if(orderId <= 0){
			throw new InvalidParameterException("Invalid orderId");
		}
		if(patientId <= 0){
			throw new InvalidParameterException("Invalid patientId");
		}
		if(userId <= 0){
			throw new InvalidParameterException("Invalid userId");
		}
		
		Map<String, Object> filterMap = new HashMap<String, Object>();
		filterMap.put("orderId", orderId);
		filterMap.put("patientId", patientId);
		filterMap.put("userId", userId);
		return pharmacyConcurrencyDAO.deletePharmacyVerificationSessionLog(filterMap);
	}

}
