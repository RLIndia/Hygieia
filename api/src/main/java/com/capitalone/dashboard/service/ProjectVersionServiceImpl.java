package com.capitalone.dashboard.service;

/**
 * Created by root on 12/9/16.
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
import com.capitalone.dashboard.model.ProjectVersionIssues;

import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import  com.capitalone.dashboard.repository.ProjectVersionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProjectVersionServiceImpl implements ProjectVersionService {
    private final ComponentRepository componentRepository;
    private final CollectorRepository collectorRepository;
    private final ProjectVersionRepository projectVersionRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectVersionServiceImpl.class);

    @Autowired
    public ProjectVersionServiceImpl(ComponentRepository componentRepository,
                                     CollectorRepository collectorRepository,
                                     ProjectVersionRepository projectVersionRepository){
        this.collectorRepository = collectorRepository;
        this.componentRepository = componentRepository;
        this.projectVersionRepository = projectVersionRepository;
    }

    @Override
    public  DataResponse<JSONObject> getProjectVersionIssues(ObjectId componentId) {

        Component component = componentRepository.findOne(componentId);
        CollectorItem item = component.getCollectorItems()
               .get(CollectorType.Jiraproject).get(0);
        LOGGER.info("componentId");
        LOGGER.info(componentId.toString());
        LOGGER.info(item.getCollectorId().toString());
        List<ProjectVersionIssues> pvi = projectVersionRepository.findByCollectorItemId(item.getCollectorId());
        JSONObject responseObj = new JSONObject();
//        //To Do after the first fetch by Collector.
//        responseObj.put("issues",pvi);

        JSONArray issues = new JSONArray();
        Collector collector = collectorRepository
                .findOne(item.getCollectorId());
        int doneCount = 0;
        int progressCount = 0;
        int issueCount = 0;
        int pendingCount =0;
        JSONObject summary = new JSONObject();
        for(ProjectVersionIssues issue : pvi){
            JSONObject issueObj = new JSONObject();
            //push the first issues projectname and versionname to the summary object
            if(issueCount == 0){
                summary.put("projectName",issue.getProjectName());
                summary.put("versionName",issue.getVersionName());
            }
            issueObj.put("issueID",issue.getIssueId());
            issueObj.put("status",issue.getIssueStatus());
            issueObj.put("statusName",issue.getStatusName());
            issueObj.put("description",issue.getIssueDescription());

            issues.add(issueObj);
            if(issue.getIssueStatus().toString().contentEquals("Done")){
                doneCount++;
            }
            if(issue.getIssueStatus().toString().contentEquals("Backlog") ){
                pendingCount++;
            }
            if( issue.getIssueStatus().toString().contentEquals("In Progress") || issue.getIssueStatus().toString().contentEquals("Requirement In Progress")){
                progressCount++;
            }
            issueCount++;
        }

        summary.put("doneCount",doneCount);
        summary.put("issueCount",issueCount);
        summary.put("inprogressCount",progressCount);
        summary.put("pendingCount",pendingCount);

        responseObj.put("issues",issues);
        responseObj.put("summary",summary);



        return  new DataResponse<>(responseObj, collector.getLastExecuted());
    }

}
