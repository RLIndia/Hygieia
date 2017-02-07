(function () {
	'use strict';

	angular
	.module(HygieiaConfig.module)
	.controller('JiraTeamVelocityViewController', JiraTeamVelocityViewController);

	JiraTeamVelocityViewController.$inject = ['$scope', 'DashStatus','jiraDefectInjectionRateData',  'DisplayState', '$q', '$modal'];
	function JiraTeamVelocityViewController($scope, DashStatus, jiraDefectInjectionRateData, DisplayState, $q, $modal) {
		/*jshint validthis:true */
		var ctrl = this;
        ctrl.load = function load(){
        	
        };
		
	}
})();