package com.capitalone.dashboard.collector;

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


    @Autowired
    public DefaultTestrailClient(TestrailSettings testrailSettings){

        this.testrailSettings = testrailSettings;
    }

    @Override
    public List<TestrailCollectorModel> getTestrailProjects() throws IOException, TRAPIException{
        List<TestrailCollectorModel> testrailCollectorModels = new ArrayList<>();
        TRAPIClient client = new TRAPIClient(testrailSettings.getBaseurl());
        client.setUser(testrailSettings.getUsername());
        client.setPassword(testrailSettings.getPassword());
        LOG.info(testrailSettings.getUsername() + " " + testrailSettings.getPassword());
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



}
