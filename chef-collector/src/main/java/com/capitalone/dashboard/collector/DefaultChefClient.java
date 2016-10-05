package com.capitalone.dashboard.collector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.chefapi.ChefApiClient;
import com.capitalone.dashboard.model.CookbookCollectorItem;

@Component
public class DefaultChefClient implements ChefClient {
	private static final Log LOG = LogFactory.getLog(DefaultChefClient.class);

	private final ChefSettings chefSettings;

	@Autowired
	public DefaultChefClient(ChefSettings chefSettings) {
		this.chefSettings = chefSettings;
	}

	@Override
	public List<CookbookCollectorItem> getRunlist() {
		ChefApiClient cac = new ChefApiClient(chefSettings.getUsername(), chefSettings.getPemFilePath(),
				chefSettings.getChefServerUrl());
		String responseBody = cac.get("/cookbooks").execute().getResponseBodyAsString();
		List<CookbookCollectorItem> runlistCollectorItems = new ArrayList<>();
		try {
			JSONObject jsonObj = (JSONObject) new JSONParser().parse(responseBody);
			Set<String> keys = jsonObj.keySet();
			Iterator<String> i = keys.iterator();
			while(i.hasNext()) {
				String cookbookName = i.next();
				CookbookCollectorItem runlistCollectorItem = new CookbookCollectorItem();
				runlistCollectorItem.setCookbookName(cookbookName);
				runlistCollectorItems.add(runlistCollectorItem);
			}
		} catch (Exception e) {
			LOG.error("Error occured ==>"+e.getMessage());
			runlistCollectorItems = null;
		}
		return runlistCollectorItems;
	}

}
