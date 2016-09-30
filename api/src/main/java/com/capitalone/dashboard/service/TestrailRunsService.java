package com.capitalone.dashboard.service;

/**
 * Created by root on 30/9/16.
 */
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

import com.capitalone.dashboard.model.DataResponse;
public interface TestrailRunsService {
    DataResponse<JSONObject> getTestrailRuns(ObjectId componentId);
    
}
