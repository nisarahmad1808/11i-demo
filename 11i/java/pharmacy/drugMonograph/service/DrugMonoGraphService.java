package inpatientWeb.pharmacy.drugMonograph.service;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.xerces.dom.DocumentImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import inpatientWeb.Global.ecw.ambulatory.CwMobile.CwUtils;
import inpatientWeb.Global.ecw.ambulatory.catalog.CwXmlHelper;
import inpatientWeb.Global.ecw.ambulatory.catalog.PatientSchedule;
import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;
import inpatientWeb.Global.ecw.ambulatory.support.config;
import inpatientWeb.Global.items.dao.IPItemkeyDAO;
import inpatientWeb.eMAR.service.impl.EMARServiceImpl;
import inpatientWeb.refMgmt.util.ReferralManagementUtil;
import inpatientWeb.utils.Util;

@Service
@Scope("prototype")
@Lazy
public class DrugMonoGraphService {
	
	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Autowired
	EMARServiceImpl emarService;

	static final int READ_TIME_OUT = 10000;
	static final int XSL_HEADER_END_INDEX = 2;
	static final int XSL_FILENAME_END_INDEX = 4;
	static final int XSL_HREF_END_INDEX = 6;
	
	/**
	 * Gets the XML response containing monograph.
	 *
	 * @param requestParams: map containing request parameters
	 * @param strUrl: lexicomp url
	 * @param strUserName: user name
	 * @param strUserPwd: user password
	 * @return string containing xml response returned by lexicomp service
	 */
	public String getXMLResponse(Map<String, Object> requestParams, String strUrl, String strUserName, String strUserPwd) {
		
		String strXmlResult = "Failure";	
		Map<String,Object> paramMap = null;
		Map<String,Object> userInfo = null;
		String strUserQuery = null;
		HttpHeaders headers = null;
		HttpEntity<String> entity = null;
		Long userId = null;
		String patientID = null;
		String strTodaysDate = null;
		String strTimeOfBirth = null;
		String strPtDOB = null;
		String strAge = null;
		String ageValue = "";
		String ageUnit = "";
		
		try {

			paramMap = new HashMap<String,Object>();
		
			strUserQuery = "SELECT timeofbirth, ptdob FROM users WHERE uid = :userId";
			patientID = Util.getStrValue(requestParams, "patientId");
			
			if(!Util.isNullEmpty(patientID)) {
				userId = Long.valueOf(patientID);
				paramMap.put("userId", userId);
			}
			
			userInfo = namedParameterJdbcTemplate.query(strUserQuery, paramMap, new ResultSetExtractor<Map<String,Object>> () {

				@Override
				public Map<String, Object> extractData(ResultSet rs) throws SQLException, DataAccessException {
					Map<String,Object> userInfo = new HashMap<String,Object>();
					
					while(rs.next()) {
						userInfo.put("userDob", rs.getString("ptdob"));
			            userInfo.put("userTimeOfBirth", rs.getTime("timeofbirth"));
					}
					return userInfo;
				}		
			});
			
			strPtDOB = Util.getStrValue(userInfo, "userDob");
			strTimeOfBirth = Util.getStrValue(userInfo, "userTimeOfBirth");
			strTodaysDate =  CwUtils.getTodaysDate("yyyy-MM-dd");
			
			if(CwUtils.isDateNull(strTimeOfBirth)) {
				strTimeOfBirth = "00:00:00";
			}
		
			if(!CwUtils.isDateNull(strPtDOB)) {
				boolean timeOfBirthReq = PatientSchedule.getTimeOfBirthItemValue();
				
				strAge = (!timeOfBirthReq) ? CwUtils.CalculateAge(strPtDOB, strTodaysDate) : CwUtils.CalculateAgeForPediatrics(strPtDOB + " " + strTimeOfBirth, CwUtils.getTodaysDate("yyyy-MM-dd kk:mm:ss"));
				/*if(!timeOfBirthReq) {
					strAge = CwUtils.CalculateAge(strPtDOB, strTodaysDate);
				} else {
					strAge = CwUtils.CalculateAgeForPediatrics(strPtDOB + " " + strTimeOfBirth, CwUtils.getTodaysDate("yyyy-MM-dd kk:mm:ss"));
				}*/
			}
			
			if(strAge != null) {
				String[] age = strAge.split("(?<=\\d)(?=\\D)");
				ageValue = age[0].trim();
				ageUnit = this.getLexiCompAgeUnit(age[1].trim());
			}
			
			UriComponentsBuilder uriComponentBuilder = UriComponentsBuilder.fromHttpUrl(strUrl)
					.queryParam("mainSearchCriteria.v.dn", Util.getStrValue(requestParams, "drugName").replaceAll("\\s+", "+"))
					.queryParam("assignedEntity.name.r", strUserName)
					.queryParam("assignedEntity.certificateText.r", strUserPwd)
					.queryParam("informationRecipient", "PROV")
					.queryParam("informationRecipient.languageCode.c", "en");
	
			addSearchCriteriaToUri(uriComponentBuilder, requestParams);
		
			if(!Util.isNullEmpty(ageValue) && !Util.isNullEmpty(ageUnit)) {
				uriComponentBuilder.queryParam("age.v.v", ageValue)
								   .queryParam("age.v.u", ageUnit);
			}
		
			UriComponents uriComponents = uriComponentBuilder.build();

			String auth = strUserName + ":" + strUserPwd;
			String authEncoded = "Basic " + Base64.encodeBase64String(auth.getBytes());
			
			headers = new HttpHeaders();
			headers.setContentType(MediaType.TEXT_XML);
			headers.setAccept(Collections.singletonList(MediaType.TEXT_XML));
			headers.add("Authorization" , authEncoded);
			entity = new HttpEntity<String>(headers);

			RestTemplate restTemplate = getRestTemplate(getClientHttpRequestFactory());
			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			ResponseEntity<String> response = restTemplate.exchange(uriComponents.toUriString(), HttpMethod.GET, entity, String.class);

			if (!response.hasBody()) {
				strXmlResult = "Error While Fetching Drug Monograph Data";
			} else {
				strXmlResult = response.getBody();
			}
		} catch (Exception ex) {
			EcwLog.AppendExceptionToLog(ex);
		}

		return strXmlResult;
	}
	
