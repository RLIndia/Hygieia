package com.capitalone.dashboard.service;

/**
 * Created by Vinod on 12/9/16.
 */

import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

import com.capitalone.dashboard.model.DataResponse;

public interface SprintVelocityService {
    DataResponse<JSONObject> getSprintVelocityReport(ObjectId componentId);
}
