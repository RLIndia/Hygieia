package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.CatalystTaskRepository;
import com.capitalone.dashboard.repository.CatalystDeployRepository;
import com.capitalone.dashboard.repository.CatalystDeployTaskHistory;

import com.capitalone.dashboard.repository.ComponentRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by root on 19/9/16.
 */
@Component
public class CatalystCollectorTask extends CollectorTask<Collector> {
    private static final Log LOG = LogFactory.getLog(CatalystCollectorTask.class);

    private final BaseCollectorRepository<Collector> collectorRepository;
    private final CatalystTaskRepository catalystTaskRepository;
    private  final CatalystClient catalystClient;
    private final CatalystSettings catalystSettings;
    private final ComponentRepository dbComponentRepository;
    private final CatalystDeployRepository catalystDeployRepository;
    private final CatalystDeployTaskHistory catalystDeployTaskHistory;

//    private String orgId;
//    private String bgId;
//    private String projectId;


    @Autowired
    public  CatalystCollectorTask(TaskScheduler taskScheduler,
                                  BaseCollectorRepository<Collector> collectorRepository,
                                  CatalystTaskRepository catalystTaskRepository,
                                  CatalystClient catalystClient,
                                  CatalystSettings catalystSettings,
                                  CatalystDeployRepository catalystDeployRepository,
                                  CatalystDeployTaskHistory catalystDeployTaskHistory,
                                  ComponentRepository dbComponentRepository){
        super(taskScheduler, "Catalystdeploy");
        LOG.info("Reached here");
        this.collectorRepository = collectorRepository;
        this.catalystTaskRepository = catalystTaskRepository;
        this.catalystClient = catalystClient;
        this.catalystSettings = catalystSettings;
        this.catalystDeployRepository = catalystDeployRepository;
        this.dbComponentRepository = dbComponentRepository;
        this.catalystDeployTaskHistory = catalystDeployTaskHistory;


    }

    @Override
    public Collector getCollector() {
        Collector protoType = new Collector();
        protoType.setName("Catalystdeploy");
        protoType.setCollectorType(CollectorType.Catalystdeploy);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        return protoType;
    }

    @Override
    public BaseCollectorRepository<Collector> getCollectorRepository() {
        return collectorRepository;
    }


    @Override
    public String getCron() {
        return catalystSettings.getCron();
    }


    private void clean(Collector collector) {
        Set<ObjectId> uniqueIDs = new HashSet<ObjectId>();
        /**
         * Logic: For each component, retrieve the collector item list of the type SCM.
         * Store their IDs in a unique set ONLY if their collector IDs match with Bitbucket collectors ID.
         */
        for (com.capitalone.dashboard.model.Component comp : dbComponentRepository.findAll()) {
            if (comp.getCollectorItems() == null || comp.getCollectorItems().isEmpty()) continue;
            List<CollectorItem> itemList = comp.getCollectorItems().get(CollectorType.Catalystdeploy);
            if (itemList == null) continue;
            for (CollectorItem ci : itemList) {
                if (ci != null && ci.getCollectorId().equals(collector.getId())) {
                    uniqueIDs.add(ci.getId());
                }
            }
        }

        /**
         * Logic: Get all the collector items from the collector_item collection for this collector.
         * If their id is in the unique set (above), keep them enabled; else, disable them.
         */
        List<CatalystRepo> repoList = new ArrayList<CatalystRepo>();
        List<CatalystRepo> deleterepoList = new ArrayList<CatalystRepo>();
        Set<ObjectId> collectorIds = new HashSet<ObjectId>();
        collectorIds.add(collector.getId());
        for (CatalystRepo repo : catalystTaskRepository.findByCollectorIdIn(collectorIds)) {
            if (repo != null) {
                repo.setEnabled(uniqueIDs.contains(repo.getId()));
                repoList.add(repo);
                LOG.info(" Found:" + repo.getREPOSITORYNAME() + ' ' + uniqueIDs.contains(repo.getId()));
                if(!uniqueIDs.contains(repo.getId())) {
                    deleterepoList.add(repo);
                    LOG.info("Added to delete list:" + repo.getId());
                }
            }
        }
        //To Do write a clean up for deactivated repos. CollectorId is being sotored unused
        catalystTaskRepository.save(repoList);
        catalystTaskRepository.delete(deleterepoList);
    }

    @Override
    public void collect(Collector collector) {
        logBanner("Catalyst Project Collector..starting");
        long start = System.currentTimeMillis();
        clean(collector);
        List<CatalystRepo> fetchedRepos = catalystClient.getCatalystRepos();
        List<CatalystRepo> repoList = new ArrayList<CatalystRepo>();
        int newRepos = 0;
        int scannedRepos = 0;
        for(CatalystRepo repo : fetchedRepos){
            CatalystRepo savedRepo = catalystTaskRepository.findCatalystRepo(collector.getId(),repo.getREPOSITORYNAME());
            if(savedRepo == null){
                repo.setCollectorId(collector.getId());
                repo.setEnabled(false);
                try {
                    catalystTaskRepository.save(repo);
                    newRepos++;
                }catch(Exception e){
                    LOG.info(e);
                }
            }
            scannedRepos++;
        }
        LOG.info("Scanned Repositories:" + scannedRepos);
        LOG.info("New / Updated Repositories:" + newRepos);
        //Get active repos
        int enabledDeploys = 0;
        int newDeploys = 0;
        int updatedDeploys = 0;
        for(CatalystRepo repo : enabledRepos(collector)) {
            boolean firstRun = false;
            List<CatalystDeploys> enabledCatalystDeploys = catalystClient.getCatalystDeploys(repo,firstRun);
            for(CatalystDeploys cd : enabledCatalystDeploys){
                CatalystDeploys savedDeploy = catalystDeployRepository.findByCollectorItemIdAndRepository(collector.getId(),cd.getRepository());
                if(savedDeploy != null){
                    savedDeploy.setLastTaskStatus(cd.getLastTaskStatus());
                    savedDeploy.setExecutedDate(cd.getExecutedDate());
                    catalystDeployRepository.save(savedDeploy);
                    updatedDeploys++;
                }
                else{
                    cd.setCollectorItemId(collector.getId());
                    catalystDeployRepository.save(cd);
                    newDeploys++;

                }
                //Get the task history and Save
                //List<CatalystDeployTaskHistory> enabledCatalystTaskHistory = catalystClient.
                List<CatalystDeploysTask> catalystDeploysTasks = catalystClient.getCatalystDeploysTasks(cd.getTaskId().toString());
                //Get all saved history items
                List<CatalystDeploysTask> savedTask = catalystDeployTaskHistory.findByTaskId(cd.getTaskId().toString());
                //Clear out these items
                catalystDeployTaskHistory.delete(savedTask);

                for(CatalystDeploysTask cdTask : catalystDeploysTasks){
                    //Check if this task is saved
                    cdTask.setCollectorId(collector.getId());
                    catalystDeployTaskHistory.save(cdTask);
                    LOG.info("Added history item " + cdTask.getTaskName());
                }
            }
            enabledDeploys++;
        }
        LOG.info("Enabled Deploys:" + enabledDeploys);
        LOG.info("Updated Deploys:" + updatedDeploys);
        LOG.info("New Deploys:" + newDeploys);
        LOG.info("Finished");

    }

        private List<CatalystRepo> enabledRepos(Collector collector) {
            return catalystTaskRepository.findEnabledCatalystRepos(collector.getId());   //gitRepoRepository.findEnabledGitRepos(collector.getId());
        }


}
