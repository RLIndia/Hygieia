(function () {
	'use strict';

	angular
	.module(HygieiaConfig.module)
	.controller('jiraDashboardViewController', projectVersionViewController);

	projectVersionViewController.$inject = ['$scope', 'DashStatus', 'jiraDashboardData', 'DisplayState', '$q', '$modal'];
	function projectVersionViewController($scope, DashStatus, jiraDashboardData, DisplayState, $q, $modal) {
		/*jshint validthis:true */
		var ctrl = this;

		// public variables
		ctrl.statuses = DashStatus;
        ctrl.jiraDashboardData = jiraDashboardData;
		ctrl.load = load;
		ctrl.jiraDashboard = {};

		ctrl.showDetail = showDetail;
		ctrl.showDetailSprint= showDetailSprint;
		ctrl.title = "";
		ctrl.sprints = [];
		ctrl.releaseStatusDataSet = [];
		ctrl.burnDownDataSet = [];
		//####
		ctrl.barChart={
            barData : {
                chartPadding: {
                    top: 100,
                    right: 0,
                    bottom: 0,
                    left: 0
                },
                labels: [],
                series: []
            },

            barOptions : {
                chartPadding: {
                    right: 0,
                    bottom: 24,
                    left: 0
                },
                axisY: {
                    onlyInteger: true
                },
                plugins: [
                    Chartist.plugins.legend(),
                    Chartist.plugins.ctAxisTitle({
                        axisX: {
                            axisTitle: 'Sprint',
                            axisClass: 'ct-axis-title',
                            offset: {
                                x: 0,
                                y: 50
                            },
                            textAnchor: 'middle'
                        },
                        axisY: {
                            axisTitle: 'Story points',
                            axisClass: 'ct-axis-title',
                            offset: {
                                x: 0,
                                y: 0
                            },
                            textAnchor: 'middle',
                            flipTitle: false
                        }
                    })
                ]
            }
		};



        ctrl.PieChart={
            data : {
                series: [],
                labels: []
            },
            options : {
                donut: true
            }
        };
        
        ctrl.DSRPieChart={
            data : {
               series: []
            },
            options : {
                donut: false,
                donutWidth: 60,
                startAngle: 270,
                total: 200,
                showLabel: false
            }
        };
        
        
        function getRandomInt(min, max) {
            return Math.floor(Math.random() * (max - min)) + min;
        }



        ctrl.RSChart1={
            data : {
                /*labels: ['Jan 01', 'Jan 02', 'Jan 03', 'Jan 04', 'Jan 05'],
                series: [
                    [1,4,6,7,9,11]
                ]*/
            },
            options : {
                chartPadding: {
                    right: 0,
                    bottom: 30,
                },
                plugins: [
                    Chartist.plugins.ctAxisTitle({
                        axisX: {
                            axisTitle: 'Days',
                            axisClass: 'ct-axis-title',
                            offset: {
                                x: 0,
                                y: 50
                            },
                            textAnchor: 'middle'
                        },
                        axisY: {
                            axisTitle: 'Story points',
                            axisClass: 'ct-axis-title',
                            offset: {
                                x: 0,
                                y: 0
                            },
                            textAnchor: 'middle',
                            flipTitle: false
                        }
                    })
                ]
            }
        };
        
        ctrl.RSChart2={
                data : {
                    /*labels: ['Jan 01', 'Jan 02', 'Jan 03', 'Jan 04', 'Jan 05'],
                    series: [
                        [1,4,6,7,9,11]
                    ]*/
                },
                options : {
                    chartPadding: {
                        right: 0,
                        bottom: 30,
                    },
                    plugins: [
                        Chartist.plugins.ctAxisTitle({
                            axisX: {
                                axisTitle: 'Days',
                                axisClass: 'ct-axis-title',
                                offset: {
                                    x: 0,
                                    y: 50
                                },
                                textAnchor: 'middle'
                            },
                            axisY: {
                                axisTitle: 'Story points',
                                axisClass: 'ct-axis-title',
                                offset: {
                                    x: 0,
                                    y: 0
                                },
                                textAnchor: 'middle',
                                flipTitle: false
                            }
                        })
                    ]
                }
            };


        ctrl.SPEChart={
            data : {
                /*labels: ['Jan 01', 'Jan 02', 'Jan 03', 'Jan 04', 'Jan 05','Jan 06'],
                series: [
                    { "name": "Actual Values", "data":[8, 7, 4, 6, 2,1]},
                    { "name": "Estimated Values", "data":[9, 7, 5, 3, 2,1]}
                ]*/
            },
            options : {
                chartPadding: {
                    right: 0,
                    bottom: 30,
                },
                plugins: [
                    Chartist.plugins.legend(),
                    Chartist.plugins.ctAxisTitle({
                        axisX: {
                            axisTitle: 'Days',
                            axisClass: 'ct-axis-title',
                            offset: {
                                x: 0,
                                y: 50
                            },
                            textAnchor: 'middle'
                        },
                        axisY: {
                            axisTitle: 'Values',
                            axisClass: 'ct-axis-title',
                            offset: {
                                x: 0,
                                y: 0
                            },
                            textAnchor: 'middle',
                            flipTitle: false
                        }
                    })
                ]
            }
        };



        ctrl.DIRChart={
            data : {
                /*labels: ['US1', 'US2', 'US3', 'US4', 'US5', 'US6'],
                series: [
                    { "name": "Money A", "data":[5, 4, 3, 7, 5, 10]}
                ]*/
            },
            options :{
                chartPadding: {
                    right: 0,
                    bottom: 30,
                },
                plugins: [
                    Chartist.plugins.ctAxisTitle({
                        axisX: {
                            axisTitle: 'User story',
                            axisClass: 'ct-axis-title',
                            offset: {
                                x: 0,
                                y: 50
                            },
                            textAnchor: 'middle'
                        },
                        axisY: {
                            axisTitle: 'Defects',
                            axisClass: 'ct-axis-title',
                            offset: {
                                x: 0,
                                y: 0
                            },
                            textAnchor: 'middle',
                            flipTitle: false
                        }
                    })
                ]
            }
        };

        //####


		function load() {
			var deferred = $q.defer();
			jiraDashboardData.details($scope.widgetConfig.componentId).then(function(data){				
				/*console.log(data.result.acceptance);*/
				console.log(data);		    
				
				/*Loop for team velocity*/
				if(data.result.teamVelocity.length!==0)					
					{
					var teamVelocity = data.result.teamVelocity;
					for(var i=0; i<=teamVelocity.length; i++)
						{
						ctrl.barChart.barData.labels.push(teamVelocity[i].SprintName);
						var seriesObjs = {
						      committed : teamVelocity[i].committed,
						      completed : teamVelocity[i].completed						
						};
						ctrl.barChart.barData.series.push(seriesObjs);
					}
					ctrl.sprints = ctrl.barChart.barData.labels;
					}
					
				/*Loop for acceptance coverage*/
				if(data.result.acceptance)
					{
					ctrl.PieChart.data.series.push(data.result.acceptance.covered);
					ctrl.PieChart.data.series.push(data.result.acceptance.notCovered);
					console.log(ctrl.PieChart.data.series);
					}
					
				/*Loop for defect slippage*/
				if(data.result.defectSlippageRate.length!=0){
					var slippageObj = data.result.defectSlippageRate[0];				
					ctrl.DSRPieChart.data.series.push(slippageObj.Production);
					ctrl.DSRPieChart.data.series.push(slippageObj.QA);
				}
				
				/*Loop for release status*/
			    if(data.result.releaseStatus.length!=0){
			    	ctrl.releaseStatusDataSet = data.result.releaseStatus;
			    }
			    
			    /*Loop for burn down chart*/
			    if(data.result.burnDownChart.length!=0){
			    	ctrl.burnDownDataSet = data.result.burnDownChart;
			    }
			    
			    /*Loop for defect injection*/
			    if(data.result.defectInjectionRate.length!=0){
			    	ctrl.defectInjectionDataSet = data.result.defectInjectionRate;
			    }
			    
			    	/*{
			    	var relStatusObj = data.result.releaseStatus;
			    	for(var i=0; i<=relStatusObj.length; i++)
			    		{
			    		var relStatusSprintData = relStatusObj[i];
			    		for()
			    		ctrl.RSChart.data.labels.push(relStatusSprintData.);
			    		}
			    	}*/
			    	
			    	
					
					
					
			});
			return deferred.promise;
		}
		function processjiraDashboardData(data) {
		  console.log("In process project data");
		  $scope.subtitle = data.result.version.projectName;
		  
		  if(data.result.sprint && data.result.sprint.sprintStart && data.result.sprint.sprintEnd) {
			 

		  }

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