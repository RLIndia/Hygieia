package com.capitalone.dashboard.service;

/**
 * Created by root on 30/9/16.
 */

import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;

import com.capitalone.dashboard.model.TestRailRuns;
import com.capitalone.dashboard.repository.TestRailRunRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TestrailRunsServiceImpl implements TestrailRunsService {
    private final ComponentRepository componentRepository;
    private final CollectorRepository collectorRepository;
    private final TestRailRunRepository testRailRunRepository;
    private final CollectorItemRepository collectorItemRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(TestrailRunsServiceImpl.class);

    @Autowired
    public TestrailRunsServiceImpl(ComponentRepository componentRepository, CollectorRepository collectorRepository,
                                   TestRailRunRepository testRailRunRepository,CollectorItemRepository collectorItemRepository) {
        this.componentRepository = componentRepository;
        this.collectorRepository = collectorRepository;
        this.collectorItemRepository = collectorItemRepository;
        this.testRailRunRepository = testRailRunRepository;
    }

    @Override
    public DataResponse<JSONObject> getTestrailRuns(ObjectId componentId){
        Component component = componentRepository.findOne(componentId);
        CollectorItem item = component.getCollectorItems().get(CollectorType.Testrail).get(0);
        LOGGER.info("componentId");
        LOGGER.info(componentId.toString());
        LOGGER.info(item.getCollectorId().toString());

        LOGGER.info("itemId==>" + item.getId());
        item = collectorItemRepository.findOne(item.getId());

        String projectId = (String) item.getOptions().get("projectId");
        String milestoneId = (String) item.getOptions().get("milestoneId");
        JSONObject responseObj = new JSONObject();
        JSONArray testRailRuns = new JSONArray();
        List<TestRailRuns> trr = testRailRunRepository.findByCollectorItemId(item.getCollectorId());
        for(TestRailRuns testRailRun : trr){
            JSONObject testrailRunObj = new JSONObject();
            testrailRunObj.put("runId",testRailRun.getRunid());
            testrailRunObj.put("completedDate",testRailRun.getCompletedDate());
            testrailRunObj.put("blockedCount",testRailRun.getBlockedCount());
            testrailRunObj.put("failedCount",testRailRun.getFailedCount());
            testrailRunObj.put("passedCount",testRailRun.getPassedCount());
            testrailRunObj.put("retestCount",testRailRun.getRetestCount());
            testrailRunObj.put("untestedCount",testRailRun.getUntestedCount());
//            testrailRunObj.put("projectId",(String) item.getOptions().get("projectId"));
//            testrailRunObj.put("milestoneId",(String) item.getOptions().get("milestoneId"));
            testrailRunObj.put("url",testRailRun.getUrl());
            testrailRunObj.put("name",testRailRun.getName());
            testRailRuns.add(testrailRunObj);


        }
        responseObj.put("runs",testRailRuns);
        Collector collector = collectorRepository.findOne(item.getCollectorId());
        return new DataResponse<>(responseObj, collector.getLastExecuted());
    }

}
