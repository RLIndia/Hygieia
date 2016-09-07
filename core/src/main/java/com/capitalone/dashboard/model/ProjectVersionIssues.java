package com.capitalone.dashboard.model;

/**
 * Created by root on 7/9/16.
 */
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "projectversion-issues")
public class ProjectVersionIssues extends BaseModel{
    public String getProjectID() {
        return projectID;
    }

    public ProjectVersionIssues setProjectID(String projectID) {
        this.projectID = projectID;
        return this;
    }

    public String getVersionID() {
        return versionID;
    }

    public ProjectVersionIssues setVersionID(String versionID) {
        this.versionID = versionID;
        return this;
    }

    public String getIssueStatus() {
        return issueStatus;
    }

    public ProjectVersionIssues setIssueStatus(String issueStatus) {
        this.issueStatus = issueStatus;
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
    private String projectID;

    @Indexed
    private String versionID;
    private String issueStatus;
    @Indexed
    private String changeDate;
    private String reportedDate;


}
