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
			// var testrailRuns = [];
			// for(var i = 0; i < data.runs.length; i++){
			// 	var datatemp = data.runs[i];
			// 	var p = parseInt

			// 	datatemp.passPercent = ()

			// }
			ctrl.testrailRuns = data.runs;
			ctrl.projectId = $scope.dashboard.application.components[0].collectorItems.Testrail[0].options.projectId;
			ctrl.milestoneId = $scope.dashboard.application.components[0].collectorItems.Testrail[0].options.milestoneId;
		}

		function showDetail(runId1,runName){
			console.log(runId1);
			
			$modal.open({
				controller: 'testrailviewresults',
				controllerAs: 'detail',
				templateUrl: 'components/widgets/testrail/detail.html',
				size: 'lg',
				resolve: {
					runId: function(){
						return runId1;
					},
					runName: function(){
						return runName;
					},
					projectId: function(){
						return $scope.dashboard.application.components[0].collectorItems.Testrail[0].options.projectId;
					},
					milestoneId: function(){
						return $scope.dashboard.application.components[0].collectorItems.Testrail[0].options.milestoneId;
					},
					projectName: function(){
						return $scope.dashboard.application.components[0].collectorItems.Testrail[0].options.projectName;
					},
					milestoneName: function(){
						return $scope.dashboard.application.components[0].collectorItems.Testrail[0].options.milestoneName;
					},
					collectorName: function () {
						return $scope.dashboard.application.components[0].collectorItems.Testrail[0].collector.name;
					},
                    collector: function () {
                        return $scope.dashboard.application.components[0].collectorItems.Testrail[0].collector;
                    }
				}
			});
			
		}
	}
})();