package com.capitalone.dashboard.model;
import java.util.Date;

import org.bson.types.ObjectId;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sprint_velocity")
public class SprintVelocity extends BaseModel {
	
	private String sprintId;
	private String sprintName;
	private String sprintStatus;
	private String committed;
	private String completed;
	private String versionId;
	@Indexed
	private String projectId;

	private ObjectId collectorItemId;
	private int storyCount;
	private String completedSum;
	private String notCompletedSum;
	private String allIssuesSum;
	private String outOfSprintSum;
	private String puntedSum;
	private String startDate;
	private String endDate;
	private String midSprintDate;
	private double midPointSum;
	private int acceptanceCriteria;    

    public int getAcceptanceCriteria() {
		return acceptanceCriteria;
	}

	public void setAcceptanceCriteria(int acceptanceCriteria) {
		this.acceptanceCriteria = acceptanceCriteria;
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
	public String getSprintStatus() {
		return sprintStatus;
	}
	public void setSprintStatus(String sprintStatus) {
		this.sprintStatus = sprintStatus;
	}
	public String getSprintId() {
		return sprintId;
	}
	public SprintVelocity setSprintId(String sprintId) {
		this.sprintId = sprintId;
		return this;
	}
	public String getCommitted() {
		return committed;
	}
	public void setCommitted(String committed) {
		this.committed = committed;
	}
	public String getCompleted() {
		return completed;
	}
	public void setCompleted(String completed) {
		this.completed = completed;
	}
	
	public String getVersionId() {
		return versionId;
	}
	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}
	public String getProjectId() {
		return projectId;
	}
	public SprintVelocity setProjectId(String projectId) {
		this.projectId = projectId;
		return this;
	}

	public void setStoryCount(int total) {
		// TODO Auto-generated method stub
		this.storyCount = total;
	}

	public int getStoryCount() {
		return storyCount;
	}

	public void setCompletedSum(String string) {
		// TODO Auto-generated method stub
		this.completedSum = string;
	}

	public void setNotCompletedSum(String string) {
		// TODO Auto-generated method stub
		this.notCompletedSum = string;
	}

	public void setAllIssuesSum(String string) {
		// TODO Auto-generated method stub
		this.allIssuesSum = string;
	}

	public void setOutOfSprintSum(String string) {
		// TODO Auto-generated method stub
		this.outOfSprintSum = string;
	}

	public String getCompletedSum() {
		return completedSum;
	}

	public String getNotCompletedSum() {
		return notCompletedSum;
	}

	public String getAllIssuesSum() {
		return allIssuesSum;
	}

	public String getOutOfSprintSum() {
		return outOfSprintSum;
	}

	public String getPuntedSum() {
		return puntedSum;
	}

	public void setPuntedSum(String string) {
		// TODO Auto-generated method stub
		this.puntedSum = string;
	}

	public void setStartDate(String string) {
		this.startDate = string;
		
	}

	public void setEndDate(String string) {
		// TODO Auto-generated method stub
		this.endDate = string;
	}

	public void setMidSprintDate(String string) {
		// TODO Auto-generated method stub
		this.midSprintDate = string;
	}

	public String getStartDate() {
		return startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public String getMidSprintDate() {
		return midSprintDate;
	}

	public void setMidPointSum(double midPointSum) {
		// TODO Auto-generated method stub
		this.midPointSum = midPointSum;
	}

	public double getMidPointSum() {
		return midPointSum;
	}

	
	
}
