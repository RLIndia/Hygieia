package com.capitalone.dashboard.collector;

import java.util.List;
import com.capitalone.dashboard.model.FunctionalTestResult;

public interface SBUXClient{
	
	List<FunctionalTestResult> getFunctionalTestResults();

}
