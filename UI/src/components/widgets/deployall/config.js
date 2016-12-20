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

        collectorData.itemsByType('deploymentenvironment').then(processResponse);

        function processResponse(data) {
            var worker = {
                getEnvs: getEnvs
            };
            //console.log(data);
            function getEnvs(data, currentCollectorId,selectedenvs, cb) {

                var selectedIndex = null;

                var deployenvs = _(data).map(function(deployenv, idx) {
//                    if(deployenv.id == currentCollectorId) {
//                        selectedIndex = idx;
//                    }
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

            console.log("Components:");
            console.log(modalData.dashboard.application.components); //all options
            console.log("deployAllCollectorId");
            console.log(deployAllCollectorId); //enabled options
            worker.getEnvs(data, deployAllCollectorId,selectedEnvs, getDeploysCallback);
        }

        function getDeploysCallback(data) {
                ctrl.envsDropdownDisabled = false;
                ctrl.jobDropdownPlaceholder = 'Select your environments';
                ctrl.deployAllEnvs = data.deployenvs;
                ctrl.envs = data.selectedEnvs
                console.log("In Call back");
               console.log(ctrl.envs);
//                if(data.selectedIndex !== null) {
//                    ctrl.deployEnv = data.deployenvs[data.selectedIndex];
//                }
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
            console.log(envs);
           ctrl.envs = envs;
          // console.log("in copy");
           console.log(ctrl.envs);
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

            console.log('in submit');
            //Rebuilding env list
            var envs = [];
            for(var i = 0; i < ctrl.envs.length;i++){
//                console.log(ctrl.envs[i].value);
//                envs += ctrl.envs[i].value;
//                if(i < ctrl.envs.length - 2)
//                    envs += ",";
                envs.push(ctrl.envs[i].value);
            }
            console.log(document.configForm.selectedEnvs.value);
            console.log(envs);

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
               // postObj.collectorItemId.push(envs);
                console.log(postObj);
                $modalInstance.close(postObj);
            }
        }
    }
})();
