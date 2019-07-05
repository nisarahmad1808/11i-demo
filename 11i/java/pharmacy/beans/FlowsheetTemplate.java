package inpatientWeb.pharmacy.beans;

import java.util.ArrayList;

public class FlowsheetTemplate {
	
	private int id;
	private String name;
	private int deleteFlag;
	private int episodeEncId;
	
	private ArrayList<FlowsheetTemplateItem> flowsheetItems;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getDeleteFlag() {
		return deleteFlag;
	}
	public void setDeleteFlag(int deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	public int getEpisodeEncId() {
		return episodeEncId;
	}
	public void setEpisodeEncId(int episodeEncId) {
		this.episodeEncId = episodeEncId;
	}
	public ArrayList<FlowsheetTemplateItem> getFlowsheetItems() {
		return flowsheetItems;
	}
	public void setFlowsheetItems(ArrayList<FlowsheetTemplateItem> flowsheetItems) {
		this.flowsheetItems = flowsheetItems;
	}
}
