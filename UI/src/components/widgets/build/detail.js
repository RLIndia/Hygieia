/**
 * Detail controller for the build widget
 */
(function() {
	'use strict';

	angular.module(HygieiaConfig.module).controller(
			'BuildWidgetDetailController', BuildWidgetDetailController);

	BuildWidgetDetailController.$inject = [ '$scope', '$modalInstance',
			'build', 'collectorName', 'componentId', '$http' ];
	function BuildWidgetDetailController($scope, $modalInstance, build,
			collectorName, componentId,$http) {
		console.log('build', $scope);
		var ctrl = this;

		ctrl.build = build;
		ctrl.collectorName = collectorName;

		ctrl.buildPassed = buildPassed;
		ctrl.close = close;

		function buildPassed() {
			return ctrl.build.buildStatus === 'Success';
		}

		function close() {
			$modalInstance.dismiss('close');
		}

		ctrl.triggerBuild = function() {
			$http.get('/api/job/run/'+componentId).then(function(response) {
				console.log(response);
			});
			alert('Build Initiated');
		};
	}
})();
