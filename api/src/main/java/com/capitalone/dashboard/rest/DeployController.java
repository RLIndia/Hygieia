package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.EnvironmentProjectsAll;
import com.capitalone.dashboard.model.EnvironmentComponent;
import com.capitalone.dashboard.model.deploy.Environment;
import com.capitalone.dashboard.request.DeployDataCreateRequest;
import com.capitalone.dashboard.request.DeployAllRequest;
import com.capitalone.dashboard.service.DeployService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@RestController
public class DeployController {


    private final DeployService deployService;

    @Autowired
    public DeployController(DeployService deployService) {
        this.deployService = deployService;
    }

    @RequestMapping(value = "/deploy/status/{componentId}", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<List<Environment>> deployStatus(@PathVariable ObjectId componentId) {
        return deployService.getDeployStatus(componentId);
    }

    @RequestMapping(value = "/deploy/status/application/{applicationName}", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<List<Environment>> deployStatus(@PathVariable String applicationName) {
        return deployService.getDeployStatus(applicationName);
    }
    
    /*@RequestMapping(value = "/deploy/rdp/{hostName}", method = GET)
    public ResponseEntity<String> hostRdp(@PathVariable String hostName,HttpServletResponse response) {
    	
    	HttpHeaders responseHeaders = new HttpHeaders();
    	responseHeaders.add("Content-disposition", "attachment; filename=" + "test" + ".rdp");
    	responseHeaders.add("Content-Type", "application/rdp");
    		
    	String rdptext = "full address:s:" + hostName + ":" + "3389" + "\n\rprompt for credentials:i:1";
        //rdptext += "prompt for credentials:i:1";
        return new ResponseEntity<String>(rdptext, responseHeaders, HttpStatus.OK);
    }*/
// DataResponse<List<EnvironmentProjectsAll>>
    @RequestMapping(value = "/deploy/allprojects", method = POST,consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @JsonIgnoreProperties(ignoreUnknown = true)

    public DataResponse<List<EnvironmentProjectsAll>> allcomponents(@RequestBody DeployAllRequest request) {

        List<String> envIds = new ArrayList<>();
        for(String envId : request.getEnvIds()){
            envIds.add(envId);
        }

        return deployService.getAllDeployments(envIds);
    }
    
    @RequestMapping(value = "/deploy/rdp/{hostName}", method = GET)
    public String hostRdp(@PathVariable String hostName,HttpServletResponse response) {
    	
    	response.setHeader("Content-disposition", "attachment; filename=" + "test" + ".rdp");
    	response.setHeader("Content-Type", "application/rdp");
    		
    	String rdptext = "full address:s:" + hostName + ":" + "3389" + "\n\rprompt for credentials:i:1";
        return rdptext;
    }

    @RequestMapping(value = "/deploy", method = POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createBuild(@Valid @RequestBody DeployDataCreateRequest request) throws HygieiaException {
        String response = deployService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
