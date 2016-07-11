package com.capitalone.dashboard.collector;

import java.util.List;

import com.capitalone.dashboard.model.ApplicationDeploymentHistoryItem;
import com.capitalone.dashboard.model.Environment;
import com.capitalone.dashboard.model.OctopusApplication;

public interface OctopusClient {
	
	
    List<OctopusApplication> getApplications();

    
    List<Environment> getEnvironments();
    
    List<ApplicationDeploymentHistoryItem> getApplicationDeploymentHistory(OctopusApplication application);

}
