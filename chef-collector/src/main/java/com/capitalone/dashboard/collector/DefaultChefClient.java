package com.capitalone.dashboard.collector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.chefapi.ChefApiClient;
import com.capitalone.dashboard.model.ChefNode;
import com.capitalone.dashboard.model.CookbookCollectorItem;
import com.google.common.util.concurrent.ExecutionError;

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

	@Override
	public List<ChefNode> getNodesByCookbookNames(List<CookbookCollectorItem> cookbookItems) {
		ChefApiClient cac = new ChefApiClient(chefSettings.getUsername(), chefSettings.getPemFilePath(),
				chefSettings.getChefServerUrl());
		LOG.info("Fetching node list");
		String responseBody = cac.get("/nodes").execute().getResponseBodyAsString();
		List<ChefNode> nodes = new ArrayList<>();
		try{
			JSONObject jsonObj = (JSONObject) new JSONParser().parse(responseBody);
			Set<String> keys = jsonObj.keySet();
			LOG.info("total number of nodes ==> "+keys.size());
			Iterator<String> i = keys.iterator();
			while(i.hasNext()) {
				String nodeName = i.next();
				LOG.info("Fetching node ==> "+nodeName);
				try {
					String nodeData = cac.get("/nodes/"+nodeName).execute().getResponseBodyAsString();
					JSONObject nodeObj = (JSONObject) new JSONParser().parse(nodeData);
					JSONArray runlist = (JSONArray)nodeObj.get("run_list");
					if(runlist != null) {
						for(int count=0;count<cookbookItems.size();count++) {
							for(Object o : runlist) {
								String r = (String)o;
								if(r.contains("recipe["+cookbookItems.get(count).getCookbookName())){
									ChefNode chefNode = new ChefNode();
									chefNode.setEnvName((String)nodeObj.get("chef_environment"));
									chefNode.setNodeName(nodeName);
									chefNode.setRunlist(runlist.toJSONString());
									chefNode.setCollectorItemId(cookbookItems.get(count).getId());
									chefNode.setCookbookName(cookbookItems.get(count).getCookbookName());
									JSONObject automatic = (JSONObject)nodeObj.get("automatic");
									if(automatic != null) {
										String ipAddress = (String)automatic.get("ipaddress");
										chefNode.setIpAddress(ipAddress);
									}

									nodes.add(chefNode);
									break;
								}
							}	
						}

					}
				} catch (Exception e) {
					LOG.error("Exception occured while processing node ==>"+nodeName);
				}
			}
		}catch(Exception e) {
			LOG.error("Exception occured ==>"+e.getMessage());
			nodes= null;
		}
		return nodes;
	}

}
