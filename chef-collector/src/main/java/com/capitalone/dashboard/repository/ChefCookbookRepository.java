package com.capitalone.dashboard.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import com.capitalone.dashboard.model.CookbookCollectorItem;

public interface ChefCookbookRepository  extends BaseCollectorItemRepository<CookbookCollectorItem> {
	
	@Query(value="{ 'collectorId' : ?0, options.cookbookName : ?1}")
	CookbookCollectorItem findCookbookCollectorItem(ObjectId collectorId, String cookbookName);

}
