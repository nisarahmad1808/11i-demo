package inpatientWeb.pharmacy.drugMonograph.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import inpatientWeb.admin.usermanagement.securitysettings.annotation.CheckForUserAccess;
import inpatientWeb.eMAR.util.EMARUtil;
import inpatientWeb.pharmacy.drugMonograph.service.DrugMonoGraphService;

@Controller
public class DrugMonographController {

	@Autowired
	private DrugMonoGraphService drugMonographService;
	
	@CheckForUserAccess(securitySettingKeys = EMARUtil.SEC_DRUG_MONOGRAPH)
	@RequestMapping(value = "drugMonograph/getDrugMonographData", method = RequestMethod.POST, produces="text/xml")
	@ResponseBody String getDrugMonoGraphData(@RequestBody Map<String, Object> requestParams) {
		return drugMonographService.getDrugMonoGraphData(requestParams);
	}
	
}
