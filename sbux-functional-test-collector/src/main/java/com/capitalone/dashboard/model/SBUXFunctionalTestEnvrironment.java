package com.capitalone.dashboard.model;

public class SBUXFunctionalTestEnvrironment extends CollectorItem {
	
	private static final String INSTANCE_URL = "instanceUrl";
	private static final String ENV_NAME = "envName";
    private static final String ENV_ID = "envId";
    
    public String getInstanceUrl() {
        return (String) getOptions().get(INSTANCE_URL);
    }

    public void setInstanceUrl(String instanceUrl) {
        getOptions().put(INSTANCE_URL, instanceUrl);
    }
    
    public String getEnvName() {
        return (String) getOptions().get(ENV_NAME);
    }

    public void setEnvName(String envName) {
        getOptions().put(ENV_NAME, envName);
    }

    public String getEnvId() {
        return (String) getOptions().get(ENV_ID);
    }

    
    public void setEnvId(String id) {
        getOptions().put(ENV_ID, id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SBUXFunctionalTestEnvrironment that = (SBUXFunctionalTestEnvrironment) o;
        return getEnvId().equals(that.getEnvId()) && getInstanceUrl().equals(that.getInstanceUrl());
    }

    @Override
    public int hashCode() {
        int result = getInstanceUrl().hashCode();
        result = 31 * result + getEnvId().hashCode();
        return result;
    }
    
}
