(function () {
	'use strict';

	angular
	.module(HygieiaConfig.module)
	.controller('JiraAcceptanceCoverageViewController', JiraAcceptanceCoverageViewController);

	JiraAcceptanceCoverageViewController.$inject = ['$scope', 'DashStatus','JiraAcceptanceCoverageData',  'DisplayState', '$q', '$modal'];
	function JiraAcceptanceCoverageViewController($scope, DashStatus, JiraAcceptanceCoverageData, DisplayState, $q, $modal) {
		/*jshint validthis:true */
		var ctrl = this;
        ctrl.load = function load(){
        	
        };
		
	}
})();