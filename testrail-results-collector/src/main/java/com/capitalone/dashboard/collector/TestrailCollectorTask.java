package com.capitalone.dashboard.collector;

/**
 * Created by root on 27/9/16.
 */

import java.io.IOException;
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
import com.capitalone.dashboard.model.TestRailRuns;
import com.capitalone.dashboard.repository.TestRailRunRepository;

import com.capitalone.dashboard.repository.TestRailRunResultRepository;
import com.capitalone.dashboard.model.TestRailRunsResults;


@Component
public class TestrailCollectorTask extends CollectorTask<Collector> {
    private static final Log LOG = LogFactory.getLog(TestrailCollectorTask.class);
    private final BaseCollectorRepository<Collector> collectorRepository;
    private final TestrailSettings testrailSettings;
    private final TestrailProjectRepository testrailProjectRepository;
    private final ComponentRepository dbComponentRepository;
    private final TestrailClient testrailClient;
    private final TestRailRunRepository testRailRunRepository;
    private final TestRailRunResultRepository testRailRunResultRepository;

    @Autowired
    public TestrailCollectorTask(TaskScheduler taskScheduler,BaseCollectorRepository<Collector> collectorRepository,TestrailSettings testrailSettings, TestrailProjectRepository testrailProjectRepository,TestrailClient testrailClient,TestRailRunRepository testRailRunRepository,ComponentRepository dbComponentRepository,TestRailRunResultRepository testRailRunResultRepository){
        super(taskScheduler, "Testrail");
        this.collectorRepository = collectorRepository;
        this.testrailSettings = testrailSettings;
        this.testrailProjectRepository = testrailProjectRepository;
        this.testrailClient = testrailClient;
        this.testRailRunRepository = testRailRunRepository;
        this.testRailRunResultRepository = testRailRunResultRepository;
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
                LOG.info("Finding " + tcm.getProjectname() + " " + tcm.getMilestonename());
                TestrailCollectorModel savedtcm = testrailProjectRepository.findCollectorItemByProjectandMilestone(collector.getId(),tcm.getProjectId(),tcm.getMilestoneId());
                //LOG.info("Savedtcm " + savedtcm.toString());
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
            int enabledRuns = 0;
            for(TestrailCollectorModel tcm : enabledRepos(collector)){
                boolean firstRun = false;
                //Return back here after creating the testcase repo in core.
                LOG.info("Enabled Collector:" + collector.getId());
                List<TestRailRuns> allRuns = testrailClient.getAllRunsForProjectAndMileStone(tcm.getProjectId(),tcm.getMilestoneId());
                int runCount = 0;
                for(TestRailRuns trr : allRuns){
                    LOG.info(trr.getUrl());
                    //to do check if entry in db else add.
                    String runId = trr.getRunid();
                    TestRailRuns savedRun = (TestRailRuns) testRailRunRepository.findByCollectorItemIdAndRunId(tcm.getId(),trr.getRunid());
                    LOG.info("Checking if found:" + savedRun);
                    if(savedRun != null){
                        //remove existing and add new
                        testRailRunRepository.delete(savedRun);
                    }
                    //To do : Historical run information would be retained in the db.
                    trr.setCollectorItemId(tcm.getId());
                    testRailRunRepository.save(trr);
                    runCount++;

                    //Fetch result details for run.
                    LOG.info("Run ID: " + trr.getRunid());
                    List<TestRailRunsResults> trrr = testrailClient.getAllResultsforRun(trr.getRunid(),trr.getMilestoneId(),trr.getProjectId());
                    for(TestRailRunsResults result : trrr){
                        LOG.info("Test ID: " + result.getTestId());
                        TestRailRunsResults savedResult = testRailRunResultRepository.findByTestIdAndRunId(result.getRunId(),result.getTestId());
                       // LOG.info(result.getTestName() + (savedResult.getTestName() == null));
                        if(savedResult != null){
                            testRailRunResultRepository.delete(savedResult);
                        }
                        //To do : Historical run result information would be retained in the db.
                        testRailRunResultRepository.save(result);
                    }

                }
                LOG.info("Found Runs:" + runCount);
                enabledRuns++;
            }
            LOG.info("Enabled runs: " + enabledRuns);
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
