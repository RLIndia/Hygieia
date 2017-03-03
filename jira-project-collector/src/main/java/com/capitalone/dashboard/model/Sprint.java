package com.capitalone.dashboard.model;

public class Sprint {
	
	private String sprintId;
	private String sprintName;
	private String startTime;
	private String endTime;
	private String activeBoardId;
	
	public String getActiveBoardId() {
		return activeBoardId;
	}
	public String getSprintName() {
		return sprintName;
	}
	public void setSprintName(String sprintName) {
		this.sprintName = sprintName;
	}
	public String getSprintId() {
		return sprintId;
	}
	public void setSprintId(String sprintId) {
		this.sprintId = sprintId;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public void setActiveBoardId(String activeBoardId) {
		this.activeBoardId = activeBoardId;		
	}
	
}
