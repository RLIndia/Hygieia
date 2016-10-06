package com.capitalone.dashboard.rest;

/**
 * Created by vinod on 30/9/16.
 */
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

//import java.util.List;

import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.service.TestrailRunsService;

@RestController
public class TestrailRunsController {
    private final TestrailRunsService testrailRunsService;

    @Autowired
    public TestrailRunsController(TestrailRunsService testrailRunsService){
        this.testrailRunsService = testrailRunsService;
    }

    @RequestMapping(value = "/testrailruns/{componentId}",method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<JSONObject> testrailruns(@PathVariable ObjectId componentId){
        return testrailRunsService.getTestrailRuns(componentId);
    }

}
