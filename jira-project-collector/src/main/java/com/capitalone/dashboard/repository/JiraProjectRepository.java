package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.JiraRepo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
/**
 * Created by Vinod on 6/9/16.
 */
public interface JiraProjectRepository extends BaseCollectorItemRepository<JiraRepo>{
    @Query(value="{ 'collectorId' : ?0, options.versionId : ?1, options.projectId : ?2}")
    JiraRepo findJiraRepo(ObjectId collectorId, String VERSIONID, String PROJECTID);

    @Query(value="{ 'collectorId' : ?0, enabled: true}")
    List<JiraRepo> findEnabledJiraRepos(ObjectId collectorId);
}
