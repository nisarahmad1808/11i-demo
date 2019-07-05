package inpatientWeb.pharmacy.drugdb.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class InteractionRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long episodeEncId;
	private Long moduleEncId;
	private String calledFrom;
	private boolean screenFlag;
	private String requestedFor;
	private int facilityId;
	private int userId;
	private int encType;
	private List<Long> recentOrderList;
	
	private transient List<Map<String, Object>> drugList;
	
	public Long getEpisodeEncId() {
		return episodeEncId;
	}
	public void setEpisodeEncId(Long episodeEncId) {
		this.episodeEncId = episodeEncId;
	}
	public Long getModuleEncId() {
		return moduleEncId;
	}
	public void setModuleEncId(Long moduleEncId) {
		this.moduleEncId = moduleEncId;
	}
	public String getCalledFrom() {
		return calledFrom;
	}
	public void setCalledFrom(String calledFrom) {
		this.calledFrom = calledFrom;
	}
	public boolean isScreenFlag() {
		return screenFlag;
	}
	public void setScreenFlag(boolean screenFlag) {
		this.screenFlag = screenFlag;
	}
	public String getRequestedFor() {
		return requestedFor;
	}
	public void setRequestedFor(String requestedFor) {
		this.requestedFor = requestedFor;
	}
	public int getFacilityId() {
		return facilityId;
	}
	public void setFacilityId(int facilityId) {
		this.facilityId = facilityId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public List<Map<String, Object>> getDrugList() {
		return drugList;
	}
	public void setDrugList(List<Map<String, Object>> drugList) {
		this.drugList = drugList;
	}
	public int getEncType() {
		return encType;
	}
	public void setEncType(int encType) {
		this.encType = encType;
	}
	public List<Long> getRecentOrderList() {
		return recentOrderList;
	}
	public void setRecentOrderList(List<Long> recentOrderList) {
		this.recentOrderList = recentOrderList;
	}
	@Override
	public String toString() {
		return "InteractionRequest [episodeEncId=" + episodeEncId + ", moduleEncId=" + moduleEncId + ", calledFrom="
				+ calledFrom + ", screenFlag=" + screenFlag + ", requestedFor=" + requestedFor + ", facilityId="
				+ facilityId + ", userId=" + userId + ", encType=" + encType + ", recentOrderList=" + recentOrderList + "]";
	}
}
