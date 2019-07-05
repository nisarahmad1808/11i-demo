package inpatientWeb.pharmacy.interfaces.outbound.controller;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import inpatientWeb.pharmacy.interfaces.outbound.serviceImpl.DispensingInterfaceLogServiceImpl;

@Controller
@Lazy
@Scope("prototype")
public class DispensingInterfaceController {
	
	@Autowired
	DispensingInterfaceLogServiceImpl dispensingInterfaceLogServiceImpl;
	
	/**
	 * It returns the view of Medication Dispensing Interafce Log
	 * @param request
	 * @param response
	 * @return String  - Path of View
	 */
	@RequestMapping(value = "/pharmacyWorkQueue.go/dispensingInterfaceLog", method = RequestMethod.GET)
	public String loadDispensingInterfaceLog(HttpServletRequest request, HttpServletResponse response) {
		return "/staticContent/pharmacy/interfaces/view/dispensingInterfaceLog";
	}
	
	/**
	 * It returns the Med Dispense Interface Log(list) and the number of records based on filter.
	 * @param request
	 * @param response
	 * @param pathVariables
	 * @return String  - Med Dispense Interface Log (list)
	 * 				   - In case of exception :"ResponseException" 
	 */
	@RequestMapping(value = "/pharmacyWorkQueue.go/getDispensingInterfaceLog", method = RequestMethod.POST, produces = MediaType.ALL_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getMedicationDispenseLog(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, String> pathVariables) {
		String responseReportList = dispensingInterfaceLogServiceImpl.getMedicationDispenseLog(pathVariables);
		return responseReportList;
	}

}
