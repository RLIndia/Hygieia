package com.capitalone.dashboard.repository;


import java.util.List;

import com.capitalone.dashboard.model.OctopusEnvironment;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;


/**
 * Created by vinod on 12/5/16.
 */
public interface OctopusEnvironmentRepository extends BaseCollectorItemRepository<OctopusEnvironment>{
    @Query(value="{ 'collectorId' : ?0, options.envId : ?1, options.envName : ?2}")
    OctopusEnvironment findOctopusEnvironment(ObjectId collectorId, String envId, String envName);


    @Query(value="{ 'collectorId' : ?0, enabled: true}")
    List<OctopusEnvironment> findEnabledEnvironments(ObjectId collectorId);



}
