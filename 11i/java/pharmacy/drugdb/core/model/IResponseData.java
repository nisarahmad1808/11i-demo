package inpatientWeb.pharmacy.drugdb.core.model;

import java.util.List;

import inpatientWeb.Global.medicationhelper.msclinical.constant.ResultCodeConstant;

/**
 * The Class IResponseData.
 *
 * @author bharat tulsiyani
 * @param <T> the generic type
 */
public interface IResponseData<T> {
	
	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public T getData();

	/**
	 * Sets the data.
	 *
	 * @param data the new data
	 */
	public void setData(T data);

	/**
	 * Gets the response code.
	 *
	 * @return the response code
	 */
	public int getResponseCode();

	/**
	 * Sets the response code.
	 *
	 * @param resultCodeConstant the new response code
	 */
	public void setResponseCodeData(ResultCodeConstant resultCodeConstant);

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage();

	/**
	 * Sets the message.
	 *
	 * @param message the new message
	 */
	public void setMessage(String message);

	/**
	 * Gets the request token.
	 *
	 * @return the request token
	 */
	public String getRequestToken();

	/**
	 * Sets the request token.
	 *
	 * @param requestToken the new request token
	 */
	public void setRequestToken(String requestToken);
	
	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public String getVersion();

	/**
	 * Sets the version.
	 *
	 * @param version the new version
	 */
	public void setVersion(String version);
	
	public List<T> getDataList();

	public void setDataList(List<T> dataList);

}
