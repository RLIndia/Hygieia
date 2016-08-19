package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.FunctionalTestResult;

public interface FunctionalTestResultRepository  extends CrudRepository<FunctionalTestResult, ObjectId>, QueryDslPredicateExecutor<FunctionalTestResult>{

	 @Query(value="{ 'envId': ?0}")
	 List<FunctionalTestResult> findByEnvId(String envId);
	 
	 @Query(value="{ 'collectorItemId': ?0,'envId': ?1,'timeExecuted':?2,'testCaseName':?3}")
	 FunctionalTestResult findByCollectorItemIdEnvIdExecutedTimeTestCaseName(ObjectId collectorItemid,String envId,long timeExecuted,String testCaseName);

	 @Query(value="{ 'collectorItemId': ?0,'envId': ?1,'timeExecuted':{ $gt: ?2 }}")
	 List<FunctionalTestResult> findByCollectorItemIdEnvIdExecutedTime(ObjectId collectorItemid,String envId,long timeExecuted);
	 
	 @Query(value="{ 'collectorItemId': ?0,'envId': ?1}")
	 List<FunctionalTestResult> findByCollectorItemIdEnvId(ObjectId collectorItemid,String envId);
}
