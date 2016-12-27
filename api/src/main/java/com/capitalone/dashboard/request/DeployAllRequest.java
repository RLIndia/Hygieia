package com.capitalone.dashboard.request;

/**
 * Created by vnair on 12/27/16.
 */
import org.bson.types.ObjectId;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class DeployAllRequest {
    public List<String> getEnvIds() {
        return envIds;
    }

    public void setEnvIds(List<String> envIds) {
        this.envIds = envIds;
    }

    @NotNull
    private List<String> envIds = new ArrayList<>();
}
