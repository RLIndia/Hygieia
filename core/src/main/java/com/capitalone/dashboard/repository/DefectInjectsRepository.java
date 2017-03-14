package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.DefectInjection;
import com.capitalone.dashboard.model.SprintVelocity;


public interface DefectInjectsRepository extends CrudRepository<DefectInjection, ObjectId>, QueryDslPredicateExecutor<DefectInjection> {
    @Query(value="{ 'collectorItemId' : ?0, projectId : ?1, versionId: ?2}")
    List<DefectInjection> findDefectInjection(ObjectId collectorItemId, String PROJECTID, String versionId);
    
    @Query(value="{ 'collectorItemId' : ?0, sprintId: ?1, versionId: ?2, projectId: ?3}")
    DefectInjection findDefectInjectionByCollectorIdSprintId(ObjectId collectorId,String sprintId, String versionId, String projectId);
}
