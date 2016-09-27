(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('projectVersionConfigController', projectVersionConfigController);

    projectVersionConfigController.$inject = ['modalData', 'collectorData','$modalInstance','$timeout'];
    function projectVersionConfigController(modalData, collectorData, $modalInstance,$timeout) {
        /*jshint validthis:true */
        var ctrl = this;

        var widgetConfig = modalData.widgetConfig;

        // public variables
        // ctrl.deployJob;
        ctrl.projectVersions = [];
        ctrl.projectVersionDropdownDisabled = true;
        ctrl.projectVersionDropdownPlaceholder = 'Loading...';
        ctrl.submitted = false;
        ctrl.projectVersions = {};
        ctrl.projectVe='';
        ctrl.versions='';

        // public methods
        ctrl.submit = submit;
        console.log("modalData");
        console.log(modalData);
        collectorData.itemsByType('Jiraproject').then(
        function (data) {
        	
        	var projectVersionCollector = modalData.dashboard.application.components[0].collectorItems.Jiraproject;
            var projectVersionCollectorId = projectVersionCollector ? projectVersionCollector[0].id : null;
            
        	var projects = {};
        	
        	var verId,projId;
        	
        	for(var i=0;i<data.length;i++) {
        		if(projects[data[i].options.projectId]) {
        			projects[data[i].options.projectId].versions.push(data[i]);
        		} else {
        			projects[data[i].options.projectId] = {
        					name:data[i].options.projectName,
        					versions:[data[i]]
        			};
        			
        	   }
        		
        		if(projectVersionCollectorId ===data[i].id) {
        			projId = data[i].options.projectId;
        			verId = data[i].id;
        		}
        	}
        	ctrl.projectVersions = projects;
        	$timeout(function(){
        		ctrl.projectVe =projId; 
        		$timeout(function(){
        			 ctrl.versions=verId;
        		});
        	});
        	
        	
        	
        	
            
            //worker.getprojectVersions(data, projectVersionCollectorId, getprojectVersionCallback);
        });

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
                    collectorItemId: ctrl.versions
                };
               // console.log(postObj);
                
                $modalInstance.close(postObj);
            }
        }
    }
})();
