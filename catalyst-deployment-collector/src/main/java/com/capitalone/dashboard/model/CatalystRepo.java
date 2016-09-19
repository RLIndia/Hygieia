package com.capitalone.dashboard.model;

/**
 * Created by root on 19/9/16.
 */
public class CatalystRepo extends CollectorItem{
    private static final String TASKNAME = "projectName";
    private static final String TASKID = "taskId";
    private static final String ORGID = "orgId";
    private static final String BGID = "bgId";
    private static final String PROJECTID = "projectId";
    private static final String TASKTYPE = "taskType";
    private static final String TASKDESCRIPTION = "taskDescription";

    public String getTASKNAME() { return (String) getOptions().get(TASKNAME);}

    public void setTASKNAME(String taskname){
        getOptions().put(TASKNAME, taskname);
    }

    public String getTASKID() { return (String) getOptions().get(TASKID);}

    public void setTASKID(String taskId){
        getOptions().put(TASKID, taskId);
    }


    public String getORGID() { return (String) getOptions().get(ORGID);}

    public void setORGID(String orgId){
        getOptions().put(ORGID, orgId);
    }

    public String getBGID() { return (String) getOptions().get(BGID);}

    public void setBGID(String bgId){
        getOptions().put(BGID, bgId);
    }

    public String getPROJECTID() { return (String) getOptions().get(PROJECTID);}

    public void setPROJECTID(String projectId){
        getOptions().put(PROJECTID, projectId);
    }

    public String getTASKTYPE() { return (String) getOptions().get(TASKTYPE);}

    public void setTASKTYPE(String taskId){
        getOptions().put(TASKTYPE, taskId);
    }

    public String getTASKDESCRIPTION() { return (String) getOptions().get(TASKDESCRIPTION);}

    public void setTASKDESCRIPTION(String taskDescription){
        getOptions().put(TASKDESCRIPTION, taskDescription);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CatalystRepo catalystRepo = (CatalystRepo) o;

        return (getPROJECTID().equals(catalystRepo.getPROJECTID()) && getTASKID().equals(catalystRepo.getTASKID()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getPROJECTID() == null) ? 0 : getPROJECTID().hashCode());
        result = prime * result + ((getPROJECTID() == null) ? 0 : getPROJECTID().hashCode());
        return result;
    }


}
