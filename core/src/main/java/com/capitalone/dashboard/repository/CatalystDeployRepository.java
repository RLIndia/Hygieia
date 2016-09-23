package com.capitalone.dashboard.repository;


import com.capitalone.dashboard.model.CatalystDeploys;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by vinod on 23/9/16.
 */
public interface CatalystDeployRepository  extends CrudRepository<CatalystDeploys, ObjectId>, QueryDslPredicateExecutor<CatalystDeploys> {
    /* finds a issue by issueid and collectorid */


    CatalystDeploys findByCollectorItemIdAndRepository(ObjectId collectorItemId, String repository);

    @Query(value="{ 'collectorItemId': ?0}")
    List<CatalystDeploys> findByCollectorItemId(ObjectId collectorItemId);
}
