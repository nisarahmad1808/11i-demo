package inpatientWeb.pharmacy.interfaces.util;

import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.DispensingInterfaceInboundStatus;
import inpatientWeb.pharmacy.interfaces.util.DispensingInterfaceConstants.DispensingInterfaceStatus;

/**
@author diveshg
*/
public class DispensingInterfaceCustomException extends Exception{
	private static final long serialVersionUID = 7271716525796239470L;
	private String logStatus;
	private String logStatusMessage;
	private String status;
	private String statusMessage;
	private int reconcile;
	
	public DispensingInterfaceCustomException(DispensingInterfaceStatus dispensingStatus){
		this.setStatus(dispensingStatus.getStatus());
		this.setStatusMessage(dispensingStatus.getMessage());
		this.setReconcile(dispensingStatus.getReconcile());
	}
	public DispensingInterfaceCustomException(DispensingInterfaceInboundStatus dispensingStatus){
		this.setLogStatus(dispensingStatus.getLogStatus());
		this.setLogStatusMessage(dispensingStatus.getLogStatusMessage());
		this.setStatus(dispensingStatus.getStatus());
		this.setStatusMessage(dispensingStatus.getStatusMessage());
		this.setReconcile(dispensingStatus.getReconcile());
	}
	public DispensingInterfaceCustomException(String status, String message,int reconcile){
		this.setStatus(status);
		this.setStatusMessage(message);
		this.setReconcile(reconcile);
	}
	public String getLogStatus() {
		return logStatus;
	}
	public void setLogStatus(String logStatus) {
		this.logStatus = logStatus;
	}
	public String getLogStatusMessage() {
		return logStatusMessage;
	}
	public void setLogStatusMessage(String logStatusMessage) {
		this.logStatusMessage = logStatusMessage;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusMessage() {
		return statusMessage;
	}
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	public int getReconcile() {
		return reconcile;
	}
	public void setReconcile(int reconcile) {
		this.reconcile = reconcile;
	}

}
