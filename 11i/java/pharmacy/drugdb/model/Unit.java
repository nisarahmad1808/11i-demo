package inpatientWeb.pharmacy.drugdb.model;

public class Unit implements Comparable<Unit>{

	private String unit;
	private String unitId;
	private boolean applicable;
	
	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public boolean isApplicable() {
		return applicable;
	}

	public void setApplicable(boolean applicable) {
		this.applicable = applicable;
	}

	@Override
	public int compareTo(Unit o) {
		int c = Boolean.valueOf(o.isApplicable()).compareTo(Boolean.valueOf(this.isApplicable()));
	    if (c == 0)
	    	c = this.getUnit().compareTo(o.getUnit());
		return c;
	}

	@Override
	public String toString() {
		return "Unit [unit=" + unit + ", unitId=" + unitId + ", applicable=" + applicable + "]";
	}


}
