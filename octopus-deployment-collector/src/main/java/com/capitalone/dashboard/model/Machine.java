package com.capitalone.dashboard.model;

public class Machine {
	private String machineId;
	private String machineName;
	private String hostName;
	private boolean status;
	private String[] roles;
	private String enviromentId;


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
	public String[] getRoles() {
		return roles;
	}
	public void setRoles(String[] roles) {
		this.roles = roles;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
}
