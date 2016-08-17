package com.capitalone.dashboard.model;

public class FunctionalTestResult {
	
	private String envId;
	private String envName;
	private String testCaseName;
	private String timeExecuted;
	private String result;
	
	
	public String getEnvId() {
		return envId;
	}
	public void setEnvId(String envId) {
		this.envId = envId;
	}
	public String getEnvName() {
		return envName;
	}
	public void setEnvName(String envName) {
		this.envName = envName;
	}
	public String getTestCaseName() {
		return testCaseName;
	}
	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getTimeExecuted() {
		return timeExecuted;
	}
	public void setTimeExecuted(String timeExecuted) {
		this.timeExecuted = timeExecuted;
	}
	
	
}
