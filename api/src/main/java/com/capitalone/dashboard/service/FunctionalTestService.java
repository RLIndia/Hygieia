package com.capitalone.dashboard.service;

//import java.util.List;

import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

import com.capitalone.dashboard.model.DataResponse;
//import com.capitalone.dashboard.model.FunctionalTestResult;

public interface FunctionalTestService {
	//DataResponse<List<FunctionalTestResult>> getFunctionalTestResults(ObjectId componentId);
	DataResponse<JSONObject> getFunctionalTestResults(ObjectId componentId);
}
