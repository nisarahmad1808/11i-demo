package inpatientWeb.pharmacy.drugdb.model;

public class DrugRoute{
	
	private String routeId;
	private String routeName;
	
	public String getRouteId() {
		return routeId;
	}
	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}
	public String getRouteName() {
		return routeName;
	}
	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}
	@Override
	public String toString() {
		return "DrugRoute [routeId=" + routeId + ", routeName=" + routeName + "]";
	}

	
}
