package inpatientWeb.pharmacy.interaction.model;

public class InteractionDrug {
	
	private String idType;
	private String id;
	private Object order;
	
	public String getIdType() {
		return idType;
	}
	public void setIdType(String idType) {
		this.idType = idType;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Object getOrder() {
		return order;
	}
	public void setOrder(Object order) {
		this.order = order;
	}
	
	@Override
	public String toString() {
		return "InteractionDrug [idType=" + idType + ", id=" + id + ", order=" + order + "]";
	}
}
