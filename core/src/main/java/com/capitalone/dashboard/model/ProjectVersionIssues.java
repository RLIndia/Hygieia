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

    @Indexed
    private String versionName;
    private String issueStatus;
    private String issueId;
    private String issueDescription;
    @Indexed
    private String changeDate;
    private String reportedDate;


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


}
