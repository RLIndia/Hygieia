package com.capitalone.dashboard.service;

/**
 * Created by vinod on 21/9/16.
 */
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

import com.capitalone.dashboard.model.DataResponse;

public interface CatalystDeployService {
    DataResponse<JSONObject> getCatalystDeployments(ObjectId componentId);
}

