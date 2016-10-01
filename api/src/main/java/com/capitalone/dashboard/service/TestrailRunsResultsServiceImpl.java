package com.capitalone.dashboard.service;

/**
 * Created by vinod on 1/10/16.
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

import com.capitalone.dashboard.model.TestRailRunsResults;
import com.capitalone.dashboard.repository.TestRailRunResultRepository;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TestrailRunsResultsServiceImpl implements TestrailRunsResultsService {

    private final ComponentRepository componentRepository;
    private final CollectorRepository collectorRepository;
    private final TestRailRunResultRepository testRailRunResultRepository;
    private final CollectorItemRepository collectorItemRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(TestrailRunsServiceImpl.class);

    @Autowired
    public TestrailRunsResultsServiceImpl(ComponentRepository componentRepository, CollectorRepository collectorRepository,
                                   TestRailRunResultRepository testRailRunResultRepository,CollectorItemRepository collectorItemRepository) {
        this.componentRepository = componentRepository;
        this.collectorRepository = collectorRepository;
        this.collectorItemRepository = collectorItemRepository;
        this.testRailRunResultRepository = testRailRunResultRepository;
    }

    @Override
    public DataResponse<JSONObject> getTestrailRunsResults(String runId,String milestoneId,String projectId){
        JSONObject responseObj = new JSONObject();
        JSONArray testRailRunsResult = new JSONArray();
        List<TestRailRunsResults> trrr = testRailRunResultRepository.findByRunIdAndProjectIdAndMilestoneId(runId,milestoneId,projectId);
        for(TestRailRunsResults testRailRunResult : trrr){
            JSONObject testrailRunResultObj = new JSONObject();
            testrailRunResultObj.put("milestoneId",testRailRunResult.getMilestoneId());
            testrailRunResultObj.put("testId",testRailRunResult.getTestId());
            testrailRunResultObj.put("runId",testRailRunResult.getRunId());
            testrailRunResultObj.put("projectId",testRailRunResult.getProjectId());
            testrailRunResultObj.put("status",testRailRunResult.getStatus());
            testrailRunResultObj.put("comment",testRailRunResult.getComment());
            testrailRunResultObj.put("testName",testRailRunResult.getTestName());
            testrailRunResultObj.put("createdOn",testRailRunResult.getCreatedOn());
            testRailRunsResult.add(testrailRunResultObj);
        }
        responseObj.put("runresults",testRailRunsResult);
        return new DataResponse<>(responseObj,0);
    }


}
