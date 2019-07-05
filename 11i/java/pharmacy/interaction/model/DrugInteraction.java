package inpatientWeb.pharmacy.interaction.model;

import java.util.List;
import java.util.Map;

/**
 * @author Disahgan Bhavsar
 * @Copyright: eClinicalWorks LLC.
 * @Date: "July 27, 2017"
 */
public class DrugInteraction {
	
	private int encounterId;
	private int severityCode;
	private String description;
	private int overrideReason;
	private int interactionCategory;
	private int overrideBy;
	private List<Map<String, Object>> orderIdList;
	private String overrideDateTime;
	private Long conditionTypeId;
	
	public int getEncounterId() {
		return encounterId;
	}
	public void setEncounterId(int encounterId) {
		this.encounterId = encounterId;
	}
	public int getSeverityCode() {
		return severityCode;
	}
	public void setSeverityCode(int severityCode) {
		this.severityCode = severityCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getOverrideReason() {
		return overrideReason;
	}
	public void setOverrideReason(int overrideReason) {
		this.overrideReason = overrideReason;
	}
	public int getInteractionCategory() {
		return interactionCategory;
	}
	public void setInteractionCategory(int interactionCategory) {
		this.interactionCategory = interactionCategory;
	}
	public int getOverrideBy() {
		return overrideBy;
	}
	public void setOverrideBy(int overrideBy) {
		this.overrideBy = overrideBy;
	}
	public String getOverrideDateTime() {
		return overrideDateTime;
	}
	public void setOverrideDateTime(String overrideDateTime) {
		this.overrideDateTime = overrideDateTime;
	}
	public List<Map<String, Object>> getOrderIdList() {
		return orderIdList;
	}
	public void setOrderIdList(List<Map<String, Object>> orderIdList) {
		this.orderIdList = orderIdList;
	}
	public Long getConditionTypeId() {
		return conditionTypeId;
	}
	public void setConditionTypeId(Long conditionTypeId) {
		this.conditionTypeId = conditionTypeId;
	}
	@Override
	public String toString() {
		return "DrugInteraction [encounterId=" + encounterId + ", severityCode=" + severityCode + ", description="
				+ description + ", overrideReason=" + overrideReason + ", interactionCategory=" + interactionCategory
				+ ", overrideBy=" + overrideBy + ", orderIdList=" + orderIdList + ", overrideDateTime="
				+ overrideDateTime + ", conditionTypeId=" + conditionTypeId + "]";
	}
}
