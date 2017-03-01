(function() {
	'use strict';

	angular.module(HygieiaConfig.module).controller('chefConfigController',
			chefConfigController);

	chefConfigController.$inject = [ 'modalData', 'collectorData',
	                                 '$modalInstance', '$timeout' ];
	function chefConfigController(modalData, collectorData, $modalInstance,
			$timeout) {

		var ctrl = this;

		ctrl.cookbooks = [];
		ctrl.cookbookDropdownDisabled = true;
		ctrl.cookbookDropdownPlaceholder = 'Loading...';
		ctrl.submitted = false;
		ctrl.cookbook = '';

		// public methods
		ctrl.submit = submit;

		var widgetConfig = modalData.widgetConfig;


		collectorData.itemsByType('chef').then(function(data) {
			console.log(data);

			var worker = {
					getCookbooks: getCookbooks
			};

			var selectedIndex = null;
			function getCookbooks(data, currentCollectorId, cb) {

				var cookbooks = _(data).map(function(cookbook, idx) {
					if(cookbook.id == currentCollectorId) {
						selectedIndex = idx;
					}
					return {
						value: cookbook.id,
						name: cookbook.options.cookbookName
					};
				}).value();
				console.log(selectedIndex);
				cb({
					cookbooks: cookbooks,
					selectedIndex: selectedIndex
				});
			}
			var chefCollector = modalData.dashboard.application.components[0].collectorItems.Chef;
			var chefCollectorId = chefCollector ? chefCollector[0].id : null;

			worker.getCookbooks(data, chefCollectorId, getCookbooksCallback);
		});

		function getCookbooksCallback(data) {
			//$scope.$apply(function() {
			console.log('in callback ',data);
			ctrl.cookbookDropdownDisabled = false;
			ctrl.cookbookDropdownPlaceholder = 'Select your cookbook';
			ctrl.cookbooks = data.cookbooks;

			if(data.selectedIndex !== null) {
				ctrl.cookbook = data.cookbooks[data.selectedIndex];
			}
			//});
		}
		
		 function submit(valid) {
	            ctrl.submitted = true;

	            if (valid) {
	                var form = document.configForm;
	                
	                var postObj = {
	                    name: 'chef',
	                    options: {
	                        id: widgetConfig.options.id
	                    },
	                    componentId: modalData.dashboard.application.components[0].id,
	                    collectorItemId: form.functionalStack.value
	                };
	                
	                $modalInstance.close(postObj);
	            }
	        }



	}
})();
