package com.capitalone.dashboard.collector;

/**
 * Created by vinod on 8/9/16.
 */

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.JiraRepo;
import com.capitalone.dashboard.model.ProjectVersionIssues;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.JiraProjectRepository;
import com.capitalone.dashboard.repository.ProjectVersionRepository;
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
 * CollectorTask that fetches Project Version Issues information from Jira
 */
@Component
public class JiraCollectorTask extends CollectorTask<Collector> {
    private static final Log LOG = LogFactory.getLog(JiraCollectorTask.class);

    private final BaseCollectorRepository<Collector> collectorRepository;
    private final JiraProjectRepository jiraprojectrepository;
    private final JiraClient jiraclient;
    private final ProjectVersionRepository projectversionrepository;
    private final JiraSettings jirasettings;
    private final ComponentRepository dbComponentRepository;

    @Autowired
    public JiraCollectorTask(TaskScheduler taskScheduler,
                             BaseCollectorRepository<Collector> collectorRepository,
                             JiraProjectRepository jiraprojectrepository,
                             ProjectVersionRepository projectversionrepository,
                             JiraClient jiraclient,
                             JiraSettings jirasettings,
                             ComponentRepository dbComponentRepository) {
        super(taskScheduler, "Jiraproject");
        this.collectorRepository = collectorRepository;
        this.jiraclient = jiraclient;
        this.jiraprojectrepository = jiraprojectrepository;
        this.projectversionrepository = projectversionrepository;
        this.jirasettings = jirasettings;
        this.dbComponentRepository = dbComponentRepository;
    }

    @Override
    public Collector getCollector() {
        Collector protoType = new Collector();
        protoType.setName("Jiraproject");
        protoType.setCollectorType(CollectorType.Jiraproject);
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
        return jirasettings.getCron();
    }

    private void clean(Collector collector) {
        Set<ObjectId> uniqueIDs = new HashSet<ObjectId>();
        /**
         * Logic: For each component, retrieve the collector item list of the type SCM.
         * Store their IDs in a unique set ONLY if their collector IDs match with Bitbucket collectors ID.
         */
        for (com.capitalone.dashboard.model.Component comp : dbComponentRepository.findAll()) {
            if (comp.getCollectorItems() == null || comp.getCollectorItems().isEmpty()) continue;
            List<CollectorItem> itemList = comp.getCollectorItems().get(CollectorType.Jiraproject);
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
        List<JiraRepo> repoList = new ArrayList<JiraRepo>();
        Set<ObjectId> jiraID = new HashSet<ObjectId>();
        jiraID.add(collector.getId());
        for (JiraRepo repo : jiraprojectrepository.findByCollectorIdIn(jiraID)) {
            if (repo != null) {
                repo.setEnabled(uniqueIDs.contains(repo.getId()));
                repoList.add(repo);
            }
        }
        jiraprojectrepository.save(repoList);
    }

    @Override
    public void collect(Collector collector) {
        logBanner("JiraProjectCollector..");
        long start = System.currentTimeMillis();
        int projectCount = 0;
        int issueCount = 0;
        clean(collector);
        for(JiraRepo repo : enabledRepos(collector)){
            boolean firstRun = false;

        }

    }

    private List<JiraRepo> enabledRepos(Collector collector) {
        return jiraprojectrepository.findEnabledJiraRepos(collector.getId());   //gitRepoRepository.findEnabledGitRepos(collector.getId());
    }

    private boolean isNewProjectVersionIssue(JiraRepo repo, ProjectVersionIssues projectversionissues){
        ProjectVersionIssues newprojectversionissues =  projectversionrepository.findByCollectorItemIdAndIssueId(repo.getId(),projectversionissues.getIssueID());
       // LOG.info("commit ==>"+newCommit);
        return newprojectversionissues == null;
    }
}
