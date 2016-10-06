package com.capitalone.dashboard.rest;

/**
 * Created by vinod on 25/9/16.
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
import  com.capitalone.dashboard.service.CatalystDeployHistoryService;

import com.capitalone.dashboard.model.DataResponse;

@RestController
public class CatalystDeployHistoryTaskController {
    private final CatalystDeployHistoryService catalystDeployHistoryService;

    @Autowired
    public CatalystDeployHistoryTaskController(CatalystDeployHistoryService catalystDeployHistoryService) {
        this.catalystDeployHistoryService = catalystDeployHistoryService;
    }

    @RequestMapping(value = "/catalystdeploymentshistory/{taskId}", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<JSONObject>  catalystdeployments(@PathVariable String taskId) {

//        JSONObject responseObj = new JSONObject();
//        responseObj.put("componentid", componentId);
//          return  new DataResponse<>(responseObj, 1234);
//        //To be updated to fetch information after collecting.

        return catalystDeployHistoryService.getCatalystDeploymentHistory(taskId);
    }

}
