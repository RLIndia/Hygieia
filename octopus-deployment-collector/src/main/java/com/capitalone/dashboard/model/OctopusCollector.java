package com.capitalone.dashboard.model;



public class OctopusCollector extends Collector{
	public static OctopusCollector prototype() {
		OctopusCollector protoType = new OctopusCollector();
        protoType.setName("Octopus");
        protoType.setCollectorType(CollectorType.Deployment);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        return protoType;
    }

}
