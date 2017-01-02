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

import com.capitalone.dashboard.model.EnvironmentProjectsAll;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
    public OctopusDashboard getDashboard() {
        String urlPath = "/api/dashboard/dynamic";
        //hit the dashboard url and store in the dashboard model then push to core repo

        OctopusDashboard od = new OctopusDashboard();

        JSONObject resDashboardObject = paresResponse(makeRestCall(octopusSettings.getUrl()[contextOS],
                urlPath,octopusSettings.getApiKey()[contextOS]));

        //Reading the project groups
        JSONArray jsonProjectGroupsArray = (JSONArray)resDashboardObject.get("ProjectGroups");
        List<OctopusProjectGroup> lnpg = new ArrayList<>();
        for(Object pg : jsonProjectGroupsArray){
            OctopusProjectGroup npg = new OctopusProjectGroup();
            JSONObject jobj = (JSONObject) pg;
            npg.setProjectGroupId(str(jobj,"Id"));
            npg.setProjectGroupName(str(jobj,"Name"));
            lnpg.add(npg);
        }
        od.setOctopusProjectGroups(lnpg);
        LOGGER.info("Finished reading Project Groups");
        //reading the projects
        JSONArray jsonProjectsArray = (JSONArray)resDashboardObject.get("Projects");
        List<OctopusProject> lnp = new ArrayList<>();
        for(Object prj : jsonProjectsArray){
            OctopusProject np = new OctopusProject();
            JSONObject jobj = (JSONObject) prj;
            np.setProjectId(str(jobj,"Id"));
            np.setProjectName(str(jobj,"Name"));
           // LOGGER.info("-----" + str(jobj,"ProjectGroupId"));
            np.setProjectGroupName(od.getProjectGroupNameByID(str(jobj,"ProjectGroupId")));
            lnp.add(np);
        }
        od.setOctopusProjects(lnp);
        LOGGER.info("Finished reading Projects");

        //reading environments
        JSONArray jsonEnvironmentsArray = (JSONArray)resDashboardObject.get("Environments");
        List<OctopusEnvironment> lne = new ArrayList<>();
        for(Object env : jsonEnvironmentsArray){
            OctopusEnvironment en = new OctopusEnvironment();
            JSONObject jobj = (JSONObject) env;
            en.setEnvId(str(jobj,"Id"));
            en.setEnvName(str(jobj,"Name"));
          //  LOGGER.info("-----" + en.getEnvName());

            lne.add(en);
        }
        od.setOctopusEnvironments(lne);
        LOGGER.info("Finished reading Envs");

        //reading items

        JSONArray jsonItemsArray = (JSONArray)resDashboardObject.get("Items");
        List<EnvironmentProjectsAll> itms = new ArrayList<>();
        for(Object item : jsonItemsArray){
            EnvironmentProjectsAll envProject = new EnvironmentProjectsAll();
            JSONObject jobj = (JSONObject) item;
            envProject.setProjectId(str(jobj,"ProjectId"));
            envProject.setProjectName(od.getProjectNameByID(envProject.getProjectId()));
            envProject.setProjectGroupName(od.getProjectGroupNameByProjectID(envProject.getProjectId()));
            envProject.setEnvironmentId(str(jobj,"EnvironmentId"));
            envProject.setEnvironmentName(od.getEnvironmentNameByID(envProject.getEnvironmentId()));
          //  LOGGER.info(envProject.getProjectName());
            String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
            DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);

            try {
                DateTime dateTime = dtf.parseDateTime(str(jobj, "CompletedTime"));
                envProject.setCompletedDate(dateTime.getMillis());
            }catch (Exception e){
                LOGGER.info("Exception " + e);
            }

            if(str(jobj,"State").equals("Success"))
                envProject.setStatus(true);
            else
                envProject.setStatus(false);

            envProject.setReleaseVersion(str(jobj,"ReleaseVersion"));
            itms.add(envProject);
            //  LOGGER.info("-----" + en.getEnvName());


        }
        LOGGER.info("Finished reading items.");
        od.setEnvironmentProjectsAll(itms);
        //LOGGER.info("+++++++++++++++++++++++++++++++++++++++++++++++++Finished reading Items");


        return od;
    }


    @Override
    public void setContext(int sc) {
        this.contextOS = sc;
    }

    @Override
    public int getContext() {
        return this.contextOS;
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
