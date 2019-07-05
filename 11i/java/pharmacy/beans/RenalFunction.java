package inpatientWeb.pharmacy.beans;

public class RenalFunction {
	
	private double value;
	private String unit;
	private String type;
	
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return "RenalFunction [value=" + value + ", unit=" + unit + ", type=" + type + "]";
	}
}
