(function () {
	'use strict';

	angular
	.module(HygieiaConfig.module)
	.controller('projectVersionViewController', projectVersionViewController);

	projectVersionViewController.$inject = ['$scope', 'DashStatus', 'projectData', 'DisplayState', '$q', '$modal'];
	function projectVersionViewController($scope, DashStatus, projectData, DisplayState, $q, $modal) {
		/*jshint validthis:true */
		var ctrl = this;

		// public variables
		ctrl.statuses = DashStatus;
        ctrl.projectData = projectData;
		ctrl.load = load;
		ctrl.jiraDashboard = {};

		//ctrl.showDetail = showDetail;
		ctrl.title = "";

		function load() {
			var deferred = $q.defer();
			console.log("In Load...");
			projectData.details($scope.widgetConfig.componentId).then(function(data){

				processProjectData(data);
				deferred.resolve(data.lastUpdated);
			
				console.log("Done Load");

			});
			return deferred.promise;
		}
		function processProjectData(data) {
		  console.log("In process project data");
		  
	      ctrl.jiraDashboard = {
	      		"issueSummary" :data.result.summary,
	      		"issues":data.result.issues
	  	  }
	      console.log(ctrl.jiraDashboard.issueSummary);
	    }

	}
})();
