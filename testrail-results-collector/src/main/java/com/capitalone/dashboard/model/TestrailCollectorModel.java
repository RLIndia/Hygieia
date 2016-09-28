package com.capitalone.dashboard.model;

/**
 * Created by vinod on 27/9/16.
 */
public class TestrailCollectorModel extends CollectorItem {
    private static final String PROJECTID = "projectId";
    private static final String PROJECTNAME = "projectName";
    private static final String MILESTONEID = "milestoneId";
    private static final String MILESTONENAME = "milestoneName";

    public String getProjectId() {
            return (String) getOptions().get(PROJECTID);
            }

    public void setProjectId(String projectId) {
            getOptions().put(PROJECTID, projectId);
            }

    public String getProjectname() {
            return (String) getOptions().get(PROJECTNAME);
            }

    public void setProjectname(String projectName) {
            getOptions().put(PROJECTNAME, projectName);
            }

    public String getMilestoneId() {
            return (String) getOptions().get(MILESTONEID);
            }

    public void setMilestoneId(String milestoneId) {
            getOptions().put(MILESTONEID, milestoneId);
            }

    public String getMilestonename() {
            return (String) getOptions().get(MILESTONENAME);
            }

    public void setMilestonename(String milestoneName) {
            getOptions().put(MILESTONENAME, milestoneName);
            }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestrailCollectorModel that = (TestrailCollectorModel) o;
        return getProjectId().equals(that.getProjectId()) && getMilestoneId().equals(that.getMilestoneId());
    }

    @Override
    public int hashCode() {
        int result = getProjectname().hashCode();
        result = 31 * result + getMilestoneId().hashCode();
        return result;
    }


}
