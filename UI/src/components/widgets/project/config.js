(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('projectVersionConfigController', projectVersionConfigController);

    projectVersionConfigController.$inject = ['modalData', 'collectorData','$modalInstance'];
    function projectVersionConfigController(modalData, collectorData, $modalInstance) {
        /*jshint validthis:true */
        var ctrl = this;

        var widgetConfig = modalData.widgetConfig;

        // public variables
        // ctrl.deployJob;
        ctrl.projectVersions = [];
        ctrl.projectVersionDropdownDisabled = true;
        ctrl.projectVersionDropdownPlaceholder = 'Loading...';
        ctrl.submitted = false;

        // public methods
        ctrl.submit = submit;
        console.log("modalData");
        console.log(modalData);
        collectorData.itemsByType('Jiraproject').then(processResponse);

        function processResponse(data) {
            
            var worker = {
                getprojectVersions: getprojectVersions
            };
            console.log(data);
            function getprojectVersions(data, currentCollectorId, cb) {
                var selectedIndex = null;

                var projectVersions = _(data).map(function(projectVersionsdata, idx) {
                    if(projectVersionsdata.id == currentCollectorId) {
                        selectedIndex = idx;
                    }
                    return {
                        value: projectVersionsdata.id,
                        name: projectVersionsdata.options.projectName + " - " + projectVersionsdata.options.versionName 
                    };
                }).value();
                console.log(selectedIndex);
                cb({
                    projectVersions: projectVersions,
                    selectedIndex: selectedIndex
                });
            }
            console.log("Collector Items:");
            console.log(modalData);
            var projectVersionCollector = modalData.dashboard.application.components[0].collectorItems.Jiraproject;
            var projectVersionCollectorId = projectVersionCollector ? projectVersionCollector[0].id : null;
            
            worker.getprojectVersions(data, projectVersionCollectorId, getprojectVersionCallback);
        }

        function getprojectVersionCallback(data) {
            //$scope.$apply(function() {
            console.log('in callback ',data);
                ctrl.projectVersionDropdownDisabled = false;
                ctrl.projectVersionDropdownPlaceholder = 'Select your project';
                ctrl.projectVersions = data.projectVersions;
                
                if(data.selectedIndex !== null) {
                    ctrl.projectVersion = data.projectVersions[data.selectedIndex];
                }
            //});
        }


        function submit(valid) {
            ctrl.submitted = true;

            if (valid) {
                var form = document.configForm;
                
                var postObj = {
                    name: 'Jiraproject',
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
