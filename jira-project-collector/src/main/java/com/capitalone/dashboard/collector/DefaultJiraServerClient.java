package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.ProjectVersionIssues;
import com.capitalone.dashboard.model.JiraRepo;
import com.capitalone.dashboard.util.Encryption;
import com.capitalone.dashboard.util.EncryptionException;
import com.capitalone.dashboard.util.Supplier;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vinod on 7/9/16.
 * This is an implementation that would display Jira project version information.
 */
@Component
public class DefaultJiraServerClient implements JiraClient{
    private static final Log LOG = LogFactory.getLog(DefaultBitbucketServerClient.class);

    private final JiraSettings settings;

    private final RestOperations restOperations;

    @Autowired
    public DefaultJiraServerClient(JiraSettings settings,
                                        Supplier<RestOperations> restOperationsSupplier) {
        this.settings = settings;
        this.restOperations = restOperationsSupplier.get();
    }

    @Override
    public List<ProjectVersionIssues> getprojectversionissues(JiraRepo jirarepo,boolean firstRun ){
        List<ProjectVersionIssues> projectVersionIssues = new ArrayList<>();
        URI queryUriPage = null;
        //Sample url
        //https://starbucks-mobile.atlassian.net/rest/api/2/search?jql=project=%22API%22+AND+fixVersion=%27Chase%20Pay%201.0%27&maxResults=1000&fields=summary,status
        try {

            //url parts: url

        }
    }

}
