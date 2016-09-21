package com.capitalone.dashboard.model;

/**
 * Created by root on 19/9/16.
 */
public class CatalystRepo extends CollectorItem{
    private static final String REPOSITORYNAME = "repositoryName";

    private static final String ORGID = "orgId";
    private static final String BGID = "bgId";
    private static final String PROJECTID = "projectId";

    private static final String ORGNAME = "orgName";
    private static final String BGNAME = "bgName";
    private static final String PROJECTNAME = "projectName";




    public String getREPOSITORYNAME() { return (String) getOptions().get(REPOSITORYNAME);}

    public void setREPOSITORYNAME(String repositoryname){
        getOptions().put(REPOSITORYNAME, repositoryname);
    }



    public String getORGID() { return (String) getOptions().get(ORGID);}

    public void setORGID(String orgId){
        getOptions().put(ORGID, orgId);
    }



    public String getORGNAME() { return (String) getOptions().get(ORGNAME);}

    public void setORGNAME(String orgName){
        getOptions().put(ORGNAME, orgName);
    }

    public String getBGNAME() { return (String) getOptions().get(BGNAME);}

    public void setBGNAME(String bgName){
        getOptions().put(BGNAME, bgName);
    }

    public String getPROJECTNAME() { return (String) getOptions().get(PROJECTNAME);}

    public void setPROJECTNAME(String projectName){
        getOptions().put(PROJECTNAME, projectName);
    }




    public String getBGID() { return (String) getOptions().get(BGID);}

    public void setBGID(String bgId){
        getOptions().put(BGID, bgId);
    }

    public String getPROJECTID() { return (String) getOptions().get(PROJECTID);}

    public void setPROJECTID(String projectId){
        getOptions().put(PROJECTID, projectId);
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

        return (getPROJECTID().equals(catalystRepo.getPROJECTID()) && getREPOSITORYNAME().equals(catalystRepo.getREPOSITORYNAME()));
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
