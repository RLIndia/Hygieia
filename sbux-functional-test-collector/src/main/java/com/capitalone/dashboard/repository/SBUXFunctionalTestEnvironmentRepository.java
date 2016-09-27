package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;


import com.capitalone.dashboard.model.SBUXFunctionalTestEnvrironment;

public interface SBUXFunctionalTestEnvironmentRepository extends BaseCollectorItemRepository<SBUXFunctionalTestEnvrironment>{

	@Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, options.envId : ?2}")
	SBUXFunctionalTestEnvrironment findTestEnvironment(ObjectId collectorId, String instanceUrl, String envId);

   
    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, enabled: true}")
    List<SBUXFunctionalTestEnvrironment> findEnabledTestEnvironments(ObjectId collectorId, String instanceUrl);
}
