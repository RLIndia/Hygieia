package com.capitalone.dashboard.collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.model.ApplicationDeploymentHistoryItem;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;

import com.capitalone.dashboard.model.EnvironmentComponent;
import com.capitalone.dashboard.model.EnvironmentStatus;
import com.capitalone.dashboard.model.Machine;
import com.capitalone.dashboard.model.OctopusApplication;
import com.capitalone.dashboard.model.OctopusCollector;

import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.EnvironmentComponentRepository;
import com.capitalone.dashboard.repository.EnvironmentStatusRepository;



import com.capitalone.dashboard.repository.OctopusApplicationRepository;
import com.capitalone.dashboard.repository.OctopusCollectorRepository;




@Component
public class OctopusCollectorTask extends CollectorTask<OctopusCollector>{
	private static final Logger LOGGER = LoggerFactory.getLogger(OctopusCollectorTask.class);
	private final OctopusCollectorRepository octopusCollectorRepository;
	private final OctopusSettings octopusSettings;

	private final OctopusApplicationRepository octopusApplicationRepository;
	private final OctopusClient octopusClient;

	private final EnvironmentComponentRepository envComponentRepository;
	private final EnvironmentStatusRepository environmentStatusRepository;

	private final ComponentRepository dbComponentRepository;


	@Autowired
	public OctopusCollectorTask(TaskScheduler taskScheduler,OctopusCollectorRepository octopusCollectorRepository
			,OctopusSettings octopusSettings,
			OctopusApplicationRepository octopusApplicationRepository,
			EnvironmentComponentRepository envComponentRepository,
			EnvironmentStatusRepository environmentStatusRepository,
			OctopusClient octopusClient,
			ComponentRepository dbComponentRepository) {
		super(taskScheduler, "Octopus");

		this.octopusCollectorRepository = octopusCollectorRepository;
		this.octopusSettings = octopusSettings;
		this.octopusApplicationRepository = octopusApplicationRepository;

		this.envComponentRepository = envComponentRepository;
		this.environmentStatusRepository = environmentStatusRepository;
		this.octopusClient = octopusClient;

		this.dbComponentRepository = dbComponentRepository;


	}

	@Override
	public OctopusCollector getCollector() {
		return OctopusCollector.prototype();
	}

	@Override
	public BaseCollectorRepository<OctopusCollector> getCollectorRepository() {
		return octopusCollectorRepository;
	}

	@Override
	public String getCron() {
		return octopusSettings.getCron();
	}

	@Override
	public void collect(OctopusCollector collector) {

		

		long start = System.currentTimeMillis();

		clean(collector);

		addNewApplications(octopusClient.getApplications(),
				collector);
		
		List<OctopusApplication> applications = enabledApplications(collector, octopusSettings.getUrl());
		LOGGER.info("Enabled Applications ==>"+applications.size());
		updateData(applications);

		log("Finished", start);

	}

	/**
	 * Clean up unused deployment collector items
	 *
	 * @param collector the {@link UDeployCollector}
	 */
	@SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
	private void clean(OctopusCollector collector) {
		deleteUnwantedJobs(collector);
		Set<ObjectId> uniqueIDs = new HashSet<>();
		for (com.capitalone.dashboard.model.Component comp : dbComponentRepository
				.findAll()) {
			if (comp.getCollectorItems() == null || comp.getCollectorItems().isEmpty()) continue;
			List<CollectorItem> itemList = comp.getCollectorItems().get(
					CollectorType.Deployment);
			if (itemList == null) continue;
			for (CollectorItem ci : itemList) {
				if (ci == null) continue;
				uniqueIDs.add(ci.getId());
			}
		}
		List<OctopusApplication> appList = new ArrayList<>();
		Set<ObjectId> udId = new HashSet< >();
		udId.add(collector.getId());
		for (OctopusApplication app : octopusApplicationRepository.findByCollectorIdIn(udId)) {
			if (app != null) {
				app.setEnabled(uniqueIDs.contains(app.getId()));
				appList.add(app);
			}
		}
		octopusApplicationRepository.save(appList);
	}

	private void deleteUnwantedJobs(OctopusCollector collector) {

		List<OctopusApplication> deleteAppList = new ArrayList<>();
		Set<ObjectId> udId = new HashSet<>();
		udId.add(collector.getId());
		//		for (OctopusApplication app : octopusApplicationRepository.findByCollectorIdIn(udId)) {
		//			//            if (!collector.getUdeployServers().contains(app.getInstanceUrl()) ||
		//			//                    (!app.getCollectorId().equals(collector.getId()))) {
		//			//                deleteAppList.add(app);
		//			//            }
		//		}

		octopusApplicationRepository.delete(deleteAppList);

	}

