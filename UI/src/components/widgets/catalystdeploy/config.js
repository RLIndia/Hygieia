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
        ctrl.projectChange = projectChange;

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
          function getcatalystRepos1(data, currentCollectorId, cb){
                var selectedIndex = null;

                var catalystrepos = _(data).map(function(catalystrepo, idx) {
                    if(catalystrepo.id == currentCollectorId) {
                        selectedIndex = idx;
                    }
                    return {
                        // value: catalystrepo.id,
                        // name: catalystrepo.options.projectName + " - " + catalystrepo.options.repositoryName 
                        org:{
                            value:catalystrepo.options.orgId,
                            name:catalystrepo.options.orgName
                        },
                        bg:{
                            value:catalystrepo.options.bgId,
                            name:catalystrepo.options.bgName
                        },
                        project:{
                            value:catalystrepo.options.projectId,
                            name:catalystrepo.options.projectName
                        },
                        repository:{
                            value:catalystrepo.id,
                            name:catalystrepo.options.repositoryName
                        }
                    };
                }).value();
                console.log(selectedIndex);
                cb({
                    catalystrepos: catalystrepos,
                    selectedIndex: selectedIndex
                });
            };

            var worker = {
                getcatalystRepos : getcatalystRepos1
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
            //re-organizing based on project to include org and bu later
            var projects = [];
            for(var i = 0; i < data.catalystrepos.length;i++){
                //check if project is already in the list
                var found = false;
                for(var j =0; j < projects.length;j++){
                    if(projects[j].value == data.catalystrepos[i].project.value){
                        //Hit same project
                        var repo = {
                            "value":data.catalystrepos[i].repository.value,
                            "name":data.catalystrepos[i].repository.name
                        }
                        projects[j].repository.push(repo);
                        found = true;
                    }
                }
                if(!found){
                    var project = {
                        "name":data.catalystrepos[i].project.name,
                        "value":data.catalystrepos[i].project.value,
                        repository:[{
                            "value":data.catalystrepos[i].repository.value,
                            "name":data.catalystrepos[i].repository.name
                        }]
                    }
                    projects.push(project);
                }
            }
            ctrl.catalystrepos = data.catalystrepos;
            ctrl.catalystprojects = projects;
            ctrl.catalystrepositories = [];



            if(data.selectedIndex !== null) {
                   ctrl.selectedrepo = data.catalystrepos[data.selectedIndex];
            }
        }

        function projectChange(catalystRepoConfig,selectprojects){
             //   console.log('in change');
              //  console.log(catalystRepoConfig, selectprojects);

                catalystRepoConfig.catalystrepositories = [];
                if(selectprojects){
                    catalystRepoConfig.catalystprojects.forEach(function(data){
                        console.log(data);
                        if(data.value == selectprojects.value){
                            catalystRepoConfig.catalystrepositories = data.repository;
                       }
                    });
                }
                
            }

        function submit(valid) {
            ctrl.submitted = true;

            if (valid) {
                var form = document.configForm;
                
                var postObj = {
                    name: 'Catalystdeploy',
                    options: {
                        id: widgetConfig.options.id,
                        repositoryName: form.catalystrepositories.name
                    },
                    componentId: modalData.dashboard.application.components[0].id,
                    collectorItemId: form.catalystrepositories.value
                };
               console.log(postObj);
               // console.log(form.catalystrepositories.value);
                $modalInstance.close(postObj);
            }
        }
    }
})();
