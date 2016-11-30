package com.capitalone.dashboard.collector;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.model.BuildTestResult;
import com.capitalone.dashboard.model.HudsonJob;
import com.capitalone.dashboard.model.SCM;
import com.capitalone.dashboard.util.Supplier;


/**
 * HudsonClient implementation that uses RestTemplate and JSONSimple to
 * fetch information from Hudson instances.
 */
@Component
public class DefaultHudsonClient implements HudsonClient {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultHudsonClient.class);

	private final RestOperations rest;
	private final HudsonSettings settings;

	private static final String JOBS_URL_SUFFIX = "/api/json?tree=jobs[name,url,builds[number,url]]";

	private static final String[] CHANGE_SET_ITEMS_TREE = new String[]{
			"user",
			"author[fullName]",
			"revision",
			"id",
			"msg",
			"timestamp",
			"date",
			"paths[file]"
	};

	private static final String[] BUILD_DETAILS_TREE = new String[]{
			"number",
			"url",
			"timestamp",
			"duration",
			"building",
			"result",
			"culprits[fullName]",
			"changeSet[items[" + StringUtils.join(CHANGE_SET_ITEMS_TREE, ",") + "]",
			"revisions[module,revision]]"
	};

	private static final String BUILD_DETAILS_URL_SUFFIX = "/api/json?tree=" + StringUtils.join(BUILD_DETAILS_TREE, ",");

	@Autowired
	public DefaultHudsonClient(Supplier<RestOperations> restOperationsSupplier, HudsonSettings settings) {
		this.rest = restOperationsSupplier.get();
		this.settings = settings;
	}

	@Override
	public Map<HudsonJob, Set<Build>> getInstanceJobs(String instanceUrl) {
		Map<HudsonJob, Set<Build>> result = new LinkedHashMap<>();
		try {
			String url = joinURL(instanceUrl, JOBS_URL_SUFFIX);
			ResponseEntity<String> responseEntity = makeRestCall(url);
			String returnJSON = responseEntity.getBody();
			JSONParser parser = new JSONParser();

			try {
				JSONObject object = (JSONObject) parser.parse(returnJSON);

				for (Object job : getJsonArray(object, "jobs")) {
					JSONObject jsonJob = (JSONObject) job;

					final String jobName = getString(jsonJob, "name");
					final String jobURL = getString(jsonJob, "url");
					LOG.debug("Job:" + jobName);
					LOG.debug("jobURL: " + jobURL);
					HudsonJob hudsonJob = new HudsonJob();
					hudsonJob.setInstanceUrl(instanceUrl);
					hudsonJob.setJobName(jobName);
					hudsonJob.setJobUrl(jobURL);

					Set<Build> builds = new LinkedHashSet<>();
					for (Object build : getJsonArray(jsonJob, "builds")) {
						JSONObject jsonBuild = (JSONObject) build;

						// A basic Build object. This will be fleshed out later if this is a new Build.
						String dockerLocalHostIP = settings.getDockerLocalHostIP();
						String buildNumber = jsonBuild.get("number").toString();
						if (!"0".equals(buildNumber)) {
							Build hudsonBuild = new Build();
							hudsonBuild.setNumber(buildNumber);
							String buildURL = getString(jsonBuild, "url");

							//Modify localhost if Docker Natting is being done
							if (!dockerLocalHostIP.isEmpty()) {
								buildURL = buildURL.replace("localhost", dockerLocalHostIP);
								LOG.debug("Adding build & Updated URL to map LocalHost for Docker: " + buildURL);
							} else {
								LOG.debug(" Adding Build: " + buildURL);
							}

							hudsonBuild.setBuildUrl(buildURL);
							builds.add(hudsonBuild);
						}
					}
					// add the builds to the job
					result.put(hudsonJob, builds);
				}
			} catch (ParseException e) {
				LOG.error("Parsing jobs on instance: " + instanceUrl, e);
			}
		} catch (RestClientException rce) {
			LOG.error("client exception loading jobs", rce);
			throw rce;
		} catch (MalformedURLException mfe) {
			LOG.error("malformed url for loading jobs", mfe);
		}
		return result;
	}

	@Override
	public Build getBuildDetails(String buildUrl, String instanceUrl) {
		LOG.info("instance url ==>"+instanceUrl);
		try {
			String newUrl = rebuildJobUrl(buildUrl, instanceUrl);
			String url = joinURL(newUrl, BUILD_DETAILS_URL_SUFFIX);
			ResponseEntity<String> result = makeRestCall(url);
			String resultJSON = result.getBody();
			if (StringUtils.isEmpty(resultJSON)) {
				LOG.error("Error getting build details for. URL=" + url);
				return null;
			}
			JSONParser parser = new JSONParser();
			try {
				JSONObject buildJson = (JSONObject) parser.parse(resultJSON);
				Boolean building = (Boolean) buildJson.get("building");
				// Ignore jobs that are building
				if (!building) {
					Build build = new Build();
					build.setNumber(buildJson.get("number").toString());
					build.setBuildUrl(buildUrl);
					build.setTimestamp(System.currentTimeMillis());
					build.setStartTime((Long) buildJson.get("timestamp"));
					build.setDuration((Long) buildJson.get("duration"));
					build.setEndTime(build.getStartTime() + build.getDuration());
					build.setBuildStatus(getBuildStatus(buildJson));
					build.setStartedBy(firstCulprit(buildJson));
					if (settings.isSaveLog()) {
						build.setLog(getLog(buildUrl));
					}
					addChangeSets(build, buildJson);

					// getting the link to trx file 
					BuildTestResult testResult = null;
					try {
						//testResult =  getBuildTestResult(buildUrl);
						LOG.info("getting test results");
						testResult =  getBuildTestResultCatalyst(buildUrl,instanceUrl);

					} catch (Exception e) {
						testResult = null;
						LOG.error("Exception Occured while trying to fetch test result",e.getMessage());
					}
					if(testResult!=null) {
						build.setBuildTestResult(testResult);
					}

					return build;
				}

			} catch (ParseException e) {
				LOG.error("Parsing build: " + buildUrl, e);
			}
		} catch (RestClientException rce) {
			LOG.error("Client exception loading build details: " + rce.getMessage() + ". URL =" + buildUrl );
		} catch (MalformedURLException mfe) {
			LOG.error("Malformed url for loading build details" + mfe.getMessage() + ". URL =" + buildUrl );
		} catch (URISyntaxException use) {
			LOG.error("Uri syntax exception for loading build details"+ use.getMessage() + ". URL =" + buildUrl );
		} catch (RuntimeException re) {
			LOG.error("Unknown error in getting build details. URL="+ buildUrl, re);
		} catch (UnsupportedEncodingException unse) {
			LOG.error("Unsupported Encoding Exception in getting build details. URL=" + buildUrl, unse);
		}
		return null;
	}

	private BuildTestResult getBuildTestResultCatalyst(String buildUrl,String instanceUrl) throws MalformedURLException, ParseException{
		BuildTestResult buildTestResult = null;
		JSONParser parser = new JSONParser();
		String buildJSONUrl = buildUrl+"/api/json";
		ResponseEntity<String> jsonResp = makeRestCall(buildJSONUrl);
		JSONObject obj = (JSONObject)parser.parse(jsonResp.getBody());
		// getting sub builds of main job
		JSONArray subBuilds = (JSONArray)obj.get("subBuilds");
		if(subBuilds != null && subBuilds.size() >0) {
			JSONObject subBuild = (JSONObject) subBuilds.get(0);
			String jobName = (String)subBuild.get("jobName");
			long buildNumber = (long)subBuild.get("buildNumber");
			String subBuildUrl = instanceUrl+"/job/"+jobName+"/"+buildNumber+"/api/json";
			int totalCases = 0;
			int passedCount = 0;
			int failedcount = 0;
			try{
				LOG.info("sub build url ===>"+subBuildUrl);
				ResponseEntity<String> jsonRespSubBuild = makeRestCall(subBuildUrl);
				JSONObject objSubBuild = (JSONObject)parser.parse(jsonRespSubBuild.getBody());

				// getting sub builds for sub build
				JSONArray subBuildsInner = (JSONArray)objSubBuild.get("subBuilds");
				if(subBuildsInner != null && subBuildsInner.size() >0) {
					for(Object o:subBuildsInner) {
						JSONObject jsonObj = (JSONObject)o;
						String subJobName = (String)jsonObj.get("jobName");
						long subBuildNumber = (long)jsonObj.get("buildNumber");
						String url = instanceUrl+"/job/"+subJobName+"/"+subBuildNumber+"/cucumber-html-reports/catalyst/target/reports/json/CucumberCatalystReport.json";
						LOG.info("report url ==>"+url);
						ResponseEntity<String> reportEntity = makeRestCall(url);
						JSONArray reportJsonArray = (JSONArray)parser.parse(reportEntity.getBody());
						if(reportJsonArray != null && reportJsonArray.size()>0) {
							for(Object testObj : reportJsonArray) {
								JSONObject jObj = (JSONObject)testObj;
								JSONArray elements = (JSONArray)jObj.get("elements");
								if(elements !=null) {
									totalCases = totalCases + elements.size();
									for(Object casesObj : elements){
										JSONObject caseJsonObj = (JSONObject)casesObj;
										JSONArray steps = (JSONArray)caseJsonObj.get("steps");
										String result = "passed";
										for(Object stepObj : steps){
											JSONObject stepJsonObj = (JSONObject)stepObj;
											String status = (String)((JSONObject)stepJsonObj.get("result")).get("status");
											if(status.equals("failed")) {
												result = "failed";
												break;
											}
										}
										if(result.equals("failed")) {
											failedcount++;
										} else {
											passedCount++;
										}

									}

								}
							}
						}
					}
				}
				
			} catch(Exception e) {
				e.printStackTrace();
			}
			if(totalCases>0) {
				buildTestResult = new BuildTestResult();
				buildTestResult.setFailed(failedcount+"");
				buildTestResult.setTotal(totalCases+"");
				buildTestResult.setExecuted(totalCases+"");
				buildTestResult.setError(0+"");
				buildTestResult.setPassed(passedCount+"");
			}




		}

		return buildTestResult;
	}

	private BuildTestResult getBuildTestResult(String buildUrl) throws MalformedURLException {

		String artifactHTMLUrl = buildUrl+"deployedArtifacts/";
		ResponseEntity<String> htmlResp = makeRestCall(artifactHTMLUrl);

		String htmlString = htmlResp.getBody();

		Document doc = Jsoup.parse(htmlString);

		Elements trs = doc.select(".fileList tr");
		for (Element tr : trs) {
			String trHTML = tr.html();
			if(trHTML.indexOf(".trx")!= -1) {
				Elements anchorTags = tr.getElementsByTag("a");
				LOG.info("anchor tag size "+anchorTags.size());
				if(anchorTags.size() > 0) {
					Element anchor = anchorTags.get(0);
					String href = anchor.attr("href");
					LOG.info(href);

					// got trx file download link
					// getting trx file
					String trxUrl = artifactHTMLUrl + href;

					ResponseEntity<String> trxResp = makeRestCall(trxUrl);
					String txrString = trxResp.getBody();
					// parsing trx file

					Document trxDoc = Jsoup.parse(txrString);
					Elements counterTags = trxDoc.select("ResultSummary Counters");
					LOG.info("CounterTags size "+counterTags.size());
					if(counterTags.size() > 0) {
						Element counter = counterTags.get(0);
						BuildTestResult testResult = new BuildTestResult();
						testResult.setTotal(counter.attr("total"));
						testResult.setPassed(counter.attr("passed"));
						testResult.setFailed(counter.attr("failed"));
						testResult.setExecuted(counter.attr("executed"));
						testResult.setError(counter.attr("error"));
						testResult.setResultUrl(trxUrl);
						return testResult;
					}
				}

			}
		}

		return null;

	}


	//This method will rebuild the API endpoint because the buildUrl obtained via Jenkins API
	//does not save the auth user info and we need to add it back.
	public static String rebuildJobUrl(String build, String server) throws URISyntaxException, MalformedURLException, UnsupportedEncodingException {
		URL instanceUrl = new URL(server);
		String userInfo = instanceUrl.getUserInfo();
		String instanceProtocol = instanceUrl.getProtocol();

		//decode to handle spaces in the job name.
		URL buildUrl = new URL(URLDecoder.decode(build, "UTF-8"));
		String buildPath = buildUrl.getPath();

		String host = buildUrl.getHost();
		int port = buildUrl.getPort();
		URI newUri = new URI(instanceProtocol, userInfo, host, port, buildPath, null, null);
		return newUri.toString();
	}


	/**
	 * Grabs changeset information for the given build.
	 *
	 * @param build     a Build
	 * @param buildJson the build JSON object
	 */
	private void addChangeSets(Build build, JSONObject buildJson) {
		JSONObject changeSet = (JSONObject) buildJson.get("changeSet");

		Map<String, String> revisionToUrl = new HashMap<>();

		// Build a map of revision to module (scm url). This is not always
		// provided by the Hudson API, but we can use it if available.
		for (Object revision : getJsonArray(changeSet, "revisions")) {
			JSONObject json = (JSONObject) revision;
			revisionToUrl.put(json.get("revision").toString(), getString(json, "module"));
		}

		for (Object item : getJsonArray(changeSet, "items")) {
			JSONObject jsonItem = (JSONObject) item;
			SCM scm = new SCM();
			scm.setScmAuthor(getCommitAuthor(jsonItem));
			scm.setScmCommitLog(getString(jsonItem, "msg"));
			scm.setScmCommitTimestamp(getCommitTimestamp(jsonItem));
			scm.setScmRevisionNumber(getRevision(jsonItem));
			scm.setScmUrl(revisionToUrl.get(scm.getScmRevisionNumber()));
			scm.setNumberOfChanges(getJsonArray(jsonItem, "paths").size());

			build.getSourceChangeSet().add(scm);
		}
	}

	////// Helpers

	private long getCommitTimestamp(JSONObject jsonItem) {
		if (jsonItem.get("timestamp") != null) {
			return (Long) jsonItem.get("timestamp");
		} else if (jsonItem.get("date") != null) {
			String dateString = (String) jsonItem.get("date");
			try {
				return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(dateString).getTime();
			} catch (java.text.ParseException e) {
				// Try an alternate date format...looks like this one is used by Git
				try {
					return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse(dateString).getTime();
				} catch (java.text.ParseException e1) {
					LOG.error("Invalid date string: " + dateString, e);
				}
			}
		}
		return 0;
	}

	private String getString(JSONObject json, String key) {
		return (String) json.get(key);
	}

	private String getRevision(JSONObject jsonItem) {
		// Use revision if provided, otherwise use id
		Long revision = (Long) jsonItem.get("revision");
		return revision == null ? getString(jsonItem, "id") : revision.toString();
	}

	private JSONArray getJsonArray(JSONObject json, String key) {
		Object array = json.get(key);
		return array == null ? new JSONArray() : (JSONArray) array;
	}

	private String firstCulprit(JSONObject buildJson) {
		JSONArray culprits = getJsonArray(buildJson, "culprits");
		if (CollectionUtils.isEmpty(culprits)) {
			return null;
		}
		JSONObject culprit = (JSONObject) culprits.get(0);
		return getFullName(culprit);
	}

	private String getFullName(JSONObject author) {
		return getString(author, "fullName");
	}

	private String getCommitAuthor(JSONObject jsonItem) {
		// Use user if provided, otherwise use author.fullName
		JSONObject author = (JSONObject) jsonItem.get("author");
		return author == null ? getString(jsonItem, "user") : getFullName(author);
	}

	private BuildStatus getBuildStatus(JSONObject buildJson) {
		String status = buildJson.get("result").toString();
		switch (status) {
		case "SUCCESS":
			return BuildStatus.Success;
		case "UNSTABLE":
			return BuildStatus.Unstable;
		case "FAILURE":
			return BuildStatus.Failure;
		case "ABORTED":
			return BuildStatus.Aborted;
		default:
			return BuildStatus.Unknown;
		}
	}

	protected ResponseEntity<String> makeRestCall(String sUrl) throws MalformedURLException {
		URI thisuri = URI.create(sUrl);
		String userInfo = thisuri.getUserInfo();

		//get userinfo from URI or settings (in spring properties)
		if (StringUtils.isEmpty(userInfo) && (this.settings.getUsername() != null) && (this.settings.getApiKey() != null)) {
			userInfo = this.settings.getUsername() + ":" + this.settings.getApiKey();
		}
		// Basic Auth only.
		if (StringUtils.isNotEmpty(userInfo)) {
			return rest.exchange(thisuri, HttpMethod.GET,
					new HttpEntity<>(createHeaders(userInfo)),
					String.class);
		} else {
			return rest.exchange(thisuri, HttpMethod.GET, null,
					String.class);
		}

	}

	protected HttpHeaders createHeaders(final String userInfo) {
		byte[] encodedAuth = Base64.encodeBase64(
				userInfo.getBytes(StandardCharsets.US_ASCII));
		String authHeader = "Basic " + new String(encodedAuth);

		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.AUTHORIZATION, authHeader);
		return headers;
	}

	protected String getLog(String buildUrl) {
		try {
			return makeRestCall(joinURL(buildUrl, "consoleText")).getBody();
		} catch (MalformedURLException mfe) {
			LOG.error("malformed url for build log", mfe);
		}

		return "";
	}

	// join a base url to another path or paths - this will handle trailing or non-trailing /'s
	public static String joinURL(String base, String... paths) throws MalformedURLException {
		StringBuilder result = new StringBuilder(base);
		for (String path : paths) {
			String p = path.replaceFirst("^(\\/)+", "");
			if (result.lastIndexOf("/") != result.length() - 1) {
				result.append('/');
			}
			result.append(p);
		}
		return result.toString();
	}
}
