(function () {
	'use strict';

	angular
	.module(HygieiaConfig.module)
	.controller('catalystDeployViewController', catalystDeployViewController);

	catalystDeployViewController.$inject = ['$scope', 'DashStatus', 'catalystdeploydata', 'DisplayState', '$q', '$modal'];
	function catalystDeployViewController($scope, DashStatus, catalystdeploydata, DisplayState, $q, $modal) {
		/*jshint validthis:true */
		var ctrl = this;

		// public variables
		ctrl.statuses = DashStatus;
        ctrl.catalystdeploydata = catalystdeploydata;
		ctrl.load = load;
		ctrl.catalystDeployDashboard = {};

		ctrl.showDetail = showDetail;
		ctrl.title = "";

		function load() {
			var deferred = $q.defer();
			console.log("In Load...");
			 $scope.sortType = 'name';
            $scope.sortReverse = false;
			catalystdeploydata.details($scope.widgetConfig.componentId).then(function(data){

				processProjectData(data);
				deferred.resolve(data.lastUpdated);
			
				console.log("Done Load");

			});
			return deferred.promise;
		}
		function processProjectData(data) {
		   console.log("In process catalystdeploydata");
		   //Get the Project name and Reponame
		   var collectorOptions = $scope.dashboard.application.components[0].collectorItems.Catalystdeploy[0].options;
		   ctrl.repoName = collectorOptions.repositoryName;
		   $scope.subtitle = "[ " + collectorOptions.projectName + "]";
		   console.log(data.result.deployments.length);
		   for(var i = 0; i < data.result.deployments.length;i++){
		   		data.result.deployments[i].lastdeployed = new Date(data.result.deployments[i].lastdeployed);
		   		console.log(data.result.deployments[i].lastdeployed);
		   }
		   var catalystData = {
		   		
		   		"deployments":data.result.deployments
		   }
		   ctrl.catalystData = catalystData;
		  // $scope.subtitle = data.result.summary.projectName;
	   //    ctrl.catalystTaskDashboard = {
	   //    		"issueSummary" :data.result.summary,
	   //    		"issues":data.result.issues
	  	//   }
	   //    console.log(ctrl.jiraDashboard.issueSummary);

	      //Data preparation for chart
	      //{"summary":{"inprogressCount":9,"doneCount":126,"pendingCount":47,"projectName":"API","versionName":"Chase Pay 1.0","issueCount":182},
	      // var chartData = {
	      // 	labels: ['Done','To Do','In Progress'],
	      // 	series: [data.result.summary.doneCount,data.result.summary.pendingCount,data.result.summary.inprogressCount],
	      // 	colors:['green','orange','red']
	      // }
	      // ctrl.pieOptions = {
       //      donut: true,
       //      donutWidth: 30,
       //      startAngle: 270,
       //      total: 360,
       //      labelOffset:20,
       //      width:'350px',
       //      height:'200px',
       //      showLabel: true
       //  };
	      // ctrl.jiraDashboard.chartData = chartData;
	     
	    }

	    function showDetail(deployment){
	    	console.log(deployment);
	    	$modal.open({
				controller: 'Catalystdeployhistorycontroller',
				controllerAs: 'taskdetail',
				templateUrl: 'components/widgets/catalystdeploy/detail.html',
				size: 'lg',
				resolve: {
					deployment: function() {
							return deployment;
					},
					taskId: function(){
						return deployment.taskId;
					},
					collectorName: function () {
						
						return $scope.dashboard.application.components[0].collectorItems.Catalystdeploy[0].collector.name;
					},
                    collector: function () {
                        return $scope.dashboard.application.components[0].collectorItems.Catalystdeploy[0].collector;
                    }
				}


			});
	    }

	}


})();
