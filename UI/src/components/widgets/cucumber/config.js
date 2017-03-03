( function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('CucumberConfigController', CucumberConfigController);

    CucumberConfigController.$inject = ['$scope','modalData', 'collectorData','$modalInstance','$timeout'];
    function CucumberConfigController($scope, modalData, collectorData, $modalInstance,$timeout) {
    	
    	var ctrl = this;
    	
    	var widgetConfig = modalData.widgetConfig;
    	ctrl.jobs = [];
		ctrl.jobDropdownDisabled = true;
		ctrl.jobDropdownPlaceholder = 'Loading...';
		ctrl.submitted = false;
		ctrl.job = '';
		
    	ctrl.submit = submit;
    	
    	collectorData.itemsByType('Test').then(function(data) {
			console.log(data);
			
			var worker = {
					getData: getData
			};

			var selectedIndex = null;
			function getData(data, currentCollectorId, cb) {

				var jobs = _(data).map(function(job, idx) {
					console.log(job, idx);
					if(job.id == currentCollectorId) {
						selectedIndex = idx;
					}
					return {
						value: job.id,
						name: job.options.jobUrl
					};
				}).value();
				console.log(selectedIndex);
				cb({
					jobs: jobs,
					selectedIndex: selectedIndex
				});
			}
			var testCollector = modalData.dashboard.application.components[0].collectorItems.Test;
			var testCollectorId = testCollector ? testCollector[0].id : null;
            console.log("collectorId ==>",testCollectorId,modalData); 
			worker.getData(data, testCollectorId, getDataCallback);
			
    	});
    	
    	
    	
    	
    	function getDataCallback(data) {
			//$scope.$apply(function() {
			console.log('in callback ',data);
			ctrl.jobDropdownDisabled = false;
			ctrl.jobDropdownPlaceholder = 'Select job';
			ctrl.jobs = data.jobs;

			if(data.selectedIndex !== null) {
				ctrl.job = data.jobs[data.selectedIndex];
			}
			//});
		}
    	
    	
    	
    	function submit(valid) {
	            ctrl.submitted = true;

	            if (valid) {
	                var form = document.configForm;
	                
	                var postObj = {
	                    name: 'Test',
	                    options: {
	                        id: widgetConfig.options.id
	                    },
	                    componentId: modalData.dashboard.application.components[0].id,
	                    collectorItemId: form.functionalStack.value
	                };
	                console.log(postObj);
	                $modalInstance.close(postObj);
	            }
	        }
    	
    }
})();