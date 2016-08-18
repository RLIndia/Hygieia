package com.capitalone.dashboard.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;


import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.FunctionalTestResult;

import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.FunctionalTestResultRepository;

@Service
public class FunctionalTestServiceImpl implements FunctionalTestService {
    private static int noOfDays = 5;
	private final ComponentRepository componentRepository;
	private final CollectorRepository collectorRepository;
	private final FunctionalTestResultRepository functionalTestResultRepository;
	
	@Autowired
	public FunctionalTestServiceImpl(ComponentRepository componentRepository,
			CollectorRepository collectorRepository,
			FunctionalTestResultRepository functionalTestResultRepository){
		this.collectorRepository = collectorRepository;
		this.componentRepository = componentRepository;
		this.functionalTestResultRepository = functionalTestResultRepository;
	}

	@Override
	public  DataResponse<JSONObject> getFunctionalTestResults(ObjectId componentId) {
		// TODO Auto-generated method stub
		Component component = componentRepository.findOne(componentId);
        CollectorItem item = component.getCollectorItems()
                .get(CollectorType.Functional).get(0);
        LocalDate daysAgo = LocalDate.now().minusDays(noOfDays);
        Date date = Date.from(daysAgo.atStartOfDay(ZoneId.systemDefault()).toInstant());
        long epoochTime = date.getTime();
        
        List<FunctionalTestResult> functionalTestResults = functionalTestResultRepository.findByCollectorItemIdEnvIdExecutedTime(item.getId(),(String)item.getOptions().get("envId"), epoochTime);
       
        JSONObject responseObj = new JSONObject();
       
        for(FunctionalTestResult functionalTestResult : functionalTestResults) {
        	JSONObject dayResponse;
        	if(!responseObj.containsKey(functionalTestResult.getTimeExecuted().toString())) {
        		dayResponse = new JSONObject();
        		dayResponse.put("results", new JSONArray());
        		dayResponse.put("totalPassed", 0);
        		dayResponse.put("totalFailed", 0);
        		
        		responseObj.put(functionalTestResult.getTimeExecuted().toString(),dayResponse);
        	} else {
        		dayResponse = (JSONObject)responseObj.get(functionalTestResult.getTimeExecuted().toString());
        	}
        	String testResult = (String)functionalTestResult.getResult();
        	dayResponse.put("status",testResult);
        	
        	if(testResult.equals("Failed")) {
        		int countFailed = (int)dayResponse.get("totalFailed");
        		countFailed++;
        		dayResponse.put("totalFailed", countFailed);
        	} else {
        		int countPassed = (int)dayResponse.get("totalPassed");
        		countPassed++;
        		dayResponse.put("totalPassed", countPassed);
        	}
        	
        	JSONArray results = (JSONArray)dayResponse.get("results");
        	results.add(functionalTestResult);
        }
        
        Collector collector = collectorRepository
                .findOne(item.getCollectorId());
        
        return new DataResponse<>(responseObj, collector.getLastExecuted());
	
	}

}
