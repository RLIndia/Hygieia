package com.capitalone.dashboard.service;

/**
 * Created by vinod on 1/10/16.
 */

import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

import com.capitalone.dashboard.model.DataResponse;

public interface TestrailRunsResultsService {
    DataResponse<JSONObject> getTestrailRunsResults(String runId,String milestoneId,String projectId);
}
