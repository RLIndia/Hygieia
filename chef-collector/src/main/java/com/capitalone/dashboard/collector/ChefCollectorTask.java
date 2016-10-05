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

import com.capitalone.dashboard.model.CookbookCollectorItem;

import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.ChefCookbookRepository;

@Component
public class ChefCollectorTask extends CollectorTask<Collector> {
	
	 private static final Log LOG = LogFactory.getLog(ChefCollectorTask.class);
	 private final BaseCollectorRepository<Collector> collectorRepository;
	 private final ChefSettings chefSettings;
	 private final ChefClient chefClient;
	 private final ChefCookbookRepository chefCookbookRepository;
	
	@Autowired
    public ChefCollectorTask(TaskScheduler taskScheduler,BaseCollectorRepository<Collector> collectorRepository,
    		ChefSettings chefSettings,
    		ChefClient chefClient,
    		ChefCookbookRepository chefCookbookRepository) {
		super(taskScheduler, "Chef");
		this.collectorRepository = collectorRepository;
		this.chefSettings = chefSettings;
		this.chefClient = chefClient;
		this.chefCookbookRepository = chefCookbookRepository;
	}

	@Override
	public Collector getCollector() {
		Collector protoType = new Collector();
        protoType.setName("Chef");
        protoType.setCollectorType(CollectorType.Chef);
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
		
		List<CookbookCollectorItem> fethedCookbook = chefClient.getRunlist();
		List<CookbookCollectorItem> cookbooksList = new ArrayList<CookbookCollectorItem>();
		
		for(CookbookCollectorItem item : fethedCookbook){
			CookbookCollectorItem fetchedItem = chefCookbookRepository.findCookbookCollectorItem(collector.getId(), item.getCookbookName());
	            //LOG.info(collector.getId() + " " + repo.getVERSIONID() + " " + repo.getPROJECTID());

	            if(fetchedItem == null){
	            	item.setCollectorId(collector.getId());
	            	item.setEnabled(false);

	              //  LOG.info("To Add:" + repo.getPROJECTNAME() + " " + repo.getVERSIONNAME());
	            	cookbooksList.add(item);
	            }
		}
		LOG.info("Cookbooks size ==>"+cookbooksList.size());
		chefCookbookRepository.save(cookbooksList);
		
	}
	
	

}
