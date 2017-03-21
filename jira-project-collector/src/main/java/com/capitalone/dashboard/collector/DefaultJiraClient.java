package com.capitalone.dashboard.collector;

import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
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
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.httpclient.apache.httpcomponents.DefaultHttpClientFactory;
import com.atlassian.httpclient.api.factory.HttpClientOptions;

/**
 * Created by vinod on 8/9/16.
 */

//import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousHttpClientFactory;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AtlassianHttpClientDecorator;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import com.atlassian.sal.api.ApplicationProperties;
/*import com.atlassian.sal.api.executor.ThreadLocalContextManager;*/
import com.atlassian.util.concurrent.Promise;
import com.capitalone.dashboard.model.DefectInjection;
import com.capitalone.dashboard.model.JiraRepo;
import com.capitalone.dashboard.model.ProjectVersionIssues;
import com.capitalone.dashboard.model.Sprint;
import com.capitalone.dashboard.model.SprintVelocity;
import com.capitalone.dashboard.util.ClientUtil;
//import com.capitalone.dashboard.util.Encryption;
//import com.capitalone.dashboard.util.EncryptionException;
import com.capitalone.dashboard.util.Supplier;
//import org.apache.commons.codec.binary.Base64;
import com.google.common.collect.Lists;

//Implementation to connect to Jira server
@Component
public class DefaultJiraClient implements JiraClient {

	private static final Log LOG = LogFactory.getLog(DefaultJiraClient.class);
	private final JiraSettings settings;

	private final RestOperations restOperations;

	private JiraRestClient client;
	private JSONObject projectVersions = null;
	URI jiraServerURI = null;
	
	private static final ClientUtil TOOLS = ClientUtil.getInstance();
	private JiraRestClientSupplier jiraRestClientSupplier;

	private static final Set<String> DEFAULT_FIELDS = new HashSet<>();
	static {
		DEFAULT_FIELDS.add("summary,status");
	}

	@Autowired
	public DefaultJiraClient(JiraSettings settings, Supplier<RestOperations> restOperationsSupplier,
			JiraRestClientSupplier restSupplier) {
		this.settings = settings;
		this.client = restSupplier.get();
		this.restOperations = restOperationsSupplier.get();
		this.jiraRestClientSupplier = restSupplier;
	}
	
	/*JiraRestClient getJiraRestClient() throws Exception
	{
		try
		{
			jiraServerURI = new URI(settings.getJiraBaseUrl());
		}
		catch(URISyntaxException e)
		{
			System.out.println(e);
		}
	    return new AsynchronousJiraRestClient(jiraServerURI, getHttpClient());
	}
	
	HttpClientOptions getClientOptions()
	{
		HttpClientOptions options = new HttpClientOptions();
	    options.setSocketTimeout(60, TimeUnit.SECONDS);
	    return options;
	}
	
	public static Object giveMeAHCFEventPubInstance() throws Exception{
		AsynchronousHttpClientFactory ahcf = new AsynchronousHttpClientFactory();
        Class<?> EventPublisher = Class.forName("com.atlassian.jira.rest.client.internal.async.AsynchronousHttpClientFactory$NoOpEventPublisher");
        Constructor<?> constructor = EventPublisher.getDeclaredConstructor(AsynchronousHttpClientFactory.class);
        constructor.setAccessible(true);
        return constructor.newInstance(ahcf);
    }
	
	public Object giveMeAHCFAppPropsInnerInstance() throws Exception{
		AsynchronousHttpClientFactory ahcf = new AsynchronousHttpClientFactory();
        Class<?> ApplicationProperties = Class.forName("com.atlassian.jira.rest.client.internal.async.AsynchronousHttpClientFactory$RestClientApplicationProperties");
        Constructor<?> constructor = ApplicationProperties.getDeclaredConstructor(AsynchronousHttpClientFactory.class,URI.class);
        constructor.setAccessible(true);
        try
		{
			jiraServerURI = new URI(settings.getJiraBaseUrl());
		}
		catch(URISyntaxException e)
		{
			System.out.println(e);
		}
        return constructor.newInstance(ahcf,jiraServerURI);
    }
	
	
	DisposableHttpClient getHttpClient() throws Exception
	{
		AsynchronousHttpClientFactory ahcf = new AsynchronousHttpClientFactory();
	    final DefaultHttpClientFactory defaultHttpClientFactory =
	        new DefaultHttpClientFactory((EventPublisher)giveMeAHCFEventPubInstance(),
	        		(ApplicationProperties)giveMeAHCFAppPropsInnerInstance(),
	        new ThreadLocalContextManager() {
	            @Override
	            public Object getThreadLocalContext() {
	                return null;
	            }

	            @Override
	            public void setThreadLocalContext(Object context) {}

	            @Override
	            public void clearThreadLocalContext() {}
	        });

	    final com.atlassian.httpclient.api.HttpClient httpClient = defaultHttpClientFactory.create(getClientOptions());

	    return new AtlassianHttpClientDecorator(httpClient, getAuthenticationHandler()) {
	        @Override
	        public void destroy() throws Exception {
	            defaultHttpClientFactory.dispose(httpClient);
	        }
	    };
	}
	
	BasicHttpAuthenticationHandler getAuthenticationHandler()
	{
		return new BasicHttpAuthenticationHandler(
	    		"NSangani-consultant@Scholastic.com",
	    		"Naveen123");
	}*/


	
	SearchResult getIssuesByVersion(String projectId, String versionId, int maxCount, int index) {
		String jql = "project in (" + projectId + ") AND fixVersion in (" + versionId + ")";

		LOG.info("query string ===>" + jql);
		LOG.info("maxCount ===>" + maxCount);
		LOG.info("index ===>" + index);

		Promise<SearchResult> src = client.getSearchClient().searchJql(jql, maxCount, index, null);
		return src.claim();
	}
	
	SearchResult getQACompleteByVersionAndSprint(String projectId, String versionId, String sprintId, int maxCount, int index) {
		String jql = "project in (" + projectId + ") AND fixVersion in (" + versionId + ") AND sprint in ("+ sprintId +") "
				+ "AND status in (\"QA Complete\")";

		LOG.info("query string ===>" + jql);
		LOG.info("maxCount ===>" + maxCount);
		LOG.info("index ===>" + index);

		Promise<SearchResult> src = client.getSearchClient().searchJql(jql, maxCount, index, null);
		return src.claim();
	}
	
	SearchResult getEnvironmentDefects(String projectId, String versionId, String environment,int maxCount, int index){
		String jql = "project in (" + projectId + ") AND fixVersion in (" + versionId + ") AND issuetype in (Defect) AND cf[14518] in ("+environment+")";
		Promise<SearchResult> src = client.getSearchClient().searchJql(jql, maxCount, index, null);
		return src.claim();
		
	}


