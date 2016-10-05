package com.capitalone.dashboard.collector;

import java.util.List;

import com.capitalone.dashboard.model.CookbookCollectorItem;

public interface ChefClient {
	
  public List<CookbookCollectorItem> getRunlist();

}
