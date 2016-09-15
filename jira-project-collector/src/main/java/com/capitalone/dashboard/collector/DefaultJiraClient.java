package com.capitalone.dashboard.collector;

/**
 * Created by vinod on 8/9/16.
 */


//import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.util.concurrent.Promise;
import com.capitalone.dashboard.model.ProjectVersionIssues;
import com.capitalone.dashboard.model.JiraRepo;
//import com.capitalone.dashboard.util.Encryption;
//import com.capitalone.dashboard.util.EncryptionException;
import com.capitalone.dashboard.util.Supplier;
import com.capitalone.dashboard.util.ClientUtil;
//import org.apache.commons.codec.binary.Base64;
import com.google.common.collect.Lists;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.apache.http.client.utils.URIBuilder;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
import org.apache.http.client.utils.URIBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import com.atlassian.jira.rest.client.api.JiraRestClient;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
//import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

//Implementation to connect to Jira server
@Component
public class DefaultJiraClient implements JiraClient {
    private static final Log LOG = LogFactory.getLog(DefaultJiraClient.class);
    private final JiraSettings settings;

    private final RestOperations restOperations;

    private JiraRestClient client;
    private JSONObject projectVersions = null;

    private static final ClientUtil TOOLS = ClientUtil.getInstance();
    private JiraRestClientSupplier jiraRestClientSupplier;

    private static final Set<String> DEFAULT_FIELDS = new HashSet<>();
    static {
        DEFAULT_FIELDS.add("summary,status");
    }


    @Autowired
    public DefaultJiraClient(JiraSettings settings,Supplier<RestOperations> restOperationsSupplier,JiraRestClientSupplier restSupplier){
        this.settings = settings;
        this.client = restSupplier.get();
        this.restOperations = restOperationsSupplier.get();
        this.jiraRestClientSupplier = restSupplier;
    }

    @Override
    public List<ProjectVersionIssues> getprojectversionissues(JiraRepo jirarepo,  boolean firstrun){
        List<ProjectVersionIssues> projectversionissues = new ArrayList<>();

        URI queryUriPage = null;
        try{
//            Set<String> flds = new HashSet<String>();
//            flds.add("status");
//            flds.add("summary");
//            Promise<SearchResult> promisedRs= client.getSearchClient().searchJql("project in (" + (String) jirarepo.getOptions().get("projectId") + ") AND fixVersion in (" +  (String) jirarepo.getOptions().get("versionId") + ")",0,1000,flds);
//            SearchResult srs = promisedRs.claim();
//             Iterable<Issue> issues =  srs.getIssues();
//            List<Issue> arryIssues = Lists.newArrayList(issues);
//            for(Issue iss : arryIssues){
//                LOG.info(iss.getSummary().toString());
//            }


//            LOG.info(jiraRestClientSupplier.decodeCredentials(settings.getJiraCredentials()).get("username"));
//            LOG.info(jiraRestClientSupplier.decodeCredentials(settings.getJiraCredentials()).get("password"));
//            ResponseEntity<String> response = makeRestCall1(buildUriVersionIssues(),jiraRestClientSupplier.decodeCredentials(settings.getJiraCredentials()).get("username"),jiraRestClientSupplier.decodeCredentials(settings.getJiraCredentials()).get("password"),(String) jirarepo.getOptions().get("projectId"),(String) jirarepo.getOptions().get("versionId"));
//            LOG.info("Returned");
//            LOG.info(response);
//            JSONObject jsonParentObject = paresAsObject(response);
//            projectversionissues = (JSONArray) jsonParentObject.get("issues");

          Promise<SearchResult> src =  client.getSearchClient().searchJql("project in (" + (String) jirarepo.getOptions().get("projectId") + ") AND fixVersion in (" + (String) jirarepo.getOptions().get("versionId") + ")",500,0,null);
            final SearchResult searchResult = src.claim();
            final Iterable<Issue> searchResultIssues = searchResult.getIssues();
            if(searchResultIssues != null){
                //LOG.info("Not Null");
                int count = 0;
                for(Issue issue : searchResultIssues){
                    //Status{self=https://starbucks-mobile.atlassian.net/rest/api/2/status/11002, name=Done, id=11002, description=, iconUrl=https://starbucks-mobile.atlassian.net/}
                    //LOG.info(issue.getId() + " - " + issue.getStatus().getName());
                    ProjectVersionIssues pvi = new ProjectVersionIssues();
                    pvi.setIssueDescription(issue.getSummary());
                    pvi.setIssueId(issue.getId().toString());
                    pvi.setIssueStatus(issue.getStatus().getName());
                    pvi.setProjectName((String)jirarepo.getOptions().get("projectName"));
                    pvi.setVersionName((String)jirarepo.getOptions().get("versionName"));
                    projectversionissues.add(pvi);
                    count++;
                }
               // LOG.info("Found " + count + " issues");
            }

        }
        catch (Exception e) {
            LOG.error("Error in call: " + e.getMessage());
        }

        return projectversionissues;
    }

