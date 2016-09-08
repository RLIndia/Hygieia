package com.capitalone.dashboard.model;

/**
 * Created by Vinod on 6/9/16.
 */
public class JiraRepo extends CollectorItem{

    private static final String PROJECTNAME = "projectname";
    private static final String VERSIONNAME = "versionname";
    private static final String VERSIONDESCRIPTION = "versiondescription";


    public String getPROJECTNAME() {
        return (String) getOptions().get("projectname");
    }

    public void setPROJECTNAME(String projectname){
        getOptions().put(PROJECTNAME, projectname);
    }

    public String getVERSIONNAME() {
        return (String) getOptions().get("versionname");
    }

    public void setVERSIONNAME(String versionname){
        getOptions().put(VERSIONNAME,versionname);
    }

    public String getVERSIONDESCRIPTION() {
        return (String) getOptions().get("versiondescription");
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

        return getPROJECTNAME().equals(jirarepo.getPROJECTNAME());
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
