package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "defect_injection")
public class DefectInjection extends BaseModel{
	
	private String sprintName;
	private int defectCount;
	private double achievedPoints;	
	private String sprintId;
	@Indexed
	private String projectId;
	
	private ObjectId collectorItemId; 
	
	
	
	public String getSprintId() {
		return sprintId;
	}
	public void setSprintId(String sprintId) {
		this.sprintId = sprintId;	
	}	
	
	public String getProjectId() {
		return projectId;
	}
	public DefectInjection setProjectId(String projectId) {
		this.projectId = projectId;
		return this;
	}
	
	public ObjectId getCollectorItemId() {
		return collectorItemId;
	}
	public void setCollectorItemId(ObjectId collectorItemId) {
		this.collectorItemId = collectorItemId;
	}
	public String getSprintName() {
		return sprintName;
	}
	public void setSprintName(String sprintName) {
		this.sprintName = sprintName;
	}
	public int getDefectCount() {
		return defectCount;
	}
	public void setDefectCount(int defectCount) {
		this.defectCount = defectCount;
	}
	public double getAchievedPoints() {
		return achievedPoints;
	}
	public void setAchievedPoints(double achievedPoints) {
		this.achievedPoints = achievedPoints;
	}
	
	

}
