package inpatientWeb.pharmacy.drugdb.core.model.impl;

import java.util.List;

import inpatientWeb.Global.medicationhelper.msclinical.constant.ResultCodeConstant;
import inpatientWeb.pharmacy.drugdb.core.model.IRequestData;
import inpatientWeb.pharmacy.drugdb.core.model.IResponseData;

/**
 * The Class ResponseData.
 *
 * @author Dishagna Bhavsar
 * @param <T>
 *            the generic type
 */
public class ResponseData<T> extends BaseResponse implements IResponseData<T> {

	/** The data. */
	private T data;
	private List<T> dataList;

	public ResponseData() {
		super();
	}

	public <E> ResponseData(ResultCodeConstant resultCodeConstant, String message, IRequestData<E> requestData) {
		super(resultCodeConstant, message, requestData);
	}

	public ResponseData(ResultCodeConstant resultCodeConstant, String message) {
		super(resultCodeConstant, message);
	}

	public <E> ResponseData(List<T> dataList, ResultCodeConstant resultCodeConstant) {
		super(resultCodeConstant, resultCodeConstant.getMessage());
		this.dataList = dataList;
	}

	public <E> ResponseData(T data, ResultCodeConstant resultCodeConstant, String message) {
		super(resultCodeConstant, message);
		this.data = data;
	}
	
	public <E> ResponseData(List<T> dataList, ResultCodeConstant resultCodeConstant, String message) { 
		super(resultCodeConstant, message);
	    this.dataList = dataList; 
	}
	 

	/**
	 * Instantiates a new response data.
	 *
	 * @param <E>
	 *            the element type
	 * @param data
	 *            the data
	 * @param resultCodeConstant
	 *            the result code constant
	 * @param requestData
	 *            the request data
	 */
	public <E> ResponseData(T data, ResultCodeConstant resultCodeConstant, IRequestData<E> requestData) {
		super(resultCodeConstant, resultCodeConstant.getMessage(), requestData);
		this.data = data;
	}

	/**
	 * Instantiates a new response data.
	 *
	 * @param <E>
	 *            the element type
	 * @param data
	 *            the data
	 * @param resultCodeConstant
	 *            the result code constant
	 * @param message
	 *            the message
	 * @param requestData
	 *            the request data
	 */
	public <E> ResponseData(T data, ResultCodeConstant resultCodeConstant, String message, IRequestData<E> requestData) {
		super(resultCodeConstant, message, requestData);
		this.data = data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ecwrx.msclinical.core.model.IResponseData#getData()
	 */
	@Override
	public T getData() {
		return data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ecwrx.msclinical.core.model.IResponseData#setData(java.lang.Object)
	 */
	@Override
	public void setData(T data) {
		this.data = data;
	}
	
	public List<T> getDataList() {
		return dataList;
	}

	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ecwrx.msclinical.core.model.impl.BaseResponse#toString()
	 */
	@Override
	public String toString() {
		return super.toString() + "ResponseData [data=" + data + ", dataList=" + dataList + "]";
	}

}
