package inpatientWeb.pharmacy.util;

import java.text.ParseException;

import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;
import inpatientWeb.utils.IPTzUtils;
import inpatientWeb.utils.Util;

public class PharmacyDateUtil {

	public static String getValidDate(String strDate, int loggedInUserId){
		String result = strDate;
		try{
			String userTimeZone = IPTzUtils.getTimeZoneForResource(loggedInUserId);

			if(Util.isValidDate(strDate, IPTzUtils.DEFAULT_DB_DT_FMT)){
				result =  IPTzUtils.convertDateStrInTz(strDate, IPTzUtils.DEFAULT_DB_DT_FMT , IPTzUtils.DEFAULT_DB_TIME_ZONE, IPTzUtils.DEFAULT_USER_DT_FMT, userTimeZone );
			} else if(Util.isValidDate(strDate, "yyyy-MM-dd")){
				result =  IPTzUtils.convertDateStrInTz(strDate, "yyyy-MM-dd" , IPTzUtils.DEFAULT_DB_TIME_ZONE, IPTzUtils.DEFAULT_USER_DT_FMT, userTimeZone );
			} else {
				result = strDate;
			}

		} catch (ParseException e){
			EcwLog.AppendExceptionToLog(e);
		}
		return result;
	}
	
	public static String convertDateTimeInUtcTz(String strDate, int loggedInUserId,String fromFormat,String toFormat){
		String result = strDate;
		try{
			String userTimeZone = IPTzUtils.getTimeZoneForResource(loggedInUserId);
			result =  IPTzUtils.convertDateStrInTz(strDate, fromFormat , IPTzUtils.DEFAULT_DB_TIME_ZONE, toFormat, userTimeZone );
		} catch (ParseException e){
			EcwLog.AppendExceptionToLog(e);
		}
		return result;
	}
}

