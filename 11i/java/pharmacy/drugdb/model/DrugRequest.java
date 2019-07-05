package inpatientWeb.pharmacy.drugdb.model;

import java.io.Serializable;

/**
 * The Class DrugRequest.
 */
public class DrugRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8233100664680417122L;
	
	private String id;
	private String idType;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIdType() {
		return idType;
	}
	public void setIdType(String idType) {
		this.idType = idType;
	}

	@Override
	public String toString() {
		return "DrugRequest [id=" + id + ", idType=" + idType + "]";
	}
}
