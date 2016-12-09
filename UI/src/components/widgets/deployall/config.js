(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('deployAllConfigController', deployAllConfigController);

    deployAllConfigController.$inject = ['modalData', 'collectorData','$modalInstance'];
    function deployAllConfigController(modalData, collectorData, $modalInstance) {
        /*jshint validthis:true */
        var ctrl = this;
        var widgetConfig = modalData.widgetConfig;
        ctrl.deployJobs = [];
        ctrl.jobDropdownDisabled = true;
        ctrl.jobDropdownPlaceholder = 'Loading...';
        ctrl.submitted = false;

        // public methods
        ctrl.submit = submit;

        collectorData.itemsByType('deploymentenvironment').then(processResponse);

        function processResponse(data) {
            var worker = {
                getEnvs: getEnvs
            };
            console.log(data);
            function getEnvs(data, currentCollectorId, cb) {

                var selectedIndex = null;

                var deployenvs = _(data).map(function(deployenv, idx) {
                    if(deployenv.id == currentCollectorId) {
                        selectedIndex = idx;
                    }
                    return {
                        value: deployenv.options.envId,
                        name: deployenv.options.envName
                    };
                }).value();

                cb({
                    deployenvs: deployenvs,
                    selectedIndex: selectedIndex
                });
            }

            var deployCollector = modalData.dashboard.application.components[0].collectorItems.Deployment;
            var deployCollectorId = deployCollector ? deployCollector[0].id : null;
            console.log(modalData.dashboard.application.components);
            console.log(deployCollectorId);
            worker.getEnvs(data, deployCollectorId, getDeploysCallback);
        }

        function getDeploysCallback(data) {
                 ctrl.envsDropdownDisabled = false;
                ctrl.envDropdownPlaceholder = 'Select your environments';
                ctrl.deployAllEnvs = data.deployenvs;
                console.log("In Call back");
                console.log(ctrl.deployAllEnvs);
                if(data.selectedIndex !== null) {
                    ctrl.deployEnv = data.deployenvs[data.selectedIndex];
                }
        }


        function submit(valid) {
            ctrl.submitted = true;
            if (valid) {
                var form = document.configForm;
                var postObj = {
                    name: 'deployenv',
                    options: {
                        id: widgetConfig.options.id
                    },
                    componentId: modalData.dashboard.application.components[0].id,
                    collectorItemId: form.deployEnv.value
                };

                $modalInstance.close(postObj);
            }
        }
    }
})();
