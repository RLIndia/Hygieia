package com.capitalone.dashboard.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.capitalone.dashboard.collector.DefaultJiraClient;

/**
 * Created by Vinod on 6/9/16.
 */
public class JiraRepo extends CollectorItem{
	
	private static final Log LOG = LogFactory.getLog(DefaultJiraClient.class);

    private static final String PROJECTNAME = "projectName";
    private static final String PROJECTID = "projectId";
    private static final String VERSIONID = "versionId";
    private static final String VERSIONNAME = "versionName";
    private static final String VERSIONDESCRIPTION = "versionDescription";
    private static final String ACTIVE_SPRINT_ID = "activeSprintId";
    private static final String ACTIVE_SPRINT_NAME = "activeSprintName";
    private static final String ACTIVE_SPRINT_START_TIME = "activeSprintStartTime";
    private static final String ACTIVE_SPRINT_END_TIME = "activeSprintEndTime";
    private static final String RAPIDVIEW_ID="rapidViewId";
    private static String STAGE_DEFECTS="stageDefectsCnt";
    private static String PROD_DEFECTS="prodDefectsCnt"; 
    
    
  
	public String getStageDefects() {
		return  (String) getOptions().get(STAGE_DEFECTS);
	}

	public void setStageDefects(String stageDefects) {
		 getOptions().put(STAGE_DEFECTS, stageDefects);
	}

	public String getProdDefects() {
		return  (String) getOptions().get(PROD_DEFECTS);
	}

	public void setProdDefects(String prodDefects) {
		 getOptions().put(PROD_DEFECTS, prodDefects);
	}
	

	public String getACTIVE_SPRINT_START_TIME() { return (String) getOptions().get(ACTIVE_SPRINT_START_TIME);}

    public void setACTIVE_SPRINT_START_TIME(String startTime){
        getOptions().put(ACTIVE_SPRINT_START_TIME, startTime);
    }
    public String geACTIVE_SPRINT_END_TIME() { return (String) getOptions().get(ACTIVE_SPRINT_END_TIME);}

    public void setACTIVE_SPRINT_END_TIME(String endTime){
        getOptions().put(ACTIVE_SPRINT_END_TIME, endTime);
    }

    public String getACTIVE_SPRINT_ID() {
    	LOG.error((String) getOptions().get(ACTIVE_SPRINT_ID));
    	return (String) getOptions().get(ACTIVE_SPRINT_ID);
    	}

    public void setACTIVE_SPRINT_ID(String sprintId){
        getOptions().put(ACTIVE_SPRINT_ID, sprintId);
    }
    
    public String getACTIVE_SPRINT_NAME() { return (String) getOptions().get(ACTIVE_SPRINT_NAME);}

    public void setACTIVE_SPRINT_NAME(String sprintName){
        getOptions().put(ACTIVE_SPRINT_NAME, sprintName);
    }
    
    
    public String getPROJECTID() { return (String) getOptions().get(PROJECTID);}

    public void setPROJECTID(String projectid){
        getOptions().put(PROJECTID, projectid);
    }

    public String getVERSIONID() { return (String) getOptions().get(VERSIONID);}

    public void setVERSIONID(String versionid){
        getOptions().put(VERSIONID, versionid);
    }

    public String getPROJECTNAME() {
        return (String) getOptions().get(PROJECTNAME);
    }

    public void setPROJECTNAME(String projectname){
        getOptions().put(PROJECTNAME, projectname);
    }

    public String getVERSIONNAME() {
        return (String) getOptions().get(VERSIONDESCRIPTION);
    }

    public void setVERSIONNAME(String versionname){
        getOptions().put(VERSIONNAME,versionname);
    }

    public String getVERSIONDESCRIPTION() {
        return (String) getOptions().get(VERSIONDESCRIPTION);
    }

    public void setVERSIONDESCRIPTION(String versiondescription){
        getOptions().put(VERSIONDESCRIPTION,versiondescription);
    }
    public String getRAPIDVIEWID() {
        return (String) getOptions().get(RAPIDVIEW_ID);
    }

    public void setRAPIDVIEWID(String rapidViewId){
       getOptions().put(RAPIDVIEW_ID,rapidViewId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JiraRepo jirarepo = (JiraRepo) o;

        return (getPROJECTID().equals(jirarepo.getPROJECTID()) && getVERSIONID().equals(jirarepo.getVERSIONID()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getPROJECTNAME() == null) ? 0 : getPROJECTNAME().hashCode());
        result = prime * result + ((getVERSIONNAME() == null) ? 0 : getVERSIONNAME().hashCode());
        return result;
    }

}
