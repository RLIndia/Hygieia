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
		ctrl.functionalTestDays = [];
		ctrl.statuses = DashStatus;
        ctrl.projectData = projectData;
		ctrl.load = load;
		
		//ctrl.showDetail = showDetail;
		ctrl.title = "";

		function load() {
			console.log("In Load...");

		}


	}
})();
