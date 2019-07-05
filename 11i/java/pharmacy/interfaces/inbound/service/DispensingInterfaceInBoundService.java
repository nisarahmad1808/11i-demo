package inpatientWeb.pharmacy.interfaces.inbound.service;

import java.util.Map;

public interface DispensingInterfaceInBoundService {
	 boolean processInboundMessage(Map<String,Object> requestMap);
}
