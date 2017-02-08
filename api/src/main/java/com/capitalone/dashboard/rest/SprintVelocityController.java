package com.capitalone.dashboard.rest;

/**
 * Created by vinod on 12/9/16.
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

import com.capitalone.dashboard.service.SprintVelocityService;


@RestController
public class SprintVelocityController {
    private  final SprintVelocityService sprintVelocityService;

    @Autowired
    public SprintVelocityController(SprintVelocityService sprintVelocityService){
        this.sprintVelocityService = sprintVelocityService;
    }

    @RequestMapping(value = "/sprintvelocity/{componentId}", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<JSONObject>  sprintVelocityReport(@PathVariable ObjectId componentId) {

        //JSONObject responseObj = new JSONObject();
        //responseObj.put("componentid", componentId);
      //  return  new DataResponse<>(responseObj, 1234);
        return sprintVelocityService.getSprintVelocityReport(componentId);
    }

}
