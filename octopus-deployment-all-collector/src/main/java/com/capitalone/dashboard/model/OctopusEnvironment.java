package com.capitalone.dashboard.model;

/**
 * Created by vinod on 12/5/16.
 */

public class OctopusEnvironment extends CollectorItem{
    private static final String ENV_ID = "envId";



    private static final String ENV_NAME = "envName";

    private static final String DASHBOARD_ID = "dashboardId";
    private static final String OCTOPUS_URL = "octopusUrl";


    public String getOctopusUrl() {
        return  (String) getOptions().get(OCTOPUS_URL);
    }

    public void setOctopusUrl(String octopusUrl) {
        getOptions().put(OCTOPUS_URL,octopusUrl);
    }



    public String getEnvId() {
        return (String) getOptions().get(ENV_ID);
    }

    public void setEnvId(String envId) {
        getOptions().put(ENV_ID,envId);
    }

    public String getEnvName() {
        return (String) getOptions().get(ENV_NAME);
    }

    public void setDashboardId(String dasboardId) {
        getOptions().put(DASHBOARD_ID,dasboardId);
    }

    public String getDashboardId() {
        return (String) getOptions().get(DASHBOARD_ID);
    }

    public void setEnvName(String envName) {
        getOptions().put(ENV_NAME,envName);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OctopusEnvironment that = (OctopusEnvironment) o;
        return getEnvId().equals(that.getEnvId()) && getEnvName().equals(that.getEnvName());
    }

    @Override
    public int hashCode() {
        int result = getEnvName().hashCode();
        result = 31 * result + getEnvId().hashCode();
        return result;
    }
}
