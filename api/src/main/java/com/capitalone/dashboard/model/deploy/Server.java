package com.capitalone.dashboard.model.deploy;

public class Server {
    private final String name;
    private final boolean online;
    private String hostname;

    public Server(String name, boolean online) {
        this.name = name;
        this.online = online;
    }
    
    public Server(String name, boolean online, String hostname) {
        this.name = name;
        this.online = online;
        this.hostname = hostname;
        
    }

    public String getName() {
        return name;
    }

    public boolean isOnline() {
        return online;
    }

	public String getHostname() {
		return hostname;
	}
}
