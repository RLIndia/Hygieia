package com.capitalone.dashboard.collector;

import java.util.List;

import com.capitalone.dashboard.model.RunlistCollectorItem;

public interface ChefClient {
	
  public List<RunlistCollectorItem> getRunlist();

}