	public void addSearchCriteriaToUri(UriComponentsBuilder uriComponentBuilder, Map<String, Object> requestParams) {
		String idType = Util.getStrValue(requestParams, "idType");
		String id = Util.getStrValue(requestParams, "id");
		
		switch(idType) {
			case "RxNorm":
				uriComponentBuilder.queryParam("mainSearchCriteria.v.cs", "2.16.840.1.113883.6.88");
				uriComponentBuilder.queryParam("mainSearchCriteria.v.c", id);
				break;
			case "GPI":
				uriComponentBuilder.queryParam("mainSearchCriteria.v.cs", "2.16.840.1.113883.6.68");
				uriComponentBuilder.queryParam("mainSearchCriteria.v.c", id.replaceAll("-", ""));
				break;
		/*	case "DDID":
				uriComponentBuilder.queryParam("mainSearchCriteria.v.cs", "2.16.840.1.113883.6.253");
				uriComponentBuilder.queryParam("mainSearchCriteria.v.c", id);
				break;*/
			case "NDC":
				uriComponentBuilder.queryParam("mainSearchCriteria.v.cs", "2.16.840.1.113883.6.69");
				uriComponentBuilder.queryParam("mainSearchCriteria.v.c", id.replaceAll("-", ""));
				break;
		}
		
	}
	
	/**
	 * Gets the drug monograph data.
	 *
	 * @param requestParams: map containing the request parameters
	 * @return html string representing the drug monograph data
	 */
	public String getDrugMonoGraphData(Map<String, Object> requestParams) {
	
		String strUrl = null;
		String strUserName = null;
		String strUserPwd = null;
		String strXMLResponse = null;
		int nBeginIndex = 0;
		String strRes = "Failure";
		String strXsl = "";
		String xslHeader = "";
		String xslFilename = "";
		
		try {
	
			strUrl = IPItemkeyDAO.getIPItemKeyValueFromName("DrugMonographDataURL", "");
			strUserName = IPItemkeyDAO.getIPItemKeyValueFromName("DrugMonographDataUserName", "");
			strUserPwd = IPItemkeyDAO.getIPItemKeyValueFromName("DrugMonographDataUserPwd", "");
			
			strXMLResponse = this.parseXML(this.getXMLResponse(requestParams, strUrl, strUserName, strUserPwd));

			strXMLResponse = strXMLResponse.replaceAll("&amp;apos;", "&apos;").replaceAll("&amp;amp;","&amp;");
			
			if(strXMLResponse != null && strXMLResponse.length() > 0 && !"Failure".equalsIgnoreCase(strXMLResponse)) {
				nBeginIndex = strXMLResponse.indexOf("<?xml-stylesheet");
				if(nBeginIndex > 0) {
					xslHeader = strXMLResponse.substring(nBeginIndex, strXMLResponse.indexOf("?>", nBeginIndex) + XSL_HEADER_END_INDEX);
					xslFilename = xslHeader.substring(xslHeader.indexOf("href") + XSL_HREF_END_INDEX,xslHeader.indexOf(".xsl") + XSL_FILENAME_END_INDEX);
					strXMLResponse = CwUtils.replaceAllWords(strXMLResponse, xslHeader, "");
				}

				strXsl = new String(ReferralManagementUtil.readFile(config.getTomcatHome() + "/webapps" + xslFilename));	
				strRes = ReferralManagementUtil.processXMLXSL(strXMLResponse, strXsl);
			} 		
		} catch (IOException e) {
			EcwLog.AppendExceptionToLog(e);
		}
		return strRes;
	}
	