	SearchResult getDefectInjects(String projectId, String sprintId, int maxCount, int index) {
		String jql = "project in (" + projectId + ") AND sprint in (" + sprintId + ") AND issuetype in (Defect)";
		Promise<SearchResult> src = client.getSearchClient().searchJql(jql, maxCount, index, null);
		return src.claim();
	}
	
	
	SearchResult getIssuesBySprint(String projectId, String sprintId, int maxCount, int index) {
		String jql = "project in (" + projectId + ") AND sprint=" + sprintId;

		LOG.info("query string ===>" + jql);
		LOG.info("maxCount ===>" + maxCount);
		LOG.info("index ===>" + index);

		Promise<SearchResult> src = client.getSearchClient().searchJql(jql, maxCount, index, null);
		return src.claim();
	}
	
	//project = "Literacy Pro Intl" AND fixVersion = 1.10 AND issuetype in (Story) AND sprint in (3879) AND updated >= 2017-01-25 AND status="QA Complete"
	
	SearchResult getMidSprintIssues(String projectId, String sprintId, String versionName, String startDate, String midDate, String doneStatus, int maxCount, int index) {
		String jql = "project in (" + projectId + ") AND sprint in (" + sprintId+ ") AND fixVersion = \""+ versionName +"\" AND issuetype in (story) AND updated >= "+ startDate +" AND updated <="
				+ midDate + " AND status in (\""+ doneStatus+"\")";

		LOG.info("query string ===>" + jql);
		LOG.info("maxCount ===>" + maxCount);
		LOG.info("index ===>" + index);

		Promise<SearchResult> src = client.getSearchClient().searchJql(jql, maxCount, index, null);
		return src.claim();
	}
	
	SearchResult getSprintVelocityCommitmentUpdated(String projectId, String sprintId, ArrayList<String> issueType, ArrayList<String> doneStatus
			, String startDate, int maxCount, int index) throws Exception
	{
		String issueTypes = "";
		if(issueType.size()==1)
		{
			issueTypes = issueType.get(0);
		}
		else
		{
			StringBuilder commaSepValueBuilder = new StringBuilder();
			for ( int i = 0; i< issueType.size(); i++)
			{
				commaSepValueBuilder.append(issueType.get(i));
				if ( i != issueType.size()-1)
				{
			        commaSepValueBuilder.append(", ");
			    }
			}
			issueTypes = commaSepValueBuilder.toString();
		}
		
		String doneStatusTypes = "";
		if(doneStatus.size()==1)
		{
			doneStatusTypes = doneStatus.get(0);
		}
		else
		{
			StringBuilder commaSepValueBuilder2 = new StringBuilder();
			for ( int i = 0; i< doneStatus.size(); i++)
			{
				commaSepValueBuilder2.append(doneStatus.get(i));
				if ( i != doneStatus.size()-1)
				{
			        commaSepValueBuilder2.append(", ");
			    }
			}
			doneStatusTypes = commaSepValueBuilder2.toString();
		}
		
		
		String jql = "project in (" + projectId + ") AND sprint in (" + sprintId+ ") AND issuetype in ("+issueTypes+") "
				+ "AND status was not in ("+doneStatusTypes+") ON ("+startDate+")";
		
		LOG.info("query string ===>" + jql);
		LOG.info("maxCount ===>" + maxCount);
		LOG.info("index ===>" + index);	
		

		Promise<SearchResult> src = client.getSearchClient().searchJql(jql, maxCount, index, null);
		return src.claim();
		
	}
	
	SearchResult getSprintVelocityAchievementUpdated(String projectId, String sprintId, ArrayList<String> issueType, ArrayList<String> doneStatus
			, String startDate, String endDate, int maxCount, int index)
	{
		String issueTypes = "";
		if(issueType.size()==1)
		{
			issueTypes = issueType.get(0);
		}
		else
		{
			StringBuilder commaSepValueBuilder = new StringBuilder();
			for ( int i = 0; i< issueType.size(); i++)
			{
				commaSepValueBuilder.append(issueType.get(i));
				if ( i != issueType.size()-1)
				{
			        commaSepValueBuilder.append(", ");
			    }
			}
			issueTypes = commaSepValueBuilder.toString();
		}
		
		String doneStatusTypes = "";
		if(doneStatus.size()==1)
		{
			doneStatusTypes = doneStatus.get(0);
		}
		else
		{
			StringBuilder commaSepValueBuilder2 = new StringBuilder();
			for ( int i = 0; i< doneStatus.size(); i++)
			{
				commaSepValueBuilder2.append(doneStatus.get(i));
				if ( i != doneStatus.size()-1)
				{
			        commaSepValueBuilder2.append(", ");
			    }
			}
			doneStatusTypes = commaSepValueBuilder2.toString();
		}
		
		
		String jql = "project in (" + projectId + ") AND sprint in (" + sprintId+ ") AND issuetype in ("+issueTypes+") "
				+ "AND status was not in ("+doneStatusTypes+") BEFORE ("+startDate+") AND status was in ("+doneStatusTypes+") DURING "
						+ "("+ startDate+","+endDate+")";
		
		LOG.info("query string ===>" + jql);
		LOG.info("maxCount ===>" + maxCount);
		LOG.info("index ===>" + index);

		Promise<SearchResult> src = client.getSearchClient().searchJql(jql, maxCount, index, null);
		return src.claim();
		
	}
	
	
	SearchResult getSprintStories(String projectId, String sprintId, int maxCount, int index) {
		String jql = "project in (" + projectId + ") AND sprint=" + sprintId+" AND issuetype in (story)";

		LOG.info("query string ===>" + jql);
		LOG.info("maxCount ===>" + maxCount);
		LOG.info("index ===>" + index);

		Promise<SearchResult> src = client.getSearchClient().searchJql(jql, maxCount, index, null);
		return src.claim();
	}
	

