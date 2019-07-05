package inpatientWeb.admin.pharmacySettings.formularySetup.modal;
public class FormularyImmunization
{
	private int itemid = -1;
	private String itemName = "";
	private String DateOnVIS = "";
	private int displayIndex = 0;
	private String VISCVXCode = "";
	
	public FormularyImmunization()
	{
		
	}
	public int getItemid() {
		return itemid;
	}
	public void setItemid(int itemid) {
		this.itemid = itemid;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getDateOnVIS() {
		return DateOnVIS;
	}
	public void setDateOnVIS(String dateOnVIS) {
		DateOnVIS = dateOnVIS;
	}
	public int getDisplayIndex() {
		return displayIndex;
	}
	public void setDisplayIndex(int displayIndex) {
		this.displayIndex = displayIndex;
	}
	public String getVISCVXCode() {
		return VISCVXCode;
	}
	public void setVISCVXCode(String vISCVXCode) {
		VISCVXCode = vISCVXCode;
	}
		
}