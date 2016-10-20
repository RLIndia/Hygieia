package com.capitalone.dashboard.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "octopus")
public class OctopusSettings {
	private String cron;

	public String[] getApiKey() {
		return apiKey;
	}

	public void setApiKey(String[] apiKey) {
		this.apiKey = apiKey;
	}

	public String[] getUrl() {
		return url;
	}

	public void setUrl(String[] url) {
		this.url = url;
	}

	public String[] getEnvironments() {
		return environments;
	}

	public void setEnvironments(String[] environments) {
		this.environments = environments;
	}

	private String[] apiKey;
    private String[] url;


	private String[] environments;
	
    public String getCron() {
		return cron;
	}
	public void setCron(String cron) {
		this.cron = cron;
	}

}
