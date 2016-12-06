package com.capitalone.dashboard.model;



public class OctopusEnvironmentCollector extends Collector{
    public static OctopusEnvironmentCollector prototype() {
        OctopusEnvironmentCollector protoType = new OctopusEnvironmentCollector();
        protoType.setName("OctopusEnvironment");
        protoType.setCollectorType(CollectorType.DeploymentEnvironment);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        return protoType;
    }

}
