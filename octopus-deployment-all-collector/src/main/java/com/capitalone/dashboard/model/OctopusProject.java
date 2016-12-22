package com.capitalone.dashboard.model;

/**
 * Created by vnair on 12/22/16.
 */
public class OctopusProject {
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

    private String projectId;
    private String projectName;

}
