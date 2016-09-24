package com.capitalone.dashboard.model;

/**
 * Created by root on 24/9/16.
 */

import org.bson.types.ObjectId;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "catalystdeploymenttaskhistory")
public class CatalystDeploysTask extends BaseModel {

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getExecutedDate() {
        return executedDate;
    }

    public void setExecutedDate(String executedDate) {
        this.executedDate = executedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNodeNames() {
        return nodeNames;
    }

    public void setNodeNames(String nodeNames) {
        this.nodeNames = nodeNames;
    }

    @Indexed
    private String taskId;
    private String taskName;
    private String executedDate;
    private String status;
    private String nodeNames;

    private ObjectId collectorId; //used for clean up activity from the collector
    private long timestamp;

    public ObjectId getCollectorId() {
        return collectorId;
    }

    public void setCollectorId(ObjectId collectorItemId) {
        this.collectorId = collectorId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


}
