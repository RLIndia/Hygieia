package com.capitalone.dashboard.repository;

/**
 * Created by vinod on 30/9/16.
 */
import com.capitalone.dashboard.model.TestRailRuns;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TestRailRunRepository extends CrudRepository<TestRailRuns, ObjectId>, QueryDslPredicateExecutor<TestRailRuns>  {

    @Query(value="{ 'collectorItemId': ?0,'runId':?1}")
    TestRailRuns findByCollectorItemIdAndRunId(ObjectId collectorItemId, String runId);

    @Query(value="{ 'collectorItemId': ?0}")
    List<TestRailRuns> findByCollectorItemId(ObjectId collectorItemId);

}
