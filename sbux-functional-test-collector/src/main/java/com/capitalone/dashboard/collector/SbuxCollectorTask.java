package com.capitalone.dashboard.collector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.repository.BaseCollectorRepository;

@Component
public class SbuxCollectorTask extends CollectorTask<Collector>{
	private static final Log LOG = LogFactory.getLog(SbuxCollectorTask.class);

	private final BaseCollectorRepository<Collector> collectorRepository;
	private final SBUXSettings sbuxSettings;
	private final SBUXClient sbuxClient;

	@Autowired
	public SbuxCollectorTask(TaskScheduler taskScheduler,
			BaseCollectorRepository<Collector> collectorRepository,
			SBUXSettings sbuxSettings,
			SBUXClient sbuxClient) {
		super(taskScheduler, "SBUXFunctionTest");
		this.collectorRepository = collectorRepository;
		this.sbuxSettings = sbuxSettings;
		this.sbuxClient = sbuxClient;

	}

	@Override
	public Collector getCollector() {
		Collector protoType = new Collector();
		protoType.setName("SBUXFunctionTest");
		protoType.setCollectorType(CollectorType.FunctionTest);
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
		LOG.info("Collector runing");
		LOG.info("size ==> "+ sbuxClient.getFunctionalTestResults().size());



	}

}
