package inpatientWeb.pharmacy.drugdb.model;

public class DispensibleDrug implements Comparable<DispensibleDrug>{
	private String dispensableDrugID;

	private String dispDrugDescription;

	private String drugName;

	private String drugNameID;
	
	private String route;

	private String routeID;

	private String formulation;

	private String formulationID;

	private String strength;
	
	private String routedDrugID;

	private String gpi;
	
	private String rxNorm;
	
	private String ndc;
	
	private String message;
	
	private String csaCode;
	
	private String rxOTC;
	
	private String drugType;
	
	/**
	 * @return the dispensableDrugID
	 */
	public String getDispensableDrugID() {
		return dispensableDrugID;
	}

	/**
	 * @param dispensableDrugID the dispensableDrugID to set
	 */
	public void setDispensableDrugID(String dispensableDrugID) {
		this.dispensableDrugID = dispensableDrugID;
	}

	/**
	 * @return the dispDrugDescription
	 */
	public String getDispDrugDescription() {
		return dispDrugDescription;
	}

	/**
	 * @param dispDrugDescription the dispDrugDescription to set
	 */
	public void setDispDrugDescription(String dispDrugDescription) {
		this.dispDrugDescription = dispDrugDescription;
	}

	/**
	 * @return the drugName
	 */
	public String getDrugName() {
		return drugName;
	}

	/**
	 * @param drugName the drugName to set
	 */
	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	/**
	 * @return the drugNameID
	 */
	public String getDrugNameID() {
		return drugNameID;
	}

	/**
	 * @param drugNameID the drugNameID to set
	 */
	public void setDrugNameID(String drugNameID) {
		this.drugNameID = drugNameID;
	}

	/**
	 * @return the route
	 */
	public String getRoute() {
		return route;
	}

	/**
	 * @param route the route to set
	 */
	public void setRoute(String route) {
		this.route = route;
	}

	/**
	 * @return the routeID
	 */
	public String getRouteID() {
		return routeID;
	}

	/**
	 * @param routeID the routeID to set
	 */
	public void setRouteID(String routeID) {
		this.routeID = routeID;
	}

	/**
	 * @return the formulation
	 */
	public String getFormulation() {
		return formulation;
	}

	/**
	 * @param formulation the formulation to set
	 */
	public void setFormulation(String formulation) {
		this.formulation = formulation;
	}

	/**
	 * @return the formulationID
	 */
	public String getFormulationID() {
		return formulationID;
	}

	/**
	 * @param formulationID the formulationID to set
	 */
	public void setFormulationID(String formulationID) {
		this.formulationID = formulationID;
	}

	/**
	 * @return the strength
	 */
	public String getStrength() {
		return strength;
	}

	/**
	 * @param strength the strength to set
	 */
	public void setStrength(String strength) {
		this.strength = strength;
	}

	/**
	 * @return the routedDrugID
	 */
	public String getRoutedDrugID() {
		return routedDrugID;
	}

	/**
	 * @param routedDrugID the routedDrugID to set
	 */
	public void setRoutedDrugID(String routedDrugID) {
		this.routedDrugID = routedDrugID;
	}

	/**
	 * Gets the gpi.
	 *
	 * @return the gpi
	 */
	public String getGpi() {
		return gpi;
	}

	public void setGpi(String gpi) {
		this.gpi = gpi;
	}
	
	/**
	 * Gets the rx norm.
	 *
	 * @return the rx norm
	 */
	public String getRxNorm() {
		return rxNorm;
	}

	public void setRxNorm(String rxNorm) {
		this.rxNorm = rxNorm;
	}
	

	public String getNdc() {
		return ndc;
	}

	public void setNdc(String ndc) {
		this.ndc = ndc;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCsaCode() {
		return csaCode;
	}

	public void setCsaCode(String csaCode) {
		this.csaCode = csaCode;
	}

	public String getRxOTC() {
		return rxOTC;
	}

	public void setRxOTC(String rxOTC) {
		this.rxOTC = rxOTC;
	}

	public String getDrugType() {
		return drugType;
	}

	public void setDrugType(String drugType) {
		this.drugType = drugType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(DispensibleDrug o) {
		 return this.getDispDrugDescription().compareTo(o.getDispDrugDescription());
	}
}
