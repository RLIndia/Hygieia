package com.capitalone.dashboard.model;

/**
 * Created by Vinod on 6/9/16.
 */
public class JiraRepo extends CollectorItem{

    private static final String PROJECTNAME = "projectName";
    private static final String PROJECTID = "projectId";
    private static final String VERSIONID = "versionId";
    private static final String VERSIONNAME = "versionName";
    private static final String VERSIONDESCRIPTION = "versionDescription";

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
