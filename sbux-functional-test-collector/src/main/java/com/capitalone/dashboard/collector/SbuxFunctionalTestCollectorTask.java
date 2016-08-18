package com.capitalone.dashboard.collector;



import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.FunctionalTestResult;
import com.capitalone.dashboard.model.SBUXFunctionalTestEnvrironment;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.FunctionalTestResultRepository;

import com.capitalone.dashboard.repository.SBUXFunctionalTestEnvironmentRepository;


@Component
public class SbuxFunctionalTestCollectorTask extends CollectorTask<Collector>{
	private static final Log LOG = LogFactory.getLog(SbuxFunctionalTestCollectorTask.class);

	private final BaseCollectorRepository<Collector> collectorRepository;
	private final SBUXSettings sbuxSettings;
	private final SBUXClient sbuxClient;
	private final FunctionalTestResultRepository functionalTestResultRepository;
	private final SBUXFunctionalTestEnvironmentRepository sbuxFunctionalTestEnvironmentRepository;

	@Autowired
	public SbuxFunctionalTestCollectorTask(TaskScheduler taskScheduler,
			BaseCollectorRepository<Collector> collectorRepository,
			SBUXSettings sbuxSettings,
			SBUXClient sbuxClient,
			FunctionalTestResultRepository functionalTestResultRepository,
			SBUXFunctionalTestEnvironmentRepository sbuxFunctionalTestEnvironmentRepository) {
		super(taskScheduler, "SBUXFunctionTest");
		this.collectorRepository = collectorRepository;
		this.sbuxSettings = sbuxSettings;
		this.sbuxClient = sbuxClient;
        this.functionalTestResultRepository = functionalTestResultRepository;
        this.sbuxFunctionalTestEnvironmentRepository = sbuxFunctionalTestEnvironmentRepository;
	}

	@Override
	public Collector getCollector() {
		Collector protoType = new Collector();
		protoType.setName("SBUXFunctionTest");
		protoType.setCollectorType(CollectorType.Functional);
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
		return sbuxSettings.getCron();
	}

	@Override
	public void collect(Collector collector) {
		// TODO Auto-generated method stub
		LOG.info("Collector running");
		
		long start = System.currentTimeMillis();

	
		addNewEnvironment(sbuxClient.getFunctionalTestEnvironment(),
				collector);
		
		List<SBUXFunctionalTestEnvrironment> functionalTestEnvrironments = enabledEnvironments(collector, sbuxSettings.getUrl());
		LOG.info("Enabled Applications ==>"+functionalTestEnvrironments.size());

		updateData(functionalTestEnvrironments);
		
		log("Finished", start);
		
	}
	
	private void addNewEnvironment(List<SBUXFunctionalTestEnvrironment> envrironments,Collector collector) {
		long start = System.currentTimeMillis();
		int count = 0;

		log("All environments", start, envrironments.size());
		for (SBUXFunctionalTestEnvrironment environment : envrironments) {

			if (isNewEnvironment(collector, environment)) {
				environment.setCollectorId(collector.getId());
				environment.setEnabled(false);
				try {
					sbuxFunctionalTestEnvironmentRepository.save(environment);
				} catch (org.springframework.dao.DuplicateKeyException ce) {
					log("Duplicates items not allowed", 0);

				}
				count++;
			}

		}
		log("New apps", start, count);
	}
	
	private boolean isNewEnvironment(Collector collector,
			SBUXFunctionalTestEnvrironment environment) {
		return sbuxFunctionalTestEnvironmentRepository.findTestEnvironment(
				collector.getId(), sbuxSettings.getUrl(),
				environment.getEnvId()) == null;
	}
	
	private List<SBUXFunctionalTestEnvrironment> enabledEnvironments(
			Collector collector, String instanceUrl) {
		return sbuxFunctionalTestEnvironmentRepository.findEnabledTestEnvironments(
				collector.getId(), instanceUrl);
	}
	
	private void updateData(List<SBUXFunctionalTestEnvrironment> environments) {
		List<FunctionalTestResult> newFunctionalTestResults = new ArrayList<>();
		for (SBUXFunctionalTestEnvrironment environment : environments) {
			List<FunctionalTestResult> functionalTestResults = sbuxClient.getFunctionalTestResults(environment);
			
			for(FunctionalTestResult functionalTestResult : functionalTestResults) {
				// checking if an entry is already in DB
				FunctionalTestResult fr = functionalTestResultRepository.findByCollectorItemIdEnvIdExecutedTimeTestCaseName(environment.getId(),
						environment.getEnvId(),
						functionalTestResult.getTimeExecuted(),
						functionalTestResult.getTestCaseName());
				if(fr == null) { // does not exists
					functionalTestResult.setCollectorItemId(environment.getId());
					newFunctionalTestResults.add(functionalTestResult);
				}
				
			}
			
		}
		functionalTestResultRepository.save(newFunctionalTestResults);
	}

}
