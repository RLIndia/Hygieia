package com.capitalone.dashboard.service;

/**
 * Created by vinod on 25/9/16.
 */


import org.json.simple.JSONObject;

import com.capitalone.dashboard.model.DataResponse;
public interface CatalystDeployHistoryService {

    DataResponse<JSONObject> getCatalystDeploymentHistory(String taskId);

}
