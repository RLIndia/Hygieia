    package com.capitalone.dashboard.collector;

    import com.capitalone.dashboard.model.CatalystDeploys;
    import com.capitalone.dashboard.model.CatalystRepo;
    import com.capitalone.dashboard.model.CatalystDeploysTask;

    import org.apache.commons.codec.binary.Base64;
    import org.apache.commons.logging.Log;
    import org.apache.commons.logging.LogFactory;
    import org.apache.http.*;
    import org.apache.http.protocol.HTTP;
    import org.json.simple.JSONArray;
    import org.json.simple.JSONObject;
    import org.json.simple.parser.JSONParser;
    import org.json.simple.parser.ParseException;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.*;
    import org.springframework.http.HttpEntity;
    import org.springframework.http.HttpHeaders;
    import org.springframework.stereotype.Component;
    import org.springframework.web.client.RestOperations;
    import com.capitalone.dashboard.util.Supplier;

    import com.capitalone.dashboard.util.ClientUtil;



    import java.nio.charset.StandardCharsets;
    import java.util.ArrayList;
    import java.util.List;

    /**
     * Created by Vinod on 19/9/16.
     */
        @Component
        public class DefaultCatalystClient implements CatalystClient{private static final Log LOG = LogFactory.getLog(DefaultCatalystClient.class);
        private final CatalystSettings settings;
        private final RestOperations restOperations;
        private String restToken = "";
        private static final ClientUtil TOOLS = ClientUtil.getInstance();

        @Autowired
        public DefaultCatalystClient(CatalystSettings settings,Supplier<RestOperations> restOperationsSupplier){
            this.settings = settings;
            this.restOperations = restOperationsSupplier.get();




        }


       @Override
       public  List<CatalystRepo> getCatalystRepos(){
           getToken(); //fetching the token.
           List<CatalystRepo> catalystRepos = new ArrayList<CatalystRepo>();
          // LOG.info("Token:" + this.restToken);
           if(this.restToken != ""){
                try{
                    ResponseEntity<String> response = getAllCatalystRepos(settings.getCatalystBaseUrl() + "/d4dMasters/readmasterjsonnew/4"); //returns all projects array
                    JSONArray jsonResponse = paresAsArray(response);
                    String repotype = settings.getRepository();
                    for(Object jtask : jsonResponse){
                        JSONObject catprojectrepo = (JSONObject) jtask;
//                        LOG.info(catprojectrepo.get("repositories." + repotype));
                       JSONArray repos =  (JSONArray) ((JSONObject)catprojectrepo.get("repositories")).get(repotype);
                        for(Object jrepo : repos){
//                            LOG.info("Reading Repo");
//                            LOG.info(jrepo.toString());
//                            LOG.info("End Reading Repo");
                        CatalystRepo cr = new CatalystRepo();
                            JSONArray orgids =  (JSONArray) catprojectrepo.get("orgname_rowid");
                            cr.setORGID(cleanString(orgids.get(0).toString()));


                        cr.setBGID(str((JSONObject) jtask,"productgroupname_rowid"));
                        cr.setPROJECTID(str((JSONObject) jtask,"rowid"));
                        cr.setREPOSITORYNAME(jrepo.toString());

                        //Getting the first orgname from arra
                            JSONArray orgs =  (JSONArray) catprojectrepo.get("orgname");
                          cr.setORGNAME(cleanString(orgs.get(0).toString()));
                        //cr.setORGNAME(str((JSONObject) jtask,"orgname"));
                        cr.setBGNAME(str((JSONObject) jtask,"productgroupname"));
                        cr.setPROJECTNAME(str((JSONObject) jtask,"projectname"));

                        catalystRepos.add(cr);

                        }

                    }


                    //LOG.info(jsonResponse.toString());

                }catch(Exception e)
                {
                        LOG.info("Error " + e.getMessage());
                }
           }
           return catalystRepos;
       }

       @Override
        public List<CatalystDeploys> getCatalystDeploys(CatalystRepo catalystRepo, boolean firstrun){
           List<CatalystDeploys> catalystDeployses = new ArrayList<CatalystDeploys>();

        //   http://localhost:4001/app-deploy/project/b38ccedc-da2c-4e2c-a278-c66333564719/application/catalyst
           String repotaskurl = settings.getCatalystBaseUrl() + "/app-deploy/project/" + catalystRepo.getPROJECTID() + "/repository/" + catalystRepo.getREPOSITORYNAME();
            LOG.info(repotaskurl);
               try {
                   if(this.restToken == "") {
                      getToken();
                   }
                   ResponseEntity<String> response = getAllCatalystRepos(repotaskurl); //returns repo details
                //   LOG.info("Returned from response");
                  // LOG.info(response.toString());
                   JSONArray jsonResponse = paresAsArray(response);
                   String repotype = settings.getRepository();
                   int tempCount =0;
                   for (Object jtask : jsonResponse) {
                       JSONObject cattask = (JSONObject) jtask;
                       LOG.info("Env:" + cattask.get("envName"));
                       LOG.info("Version:" + cattask.get("version"));
                       JSONObject deployobj = (JSONObject) cattask.get(settings.getRepository());

                       LOG.info("Task ID:" + (deployobj.get("taskId").toString().isEmpty() == true));
                       if(deployobj.get("taskId").toString().isEmpty() == false) {
                           String taskdetailsurl = settings.getCatalystBaseUrl() + "/tasks/" + deployobj.get("taskId");
                           LOG.info("Task Url:" + taskdetailsurl);
                           ResponseEntity<String> taskResponse = getAllCatalystRepos(taskdetailsurl);
                           JSONObject jsonTaskResponse = paresAsObject(taskResponse);
                           LOG.info("Last Status:" + jsonTaskResponse.get("lastTaskStatus"));
                           LOG.info("Last Run Time:" + jsonTaskResponse.get("lastRunTimestamp"));
                           CatalystDeploys cd = new CatalystDeploys();
                           cd.setEnvName(cattask.get("envName").toString());
                           cd.setVersion(cattask.get("version").toString());
                           cd.setTaskId(deployobj.get("taskId").toString());
                           cd.setExecutedDate(jsonTaskResponse.get("lastRunTimestamp").toString());
                           cd.setLastTaskStatus(jsonTaskResponse.get("lastTaskStatus").toString());
                           catalystDeployses.add(cd);

                       }else{
                           LOG.info("No Task ID found. Skipping");
                       }
                       LOG.info("In run : " + tempCount);
                       tempCount++;
                   }
               } catch (Exception e) {
                   LOG.info("Error " + e.getMessage());
               }

           // from the above get nexus.taskId and run the below

        //   http://localhost:4001/tasks/57e22701b1012b8004e9249f
            return catalystDeployses;

       }

       @Override
       public List<CatalystDeploysTask> getCatalystDeploysTasks(String taskId){
           List<CatalystDeploysTask> catalystDeploysTasks = new ArrayList<CatalystDeploysTask>();
           String repotaskhistoryurl = settings.getCatalystBaseUrl() + "/tasks/" + taskId + "/history";
           LOG.info(repotaskhistoryurl);
           try {
               if (this.restToken == "") {
                   getToken();
               }
               ResponseEntity<String> response = getAllCatalystRepos(repotaskhistoryurl); //returns repo historydetails
               JSONArray jsonResponse = paresAsArray(response);
               for (Object jtask : jsonResponse) {
                   JSONObject cattask = (JSONObject) jtask;
                   CatalystDeploysTask cdt = new CatalystDeploysTask();
                   cdt.setTaskId(taskId);
                   cdt.setExecutedDate(cattask.get("timestampStarted").toString());
                   cdt.setStatus(cattask.get("status").toString());
                   cdt.setNodeNames(cattask.get("nodeIds").toString());
                   catalystDeploysTasks.add(cdt);
                   LOG.info("Task ID" + taskId + " status " + cdt.getStatus());
               }
           }catch(Exception e) {
               LOG.info("Error " + e.getMessage());
           }
           return catalystDeploysTasks;
       }

       private void getToken(){
           try {
               ResponseEntity<String> response = getToken(settings.getCatalystBaseUrl() + "/auth/signin",settings.getCatalystUserName(),settings.getCatalystPassword());
               JSONObject jtoken = paresAsObject(response);

               this.restToken = jtoken.get("token").toString();
               LOG.info(this.restToken);

               //Proceed only when there is a token.
           }catch (Exception e){
               LOG.info("Could not receive token:");
               LOG.info(e.getMessage());

           }
       }

        private JSONArray paresAsArray(ResponseEntity<String> response) {
            try {
                return (JSONArray) new JSONParser().parse(response.getBody());
            } catch (ParseException pe) {
                LOG.error(pe.getMessage());
            }
            return new JSONArray();
        }

        private JSONObject paresAsObject(ResponseEntity<String> response) {
            try {
                return (JSONObject) new JSONParser().parse(response.getBody());
            } catch (ParseException pe) {
                LOG.error(pe.getMessage());
            }
            return new JSONObject();
        }

        private ResponseEntity<String> getAllCatalystRepos(String url) {


            // Basic Auth only.
            if (!"".equals(url)) {
                //LOG.info("Call with userid and password");
         //       LOG.info("IN getcatalyst data. Hitting " + url);
                JSONObject request = new JSONObject();
                HttpHeaders headers = new HttpHeaders();
                headers.add("x-catalyst-auth",this.restToken);
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> entity = new HttpEntity<String>(request.toString(),headers);
                return restOperations.exchange(url, HttpMethod.GET,
                        entity,String.class);

            } else {
                return restOperations.exchange(url, HttpMethod.GET, null,
                        String.class);
            }

        }



        private ResponseEntity<String> getToken(String url, String userName,
                                                    String password) {
            JSONObject request = new JSONObject();
            request.put("username",userName);
            request.put("pass",password);
            request.put("authType","token");
         //   LOG.info("About to get token ->" + userName + " " + password + " for " + url);
            // Basic Auth only.
            if (!"".equals(userName) && !"".equals(password)) {
                //LOG.info("Call with userid and password");
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> entity = new HttpEntity<String>(request.toString(),headers);
                return restOperations.exchange(url, HttpMethod.POST,
                        entity,String.class);

            } else {
                return restOperations.exchange(url, HttpMethod.GET, null,
                        String.class);
            }

        }

        private HttpHeaders createHeaders(final String token) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-catalyst-auth",token);
            return headers;
        }

        private String str(JSONObject json, String key) {
            Object value = json.get(key);
            return value == null ? null : value.toString();
        }

        private String cleanString(String txt){
            return txt.replace("[", "").replace("]","").replace("\"","");
        }

    }
