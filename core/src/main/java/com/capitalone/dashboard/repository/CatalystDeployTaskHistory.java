package com.capitalone.dashboard.repository;

/**
 * Created by vinod on 24/9/16.
 */


import com.capitalone.dashboard.model.CatalystDeploysTask;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface CatalystDeployTaskHistory extends CrudRepository<CatalystDeploysTask, ObjectId>, QueryDslPredicateExecutor<CatalystDeploysTask> {

    @Query(value="{ 'taskId': ?0}")
    List<CatalystDeploysTask> findByTaskId(String taskId);

    @Query(value="{ 'collectorId': ?0}")
    List<CatalystDeploysTask> findByCollectorId(String collectorId);
}
