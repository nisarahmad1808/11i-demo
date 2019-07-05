package inpatientWeb.pharmacy.billingdata.exception;

/**
 * @author bharat tulsiyani on Jun 15, 2018
 */
public class PharmacyBillingException extends Exception {

	private static final long serialVersionUID = 2817415546287424594L;

	public PharmacyBillingException() {
		super();
	}

	public PharmacyBillingException(String message) {
		super(message);
	}

	public PharmacyBillingException(String message, Throwable cause) {
		super(message, cause);
	}

	public PharmacyBillingException(Throwable cause) {
		super(cause);
	}
}
