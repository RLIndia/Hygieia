package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.CatalystRepo;
import com.capitalone.dashboard.model.CatalystDeploys;
import java.util.List;
/**
 * Created by Vinod on 19/9/16.
 */
public interface CatalystClient {

    List<CatalystRepo> getCatalystRepos();
    List<CatalystDeploys> getCatalystDeploys(CatalystRepo catalystRepo,boolean firstrun);

}
