package com.capitalone.dashboard.service;

import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

import com.capitalone.dashboard.model.DataResponse;

public interface ProjectIssuesService {
	
	DataResponse<JSONObject> getProjectIssues(ObjectId componentId);

}
