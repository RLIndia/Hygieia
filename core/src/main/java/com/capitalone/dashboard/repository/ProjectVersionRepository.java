package com.capitalone.dashboard.repository;


import com.capitalone.dashboard.model.ProjectVersionIssues;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by vinod on 8/9/16.
 */
public interface ProjectVersionRepository  extends CrudRepository<ProjectVersionIssues, ObjectId>, QueryDslPredicateExecutor<ProjectVersionIssues> {
    /* finds a issue by issueid and collectorid */

    @Query(value="{ 'collectorItemId': ?0,'issueID':?1}")
    ProjectVersionIssues findByCollectorItemIdAndIssueId(ObjectId collectorItemId, String issueID);

    @Query(value="{ 'collectorItemId': ?0}")
    List<ProjectVersionIssues> findByCollectorItemId(ObjectId collectorItemId);
}
