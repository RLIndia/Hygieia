package com.capitalone.dashboard.collector;
import java.util.ArrayList;
import java.util.List;

import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import com.capitalone.dashboard.model.FunctionalTestResult;
import com.capitalone.dashboard.util.Supplier;

@Component
public class DefaultSBUXClient implements SBUXClient{
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSBUXClient.class);
	private final SBUXSettings sbuxSettings;
	private final RestOperations restOperations;
	
	@Autowired
	public DefaultSBUXClient(SBUXSettings sbuxSettings,
			Supplier<RestOperations> restOperationsSupplier) {
		this.sbuxSettings = sbuxSettings;
		this.restOperations = restOperationsSupplier.get();
	}
	
	
	@Override
	public List<FunctionalTestResult> getFunctionalTestResults() {
		
		List<FunctionalTestResult> functionalTestResults = new ArrayList<>();
		
		JSONObject envResObj = getEnvironments();
		Set<String> keys = envResObj.keySet();
		 for(String key : keys ) {
			 String environmentName = (String)envResObj.get(key);
			 JSONObject testResultRespObj = getTestResults(key);
			 JSONArray testTimes = (JSONArray)testResultRespObj.get("TestRunTimes");
			 JSONObject results = (JSONObject)testResultRespObj.get("Results");
			 Set<String> testCases = results.keySet();
			 for(String testCase : testCases) {
				JSONArray testCaseResults = (JSONArray)results.get(testCase);
			    
				int index = -1;
				for(Object obj : testCaseResults) {
					index++;
					String result = (String)obj;
					FunctionalTestResult fr = new FunctionalTestResult();
					fr.setEnvId(key);
					fr.setEnvName(environmentName);
					fr.setTestCaseName(testCase);
					fr.setResult(result);
					fr.setTimeExecuted((String)testTimes.get(index));
					functionalTestResults.add(fr);
				}
				
			 }
			 
		 }
		
		return functionalTestResults;
	}
	
	private JSONObject getEnvironments() {
		ResponseEntity<String> response = makeRestCall(sbuxSettings.getUrl(),"teststacks");
		JSONObject resObj = paresResponse(response);
		return resObj;
	}
	
	private JSONObject getTestResults(String envId){
		ResponseEntity<String> response = makeRestCall(sbuxSettings.getUrl(),"reporttesthistory/bvt/"+envId+"?"+sbuxSettings.getDays());
		JSONObject resObj = paresResponse(response);
		return resObj;
	}
	
	private ResponseEntity<String> makeRestCall(String instanceUrl,
			String endpoint) {
		String url = instanceUrl+ endpoint;
		ResponseEntity<String> response = null;
		try {
			response =  restOperations.exchange(url, HttpMethod.GET, null,
					String.class);

		} catch (RestClientException re) {
			LOG.error("Error with REST url: " + url);
			LOG.error(re.getMessage());
		}
		return response;
	}

	protected HttpHeaders createHeaders(String headerName,String headerValue) {

		HttpHeaders headers = new HttpHeaders();
		headers.set(headerName, headerValue);
		return headers;
	}

	private JSONObject paresResponse(ResponseEntity<String> response) {
		if (response == null)
			return new JSONObject();
		try {

			JSONObject jsonObject = (JSONObject)new JSONParser().parse(response.getBody());
			return jsonObject;

		} catch (ParseException pe) {
			LOG.debug(response.getBody());
			LOG.error(pe.getMessage());
		}
		return new JSONObject();
	}

}
