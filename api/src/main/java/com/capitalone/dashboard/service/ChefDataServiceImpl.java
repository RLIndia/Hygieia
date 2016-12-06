package com.capitalone.dashboard.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.model.ChefNode;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.repository.ChefNodeRepository;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.ProjectVersionRepository;

@Service
public class ChefDataServiceImpl implements ChefDataService {
	
	private final ComponentRepository componentRepository;
	private final CollectorRepository collectorRepository;
	private final ChefNodeRepository chefNodeRepository;
	private final CollectorItemRepository collectorItemRepository;

	@Autowired
	public ChefDataServiceImpl(ComponentRepository componentRepository,
			CollectorRepository collectorRepository,
			ChefNodeRepository chefNodeRepository,
			CollectorItemRepository collectorItemRepository) {
		
		this.componentRepository = componentRepository;
		this.collectorItemRepository = collectorItemRepository;
		this.collectorRepository = collectorRepository;
		this.chefNodeRepository = chefNodeRepository;
	
	}

	@Override
	public DataResponse<Iterable<ChefNode>> getChefNodesByComponentId(ObjectId componentId) {
		Component component = componentRepository.findOne(componentId);
		CollectorItem item = component.getCollectorItems().get(CollectorType.Chef).get(0);
		item = collectorItemRepository.findOne(item.getId());
		
		List<ChefNode> chefNodes = (List<ChefNode>) chefNodeRepository.findByCollectorItemId(item.getId());
		
		Collector collector = collectorRepository.findOne(item.getCollectorId());
		

		return new DataResponse<>(chefNodes, collector.getLastExecuted());
		
	}
}
