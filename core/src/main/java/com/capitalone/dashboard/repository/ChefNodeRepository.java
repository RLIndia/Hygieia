package com.capitalone.dashboard.repository;

import org.bson.types.ObjectId;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.ChefNode;


public interface ChefNodeRepository extends CrudRepository<ChefNode, ObjectId>, QueryDslPredicateExecutor<ChefNode> {

}
