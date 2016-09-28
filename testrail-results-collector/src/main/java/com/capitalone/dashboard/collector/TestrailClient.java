package com.capitalone.dashboard.collector;

/**
 * Created by vinod on 27/9/16.
 */

import java.io.IOException;
import java.util.List;
import com.capitalone.dashboard.model.TestrailCollectorModel;

public interface TestrailClient {
    List<TestrailCollectorModel> getTestrailProjects() throws IOException, TRAPIException;
}
