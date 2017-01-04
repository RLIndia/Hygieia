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

        ctrl.removeEnv = removeEnv;

        ctrl.selectallEnvs = selectallEnvs;

        collectorData.itemsByType('deploymentenvironment').then(processResponse);

        function processResponse(data) {
            var worker = {
                getEnvs: getEnvs
            };
            //console.log(data);
            function getEnvs(data, currentCollectorId,selectedenvs, cb) {

                var selectedIndex = null;

                var deployenvs = _(data).map(function(deployenv, idx) {
                    return {
                        value: deployenv.id,
                        name: deployenv.options.envName
                    };
                }).value();

                var selectedEnvs = _(selectedenvs).map(function(selectedenv,ide){
                    return{
                        value: selectedenv.id,
                        name: selectedenv.options.envName
                    }
                }).value();

                cb({
                    deployenvs: deployenvs,
                    selectedEnvs: selectedEnvs
                });
            }

            var deployAllCollector = modalData.dashboard.application.components[0].collectorItems.Deployment;
            var deployAllCollectorId = deployAllCollector ? deployAllCollector[0].id : null;
            var selectedEnvs = modalData.dashboard.application.components[0].collectorItems.DeploymentEnvironment;
            //Converting to dropdown
            worker.getEnvs(data, deployAllCollectorId,selectedEnvs, getDeploysCallback);
        }

        function getDeploysCallback(data) {
                ctrl.envsDropdownDisabled = false;
                ctrl.jobDropdownPlaceholder = 'Select your environments';
                ctrl.deployAllEnvs = data.deployenvs;
                ctrl.envs = data.selectedEnvs
          }

        function selectallEnvs(cb){
            if(ctrl.envs){
                console.log(ctrl.envs);
                ctrl.deploySelectedEnvs = ctrl.envs;
                var form = document.configForm;
                form.selectedEnvs.value = ctrl.envs[0].value;
                cb(true);
            }
             else
                cb(false);
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
            });
            //append all selected environment. Remove repeated envs
            if(ctrl.envs){
                var foundEnv = false;
                ctrl.envs.forEach(function(obj,value){
                    var _env = {
                        value:obj.value,
                        name:obj.name
                    }
                    //console.log("here 2...");
                    foundEnv = false;
                    for(var i = 0; i < envs.length; i++){

                        if(envs[i].value == _env.value)
                            foundEnv = true;
                    }

                    if(foundEnv == false)
                        envs.push(_env);

                });
            }
            ctrl.envs = envs;
        }

        function removeEnv(){
            var envs = ctrl.envs; //make a copy
            //console.log("in remove env");
            ctrl.deploySelectedEnvs.forEach(function(obj1,value1){
                    //console.log(obj1.value);
                    var removeIndex = -1;
                    for(var i = 0; i < envs.length;i++){
                        if(envs[i].value == obj1.value)
                          {
                            removeIndex = i;
                                break;
                          }
                    }

                    if(removeIndex > -1){
                        envs.splice(removeIndex,1);
                    }

            });
            ctrl.envs = envs;
        }


        function submit(valid) {
            ctrl.submitted = true;
            //Rebuilding env list
            var envs = [];
            for(var i = 0; i < ctrl.envs.length;i++){
                envs.push(ctrl.envs[i].value);
            }
            if (valid) {
                var form = document.configForm;
                var postObj = {
                    name: 'deploymentEnvironment',
                    options: {
                        id: widgetConfig.options.id
                    },
                    componentId: modalData.dashboard.application.components[0].id,
                    collectorItemId: form.selectedEnvs.value,
                    envs: envs
                };
                $modalInstance.close(postObj);
            }
            else{
                console.log("invalid");
            }
        }
    }
})();
