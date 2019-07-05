package inpatientWeb.pharmacy.drugdb.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import inpatientWeb.Global.ecw.ambulatory.com.ecw.dao.EcwLog;
import inpatientWeb.pharmacy.drugdb.core.model.impl.RequestData;
import inpatientWeb.pharmacy.interaction.constants.InteractionConstants.INTERACTION;

public class Utils {
	
	/* Constant declaration */
	static final int MS_TIME_OUT = 10000;
	static final int HTTP_PORT = 80;
	static final int HTTPS_PORT = 443;
	static final int MAJOR_VERSION_7 = 7;

	private Utils() {}
	/*********************************************************************************************************************************
	 * 											Common methods for JSON request and response for DrugDB
	 *********************************************************************************************************************************/
	
	/**
	 * Gets the error data.
	 *
	 * @param msg the msg
	 * @return the error data
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject getErrorData(String msg) {
		JSONObject responseObj = new JSONObject();
		responseObj.put(INTERACTION.STATUS_CODE.text(), 0);
		responseObj.put(INTERACTION.DATA.text(), "");
		responseObj.put(INTERACTION.MSG.text(), msg);
		return responseObj;
	}

	/**
	 * Gets the json post response.
	 *
	 * @param <T> the generic type
	 * @param uri the uri
	 * @param request the request
	 * @param responseType the response type
	 * @return the json post response
	 * @throws RestClientException the rest client exception
	 * @throws URISyntaxException the URI syntax exception
	 */
	public static <T> T getJsonPostResponse(URI uri, JSONObject request, Class<T> responseType) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(String.valueOf(request), headers);
		RestTemplate restTemplate = getRestTemplate(getClientHttpRequestFactory());
		return restTemplate.postForObject(uri, entity, responseType);
	}
	
	public static <T, E> T getJsonPostResponseFromObject(URI uri, RequestData<E> request, Class<T> responseType) {
		RestTemplate restTemplate = getRestTemplate(getClientHttpRequestFactory());
		return restTemplate.postForObject(uri, request, responseType);
	}
	
	public static <T> T getJsonGetResponse(URI uri, Class<T> responseType) {
		RestTemplate restTemplate = getRestTemplate(getClientHttpRequestFactory());
		return restTemplate.getForObject(uri, responseType);
	}
	
	public static <T> T getJsonGetResponseFromParams(URI uri, Class<T> responseType, String param) {
		RestTemplate restTemplate = getRestTemplate(getClientHttpRequestFactory());
		return restTemplate.getForObject(uri + param, responseType);
	}
	
	/**
	 * Gets the client http request factory.
	 *
	 * @return the client http request factory
	 */
	public static ClientHttpRequestFactory getClientHttpRequestFactory() {
		HttpComponentsClientHttpRequestFactory factory = getHttpComponentClientFactory();
		factory.setReadTimeout(MS_TIME_OUT);
		return factory;
	}
	
	/**
	 * Gets the rest template.
	 *
	 * @return the rest template
	 */
	public static RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	/**
	 * Gets the rest template.
	 *
	 * @param clientHttpRequestFactory the client http request factory
	 * @return the rest template
	 */
	public static RestTemplate getRestTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
		if (clientHttpRequestFactory == null) {
			return getRestTemplate();
		}
		return new RestTemplate(clientHttpRequestFactory);
	}
	
	/**
	 * Gets the success data.
	 *
	 * @param data the data
	 * @return the success data
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject getSuccessData(Object data) {

		JSONObject responseObj = new JSONObject();

		try {
			responseObj.put(INTERACTION.STATUS_CODE.text(), 1);
			responseObj.put(INTERACTION.DATA.text(), data);
			responseObj.put(INTERACTION.MSG.text(), "");
		} catch (Exception e) {
			EcwLog.AppendExceptionToLog(e);
		}
		return responseObj;
	}
	
	
	/**
	 * Gets the http request client factory.
	 *
	 * @return the http request client factory
	 */
	public static HttpComponentsClientHttpRequestFactory getHttpComponentClientFactory(){
		if(inpatientWeb.Global.ecw.ambulatory.addressvalidator.Utils.getJavaMajorVersion() > MAJOR_VERSION_7){
			return new HttpComponentsClientHttpRequestFactory();
		}
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
	    try {
	        SSLContext context= SSLContext.getInstance("TLSv1.2");
	        context.init(null, null, null);

	        SSLSocketFactory ssf = new SSLSocketFactory(context);
	        ClientConnectionManager ccm = httpClient.getConnectionManager();
	        SchemeRegistry sr = ccm.getSchemeRegistry();
	        sr.register(new Scheme("http", HTTP_PORT, PlainSocketFactory.getSocketFactory()));
	        sr.register(new Scheme("https", HTTPS_PORT, ssf));

	    } catch (NoSuchAlgorithmException | KeyManagementException e) {
	    	EcwLog.AppendToLog("Could not load the TLS version 1.2 due to => " + e.getMessage());
	    }
		return new HttpComponentsClientHttpRequestFactory(httpClient);
		
	}
	
	/**
	 * Gets the response data wrapper.
	 *
	 * @param <T> the generic type
	 * @param data the data
	 * @return the response data wrapper
	 */
	public static <T> RequestData<T> getResponseDataWrapper(T data){
		RequestData<T> requestData= new RequestData<>();
		requestData.setData(data);
		requestData.setAuthToken("");
		requestData.setRequestToken("");
		return requestData;
	}
	
	public static <T> boolean containAny(List<T> mainList, List<T> compareList){
		if(mainList == null || compareList == null) {
			return false;
		}
		for(T data : compareList) {
			if(mainList.contains(data)) {
				return true;
			}
		}
		return false;
	}
}
