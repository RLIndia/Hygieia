(function () {
	'use strict';

	angular
	.module(HygieiaConfig.module)
	.controller('chefViewController', chefViewController);

	chefViewController.$inject = ['$scope', 'DashStatus', 'chefData', 'DisplayState', '$q', '$modal'];
	function chefViewController($scope, DashStatus, chefData, DisplayState, $q, $modal) {
		var ctrl = this;
		ctrl.load = load;
		ctrl.nodes = [];
		ctrl.cookbookName = $scope.dashboard.application.components[0].collectorItems.Chef[0].options.cookbookName;
		
		function load() {
			var deferred = $q.defer();
			console.log("In Load...");
			chefData.details($scope.widgetConfig.componentId).then(function(data){

				ctrl.nodes = data.result;
				deferred.resolve(data.lastUpdated);
				console.log("Done Load");

			});
			return deferred.promise;
			
		}
		
	}


})();
