(function () {
	'use strict';

	angular
	.module(HygieiaConfig.module)
	.controller('functionalViewController', functionalViewController);

	functionalViewController.$inject = ['$scope', 'DashStatus', 'functionalData', 'DisplayState', '$q', '$modal'];
	function functionalViewController($scope, DashStatus, functionalData, DisplayState, $q, $modal) {
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

		function showDetail(day) {
			console.log(day);
			$modal.open({
				controller: 'FunctionalDetailController',
				controllerAs: 'detail',
				templateUrl: 'components/widgets/functional/detail.html',
				size: 'lg',
				resolve: {
					day: function() {
						return day;
					},
					collectorName: function () {
						return $scope.dashboard.application.components[0].collectorItems.Functional[0].collector.name;
					},
                    collector: function () {
                        return $scope.dashboard.application.components[0].collectorItems.Functional[0].collector;
                    }
				}
			});
		}

		function processResponse(data) {
			var functionalTestDays = [];
			var days = Object.keys(data);
			for(var i=0;i<days.length;i++) {
				var date = new Date();
				date.setTime(parseInt(days[i]));
				functionalTestDays.push({
					timestamp:days[i],
					date : date,
					totalPassed:data[days[i]].totalPassed,
					totalFailed:data[days[i]].totalFailed,
					status:data[days[i]].status,
					testCases:data[days[i]]
				});
			}
			console.log('functionalTestDays ==>',functionalTestDays);
			ctrl.functionalTestDays = functionalTestDays;
		}

		function defaultStateCallback(isDefaultState) {
			//$scope.$apply(function() {
			$scope.display = isDefaultState ? DisplayState.DEFAULT : DisplayState.ERROR;
			//});
		}

		function environmentsCallback(data) {
			//$scope.$apply(function () {
			ctrl.environments = data.environments;
			//});
		}
	}
})();
