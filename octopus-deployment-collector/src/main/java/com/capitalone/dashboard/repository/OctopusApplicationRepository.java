package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import com.capitalone.dashboard.model.OctopusApplication;



public interface OctopusApplicationRepository extends BaseCollectorItemRepository<OctopusApplication>{

    
    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, options.applicationId : ?2}")
    OctopusApplication findUDeployApplication(ObjectId collectorId, String instanceUrl, String applicationId);

   
    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, enabled: true}")
    List<OctopusApplication> findEnabledApplications(ObjectId collectorId, String instanceUrl);

}
