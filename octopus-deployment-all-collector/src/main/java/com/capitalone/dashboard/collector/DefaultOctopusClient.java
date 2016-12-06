package com.capitalone.dashboard.collector;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import com.capitalone.dashboard.model.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import com.capitalone.dashboard.util.Supplier;

@Component
public class DefaultOctopusClient implements OctopusClient{
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultOctopusClient.class);
    private final OctopusSettings octopusSettings;
    private final RestOperations restOperations;
    private final Locale locale;
    private int contextOS;

    @Autowired
    public DefaultOctopusClient(OctopusSettings octopusSettings,
                                Supplier<RestOperations> restOperationsSupplier) {
        this.octopusSettings = octopusSettings;
        this.restOperations = restOperationsSupplier.get();
        this.locale = new Locale("en", "US");
        this.contextOS = 0;
    }





    @Override
    public List<OctopusEnvironment> getEnvironments() {
        List<OctopusEnvironment> environments = new ArrayList<>();
        boolean hasNext = true;
        String urlPath = "/api/environments";
        while(hasNext) {
            JSONObject resJsonObject =  paresResponse(makeRestCall(octopusSettings.getUrl()[contextOS],
                    urlPath,octopusSettings.getApiKey()[contextOS]));
            JSONArray jsonArray = (JSONArray)resJsonObject.get("Items");
            for (Object item : jsonArray) {
                JSONObject jsonObject = (JSONObject) item;

                OctopusEnvironment newEnv = new OctopusEnvironment();
                newEnv.setEnvId(str(jsonObject,"Id"));
                newEnv.setEnvName(str(jsonObject,"Name"));
                environments.add(newEnv);
            }

            JSONObject links = (JSONObject)resJsonObject.get("Links");
            urlPath = (String)links.get("Page.Next");

            if(urlPath == null || urlPath.isEmpty()) {
                hasNext = false;
            }

        }
        return environments;
    }


    @Override
    public void setContext(int sc) {
        this.contextOS = sc;
    }

    @Override
    public int getContext() {
        return this.contextOS;
    }

    private Task getTaskById(String taskId) {
        JSONObject resJsonObject =  paresResponse(makeRestCall(octopusSettings.getUrl()[contextOS],
                "/api/tasks/"+taskId,octopusSettings.getApiKey()[contextOS]));
        Task task = new Task();

        task.setTaskId(taskId);
        task.setTaskName((String)resJsonObject.get("Name"));
        String state = (String)resJsonObject.get("State");
        if(state.equals("Failed")) {
            task.setState(false);
        } else {
            task.setState(true);
        }


        return task;
    }

    private Set<String> getRolesFromDeploymentProcess(String deploymentProcessId) {
        JSONObject resJsonObject =  paresResponse(makeRestCall(octopusSettings.getUrl()[contextOS],
                "/api/deploymentprocesses/"+deploymentProcessId, octopusSettings.getApiKey()[contextOS]));
        JSONArray steps = (JSONArray)resJsonObject.get("Steps");

        //LOGGER.info("steps size ==>"+steps.size());

        Set<String> roleSet = new HashSet<>();

        for(Object object : steps) {
            JSONObject obj = (JSONObject)object;
            JSONObject propertiesObj = (JSONObject)obj.get("Properties");
            if(propertiesObj != null) {
                String roles = (String)propertiesObj.get("Octopus.Action.TargetRoles");
                //LOGGER.info("deployment step role ==>"+roles);
                if(roles != null && !roles.isEmpty()) {
                    String[] rolesArray = roles.split(",");
                    for(int i=0;i<rolesArray.length;i++) {
                        roleSet.add(rolesArray[i].toLowerCase(locale));
                    }
                }

            }
        }
        return roleSet;
    }

    private Machine getMachineById(String machineId,String envId) throws MalformedURLException {
        JSONObject resJsonObject =  paresResponse(makeRestCall(octopusSettings.getUrl()[contextOS],
                "/api/machines/"+machineId,octopusSettings.getApiKey()[contextOS]));
        Machine machine = new Machine();
        //checking for 404
        if(resJsonObject.isEmpty() == false){
            machine.setEnviromentId(envId);
            machine.setMachineName((String) resJsonObject.get("Name"));
            machine.setMachineId((String) resJsonObject.get("Id"));
            String status = (String) resJsonObject.get("Status");
            if (status.equals("Online")) {
                machine.setStatus(true);
            } else {
                machine.setStatus(false);
            }

            String url = (String) resJsonObject.get("Uri");
            //LOGGER.info("url ==>"+url);

            if (url != null && !url.isEmpty()) {
                String hostname = new URL(url).getHost();
                //LOGGER.info("Host name ==>"+hostname);
                machine.setHostName(hostname);
            }
        }
        return machine;

    }

    private List<Machine> getMachinesByEnvId(String envId,Set<String> roleSet) throws MalformedURLException {

        List<Machine> machines= new ArrayList<Machine>();

        boolean hasNext = true;
        String urlPath = "/api/environments/"+envId+"/machines";
        while(hasNext) {

            JSONObject resJsonObject =  paresResponse(makeRestCall(octopusSettings.getUrl()[contextOS],
                    urlPath,octopusSettings.getApiKey()[contextOS]));

            JSONArray jsonArray = (JSONArray)resJsonObject.get("Items");
            for (Object item :jsonArray) {
                JSONObject jsonObject = (JSONObject) item;
                Machine machine = new Machine();
                machine.setEnviromentId(envId);
                machine.setMachineName((String)jsonObject.get("Name"));
                machine.setMachineId((String)jsonObject.get("Id"));
                String status = (String)jsonObject.get("Status");
                if(status.equals("Online")) {
                    machine.setStatus(true);
                } else {
                    machine.setStatus(false);
                }

                String url = (String)jsonObject.get("Uri");
                //LOGGER.info("url ==>"+url);

                if(url != null && !url.isEmpty()) {
                    String hostname = new URL(url).getHost();
                    //LOGGER.info("Host name ==>"+hostname);
                    machine.setHostName(hostname);
                }



                if(roleSet.isEmpty()) {
                    machines.add(machine);
                } else {
                    //LOGGER.info("roleset ==> "+roleSet.toString());
                    JSONArray roles = (JSONArray)jsonObject.get("Roles");
                    //LOGGER.info("machine roles ==>"+roles.toJSONString());
                    for(Object obj : roles) {
                        String role = (String)obj;
                        if(roleSet.contains(role.toLowerCase(locale))) {
                            //LOGGER.info("adding machine by role "+role);
                            machines.add(machine);
                            break;
                        }
                    }
                }
            }

            JSONObject links = (JSONObject)resJsonObject.get("Links");
            urlPath = (String)links.get("Page.Next");

            if(urlPath == null || urlPath.isEmpty()) {
                hasNext = false;
            }
        }

        return machines;
    }

    private Release getReleaseById(String id) {
        JSONObject resJsonObject =  paresResponse(makeRestCall(octopusSettings.getUrl()[contextOS],
                "/api/releases/"+id,octopusSettings.getApiKey()[contextOS]));
        Release rel = new Release();

        rel.setApplicationId((String)resJsonObject.get("ProjectId"));
        rel.setReleaseId((String)resJsonObject.get("Id"));
        rel.setVersion((String)resJsonObject.get("Version"));
        rel.setDeploymentProcessSnapShotId((String)resJsonObject.get("ProjectDeploymentProcessSnapshotId"));

        return rel;
    }

    private OctopusEnvironment getEnvironmentById(String envId){

        JSONObject resJsonObject =  paresResponse(makeRestCall(octopusSettings.getUrl()[contextOS],
                "/api/environments/"+envId,octopusSettings.getApiKey()[contextOS]));
        OctopusEnvironment env = new OctopusEnvironment();
        env.setEnvName((String)resJsonObject.get("Name"));
        env.setEnvId((String)resJsonObject.get("Id"));


        return env;



    }

    private ResponseEntity<String> makeRestCall(String instanceUrl,
                                                String endpoint,String apiKey) {
        String url = instanceUrl+ endpoint;
        ResponseEntity<String> response = null;
        try {
            response = restOperations.exchange(url, HttpMethod.GET,
                    new HttpEntity<>(createHeaders("X-Octopus-ApiKey",apiKey)), String.class);

        } catch (RestClientException re) {
            LOGGER.error("Error with REST url: " + url);
            LOGGER.error(re.getMessage());
        }
        return response;
    }

    protected HttpHeaders createHeaders(String headerName,String headerValue) {

        HttpHeaders headers = new HttpHeaders();
        headers.set(headerName, headerValue);
        return headers;
    }

    private JSONObject paresResponse(ResponseEntity<String> response) {
        if (response == null)
            return new JSONObject();
        try {

            JSONObject jsonObject = (JSONObject)new JSONParser().parse(response.getBody());
            return jsonObject;

        } catch (ParseException pe) {
            LOGGER.debug(response.getBody());
            LOGGER.error(pe.getMessage());
        }
        return new JSONObject();
    }


    private String str(JSONObject json, String key) {

        Object value = json.get(key);
        return value == null ? null : value.toString();
    }

}
