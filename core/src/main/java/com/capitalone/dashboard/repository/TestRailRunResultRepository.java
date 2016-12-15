package com.capitalone.dashboard.repository;

/**
 * Created by vinod on 1/10/16.
 */

import com.capitalone.dashboard.model.TestRailRunsResults;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TestRailRunResultRepository extends CrudRepository<TestRailRunsResults, ObjectId>, QueryDslPredicateExecutor<TestRailRunsResults> {

    @Query(value="{ 'runId': ?0,'testId':?1}")
    TestRailRunsResults findByTestIdAndRunId(String runId, String testId);

    @Query(value="{ 'runId': ?0,'projectId':?1,'milestoneId':?2}")
    List<TestRailRunsResults> findByRunIdAndProjectIdAndMilestoneId(String id,String projectId, String milestoneId);

}
