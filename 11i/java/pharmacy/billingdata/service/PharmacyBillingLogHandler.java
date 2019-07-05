package inpatientWeb.pharmacy.billingdata.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;
import inpatientWeb.Global.ecw.auditlogs.AuditLogService;
import inpatientWeb.pharmacy.billingdata.constant.PharmacyLogEnum;
import inpatientWeb.pharmacy.billingdata.exception.PharmacyBillingException;
import inpatientWeb.pharmacy.billingdata.model.PharmacyBillingLogData;

@Service
@Scope("prototype")
@Lazy
public class PharmacyBillingLogHandler{

	private AuditLogService auditLogServ;
	
	ObjectMapper objMapper = new ObjectMapper();
	
	private static final String MODULE_NAME = "ip_pharmacy_billing";
	
	public void executeLog(int userId, PharmacyLogEnum.SubModule subModule, String logStatus, PharmacyBillingLogData logObject) {
		try{
			if(logStatus == null){
				throw new PharmacyBillingException("Status can not be null");
			}
			Map<String, Object> logMap = objMapper.convertValue(logObject, new TypeReference<Map<String, Object>>(){});
			auditLogServ.logEvent(userId, MODULE_NAME, logStatus, logMap, subModule.name());
		} catch (Exception e) {
			EcwLog.AppendExceptionToLog(e);
		}
	}
	
	public Map<String, Object> getEMARLogDataByFormularyId(Long formularyId) {
		try {
			Map<String, Object> searchParams = new HashMap<>();
			searchParams.put("moduleName", MODULE_NAME);
			searchParams.put("formularyId", formularyId);
			return auditLogServ.getLogData(searchParams);
		} catch (Exception e) {
			EcwLog.AppendExceptionToLog(e);
		}
		return new HashMap<>();
	}
	
	public Map<String, Object> getEMARLogDataByBillingDataId(Long billingDataId) {
		try {
			Map<String, Object> searchParams = new HashMap<>();
			searchParams.put("moduleName", MODULE_NAME);
			searchParams.put("billingDataId", billingDataId);
			return auditLogServ.getLogData(searchParams);
		} catch (Exception e) {
			EcwLog.AppendExceptionToLog(e);
		}
		return new HashMap<>();
	}
	
 	public AuditLogService getAuditLogServ() {
		return auditLogServ;
	}

	@Autowired
	public void setAuditLogServ(AuditLogService auditLogServ) {
		this.auditLogServ = auditLogServ;
	}

}