	private List<ProjectVersionIssues> getProjectVersionIssuesByVersion(JiraRepo jirarepo) {
		List<ProjectVersionIssues> projectversionissues = new ArrayList<>();
		try {

			boolean hasNextPage = true;
			int maxCount = 500;
			int index = 0;
			int fetchedCount = 0;

			while (hasNextPage) {
				
				SearchResult searchResult = getIssuesByVersion((String) jirarepo.getOptions().get("projectId"),
						jirarepo.getVERSIONID(), maxCount, index);
				

				Iterable<Issue> searchResultIssues = searchResult.getIssues();
				if (searchResultIssues != null) {
					// LOG.info("Not Null");
					int count = 0;
					for (Issue issue : searchResultIssues) {
						// Status{self=https://starbucks-mobile.atlassian.net/rest/api/2/status/11002,
						// name=Done, id=11002, description=,
						// iconUrl=https://starbucks-mobile.atlassian.net/}
						// LOG.info(issue.getId() + " - " +
						// issue.getStatus().getName());settings
						ProjectVersionIssues pvi = new ProjectVersionIssues();
						pvi.setIssueDescription(issue.getSummary());						
						pvi.setIssueId(issue.getId().toString());
						pvi.setKey(issue.getKey());
						//pvi.setIssueType(issue.getIssueType());

						String status = issue.getStatus().getName();
						pvi.setStatusName(status);

						String[] statuses = settings.getTodoStatuses();
						for (int j = 0; j < statuses.length; j++) {
							if (statuses[j].equals(status)) {
								pvi.setIssueStatus("Backlog");
								break;
							}
						}

						statuses = settings.getDoneStatuses();
						for (int j = 0; j < statuses.length; j++) {
							if (statuses[j].equals(status)) {
								pvi.setIssueStatus("Done");
								break;
							}
						}
						statuses = settings.getDoingStatuses();
						for (int j = 0; j < statuses.length; j++) {
							if (statuses[j].equals(status)) {
								pvi.setIssueStatus("In Progress");
								break;
							}
						}

						pvi.setProjectName((String) jirarepo.getOptions().get("projectName"));
						pvi.setVersionName((String) jirarepo.getOptions().get("versionName"));
						pvi.setVersionId((String) jirarepo.getOptions().get("versionId"));
						//pvi.setIssueType((String)issue.getIssueType().getName());	    
										
						pvi.setAcceptanceCriteria((String)issue.getField(settings.getAcceptanceCriteriaFieldName()).getValue());						
						
						projectversionissues.add(pvi);
						count++;
					}
					int totalResults = searchResult.getTotal();
					fetchedCount = fetchedCount + count;
					LOG.info("total count ==>" + totalResults);
					if (totalResults > fetchedCount) { // checking if all the
														// issues are fetched
						hasNextPage = true;
						index = fetchedCount;
					} else {
						hasNextPage = false;
					}

				} else {
					hasNextPage = false;
				}

			}

		} catch (Exception e) {
			LOG.error("Error in call: " + e.getMessage());
		}

		return projectversionissues;
	}

	private List<ProjectVersionIssues> getProjectVersionIssuesBySprint(JiraRepo jirarepo) {
		List<ProjectVersionIssues> projectversionissues = new ArrayList<>();
		try {

			boolean hasNextPage = true;
			int maxCount = 500;
			int index = 0;
			int fetchedCount = 0;

			if (jirarepo.getACTIVE_SPRINT_ID() == null) {
				return null;
			}

			while (hasNextPage) {
				SearchResult searchResult = getIssuesBySprint((String) jirarepo.getOptions().get("projectId"),
						jirarepo.getACTIVE_SPRINT_ID(), maxCount, index);

				Iterable<Issue> searchResultIssues = searchResult.getIssues();
				if (searchResultIssues != null) {
					// LOG.info("Not Null");
					int count = 0;
					for (Issue issue : searchResultIssues) {
						// Status{self=https://starbucks-mobile.atlassian.net/rest/api/2/status/11002,
						// name=Done, id=11002, description=,
						// iconUrl=https://starbucks-mobile.atlassian.net/}
						// LOG.info(issue.getId() + " - " +
						// issue.getStatus().getName());
						if (issue.getIssueType().getName().equals("Story")) {
							ProjectVersionIssues pvi = new ProjectVersionIssues();
							pvi.setSprintId(jirarepo.getACTIVE_SPRINT_ID());
							pvi.setIssueDescription(issue.getSummary());
							pvi.setIssueId(issue.getId().toString());
							pvi.setKey(issue.getKey());

							String status = issue.getStatus().getName();
							pvi.setStatusName(status);

							String[] statuses = settings.getTodoStatuses();
							for (int j = 0; j < statuses.length; j++) {
								if (statuses[j].equals(status)) {
									pvi.setIssueStatus("Backlog");
									break;
								}
							}

							statuses = settings.getDoneStatuses();
							for (int j = 0; j < statuses.length; j++) {
								if (statuses[j].equals(status)) {
									pvi.setIssueStatus("Done");
									break;
								}
							}
							statuses = settings.getDoingStatuses();
							for (int j = 0; j < statuses.length; j++) {
								if (statuses[j].equals(status)) {
									pvi.setIssueStatus("In Progress");
									break;
								}
							}

							pvi.setProjectName((String) jirarepo.getOptions().get("projectName"));
							pvi.setVersionName((String) jirarepo.getOptions().get("versionName"));
							LOG.info("custom field ==>" + settings.getJiraSprintDataFieldName());
							LOG.info("issue type name ==>" + issue.getIssueType().getName());
							try {

								IssueField issueField = issue.getField(settings.getJiraSprintDataFieldName());
								org.codehaus.jettison.json.JSONArray value = (org.codehaus.jettison.json.JSONArray) issueField
										.getValue();
								LOG.info("value===>" + value.toString());
								if (value != null) {
									int length = value.length();
									if (length > 0) {
										String grassHopperString = value.getString(length - 1);
										int indexOfOB = grassHopperString.indexOf('[');
										if (indexOfOB != -1) {
											grassHopperString = grassHopperString.substring(indexOfOB + 1,
													grassHopperString.length() - 1);

											String[] arr = grassHopperString.split(",");
											for (int i = 0; i < arr.length; i++) {
												if (arr[i].startsWith("id=")) {
													String[] sprintIdItems = arr[i].split("=");
													if (sprintIdItems.length == 2) {
														pvi.setSprintId(sprintIdItems[1]);
													}
												}
												if (arr[i].startsWith("name=")) {
													String[] sprintNameItems = arr[i].split("=");
													if (sprintNameItems.length == 2) {
														pvi.setSprintName(sprintNameItems[1]);
													}
												}
												if (arr[i].startsWith("rapidViewId=")) {
													String[] rapidViewIdItems = arr[i].split("=");
													if (rapidViewIdItems.length == 2) {														
														jirarepo.setRAPIDVIEWID(rapidViewIdItems[1]);
													}
												}
											}

										}

										LOG.info("grass hopper string ==>" + grassHopperString);
									}
								}
							} catch (Exception e) {
								LOG.error("Exception occured while extracting sprint data from issue : "
										+ e.getMessage());
							}

							// getting storypoint
							try {
								IssueField issueField = issue.getField(settings.getStoryPointDataFieldName());
								double storyPoint = (double) issueField.getValue();
								pvi.setStoryPoint(storyPoint);
							} catch (Exception e) {
								LOG.error("Exception occured while extracting story point from issue : "
										+ e.getMessage());
							}
							projectversionissues.add(pvi);
						}

						count++;
					}
					int totalResults = searchResult.getTotal();
					fetchedCount = fetchedCount + count;
					LOG.info("total count ==>" + totalResults);
					if (totalResults > fetchedCount) { // checking if all the
														// issues are fetched
						hasNextPage = true;
						index = fetchedCount;
					} else {
						hasNextPage = false;
					}

				} else {
					hasNextPage = false;
				}

			}

		} catch (Exception e) {
			LOG.error("Error in call: " + e.getMessage());
		}

		return projectversionissues;
	}

