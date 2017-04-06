package com.capitalone.dashboard.collector;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

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

import com.capitalone.dashboard.model.ApplicationDeploymentHistoryItem;
import com.capitalone.dashboard.model.Environment;
import com.capitalone.dashboard.model.Machine;
import com.capitalone.dashboard.model.OctopusApplication;
import com.capitalone.dashboard.model.Release;
import com.capitalone.dashboard.model.Task;
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
	public List<OctopusApplication> getApplications() {
		List<OctopusApplication> applications = new ArrayList<>();
		boolean hasNext = true;
		String urlPath = "/api/projects";
		while(hasNext) {

			JSONObject resJsonObject =  paresResponse(makeRestCall(octopusSettings.getUrl()[contextOS],urlPath,octopusSettings.getApiKey()[contextOS]));

			JSONArray jsonArray = (JSONArray)resJsonObject.get("Items");

			for (Object item :jsonArray) {
				JSONObject jsonObject = (JSONObject) item;
				OctopusApplication application = new OctopusApplication();
				application.setInstanceUrl(octopusSettings.getUrl()[contextOS]);
				application.setApplicationName(str(jsonObject, "Name"));
				application.setApplicationId(str(jsonObject, "Id"));
				applications.add(application);
			}
			JSONObject links = (JSONObject)resJsonObject.get("Links");
			urlPath = (String)links.get("Page.Next");

			if(urlPath == null || urlPath.isEmpty()) {
				hasNext = false;
			}
		}

		return applications;
	}


	@Override
	public List<Environment> getEnvironments() {
		List<Environment> environments = new ArrayList<>();
		boolean hasNext = true;
		String urlPath = "/api/environments";
		while(hasNext) {
			JSONObject resJsonObject =  paresResponse(makeRestCall(octopusSettings.getUrl()[contextOS],
					urlPath,octopusSettings.getApiKey()[contextOS]));
			JSONArray jsonArray = (JSONArray)resJsonObject.get("Items");
			for (Object item : jsonArray) {
				JSONObject jsonObject = (JSONObject) item;
				environments.add(new Environment(str(jsonObject, "Id"), str(
						jsonObject, "Name")));
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
	public List<ApplicationDeploymentHistoryItem> getApplicationDeploymentHistory(OctopusApplication application) {
		return getApplicationDeploymentHistory(application,"");
	}

	@Override
	public List<ApplicationDeploymentHistoryItem> getApplicationDeploymentHistory(OctopusApplication application,String environments) {
		List<String> envs = new ArrayList<String>(Arrays.asList(environments.toLowerCase().split(",")));
		List<ApplicationDeploymentHistoryItem> applicationDeployments = new ArrayList<>();

		//setting envs to empty if argument is empty. Is is a fix due to the overloaded function
		if(environments.isEmpty())
			envs = new ArrayList<>();

		boolean hasNext = true;
		String urlPath = "/api/deployments?projects="+application.getApplicationId();
		while(hasNext) {

			JSONObject resJsonObject =  paresResponse(makeRestCall(octopusSettings.getUrl()[contextOS],
					urlPath,octopusSettings.getApiKey()[contextOS]));

			JSONArray jsonArray = (JSONArray)resJsonObject.get("Items");

			//	LOGGER.info("applicationID ==>"+application.getApplicationId());
			//	LOGGER.info("Deployment History size ==>"+jsonArray.size());
			//	LOGGER.info("environments ==>" + envs.toString());

			for (Object item :jsonArray) {
				JSONObject jsonObject = (JSONObject) item;
				//LOGGER.info("Project Id ==>"+str(jsonObject, "ProjectId"));

				ApplicationDeploymentHistoryItem historyItem = new ApplicationDeploymentHistoryItem();
				historyItem.setApplicationId(application.getApplicationId());
				historyItem.setApplicationName(application.getApplicationName());
				historyItem.setEnvironmentId(str(jsonObject, "EnvironmentId"));
				historyItem.setDeploymentId(str(jsonObject, "Id"));
				JSONObject links = (JSONObject)jsonObject.get("Links");

				historyItem.setDeployedWebUrl((String)links.get("Web"));
				historyItem.setCollectorItemId(application.getId());

				Environment env = getEnvironmentById(historyItem.getEnvironmentId());
				historyItem.setEnvironmentName(env.getName());

				//Skip saving if not in the list of environments

				//			LOGGER.info("Envs : " + envs.isEmpty() + " Size " + envs.size() + " contains " + env.getName().toLowerCase() + " " + envs.contains(env.getName().toLowerCase()));
				if(envs.size() > 0 && envs.contains(env.getName().toLowerCase()) == false){
					//LOGGER.info("Skipping saving history item..No env match found " + env.getName());
					continue;
				}

				Release rel = getReleaseById(str(jsonObject, "ReleaseId"));

				historyItem.setVersion(rel.getVersion());

				//LOGGER.info("Env Match found proceeding");


				//historyItem.setAsOfDate(System.currentTimeMillis());// for testing

				String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
				DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
				DateTime dateTime = dtf.parseDateTime(str(jsonObject, "Created"));

				historyItem.setAsOfDate(dateTime.getMillis());

				Task task = getTaskById(str(jsonObject, "TaskId"));
				//LOGGER.info("Task ==>"+ jsonObject.toString());
				historyItem.setDeployed(task.isState());


				// getting list of machines
				JSONArray specificMachineIds = (JSONArray) jsonObject.get("SpecificMachineIds");
				if(specificMachineIds.size() == 0) {
					String deploymentProcessId =  (String)jsonObject.get("DeploymentProcessId");
					if(deploymentProcessId == null || deploymentProcessId.isEmpty()) {
						// version 2 of octopus.. trying to get id from release
						deploymentProcessId = rel.getDeploymentProcessSnapShotId();
					}
					Set<String> roleSet = getRolesFromDeploymentProcess(deploymentProcessId);
					List<Machine> machines;
					try {

						machines = getMachinesByEnvId(historyItem.getEnvironmentId(),roleSet);
						//					LOGGER.info("Machines by env ID: " + historyItem.getEnvironmentId() + " roleset:" + roleSet.toString());
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						//					LOGGER.error(e.getMessage());
						machines = new ArrayList<>();
					}
					historyItem.setMachines(machines);
				} else {
					List<Machine> machines = new ArrayList<Machine>();
					for (Object obj :specificMachineIds) {
						String machineId = (String)obj;
						Machine m =null;
						try {
							m = getMachineById(machineId, historyItem.getEnvironmentId());
							//						LOGGER.info("Machines by ID : " + machineId +  " env id:" + historyItem.getEnvironmentId());
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							//						LOGGER.error(e.getMessage());
						}
						machines.add(m);
					}
					historyItem.setMachines(machines);
				}
				applicationDeployments.add(historyItem);
				//		LOGGER.info("."); //including a running indicator
			}

			JSONObject links = (JSONObject)resJsonObject.get("Links");
			urlPath = (String)links.get("Page.Next");

			if(urlPath == null || urlPath.isEmpty()) {
				hasNext = false;
			}

		}

		return applicationDeployments;
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

	private Environment getEnvironmentById(String envId){

		JSONObject resJsonObject =  paresResponse(makeRestCall(octopusSettings.getUrl()[contextOS],
				"/api/environments/"+envId,octopusSettings.getApiKey()[contextOS]));
		Environment env = new Environment(envId, (String)resJsonObject.get("Name"));

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