    @Override
    public List<JiraRepo> getProjects() {
        List<BasicProject> rt = new ArrayList<>();
       // List<String> projectVersions = new ArrayList<>();
        List<JiraRepo> projectVersions = new ArrayList<JiraRepo>();
        if (client != null) {
            try {
                Promise<Iterable<BasicProject>> promisedRs = client.getProjectClient().getAllProjects();
//client.getSearchClient().searchJql()
//               Promise<SearchResult> promisedR1s =  client.getSearchClient().searchJql("test");
//                SearchResult searchResult = promisedR1s.claim();
//                searchResult.getIssues()
                Iterable<BasicProject> jiraRawRs = promisedRs.claim();
                if (jiraRawRs != null) {
                    rt = Lists.newArrayList(jiraRawRs);
                    int count;
                    count = 0;
                    for (BasicProject jiraProject : rt) {
                        String projectName = TOOLS.sanitizeResponse(jiraProject.getName());
                        String projectID = TOOLS.sanitizeResponse(jiraProject.getId());
                        //Fetch Version
                       // LOG.info(projectName);
                        JSONArray versions = getProjectVersions(projectID);
                        int versioncount = 0;
                        for(Object version : versions){
                            JiraRepo jr = new JiraRepo();
                            jr.setPROJECTID(projectID);
                            jr.setPROJECTNAME(projectName);
                            jr.setVERSIONID(str((JSONObject)version,"id"));
                            jr.setVERSIONDESCRIPTION(str((JSONObject)version,"description"));
                            jr.setVERSIONNAME(str((JSONObject)version,"name"));
                            projectVersions.add(jr);
                           // LOG.info("Added:" + jr.getVERSIONNAME());
                            count++;
                            versioncount++;
                        }
                      //  LOG.info("For " + projectName + " found " + versioncount + " Versions.");
                        //LOG.info(versions);
                        //projectVersions.add(versions);
                    }
                    LOG.info("Scanned " + count + " projects.");

                }

            } catch (com.atlassian.jira.rest.client.api.RestClientException e) {
                if (e.getStatusCode().get() != null && e.getStatusCode().get() == 401 ) {
                    LOG.error("Error 401 connecting to JIRA server, your credentials are probably wrong. Note: Ensure you are using JIRA user name not your email address.");
                } else {
                    LOG.error("No result was available from Jira unexpectedly - defaulting to blank response. The reason for this fault is the following:" + e.getCause());
                }
                LOG.debug("Exception", e);
            }
        } else {
            LOG.warn("Jira client setup failed. No results obtained. Check your jira setup.");
        }

        return projectVersions;
    }

