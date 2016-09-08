package com.capitalone.dashboard.collector;

/**
 * Created by vinod on 8/9/16.
 */


import com.capitalone.dashboard.model.ProjectVersionIssues;
import com.capitalone.dashboard.model.JiraRepo;
//import com.capitalone.dashboard.util.Encryption;
//import com.capitalone.dashboard.util.EncryptionException;
import com.capitalone.dashboard.util.Supplier;
//import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.apache.http.client.utils.URIBuilder;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.net.URISyntaxException;
//import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

//Implementation to connect to Jira server
@Component
public class DefaultJiraClient implements JiraClient {
    private static final Log LOG = LogFactory.getLog(DefaultJiraClient.class);
    private final JiraSettings settings;

    private final RestOperations restOperations;

    @Autowired
    public DefaultJiraClient(JiraSettings settings,Supplier<RestOperations> restOperationsSupplier){
        this.settings = settings;
        this.restOperations = restOperationsSupplier.get();
    }

    @Override
    public List<ProjectVersionIssues> getprojectversionissues(JiraRepo jirarepo,  boolean firstrun){
        List<ProjectVersionIssues> projectversionissues = new ArrayList<>();
        URI queryUriPage = null;
        try{
            URI queryUri = buildUri((String) jirarepo.getOptions().get("projectname"),(String) jirarepo.getOptions().get("versionname"));
            LOG.info(queryUri);

        }
        catch (URISyntaxException e) {
            LOG.error("Invalid uri: " + e.getMessage());
        } catch (RestClientException re) {
            LOG.error("Failed to obtain commits from " + queryUriPage, re);
        }

        return projectversionissues;
    }

    URI buildUri(final String projectname, final String versionname) throws URISyntaxException {
        //https://starbucks-mobile.atlassian.net/rest/api/2/search?jql=project=%22API%22+AND+fixVersion=%27Chase%20Pay%201.0%27&maxResults=1000&fields=summary,status


        URI uri = URI.create(settings.getJiraBaseUrl() + "/" +  settings.getApi() + "/search?jql=project=%22" + projectname + "%22+AND+fixVersion=%27" + versionname.replaceAll(" ","%20"));
        LOG.info(uri);
        return uri;
    }
}
