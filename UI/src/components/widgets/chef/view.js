(function () {
	'use strict';

	angular
	.module(HygieiaConfig.module)
	.controller('chefViewController', chefViewController);

	chefViewController.$inject = ['$scope', 'DashStatus', 'projectData', 'DisplayState', '$q', '$modal'];
	function chefViewController($scope, DashStatus, projectData, DisplayState, $q, $modal) {
		var ctrl = this;
		ctrl.load = load;
		
		function load() {
			
		}
		
	}


})();
