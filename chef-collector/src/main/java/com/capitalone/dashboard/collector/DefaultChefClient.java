package com.capitalone.dashboard.collector;


import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.chefapi.ChefApiClient;
import com.capitalone.dashboard.model.RunlistCollectorItem;


@Component
public class DefaultChefClient implements ChefClient {
	private static final Log LOG = LogFactory.getLog(DefaultChefClient.class);
    
	private final ChefSettings chefSettings;
	
	@Autowired
	public DefaultChefClient(ChefSettings chefSettings) {
		this.chefSettings = chefSettings;
	}
	
	@Override
	public List<RunlistCollectorItem> getRunlist() {
		ChefApiClient cac = new ChefApiClient(chefSettings.getUsername(),chefSettings.getPemFilePath(),chefSettings.getChefServerUrl());
		String responseBody = cac.get("/cookbooks").execute().getResponseBodyAsString();
		LOG.info(responseBody);
		
		return null;
	}

}
