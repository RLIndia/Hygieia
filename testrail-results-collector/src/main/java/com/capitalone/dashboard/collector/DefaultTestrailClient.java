package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.TestRailRuns;
import com.capitalone.dashboard.model.TestRailRunsResults;
import com.capitalone.dashboard.model.TestrailCollectorModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.capitalone.dashboard.collector.TRAPIClient;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vinod on 27/9/16.
 */
@Component
public class DefaultTestrailClient implements TestrailClient {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultTestrailClient.class);
    private final TestrailSettings testrailSettings;
 //   private final TRAPIClient trapiClient;
    private TRAPIClient client = null;

    @Autowired
    public DefaultTestrailClient(TestrailSettings testrailSettings){

        this.testrailSettings = testrailSettings;
    }

    @Override
    public List<TestrailCollectorModel> getTestrailProjects() throws IOException, TRAPIException{
        List<TestrailCollectorModel> testrailCollectorModels = new ArrayList<>();
        if(client == null) {
            client = new TRAPIClient(testrailSettings.getBaseurl());
            client.setUser(testrailSettings.getUsername());
            client.setPassword(testrailSettings.getPassword());
        }
        //LOG.info(testrailSettings.getUsername() + " " + testrailSettings.getPassword());
        JSONArray projects = (JSONArray) client.sendGet("get_projects");
        for(Object obj : projects){
            JSONObject trprojectObj = (JSONObject) obj;
            //Get all the milestones
            JSONArray milestones = (JSONArray) client.sendGet("get_milestones/" + trprojectObj.get("id"));
            LOG.info("Reading Milestones for " + trprojectObj.get("name").toString());
            for(Object msobj : milestones ) {
                JSONObject trmilestones = (JSONObject) msobj;
                TestrailCollectorModel tcm = new TestrailCollectorModel();
                tcm.setProjectname(trprojectObj.get("name").toString());
                tcm.setProjectId(trprojectObj.get("id").toString());
                tcm.setMilestonename(trmilestones.get("name").toString());
                tcm.setMilestoneId(trmilestones.get("id").toString());
                LOG.info("Got Milestone: " + trmilestones.get("name").toString());
                testrailCollectorModels.add(tcm);
            }

        }
        LOG.info(projects.toString());

        return testrailCollectorModels;
    }

    @Override
    public List<TestRailRuns> getAllRunsForProjectAndMileStone(String projectId,String milestoneId) throws IOException, TRAPIException{
        List<TestRailRuns> testRailRuns = new ArrayList<>();
        if(client == null) {
            client = new TRAPIClient(testrailSettings.getBaseurl());
            client.setUser(testrailSettings.getUsername());
            client.setPassword(testrailSettings.getPassword());
        }
        //LOG.info(testrailSettings.getUsername() + " " + testrailSettings.getPassword());
        JSONArray runs = (JSONArray) client.sendGet("get_runs/" + projectId);
        for(Object rObj : runs){
            JSONObject runObj = (JSONObject) rObj;
            //Get the run detail

            JSONObject runDetail = (JSONObject) client.sendGet("get_run/" + runObj.get("id"));

            LOG.info("Reading Details for project : " + projectId + " run : " + runObj.get("id"));
            if(runDetail.get("milestone_id") == null)
                continue;
            LOG.info(runDetail.get("milestone_id").toString() + " " + milestoneId);
            LOG.info("Finding match " + (runDetail.get("milestone_id").toString().contentEquals(milestoneId)) );
            if(runDetail.get("milestone_id").toString().contentEquals(milestoneId)) {
                LOG.info("Hit a match");
                TestRailRuns trr = new TestRailRuns();
                trr.setProjectId(projectId);
                trr.setMilestoneId(milestoneId);
                trr.setBlockedCount((Long)runDetail.get("blocked_count"));
                trr.setPassedCount((Long)runDetail.get("passed_count"));
                trr.setFailedCount((Long)runDetail.get("failed_count"));
                trr.setRetestCount((Long)runDetail.get("retest_count"));
                trr.setUntestedCount((Long)runDetail.get("untested_count"));
                trr.setName(runDetail.get("name").toString());
                trr.setRunid(runObj.get("id").toString());
                if(runDetail.get("completed_on") != null)
                    trr.setCompletedDate(runDetail.get("completed_on").toString());
                trr.setUrl(runDetail.get("url").toString());
                testRailRuns.add(trr);
            }

        }
        return testRailRuns;
    }

    @Override
    public List<TestRailRunsResults> getAllResultsforRun(String runId,String milestoneId,String projectId) throws IOException, TRAPIException {
        //Fetch result details for run.
        //1. Get Tests for run
        //2. Get result statuses
        //3. Get Results for test
        List<TestRailRunsResults> testRailRunsResultses = new ArrayList<>();
        LOG.info("Run ID:" + runId);
        LOG.info("Milestone ID:" + milestoneId);
        LOG.info("Project ID:" + projectId);
        if(client == null) {
            client = new TRAPIClient(testrailSettings.getBaseurl());
            client.setUser(testrailSettings.getUsername());
            client.setPassword(testrailSettings.getPassword());
        }
        JSONArray tests = (JSONArray) client.sendGet("get_tests/" + runId);
        JSONArray resultStatus = (JSONArray)  client.sendGet("get_statuses");
        for(Object test : tests) {

            JSONObject testObj = (JSONObject) test;

            LOG.info("In tests loop " + testObj.get("id").toString());
            TestRailRunsResults trrr = new TestRailRunsResults();
            trrr.setMilestoneId(milestoneId);
            trrr.setProjectId(projectId);
            trrr.setTestName(testObj.get("title").toString());
            trrr.setRunId(runId);
            trrr.setStatus(getStatus(resultStatus,(Long) testObj.get("status_id")));
            trrr.setTestId(testObj.get("id").toString());
            testRailRunsResultses.add(trrr);
            //trrr.setCreatedOn(getStatus(.));
        }
        return testRailRunsResultses;
    }

    private String getStatus(JSONArray resultStatus,Long id){
        String statusName = "";
        for(Object status: resultStatus){
            JSONObject statusObj = (JSONObject) status;
            if(id == (Long) statusObj.get("id")){
                statusName = statusObj.get("name").toString();
            }
        }
        return statusName;
    }



}
