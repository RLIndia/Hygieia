package com.capitalone.dashboard.collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import com.capitalone.dashboard.model.*;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import com.capitalone.dashboard.model.OctopusEnvironmentCollector;

import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.EnvironmentComponentRepository;
import com.capitalone.dashboard.repository.EnvironmentStatusRepository;


//This reference needs to be removed.

import com.capitalone.dashboard.repository.OctopusEnvironmentRepository;

import com.capitalone.dashboard.repository.OctopusCollectorRepository;

import com.capitalone.dashboard.repository.EnvironmentComponentsAllRepository;


@Component
public class OctopusCollectorTask extends CollectorTask<OctopusEnvironmentCollector>{
    private static final Logger LOGGER = LoggerFactory.getLogger(OctopusCollectorTask.class);
    private final OctopusCollectorRepository octopusCollectorRepository;
    private final OctopusSettings octopusSettings;

    private int contextOctopusIndex = 0;


    private final OctopusEnvironmentRepository octopusEnvironmentRepository;
    private final OctopusClient octopusClient;

//    private final EnvironmentComponentRepository envComponentRepository;
//    private final EnvironmentStatusRepository environmentStatusRepository;

    private final ComponentRepository dbComponentRepository;

//    private final EnvironmentComponentsAllRepository environmentComponentsAllRepository;

    private int contextOserver;


    @Autowired
    public OctopusCollectorTask(TaskScheduler taskScheduler,OctopusCollectorRepository octopusCollectorRepository
            ,OctopusSettings octopusSettings,
                                OctopusEnvironmentRepository octopusEnvironmentRepository,
                                EnvironmentComponentRepository envComponentRepository,
                                EnvironmentStatusRepository environmentStatusRepository,
                                OctopusClient octopusClient,
                                ComponentRepository dbComponentRepository, EnvironmentComponentsAllRepository environmentComponentsAllRepository) {
        super(taskScheduler, "OctopusEnvironment");

        this.octopusCollectorRepository = octopusCollectorRepository;
        this.octopusSettings = octopusSettings;


        this.octopusEnvironmentRepository = octopusEnvironmentRepository;

//        this.envComponentRepository = envComponentRepository;
//        this.environmentStatusRepository = environmentStatusRepository;
//
//        this.environmentComponentsAllRepository = environmentComponentsAllRepository;

        this.octopusClient = octopusClient;

        this.dbComponentRepository = dbComponentRepository;
        this.contextOserver = 0;

    }

    @Override
    public OctopusEnvironmentCollector getCollector() {
        return OctopusEnvironmentCollector.prototype();
    }

    @Override
    public BaseCollectorRepository<OctopusEnvironmentCollector> getCollectorRepository() {
        return octopusCollectorRepository;
    }

    @Override
    public String getCron() {
        return octopusSettings.getCron();
    }

    @Override
    public void collect(OctopusEnvironmentCollector collector) {



        long start = System.currentTimeMillis();

        clean(collector);

        String[] os = octopusSettings.getUrl();
        LOGGER.info("Only errors would be displayed..wait for finshed message");
        /*
        Design: Get list of env's, Get enabled envs, fetch apps for enabled envs, get deployment status, release version and date for apps.
         */



        for(int co = 0; co < os.length; co++) {

            this.contextOserver=co;
            octopusClient.setContext(co);
            LOGGER.info("Octopus Server " + (octopusClient.getContext() + 1) + " " + octopusSettings.getUrl()[this.contextOserver]);

            //Getting all envs
//            List<OctopusEnvironment> envs = octopusClient.getEnvironments();
//
//            for(OctopusEnvironment env : envs){
//                LOGGER.info("Environemnt: " + env.getEnvName() + " [" + env.getEnvId() + "]" );
//
//            }

            addNewEnvironments(octopusClient.getEnvironments(),collector);

//            addNewApplications(octopusClient.getApplications(),
//                    collector);
//
//            List<OctopusApplication> applications = enabledApplications(collector, octopusSettings.getUrl()[this.contextOserver]);
//            LOGGER.info("Enabled Applications ==>" + applications.size());
//            updateData(applications);
//
//            List<OctopusApplication> allApplications = allApplications(collector, octopusSettings.getUrl()[this.contextOserver]);
//            LOGGER.info("------------------------------------------");
//            LOGGER.info("All Applications ==>" + allApplications.size());
//            saveAllComponents(allApplications, collector);
        }
        log("Finished", start);
        LOGGER.info("Finished -------------------------------------");

    }


    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
    private void clean(OctopusEnvironmentCollector collector) {
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
        List<OctopusEnvironment> envList = new ArrayList<>();
        Set<ObjectId> udId = new HashSet< >();
        udId.add(collector.getId());
        for (OctopusEnvironment env : octopusEnvironmentRepository.findByCollectorIdIn(udId)) {
            if (env != null) {
                env.setEnabled(uniqueIDs.contains(env.getId()));
                envList.add(env);
            }
        }
        octopusEnvironmentRepository.save(envList);
    }

