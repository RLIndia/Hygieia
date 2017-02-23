package com.capitalone.dashboard.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.DefectInjection;

public interface DefectInjectionService {
	
	DataResponse<JSONObject> getDefectInjectionReport(ObjectId componentId, String sprintId);

}
