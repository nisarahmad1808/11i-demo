package inpatientWeb.pharmacy.beans;

import java.util.HashMap;
import java.util.List;

public class FlowsheetTemplateItem {
	
	private int id;
	private int flowsheetId;
	private int type;
	private int itemId;
	private String itemName;
	private int displayIndex;
	private int categoryLimit;
	private String goalLowerLimit;
	private String goalUpperLimit;
	private String groupName;
	private String labAttributes;
	private String displayInHeader;
	private int defaultAttribute;
	private int deleteFlag;
	private boolean isValDateTime;
	
	private List<HashMap<String, Object>> dataListPerDates;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getFlowsheetId() {
		return flowsheetId;
	}
	public void setFlowsheetId(int flowsheetId) {
		this.flowsheetId = flowsheetId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getItemId() {
		return itemId;
	}
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public int getDisplayIndex() {
		return displayIndex;
	}
	public void setDisplayIndex(int displayIndex) {
		this.displayIndex = displayIndex;
	}
	public int getCategoryLimit() {
		return categoryLimit;
	}
	public void setCategoryLimit(int categoryLimit) {
		this.categoryLimit = categoryLimit;
	}
	public String getGoalLowerLimit() {
		return goalLowerLimit;
	}
	public void setGoalLowerLimit(String goalLowerLimit) {
		this.goalLowerLimit = goalLowerLimit;
	}
	public String getGoalUpperLimit() {
		return goalUpperLimit;
	}
	public void setGoalUpperLimit(String goalUpperLimit) {
		this.goalUpperLimit = goalUpperLimit;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getLabAttributes() {
		return labAttributes;
	}
	public void setLabAttributes(String labAttributes) {
		this.labAttributes = labAttributes;
	}
	public String getDisplayInHeader() {
		return displayInHeader;
	}
	public void setDisplayInHeader(String displayInHeader) {
		this.displayInHeader = displayInHeader;
	}
	public int getDefaultAttribute() {
		return defaultAttribute;
	}
	public void setDefaultAttribute(int defaultAttribute) {
		this.defaultAttribute = defaultAttribute;
	}
	public int getDeleteFlag() {
		return deleteFlag;
	}
	public void setDeleteFlag(int deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	public boolean isValDateTime() {
		return isValDateTime;
	}
	public void setValDateTime(boolean isValDateTime) {
		this.isValDateTime = isValDateTime;
	}
	public List<HashMap<String, Object>> getDataListPerDates() {
		return dataListPerDates;
	}
	public void setDataListPerDates(List<HashMap<String, Object>> dataListPerDates) {
		this.dataListPerDates = dataListPerDates;
	}

}
