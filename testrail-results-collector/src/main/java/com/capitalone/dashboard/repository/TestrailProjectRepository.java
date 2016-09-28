package com.capitalone.dashboard.repository;

/**
 * Created by vinod on 27/9/16.
 */

import java.util.List;

import com.capitalone.dashboard.model.CollectorItem;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import com.capitalone.dashboard.model.TestrailCollectorModel;
public interface TestrailProjectRepository extends BaseCollectorItemRepository<TestrailCollectorModel>{

    @Query(value="{ 'collectorId' : ?0}")
    List<TestrailCollectorModel> getAllTestrailProjects(ObjectId collectorId);

    @Query(value="{ 'collectorId' : ?0, enabled: true}")
    List<TestrailCollectorModel> findEnabledTestrailProjects(ObjectId collectorId);

    @Query(value="{ 'collectorId' : ?0, options.projectId : ?1, options.milestoneId : ?2}")
    TestrailCollectorModel findCollectorItemByProjectandMilestone(ObjectId collectorId,String projectId,String milestoneId);
}
