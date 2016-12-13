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

        ctrl.copyEnv = copyEnv;

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

            var deployAllCollector = modalData.dashboard.application.components[0].collectorItems.Deployment;
            var deployAllCollectorId = deployAllCollector ? deployAllCollector[0].id : null;
            console.log(modalData.dashboard.application.components);
            console.log(deployAllCollectorId);
            worker.getEnvs(data, deployAllCollectorId, getDeploysCallback);
        }

        function getDeploysCallback(data) {
                 ctrl.envsDropdownDisabled = false;
                ctrl.jobDropdownPlaceholder = 'Select your environments';
                ctrl.deployAllEnvs = data.deployenvs;
                console.log("In Call back");
                console.log(ctrl.deployAllEnvs);
                if(data.selectedIndex !== null) {
                    ctrl.deployEnv = data.deployenvs[data.selectedIndex];
                }
        }

        function copyEnv(){
            var envs = [];
            //envs.push(ctrl.envs);
            console.log(ctrl.envs);
            //Get all selected environments
            ctrl.deployAllEnv.forEach(function(obj,value){
                var _env = {
                    value:obj.value,
                    name:obj.name
                }
                envs.push(_env);
                console.log(obj);
                console.log(value);
            });
            //append all selected environment. Remove repeated envs
            if(ctrl.envs){
                ctrl.envs.forEach(function(obj,value){
                    var _env = {
                        value:obj.value,
                        name:obj.name
                    }
                    envs.push(_env);
                });
            }
           ctrl.envs = envs;
           console.log("in copy");
           console.log(ctrl.envs);
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
