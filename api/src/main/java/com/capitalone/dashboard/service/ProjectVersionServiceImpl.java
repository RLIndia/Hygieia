package com.capitalone.dashboard.service;

import java.util.Iterator;

/**
 * Created by root on 12/9/16.
 */

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.capitalone.dashboard.repository.FunctionalTestResultRepository;
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
import com.capitalone.dashboard.model.DefectInjection;
import com.capitalone.dashboard.model.ProjectVersionIssues;
import com.capitalone.dashboard.model.SprintVelocity;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.DefectInjectsRepository;
import com.capitalone.dashboard.repository.ProjectVersionRepository;
import com.capitalone.dashboard.repository.SprintVelocityRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProjectVersionServiceImpl implements ProjectVersionService {
	private final ComponentRepository componentRepository;
	private final CollectorRepository collectorRepository;
	private final ProjectVersionRepository projectVersionRepository;
	private final CollectorItemRepository collectorItemRepository;
	private final SprintVelocityRepository sprintVelocityRepository;
	private final DefectInjectsRepository defectInjectsRepository;
	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectVersionServiceImpl.class);

	@Autowired
	public ProjectVersionServiceImpl(ComponentRepository componentRepository, CollectorRepository collectorRepository,
			ProjectVersionRepository projectVersionRepository, SprintVelocityRepository sprintVelocityRepository, 
			CollectorItemRepository collectorItemRepository,DefectInjectsRepository defectInjectsRepository) {
		this.collectorRepository = collectorRepository;
		this.componentRepository = componentRepository;
		this.projectVersionRepository = projectVersionRepository;
		this.collectorItemRepository = collectorItemRepository;
		this.sprintVelocityRepository =sprintVelocityRepository;
		this.defectInjectsRepository = defectInjectsRepository;
	}

	@SuppressWarnings("unchecked")
	@Override
	public DataResponse<JSONObject> getProjectVersionIssues(ObjectId componentId) {

		Component component = componentRepository.findOne(componentId);
		CollectorItem item = component.getCollectorItems().get(CollectorType.Jiraproject).get(0);
		LOGGER.info("componentId");
		LOGGER.info(componentId.toString());
		LOGGER.info(item.getCollectorId().toString());

		LOGGER.info("itemId==>" + item.getId());

		item = collectorItemRepository.findOne(item.getId());

		String versionId = (String) item.getOptions().get("versionId");
		String sprintId = (String) item.getOptions().get("activeSprintId");
		String projectId = (String) item.getOptions().get("projectId");	
		String stageDefects = (String) item.getOptions().get("stageDefectsCnt");
		String prodDefects = (String)item.getOptions().get("prodDefectsCnt");
		

		LOGGER.info("versionId ===>" + versionId);
		LOGGER.info("Sprint Id==>" + sprintId);
		JSONObject responseObj = new JSONObject();
		List<ProjectVersionIssues> pvi = projectVersionRepository
				.findByCollectorItemIdAndVersionId(item.getCollectorId(), versionId);

		// //To Do after the first fetch by Collector.
		// responseObj.put("issues",pvi);

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
			
		
			//JSONArray coverageArray = new JSONArray();
			JSONObject coverageObj = new JSONObject();
			coverageObj.put("covered",cntStryAccptCriteria);
			coverageObj.put("notCovered",issues.size()-cntStryAccptCriteria);	
			coverageObj.put("Total", issues.size());		
			
			responseObj.put("acceptance", coverageObj);
			
			
			List<SprintVelocity> pviSprintVel = sprintVelocityRepository
					.findVelocityReport(item.getCollectorId(), versionId,projectId);
			
			JSONArray velocities = new JSONArray();
			JSONArray sprintStoryPointsArray= new JSONArray();			
			JSONArray midStoryPointsArray= new JSONArray();

			boolean isfirstRec=true;
			JSONObject summaryVel = new JSONObject();
			for (SprintVelocity velocity : pviSprintVel) {
				JSONObject velocityObj = new JSONObject();
				JSONObject sprintPointsObj = new JSONObject();
				JSONObject midSprintPointsObj = new JSONObject();
				// push the first issues projectname and versionname to the summary
				// object
				if (isfirstRec){
					summaryVel.put("projectId", velocity.getProjectId());
					summaryVel.put("versionId", velocity.getVersionId());
					isfirstRec=false;
				}
				velocityObj.put("SprintId", velocity.getSprintId());
				velocityObj.put("SprintName", velocity.getSprintName());
				velocityObj.put("SprintStatus", velocity.getSprintStatus());
				velocityObj.put("Committed", velocity.getAllIssuesSum());
				if(velocity.getCompletedSum().equals("") || velocity.getCompletedSum()==null || velocity.getCompletedSum().equals("null"))
					velocity.setCompletedSum("0.0");
				if(velocity.getOutOfSprintSum().equals("") || velocity.getOutOfSprintSum()==null || velocity.getOutOfSprintSum().equals("null"))
					velocity.setOutOfSprintSum("0.0");
				velocityObj.put("Completed", Double.parseDouble(velocity.getCompletedSum())+Double.parseDouble(velocity.getOutOfSprintSum()));
				velocities.add(velocityObj);	
				
				sprintPointsObj.put("SprintName", velocity.getSprintName());
				sprintPointsObj.put("StoryCount", velocity.getStoryCount());
				sprintPointsObj.put("StoryPoints", velocity.getCompleted());
				sprintStoryPointsArray.add(sprintPointsObj);
				
				midSprintPointsObj.put("SprintName", velocity.getSprintName());
				midSprintPointsObj.put("MidSprintPoints", velocity.getMidPointSum());
				midStoryPointsArray.add(midSprintPointsObj);				
			}
			
			List<DefectInjection> diList = defectInjectsRepository.findDefectInjection(item.getCollectorId(),(String) item.getOptions().get("projectId"),
					(String) item.getOptions().get("versionId"));
			
			JSONArray defectInjection = new JSONArray();
			for(DefectInjection di : diList)
			{
				JSONObject diObj = new JSONObject();
				diObj.put("SprintName", di.getSprintName());			
				if(di.getAchievedPoints() != 0.0 || di.getAchievedPoints() != 0)
				{
				  diObj.put("InjectionRatio", di.getDefectCount()/di.getAchievedPoints());
				}
				else
				{
					 diObj.put("InjectionRatio",0);
				}
				
				defectInjection.add(diObj);
			}

			
			JSONObject slippageObj = new JSONObject();
			
			slippageObj.put("QA",stageDefects);
			slippageObj.put("Production",prodDefects);
			if(prodDefects != null && stageDefects != null)
			{
			Double denominator = (Double.parseDouble(prodDefects)+Double.parseDouble(stageDefects));			
			if(denominator != 0.0)
			{
			Double ratio = (double) (Math.round((Double.parseDouble(prodDefects)/(Double.parseDouble(prodDefects)+Double.parseDouble(stageDefects)))*100));
			slippageObj.put("Ratio",ratio);
			}
			else
			{
			slippageObj.put("Ratio",0);
			}
			}
			else
			{
				slippageObj.put("Ratio",0);
			}
			
			
			
			

		//	responseObj.put("version", summary);

			responseObj.put("teamVelocity", velocities);
			
			responseObj.put("defectSlippageRate",slippageObj);			
			
			responseObj.put("defectInjectionRate",defectInjection);
			
			responseObj.put("IssueStoryPoints", sprintStoryPointsArray);
			
			responseObj.put("MidSprintPoints", midStoryPointsArray);
			

		Collector collector = collectorRepository.findOne(item.getCollectorId());

		return new DataResponse<>(responseObj, collector.getLastExecuted());
	}

}
