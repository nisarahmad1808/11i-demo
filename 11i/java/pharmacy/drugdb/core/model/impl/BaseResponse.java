package inpatientWeb.pharmacy.drugdb.core.model.impl;

import inpatientWeb.Global.medicationhelper.msclinical.constant.ResultCodeConstant;
import inpatientWeb.pharmacy.drugdb.core.model.IBaseResponse;
import inpatientWeb.pharmacy.drugdb.core.model.IRequestData;

/**
 * The Class EmptyResponse.
 * 
 * @author bharat tulsiyani
 */
public abstract class BaseResponse implements IBaseResponse {

	/** The response code. */
	private int responseCode;
	
	/** The message. */
	private String message;
	
	/** The request token. */
	private String requestToken;
	
	/** The version. */
	private String version;
	
	private String serverToken;
	
	public String getServerToken() {
		return serverToken;
	}

	public void setServerToken(String serverToken) {
		this.serverToken = serverToken;
	}
	
	/**
	 * Instantiates a new base response.
	 */
	public BaseResponse() {
		super();
		this.responseCode = ResultCodeConstant.INTERNAL_SERVER_ERROR.getResultCode();
		this.message = ResultCodeConstant.INTERNAL_SERVER_ERROR.getMessage();
		this.version = "";
		this.requestToken = "";
		this.serverToken = Thread.currentThread().getName();
	}

	/**
	 * Instantiates a new base response.
	 *
	 * @param resultCodeConstant the result code constant
	 * @param message the message
	 */
	public BaseResponse(ResultCodeConstant resultCodeConstant, String message) {
		super();
		this.responseCode = resultCodeConstant.getResultCode();
		this.message = message;
		this.version = "";
		this.requestToken = "";
		this.serverToken = Thread.currentThread().getName();
	}
	
	/**
	 * Instantiates a new base response.
	 *
	 * @param <E> the element type
	 * @param resultCodeConstant the result code constant
	 * @param message the message
	 * @param requestData the request data
	 */
	public <E> BaseResponse(ResultCodeConstant resultCodeConstant, String message, IRequestData<E> requestData) {
		super();
		this.responseCode = resultCodeConstant.getResultCode();
		this.message = message;
		if(requestData != null)
			this.requestToken = requestData.getRequestToken();
		this.version = "";
		this.serverToken = Thread.currentThread().getName();
	}

	/* (non-Javadoc)
	 * @see ecwrx.msclinical.core.model.IBaseResponse#getResponseCode()
	 */
	public int getResponseCode() {
		return responseCode;
	}

	/* (non-Javadoc)
	 * @see ecwrx.msclinical.core.model.IBaseResponse#setResponseCode(ecwrx.msclinical.core.constant.ResultCodeConstant)
	 */
	public void setResponseCodeData(ResultCodeConstant resultCodeConstant) {
		this.responseCode = resultCodeConstant.getResultCode();
	}
	
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	/* (non-Javadoc)
	 * @see ecwrx.msclinical.core.model.IBaseResponse#getMessage()
	 */
	public String getMessage() {
		return message;
	}

	/* (non-Javadoc)
	 * @see ecwrx.msclinical.core.model.IBaseResponse#setMessage(java.lang.String)
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/* (non-Javadoc)
	 * @see ecwrx.msclinical.core.model.IBaseResponse#getRequestToken()
	 */
	public String getRequestToken() {
		return requestToken;
	}

	/* (non-Javadoc)
	 * @see ecwrx.msclinical.core.model.IBaseResponse#setRequestToken(java.lang.String)
	 */
	public void setRequestToken(String requestToken) {
		this.requestToken = requestToken;
	}

	/* (non-Javadoc)
	 * @see ecwrx.msclinical.core.model.IBaseResponse#getVersion()
	 */
	public String getVersion() {
		return version;
	}

	/* (non-Javadoc)
	 * @see ecwrx.msclinical.core.model.IBaseResponse#setVersion(java.lang.String)
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BaseResponse [responseCode=" + responseCode + ", message=" + message + ", requestToken=" + requestToken + ", version=" + version + "]";
	}
	
}
