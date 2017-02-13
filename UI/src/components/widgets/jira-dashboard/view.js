(function () {
	'use strict';

	angular
	.module(HygieiaConfig.module)
	.controller('jiraDashboardViewController', projectVersionViewController);

	projectVersionViewController.$inject = ['$scope', 'DashStatus', 'projectData', 'DisplayState', '$q', '$modal'];
	function projectVersionViewController($scope, DashStatus, projectData, DisplayState, $q, $modal) {
		/*jshint validthis:true */
		var ctrl = this;

		// public variables
		ctrl.statuses = DashStatus;
        ctrl.projectData = projectData;
		ctrl.load = load;
		ctrl.jiraDashboard = {};

		ctrl.showDetail = showDetail;
		ctrl.showDetailSprint= showDetailSprint;
		ctrl.title = "";
		//####
		ctrl.barChart={
            barData : {
                labels: ['S1', 'S2', 'S3', 'S4', 'S5', 'S6'],
                series: [
                    { "name": "Money A", "data":[5, 4, 3, 7, 5, 10]},
                    { "name": "Money B", "data":[3, 2, 9, 5, 4, 6]}
                ]
            },
            barOptions : {
                seriesBarDistance: 15
            },barResponsiveOptions : [
                ['screen and (min-width: 641px) and (max-width: 1024px)', {
                    seriesBarDistance: 10,
                    axisX: {
                        labelInterpolationFnc: function(value) {
                            return value;
                        }
                    }
                }],
                ['screen and (max-width: 640px)', {
                    seriesBarDistance: 5,
                    axisX: {
                        labelInterpolationFnc: function(value) {
                            return value[0];
                        }
                    }
                }]
            ]
		};


        function getRandomInt(min, max) {
            return Math.floor(Math.random() * (max - min)) + min;
        }



        ctrl.PieChart={
            data : {
                series: [60, 19],
                labels: ['Covered','Not covered']
            },
            options : {
                donut: true
            }
        };
        function getRandomInt(min, max) {
            return Math.floor(Math.random() * (max - min)) + min;
        }




        ctrl.RSChart={
            data : {
                labels: ['D1', 'D2', 'D3', 'D4', 'D5'],
                series: [
                    [1, 5, 10, 0, 1],
                    [10, 15, 0, 1, 2]
                ]
            },
            options : {
                axisX: {
                    labelInterpolationFnc: function(value) {
                        return value;
                    }
                }
            }
        };


        ctrl.SPEChart={
            data : {
                labels: ['D1', 'D2', 'D3', 'D4', 'D5'],
                series: [
                    [1, 5, 10, 0, 1],
                    [10, 15, 0, 1, 2]
                ]
            },
            dptions : {
                axisX: {
                    labelInterpolationFnc: function(value) {
                        return value;
                    }
                }
            }
        };



        ctrl.DIRChart={
            data : {
                labels: ['SP1', 'SP2', 'SP3', 'SP4', 'SP5', 'SP6'],
                series: [
                    { "name": "Money A", "data":[5, 4, 3, 7, 5, 10]}
                ]
            },
            options : {
                seriesBarDistance: 15
            },barResponsiveOptions : [
                ['screen and (min-width: 641px) and (max-width: 1024px)', {
                    seriesBarDistance: 10,
                    axisX: {
                        labelInterpolationFnc: function(value) {
                            return value;
                        }
                    }
                }],
                ['screen and (max-width: 640px)', {
                    seriesBarDistance: 5,
                    axisX: {
                        labelInterpolationFnc: function(value) {
                            return value[0];
                        }
                    }
                }]
            ]
        };

        //####


		function load() {
			var deferred = $q.defer();
			console.log("In Load...");
			projectData.details($scope.widgetConfig.componentId).then(function(data){

				processProjectData(data);
				deferred.resolve(data.lastUpdated);
			
				console.log("Done Load");

			});
			return deferred.promise;
		}
		function processProjectData(data) {
		  console.log("In process project data");
		  $scope.subtitle = data.result.version.projectName;
		  
		  if(data.result.sprint && data.result.sprint.sprintStart && data.result.sprint.sprintEnd) {
			 
			  var str = data.result.sprint.sprintStart.substring(0,8);
			  console.log(str);
			  var day = parseInt(str.substring(0,2));
			  var month = parseInt(str.substring(2,4));
			  var year = parseInt(str.substring(4));
			 
			  
			  console.log(day,month,year);
			  
			  data.result.sprint.startDate = day+'/'+month+'/'+year;
			 
			  var str = data.result.sprint.sprintEnd.substring(0,8);
			  var day = parseInt(str.substring(0,2));
			  var month = parseInt(str.substring(2,4));
			  var year = parseInt(str.substring(4));
			
			  data.result.sprint.endDate = day+'/'+month+'/'+year;;
		  }
		  
	      ctrl.jiraDashboard = {
	      		"issueSummary" :data.result.version,
	      		"issues":data.result.version.issues,
	      		"sprintSummary":data.result.sprint
	  	  }
	      console.log(ctrl.jiraDashboard.issueSummary);

	      //Data preparation for chart
	      //{"summary":{"inprogressCount":9,"doneCount":126,"pendingCount":47,"projectName":"API","versionName":"Chase Pay 1.0","issueCount":182},
	      var chartData = {
	      	labels: ['Done','In Progress','To Do'],
	      	series: [data.result.version.doneCount,data.result.version.inprogressCount,data.result.version.pendingCount],
	      	//colors:['green','orange','red']
	      	colors:["#333", "#222", "#111"]
	      }
	      ctrl.pieOptions = {
            // donut: false,
            // donutWidth: 30,
            // startAngle: 270,
            // total: 390,
            labelOffset:0,
            labelDirection: 'explode',
            chartPadding: 20,
            width:'200px',
            height:'160px',
            showLabel: false
        };
	      ctrl.jiraDashboard.chartData = chartData;
	    }

	    function showDetail(jiraDashboard){
	    	$modal.open({
				controller: 'projectVersionViewDetailController',
				controllerAs: 'detail',
				templateUrl: 'components/widgets/project/detail.html',
				size: 'lg',
				resolve: {
					jiraDashboard: function() {
						return jiraDashboard;
					},
					collectorName: function () {
						
						return $scope.dashboard.application.components[0].collectorItems.Jiraproject[0].collector.name;
					},
                    collector: function () {
                        return $scope.dashboard.application.components[0].collectorItems.Jiraproject[0].collector;
                    }
				}
			});
	    }
	    
	    function showDetailSprint(jiraDashboard){
	    	$modal.open({
				controller: 'projectVersionViewSprintDetailController',
				controllerAs: 'detail',
				templateUrl: 'components/widgets/project/detailSprint.html',
				size: 'lg',
				resolve: {
					jiraDashboard: function() {
						return jiraDashboard;
					},
					collectorName: function () {
						
						return $scope.dashboard.application.components[0].collectorItems.Jiraproject[0].collector.name;
					},
                    collector: function () {
                        return $scope.dashboard.application.components[0].collectorItems.Jiraproject[0].collector;
                    }
				}
			});
	    }

	}


})();
