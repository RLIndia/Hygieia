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
		//####
		ctrl.barChart={
            barData : {
                labels: [],
                series: [
                    { "name": "Committed", "data":[]},
                    { "name": "Completed", "data":[]}
                ]
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
                    Chartist.plugins.tooltip(),
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
            },
            options : {
                donut: true
            }
        };

        ctrl.SPEChart={
            data : {
                labels: [],
                series: [
                    { "name": "Estimated Values", "data":[]},
                    { "name": "Actual Values", "data":[]}
                ]
            },
            options : {
                showArea: true,
                showLine: true,
                showPoint: true,
                fullWidth: true,
                axisX: {
                    showGrid: false
                },
                chartPadding: {
                    right: 0,
                    bottom: 30,
                },
                plugins: [
                    Chartist.plugins.tooltip(),
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
                            axisTitle: 'Story Points',
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

        ctrl.SPSChart={
        data : {
                labels: [],
                series: [
                    { "name": "User Story", "data":[]},
                    { "name": "Story Points", "data":[]}
                ]
            },
            options : {
                scales: {
                    yAxes: [{
                        position: "left",
                        type: 'linear',
                        id: "y-axis-0"
                    }, {
                        position: "right",
                        type: 'linear',
                        id: "y-axis-1"
                    }]
                },
                chartPadding: {
                    right: 30,
                    bottom: 30,
                },
                plugins: [
                    Chartist.plugins.tooltip(),
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
                            axisTitle: 'Story Points',
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

        ctrl.DSPChart={
            data : {
                labels: ['Critical', 'Blocker', 'Minor'],
                series: [
                    { "name": "Critical", "data":[2, 4, 4]}
                ]
            },
            options :{
                chartPadding: {
                    right: 0,
                    bottom: 30,
                },
                axisY: {
                    labelInterpolationFnc: function(value) {
                        return (value ) + '0 %';
                    }
                },
                plugins: [
                    Chartist.plugins.tooltip(),
                    Chartist.plugins.ctAxisTitle({
                        axisX: {
                            axisTitle: ' ',
                            axisClass: 'ct-axis-title',
                            offset: {
                                x: 0,
                                y: 50
                            },
                            textAnchor: 'middle'
                        },
                        axisY: {
                            axisTitle: 'Defects (%)',
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
                // labels: ['PSI-SLZ-12.4', 'PSI-SLZ-13.1', 'PSI-SLZ-13.2', 'PSI-SLZ-13.3', 'PSI-SLZ-13.5','PSI-SLZ-13.13', 'PSI-SLZ-13.15'],
                // series: [[70, 80, 110, 73,64,64,82]
                // ]
            },
            options :{
                chartPadding: {
                    right: 0,
                    bottom: 30,
                },
                axisY: {
                    offset: 30,
                    showLabel: true,
                    // labelInterpolationFnc: function(value) {
                    //     return value === 0 ? 0 : ((Math.round(value / 100 * 10)) + '%');
                    // }
                },
                plugins: [
                    Chartist.plugins.tooltip(),
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
                            axisTitle: 'Defects (%)',
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
			jiraDashboardData.details($scope.widgetConfig.componentId).then(function(data){
               // ctrl.barChart.barData={};
               ctrl.projectName=data.result.version.projectName;
                ctrl.versionName=data.result.version.versionName;

                // acceptanceCover
                ctrl.acceptanceCover=(Math.round(data.result.acceptance.covered/data.result.acceptance.Total)*100);
                var notCovered=(Math.round(data.result.acceptance.notCovered/data.result.acceptance.Total)*100);

                ctrl.PieChart.data={
                    series: [ctrl.acceptanceCover,notCovered ],
                    labels: [' ',' ']
                }
                //defectSlippageRate

                ctrl.defectSlippageRate=data.result.defectSlippageRate;
                //defectInjectionRate
                ctrl.DIRChart.data={}
                ctrl.DIRChart.data ={
                        labels: [],
                        series: []
                    };
                var dataSeries=[];
                angular.forEach(data.result.defectInjectionRate,function (val) {
                    ctrl.DIRChart.data.labels.push(val.SprintName);
                    dataSeries.push(val.InjectionRatio);

                });
                ctrl.DIRChart.data.series.push(dataSeries);

                //teamVelocity
                ctrl.barChart.barData ={
                        labels: [],
                        series: [
                            { "name": "Committed", data:[]},
                            { "name": "Completed", data:[]}]
                        };
                angular.forEach(data.result.teamVelocity,function (val) {
                    ctrl.barChart.barData.labels.push(val.SprintName);
                    ctrl.barChart.barData.series[0].data.push(val.Committed);
                    ctrl.barChart.barData.series[1].data.push(val.Completed);
                });

                //Release Status
                ctrl.SPEChart.data ={
                    labels: [],
                    series: [
                        { "name": "Estimated Values", data:[]},
                        { "name": "Actual Values", data:[]}]
                };
                angular.forEach(data.result.teamVelocity,function (val) {
                    ctrl.SPEChart.data.labels.push(val.SprintName);
                    ctrl.SPEChart.data.series[0].data.push(val.Committed);
                    ctrl.SPEChart.data.series[1].data.push(val.Completed);
                });

                //IssueStoryPoints
                ctrl.SPSChart.data ={
                    labels: [],
                    series: [
                        { "name": "Story Points", data:[]},
                        { "name": "User Story", data:[]}]
                };
                angular.forEach(data.result.IssueStoryPoints,function (val) {
                    ctrl.SPSChart.data.labels.push(val.SprintName);
                    ctrl.SPSChart.data.series[0].data.push(val.StoryPoint);
                    ctrl.SPSChart.data.series[1].data.push(val.StoryCount);
                });

			});
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
