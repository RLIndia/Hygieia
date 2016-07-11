package com.capitalone.dashboard.model;

public class Machine {
	private String machineId;
	private String machineName;
	private boolean status;
	
	
	public String getMachineId() {
		return machineId;
	}
	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}
	public String getMachineName() {
		return machineName;
	}
	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getEnviromentId() {
		return enviromentId;
	}
	public void setEnviromentId(String enviromentId) {
		this.enviromentId = enviromentId;
	}
	private String enviromentId;
	

}
