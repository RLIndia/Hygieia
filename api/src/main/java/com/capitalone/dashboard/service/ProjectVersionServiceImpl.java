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
import com.capitalone.dashboard.model.ProjectVersionIssues;
import com.capitalone.dashboard.model.SprintVelocity;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
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
	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectVersionServiceImpl.class);

	@Autowired
	public ProjectVersionServiceImpl(ComponentRepository componentRepository, CollectorRepository collectorRepository,
			ProjectVersionRepository projectVersionRepository, SprintVelocityRepository sprintVelocityRepository, 
			CollectorItemRepository collectorItemRepository) {
		this.collectorRepository = collectorRepository;
		this.componentRepository = componentRepository;
		this.projectVersionRepository = projectVersionRepository;
		this.collectorItemRepository = collectorItemRepository;
		this.sprintVelocityRepository =sprintVelocityRepository;
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
		int cntStryWoAccptCriteria=0;
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
						if ("Story".equals(issue.getIssueType()) && issue.getAcceptanceCriteria()== null)
								cntStryWoAccptCriteria++;
							if ("Story".equals(issue.getIssueType()) && issue.getDefectsCnt()>0)
								defectsOfStory++;
							if ("Defect".equals(issue.getIssueType()) || "Bug".equals(issue.getIssueType())  )
								defectsCnt++;


				sprintCommitted+=issue.getStoryPoint();			
				if ("Story".equals(issue.getIssueType()) && issue.getAcceptanceCriteria()== null)
					cntStryWoAccptCriteria++;
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
			summarySprint.put("IssuesWithoutAcceptanceCriteria",cntStryWoAccptCriteria);
			summarySprint.put("storydefect",defectsOfStory);
			summarySprint.put("DefectsInSprint",defectsCnt);
			summarySprint.put("storyPointsEarned",sprintCompleted);
			summarySprint.put("issues", issuesSprint);

			responseObj.put("sprint", summarySprint);
		}
			
			JSONObject coverageObj = new JSONObject();
			coverageObj.put("notCovered",cntStryWoAccptCriteria);
			coverageObj.put("covered",issues.size()-cntStryWoAccptCriteria);			
			
			
			responseObj.put("acceptance", coverageObj);
			
			
			List<SprintVelocity> pviSprintVel = sprintVelocityRepository
					.findVelocityReport(item.getCollectorId(), versionId,projectId);
			
			JSONArray velocities = new JSONArray();

			boolean isfirstRec=true;
			JSONObject summaryVel = new JSONObject();
			for (SprintVelocity velocity : pviSprintVel) {
				JSONObject velocityObj = new JSONObject();
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
				velocityObj.put("Committed", velocity.getCommitted());
				velocityObj.put("Completed", velocity.getCompleted());

				velocities.add(velocityObj);	

				
			}

			JSONArray defectSlippage = new JSONArray();
			JSONObject slippageObj = new JSONObject();
			slippageObj.put("QA",100);
			slippageObj.put("Production",15);
			defectSlippage.add(slippageObj);
			
			
			JSONArray releaseStatus = new JSONArray();
			JSONObject releaseObj = new JSONObject();
			JSONArray releaseSprintData = new JSONArray();
			
			JSONObject releaseObj2 = new JSONObject();
			JSONArray releaseSprintData2 = new JSONArray();
			
			releaseObj.put("SprintName", "SLZ2.1Sprint4");
			JSONObject releaseSprintDataPoints1 = new JSONObject();
			releaseSprintDataPoints1.put("JAN 1","35");
			releaseSprintData.add(releaseSprintDataPoints1);
			JSONObject releaseSprintDataPoints2 = new JSONObject();
			releaseSprintDataPoints2.put("JAN 2","25");
			releaseSprintData.add(releaseSprintDataPoints2);
			JSONObject releaseSprintDataPoints3 = new JSONObject();
			releaseSprintDataPoints3.put("JAN 3","15");
			releaseSprintData.add(releaseSprintDataPoints3);
			JSONObject releaseSprintDataPoints4 = new JSONObject();
			releaseSprintDataPoints4.put("JAN 4","10");
			releaseSprintData.add(releaseSprintDataPoints4);
			JSONObject releaseSprintDataPoints5 = new JSONObject();
			releaseSprintDataPoints5.put("JAN 5","5");
			releaseSprintData.add(releaseSprintDataPoints5);			
			releaseObj.put("SprintData", releaseSprintData);
			
			releaseStatus.add(releaseObj);
			
			releaseObj2.put("SprintName", "PSI-SLZ-13.2");
			JSONObject releaseSprintDataPoints21 = new JSONObject();
			releaseSprintDataPoints21.put("JAN 1","31");
			releaseSprintData.add(releaseSprintDataPoints21);
			JSONObject releaseSprintDataPoints22 = new JSONObject();
			releaseSprintDataPoints22.put("JAN 2","20");
			releaseSprintData.add(releaseSprintDataPoints22);
			JSONObject releaseSprintDataPoints23 = new JSONObject();
			releaseSprintDataPoints23.put("JAN 3","15");
			releaseSprintData.add(releaseSprintDataPoints23);
			JSONObject releaseSprintDataPoints24 = new JSONObject();
			releaseSprintDataPoints24.put("JAN 4","17");
			releaseSprintData.add(releaseSprintDataPoints24);
			JSONObject releaseSprintDataPoints25 = new JSONObject();
			releaseSprintDataPoints25.put("JAN 5","4");
			releaseSprintData2.add(releaseSprintDataPoints25);			
			releaseObj2.put("SprintData", releaseSprintData2);
			
			releaseStatus.add(releaseObj2);
			
			
			JSONArray burnDownChart = new JSONArray();
			JSONObject burnDownObj = new JSONObject();
			JSONArray burnDownData = new JSONArray();
			
			JSONObject burnDownObj2 = new JSONObject();
			JSONArray burnDownData2 = new JSONArray();
			
			burnDownObj.put("SprintName", "SLZ2.1Sprint4");
			JSONObject burnDownDataPoints1 = new JSONObject();			
			burnDownDataPoints1.put("actual values","8");
			burnDownDataPoints1.put("estimated values","9");
			JSONObject burnDownDataPoints1Date = new JSONObject();	
			burnDownDataPoints1Date.put("JAN 1",burnDownDataPoints1);			
			burnDownData.add(burnDownDataPoints1Date);
			
			JSONObject burnDownDataPoints2 = new JSONObject();
			burnDownDataPoints2.put("actual values","9");
			burnDownDataPoints2.put("estimated values","11");
			JSONObject burnDownDataPoints2Date = new JSONObject();	
			burnDownDataPoints2Date.put("JAN 1",burnDownDataPoints2);			
			burnDownData.add(burnDownDataPoints2Date);
			
			JSONObject burnDownDataPoints3 = new JSONObject();
			burnDownDataPoints3.put("actual values","5");
			burnDownDataPoints3.put("estimated values","8");
			JSONObject burnDownDataPoints3Date = new JSONObject();	
			burnDownDataPoints3Date.put("JAN 1",burnDownDataPoints3);			
			burnDownData.add(burnDownDataPoints3Date);
			
			JSONObject burnDownDataPoints4 = new JSONObject();
			burnDownDataPoints4.put("actual values","8");
			burnDownDataPoints4.put("estimated values","8");
			JSONObject burnDownDataPoints4Date = new JSONObject();	
			burnDownDataPoints4Date.put("JAN 1",burnDownDataPoints4);			
			burnDownData.add(burnDownDataPoints4Date);
			
			JSONObject burnDownDataPoints5 = new JSONObject();
			burnDownDataPoints5.put("actual values","6");
			burnDownDataPoints5.put("estimated values","9");
			JSONObject burnDownDataPoints5Date = new JSONObject();	
			burnDownDataPoints5Date.put("JAN 1",burnDownDataPoints5);			
			burnDownData.add(burnDownDataPoints5Date);	
			
			burnDownObj.put("SprintData", burnDownData);
			
			burnDownChart.add(burnDownObj);
			
			burnDownObj2.put("SprintName", "PSI-SLZ-13.2");
			JSONObject burnDownDataPoints21 = new JSONObject();
			burnDownDataPoints21.put("actual values","8");
			burnDownDataPoints21.put("estimated values","11");
			JSONObject burnDownDataPoints21Date = new JSONObject();	
			burnDownDataPoints21Date.put("JAN 1",burnDownDataPoints21);			
			burnDownData2.add(burnDownDataPoints21Date);	
			
			JSONObject burnDownDataPoints22 = new JSONObject();
			burnDownDataPoints22.put("actual values","9");
			burnDownDataPoints22.put("estimated values","10");
			JSONObject burnDownDataPoints22Date = new JSONObject();	
			burnDownDataPoints22Date.put("JAN 1",burnDownDataPoints22);			
			burnDownData2.add(burnDownDataPoints22Date);	
			
			JSONObject burnDownDataPoints23 = new JSONObject();
			burnDownDataPoints23.put("actual values","7");
			burnDownDataPoints23.put("estimated values","8");
			JSONObject burnDownDataPoints23Date = new JSONObject();	
			burnDownDataPoints23Date.put("JAN 1",burnDownDataPoints23);			
			burnDownData2.add(burnDownDataPoints23Date);	
			
			JSONObject burnDownDataPoints24 = new JSONObject();
			burnDownDataPoints24.put("actual values","8");
			burnDownDataPoints24.put("estimated values","12");
			JSONObject burnDownDataPoints24Date = new JSONObject();	
			burnDownDataPoints24Date.put("JAN 1",burnDownDataPoints24);			
			burnDownData2.add(burnDownDataPoints24Date);	
			
			JSONObject burnDownDataPoints25 = new JSONObject();
			burnDownDataPoints25.put("actual values","7");
			burnDownDataPoints25.put("estimated values","9");
			JSONObject burnDownDataPoints25Date = new JSONObject();	
			burnDownDataPoints25Date.put("JAN 1",burnDownDataPoints25);			
			burnDownData2.add(burnDownDataPoints25Date);		
			
			burnDownObj2.put("SprintData", burnDownData2);
			
			burnDownChart.add(burnDownObj2);
			
			JSONArray defectInjection = new JSONArray();
			JSONObject dIObj = new JSONObject();
			JSONArray diObjData = new JSONArray();
			
			JSONObject dIObj2 = new JSONObject();
			JSONArray diObjData2 = new JSONArray();
			
			dIObj.put("SprintName", "SLZ2.1Sprint4");
			JSONObject dIObjDataPoints1 = new JSONObject();
			dIObjDataPoints1.put("US 1","35");
			diObjData.add(dIObjDataPoints1);
			JSONObject dIObjDataPoints2 = new JSONObject();
			dIObjDataPoints2.put("US 2","25");
			diObjData.add(dIObjDataPoints2);
			JSONObject dIObjDataPoints3 = new JSONObject();
			dIObjDataPoints3.put("US 3","15");
			diObjData.add(dIObjDataPoints3);
			JSONObject dIObjDataPoints4 = new JSONObject();
			dIObjDataPoints4.put("US 4","10");
			diObjData.add(dIObjDataPoints4);
			JSONObject dIObjDataPoints5 = new JSONObject();
			dIObjDataPoints5.put("US 5","5");
			diObjData.add(dIObjDataPoints5);			
			dIObj.put("SprintData", diObjData);
			
			defectInjection.add(releaseObj);
			
			dIObj2.put("SprintName", "PSI-SLZ-13.2");
			JSONObject dIObjDataPoints21 = new JSONObject();
			dIObjDataPoints21.put("US 1","55");
			diObjData2.add(dIObjDataPoints21);
			JSONObject dIObjDataPoints22 = new JSONObject();
			dIObjDataPoints22.put("US 2","45");
			diObjData2.add(dIObjDataPoints22);
			JSONObject dIObjDataPoints23 = new JSONObject();
			dIObjDataPoints23.put("US 3","25");
			diObjData2.add(dIObjDataPoints23);
			JSONObject dIObjDataPoints24 = new JSONObject();
			dIObjDataPoints24.put("US 4","16");
			diObjData2.add(dIObjDataPoints24);
			JSONObject dIObjDataPoints25 = new JSONObject();
			dIObjDataPoints25.put("US 5","6");
			diObjData2.add(dIObjDataPoints25);			
			dIObj2.put("SprintData", diObjData2);
			
			defectInjection.add(releaseObj2);			
			

			responseObj.put("version", summary);

			responseObj.put("teamVelocity", velocities);
			
			responseObj.put("defectSlippageRate",defectSlippage);
			
			responseObj.put("releaseStatus",releaseStatus);
			
			responseObj.put("burnDownChart",burnDownChart);
			
			responseObj.put("defectInjectionRate",defectInjection);			

		

		Collector collector = collectorRepository.findOne(item.getCollectorId());

		return new DataResponse<>(responseObj, collector.getLastExecuted());
	}

}
