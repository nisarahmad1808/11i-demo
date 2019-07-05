package inpatientWeb.pharmacy.drugdb.core.model;

/**
 * The Class IRequestData.
 *
 * @author bharat tulsiyani
 * @param <T> the generic type
 */
public interface IRequestData<T> {
	
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
	 * Gets the auth token.
	 *
	 * @return the auth token
	 */
	public String getAuthToken();

	/**
	 * Sets the auth token.
	 *
	 * @param authToken the new auth token
	 */
	public void setAuthToken(String authToken);

}