    private void deleteUnwantedJobs(OctopusEnvironmentCollector collector) {

        List<OctopusEnvironment> deleteEnvList = new ArrayList<>();
        Set<ObjectId> udId = new HashSet<>();
        udId.add(collector.getId());
        //		for (OctopusApplication app : octopusApplicationRepository.findByCollectorIdIn(udId)) {
        //			//            if (!collector.getUdeployServers().contains(app.getInstanceUrl()) ||
        //			//                    (!app.getCollectorId().equals(collector.getId()))) {
        //			//                deleteAppList.add(app);
        //			//            }
        //		}

        octopusEnvironmentRepository.delete(deleteEnvList);

    }

    private void addNewEnvironments(List<OctopusEnvironment> environments,OctopusEnvironmentCollector collector){
        long start = System.currentTimeMillis();
        int count = 0;

        for(OctopusEnvironment env : environments){
            if(isNewEnvironment(collector,env)){
                //Add this environment to the list
                env.setCollectorId(collector.getId());
                env.setEnabled(false);
                try{
                    octopusEnvironmentRepository.save(env);
                    LOGGER.info("Added New Env " +  env.getEnvName());
                }catch (org.springframework.dao.DuplicateKeyException ce){

                }
                //TO Do: Logic to clean up older environments, That is not used.
            }else{
                LOGGER.info("Skipped Adding Env, already in. " + env.getEnvName());
            }
        }
    }

    private boolean isNewEnvironment(OctopusEnvironmentCollector collector,
                                     OctopusEnvironment environment) {
        return octopusEnvironmentRepository.findOctopusEnvironment(
                collector.getId(),
                environment.getEnvId(),environment.getEnvName()) == null;
    }



    private List<EnvironmentComponent> getEnvironmentComponent(List<ApplicationDeploymentHistoryItem> dataList) {
        List<EnvironmentComponent> returnList = new ArrayList<>();
        for (ApplicationDeploymentHistoryItem data : dataList) {
            EnvironmentComponent component = new EnvironmentComponent();

            component.setComponentName(data.getApplicationName());
            component.setComponentID(data.getApplicationId());
            component.setCollectorItemId(data.getCollectorItemId());

            component.setEnvironmentID(data.getEnvironmentId());


            //component.setEnvironmentUrl(octopusSettings.getUrl()+"/app#/environments/"+data.getEnvironmentId());
            component.setEnvironmentUrl(octopusSettings.getUrl()[this.contextOserver]+data.getDeployedWebUrl());
            //ex. http://testoctopus2.starbucks.net/app#/deployments/deployments-45583
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
                    status.setHostname(machine.getHostName());
                    returnList.add(status);
                    machineIds.add(machine.getMachineId());
                }
            }
        }
        return returnList;
    }


    private boolean isLatestDeploy(List<EnvironmentComponentsAll> compListAll,EnvironmentComponent ec){
        boolean canAdd = true;
        for(EnvironmentComponentsAll eca : compListAll){
            //    LOGGER.info("Checking for existing env: " + eca.getEnvironmentID() + " " + ec.getEnvironmentID());
            //    LOGGER.info("Checking for existing comp: " + eca.getComponentID() + " " + ec.getComponentID());
            if(eca.getEnvironmentID().compareTo(ec.getEnvironmentID()) == 0 && eca.getComponentID().compareTo(ec.getComponentID()) == 0){
                //        LOGGER.info("Checking for time new - existing : " + ec.getDeployTime() + " " + eca.getDeployTime() + " " + (ec.getDeployTime() - eca.getDeployTime()));
                if(ec.getDeployTime() < eca.getDeployTime()){
                    //compListAll.remove(eca);
                    canAdd = false;
                    //           LOGGER.info("Removing : " + eca.getComponentID() + " " + eca.getEnvironmentID());
                }
            }
        }
        return canAdd;
    }





}
