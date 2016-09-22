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

    @Indexed

    private String version;
    private String nodeIds;

    @Indexed
    private String executedDate;

    private String lastTaskStatus;




}
