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

@Service
public class ProjectVersionServiceImpl implements ProjectVersionService {
    private final ComponentRepository componentRepository;
    private final CollectorRepository collectorRepository;
    private final ProjectVersionRepository projectVersionRepository;

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

        List<ProjectVersionIssues> projectVersionIssues = projectVersionRepository.findByCollectorItemId(item.getId());
        JSONObject responseObj = new JSONObject();
        //To Do after the first fetch by Collector.


        Collector collector = collectorRepository
                .findOne(item.getCollectorId());

        return  new DataResponse<>(responseObj, collector.getLastExecuted());
    }

}
