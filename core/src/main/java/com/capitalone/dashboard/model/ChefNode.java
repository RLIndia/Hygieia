package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="chef_nodes")
public class ChefNode {
	
	
	private ObjectId id;
    private ObjectId collectorItemId;
	private String nodeName;
	private String envName;
	private String runlist;
	private String cookbookName;
	private String ipAddress;


	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public String getEnvName() {
		return envName;
	}
	public void setEnvName(String envName) {
		this.envName = envName;
	}
	public String getRunlist() {
		return runlist;
	}
	public void setRunlist(String runlist) {
		this.runlist = runlist;
	}
	public String getCookbookName() {
		return cookbookName;
	}
	public void setCookbookName(String cookbookName) {
		this.cookbookName = cookbookName;
	}
	public ObjectId getCollectorItemId() {
		return collectorItemId;
	}
	public void setCollectorItemId(ObjectId collectorItemId) {
		this.collectorItemId = collectorItemId;
	}
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
}
