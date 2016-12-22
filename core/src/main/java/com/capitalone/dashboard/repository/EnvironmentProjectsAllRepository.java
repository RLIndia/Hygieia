package com.capitalone.dashboard.repository;

/**
 * Created by vinod on 5/10/16.
 */
import com.capitalone.dashboard.model.EnvironmentProjectsAll;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
public interface EnvironmentProjectsAllRepository extends CrudRepository<EnvironmentProjectsAll, ObjectId>{
    @Query(value="{ collectorId : ?0, projectId : ?1}")
    List<EnvironmentProjectsAll> findItemForProject(ObjectId collectorId,String projectId);

    @Query(value="{}")
    List<EnvironmentProjectsAll> findProjectsAll();
}
