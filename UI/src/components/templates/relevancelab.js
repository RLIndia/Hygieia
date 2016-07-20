/**
 * Controller for the dashboard route.
 * Render proper template.
 */
(function () {
	'use strict';

	angular
	.module(HygieiaConfig.module)
	.controller('RelevanceLabTemplateController', RelevanceLabTemplateController);

	RelevanceLabTemplateController.$inject = ['dashboardData','$scope','$compile','$route'];
	function RelevanceLabTemplateController(dashboardData,$scope,$compile,$route) {
       
		
		var ctrl = this;

		ctrl.tabs = [
		             { name: "Widget"},
		             { name: "Pipeline"},
		             { name: "Cloud"}
		             ];

		ctrl.widgetView = ctrl.tabs[0].name;
		ctrl.toggleView = function (index) {
			ctrl.widgetView = typeof ctrl.tabs[index] === 'undefined' ? ctrl.tabs[0].name : ctrl.tabs[index].name;
		};

		ctrl.hasComponents = function (dashboard, names) {
			var hasAllComponents = true;

			try {
				_(names).forEach(function (name) {
					if(!dashboard.application.components[0].collectorItems[name]) {
						hasAllComponents = false;
					}
				});
			} catch(e) {
				hasAllComponents = false;
			}

			return hasAllComponents;
		};
		$scope.addCodeRepoWidget = function(){
			angular.element(document.getElementById('codeWidgetArea')).append($compile("<div class='col-md-4'><widget name='repo'></widget></div>")($scope));
		};
		
		var dashboardPromise = dashboardData.detail($route.current.params.id);
		dashboardPromise.then(function(dashboard){
			var widgets = dashboard.widgets;
			if(widgets && widgets.length) {
				for(var i=0;i<widgets.length;i++) {
					angular.element(document.getElementById('codeWidgetArea')).append($compile("<div class='col-md-4'><widget name='repo'></widget></div>")($scope));
				}	
			}
			
		});
		
		
		
		
	}
})();
