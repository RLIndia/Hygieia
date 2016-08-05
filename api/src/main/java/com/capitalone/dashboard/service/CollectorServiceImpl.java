package com.capitalone.dashboard.service;

import com.capitalone.dashboard.config.GitSettings;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;

import javax.annotation.PostConstruct;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CollectorServiceImpl implements CollectorService {
	private static final Log LOG = LogFactory.getLog(CollectorServiceImpl.class);
    private final CollectorRepository collectorRepository;
    private final CollectorItemRepository collectorItemRepository;
    private final DashboardRepository dashboardRepository;
    private final GitSettings gitSettings;
    private final RestOperations restOperations;


    @Autowired
    public CollectorServiceImpl(CollectorRepository collectorRepository,
                                CollectorItemRepository collectorItemRepository,
                                DashboardRepository dashboardRepository,GitSettings gitSettings,RestOperations restOperations) {
        this.collectorRepository = collectorRepository;
        this.collectorItemRepository = collectorItemRepository;
        this.dashboardRepository = dashboardRepository;
        this.gitSettings = gitSettings;
        this.restOperations = restOperations;
    }

    @Override
    public List<Collector> collectorsByType(CollectorType collectorType) {
        return collectorRepository.findByCollectorType(collectorType);
    }

    @Override
    public List<CollectorItem> collectorItemsByType(CollectorType collectorType) {
        List<Collector> collectors = collectorRepository.findByCollectorType(collectorType);

        List<ObjectId> collectorIds = Lists.newArrayList(Iterables.transform(collectors, new ToCollectorId()));

        List<CollectorItem> collectorItems = collectorItemRepository.findByCollectorIdIn(collectorIds);

        for (CollectorItem options : collectorItems) {
            options.setCollector(collectorById(options.getCollectorId(), collectors));
        }

        return collectorItems;
    }

    /**
     * We want to initialize the Quasi-product collector when the API starts up
     * so that any existing Team dashboards will be added as CollectorItems.
     *
     * TODO - Is this the best home for this method??
     */
    @PostConstruct
    public void initProductCollectorOnStartup() {
        Collector productCollector = collectorRepository.findByName("Product");
        if (productCollector == null) {
            productCollector = new Collector();
            productCollector.setName("Product");
            productCollector.setCollectorType(CollectorType.Product);
            productCollector.setEnabled(true);
            productCollector.setOnline(true);
            collectorRepository.save(productCollector);

            // Create collector items for existing team dashboards
            for (Dashboard dashboard : dashboardRepository.findTeamDashboards()) {
                CollectorItem item = new CollectorItem();
                item.setCollectorId(productCollector.getId());
                item.getOptions().put("dashboardId", dashboard.getId().toString());
                item.setDescription(dashboard.getTitle());
                collectorItemRepository.save(item);
            }
        }
    }

    @Override
    public CollectorItem getCollectorItem(ObjectId id) {
        CollectorItem item = collectorItemRepository.findOne(id);
        item.setCollector(collectorRepository.findOne(item.getCollectorId()));
        return item;
    }

    @Override
    public CollectorItem createCollectorItem(CollectorItem item) {
        
        CollectorItem existing = collectorItemRepository.findByCollectorAndOptions(
                item.getCollectorId(), item.getOptions());
        if (existing != null) {
            item.setId(existing.getId());
        }
        return collectorItemRepository.save(item);
    }
    
    @Override
    public List<CollectorItem> createCollectorItemBitBucket(CollectorItem item) {
    	
    	// trying to find all the branches
    	Map<String,Object> options = item.getOptions();
    	
    	String repoUrl = (String) options.get("url");
		if (repoUrl.endsWith(".git")) {
			repoUrl = repoUrl.substring(0, repoUrl.lastIndexOf(".git"));
		}
		URL url = null;
		String hostName = "";
		String protocol = "";
		try {
			url = new URL(repoUrl);
			hostName = url.getHost();
			protocol = url.getProtocol();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			LOG.info("error ==>"+e.getMessage());
		}
		String hostUrl = protocol + "://" + hostName + "/";
		String repoName = repoUrl.substring(hostUrl.length(), repoUrl.length());
		String apiUrl = "";
		if (hostName.startsWith(gitSettings.getHost())) {
			apiUrl = protocol + "://" + gitSettings.getHost() + repoName;
			//LOG.info("API URL IS IF :"+apiUrl);

		} else {
			//apiUrl = protocol + "://" + hostName + settings.getApi() + repoName;
			apiUrl = protocol + "://" + gitSettings.getHost() + gitSettings.getApi() + repoName;
			//LOG.info("API URL IS ELSE :"+apiUrl);
		}
		
		String queryUrl = apiUrl + "refs/branches";
		
		ResponseEntity<String> response = makeRestCall(queryUrl, gitSettings.getUsername(), gitSettings.getPassword());
		//JSONObject jsonParentObject = paresAsObject(response);
		
		paresAsObject(response);
		
    	
    	//return collectorItemRepository.save(item);
		return new ArrayList<>();
    }
    
    
    private ResponseEntity<String> makeRestCall(String url, String userId,
			String password) {
		// Basic Auth only.
		//LOG.info("username ==> "+userId);
		//LOG.info("password ==> "+password);

		if (!"".equals(userId) && !"".equals(password)) {
			return restOperations.exchange(url, HttpMethod.GET,
					new HttpEntity<>(createHeaders(userId, password)),
					String.class);

		} else {
			return restOperations.exchange(url, HttpMethod.GET, null,
					String.class);
		}

	}

	private HttpHeaders createHeaders(final String userId, final String password) {
		String auth = userId + ":" + password;
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII));

		String authHeader = "Basic " + new String(encodedAuth);
		//String authHeader = new String(encodedAuth);


		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", authHeader);
		return headers;
	}
	
	private JSONObject paresAsObject(ResponseEntity<String> response) {
		try {
			return (JSONObject) new JSONParser().parse(response.getBody());
		} catch (ParseException pe) {
			LOG.error(pe.getMessage());
		}
		return new JSONObject();
	}
    

    @Override
    public CollectorItem createCollectorItemByNiceNameAndProjectId(CollectorItem item, String projectId) throws HygieiaException {
        //Try to find a matching by collector ID and niceName.
        CollectorItem existing = collectorItemRepository.findByCollectorIdNiceNameAndProjectId(item.getCollectorId(), item.getNiceName(), projectId);

        //if not found, call the method to look up by collector ID and options. NiceName would be saved too
        if (existing == null) return createCollectorItem(item);

        //Flow is here because there is only one collector item with the same collector id and niceName. So, update with
        // the new info - keep the same collector item id. Save = Update or Insert.
        item.setId(existing.getId());

        return collectorItemRepository.save(item);
    }

    @Override
    public CollectorItem createCollectorItemByNiceNameAndJobName(CollectorItem item, String jobName) throws HygieiaException {
        //Try to find a matching by collector ID and niceName.
        CollectorItem existing = collectorItemRepository.findByCollectorIdNiceNameAndJobName(item.getCollectorId(), item.getNiceName(), jobName);

        //if not found, call the method to look up by collector ID and options. NiceName would be saved too
        if (existing == null) return createCollectorItem(item);

        //Flow is here because there is only one collector item with the same collector id and niceName. So, update with
        // the new info - keep the same collector item id. Save = Update or Insert.
        item.setId(existing.getId());

        return collectorItemRepository.save(item);
    }

    @Override
    public Collector createCollector(Collector collector) {
        Collector existing = collectorRepository.findByName(collector.getName());
        if (existing != null) {
            collector.setId(existing.getId());
        }
        return collectorRepository.save(collector);
    }

    private Collector collectorById(ObjectId collectorId, List<Collector> collectors) {
        for (Collector collector : collectors) {
            if (collector.getId().equals(collectorId)) {
                return collector;
            }
        }
        return null;
    }

    private static class ToCollectorId implements Function<Collector, ObjectId> {
        @Override
        public ObjectId apply(Collector input) {
            return input.getId();
        }
    }
}
