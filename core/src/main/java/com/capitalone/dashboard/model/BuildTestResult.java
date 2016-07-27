package com.capitalone.dashboard.model;

public class BuildTestResult {
	
	
	private String total;
	private String executed;
	private String error;
	private String passed;
	private String failed;
	
	private String resultUrl;
	
	
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = getValidValue(total);
	}
	public String getExecuted() {
		return executed;
	}
	public void setExecuted(String executed) {
		this.executed = getValidValue(executed);
	}
	public String getError() {
		return error;
	}
	
	public void setError(String error) {
		this.error = getValidValue(error);
	}
	
	public String getPassed() {
		return passed;
	}
	
	public void setPassed(String passed) {
		this.passed = getValidValue(passed);
	}
	public String getFailed() {
		return failed;
	}
	public void setFailed(String failed) {
		this.failed = getValidValue(failed);
	}
	
	public String getResultUrl() {
		return resultUrl;
	}
	public void setResultUrl(String resultUrl) {
		this.resultUrl = resultUrl;
	}
	
	private String getValidValue(String val) {
		if(val==null ||  val.isEmpty()) {
			return "-";
		} else {
			return val;	
		}
		
	}	

}
