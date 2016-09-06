package com.capitalone.dashboard.model;

/**
 * Created by root on 6/9/16.
 */
public class JiraRepo extends CollectorItem{
        private static final String VERSIONURL = "versionurl";


    public static String getVERSIONURL() {
        return VERSIONURL;
    }

    public void setVersionurl(String versionurl){
        getOptions().put(VERSIONURL,versionurl);
    }

    public static String getPROJECTID() {
        return PROJECTID;
    }

    public void setPROJECTID(String projectid){
        getOptions().put(PROJECTID,projectid);
    }

    public static String getVERSIONNAME() {
        return VERSIONNAME;
    }

    public void setVERSIONNAME(String versionname){
        getOptions().put(VERSIONNAME,versionname);
    }

    public static String getVERSIONDESCRIPTION() {
        return VERSIONDESCRIPTION;
    }

    public void setVERSIONDESCRIPTION(String versiondescription){
        getOptions().put(VERSIONDESCRIPTION,versiondescription);
    }

    public static String getUSERID() {
        return USERID;
    }

    public void setUSERID(String userid){
        getOptions().put(USERID,userid);
    }

    public static String getPASSWORD() {
        return PASSWORD;
    }

    public void setPASSWORD(String password){
        getOptions().put(PASSWORD,password);
    }

    private static final String PROJECTID = "projectid";
        private static final String VERSIONNAME = "versionname";
        private static final String VERSIONDESCRIPTION = "versiondescription";
        private static final String USERID = "userID";
        private static final String PASSWORD = "password";


}
