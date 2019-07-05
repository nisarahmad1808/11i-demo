package inpatientWeb.pharmacy.interfaces.util;

import inpatientWeb.utils.DateUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class DispensingInterfaceUtil {
	
	private static final int OCTAL = 8;
	
	@SuppressWarnings("unused")
	public static Boolean validateNullObject(String val){
		Boolean result=false;
		if(val!=null && !"".equals(val)){
			result=true;
		}else{
			result=false;
		}
		return result;
		
	}
	
	/**
	 * API to generate random number as Message Control Id
	 * @return String - 18 digit number(15 Digit CurrentDateTimeMiliSeconds + 3 Digit Random)
	 */
	public synchronized String generateMessageId() {
		StringBuilder uniqueId = new StringBuilder();
		uniqueId.append(DateUtil.getTodaysDate("yyMMddHHmmss.SSS").replace(".", ""));
		uniqueId.append(100 + new Random().nextInt(900));
		return uniqueId.toString();
	}
	/**
	 * API to accept octet value and convert in to decimal value if incorrect value convert default value
	 * @param value
	 * @return int - decimalValue
	 */
	 public int getOctToDecimalValue(String octetValue,String defaultValue){
		int decimalValue = 0;
		if((!("".equals(octetValue))) && (octetValue.contains("[0-9]+"))) {
			decimalValue = Integer.parseInt(octetValue, OCTAL);	
		}else {
			decimalValue = Integer.parseInt(defaultValue, OCTAL);	
		}	
		return decimalValue;
	}
	
	/**
	 * This method will generate unique string.
	 * @return String as unique alphanumeric character string.
	 */
	public synchronized String generateUniqueId()
    {
		String id = "";
		try {
	        id = UUID.randomUUID().toString();
	        id = id.replaceAll("-", "");
	       
		} catch (RuntimeException ex) {
			inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog.AppendExceptionToLog(ex);
		}
		return id;
    }
	/**
	 * It takes date as input in "fromFormat"(user defined input) format & returns date in "toFormat"(user defined input) format.
	 * @param dbDate
	 * @return String
	 */
	public String convertDateFormat(Object dbDate, String fromFormat,String toFormat) {

		SimpleDateFormat formatParser = new SimpleDateFormat(fromFormat);
		SimpleDateFormat formatFormatter = new SimpleDateFormat(toFormat);
		Date date = null;
		String formattedDate = "";
		try {
			if (dbDate != null) {
				String dbDate1 = dbDate.toString();
				date = formatParser.parse(dbDate1);
				formattedDate = formatFormatter.format(date);
			}
		} catch (ParseException | RuntimeException e) {
			inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog.AppendExceptionToLog(e);
		}
		return formattedDate;
	}
}
