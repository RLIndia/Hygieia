package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents deployable units (components) deployed to an environment.
 */
@Document(collection = "octopus_deployment_all")
public class EnvironmentProjectsAll extends BaseModel {
    /**
     * Deploy collectorItemId
     */
    private ObjectId collectorId;
    private String environmentId;
    private String environmentName;
    private String environmentUrl;
    private String projectId;

    public ObjectId getCollectorId() {
        return collectorId;
    }

    public void setCollectorId(ObjectId collectorId) {
        this.collectorId = collectorId;
    }

    public String getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(String environmentId) {
        this.environmentId = environmentId;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

    public String getEnvironmentUrl() {
        return environmentUrl;
    }

    public void setEnvironmentUrl(String environmentUrl) {
        this.environmentUrl = environmentUrl;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getReleaseVersion() {
        return releaseVersion;
    }

    public void setReleaseVersion(String releaseVersion) {
        this.releaseVersion = releaseVersion;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public long getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(long completedDate) {
        this.completedDate = completedDate;
    }

    public long getAsOfDate() {
        return asOfDate;
    }

    public void setAsOfDate(long asOfDate) {
        this.asOfDate = asOfDate;
    }

    public String getProjectGroupId() {
        return projectGroupId;
    }

    public void setProjectGroupId(String projectGroupId) {
        this.projectGroupId = projectGroupId;
    }

    public String getProjectGroupName() {
        return projectGroupName;
    }

    public void setProjectGroupName(String projectGroupName) {
        this.projectGroupName = projectGroupName;
    }

    private String projectName;
    private String releaseVersion;
    private boolean status;
    private long completedDate;
    private long asOfDate;
    private String projectGroupId;
    private String projectGroupName;




}
