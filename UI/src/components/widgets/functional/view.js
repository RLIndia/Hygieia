(function () {
	'use strict';

	angular
	.module(HygieiaConfig.module)
	.controller('functionalViewController', functionalViewController);

	functionalViewController.$inject = ['$scope', 'DashStatus', 'functionalData', 'DisplayState', '$q', '$modal','$interval'];
	function functionalViewController($scope, DashStatus, functionalData, DisplayState, $q, $modal,$interval) {
		/*jshint validthis:true */
		var ctrl = this;
        //*** Retain console logs for debug purposes ****/
		// public variables
		ctrl.functionalTestDays = [];
		ctrl.statuses = DashStatus;

		ctrl.load = load;
		ctrl.showDetail = showDetail;
		ctrl.title = "";
        var prom; //used for refresh during collector runs
		function load() {
			var deferred = $q.defer();
//			console.log("**********Functional Test***********");
//			console.log($scope.widgetConfig.componentId);
//			console.log($scope.dashboard.application.components[0].collectorItems.Functional[0].options.envId);
			ctrl.title = $scope.dashboard.application.components[0].collectorItems.Functional[0].options.envName;
			$scope.subtitle = '[' + ctrl.title + ']';
			$scope.fdsortType = 'date';
			$scope.sortReverse = true;
//			console.log("**********End functional test*************");
			functionalData.details($scope.widgetConfig.componentId).then(function(data) {
				processResponse(data.result);
				deferred.resolve(data.lastUpdated);
			});
			return deferred.promise;
		}

		function showDetail(day) {
		//	console.log(day);
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
					totalNotrun:data[days[i]].totalNotrun,
					status:data[days[i]].status,
					testCases:data[days[i]]
				});
			}
			//console.log('functionalTestDays ==>',functionalTestDays);
			ctrl.functionalTestDays = functionalTestDays;

			//When there is no data to process retry frequently..
		    //	console.log("Functional Days -----------------" + days.length);
			if(days.length == 0 && !prom){
			    prom = $interval(load,2000);
			   // console.log("Set Interval -----------");
			}
			else{
			    if(days.length > 0 && prom)
                {
                    $interval.cancel(prom);
             //       console.log("Cancelling Interval...........");
                    prom = null;
                }
			}

		}

		function defaultStateCallback(isDefaultState) {
			$scope.display = isDefaultState ? DisplayState.DEFAULT : DisplayState.ERROR;
		}

		function environmentsCallback(data) {
			ctrl.environments = data.environments;
		}
	}
})();
