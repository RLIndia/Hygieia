package com.capitalone.dashboard.collector;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import com.capitalone.dashboard.chefapi.ChefApiClient;
import com.capitalone.dashboard.model.ChefNode;
import com.capitalone.dashboard.model.CookbookCollectorItem;
import com.capitalone.dashboard.util.Supplier;
import com.google.common.util.concurrent.ExecutionError;

@Component
public class DefaultChefClient implements ChefClient {
	private static final Log LOG = LogFactory.getLog(DefaultChefClient.class);

	private final ChefSettings chefSettings;
	private final RestOperations restOperations;


	@Autowired
	public DefaultChefClient(ChefSettings chefSettings,Supplier<RestOperations> restOperationsSupplier) {
		this.chefSettings = chefSettings;
		this.restOperations = restOperationsSupplier.get();
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
								//chefNode.setNodeName(nodeName);
								chefNode.setRunlist(runlist.toJSONString());
								chefNode.setCollectorItemId(cookbookItems.get(count).getId());
								//chefNode.setCookbookName(cookbookItems.get(count).getCookbookName());
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
			}
		}catch(Exception e) {
			LOG.error("Exception occured ==>"+e.getMessage());
			nodes= null;
		}
		return nodes;
	}

	@Override
	public List<ChefNode> getNodes() {
		List<ChefNode> nodes = null;
		String sintlUrl = chefSettings.getSintlUrl();
		URI uri = URI.create(sintlUrl);
		try{
			nodes = new ArrayList<>();
			ResponseEntity<String> responseEntityString  = makeRestCall(uri,null,null);
			JSONObject jsonObj = (JSONObject) new JSONParser().parse(responseEntityString.getBody());
			JSONArray nodesArray = (JSONArray)jsonObj.get("nodes");
			
			for(Object o:nodesArray) {
				JSONObject n = (JSONObject)o;
				ChefNode node = new ChefNode();
				node.setEnvName((String)n.get("env"));
				node.setProductName((String)n.get("product"));
				node.setIpAddress((String)n.get("node"));
				node.setVersion((String)n.get("version"));
				node.setBuild((String)n.get("build"));
				node.setDate((String)n.get("date"));
				//node.setCollectorItemId(enabledItem.getId());
				nodes.add(node);
			}
			
		} catch(Exception e) {
			LOG.error("error while fetching nodes ==>"+e.getMessage());
			nodes=null;
		}
		return nodes;
	}

	private ResponseEntity<String> makeRestCall(URI uri, String userId,	String password) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("GET " + uri);
		}
		// Basic Auth only.
		if (!"".equals(userId) && !"".equals(password)) {
			return restOperations.exchange(uri, HttpMethod.GET,
					new HttpEntity<>(createHeaders(userId, password)),
					String.class);

		} else {
			return restOperations.exchange(uri, HttpMethod.GET, null,
					String.class);
		}

	}

	private HttpHeaders createHeaders(final String userId, final String password) {
		String auth = userId + ":" + password;
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII));
		String authHeader = "Basic " + new String(encodedAuth);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", authHeader);
		return headers;
	}


}
