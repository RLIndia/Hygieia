package com.capitalone.dashboard.model;

import java.util.List;
import java.util.Map;

public class CommitDataResponse {

	private final Map<String, List<Commit>> result;
	private final long lastUpdated;

	public CommitDataResponse(Map<String, List<Commit>> result, long lastUpdated) {
		this.result = result;
		this.lastUpdated = lastUpdated;
	}

	public long getLastUpdated() {
		return lastUpdated;
	}

	public Map<String, List<Commit>> getResult() {
		return result;
	}
}
