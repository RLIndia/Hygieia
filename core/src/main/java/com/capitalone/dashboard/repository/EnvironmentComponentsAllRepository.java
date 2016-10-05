package com.capitalone.dashboard.repository;

/**
 * Created by vinod on 5/10/16.
 */
import com.capitalone.dashboard.model.EnvironmentComponentsAll;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
public interface EnvironmentComponentsAllRepository extends CrudRepository<EnvironmentComponentsAll, ObjectId>{
    @Query(value="{ collectorId : ?0}")
    List<EnvironmentComponentsAll> findComponent(ObjectId collectorId);
}
