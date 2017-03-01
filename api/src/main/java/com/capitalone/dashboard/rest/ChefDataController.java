package com.capitalone.dashboard.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;


import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.model.ChefNode;
import com.capitalone.dashboard.model.DataResponse;

import com.capitalone.dashboard.service.ChefDataService;

@RestController
public class ChefDataController {

	private final ChefDataService chefDataService;
	@Autowired
	public ChefDataController(ChefDataService chefDataService) {
		this.chefDataService = chefDataService;
	}

	@RequestMapping(value = "/chefData/{componentId}", method = GET, produces = APPLICATION_JSON_VALUE)
	public DataResponse<Iterable<ChefNode>> builds(@PathVariable ObjectId componentId) {
		return chefDataService.getChefNodesByComponentId(componentId);
	}
}