	@Override
	public List<ProjectVersionIssues> getprojectversionissues(JiraRepo jirarepo, boolean firstrun) {
		List<ProjectVersionIssues> projectversionissues = new ArrayList<>();
		List<ProjectVersionIssues> issuesV = getProjectVersionIssuesByVersion(jirarepo);
		List<ProjectVersionIssues> issuesS = getProjectVersionIssuesBySprint(jirarepo);
		if (issuesV != null)			
			projectversionissues.addAll(issuesV);
		if (issuesS != null)			
			projectversionissues.addAll(issuesS);

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
				// client.getSearchClient().searchJql()
				// Promise<SearchResult> promisedR1s =
				// client.getSearchClient().searchJql("test");
				// SearchResult searchResult = promisedR1s.claim();
				// searchResult.getIssues()
				Iterable<BasicProject> jiraRawRs = promisedRs.claim();
				if (jiraRawRs != null) {
					rt = Lists.newArrayList(jiraRawRs);
					int count;
					count = 0;
					for (BasicProject jiraProject : rt) {
						String projectName = TOOLS.sanitizeResponse(jiraProject.getName());
						String projectID = TOOLS.sanitizeResponse(jiraProject.getId());
						// Fetch Version
						// LOG.info(projectName);
						
						JSONArray versions = getProjectVersions(projectID);
						int versioncount = 0;
						if(projectName.equals("Literacy Pro Intl") || projectName.equals("LitPro Library"))
						{
						for (Object version : versions) {
							/*SearchResult searchResultQA = getEnvironmentDefects(projectID, str((JSONObject) version, "id"), "QA", 500,0);
							SearchResult searchResultProd = getEnvironmentDefects(projectID, str((JSONObject) version, "id"), "Production", 500,0);			*/				
							JiraRepo jr = new JiraRepo();
							jr.setPROJECTID(projectID);
							jr.setPROJECTNAME(projectName);
							jr.setVERSIONID(str((JSONObject) version, "id"));
							jr.setVERSIONDESCRIPTION(str((JSONObject) version, "description"));
							jr.setVERSIONNAME(str((JSONObject) version, "name"));
							/*jr.setStageDefects(searchResultQA.getTotal()+"");
							jr.setProdDefects(searchResultProd.getTotal()+"");*/
							
							projectVersions.add(jr);
							// LOG.info("Added:" + jr.getVERSIONNAME());
							count++;
							versioncount++;
						}
						// LOG.info("For " + projectName + " found " +
						// versioncount + " Versions.");
						// LOG.info(versions);
						// projectVersions.add(versions);
					}
					}
					LOG.info("Scanned " + count + " projects.");

				}

			} catch (com.atlassian.jira.rest.client.api.RestClientException e) {
				if (e.getStatusCode().get() != null && e.getStatusCode().get() == 401) {
					LOG.error(
							"Error 401 connecting to JIRA server, your credentials are probably wrong. Note: Ensure you are using JIRA user name not your email address.");
				} else {
					LOG.error(
							"No result was available from Jira unexpectedly - defaulting to blank response. The reason for this fault is the following:"
									+ e.getCause());
				}
				LOG.debug("Exception", e);
			}
		} else {
			LOG.warn("Jira client setup failed. No results obtained. Check your jira setup.");
		}

		return projectVersions;
	}

	@Override
	public Sprint getActiveSprint(JiraRepo jirarepo) {
		Sprint s = null;
		try {
			/*System.out.println(jiraRestClientSupplier.decodeCredentials(settings.getJiraCredentials()).get("username"));
			System.out.println(jiraRestClientSupplier.decodeCredentials(settings.getJiraCredentials()).get("password"));*/
			
			ResponseEntity<String> rapidViews = makeRestCall(buildUriRapidViews(),
					jiraRestClientSupplier.decodeCredentials(settings.getJiraCredentials()).get("username"),
					jiraRestClientSupplier.decodeCredentials(settings.getJiraCredentials()).get("password"));
			JSONObject respObjRap = (JSONObject) new JSONParser().parse(rapidViews.getBody());
			JSONArray rapidViewArray = (JSONArray) respObjRap.get("views");
			
			ResponseEntity<String> response = makeRestCall(buildUriSprint(jirarepo.getPROJECTID()),
					jiraRestClientSupplier.decodeCredentials(settings.getJiraCredentials()).get("username"),
					jiraRestClientSupplier.decodeCredentials(settings.getJiraCredentials()).get("password"));
			JSONObject respObj = (JSONObject) new JSONParser().parse(response.getBody());
			JSONArray sprintArray = (JSONArray) respObj.get("sprints");	
			
					
			
		    
			String rapidViewId = "";
			if (sprintArray != null) {
				
				if(rapidViewArray != null)
				{
					JSONObject obj = (JSONObject) sprintArray.get(sprintArray.size()-1);
					JSONArray proArray = (JSONArray) obj.get("projects");
					for(Object proj : proArray)
					{
						JSONObject temp = (JSONObject)proj;
						if (temp.get("name").equals(jirarepo.getPROJECTNAME()))
						{
							for(Object views : rapidViewArray)
							{
								JSONObject view = (JSONObject) views;
								if(view.get("name").equals(temp.get("key")))
								{
									rapidViewId = String.valueOf(view.get("id"));
									System.out.println("RapidBoard for"+ jirarepo.getPROJECTNAME()+" : "+rapidViewId);
								}
							}
						}
					}
				}
				
				for (int i = 0; i < sprintArray.size(); i++) {
					JSONObject obj = (JSONObject) sprintArray.get(i);
					boolean closed = (boolean) obj.get("closed");
					if (!closed) {
						s = new Sprint();
						s.setSprintId(((Long) obj.get("id")) + "");
						s.setSprintName((String) obj.get("name"));
						s.setStartTime((String) obj.get("start"));
						s.setEndTime((String) obj.get("end"));
						s.setActiveBoardId(rapidViewId);
						break;
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error occured while fetching sprint ==>" + e.getMessage());

		}

		return s;
	}

	private JSONArray getProjectVersions(String projectName) {
		JSONArray jsonParentObject = new JSONArray();
		try {

			ResponseEntity<String> response = makeRestCall(buildUriVerion(projectName),
					jiraRestClientSupplier.decodeCredentials(settings.getJiraCredentials()).get("username"),
					jiraRestClientSupplier.decodeCredentials(settings.getJiraCredentials()).get("password"));
			jsonParentObject = paresAsArray(response);

		} catch (URISyntaxException e) {
			LOG.error("Invalid uri: " + e.getMessage());
		} catch (RestClientException re) {
			LOG.error("Failed to obtain versions from " + projectName, re);
			return jsonParentObject;
		}
		return jsonParentObject;
	}
	
	private ClientHttpRequestFactory getClientHttpRequestFactory() {
	    int timeout = 60000;
	    HttpComponentsClientHttpRequestFactory clientHttpRequestFactory =
	      new HttpComponentsClientHttpRequestFactory();
	    clientHttpRequestFactory.setConnectTimeout(timeout);
	    return clientHttpRequestFactory;
	}

	private ResponseEntity<String> makeRestCall(String url, String userId, String password) {
		// Basic Auth only.
		if (!"".equals(userId) && !"".equals(password)) {
			/*RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
			restOperations.*/
			// LOG.info("Call with userid and password");
			RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
			return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(createHeaders(userId, password)),
					String.class);

		} else {
			return restOperations.exchange(url, HttpMethod.GET, null, String.class);
		}

	}

	// private ResponseEntity<String> makeRestCall1(String url, String userId,
	// String password,String projectId,String versionId) {
	// // Basic Auth only.
	// String params = "";
	// // String qu ="jql=project in (\"" + projectId + "\")"; // AND fixVersion
	// in ('" + versionId + "')";
	// String qu ="project in (\"" + projectId + "\") AND fixVersion in (\"" +
	// versionId + "\")";
	// try {
	// params = URLEncoder.encode(qu,"UTF-8") + "&fields=summary,status";
	// //params += "&fields=summary,status";
	// } catch (UnsupportedEncodingException e) {
	// e.printStackTrace();
	// }
	// if (!"".equals(userId) && !"".equals(password)) {
	// LOG.info("Call with userid and password");
	// LOG.info(url + "?jql={q}" + params);
	// return restOperations.exchange(url + "?jql=" + params, HttpMethod.GET,
	// new HttpEntity<>(createHeaders(userId, password)),
	// String.class,params);
	//
	// } else {
	// return restOperations.exchange(url, HttpMethod.GET, null,
	// String.class);
	// }

	// }

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

	// String buildUriVersionIssues(final String projectId, final String
	// versionId) throws URISyntaxException {
	String buildUriVersionIssues() throws URISyntaxException {
		// https://starbucks-mobile.atlassian.net/rest/api/2/search?jql=project=%2714500%27AND+fixVersion=%2718600%27&maxResults=1000&fields=summary,status
		// https://starbucks-mobile.atlassian.net/rest/api/2/search?jql=project+in+('14500')+AND+fixVersion+in+('18600')
		// String qu ="project in (\"" + projectId + "\")"; // AND fixVersion in
		// ('" + versionId + "')";
		// URIBuilder uriBuilder = new URIBuilder(settings.getJiraBaseUrl() +
		// "/" + settings.getApi() + "/search");
		// uriBuilder.setParameter("jql",qu);
		// uriBuilder.setUserInfo(jiraRestClientSupplier.decodeCredentials(settings.getJiraCredentials()).get("username"),jiraRestClientSupplier.decodeCredentials(settings.getJiraCredentials()).get("password"));
		// LOG.info(uriBuilder.build().toString());
		// return(uriBuilder.build());
		// try {
		// qu = URLEncoder.encode(qu,"UTF-8");
		// } catch (UnsupportedEncodingException e) {
		// LOG.error(e.getMessage());
		// }
		String url = settings.getJiraBaseUrl() + "/" + settings.getApi() + "/search"; // ?jql="
																						// +
																						// qu
																						// +
																						// "&maxResults=1000";
																						// //&fields=summary%2Cstatus

		LOG.info(url);
		return url;
	}

	String buildUriVerion(String projectname) throws URISyntaxException {
		// projectname = projectname.replaceAll(" ","%20");
		String url = settings.getJiraBaseUrl() + "/" + settings.getApi() + "/project/"
				+ projectname.replaceAll(" ", "%20") + "/versions" + "?maxResults=50&startAt=0";

		LOG.info(url);
		return url;
	}

	String buildUriSprint(String projectId) {
		// projectname = projectname.replaceAll(" ","%20");

		String url = settings.getJiraBaseUrl()
				+ "/rest/greenhopper/1.0/integration/teamcalendars/sprint/list?jql=project=" + projectId;

		LOG.info(url);
		return url;
	}
	
	
	String buildURICommitmentUpdated(String projectId, String sprintId, ArrayList<String> issueType, ArrayList<String> doneStatus
			, String startDate)
	{
		String issueTypes = "";
		if(issueType.size()==1)
		{
			issueTypes = issueType.get(0);
		}
		else
		{
			StringBuilder commaSepValueBuilder = new StringBuilder();
			for ( int i = 0; i< issueType.size(); i++)
			{
				commaSepValueBuilder.append(issueType.get(i));
				if ( i != issueType.size()-1)
				{
			        commaSepValueBuilder.append(", ");
			    }
			}
			issueTypes = commaSepValueBuilder.toString();
		}
		
		String doneStatusTypes = "";
		if(doneStatus.size()==1)
		{
			doneStatusTypes = doneStatus.get(0);
		}
		else
		{
			StringBuilder commaSepValueBuilder2 = new StringBuilder();
			for ( int i = 0; i< doneStatus.size(); i++)
			{
				commaSepValueBuilder2.append(doneStatus.get(i));
				if ( i != doneStatus.size()-1)
				{
			        commaSepValueBuilder2.append(", ");
			    }
			}
			doneStatusTypes = commaSepValueBuilder2.toString();
		}
		
		
		String jqlString = "project in (" + projectId + ") AND sprint in (" + sprintId+ ") AND issuetype in ("+issueTypes+") "
				+ "AND status was not in ("+doneStatusTypes+") ON ("+startDate+")";
		
		String url = settings.getJiraBaseUrl()+"/"+"rest/api/2/search?jql="+jqlString;
		
		return url;
		
	}
	
	
	private String buildURIAchievementUpdated(String projectId, String sprintId, ArrayList<String> issueType, ArrayList<String> doneStatus
			, String startDate, String endDate) {
		// TODO Auto-generated method stub
		String issueTypes = "";
		if(issueType.size()==1)
		{
			issueTypes = issueType.get(0);
		}
		else
		{
			StringBuilder commaSepValueBuilder = new StringBuilder();
			for ( int i = 0; i< issueType.size(); i++)
			{
				commaSepValueBuilder.append(issueType.get(i));
				if ( i != issueType.size()-1)
				{
			        commaSepValueBuilder.append(", ");
			    }
			}
			issueTypes = commaSepValueBuilder.toString();
		}
		
		String doneStatusTypes = "";
		if(doneStatus.size()==1)
		{
			doneStatusTypes = doneStatus.get(0);
		}
		else
		{
			StringBuilder commaSepValueBuilder2 = new StringBuilder();
			for ( int i = 0; i< doneStatus.size(); i++)
			{
				commaSepValueBuilder2.append(doneStatus.get(i));
				if ( i != doneStatus.size()-1)
				{
			        commaSepValueBuilder2.append(", ");
			    }
			}
			doneStatusTypes = commaSepValueBuilder2.toString();
		}
		
		String jqlString = "project in (" + projectId + ") AND sprint in (" + sprintId+ ") AND issuetype in ("+issueTypes+") "
				+ "AND status was not in ("+doneStatusTypes+") BEFORE ("+startDate+") AND status was in ("+doneStatusTypes+") DURING "
						+ "("+ startDate+","+endDate+")";
		
		String url = settings.getJiraBaseUrl()+"/"+"rest/api/2/search?jql="+jqlString;
		
		return url;
	}
	
	String buildURIMidSprintUpdated(String projectId, String sprintId, ArrayList<String> issueType, ArrayList<String> doneStatus
			, String startDate, String midDate)
	{
		String issueTypes = "";
		if(issueType.size()==1)
		{
			issueTypes = issueType.get(0);
		}
		else
		{
			StringBuilder commaSepValueBuilder = new StringBuilder();
			for ( int i = 0; i< issueType.size(); i++)
			{
				commaSepValueBuilder.append(issueType.get(i));
				if ( i != issueType.size()-1)
				{
			        commaSepValueBuilder.append(", ");
			    }
			}
			issueTypes = commaSepValueBuilder.toString();
		}
		
		String doneStatusTypes = "";
		if(doneStatus.size()==1)
		{
			doneStatusTypes = doneStatus.get(0);
		}
		else
		{
			StringBuilder commaSepValueBuilder2 = new StringBuilder();
			for ( int i = 0; i< doneStatus.size(); i++)
			{
				commaSepValueBuilder2.append(doneStatus.get(i));
				if ( i != doneStatus.size()-1)
				{
			        commaSepValueBuilder2.append(", ");
			    }
			}
			doneStatusTypes = commaSepValueBuilder2.toString();
		}
		
		
		String jqlString = "project in (" + projectId + ") AND sprint in (" + sprintId+ ") AND issuetype in ("+issueTypes+") "
				+ "AND status was not in ("+doneStatusTypes+") BEFORE ("+startDate+") AND status was in ("+doneStatusTypes+") DURING"
						+ "("+startDate+","+midDate +")";
		
		String url = settings.getJiraBaseUrl()+"/"+"rest/api/2/search?jql="+jqlString;
		System.out.println(url);
		return url;
		
	}
	
	
	
	String buildUriCustomVelocity(String rapidViewId, String sprintId)
	{
		//https://jira.sts.scholastic.com/rest/greenhopper/1.0/rapid/charts/sprintreport?rapidViewId=852&sprintId=3878
		String url = settings.getJiraBaseUrl()+"/rest/greenhopper/1.0/rapid/charts/sprintreport?rapidViewId="+rapidViewId+"&sprintId="+sprintId;
	    LOG.info(url);
	    return url;
	}
	
	
	
	
	String buildUriRapidViews() {
		// projectname = projectname.replaceAll(" ","%20");

		String url = settings.getJiraBaseUrl()
				+ "/rest/greenhopper/1.0/rapidview";

		LOG.info(url);
		return url;
	}	
	
	
	
	@Override
	public List<SprintVelocity> getVelocityReportByProject(JiraRepo jirarepo) {
		List<SprintVelocity> lstSprintVelocity=new ArrayList<SprintVelocity>();
		try{
			if (jirarepo.getRAPIDVIEWID() == null)
			{
				System.out.println("There is no rapidViewId for this project-" + jirarepo.getPROJECTNAME());
				return null;
			}
			String url = settings.getJiraBaseUrl()
				+ "/rest/greenhopper/1.0/rapid/charts/velocity.json?rapidViewId=" + jirarepo.getRAPIDVIEWID();
			ResponseEntity<String> response = makeRestCall(url,
					jiraRestClientSupplier.decodeCredentials(settings.getJiraCredentials()).get("username"),
					jiraRestClientSupplier.decodeCredentials(settings.getJiraCredentials()).get("password"));
			LOG.info(url);
			JSONObject respObj = (JSONObject) new JSONParser().parse(response.getBody());
			//LOG.info("Velocity json ===> " + response.getBody());
			JSONArray sprintArray = (JSONArray) respObj.get("sprints");

			
			List<String> sprints=new ArrayList<String>();
			//List<SprintVelocity> velocities=new ArrayList<SprintVelocity>();
			LinkedHashMap<String,SprintVelocity> mapVelocities=new LinkedHashMap<String,SprintVelocity>();
			if (sprintArray != null) {
				for (int i = 0; i < sprintArray.size(); i++) {
					JSONObject obj = (JSONObject) sprintArray.get(i);
					String sprintId=String.valueOf(obj.get("id"));					
					String name=(String) obj.get("name");	
					String state=(String) obj.get("state");	
					SprintVelocity v=new SprintVelocity();
					/*SearchResult result = getSprintStories(jirarepo.getPROJECTID(),sprintId,500,0);					
					v.setStoryCount(result.getTotal());*/
					v.setSprintId(sprintId);
					v.setSprintName(name);
					v.setSprintStatus(state);
					v.setProjectId(jirarepo.getPROJECTID());
					v.setVersionId(jirarepo.getVERSIONID());				
					
					mapVelocities.put(sprintId, v);
				}
			}
			JSONObject velocityEntriesObj = (JSONObject) respObj.get("velocityStatEntries");	
			/*for(int i=0; i<=sprintArray.size(); i++)
			{
				JSONObject velocityEntry = new JSONObject();
				velocityEntry = (JSONObject) velocityEntriesObj.get("sprintId");
				
			}*/
			if (velocityEntriesObj != null) {
				
				Iterator it = mapVelocities.entrySet().iterator();
			    while (it.hasNext()) {					
			        Map.Entry pair = (Map.Entry)it.next();
			        String sprintId=(String)pair.getKey();
			        SprintVelocity v=(SprintVelocity)pair.getValue();
			        JSONObject velocityObj = (JSONObject) velocityEntriesObj.get(sprintId);
			        JSONObject estimatedObj=(JSONObject)velocityObj.get("estimated");
			        String estimated=(String)estimatedObj.get("text");		        
			        
			        ResponseEntity<String> miscResponse = makeRestCall(buildUriCustomVelocity(jirarepo.getRAPIDVIEWID(),sprintId),
							jiraRestClientSupplier.decodeCredentials(settings.getJiraCredentials()).get("username"),
							jiraRestClientSupplier.decodeCredentials(settings.getJiraCredentials()).get("password"));
					JSONObject miscRespObj = (JSONObject) new JSONParser().parse(miscResponse.getBody());
					
					JSONObject sprintContents = (JSONObject) miscRespObj.get("contents");
			        
			        /*JSONObject completedObj=(JSONObject)velocityObj.get("completed");
			        String completed=(String)completedObj.get("text");
			        
			        
			       // SearchResult resQAComplete = getQACompleteByVersionAndSprint(jirarepo.getPROJECTID(), jirarepo.getVERSIONNAME(), sprintId, 500, 0);
			        
			        v.setCompleted(completed);
			        v.setCommitted(estimated);*/
			        
			        JSONObject completedIssuesEstimateSumObj = (JSONObject) sprintContents.get("completedIssuesEstimateSum");
			        v.setCompletedSum((String)completedIssuesEstimateSumObj.get("text"));
			        JSONObject issuesNotCompletedEstimateSumObj = (JSONObject) sprintContents.get("issuesNotCompletedEstimateSum");
					v.setNotCompletedSum((String)issuesNotCompletedEstimateSumObj.get("text"));
					JSONObject allIssuesEstimateSumObj = (JSONObject) sprintContents.get("allIssuesEstimateSum");
					v.setAllIssuesSum((String)allIssuesEstimateSumObj.get("text"));
					JSONObject issuesCompletedInAnotherSprintEstimateSumObj = (JSONObject) sprintContents.get("issuesCompletedInAnotherSprintEstimateSum");
					v.setOutOfSprintSum((String) issuesCompletedInAnotherSprintEstimateSumObj.get("text"));
					JSONObject puntedIssuesEstimateSumObj = (JSONObject) sprintContents.get("puntedIssuesEstimateSum");
					v.setPuntedSum((String) puntedIssuesEstimateSumObj.get("text"));
					
					JSONObject sprintInfo = (JSONObject) miscRespObj.get("sprint");
					String startDate = (String) sprintInfo.get("startDate");
					System.out.println(startDate);
					// 28/Dec/16 11:40 AM
					SimpleDateFormat inBoundFormatter = new SimpleDateFormat("dd/MMM/yy HH:mm a");
					SimpleDateFormat outBoundFormatter = new SimpleDateFormat("yyyy-MM-dd");
					
					
					v.setStartDate(outBoundFormatter.format(inBoundFormatter.parse( (String) sprintInfo.get("startDate"))));
					v.setEndDate(outBoundFormatter.format(inBoundFormatter.parse( (String) sprintInfo.get("endDate"))));
					
					Date midDate = new Date((inBoundFormatter.parse((String) sprintInfo.get("startDate")).getTime()+inBoundFormatter.parse((String) sprintInfo.get("endDate")).getTime())/2);					
					v.setMidSprintDate(outBoundFormatter.format(midDate));
					
					/*SearchResult midSprintIssues = getMidSprintIssues(jirarepo.getPROJECTID(),sprintId , jirarepo.getVERSIONNAME(), v.getStartDate(), v.getMidSprintDate(), "QA Complete", 500, 0);
					double midPointSum = 0;
					if(midSprintIssues.getTotal()>0)
					{
						ArrayList<Issue> issuesItr= (ArrayList<Issue>) midSprintIssues.getIssues();
					    for(Issue issue: issuesItr)
						{							
					    	midPointSum = midPointSum + (double) issue.getFieldByName("Story Points").getValue();							
						}						
					}
					
					v.setMidPointSum(midPointSum);*/
					
					
					ArrayList<String> issueTypes = new ArrayList<String>();
					issueTypes.add("story");
					
					ArrayList<String> statusTypes = new ArrayList<String>();
					statusTypes.add("\"QA Complete\"");
					statusTypes.add("Done");
					statusTypes.add("Closed");
					
					JSONArray issueArray = null;
					LOG.info("Fetching Commitment");
				    ResponseEntity<String> commitResponse = makeRestCall(buildURICommitmentUpdated(jirarepo.getPROJECTID(),
						    sprintId,issueTypes, statusTypes, v.getStartDate()),
							jiraRestClientSupplier.decodeCredentials(settings.getJiraCredentials()).get("username"),
							jiraRestClientSupplier.decodeCredentials(settings.getJiraCredentials()).get("password"));
					//SearchResult sprintIssues = (SearchResult) commitResponse;
					JSONObject miscCommitObj = (JSONObject) new JSONParser().parse(commitResponse.getBody());
					issueArray = (JSONArray) miscCommitObj.get("issues");
					Double commitPointSum = 0.0;
					int acceptance = 0;
					for(int i=0; i < issueArray.size(); i++)
					{
					JSONObject issue = (JSONObject) issueArray.get(i);
					JSONObject fields = (JSONObject) issue.get("fields");
					Double commitPoints = (Double) fields.get(settings.getStoryPointDataFieldName());
					commitPointSum = commitPointSum+commitPoints;
					if(fields.get(settings.getAcceptanceCriteriaFieldName())!=null)
					{
						acceptance++;
					}
					}
					v.setAcceptanceCriteria(acceptance);
					v.setCommitted(commitPointSum+"");
					v.setStoryCount(issueArray.size());
					
					LOG.info("Fetching Achievement");
					ResponseEntity<String> achieveResponse = makeRestCall(buildURIAchievementUpdated(jirarepo.getPROJECTID(),
						    sprintId,issueTypes, statusTypes, v.getStartDate(), v.getEndDate()),
							jiraRestClientSupplier.decodeCredentials(settings.getJiraCredentials()).get("username"),
							jiraRestClientSupplier.decodeCredentials(settings.getJiraCredentials()).get("password"));
					//SearchResult sprintIssues = (SearchResult) commitResponse;
					JSONObject miscAchieveObj = (JSONObject) new JSONParser().parse(achieveResponse.getBody());
					issueArray = (JSONArray) miscAchieveObj.get("issues");
					Double achievePointSum = 0.0;
					for(int i=0; i < issueArray.size(); i++)
					{
					JSONObject issue = (JSONObject) issueArray.get(i);
					JSONObject fields = (JSONObject) issue.get("fields");
					Double achievePoints = (Double) fields.get(settings.getStoryPointDataFieldName());
					achievePointSum = achievePointSum+achievePoints;					
					}
					
					v.setCompleted(achievePointSum+"");
					
					LOG.info("Fetching MidSprint Achievement");
					ResponseEntity<String> misSprintResponse = makeRestCall(buildURIMidSprintUpdated(jirarepo.getPROJECTID(),
						    sprintId,issueTypes, statusTypes, v.getStartDate(), v.getMidSprintDate()),
							jiraRestClientSupplier.decodeCredentials(settings.getJiraCredentials()).get("username"),
							jiraRestClientSupplier.decodeCredentials(settings.getJiraCredentials()).get("password"));
					//SearchResult sprintIssues = (SearchResult) commitResponse;
					JSONObject miscMidSprintObj = (JSONObject) new JSONParser().parse(misSprintResponse.getBody());
					issueArray = (JSONArray) miscMidSprintObj.get("issues");
					Double midPointSum = 0.0;
					for(int i=0; i < issueArray.size(); i++)
					{
					JSONObject issue = (JSONObject) issueArray.get(i);
					JSONObject fields = (JSONObject) issue.get("fields");
					Double midPoints = (Double) fields.get(settings.getStoryPointDataFieldName());
					midPointSum = midPointSum+midPoints;					
					}
					
					v.setMidPointSum(midPointSum);
					
					
					
					
					/*SearchResult commitResponse = getSprintVelocityCommitmentUpdated(jirarepo.getPROJECTID(),
						    sprintId,issueTypes, statusTypes, v.getStartDate(),500,0);*/
					/*double totalCommited = 0;
					if(commitResponse.getTotal()>0)
					{
						ArrayList<Issue> issuesItr= (ArrayList<Issue>) commitResponse.getIssues();
					    for(Issue issue: issuesItr)
						{							
					    	totalCommited = totalCommited + (double) issue.getFieldByName("Story Points").getValue();							
						}						
					}
					v.setCommitted(totalCommited+"");
					
					SearchResult achievementResponse = getSprintVelocityAchievementUpdated(jirarepo.getPROJECTID(),
						    sprintId,issueTypes, statusTypes, v.getStartDate(),v.getEndDate(),500,0);
					double totalAchieved = 0;
					if(achievementResponse.getTotal()>0)
					{
						ArrayList<Issue> issuesItr= (ArrayList<Issue>) achievementResponse.getIssues();
					    for(Issue issue: issuesItr)
						{							
					    	totalAchieved = totalAchieved + (double) issue.getFieldByName("Story Points").getValue();							
						}						
					}
					v.setCompleted(totalAchieved+"");*/
					
					
					
					
					
			        lstSprintVelocity.add(v);
			    }
			}			
		}catch(Exception ex){
			LOG.info("Error on fetching velocity");
			ex.printStackTrace();
			return null;
		}
		return lstSprintVelocity;
	}

	

	@Override
	public JiraRepo getDefectSlippage(JiraRepo repo) {
		
		String[] minors = settings.getPreviousMinorVersions();
		String[] preQAs = settings.getPreQA();
		String[] postQAs = settings.getPostQA();
		String preQAString = "";
		if(preQAs.length==1)
		{
			preQAString = preQAs[0];
		}
		else
		{
			StringBuilder commaSepValueBuilder = new StringBuilder();
			for ( int i = 0; i< preQAs.length; i++)
			{
				commaSepValueBuilder.append(preQAs[0]);
				if ( i != preQAs.length-1)
				{
			        commaSepValueBuilder.append(", ");
			    }
			}
			preQAString = commaSepValueBuilder.toString();
		}
		
		String postQAString = "";
		if(postQAs.length==1)
		{
			postQAString = postQAs[0];
		}
		else
		{
			StringBuilder commaSepValueBuilder = new StringBuilder();
			for ( int i = 0; i< postQAs.length; i++)
			{
				commaSepValueBuilder.append(postQAs[0]);
				if ( i != postQAs.length-1)
				{
			        commaSepValueBuilder.append(", ");
			    }
			}
			postQAString = commaSepValueBuilder.toString();
		}
		
		int preQADefCount = 0;
		int postQADefCount = 0;
		
		SearchResult searchResultPreQA = getEnvironmentDefects(repo.getPROJECTID(),settings.getPreviousMajorVersion(), preQAString, 500,0);
		preQADefCount = preQADefCount + searchResultPreQA.getTotal();
		SearchResult searchResultPostQA = getEnvironmentDefects(repo.getPROJECTID(),settings.getPreviousMajorVersion(), postQAString, 500,0);
		postQADefCount = postQADefCount + searchResultPostQA.getTotal();
		
		for(int i=0; i < minors.length; i++)
		{
			searchResultPreQA = getEnvironmentDefects(repo.getPROJECTID(), minors[i], preQAString, 500,0);
			preQADefCount = preQADefCount + searchResultPreQA.getTotal();
			searchResultPostQA = getEnvironmentDefects(repo.getPROJECTID(),minors[i], postQAString, 500,0);
			postQADefCount = postQADefCount + searchResultPostQA.getTotal();
		}
		
		repo.setPreQADefects(preQADefCount+"");
		repo.setPostQADefects(postQADefCount+"");
		
		if((postQADefCount+postQADefCount)!=0)
		{
			double slippage = (postQADefCount/(preQADefCount+postQADefCount))*100;
			repo.setDefectSlippage(slippage+"");
		}
		else
		{
			repo.setDefectSlippage(0.0+"");
		}
		
		
	    /*repo.setStageDefects(searchResultPreQA.getTotal()+"");
		repo.setProdDefects(searchResultProd.getTotal()+"");*/
		
		return repo;
		
	}

	@Override
	public List<DefectInjection> getDefectInjections(List<SprintVelocity> sprintVelocities) {
		List<DefectInjection> diList = new ArrayList<DefectInjection>();
		
		for(SprintVelocity velocity : sprintVelocities)
		{
		DefectInjection dInject = new DefectInjection();
		SearchResult searchResultDI = getDefectInjects(velocity.getProjectId(),velocity.getSprintId(), 500,0);
		dInject.setSprintId(velocity.getSprintId());
		dInject.setSprintName(velocity.getSprintName());
		dInject.setDefectCount(searchResultDI.getTotal());
		dInject.setProjectId(velocity.getProjectId());
		dInject.setVersionId(velocity.getVersionId());
		/*SearchResult searchResultSP = getStoryPoints(velocity.getProjectId(),velocity.getSprintId(), 500,0);*/
		dInject.setAchievedPoints(Double.parseDouble(velocity.getCompleted()));
		diList.add(dInject);
		}
		
		return diList;
	}
}






















