package com.capitalone.dashboard.model;

public class RunlistCollectorItem extends CollectorItem{
	
	private static final String RUNLIST_NAME = "runlistName";
	
	public String getRunlistName() {
        return (String) getOptions().get(RUNLIST_NAME);
    }

    public void setRunlistName(String runlistName) {
        getOptions().put(RUNLIST_NAME, runlistName);
    }

}
