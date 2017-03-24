package com.capitalone.dashboard.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
/**
 * Created by Vinod on 7/9/16.
 */
@Component
@ConfigurationProperties(prefix = "feature")
public class JiraSettings {
    private String cron;
    private String host;
    private String key;
    private int firstRunHistoryDays;
    private String api;
    private int pageSize;
    private String jiraBaseUrl;
    private String jiraCredentials;
    private String jiraSprintDataFieldName;
    private String storyPointDataFieldName;
    private String sprintFieldName;
    private String acceptanceCriteriaFieldName;
    private String secondPreviousReleaseLPI;
    private String previousMinorReleaseVersionLPI;
    private String previousMajorVersionLPI;
    private String secondPreviousReleaseEBB;
    private String previousMinorReleaseVersionEBB;
    private String previousMajorVersionEBB;
    private String[] doneStatuses;
    private String[] todoStatuses;
    private String[] doingStatuses;    
    private String[] previousMinorVersions;
    private String[] preQA;
    private String[] postQA;
    
    
    
    public String getPreviousMinorReleaseVersionEBB() {
		return previousMinorReleaseVersionEBB;
	}

	public void setPreviousMinorReleaseVersionEBB(String previousMinorReleaseVersionEBB) {
		this.previousMinorReleaseVersionEBB = previousMinorReleaseVersionEBB;
	}

	public String getPreviousMajorVersionEBB() {
		return previousMajorVersionEBB;
	}

	public void setPreviousMajorVersionEBB(String previousMajorVersionEBB) {
		this.previousMajorVersionEBB = previousMajorVersionEBB;
	}

	public void setPreviousMajorVersionLPI(String previousMajorVersionLPI) {
		this.previousMajorVersionLPI = previousMajorVersionLPI;
	}  
    
    
    public String getCron() {
        return cron;
    }

    public JiraSettings setCron(String cron) {
        this.cron = cron;
        return this;
    }

    public String getHost() {
        return host;
    }

    public JiraSettings setHost(String host) {
        this.host = host;
        return this;
    }

    public String getKey() {
        return key;
    }

    public JiraSettings setKey(String key) {
        this.key = key;
        return this;
    }

    public int getFirstRunHistoryDays() {
        return firstRunHistoryDays;
    }

    public JiraSettings setFirstRunHistoryDays(int firstRunHistoryDays) {
        this.firstRunHistoryDays = firstRunHistoryDays;
        return this;
    }

    public String getApi() {
        return api;
    }

    public JiraSettings setApi(String api) {
        this.api = api;
        return this;
    }

    public int getPageSize() {
        return pageSize;
    }

    public JiraSettings setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public String getJiraBaseUrl() {
        return jiraBaseUrl;
    }

    public JiraSettings setJiraBaseUrl(String jiraBaseUrl) {
        this.jiraBaseUrl = jiraBaseUrl;
        return this;
    }

    public String getJiraCredentials() {
        return jiraCredentials;
    }

    public JiraSettings setJiraCredentials(String jiraCredentials) {
        this.jiraCredentials = jiraCredentials;
        return this;
    }

	public String getJiraSprintDataFieldName() {
		return jiraSprintDataFieldName;
	}

	public void setJiraSprintDataFieldName(String jiraSprintDataFieldName) {
		this.jiraSprintDataFieldName = jiraSprintDataFieldName;
	}

	public String[] getDoneStatuses() {
		return doneStatuses;
	}

	public void setDoneStatuses(String[] doneStatuses) {
		this.doneStatuses = doneStatuses;
	}

	public String[] getTodoStatuses() {
		return todoStatuses;
	}

	public void setTodoStatuses(String[] todoStatuses) {
		this.todoStatuses = todoStatuses;
	}

	public String[] getDoingStatuses() {
		return doingStatuses;
	}

	public void setDoingStatuses(String[] doingStatuses) {
		this.doingStatuses = doingStatuses;
	}
	

	public String getStoryPointDataFieldName() {
		return storyPointDataFieldName;
	}

	public void setStoryPointDataFieldName(String storyPointDataFieldName) {
		this.storyPointDataFieldName = storyPointDataFieldName;
	}

    public String getSprintFieldName() {
        return this.sprintFieldName;
    }
    public void setSprintFieldName(String sprintFieldName) {
        this.sprintFieldName = sprintFieldName;
    }
    public String getAcceptanceCriteriaFieldName() {
        return this.acceptanceCriteriaFieldName;
    }
    public void setAcceptanceCriteriaFieldName(String criteria) {
        this.acceptanceCriteriaFieldName = criteria;
    }
    private String storyPointFieldName;
    public String getStoryPointFieldName() {
        return this.storyPointFieldName;
    }
    public void setStoryPointFieldName(String storypoint) {
        this.storyPointFieldName = storypoint;
    }

	public String getPreviousMajorVersionLPI() {
		return previousMajorVersionLPI;
	}

	public void setPreviousMajorVersion(String previousMajorVersion) {
		this.previousMajorVersionLPI = previousMajorVersion;
	}

	public String[] getPreviousMinorVersions() {
		return previousMinorVersions;
	}

	public void setPreviousMinorVersions(String[] previousMinorVersions) {
		this.previousMinorVersions = previousMinorVersions;
	}

	public String[] getPreQA() {
		return preQA;
	}

	public void setPreQA(String[] preQA) {
		this.preQA = preQA;
	}

	public String[] getPostQA() {
		return postQA;
	}

	public void setPostQA(String[] postQA) {
		this.postQA = postQA;
	}

	public String getSecondPreviousReleaseLPI() {
		return secondPreviousReleaseLPI;
	}

	public void setSecondPreviousReleaseLPI(String secondPreviousRelease) {
		this.secondPreviousReleaseLPI = secondPreviousRelease;
	}

	public String getPreviousMinorReleaseVersionLPI() {
		return previousMinorReleaseVersionLPI;
	}

	public void setPreviousMinorReleaseVersionLPI(String previousMinorReleaseVersion) {
		this.previousMinorReleaseVersionLPI = previousMinorReleaseVersion;
	}

	public String getSecondPreviousReleaseEBB() {
		return secondPreviousReleaseEBB;
	}

	public void setSecondPreviousReleaseEBB(String secondPreviousReleaseEBB) {
		this.secondPreviousReleaseEBB = secondPreviousReleaseEBB;
	}
}
