package inpatientWeb.pharmacy.serviceImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xerces.dom.DocumentImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import inpatientWeb.Auth.exception.InvalidParameterException;
import inpatientWeb.Global.ecw.ambulatory.catalog.CwXmlHelper;
import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;
import inpatientWeb.Global.ecw.ambulatory.json.JSONException;
import inpatientWeb.Global.ecw.ambulatory.json.JSONObject;
import inpatientWeb.admin.pharmacySettings.pharmacyHelpers.PharmacyConstants;
import inpatientWeb.admin.pharmacySettings.pharmacyHelpers.PharmacyHelper;
import inpatientWeb.pharmacy.beans.WorkQueue;
import inpatientWeb.pharmacy.dao.WorkQueueDAO;
import inpatientWeb.pharmacy.service.MedicationProfileService;

@Service
public class MedicationProfileServiceImpl implements MedicationProfileService {
	
	@Autowired
	public WorkQueueDAO workQueueDAO;	

	@Autowired
	PharmacyHelper pharmacyHelper;
	
	public String getMedicationProfileData(int patientId, int pharmacyStatus, int loggedInUserId)  {

		if(patientId <= 0){
			throw new InvalidParameterException("Invalid patientId");
		}
		if(loggedInUserId <= 0){
			throw new InvalidParameterException("Invalid loggedInUserId");
		}

		JSONObject result = new JSONObject();
		try{

			List<WorkQueue> arrAllMedData = new ArrayList<WorkQueue>();
			List<WorkQueue> arrAllIVData = new ArrayList<WorkQueue>();
			List<WorkQueue> arrAllComplexIVData = new ArrayList<WorkQueue>();

			ArrayList<WorkQueue> workQueueList = workQueueDAO.getMedicationProfileData(patientId, pharmacyStatus, 0, loggedInUserId);
			for(WorkQueue workQueue : workQueueList){
				addMedInWorkQueue(arrAllMedData, arrAllIVData, arrAllComplexIVData, workQueue);
			}

			result.put("allMeds", arrAllMedData);
			result.put("allMedsCount", arrAllMedData.size());
			result.put("allIVs", arrAllIVData);
			result.put("allIVsCount", arrAllIVData.size());
			result.put("allComplexIVs", arrAllComplexIVData);
			result.put("allComplexIVsCount", arrAllComplexIVData.size());

		}catch(JSONException e){
			EcwLog.AppendExceptionToLog(e);
		}

		return result.toString();
	}

	public Map<String, Object> getMedicationProfile(int patientId, int loggedInUserId)  {

		if(patientId <= 0){
			throw new InvalidParameterException("Invalid patientId");
		}
		if(loggedInUserId <= 0){
			throw new InvalidParameterException("Invalid loggedInUserId");
		}

		Map<String, Object> result = new HashMap<String, Object>();

		List<WorkQueue> arrAllMedData = new ArrayList<WorkQueue>();
		List<WorkQueue> arrVerifiedMedData = new ArrayList<WorkQueue>();
		List<WorkQueue> arrActiveMedData = new ArrayList<WorkQueue>();

		List<WorkQueue> arrAllIVData = new ArrayList<WorkQueue>();
		List<WorkQueue> arrVerifiedIVData = new ArrayList<WorkQueue>();
		List<WorkQueue> arrActiveIVData = new ArrayList<WorkQueue>();

		List<WorkQueue> arrAllComplexIVData = new ArrayList<WorkQueue>();
		List<WorkQueue> arrVerifiedComplexIVData = new ArrayList<WorkQueue>();
		List<WorkQueue> arrActiveComplexIVData = new ArrayList<WorkQueue>();
		PharmacyHelper pharmacyHelper =  new PharmacyHelper();	

		ArrayList<WorkQueue> workQueueList = workQueueDAO.getMedicationProfileData(patientId, 0, 0, loggedInUserId);
		for(WorkQueue workQueue : workQueueList){
			boolean isNotActiveOrder = pharmacyHelper.isNotActiveOrder(workQueue.getOrderStatusId());
			if(workQueue.getPharmacyStatusId() == PharmacyConstants.PharmacyVerificationStatus.VERIFIED.getId() && (!isNotActiveOrder)){
				int orderType = workQueue.getOrderType();

				switch(orderType){

				case WorkQueue.ORDER_TYPE_MED:
					arrVerifiedMedData.add(workQueue);
					arrActiveMedData.add(workQueue);
					break;
				case WorkQueue.ORDER_TYPE_IV:
					arrVerifiedIVData.add(workQueue);
					arrActiveIVData.add(workQueue);
					break;
				case WorkQueue.ORDER_TYPE_COMPLEX_IV:
					arrVerifiedComplexIVData.add(workQueue);
					arrActiveComplexIVData.add(workQueue);
					break;
				default:
					break;

				}
			} else if(workQueue.getPharmacyStatusId() == PharmacyConstants.PharmacyVerificationStatus.UNVERIFIED.getId()
					|| workQueue.getPharmacyStatusId() == PharmacyConstants.PharmacyVerificationStatus.PENDING.getId()
					){

				addMedInWorkQueue(arrAllMedData, arrAllIVData, arrAllComplexIVData, workQueue);

			}
		}
		result.put("allMeds", arrAllMedData);
		result.put("allMedsCount", arrAllMedData.size());
		result.put("verifiedMeds", arrVerifiedMedData);
		result.put("verifiedMedsCount", arrVerifiedMedData.size());
		result.put("activeMeds", arrActiveMedData);
		result.put("activeMedsCount", arrActiveMedData.size());

		result.put("allIVs", arrAllIVData);
		result.put("allIVsCount", arrAllIVData.size());
		result.put("verifiedIVs", arrVerifiedIVData);
		result.put("verifiedIVsCount", arrVerifiedIVData.size());
		result.put("activeIVs", arrActiveIVData);
		result.put("activeIVsCount", arrActiveIVData.size());

		result.put("allComplexIVs", arrAllComplexIVData);
		result.put("allComplexIVsCount", arrAllComplexIVData.size());
		result.put("verifiedComplexIVs", arrVerifiedComplexIVData);
		result.put("verifiedComplexIVsCount", arrVerifiedComplexIVData.size());
		result.put("activeComplexIVs", arrActiveComplexIVData);
		result.put("activeComplexIVsCount", arrActiveComplexIVData.size());

		return result;
	}

