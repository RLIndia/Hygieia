( function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('TestRailConfigController', TestRailConfigController);

    TestRailConfigController.$inject = ['$scope','modalData', 'collectorData','$modalInstance','$timeout'];
    function TestRailConfigController($scope, modalData, collectorData, $modalInstance,$timeout) {
        /*jshint validthis:true */
        var ctrl = this;

        var widgetConfig = modalData.widgetConfig;

        // public variables
        // ctrl.deployJob;
        ctrl.testrailprojects = [];
        ctrl.projectDropdownDisabled = true;
        ctrl.projectDropdownPlaceholder = 'Loading...';
        ctrl.submitted = false;
        ctrl.selectedproject = {};
        //ctrl.selectedProjectabc = "1";
       // $scope.selectedProjectabcd = "1";
             // public methods
        ctrl.submit = submit;
        console.log("modalData");
        console.log(modalData);
        var TestrailCollector = modalData.dashboard.application.components[0].collectorItems.Testrail;
        var TestrailCollectorId = TestrailCollector ? TestrailCollector[0].id : null;
        ctrl.TestrailCollector = TestrailCollector;
        ctrl.TestrailCollectorId = TestrailCollectorId;
        collectorData.itemsByType('Testrail').then(
        function (data) {
            
            var projects = [];
            console.log("**************** Scope start ***************");
//            console.log(TestrailCollector[0].options.milestoneId);
//            console.log(TestrailCollector[0].options.projectId);
            if(TestrailCollector[0].options.milestoneId && TestrailCollector[0].options.projectId){
                ctrl.selectedproject =  {
                                        "name": TestrailCollector[0].options.projectName,
                                        "value":TestrailCollector[0].options.projectId,
                                        "milestone":
                                            {
                                                "name": TestrailCollector[0].options.milestoneName,
                                                "value":TestrailCollector[0].options.milestoneId
                                            }
                                    };

             }
             else{
                ctrl.selectedproject =  {};
             }
              console.log(ctrl.selectedproject);
//            console.log(TestrailCollector[0].options.projectId);
            //console.log(TestrailCollector);
            console.log("**************** Scope end ***************");

            for(var i = 0; i < data.length;i++){
                //var project = data[i].options;
                //check if a project entry with the id exists
                var found = false;
                var project = {};
                for(var j = 0; j < projects.length;j++){
                    if(projects[j].value == data[i].options.projectId){
                        found = true;
                        var milestone = {
                            "name":data[i].options.milestoneName,
                            "value":data[i].id

                        }
                        projects[j].milestones.push(milestone);
                        project = project[j];
                    }
                }
                if(!found){
                    project = {
                        "name": data[i].options.projectName,
                        "value":data[i].options.projectId,
                        "milestones":[
                            {
                                "name": data[i].options.milestoneName,
                                "value":data[i].id
                            }
                        ]
                    }
                    projects.push(project);

                }
                console.log(data);
            }
            ctrl.testrailprojects = projects;
            ctrl.projectChange = projectChange;
            if(ctrl.selectedproject)
                {
                        //selects the saved project
                        $scope.TestRailConfig.selectedProject = {
                            "name": ctrl.selectedproject.name,
                            "value": ctrl.selectedproject.value
                        }

                        //populate the milestones dropdown
                        
                        for(var k = 0; k < projects.length;k++){
                            console.log("in loop " + projects[k].value + " " + ctrl.selectedproject.value);
                            if(projects[k].value == ctrl.selectedproject.value){
                                $scope.TestRailConfig.testrailmilestones = projects[k].milestones;
                                for(var l =0; l < projects[k].milestones.length; l++){
                                    if(projects[k].milestones[l].name == ctrl.selectedproject.milestone.name){
                                        console.log('in selected milestone');
                                        $scope.TestRailConfig.selectedMilestone = {
                                                "name": ctrl.selectedproject.milestone.name,
                                                "value":projects[k].milestones[l].value
                                        }
                                    }
                                }

                                console.log($scope.TestRailConfig.selectedMilestone)
                                console.log('in selected project');
                            }
                        }


                        $scope.TestRailConfig.selectedmilestone = ctrl.selectedproject.milestone;
                        

                            console.log($scope.TestRailConfig.selectedProject);
                           //ctrl.projectChange(ctrl,$scope.TestRailConfig.selectedProject);
                }
                else
                    ctrl.testrailmilestones = [];
            
            //console.log(data);
        	});

        function projectChange(TestRailConfig,selectedProject){
                console.log('in change');
              //  console.log(catalystRepoConfig, selectprojects);
                console.log(ctrl.selectedproject);
               // console.log($scope.selectedProjectabcd);
                TestRailConfig.testrailmilestones = [];
                if(selectedProject){
                    console.log("Found one");
                     TestRailConfig.testrailprojects.forEach(function(data){
                        console.log(selectedProject.value);
                        console.log(data.milestones);
                        if(data.value == selectedProject.value){
                            TestRailConfig.testrailmilestones = data.milestones;
                            if(TestRailConfig.selectedproject)
                             {

                                TestRailConfig.selectedmilestone = TestRailConfig.selectedproject.milestone;
                                console.log('in selected milestone');
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
                    name: 'Testrail',
                    options: {
                        id: widgetConfig.options.id
                    },
                    componentId: modalData.dashboard.application.components[0].id,
                    collectorItemId: form.selectmilestone.value
                };
               // console.log(postObj);
                
                $modalInstance.close(postObj);
            }
        }
    }
})();