package com.capitalone.dashboard.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "chef")
public class ChefSettings {
	private String username;
	private String chefServerUrl;
	private String pemFilePath;
	private String cron;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getChefServerUrl() {
		return chefServerUrl;
	}

	public void setChefServerUrl(String chefServerUrl) {
		this.chefServerUrl = chefServerUrl;
	}

	public String getPemFilePath() {
		return pemFilePath;
	}

	public void setPemFilePath(String pemFilePath) {
		this.pemFilePath = pemFilePath;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

}
