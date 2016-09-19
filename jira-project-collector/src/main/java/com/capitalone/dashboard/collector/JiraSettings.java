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


}
