package com.capitalone.dashboard.collector;

import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.capitalone.dashboard.model.ProjectVersionIssues;
import com.capitalone.dashboard.model.JiraRepo;
import java.util.List;
/**
 * Created by vinod on 6/9/16.
 * This client will fetch all issues for a version
 */
public interface JiraClient {

    /**
     * Fetch all of the commits for the provided GitRepo.
     *
     * @param jirarepo
     * @param firstRun
     * @return all commits in repo
     */

    List<ProjectVersionIssues> getprojectversionissues(JiraRepo jirarepo,  boolean firstrun);
    List<BasicProject> getProjects();
}
