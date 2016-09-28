(function () {
	'use strict';

	angular
	.module(HygieiaConfig.module)
	.controller('TestRailViewController', TestRailViewController);

	TestRailViewController.$inject = ['$scope', 'DashStatus',  'DisplayState', '$q', '$modal'];
	function TestRailViewController($scope, DashStatus, DisplayState, $q, $modal) {
		/*jshint validthis:true */
		var ctrl = this;

		// public variables
		ctrl.functionalTestDays = [];
		ctrl.statuses = DashStatus;

		ctrl.load = load;
		ctrl.showDetail = showDetail;
		ctrl.title = "";

		function load() {
			var deferred = $q.defer();
			console.log("*********************");
			console.log($scope.widgetConfig.componentId);
			console.log($scope.dashboard.application.components[0].collectorItems.Functional[0].options.envId);
			ctrl.title = $scope.dashboard.application.components[0].collectorItems.Functional[0].options.envName;
			$scope.subtitle = '[' + ctrl.title + ']';
			$scope.fdsortType = 'date';
			$scope.sortReverse = true;
			console.log("***********************");
			functionalData.details($scope.widgetConfig.componentId).then(function(data) {
				processResponse(data.result);
				deferred.resolve(data.lastUpdated);
			});
			return deferred.promise;
		}

		function processResponse(data){
			ctrl.mileStoneData = data;
		}

		function showDetail(){
			
		}
	}
})();