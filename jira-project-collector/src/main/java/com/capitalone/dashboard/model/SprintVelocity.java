package com.capitalone.dashboard.model;
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
}
