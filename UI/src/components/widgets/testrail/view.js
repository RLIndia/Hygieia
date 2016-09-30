(function () {
	'use strict';

	angular
	.module(HygieiaConfig.module)
	.controller('TestRailViewController', TestRailViewController);

	TestRailViewController.$inject = ['$scope', 'DashStatus','testrailRunData',  'DisplayState', '$q', '$modal'];
	function TestRailViewController($scope, DashStatus, testrailRunData, DisplayState, $q, $modal) {
		/*jshint validthis:true */
		var ctrl = this;

		// public variables
		
		ctrl.statuses = DashStatus;
		ctrl.testrailRuns = [];
		ctrl.load = load;
		ctrl.showDetail = showDetail;
		ctrl.title = "";
		ctrl.milestoneName = $scope.dashboard.application.components[0].collectorItems.Testrail[0].options.milestoneName;
		function load() {
			var deferred = $q.defer();
			console.log("*********************");
			console.log($scope.widgetConfig.componentId);
			console.log($scope.dashboard.application.components[0].collectorItems.Testrail[0].options.milestoneName);
			ctrl.title = $scope.dashboard.application.components[0].collectorItems.Testrail[0].options.projectName;
			$scope.subtitle = '[' + ctrl.title + ']';
			$scope.fdsortType = 'date';
			$scope.sortReverse = true;
			console.log("***********************");
			testrailRunData.details($scope.widgetConfig.componentId).then(function(data) {
				processResponse(data.result);
				deferred.resolve(data.lastUpdated);
			});
			return deferred.promise;
		}

		function processResponse(data){
			ctrl.testrailRuns = data.runs;
		}

		function showDetail(){
			
		}
	}
})();