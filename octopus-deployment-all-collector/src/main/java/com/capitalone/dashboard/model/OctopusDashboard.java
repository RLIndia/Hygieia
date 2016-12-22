package com.capitalone.dashboard.model;

/**
 * Created by vnair on 12/22/16.
 */
import com.capitalone.dashboard.model.OctopusProjectGroup;
import com.capitalone.dashboard.model.OctopusEnvironment;
import com.capitalone.dashboard.model.OctopusProject;
import com.capitalone.dashboard.model.EnvironmentProjectsAll;
import java.util.List;

public class OctopusDashboard {
    private List<OctopusProject> octopusProjects;
    private  List<OctopusEnvironment> octopusEnvironments;
    private List<OctopusProjectGroup> octopusProjectGroups;

    public List<EnvironmentProjectsAll> getEnvironmentProjectsAlls() {
        return environmentProjectsAll;
    }

    public void setEnvironmentProjectsAlls(List<EnvironmentProjectsAll> environmentProjectsAlls) {
        this.environmentProjectsAll = environmentProjectsAlls;
    }

    private List<EnvironmentProjectsAll> environmentProjectsAll;


    public String getProjectGroupNameByID(String projectGroupId){
        for(OctopusProjectGroup opg : octopusProjectGroups){
            if(opg.getProjectGroupId() == projectGroupId) {
                return opg.getProjectGroupName();
            }
        }
        return null;
    }

    public String getProjectNameByID(String projectId){
        for(OctopusProject op : octopusProjects){
            if(op.getProjectId() == projectId) {
                return op.getProjectName();
            }
        }
        return null;
    }

    public String getEnvironmentNameByID(String environmentId){
        for(OctopusEnvironment env : octopusEnvironments){
            if(env.getEnvId() == environmentId) {
                return env.getEnvName();
            }
        }
        return null;
    }


}
