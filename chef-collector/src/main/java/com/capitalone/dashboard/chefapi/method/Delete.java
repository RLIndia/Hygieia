package com.capitalone.dashboard.chefapi.method;

import org.apache.commons.httpclient.HttpMethod;

public class Delete extends ApiMethod{

	public Delete(HttpMethod method) {
		super("DELETE");
		this.method = method;
	}

}
