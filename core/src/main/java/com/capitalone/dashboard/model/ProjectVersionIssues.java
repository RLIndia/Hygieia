package com.capitalone.dashboard.model;

/**
 * Created by Vinod on 7/9/16.
 */
//import org.bson.types.ObjectId;
import org.bson.types.ObjectId;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "projectversion_issues")
public class ProjectVersionIssues extends BaseModel{


    @Indexed
    private String projectName;

   

    @Indexed
    private String versionName;
    
    private String issueStatus;
    private String statusName;
    
    private String issueId;
    private String issueDescription;
    @Indexed
    private String changeDate;
    private String reportedDate;
    private String sprintName;
    private String sprintId;
    private String key;
    private double storyPoint;


    private ObjectId collectorItemId;
    private long timestamp;

    public ObjectId getCollectorItemId() {
        return collectorItemId;
    }

    public void setCollectorItemId(ObjectId collectorItemId) {
        this.collectorItemId = collectorItemId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getProjectName() {
        return projectName;
    }

    public ProjectVersionIssues setProjectName(String projectName) {
        this.projectName = projectName;
        return this;
    }

    public String getVersionName() {
        return versionName;
    }

    public ProjectVersionIssues setVersionName(String versionName) {
        this.versionName = versionName;
        return this;
    }

    public String getIssueStatus() {
        return issueStatus;
    }

    public ProjectVersionIssues setIssueStatus(String issueStatus) {
        this.issueStatus = issueStatus;
        return this;
    }

    public String getIssueId() {
        return issueId;
    }

    public ProjectVersionIssues setIssueId(String issueId) {
        this.issueId = issueId;
        return this;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public ProjectVersionIssues setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
        return this;
    }

    public String getChangeDate() {
        return changeDate;
    }

    public ProjectVersionIssues setChangeDate(String changeDate) {
        this.changeDate = changeDate;
        return this;
    }

    public String getReportedDate() {
        return reportedDate;
    }

    public ProjectVersionIssues setReportedDate(String reportedDate) {
        this.reportedDate = reportedDate;
        return this;
    }

	public String getSprintId() {
		return sprintId;
	}

	public void setSprintId(String sprintId) {
		this.sprintId = sprintId;
	}

	public String getSprintName() {
		return sprintName;
	}

	public void setSprintName(String sprintName) {
		this.sprintName = sprintName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public double getStoryPoint() {
		return storyPoint;
	}

	public void setStoryPoint(double storyPoint) {
		this.storyPoint = storyPoint;
	}


}
