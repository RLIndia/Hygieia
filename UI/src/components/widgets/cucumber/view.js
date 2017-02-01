(function () {
	'use strict';

	angular
	.module(HygieiaConfig.module)
	.controller('CucumberViewController', CucumberViewController);

	CucumberViewController.$inject = ['$scope', 'DashStatus','cucumberData',  'DisplayState', '$q', '$modal'];
	function CucumberViewController($scope, DashStatus, cucumberData, DisplayState, $q, $modal) {
		/*jshint validthis:true */
		var ctrl = this;

		// public variables
		
		ctrl.statuses = DashStatus;
		ctrl.cucumberResult = [];
		ctrl.load = load;
		ctrl.showDetail = showDetail;
		ctrl.title = "";
		ctrl.jobName = $scope.dashboard.application.components[0].collectorItems.Test[0].options.jobName;
		function load() {
			var deferred = $q.defer();
			console.log("*********************");
			console.log($scope.widgetConfig.componentId);
			console.log($scope.dashboard.application.components[0].collectorItems.Test[0].options.jobName);
			ctrl.title = $scope.dashboard.application.components[0].collectorItems.Test[0].options.jobName;
			$scope.subtitle = '[' + ctrl.title + ']';
			$scope.fdsortType = 'date';
			$scope.sortReverse = true;
			console.log("***********************");
			cucumberData.details($scope.widgetConfig.componentId).then(function(data) {
				processResponse(data.result[0]);
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
			ctrl.cucumberResult = data.testCapabilities;
			
			
			ctrl.instanceUrl = $scope.dashboard.application.components[0].collectorItems.Test[0].options.instanceUrl;
			ctrl.jobName = $scope.dashboard.application.components[0].collectorItems.Test[0].options.jobName;
		}

		function showDetail(cucumberResult){
			console.log(cucumberResult);
			var executionId = cucumberResult[0].executionId;
			var testsuites=cucumberResult[0].testSuites;			
			var features=[];
			for(var k = 0; k < testsuites.length;k++){
				var testsuite=testsuites[k];
				var testCases=testsuites[k].testCases;
				var feature={
					"desc": testsuite.id,
					"totalScenarioCount":testsuite.totalTestCaseCount,
					"passedScenarioCount": testsuite.successTestCaseCount,
					"failedScenarioCount":testsuite.failedTestCaseCount,
					"skippedScenarioCount":testsuite.skippedTestCaseCount,
					"totalStepsCount":0,
					"failedStepsCount":0,
					"passedStepsCount":0,
					"skippedStepsCount":0,
					"unknownStepsCount":0,
					"duration":testsuite.duration,
					"status":testsuite.status
				};

				for(var l = 0; l < testCases.length;l++){
					var testcase=testCases[l];
					feature.totalStepsCount +=testcase.totalTestStepCount;
					feature.failedStepsCount+=testcase.failedTestStepCount;
					feature.passedStepsCount +=testcase.successTestStepCount;
					feature.skippedStepsCount +=testcase.skippedTestStepCount;
					feature.unknownStepsCount +=testcase.unknownStatusTestStepCount;
				}
				features.push(feature);
			}
			console.log(features);
			$modal.open({
				controller: 'cucumberviewresults',
				controllerAs: 'detail',
				templateUrl: 'components/widgets/cucumber/detail.html',
				size: 'lg',
				resolve: {
					buildNumber:function(){
						return executionId;
					},
					cucumberResult: function(){
						return features;
					},
					componentId: function(){
						return $scope.widgetConfig.componentId;
					},									
					instanceUrl: function(){
						return $scope.dashboard.application.components[0].collectorItems.Test[0].options.instanceUrl;
					},
					jobName: function(){
						return $scope.dashboard.application.components[0].collectorItems.Test[0].options.jobName;
					},
					jobUrl: function(){
						return $scope.dashboard.application.components[0].collectorItems.Test[0].options.jobUrl;
					},
					collectorName: function () {
						return $scope.dashboard.application.components[0].collectorItems.Test[0].collector.name;
					},
                    collector: function () {
                        return $scope.dashboard.application.components[0].collectorItems.Test[0].collector;
                    }
				}
			});
			
		}
	}
})();