package com.capitalone.dashboard.collector;

import java.util.List;

import com.capitalone.dashboard.model.ApplicationDeploymentHistoryItem;
import com.capitalone.dashboard.model.OctopusEnvironment;
//import com.capitalone.dashboard.model.OctopusApplication;

public interface OctopusClient {


   // List<OctopusApplication> getApplications();


    List<OctopusEnvironment> getEnvironments();

   // List<ApplicationDeploymentHistoryItem> getApplicationDeploymentHistory(OctopusApplication application);

  //  List<ApplicationDeploymentHistoryItem> getApplicationDeploymentHistory(OctopusApplication application,String environments);

    void setContext(int sc);
    int getContext();


}
