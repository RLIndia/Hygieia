( function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('CucumberConfigController', CucumberConfigController);

    CucumberConfigController.$inject = ['$scope','modalData', 'collectorData','$modalInstance','$timeout'];
    function CucumberConfigController($scope, modalData, collectorData, $modalInstance,$timeout) {
        /*jshint validthis:true */
        var ctrl = this;

        var widgetConfig = modalData.widgetConfig;

        // public variables
        // ctrl.deployJob;
        ctrl.JenkinsCucumberProjects = [];
        ctrl.projectDropdownDisabled = true;
        ctrl.projectDropdownPlaceholder = 'Loading...';
        ctrl.submitted = false;
        ctrl.selectedInstance = {};
        //ctrl.selectedProjectabc = "1";
       // $scope.selectedProjectabcd = "1";
             // public methods
        ctrl.submit = submit;
        console.log("modalData");
        console.log(modalData);
        var CucumberCollector= modalData.dashboard.application.components[0].collectorItems.Test;
        var CucumberCollectorId = CucumberCollector ? CucumberCollector[0].id : null;
        ctrl.CucumberCollector = CucumberCollector;
        ctrl.CucumberCollectorId = CucumberCollectorId;
        collectorData.itemsByType('Test').then(
        function (data) {
            
            var instances = [];
            console.log("**************** Scope start ***************");
//            console.log(TestrailCollector[0].options.milestoneId);
//            console.log(TestrailCollector[0].options.projectId);
console.log(data);
            if(CucumberCollector && CucumberCollector[0].options.instanceUrl && CucumberCollector[0].options.jobName){
                ctrl.selectedInstance =  {
                                        "name": CucumberCollector[0].options.instanceUrl,
                                        "value":CucumberCollector[0].options.instanceUrl,
                                        "job":
                                            {
                                                "name": CucumberCollector[0].options.jobName,
                                                "value":CucumberCollector[0].options.jobName
                                            }
                                    };

             }
             else{
                ctrl.selectedInstance =  {};
             }
              console.log( ctrl.selectedInstance);
//            console.log(TestrailCollector[0].options.projectId);
            //console.log(TestrailCollector);
            console.log("**************** Scope end ***************");

            for(var i = 0; i < data.length;i++){
                //var project = data[i].options;
                //check if a project entry with the id exists
                var found = false;
                var instance = {};
                for(var j = 0; j < instances.length;j++){
                    if(instances[j].value == data[i].options.jobName){
                        found = true;
                        var job = {
                            "name":data[i].options.jobName,
                            "value":data[i].options.jobName

                        }
                        instances[j].jobs.push(job);
                        instance = instances[j];
                    }
                }
                if(!found){
                    instance = {
                        "name": data[i].options.instanceUrl,
                        "value":data[i].options.instanceUrl,
                        "jobs":[
                            {
                                "name": data[i].options.jobName,
                                "value":data[i].options.jobName
                            }
                        ]
                    }
                    instances.push(instance);

                }
                console.log(data);
            }
            console.log(instances);
            ctrl.JenkinsCucumberProjects = instances;
            ctrl.projectChange = projectChange;
            if(ctrl.selectedInstance)
                {
                        //selects the saved project
                        $scope.CucumberConfig.selectedInstance = {
                            "name": ctrl.selectedInstance.name,
                            "value": ctrl.selectedInstance.value,
                            "job":{
                                "name": ctrl.selectedInstance.job.name,
                                "value": ctrl.selectedInstance.job.value
                            }
                        }

                        //populate the milestones dropdown
                        
                        for(var k = 0; k < instances.length;k++){
                            console.log("in loop " + instances[k].value + " " + ctrl.selectedInstance.value);
                            if(instances[k].value == ctrl.selectedInstance.value){
                                $scope.CucumberConfig.jobs = instances[k].jobs;
                                for(var l =0; l < instances[k].jobs.length; l++){
                                    console.log('instances[k].jobs[l].name:' + instances[k].jobs[l].name);
                                    console.log('ctrl.selectedInstance.job.name:' + ctrl.selectedInstance.job.name);
                                    if(instances[k].jobs[l].name == ctrl.selectedInstance.job.name){
                                        console.log('in selected job');
                                        $scope.CucumberConfig.selectedJob = {
                                                "name": ctrl.selectedInstance.job.name,
                                                "value":instances[k].jobs[l].value
                                        }
                                    }
                                }

                                console.log($scope.CucumberConfig.selectedJob)
                                console.log('in selected project');
                            }
                        }


                        $scope.CucumberConfig.selectedJob = ctrl.selectedInstance.job;
                        $scope.CucumberConfig.instances=instances;

                            console.log($scope.CucumberConfig.selectedInstance);
                           //ctrl.projectChange(ctrl,$scope.TestRailConfig.selectedProject);
                }
                else
                    ctrl.jobs = [];
            
            //console.log(data);
        	});

        function projectChange(CucumberConfig,selectedInstance){
                console.log('in change');
              //  console.log(catalystRepoConfig, selectprojects);
                console.log(ctrl.selectedInstance);
               // console.log($scope.selectedProjectabcd);
                CucumberConfig.jobs = [];
                if(selectedInstance){
                    console.log("Found one");
                     CucumberConfig.instances.forEach(function(data){
                        console.log(selectedInstance.value);
                        console.log(data.jobs);
                        if(data.value == selectedInstance.value){
                            CucumberConfig.jobs = data.jobs;
                            if(CucumberConfig.selectedInstance)
                             {

                                CucumberConfig.selectedjob = CucumberConfig.selectedInstance.job;
                                console.log('in selected job');
                            }
                       }
                    });

                }
                // if(selectprojects){
                //     TestRailConfig.testrailprojects.forEach(function(data){
                //         console.log(data);
                //         if(data.value == selectprojects.value){
                //             TestRailConfig.testrailmilestones = data.milestones;
                //             if(TestRailConfig.selectedproject)
                //              {

                //                 TestRailConfig.selectmilestone = TestRailConfig.selectedproject.milestone.value;
                //                 console.log('in selected milestone');
                //             }
                //        }
                //     });
                // }
                
            }
        function submit(valid){
            ctrl.submitted = true;

            if (valid) {
                var form = document.configForm;
                
                var postObj = {
                    name: 'smoketest',
                    options: {
                        id: widgetConfig.options.id
                    },
                    componentId: modalData.dashboard.application.components[0].id,
                    collectorItemId: form.selectJob.value
                };
               // console.log(postObj);
                
                $modalInstance.close(postObj);
            }
        }
    }
})();