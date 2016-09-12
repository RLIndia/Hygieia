(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('FunctionalConfigController', FunctionalConfigController);

    FunctionalConfigController.$inject = ['modalData', 'collectorData','$modalInstance'];
    function FunctionalConfigController(modalData, collectorData, $modalInstance) {
        /*jshint validthis:true */
        var ctrl = this;

        var widgetConfig = modalData.widgetConfig;

        // public variables
        // ctrl.deployJob;
        ctrl.functionalStacks = [];
        ctrl.stackDropdownDisabled = true;
        ctrl.stackDropdownPlaceholder = 'Loading...';
        ctrl.submitted = false;

        // public methods
        ctrl.submit = submit;

        collectorData.itemsByType('functional').then(processResponse);

        function processResponse(data) {
            
            var worker = {
                getFunctionals: getFunctionals
            };

            function getFunctionals(data, currentCollectorId, cb) {
                var selectedIndex = null;

                var functionals = _(data).map(function(functional, idx) {
                    if(functional.id == currentCollectorId) {
                        selectedIndex = idx;
                    }
                    return {
                        value: functional.id,
                        name: functional.options.envName
                    };
                }).value();
                console.log(selectedIndex);
                cb({
                    functionals: functionals,
                    selectedIndex: selectedIndex
                });
            }

            var functionalCollector = modalData.dashboard.application.components[0].collectorItems.Functional;
            var functionalCollectorId = functionalCollector ? functionalCollector[0].id : null;
            
            worker.getFunctionals(data, functionalCollectorId, getFunctionalsCallback);
        }

        function getFunctionalsCallback(data) {
            //$scope.$apply(function() {
            console.log('in callback ',data);
                ctrl.stackDropdownDisabled = false;
                ctrl.stackDropdownPlaceholder = 'Select your stack';
                ctrl.functionalStacks = data.functionals;
                
                if(data.selectedIndex !== null) {
                    ctrl.functionalStack = data.functionals[data.selectedIndex];
                }
            //});
        }


        function submit(valid) {
            ctrl.submitted = true;

            if (valid) {
                var form = document.configForm;
                
                var postObj = {
                    name: 'functional',
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
