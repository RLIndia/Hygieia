package com.capitalone.dashboard.model;

/**
 * Created by vinod on 19/9/16.
 */
import org.bson.types.ObjectId;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "catalysttask_history")
public class CatalystTaskHistory extends BaseModel{

    @Indexed
    private String taskName;

    public String getTaskName() {
        return taskName;
    }

    public CatalystTaskHistory setTaskName(String taskName) {
        this.taskName = taskName;
        return this;
    }

    public String getTaskId() {
        return taskId;
    }

    public CatalystTaskHistory setTaskId(String taskId) {
        this.taskId = taskId;
        return this;
    }

    public String getExecutedDate() {
        return executedDate;
    }

    public CatalystTaskHistory setExecutedDate(String executedDate) {
        this.executedDate = executedDate;
        return this;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public CatalystTaskHistory setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
        return this;
    }

    @Indexed
    private String taskId;

    @Indexed
    private String executedDate;

    private String taskStatus;



}
