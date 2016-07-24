package com.capitalone.dashboard.model;

public class Release {
	
	private String releaseId;
	private String applicationId;
	private String version;
	private String deploymentProcessSnapShotId;
	
	public String getReleaseId() {
		return releaseId;
	}
	public void setReleaseId(String releaseId) {
		this.releaseId = releaseId;
	}
	
	public String getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getDeploymentProcessSnapShotId() {
		return deploymentProcessSnapShotId;
	}
	public void setDeploymentProcessSnapShotId(String deploymentProcessSnapShotId) {
		this.deploymentProcessSnapShotId = deploymentProcessSnapShotId;
	}

}