    private JSONArray getProjectVersions(String projectName){
        JSONArray jsonParentObject = new JSONArray();
        try{

            ResponseEntity<String> response = makeRestCall(buildUriVerion(projectName),jiraRestClientSupplier.decodeCredentials(settings.getJiraCredentials()).get("username"),jiraRestClientSupplier.decodeCredentials(settings.getJiraCredentials()).get("password"));
            jsonParentObject = paresAsArray(response);

        }
        catch (URISyntaxException e) {
            LOG.error("Invalid uri: " + e.getMessage());
        } catch (RestClientException re) {
            LOG.error("Failed to obtain versions from " + projectName, re);
            return jsonParentObject;
        }
        return  jsonParentObject;
    }


    private ResponseEntity<String> makeRestCall(String url, String userId,
                                                String password) {
        // Basic Auth only.
        if (!"".equals(userId) && !"".equals(password)) {
            //LOG.info("Call with userid and password");
            return restOperations.exchange(url, HttpMethod.GET,
                    new HttpEntity<>(createHeaders(userId, password)),
                    String.class);

        } else {
            return restOperations.exchange(url, HttpMethod.GET, null,
                    String.class);
        }

    }

   // private ResponseEntity<String> makeRestCall1(String url, String userId,
   //                                             String password,String projectId,String versionId) {
//        // Basic Auth only.
//        String params = "";
//     //   String qu ="jql=project in (\"" + projectId + "\")"; // AND fixVersion in ('" + versionId + "')";
//        String qu ="project in (\"" + projectId + "\") AND fixVersion in (\"" + versionId + "\")";
//        try {
//           params = URLEncoder.encode(qu,"UTF-8") + "&fields=summary,status";
//            //params += "&fields=summary,status";
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        if (!"".equals(userId) && !"".equals(password)) {
//            LOG.info("Call with userid and password");
//            LOG.info(url + "?jql={q}" + params);
//            return restOperations.exchange(url + "?jql=" + params, HttpMethod.GET,
//                    new HttpEntity<>(createHeaders(userId, password)),
//                    String.class,params);
//
//        } else {
//            return restOperations.exchange(url, HttpMethod.GET, null,
//                    String.class);
//        }

  //  }


    private HttpHeaders createHeaders(final String userId, final String password) {
        String auth = userId + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        return headers;
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

    private String str(JSONObject json, String key) {
        Object value = json.get(key);
        return value == null ? null : value.toString();
    }

   // String buildUriVersionIssues(final String projectId, final String versionId) throws URISyntaxException {
        String buildUriVersionIssues() throws URISyntaxException {
        //https://starbucks-mobile.atlassian.net/rest/api/2/search?jql=project=%2714500%27AND+fixVersion=%2718600%27&maxResults=1000&fields=summary,status
        //https://starbucks-mobile.atlassian.net/rest/api/2/search?jql=project+in+('14500')+AND+fixVersion+in+('18600')
//        String qu ="project in (\"" + projectId + "\")"; // AND fixVersion in ('" + versionId + "')";
//        URIBuilder uriBuilder = new URIBuilder(settings.getJiraBaseUrl() + "/" +  settings.getApi() + "/search");
//        uriBuilder.setParameter("jql",qu);
//        uriBuilder.setUserInfo(jiraRestClientSupplier.decodeCredentials(settings.getJiraCredentials()).get("username"),jiraRestClientSupplier.decodeCredentials(settings.getJiraCredentials()).get("password"));
//        LOG.info(uriBuilder.build().toString());
//        return(uriBuilder.build());
//        try {
//            qu = URLEncoder.encode(qu,"UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            LOG.error(e.getMessage());
//        }
        String url = settings.getJiraBaseUrl() + "/" +  settings.getApi() + "/search"; //?jql=" + qu  + "&maxResults=1000"; //&fields=summary%2Cstatus

        LOG.info(url);
        return url;
    }

    String buildUriVerion(String projectname)throws URISyntaxException {
        //projectname = projectname.replaceAll(" ","%20");
        String url = settings.getJiraBaseUrl() + "/" +  settings.getApi() + "/project/" + projectname.replaceAll(" ","%20") + "/versions" + "?maxResults=50&startAt=0";

        LOG.info(url);
        return url;
    }
}