	/**
	 * Parses the XML.
	 *
	 * @param responseXML returned by lexicomp service
	 * @return the string containing the desired xml data
	 */
	
	private String parseXML(String responseXML) {
		Document responseDoc = null; 
		Document monographDoc = null;
		NodeList docNodeList = null;
		Element oReturn = null;
		Element monographFields = null;
		String strResult = "Failure";
		
		try {
			if(!Util.isNullEmpty(responseXML) && !"Failure".equalsIgnoreCase(responseXML)) {
				responseDoc = this.convertXMLToDocument(responseXML);
				monographDoc = new DocumentImpl();
				oReturn = CwXmlHelper.CreateXslHeaders(monographDoc, "/mobiledoc/jsp/inpatientWeb/staticContent/pharmacy/drugMonograph/view/drugMonograph.xsl");
				monographFields = monographDoc.createElement("monographFields");
				oReturn.appendChild(monographFields);
				
				if(responseDoc != null) {
					docNodeList = responseDoc.getElementsByTagName("monograghFields");
					traverseNodeListAndAddToDoc(docNodeList.item(0).getChildNodes(), monographDoc, monographFields);
				}
	
				strResult = CwXmlHelper.getXmlString(monographDoc);
				
				Pattern pattern = Pattern.compile("(&#x(?:[0-9A-Za-z]{4};))");
				Matcher matcher = pattern.matcher(strResult);
				
				while(matcher.find()) {
					String replaceString = new StringBuilder(matcher.group()).insert(1, "amp;").toString();
					strResult = strResult.replace(matcher.group(), replaceString);
				}
			}
		} catch (Exception ex) {
			EcwLog.AppendExceptionToLog(ex);
		}
	
		return strResult;
	}
	
	/**
	 * Traverse node list and add to XML document.
	 *
	 * @param nodeList: monograph field node list
	 * @param oDoc: xml document to be returned as response
	 * @param monographFields: monograph field element
	 */
	private void traverseNodeListAndAddToDoc(NodeList nodeList, Document oDoc, Element monographFields) {
		for(int i=0; i<nodeList.getLength(); i++) {
			if(nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nodeElement = (Element) nodeList.item(i);
				if (nodeElement.getNodeName().contains("field")) {
					String fieldName = nodeElement.getElementsByTagName("fieldName").item(0).getTextContent();
					String content = nodeElement.getElementsByTagName("content").item(0).getTextContent();
					Element item = oDoc.createElement("field");
					CwXmlHelper.AppendTextNode(oDoc, item, "fieldName", fieldName);
					CwXmlHelper.AppendTextNode(oDoc, item, "content", content);
					monographFields.appendChild(item);
				}
			}
		} 
	}
	
	/**
	 * Convert XML to document.
	 *
	 * @param responseXML: input XML to be converted
	 * @return the document
	 */
	private Document convertXMLToDocument(String responseXML) {
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;  
		Document responseDoc = null;
		InputSource inputSource = null;
		
		try {
			factory = DocumentBuilderFactory.newInstance();  
			factory.setIgnoringElementContentWhitespace(true);
			builder = factory.newDocumentBuilder();  
			inputSource = new InputSource();
			inputSource.setEncoding("UTF-8");
			inputSource.setCharacterStream(new StringReader(new String(responseXML.getBytes("UTF-8"), "UTF-8")));
			responseDoc = builder.parse(inputSource);   
		} catch(ParserConfigurationException | SAXException | IOException ex) {
			EcwLog.AppendExceptionToLog(ex);
		}
		
		return responseDoc;
	}
		
	/**
	 * Gets the age unit as defined in the lexicomp criteria
	 *
	 * @param patient age unit
	 * @return the lexicomp age unit
	 */
	private String getLexiCompAgeUnit(String ageUnit) {
		String unit = "";
		
		switch(ageUnit) {
			case "Y":
				unit = "a";
				break;
			case "M":
				unit = "mo";
				break;
			case "W":
				unit = "wk";
				break;
			case "D":
				unit = "d";
				break;
			case "H":
				unit = "h";
				break;
			default:
				unit = "";
		}
		return unit;
	}

	/**
	 * Gets the client http request factory.
	 *
	 * @return the client http request factory
	 */
	private static ClientHttpRequestFactory getClientHttpRequestFactory() {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setReadTimeout(READ_TIME_OUT);
		return factory;
	}

	/**
	 * Gets the rest template.
	 *
	 * @return the rest template
	 */
	private static RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	/**
	 * Gets the rest template.
	 *
	 * @param clientHttpRequestFactory: the client http request factory
	 * @return the rest template
	 */
	private static RestTemplate getRestTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
		if (clientHttpRequestFactory == null) {
			return getRestTemplate();
		}
		return new RestTemplate(clientHttpRequestFactory);
	}
}
