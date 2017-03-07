package com.capitalone.dashboard.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.ProjectVersionIssues;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.DefectInjectsRepository;
import com.capitalone.dashboard.repository.ProjectVersionRepository;
import com.capitalone.dashboard.repository.SprintVelocityRepository;

@Service
public class ProjectIssuesServiceImpl implements ProjectIssuesService{
	
	
	private final ComponentRepository componentRepository;
	private final CollectorRepository collectorRepository;
	private final ProjectVersionRepository projectVersionRepository;
	private final CollectorItemRepository collectorItemRepository;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectIssuesServiceImpl.class);
	
	@Autowired
	public ProjectIssuesServiceImpl(ComponentRepository componentRepository, CollectorRepository collectorRepository,
			ProjectVersionRepository projectVersionRepository, SprintVelocityRepository sprintVelocityRepository, 
			CollectorItemRepository collectorItemRepository,DefectInjectsRepository defectInjectsRepository) {
		this.collectorRepository = collectorRepository;
		this.componentRepository = componentRepository;
		this.projectVersionRepository = projectVersionRepository;
		this.collectorItemRepository = collectorItemRepository;
		
	}
	@SuppressWarnings("unchecked")
	@Override
	public DataResponse<JSONObject> getProjectIssues(ObjectId componentId) {
		Component component = componentRepository.findOne(componentId);
		CollectorItem item = component.getCollectorItems().get(CollectorType.Jiraproject).get(0);
		LOGGER.info("componentId");
		LOGGER.info(componentId.toString());
		LOGGER.info(item.getCollectorId().toString());

		LOGGER.info("itemId==>" + item.getId());

		item = collectorItemRepository.findOne(item.getId());
		String versionId = (String) item.getOptions().get("versionId");
		String sprintId = (String) item.getOptions().get("activeSprintId");
		List<ProjectVersionIssues> pvi = projectVersionRepository
				.findByCollectorItemIdAndVersionId(item.getCollectorId(), versionId);
		JSONObject responseObj = new JSONObject();

		JSONArray issues = new JSONArray();

		int doneCount = 0;
		int progressCount = 0;
		int issueCount = 0;
		int pendingCount = 0;
		int cntStryAccptCriteria=0;
		JSONObject summary = new JSONObject();
		for (ProjectVersionIssues issue : pvi) {
			JSONObject issueObj = new JSONObject();
			// push the first issues projectname and versionname to the summary
			// object
			if (issueCount == 0) {
				summary.put("projectName", issue.getProjectName());
				summary.put("versionName", issue.getVersionName());
			}
			issueObj.put("issueID", issue.getIssueId());
			issueObj.put("status", issue.getIssueStatus());
			issueObj.put("statusName", issue.getStatusName());
			issueObj.put("description", issue.getIssueDescription());

			issues.add(issueObj);
			String issueStatus = issue.getIssueStatus();
			if (issueStatus != null) {
				if (issueStatus.contentEquals("Done")) {
					doneCount++;
				}
				if (issueStatus.contentEquals("Backlog")) {
					pendingCount++;
				}
				if (issueStatus.contentEquals("In Progress") || issueStatus.contentEquals("Requirement In Progress")) {
					progressCount++;
				}
			}
			if (issue.getAcceptanceCriteria()!= null)
				cntStryAccptCriteria++;

			issueCount++;
		}

		summary.put("doneCount", doneCount);
		summary.put("issueCount", issueCount);
		summary.put("inprogressCount", progressCount);
		summary.put("pendingCount", pendingCount);
		summary.put("issues", issues);

		responseObj.put("version", summary);
		
		if (sprintId != null) {
			List<ProjectVersionIssues> pviSprints = projectVersionRepository
					.findByCollectorItemIdAndSprintId(item.getCollectorId(), sprintId);

			JSONArray issuesSprint = new JSONArray();

			double doneTotal = 0;
			double progressTotal = 0;
			double pendingTotal = 0;
			double total = 0;
			JSONObject summarySprint = new JSONObject();
			summarySprint.put("sprintName", (String) item.getOptions().get("activeSprintName"));
			summarySprint.put("sprintId", (String) item.getOptions().get("activeSprintId"));
			summarySprint.put("sprintStart", (String) item.getOptions().get("activeSprintStartTime"));
			summarySprint.put("sprintEnd", (String) item.getOptions().get("activeSprintEndTime"));			
 
			double sprintCommitted=0;
			double sprintCompleted=0;			
			int defectsOfStory=0;
			int defectsCnt=0;

			for (ProjectVersionIssues issue : pviSprints) {
				JSONObject issueObj = new JSONObject();

				issueObj.put("issueID", issue.getIssueId());
				issueObj.put("status", issue.getIssueStatus());
				issueObj.put("statusName", issue.getStatusName());
				issueObj.put("description", issue.getIssueDescription());
				issueObj.put("storyPoint", issue.getStoryPoint());

				
				sprintCommitted+=issue.getStoryPoint();		
						
							if ("Story".equals(issue.getIssueType()) && issue.getDefectsCnt()>0)
								defectsOfStory++;
							if ("Defect".equals(issue.getIssueType()) || "Bug".equals(issue.getIssueType())  )
								defectsCnt++;


				sprintCommitted+=issue.getStoryPoint();			
				if ("Story".equals(issue.getIssueType()) && issue.getAcceptanceCriteria()== null)
					cntStryAccptCriteria++;
				if ("Story".equals(issue.getIssueType()) && issue.getDefectsCnt()>0)
					defectsOfStory++;
				if ("Defect".equals(issue.getIssueType()) || "Bug".equals(issue.getIssueType())  )
					defectsCnt++;

				issuesSprint.add(issueObj);
				String issueStatus = issue.getIssueStatus();
				if (issueStatus != null) {
					if (issueStatus.contentEquals("Done")) {
						doneTotal = doneTotal + issue.getStoryPoint();
						sprintCompleted += issue.getStoryPoint();
					}
					if (issueStatus.contentEquals("Backlog")) {
						pendingTotal = pendingTotal + issue.getStoryPoint();
					}
					if (issueStatus.contentEquals("In Progress")
							|| issueStatus.contentEquals("Requirement In Progress")) {
						progressTotal = progressTotal + issue.getStoryPoint();
					}
				}
				total = total + issue.getStoryPoint();
			}
			
			summarySprint.put("doneTotal", doneTotal);
			summarySprint.put("total", total);
			summarySprint.put("progressTotal", progressTotal);
			summarySprint.put("pendingTotal", pendingTotal);
			summarySprint.put("velocityCommitted", sprintCommitted);
			summarySprint.put("velocityCompleted", sprintCompleted);
			summarySprint.put("IssuesWithoutAcceptanceCriteria",cntStryAccptCriteria);
			summarySprint.put("storydefect",defectsOfStory);
			summarySprint.put("DefectsInSprint",defectsCnt);
			summarySprint.put("storyPointsEarned",sprintCompleted);
			summarySprint.put("issues", issuesSprint);

			responseObj.put("sprint", summarySprint);
		}
		
		Collector collector = collectorRepository.findOne(item.getCollectorId());

		return new DataResponse<>(responseObj, collector.getLastExecuted());
		
	}
	
	

}
