
package com.capitalone.dashboard.service;

/**
 * Created by vinod on 25/9/16.
 */

import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.repository.CatalystDeployTaskHistory;
import com.capitalone.dashboard.model.CatalystDeploysTask;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CatalystDeployHistoryServiceImpl implements CatalystDeployHistoryService {
    private final CatalystDeployTaskHistory catalystDeployTaskHistory;
    private static final Logger LOGGER = LoggerFactory.getLogger(CatalystDeployHistoryServiceImpl.class);

    @Autowired
    public CatalystDeployHistoryServiceImpl(CatalystDeployTaskHistory catalystDeployTaskHistory){
        this.catalystDeployTaskHistory = catalystDeployTaskHistory;
    }

    @Override
    public DataResponse<JSONObject> getCatalystDeploymentHistory(String taskId){
        List<CatalystDeploysTask> allCdt = catalystDeployTaskHistory.findByTaskId(taskId);
        JSONArray deployHistory = new JSONArray();
        for(CatalystDeploysTask cdt : allCdt){
            JSONObject cdtobj = new JSONObject();
            cdtobj.put("taskId",cdt.getTaskId());
            cdtobj.put("taskName",cdt.getTaskName());
            cdtobj.put("status",cdt.getStatus());
            cdtobj.put("nodeNames",cdt.getNodeNames());
            cdtobj.put("executedData",cdt.getExecutedDate());
            deployHistory.add(cdtobj);
        }
        JSONObject responseObj = new JSONObject();


        responseObj.put("deploymenthistory",deployHistory);
        return  new DataResponse<>(responseObj,new Date().getTime());
    }

}
