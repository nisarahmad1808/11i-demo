package inpatientWeb.pharmacy.interfaces.outbound.service;

import java.util.Map;

import inpatientWeb.pharmacy.interfaces.outbound.dto.DispensingInterfaceOutBoundModel;

public interface DispensingInterfaceOutBoundService {
	public Map<String, Object> processDispensingOutBoundHl7Message(DispensingInterfaceOutBoundModel dispensingInterfaceOutBoundModel);
}
