package inpatientWeb.pharmacy.interfaces.outbound.dto;

import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.DispenseDrugComponentType;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.IsPrimaryDrug;

public class DispensingRxDetailsModel {
	//** 
	private DispenseDrugComponentType rDispenseDrugComponentType;
	//** 
	private IsPrimaryDrug isPrimary;
	//** Unique Drug Dispense Code 
	private String rDispenseDrugCode = "";
	// ** Description of the Drug
	private String rDispenseDrugDesc="";
	//** Drug Strength
	private String oDispenseDrugStrength = "";
	//**Drug Strength Unit
	private String oDispenseDrugStrengthUnit = "";
	//
	private String oDispenseDrugFormulationCode = "";
	//
	private String oDispenseDrugFormulationDesc = "";
	//** Individual calculated drug amount for placed order
	private String rDispenseDrugComponentAmount = "";
	//** Individual calculated drug amount unit for placed order
	private String rDispenseDrugComponentUnit = "";
	
	private String rDispenseDrugOrderDose = "";
	
	private String rDispenseDrugOrderDoseUnit = "";
	
	public DispenseDrugComponentType getrDispenseDrugComponentType() {
		return rDispenseDrugComponentType;
	}
	public void setrDispenseDrugComponentType(
			DispenseDrugComponentType rDispenseDrugComponentType) {
		this.rDispenseDrugComponentType = rDispenseDrugComponentType;
	}
	public IsPrimaryDrug getIsPrimary() {
		return isPrimary;
	}
	public void setIsPrimary(IsPrimaryDrug isPrimary) {
		this.isPrimary = isPrimary;
	}
	public String getrDispenseDrugCode() {
		return rDispenseDrugCode;
	}
	public void setrDispenseDrugCode(String rDispenseDrugCode) {
		this.rDispenseDrugCode = rDispenseDrugCode;
	}
	public String getrDispenseDrugDesc() {
		return rDispenseDrugDesc;
	}
	public void setrDispenseDrugDesc(String rDispenseDrugDesc) {
		this.rDispenseDrugDesc = rDispenseDrugDesc;
	}
	public String getoDispenseDrugStrength() {
		return oDispenseDrugStrength;
	}
	public void setoDispenseDrugStrength(String oDispenseDrugStrength) {
		this.oDispenseDrugStrength = oDispenseDrugStrength;
	}
	public String getoDispenseDrugStrengthUnit() {
		return oDispenseDrugStrengthUnit;
	}
	public void setoDispenseDrugStrengthUnit(String oDispenseDrugStrengthUnit) {
		this.oDispenseDrugStrengthUnit = oDispenseDrugStrengthUnit;
	}
	public String getoDispenseDrugFormulationCode() {
		return oDispenseDrugFormulationCode;
	}
	public void setoDispenseDrugFormulationCode(String oDispenseDrugFormulationCode) {
		this.oDispenseDrugFormulationCode = oDispenseDrugFormulationCode;
	}
	public String getoDispenseDrugFormulationDesc() {
		return oDispenseDrugFormulationDesc;
	}
	public void setoDispenseDrugFormulationDesc(String oDispenseDrugFormulationDesc) {
		this.oDispenseDrugFormulationDesc = oDispenseDrugFormulationDesc;
	}
	public String getrDispenseDrugComponentUnit() {
		return rDispenseDrugComponentUnit;
	}
	public void setrDispenseDrugComponentUnit(String rDispenseDrugComponentUnit) {
		this.rDispenseDrugComponentUnit = rDispenseDrugComponentUnit;
	}
	public String getrDispenseDrugComponentAmount() {
		return rDispenseDrugComponentAmount;
	}
	public void setrDispenseDrugComponentAmount(String rDispenseDrugComponentAmount) {
		this.rDispenseDrugComponentAmount = rDispenseDrugComponentAmount;
	}
	public String getrDispenseDrugOrderDose() {
		return rDispenseDrugOrderDose;
	}
	public void setrDispenseDrugOrderDose(String rDispenseDrugOrderDose) {
		this.rDispenseDrugOrderDose = rDispenseDrugOrderDose;
	}
	public String getrDispenseDrugOrderDoseUnit() {
		return rDispenseDrugOrderDoseUnit;
	}
	public void setrDispenseDrugOrderDoseUnit(String rDispenseDrugOrderDoseUnit) {
		this.rDispenseDrugOrderDoseUnit = rDispenseDrugOrderDoseUnit;
	}
}
