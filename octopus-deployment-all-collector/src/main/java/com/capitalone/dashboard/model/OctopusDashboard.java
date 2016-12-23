package com.capitalone.dashboard.model;

/**
 * Created by vnair on 12/22/16.
 */
import com.capitalone.dashboard.model.OctopusProjectGroup;
import com.capitalone.dashboard.model.OctopusEnvironment;
import com.capitalone.dashboard.model.OctopusProject;
import com.capitalone.dashboard.model.EnvironmentProjectsAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class OctopusDashboard {
    private static final Logger LOGGER = LoggerFactory.getLogger(OctopusDashboard.class);
    private List<OctopusProject> octopusProjects;

    public List<OctopusProject> getOctopusProjects() {
        return octopusProjects;
    }

    public void setOctopusProjects(List<OctopusProject> octopusProjects) {
        this.octopusProjects = octopusProjects;
    }

    public List<OctopusEnvironment> getOctopusEnvironments() {
        return octopusEnvironments;
    }

    public void setOctopusEnvironments(List<OctopusEnvironment> octopusEnvironments) {
        this.octopusEnvironments = octopusEnvironments;
    }

    public List<OctopusProjectGroup> getOctopusProjectGroups() {
        return octopusProjectGroups;
    }

    public void setOctopusProjectGroups(List<OctopusProjectGroup> octopusProjectGroups) {
        this.octopusProjectGroups = octopusProjectGroups;
    }

    private  List<OctopusEnvironment> octopusEnvironments;
    private List<OctopusProjectGroup> octopusProjectGroups;

    public List<EnvironmentProjectsAll> getEnvironmentProjectsAll() {
        return environmentProjectsAll;
    }

    public void setEnvironmentProjectsAll(List<EnvironmentProjectsAll> environmentProjectsAll) {
        this.environmentProjectsAll = environmentProjectsAll;
    }

    private List<EnvironmentProjectsAll> environmentProjectsAll;


    public String getProjectGroupNameByID(String projectGroupId){
        for(OctopusProjectGroup opg : octopusProjectGroups){
           // LOGGER.info("IN group " + opg.getProjectGroupId() + " " +  projectGroupId);
            if(opg.getProjectGroupId().equals(projectGroupId)) {
                return opg.getProjectGroupName();
            }
        }
        return null;
    }

    public String getProjectGroupNameByProjectID(String projectId){
        for(OctopusProject op : octopusProjects){

            if(op.getProjectId().equals(projectId)) {
                return op.getProjectGroupName();
            }
        }
        return null;
    }

    public String getProjectNameByID(String projectId){
        for(OctopusProject op : octopusProjects){
            if(op.getProjectId().equals(projectId)) {
                return op.getProjectName();
            }
        }
        return null;
    }

    public String getEnvironmentNameByID(String environmentId){
        for(OctopusEnvironment env : octopusEnvironments){
            if(env.getEnvId().equals(environmentId)) {
                return env.getEnvName();
            }
        }
        return null;
    }


}
