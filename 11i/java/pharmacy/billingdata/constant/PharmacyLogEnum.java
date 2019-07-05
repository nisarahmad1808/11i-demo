package inpatientWeb.pharmacy.billingdata.constant;

import java.util.HashMap;
import java.util.Map;

public final class PharmacyLogEnum {

	public static enum OPERATION {LOG};
	public enum SubModule {
		EMAR("EMAR"),
		PWQ_VERIFY("PWQ_VERIFY"),
		PWQ_MANUAL_CHARGE("PWQ_MANUAL_CHARGE"),
		DEFAULT("NA"),
		;
		
		private String value;
		
		SubModule(String value) {
			this.value = value;
		}
		
		private static Map<String, SubModule> subModuleMapValues = new HashMap<>();

		static {
			for (SubModule subModule : values()) {
				subModuleMapValues.put(subModule.name(), subModule);
			}
		}

		public static SubModule getSubModuleName(String code) {
			SubModule subModule = subModuleMapValues.get(code);
			if (subModule == null) {
				return SubModule.DEFAULT;
			}
			return subModule;
		}

		public String getValue() {
			return value;
		}

	}
	
	public enum LogMessage {
		EMAR_LOG("Charges dropped at time of pharmacy verification"),
		PWQ_LOG("Charges will be dropped at the time of drug administration"),
		DEFAULT("NA"),
		;
		
		private String value;
		
		LogMessage(String value) {
			this.value = value;
		}
		
		private static Map<String, LogMessage> logMessageMapValues = new HashMap<>();

		static {
			for (LogMessage logMessage : values()) {
				logMessageMapValues.put(logMessage.name(), logMessage);
			}
		}

		public static LogMessage getSubModuleName(String code) {
			LogMessage logMessage = logMessageMapValues.get(code);
			if (logMessage == null) {
				return LogMessage.DEFAULT;
			}
			return logMessage;
		}

		public String getValue() {
			return value;
		}

	}

}
