package com.capitalone.dashboard.service;


import org.bson.types.ObjectId;

import com.capitalone.dashboard.model.ChefNode;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.request.BuildSearchRequest;

public interface ChefDataService {

	 DataResponse<Iterable<ChefNode>> getChefNodesByComponentId(ObjectId componentId);
}
