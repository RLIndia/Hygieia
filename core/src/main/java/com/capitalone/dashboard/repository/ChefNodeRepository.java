package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.ChefNode;
import com.capitalone.dashboard.model.ProjectVersionIssues;


public interface ChefNodeRepository extends CrudRepository<ChefNode, ObjectId>, QueryDslPredicateExecutor<ChefNode> {
	
	@Query(value="{ 'collectorItemId': ?0}")
    List<ChefNode> findByCollectorItemId(ObjectId collectorItemId);
}
