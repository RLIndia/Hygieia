package com.capitalone.dashboard.collector;

import java.util.List;

import com.capitalone.dashboard.model.ChefNode;
import com.capitalone.dashboard.model.CookbookCollectorItem;

public interface ChefClient {
	
  public List<CookbookCollectorItem> getRunlist();
  
  public List<ChefNode> getNodesByCookbookNames(List<CookbookCollectorItem> cookbookName);
  
  public List<ChefNode> getNodes(CookbookCollectorItem item);

}
