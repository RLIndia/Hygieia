package com.capitalone.dashboard.service;

import java.util.Iterator;

/**
 * Created by root on 12/9/16.
 */

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.capitalone.dashboard.repository.FunctionalTestResultRepository;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.SprintVelocity;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.SprintVelocityRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SprintVelocityServiceImpl implements SprintVelocityService {
	private final ComponentRepository componentRepository;
	private final CollectorRepository collectorRepository;
	private final SprintVelocityRepository sprintVelocityRepository;
	private final CollectorItemRepository collectorItemRepository;
	private static final Logger LOGGER = LoggerFactory.getLogger(SprintVelocityServiceImpl.class);

	@Autowired
	public SprintVelocityServiceImpl(ComponentRepository componentRepository, CollectorRepository collectorRepository,
			SprintVelocityRepository sprintVelocityRepository, CollectorItemRepository collectorItemRepository) {
		this.collectorRepository = collectorRepository;
		this.componentRepository = componentRepository;
		this.sprintVelocityRepository = sprintVelocityRepository;
		this.collectorItemRepository = collectorItemRepository;
	}

	@Override
	public DataResponse<JSONObject> getSprintVelocityReport(ObjectId componentId) {

		Component component = componentRepository.findOne(componentId);
		CollectorItem item = component.getCollectorItems().get(CollectorType.Jiraproject).get(0);
		LOGGER.info("componentId");
		LOGGER.info(componentId.toString());
		LOGGER.info(item.getCollectorId().toString());

		LOGGER.info("itemId==>" + item.getId());

		item = collectorItemRepository.findOne(item.getId());

		String versionId = (String) item.getOptions().get("versionId");
		String projectId = (String) item.getOptions().get("projectId");

		LOGGER.info("versionId ===>" + versionId);
		LOGGER.info("Project Id==>" + projectId);
		JSONObject responseObj = new JSONObject();
		List<SprintVelocity> pvi = sprintVelocityRepository
				.findVelocityReport(item.getCollectorId(), versionId,projectId);

		// //To Do after the first fetch by Collector.
		// responseObj.put("issues",pvi);

		JSONArray velocities = new JSONArray();

		boolean isfirstRec=true;
		JSONObject summary = new JSONObject();
		for (SprintVelocity velocity : pvi) {
			JSONObject velocityObj = new JSONObject();
			// push the first issues projectname and versionname to the summary
			// object
			if (isfirstRec){
				summary.put("projectId", velocity.getProjectId());
				summary.put("versionId", velocity.getVersionId());
				isfirstRec=false;
			}
			velocityObj.put("SprintId", velocity.getSprintId());
			velocityObj.put("SprintName", velocity.getSprintName());
			velocityObj.put("SprintStatus", velocity.getSprintStatus());
			velocityObj.put("Committed", velocity.getCommitted());
			velocityObj.put("Completed", velocity.getCompleted());

			velocities.add(velocityObj);	

			
		}

		

		responseObj.put("version", summary);

		responseObj.put("velocities", velocities);

		Collector collector = collectorRepository.findOne(item.getCollectorId());

		return new DataResponse<>(responseObj, collector.getLastExecuted());
	}

}
