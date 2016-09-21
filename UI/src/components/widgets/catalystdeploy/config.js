(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('catalystRepoConfigController', catalystRepoConfigController);

    catalystRepoConfigController.$inject = ['modalData', 'collectorData','$modalInstance'];
    function catalystRepoConfigController(modalData, collectorData, $modalInstance) {
        /*jshint validthis:true */
        var ctrl = this;

        var widgetConfig = modalData.widgetConfig;

        // public variables
        // ctrl.deployJob;
        ctrl.tasks = [];
       
        ctrl.orgNameDropdownDisabled = false;
        ctrl.orgNameDropdownPlaceholder = 'Loading...';
        ctrl.submitted = false;

        // public methods
        ctrl.submit = submit;
        console.log("modalData");
        console.log(modalData);
        collectorData.itemsByType('Catalystdeploy').then(processResponse);

        function processResponse(data) {

            console.log("process Response >>>>>")
            console.log(data);
            
            console.log("Collector Items:");
            console.log(modalData);
          //  var getcatalystRepos1 = 

            var worker = {
                getcatalystRepos : function(data, currentCollectorId, cb){
                                        var selectedIndex = null;

                                        var catalystrepos = _(data).map(function(catalystrepo, idx) {
                                            if(catalystrepo.id == currentCollectorId) {
                                                selectedIndex = idx;
                                            }
                                            return {
                                                value: catalystrepo.id,
                                                name: catalystrepo.options.projectName + " - " + catalystrepo.options.repositoryName 
                                                // org:{
                                                //     value:catalystrepo.options.orgId,
                                                //     name:catalystrepo.options.orgName
                                                // },
                                                // bg:{
                                                //     value:catalystrepo.options.bgId,
                                                //     name:catalystrepo.options.bgName
                                                // },
                                                // project:{
                                                //     value:catalystrepo.options.projectId,
                                                //     name:catalystrepo.options.projectName
                                                // },
                                                // repository:{
                                                //     value:catalystrepo.id,
                                                //     name:catalystrepo.options.repositoryName
                                                // }
                                            };
                                        }).value();
                                        console.log(selectedIndex);
                                        cb({
                                            catalystrepos: catalystrepos,
                                            selectedIndex: selectedIndex
                                        });
                                    };
            };

            var catalystRepoCollector = modalData.dashboard.application.components[0].collectorItems.Catalystdeploy;
            var catalystRepoCollectorId = catalystRepoCollector ? catalystRepoCollector[0].id : null;
            console.log(data);
            console.log(catalystRepoCollectorId);
            console.log(typeof(worker.getcatalystRepos));

            worker.getcatalystRepos(data, catalystRepoCollectorId, catalystRepoCallback);
        }

        function catalystRepoCallback(data){
            console.log('in cat callback ',data);
            ctrl.catalystrepos = data.catalystrepos;
            if(data.selectedIndex !== null) {
                   ctrl.selectedrepo = data.catalystrepos[data.selectedIndex];
            }
        }

        function submit(valid) {
            ctrl.submitted = true;

            if (valid) {
                var form = document.configForm;
                
                var postObj = {
                    name: 'Catalysttask',
                    options: {
                        id: widgetConfig.options.id
                    },
                    componentId: modalData.dashboard.application.components[0].id,
                    collectorItemId: form.projectVersion.value
                };
               // console.log(postObj);
                
                $modalInstance.close(postObj);
            }
        }
    }
})();
