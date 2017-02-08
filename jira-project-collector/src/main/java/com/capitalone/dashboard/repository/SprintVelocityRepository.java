package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.SprintVelocity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface SprintVelocityRepository extends CrudRepository<SprintVelocity, ObjectId>, QueryDslPredicateExecutor<SprintVelocity> {
    @Query(value="{ 'collectorItemId' : ?0, versionId : ?1, projectId : ?2}")
    List<SprintVelocity> findVelocityReport(ObjectId collectorItemId, String VERSIONID, String PROJECTID);


    @Query(value="{ 'collectorItemId' : ?0, sprintId: ?1}")
    SprintVelocity findSprintVelocityByCollectorIdSprintId(ObjectId collectorId,String sprintId);
}