	private void addNewApplications(List<OctopusApplication> applications,
			OctopusCollector collector) {
		long start = System.currentTimeMillis();
		int count = 0;

		log("All apps", start, applications.size());
		for (OctopusApplication application : applications) {

			if (isNewApplication(collector, application)) {
				application.setCollectorId(collector.getId());
				application.setEnabled(false);
				application.setDescription(application.getApplicationName());
				try {
					octopusApplicationRepository.save(application);
				} catch (org.springframework.dao.DuplicateKeyException ce) {
					log("Duplicates items not allowed", 0);

				}
				count++;
			}

		}
		log("New apps", start, count);
	}

	private boolean isNewApplication(OctopusCollector collector,
			OctopusApplication application) {
		return octopusApplicationRepository.findUDeployApplication(
				collector.getId(), octopusSettings.getUrl(),
				application.getApplicationId()) == null;
	}

	private List<OctopusApplication> enabledApplications(
			OctopusCollector collector, String instanceUrl) {
		return octopusApplicationRepository.findEnabledApplications(
				collector.getId(), instanceUrl);
	}
	
    private List<EnvironmentComponent> getEnvironmentComponent(List<ApplicationDeploymentHistoryItem> dataList) {
        List<EnvironmentComponent> returnList = new ArrayList<>();
        for (ApplicationDeploymentHistoryItem data : dataList) {
            EnvironmentComponent component = new EnvironmentComponent();
            
            component.setComponentName(data.getApplicationName());
            component.setComponentID(data.getApplicationId());
            component.setCollectorItemId(data.getCollectorItemId());
            
            component.setEnvironmentID(data.getEnvironmentId());
            
            
            component.setEnvironmentUrl(octopusSettings.getUrl()+"app#/environments/"+data.getEnvironmentId());
            
            
            component.setComponentVersion(data.getVersion());
            
            
            
            component.setDeployed(data.isDeployed());
           
            component.setEnvironmentName(data
                    .getEnvironmentName());
            component.setDeployTime(data.getAsOfDate());
            
            
            component.setAsOfDate(data.getAsOfDate());
            
            returnList.add(component);
        }
        return returnList;
    }
    
    private List<EnvironmentStatus> getEnvironmentStatus(List<ApplicationDeploymentHistoryItem> dataList) {
        List<EnvironmentStatus> returnList = new ArrayList<>();
        Map<String,List<String>> tempStorage = new HashMap<String,List<String>>();
        
        
        
        for (ApplicationDeploymentHistoryItem data : dataList) {
        	
        	List<String> machineIds = null;
        	
        	if(!tempStorage.containsKey(data.getApplicationId())) {
        		machineIds = new ArrayList<>();
        		tempStorage.put(data.getApplicationId(), machineIds);
        	} else {
        		machineIds = tempStorage.get(data.getApplicationId());
        	}
            
        	
        	
        	for(Machine machine:data.getMachines()) {
        		
        		if(!machineIds.contains(machine.getMachineId())) {
        			EnvironmentStatus status = new EnvironmentStatus();
                    status.setCollectorItemId(data.getCollectorItemId());
                    status.setComponentID(data.getApplicationId());
                    status.setComponentName(data.getApplicationName());
                    status.setEnvironmentName(data.getEnvironmentName());
                    status.setOnline(machine.isStatus()); // for testing
                    status.setResourceName(machine.getMachineName()); // for testing
                    returnList.add(status);
                    machineIds.add(machine.getMachineId());
        		}
            	
        	
        	}
        }
        return returnList;
    }


	private void updateData(List<OctopusApplication> octopusApplications) {
		for (OctopusApplication application : octopusApplications) {
			List<EnvironmentComponent> compList = new ArrayList<>();
			List<EnvironmentStatus> statusList = new ArrayList<>();
			long startApp = System.currentTimeMillis();

			List<ApplicationDeploymentHistoryItem> applicationDeploymentHistoryItems = octopusClient.getApplicationDeploymentHistory(application);
		    
			LOGGER.info("history ==>"+applicationDeploymentHistoryItems.size()); 
			
			compList.addAll(getEnvironmentComponent(applicationDeploymentHistoryItems));
			
			statusList.addAll(getEnvironmentStatus(applicationDeploymentHistoryItems));
			
			
			
			LOGGER.info("compList ==>"+compList.size()); 
			
			LOGGER.info("statusList ==>"+statusList.size()); 
			
			if (!compList.isEmpty()) {
				List<EnvironmentComponent> existingComponents = envComponentRepository
						.findByCollectorItemId(application.getId());
				envComponentRepository.delete(existingComponents);
				envComponentRepository.save(compList);
			}
			if (!statusList.isEmpty()) {
				List<EnvironmentStatus> existingStatuses = environmentStatusRepository
						.findByCollectorItemId(application.getId());
				environmentStatusRepository.delete(existingStatuses);
				environmentStatusRepository.save(statusList);
			}

			log(" " + application.getApplicationName(), startApp);
		}
	}



}
