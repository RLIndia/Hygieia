package com.capitalone.dashboard.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.service.ProjectIssuesService;

@RestController
public class ProjectIssuesController {
	
	private  final ProjectIssuesService ProjectIssuesService;

    @Autowired
    public ProjectIssuesController(ProjectIssuesService ProjectIssuesService){
        this.ProjectIssuesService = ProjectIssuesService;
    }

    @RequestMapping(value = "/projectVersionIssues/{componentId}", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<JSONObject>  ProjectIssuesIssues(@PathVariable ObjectId componentId) {

        //JSONObject responseObj = new JSONObject();
        //responseObj.put("componentid", componentId);
      //  return  new DataResponse<>(responseObj, 1234);
        return ProjectIssuesService.getProjectIssues(componentId);
    }

}
