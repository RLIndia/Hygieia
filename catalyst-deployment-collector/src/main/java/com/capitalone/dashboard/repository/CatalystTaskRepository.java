package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.CatalystRepo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * Created by root on 19/9/16.
 */
public interface CatalystTaskRepository  extends BaseCollectorItemRepository<CatalystRepo> {

    @Query(value="{ 'collectorId' : ?0, options.taskId : ?1}")
    CatalystRepo findCatalystRepo(ObjectId collectorId, String taskId);

    @Query(value="{ 'collectorId' : ?0, enabled: true}")
    List<CatalystRepo> findEnabledCatalystRepos(ObjectId collectorId);
}


