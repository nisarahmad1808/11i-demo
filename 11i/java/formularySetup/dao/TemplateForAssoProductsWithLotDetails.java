package inpatientWeb.admin.pharmacySettings.formularySetup.dao;

/**
 * The Class TemplateForAssoProductsWithLotDetails.
 *
 * @author bharat tulsiyani
 * The Class TemplateForAssoProductsLotDetails.
 */
public class TemplateForAssoProductsWithLotDetails extends TemplateForAssoProducts {
	
	private String lotId = ""; 
	
	/** The lotno. */
	private String lotno = "";	
	
	/** The lot type. */
	private String lotType = "";
	
	/** The expiry date. */
	private String expiryDate = "";
	
	public String getLotId() {
		return lotId;
	}

	public void setLotId(String lotId) {
		this.lotId = lotId;
	}

	/**
	 * Gets the lotno.
	 *
	 * @return the lotno
	 */
	public String getLotno() {
		return lotno;
	}
	
	/**
	 * Sets the lotno.
	 *
	 * @param lotno the new lotno
	 */
	public void setLotno(String lotno) {
		this.lotno = lotno;
	}
	
	/**
	 * Gets the lot type.
	 *
	 * @return the lot type
	 */
	public String getLotType() {
		return lotType;
	}
	
	/**
	 * Sets the lot type.
	 *
	 * @param lotType the new lot type
	 */
	public void setLotType(String lotType) {
		this.lotType = lotType;
	}
	
	/**
	 * Gets the expiry date.
	 *
	 * @return the expiry date
	 */
	public String getExpiryDate() {
		return expiryDate;
	}
	
	/**
	 * Sets the expiry date.
	 *
	 * @param expiryDate the new expiry date
	 */
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	
	/* (non-Javadoc)
	 * @see inpatientWeb.admin.pharmacySettings.formularySetup.dao.TemplateForAssoProducts#toString()
	 */
	@Override
	public String toString() {
		return super.toString() + "TemplateForAssoProductsWithLotDetails [lotId=" + lotId + ", lotno=" + lotno + ", lotType=" + lotType
				+ ", expiryDate=" + expiryDate + "]";
	}

}