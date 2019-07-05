package inpatientWeb.pharmacy.drugdb.controller;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import inpatientWeb.pharmacy.drugdb.model.InteractionRequest;
import inpatientWeb.pharmacy.drugdb.service.DrugDbService;

/**
 * <h1>Drug Database Controller</h1> This class contains APIs to get data from drug database,
 *  1. Drug interactions
 * <p>
 * 
 * @author Dishagna Bhavsar
 * @version 1.0
 * @since 1.0
 * 
 */

@Controller
@SessionAttributes({"uid", "UserType"})
@RequestMapping("drugDb/*")
public class DrugDbController {
	
	private DrugDbService drugDbService;

	/**
	 * This API will return interactions based on episodeEncouterId.
	 *
	 * @param request 
	 * 				This parameter contain episodeEncounterId, 
	 * @param userId 
	 * @return the drug interactions
	 * @throws ParseException 
	 */
	@RequestMapping(value = "getInteractions",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getDrugInteractions(@RequestBody InteractionRequest interactionRequest, @ModelAttribute("uid") int userId){
			return drugDbService.getInteractions(interactionRequest, userId);
	}

	/********************************************************************************************
	 * 										Autowired
	 ********************************************************************************************/
	
	public DrugDbService getDrugDbService() {	
		return drugDbService;
	}

	@Autowired
	public void setDrugDbService(DrugDbService drugDbService) {
		this.drugDbService = drugDbService;
	}
}
