package com.capitalone.dashboard.collector;

import java.util.List;
import com.capitalone.dashboard.model.FunctionalTestResult;
import com.capitalone.dashboard.model.SBUXFunctionalTestEnvrironment;

public interface SBUXClient{
	
	List<SBUXFunctionalTestEnvrironment> getFunctionalTestEnvironment();
	List<FunctionalTestResult> getFunctionalTestResults(SBUXFunctionalTestEnvrironment envId);

}
