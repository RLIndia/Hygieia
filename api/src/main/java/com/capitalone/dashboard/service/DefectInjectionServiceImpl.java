package com.capitalone.dashboard.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.DefectInjection;
import com.capitalone.dashboard.model.SprintVelocity;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.DefectInjectsRepository;
import com.capitalone.dashboard.repository.SprintVelocityRepository;

public class DefectInjectionServiceImpl implements DefectInjectionService {
	
	private final ComponentRepository componentRepository;
	private final CollectorRepository collectorRepository;
	private final DefectInjectsRepository defectInjectsRepository;
	private final CollectorItemRepository collectorItemRepository;
	private static final Logger LOGGER = LoggerFactory.getLogger(SprintVelocityServiceImpl.class);
	
	@Autowired
	public DefectInjectionServiceImpl(ComponentRepository componentRepository, CollectorRepository collectorRepository,
			DefectInjectsRepository defectInjectsRepository, CollectorItemRepository collectorItemRepository) {
		this.collectorRepository = collectorRepository;
		this.componentRepository = componentRepository;
		this.defectInjectsRepository = defectInjectsRepository;
		this.collectorItemRepository = collectorItemRepository;
	}
	

	@Override
	public DataResponse<JSONObject> getDefectInjectionReport(ObjectId componentId, String sprintID) {
		// TODO Auto-generated method stub
		Component component = componentRepository.findOne(componentId);
		CollectorItem item = component.getCollectorItems().get(CollectorType.Jiraproject).get(0);
		LOGGER.info("componentId");
		LOGGER.info(componentId.toString());
		LOGGER.info(item.getCollectorId().toString());

		LOGGER.info("itemId==>" + item.getId());

		item = collectorItemRepository.findOne(item.getId());
		
		JSONObject responseObj = new JSONObject();
		List<DefectInjection> di = defectInjectsRepository.findDefectInjection(item.getCollectorId(),(String) item.getOptions().get("projectId"));
		
		JSONArray diArray = new JSONArray();
		
		JSONObject diSummary = new JSONObject();
		
		
		
		responseObj.put("defectInjects", diArray);

		//responseObj.put("velocities", velocities);

		Collector collector = collectorRepository.findOne(item.getCollectorId());

		return new DataResponse<>(responseObj, collector.getLastExecuted());
		
		
	}
}


