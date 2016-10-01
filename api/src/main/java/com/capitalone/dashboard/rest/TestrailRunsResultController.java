package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.model.DataResponse;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import com.capitalone.dashboard.service.TestrailRunsResultsService;

import java.util.List;
import java.util.Optional;

/**
 * Created by root on 1/10/16.
 */
@RestController
public class TestrailRunsResultController {
    private final TestrailRunsResultsService testrailRunsResultService;

    @Autowired
    public TestrailRunsResultController(TestrailRunsResultsService testrailRunsResultService){
        this.testrailRunsResultService = testrailRunsResultService;
    }


    @RequestMapping(value = "/testrailrunsresult/{runId}", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<JSONObject> testrailruns(
            @RequestParam(value = "projectId", required = true) String projectId,
            @RequestParam(value = "milestoneId", required = true) String milestoneId,
            @PathVariable String runId) {

        return this.testrailRunsResultService.getTestrailRunsResults(runId,milestoneId,projectId);
    }

}
