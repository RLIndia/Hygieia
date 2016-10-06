package com.capitalone.dashboard.model;

/**
 * Created by vinod on 30/9/16.
 */
import org.bson.types.ObjectId;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "testrail_runs")
public class TestRailRuns extends BaseModel{


    @Indexed
    private String runId;
    private String completedDate;
    private Long failedCount;
    private String milestoneId;
    private Long blockedCount;
    private Long passedCount;
    private String name;
    private String projectId;
    private Long retestCount;
    private Long untestedCount;
    private String url;
    private ObjectId collectorItemId;

    public String getRunid() {
        return runId;
    }

    public void setRunid(String runId) {
        this.runId = runId;
    }


    public String getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(String completedDate) {
        this.completedDate = completedDate;
    }

    public Long getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(Long failedCount) {
        this.failedCount = failedCount;
    }

    public String getMilestoneId() {
        return milestoneId;
    }

    public void setMilestoneId(String milestoneId) {
        this.milestoneId = milestoneId;
    }

    public Long getBlockedCount() {
        return blockedCount;
    }

    public void setBlockedCount(Long blockedCount) {
        this.blockedCount = blockedCount;
    }

    public Long getPassedCount() {
        return passedCount;
    }

    public void setPassedCount(Long passedCount) {
        this.passedCount = passedCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public Long getRetestCount() {
        return retestCount;
    }

    public void setRetestCount(Long retestCount) {
        this.retestCount = retestCount;
    }

    public Long getUntestedCount() {
        return untestedCount;
    }

    public void setUntestedCount(Long untestedCount) {
        this.untestedCount = untestedCount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ObjectId getCollectorItemId() {
        return collectorItemId;
    }

    public void setCollectorItemId(ObjectId collectorItemId) {
        this.collectorItemId = collectorItemId;
    }

}
