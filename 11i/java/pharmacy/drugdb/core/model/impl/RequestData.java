package inpatientWeb.pharmacy.drugdb.core.model.impl;


import com.fasterxml.jackson.annotation.JsonAutoDetect;

import inpatientWeb.pharmacy.drugdb.core.model.IRequestData;

/**
 * The Class RequestData.
 *
 * @author bharat tulsiyani
 * @param <T> the generic type
 */
@JsonAutoDetect
public class RequestData<T> implements IRequestData<T> {
	
	/** The data. */
	private T data;
	
	/** The request token. */
	private String requestToken;
	
	/** The auth token. */
	private String authToken;

	/* (non-Javadoc)
	 * @see ecwrx.msclinical.core.model.IRequestData#getData()
	 */
	@Override	
	public T getData() {
		return data;
	}

	/* (non-Javadoc)
	 * @see ecwrx.msclinical.core.model.IRequestData#setData(java.lang.Object)
	 */
	@Override
	public void setData(T data) {
		this.data = data;
	}

	/* (non-Javadoc)
	 * @see ecwrx.msclinical.core.model.IRequestData#getRequestToken()
	 */
	@Override
	public String getRequestToken() {
		return requestToken;
	}

	/* (non-Javadoc)
	 * @see ecwrx.msclinical.core.model.IRequestData#setRequestToken(java.lang.String)
	 */
	@Override
	public void setRequestToken(String requestToken) {
		this.requestToken = requestToken;
	}

	/* (non-Javadoc)
	 * @see ecwrx.msclinical.core.model.IRequestData#getAuthToken()
	 */
	@Override
	public String getAuthToken() {
		return authToken;
	}

	/* (non-Javadoc)
	 * @see ecwrx.msclinical.core.model.IRequestData#setAuthToken(java.lang.String)
	 */
	@Override
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RequestData [data=" + data + ", requestToken=" + requestToken + ", authToken=" + authToken + "]";
	}

}