	private void addMedInWorkQueue(List<WorkQueue> arrAllMedData, List<WorkQueue> arrAllIVData, List<WorkQueue> arrAllComplexIVData, WorkQueue workQueue) {

		int orderType = workQueue.getOrderType();

		switch(orderType){

		case WorkQueue.ORDER_TYPE_MED:
			arrAllMedData.add(workQueue);
			break;
		case WorkQueue.ORDER_TYPE_IV:
			arrAllIVData.add(workQueue);
			break;
		case WorkQueue.ORDER_TYPE_COMPLEX_IV:
			arrAllComplexIVData.add(workQueue);
			break;
		default:
			break;

		}

	}
	
	/**
	 * @author Vipalk
	 * API used for Medication Profile Printing 
	 */
	public String generateHtmlForMedicationProfile(int patientId, int loggedInUserId){
		if(patientId<=0){
			throw new InvalidParameterException("Invalid patientId");
		}
		if (loggedInUserId<=0){
			throw new InvalidParameterException("Invalid userId");
		}
		String xml=createMedicationProfileXml(getMedicationProfile(patientId,loggedInUserId),patientId);
		String xslPath= new StringBuilder().append(File.separator).append("mobiledoc").append(File.separator).append("jsp").append(File.separator)
				.append("inpatientWeb").append(File.separator).append("staticContent").append(File.separator).append("pharmacy").append(File.separator)
				.append("workQueue").append(File.separator).append("views").append(File.separator)
				.append("PrintMedicationProfile").append(".xsl").toString();
		return pharmacyHelper.generateHTML(xml, xslPath);
	}
	/**
	 * @author Vipalk
	 * API used for Medication Profile Printing XML generation 
	 */
	@SuppressWarnings("unchecked")
	private String createMedicationProfileXml(Map<String,Object> map, int patientId){
		String resultXml=null;
		List<WorkQueue>  allMeds= map.get("allMeds")!=null ? (ArrayList<WorkQueue>)map.get("allMeds") :null;
		List<WorkQueue>  verifiedMeds= map.get("verifiedMeds")!=null ? (ArrayList<WorkQueue>)map.get("verifiedMeds") :null;
		List<WorkQueue>  allIVs= map.get("allIVs")!=null ? (ArrayList<WorkQueue>)map.get("allIVs") :null;
		List<WorkQueue>  verifiedIVs= map.get("verifiedIVs")!=null ? (ArrayList<WorkQueue>)map.get("verifiedIVs") :null;
		List<WorkQueue>  allComplexIVs= map.get("allComplexIVs")!=null ? (ArrayList<WorkQueue>)map.get("allComplexIVs") :null;
		List<WorkQueue>  verifiedComplexIVs= map.get("verifiedComplexIVs")!=null ? (ArrayList<WorkQueue>)map.get("verifiedComplexIVs") :null;

		Document oDoc = new DocumentImpl();
		Element oReturn=CwXmlHelper.CreateSoapHeaders(oDoc);

		appendPatientInfo(oDoc,oReturn,patientId);

		appendOrderElement(oDoc, oReturn, "medUnverified", "UnVerified","Medication", allMeds);
		appendOrderElement(oDoc, oReturn, "ivUnverified", "UnVerified","IV", allIVs);
		appendOrderElement(oDoc, oReturn, "complexUnverified", "UnVerified","Complex Orders", allComplexIVs);
		appendOrderElement(oDoc, oReturn, "medVerified", "Verified","Medication", verifiedMeds);
		appendOrderElement(oDoc, oReturn, "ivVerified", "Verified","IV", verifiedIVs);
		appendOrderElement(oDoc, oReturn, "complexVerified", "Verified","Complex Orders", verifiedComplexIVs);

		try{
			resultXml=CwXmlHelper.getXmlString(oDoc);
		}catch(Exception e){
			EcwLog.AppendExceptionToLog(e);
		}
		return resultXml;
	}

