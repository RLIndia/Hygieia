package com.capitalone.dashboard.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

//import java.util.List;

import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.model.DataResponse;
//import com.capitalone.dashboard.model.FunctionalTestResult;


import com.capitalone.dashboard.service.FunctionalTestService;

@RestController
public class FunctionalTestController {
	private final FunctionalTestService functionalTestService;
	@Autowired
	public FunctionalTestController(FunctionalTestService functionalTestService) {
		// TODO Auto-generated constructor stub
		this.functionalTestService = functionalTestService;
	}
	
	@RequestMapping(value = "/functionalTest/{componentId}", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<JSONObject>  functionalTestResults(@PathVariable ObjectId componentId) {
		 return functionalTestService.getFunctionalTestResults(componentId);
    }
}
