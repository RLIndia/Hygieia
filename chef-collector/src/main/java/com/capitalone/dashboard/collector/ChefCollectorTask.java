package com.capitalone.dashboard.collector;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.model.ChefNode;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorType;

import com.capitalone.dashboard.model.CookbookCollectorItem;

import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.ChefCookbookRepository;
import com.capitalone.dashboard.repository.ChefNodeRepository;

@Component
public class ChefCollectorTask extends CollectorTask<Collector> {

	private static final Log LOG = LogFactory.getLog(ChefCollectorTask.class);
	private final BaseCollectorRepository<Collector> collectorRepository;
	private final ChefSettings chefSettings;
	private final ChefClient chefClient;
	private final ChefCookbookRepository chefCookbookRepository;
	private final ChefNodeRepository chefNodeRepository;

	@Autowired
	public ChefCollectorTask(TaskScheduler taskScheduler,BaseCollectorRepository<Collector> collectorRepository,
			ChefSettings chefSettings,
			ChefClient chefClient,
			ChefCookbookRepository chefCookbookRepository,
			ChefNodeRepository chefNodeRepository) {
		super(taskScheduler, "Chef");
		this.collectorRepository = collectorRepository;
		this.chefSettings = chefSettings;
		this.chefClient = chefClient;
		this.chefCookbookRepository = chefCookbookRepository;
		this.chefNodeRepository = chefNodeRepository;
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

	/*@Override
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

		List<CookbookCollectorItem> cookbookItems = new ArrayList<>(); 
		for(CookbookCollectorItem item : enabledCollectorItems(collector)){
			cookbookItems.add(item);
		}
		LOG.info("Enabled Application ==>"+cookbookItems.size());

		if(cookbookItems.size()!=0) {
			List<ChefNode> nodes= chefClient.getNodesByCookbookNames(cookbookItems);
			LOG.info("nodes ==>"+nodes);
			if(nodes != null && nodes.size()!=0){
				LOG.info("nodes size ==>"+nodes.size());
				chefNodeRepository.deleteAll();
				LOG.info("nodes deleted");

				chefNodeRepository.save(nodes);
				LOG.info("total nodes saved ==>"+nodes.size());
			}
		}




	}*/

	@Override
	public void collect(Collector collector) {
		
		List<ChefNode> nodes= chefClient.getNodes();
		if(nodes !=null) {
			for(int i=0;i<nodes.size();i++) {
				CookbookCollectorItem fetchedItem = chefCookbookRepository.findCookbookCollectorItem(collector.getId(), nodes.get(i).getProductName());
				if(fetchedItem == null) {
					CookbookCollectorItem item = new CookbookCollectorItem();
					item.setCookbookName(nodes.get(i).getProductName());
					item.setEnabled(false);
					item.setCollectorId(collector.getId());
					LOG.info("saving");
					chefCookbookRepository.save(item);
				}
			}
		}
		

		List<CookbookCollectorItem> cookbookItems = enabledCollectorItems(collector);
		if(cookbookItems != null && cookbookItems.size()>0) {
			CookbookCollectorItem enabledItem = cookbookItems.get(0);
			LOG.info("nodes ==>"+nodes);
			if(nodes != null && nodes.size()!=0){
				List<ChefNode> saveNodes = new ArrayList<>();
				
				for(int i=0;i<nodes.size();i++) {
					String productName = nodes.get(i).getProductName();
					boolean found = false;
					CookbookCollectorItem item = null;
					for(int j=0;j<cookbookItems.size();j++) {
						if(productName.equals(cookbookItems.get(j).getCookbookName())) {
							found = true;
							item = cookbookItems.get(j);
							break;
						}
					}
					if(found) {
						ChefNode node = nodes.get(i);
						node.setCollectorItemId(item.getId());
						saveNodes.add(node);
					}
					
				}
				
				LOG.info("nodes size ==>"+saveNodes.size());
				chefNodeRepository.deleteAll();
				LOG.info("nodes deleted");
				chefNodeRepository.save(saveNodes);
				LOG.info("total nodes saved ==>"+saveNodes.size());
			}
		}




	}

	private List<CookbookCollectorItem> enabledCollectorItems(Collector collector) {
		return chefCookbookRepository.findEnabledJiraRepos(collector.getId());   //gitRepoRepository.findEnabledGitRepos(collector.getId());
	}



}
