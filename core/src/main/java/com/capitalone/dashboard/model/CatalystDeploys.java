package com.capitalone.dashboard.model;

/**
 * Created by vinod on 19/9/16.
 */
import org.bson.types.ObjectId;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "catalystdeployments")
public class CatalystDeploys extends BaseModel{

    @Indexed
    private String envName;
    private String taskId;


    @Indexed

    private String version;
    private String nodeIds;

    @Indexed
    private String executedDate;

    private String lastTaskStatus;


    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    private String repository;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    private String projectId;


    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNodeIds() {
        return nodeIds;
    }

    public void setNodeIds(String nodeIds) {
        this.nodeIds = nodeIds;
    }

    public String getExecutedDate() {
        return executedDate;
    }

    public void setExecutedDate(String executedDate) {
        this.executedDate = executedDate;
    }

    public String getLastTaskStatus() {
        return lastTaskStatus;
    }

    public void setLastTaskStatus(String lastTaskStatus) {
        this.lastTaskStatus = lastTaskStatus;
    }

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
