package com.capitalone.dashboard.collector;

/**
 * Created by root on 27/9/16.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.capitalone.dashboard.repository.ComponentRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorType;

import com.capitalone.dashboard.repository.TestrailProjectRepository;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.model.TestrailCollectorModel;


@Component
public class TestrailCollectorTask extends CollectorTask<Collector> {
    private static final Log LOG = LogFactory.getLog(TestrailCollectorTask.class);
    private final BaseCollectorRepository<Collector> collectorRepository;
    private final TestrailSettings testrailSettings;
    private final TestrailProjectRepository testrailProjectRepository;
    private final ComponentRepository dbComponentRepository;
    private final TestrailClient testrailClient;

    @Autowired
    public TestrailCollectorTask(TaskScheduler taskScheduler,BaseCollectorRepository<Collector> collectorRepository,TestrailSettings testrailSettings, TestrailProjectRepository testrailProjectRepository,TestrailClient testrailClient,ComponentRepository dbComponentRepository){
        super(taskScheduler, "Testrail");
        this.collectorRepository = collectorRepository;
        this.testrailSettings = testrailSettings;
        this.testrailProjectRepository = testrailProjectRepository;
        this.testrailClient = testrailClient;
        this.dbComponentRepository = dbComponentRepository;
    }

    @Override
    public Collector getCollector() {
        Collector protoType = new Collector();
        protoType.setName("Testrail");
        protoType.setCollectorType(CollectorType.Testrail);
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
        return testrailSettings.getCron();
    }

    @Override
    public void collect(Collector collector){
        // TODO Auto-generated method stub
        long start = System.currentTimeMillis();

        LOG.info("Starting Testrail Collector..");
        try{
            List<TestrailCollectorModel> tcmprojects = testrailClient.getTestrailProjects();
            for(TestrailCollectorModel tcm : tcmprojects){
                TestrailCollectorModel savedtcm = (TestrailCollectorModel) testrailProjectRepository.findCollectorItemByProjectandMilestone(collector.getId(),tcm.getProjectId(),tcm.getMilestoneId());
                if(savedtcm == null){
                    tcm.setCollectorId(collector.getId());
                    tcm.setEnabled(false);
                    try{
                        testrailProjectRepository.save(tcm);
                    }catch (Exception e){
                        LOG.info("Error Saving: " + e.getMessage());
                    }


                }
            }

            //Get All the enabled Projects
            for(TestrailCollectorModel tcm : enabledRepos(collector)){
                boolean firstRun = false;
                //Return back here after creating the testcase repo in core.
            }

            LOG.info("Finished.");
        }catch (TRAPIException e) {
            LOG.info(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private List<TestrailCollectorModel> enabledRepos(Collector collector) {
        return testrailProjectRepository.findEnabledTestrailProjects(collector.getId());
    }

}
