package com.capitalone.dashboard.model;

public class CookbookCollectorItem extends CollectorItem{
	
	private static final String COOKBOOK_NAME = "cookbookName";
	
	public String getCookbookName() {
        return (String) getOptions().get(COOKBOOK_NAME);
    }

    public void setCookbookName(String cookbookName) {
        getOptions().put(COOKBOOK_NAME, cookbookName);
    }

}
