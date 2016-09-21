package com.capitalone.dashboard.service;

/**
 * Created by vinod on 21/9/16.
 */
import java.util.List;


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


import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CatalystDeployServiceImpl implements CatalystDeployService {
    private final ComponentRepository componentRepository;
    private final CollectorRepository collectorRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(CatalystDeployServiceImpl.class);

    @Autowired
    public CatalystDeployServiceImpl(ComponentRepository componentRepository,
                                     CollectorRepository collectorRepository){
        this.collectorRepository = collectorRepository;
        this.componentRepository = componentRepository;
    }

    @Override
    public  DataResponse<JSONObject> getCatalystDeployments(ObjectId componentId) {

        Component component = componentRepository.findOne(componentId);
        CollectorItem item = component.getCollectorItems()
                .get(CollectorType.Catalystdeploy).get(0);
        LOGGER.info("componentId");
        LOGGER.info(componentId.toString());
        LOGGER.info(item.getCollectorId().toString());

        Collector collector = collectorRepository
                .findOne(item.getCollectorId());
        JSONObject responseObj = new JSONObject();
        JSONObject summary = new JSONObject();
        summary.put("Test","test1");
        responseObj.put("deployments",summary);
        return  new DataResponse<>(responseObj,collector.getLastExecuted());
    }
}