	private void appendOrderElement(Document oDoc, Element oReturn, String tagName, String status, String orderType, List<WorkQueue> medList){
		if(medList != null && medList.size()>0){
			Element orderElement= oDoc.createElement("Orders");
			Element workQueueElement=oDoc.createElement(tagName);

			for(WorkQueue i : medList){
				appendMedicationProfileNodes(oDoc, workQueueElement, i,status,orderType);
			}

			orderElement.appendChild(workQueueElement);
			oReturn.appendChild(orderElement);
		}
	}

	private void appendMedicationProfileNodes(Document oDoc, Element parentElement,WorkQueue ref,String status,String orderType){
		if(ref != null){
			Element orderElement=oDoc.createElement("Order");
			CwXmlHelper.AppendTextNode(oDoc, orderElement, "priority", ref.getPriorityName());
			CwXmlHelper.AppendTextNode(oDoc, orderElement, "tag", "".equalsIgnoreCase(ref.getTag())? "Others" : ref.getTag());
			CwXmlHelper.AppendTextNode(oDoc, orderElement, "startDateTime", ref.getStartDateTime());
			CwXmlHelper.AppendTextNode(oDoc, orderElement, "endDateTime", ref.getEndDateTime());
			CwXmlHelper.AppendTextNode(oDoc, orderElement, "order", ref.getItemName());
			CwXmlHelper.AppendTextNode(oDoc, orderElement, "details", ref.getOrderDetail());
			CwXmlHelper.AppendTextNode(oDoc, orderElement, "orderDetails", ref.getOrderDetail());
			CwXmlHelper.AppendTextNode(oDoc, orderElement, "orderingProvider", ref.getOrderingProviderName());
			CwXmlHelper.AppendTextNode(oDoc, orderElement, "pharmacyOrderStatus", ref.getPharmacyStatus());
			CwXmlHelper.AppendTextNode(oDoc, orderElement, "pharmacyOrderType",orderType);
			parentElement.appendChild(orderElement);
		}
	}
	/**
	 * API for adding the patient information
	 * on the print handout
	 * @param oDoc
	 * @param parentElement
	 * @author Vipalk
	 */
	private void appendPatientInfo(Document oDoc, Element parentElement ,int patientId){
		Map<String, Object> patientInfo= pharmacyHelper.getBasicPatientDetails(patientId);
		if(patientInfo==null || patientInfo.isEmpty())
			return ;

		Element patientInfoElement= oDoc.createElement("patientInfo");
		CwXmlHelper.AppendTextNode(oDoc, patientInfoElement, "patientname",patientInfo.get("fullname")!=null?(String)patientInfo.get("fullname"):"");
		CwXmlHelper.AppendTextNode(oDoc, patientInfoElement, "dob", patientInfo.get("dob")!=null?(String)patientInfo.get("dob"):"");
		CwXmlHelper.AppendTextNode(oDoc, patientInfoElement, "gender", patientInfo.get("gender")!=null? (String)patientInfo.get("gender"):"");
		parentElement.appendChild(patientInfoElement);
	}

}
