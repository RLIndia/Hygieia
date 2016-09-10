package com.capitalone.dashboard.collector;

/**
 * Created by vinod on 8/9/16.
 */


//import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.apache.http.client.utils.URIBuilder;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
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

import java.net.URI;
import java.net.URISyntaxException;
//import java.nio.charset.StandardCharsets;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
            URI queryUri = buildUriVersionIssues((String) jirarepo.getOptions().get("projectname"),(String) jirarepo.getOptions().get("versionname"));
            LOG.info(queryUri);

        }
        catch (URISyntaxException e) {
            LOG.error("Invalid uri: " + e.getMessage());
        } catch (RestClientException re) {
            LOG.error("Failed to obtain commits from " + queryUriPage, re);
        }

        return projectversionissues;
    }

    @Override
    public List<String> getProjects() {
        List<BasicProject> rt = new ArrayList<>();
        List<String> projectVersions = new ArrayList<>();
        if (client != null) {
            try {
                Promise<Iterable<BasicProject>> promisedRs = client.getProjectClient().getAllProjects();

//               Promise<SearchResult> promisedR1s =  client.getSearchClient().searchJql("test");
//                SearchResult searchResult = promisedR1s.claim();
//                searchResult.getIssues()
                Iterable<BasicProject> jiraRawRs = promisedRs.claim();
                if (jiraRawRs != null) {
                    rt = Lists.newArrayList(jiraRawRs);

                    for (BasicProject jiraProject : rt) {
                        String projectName = TOOLS.sanitizeResponse(jiraProject.getId());
                        //Fetch Version
                        LOG.info(projectName);
                        String versions = getProjectVersions(projectName).toJSONString();
                        LOG.info(versions);
                        projectVersions.add(versions);
                    }

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
            LOG.info("Call with userid and password");
            return restOperations.exchange(url, HttpMethod.GET,
                    new HttpEntity<>(createHeaders(userId, password)),
                    String.class);

        } else {
            return restOperations.exchange(url, HttpMethod.GET, null,
                    String.class);
        }

    }

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

    URI buildUriVersionIssues(final String projectname, final String versionname) throws URISyntaxException {
        //https://starbucks-mobile.atlassian.net/rest/api/2/search?jql=project=%22API%22+AND+fixVersion=%27Chase%20Pay%201.0%27&maxResults=1000&fields=summary,status


        URI uri = URI.create(settings.getJiraBaseUrl() + "/" +  settings.getApi() + "/search?jql=project=%22" + projectname + "%22+AND+fixVersion=%27" + versionname.replaceAll(" ","%20"));
        LOG.info(uri);
        return uri;
    }

    String buildUriVerion(String projectname)throws URISyntaxException {
        //projectname = projectname.replaceAll(" ","%20");
        String url = settings.getJiraBaseUrl() + "/" +  settings.getApi() + "/project/" + projectname.replaceAll(" ","%20") + "/versions" + "?maxResults=50&startAt=0";
        LOG.info(url);
        return url;
    }
}
