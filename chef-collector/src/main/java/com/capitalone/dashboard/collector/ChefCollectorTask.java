package com.capitalone.dashboard.collector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.repository.BaseCollectorRepository;

@Component
public class ChefCollectorTask extends CollectorTask<Collector> {
	 private final BaseCollectorRepository<Collector> collectorRepository;
	 private final ChefSettings chefSettings;
	 private final ChefClient chefClient;
	
	@Autowired
    public ChefCollectorTask(TaskScheduler taskScheduler,BaseCollectorRepository<Collector> collectorRepository,
    		ChefSettings chefSettings,
    		ChefClient chefClient) {
		super(taskScheduler, "Chef");
		this.collectorRepository = collectorRepository;
		this.chefSettings = chefSettings;
		this.chefClient = chefClient;
	}

	@Override
	public Collector getCollector() {
		Collector protoType = new Collector();
        protoType.setName("Chef");
        protoType.setCollectorType(CollectorType.SCM);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        return protoType;
	}

	@Override
	public BaseCollectorRepository<Collector> getCollectorRepository() {
		// TODO Auto-generated method stub
		return collectorRepository;
	}

	@Override
	public String getCron() {
		return chefSettings.getCron();
	}

	@Override
	public void collect(Collector collector) {
		chefClient.getRunlist();
		
	}
	
	

}
