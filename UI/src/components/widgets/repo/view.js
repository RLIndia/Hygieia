(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('RepoViewController', RepoViewController);

    RepoViewController.$inject = ['$q', '$scope','codeRepoData', '$modal'];
    function RepoViewController($q, $scope, codeRepoData, $modal) {
        var ctrl = this;
        
        var commitChartOptions = {
            plugins: [
                Chartist.plugins.gridBoundaries(),
                Chartist.plugins.lineAboveArea(),
                Chartist.plugins.pointHalo(),
                Chartist.plugins.ctPointClick({
                    onClick: showDetail
                }),
                Chartist.plugins.axisLabels({
                    stretchFactor: 1.4,
                    axisX: {
                        labels: [
                            moment().subtract(14, 'days').format('MMM DD'),
                            moment().subtract(7, 'days').format('MMM DD'),
                            moment().format('MMM DD')
                        ]
                    }
                }),
                Chartist.plugins.ctPointLabels({
                    textAnchor: 'middle'
                })
            ],
            showArea: true,
            lineSmooth: false,
            fullWidth: true,
            axisY: {
                offset: 30,
                showGrid: true,
                showLabel: true,
                labelInterpolationFnc: function(value) { return Math.round(value * 100) / 100; }
            }
        };

        ctrl.commits = [];
        ctrl.branches = {};
        ctrl.branchNames = [];
        //$scope.selectedDropDownValue='';
        $scope.dropdownList  = [];
        ctrl.$scope =  $scope;
        
        var selectedBranch; 
        
        $scope.test = function(selectedDropDownValue) {
        	var brancheNames = Object.keys(ctrl.branches);
    	    for(var i=0;i<brancheNames.length;i++) {
    	    	ctrl.branches[brancheNames[i]].show = true;
    	    	ctrl.branches[brancheNames[i]].cssClass='';
    	    }
        	ctrl.branches[selectedDropDownValue].show = true;
        	ctrl.branches[selectedDropDownValue].cssClass='commit-chart-visible';
        	selectedBranch = selectedDropDownValue;
        }
        console.log('ctrl ==>',ctrl)
        
       
        //ctrl.showDetail = showDetail;
        ctrl.load = function() {
            $scope.dropdownList  = [];
            var deferred = $q.defer();
            var params = {
                componentId: $scope.widgetConfig.componentId,
                numberOfDays: 14
            };
            console.log("config -==>",$scope.widgetConfig);
            var widgetConfig = $scope.widgetConfig;
            if(widgetConfig.options && widgetConfig.options.scm && widgetConfig.options.scm.name ==="Bitbucket") {
            	params.scmType = 'Bitbucket';
            }
            
            codeRepoData.details(params).then(function(data) {
            	if(params.scmType = 'Bitbucket') {
            		var brancheNames = Object.keys(data.result);
            	    for(var i=0;i<brancheNames.length;i++) {
            	    	 ctrl.branches[brancheNames[i]] = {
            	    			 commits : [],
            	    			 commitChartOptions:commitChartOptions,
            	    			 showDetail:showDetail,
            	    			 groupedCommitData:[],
            	    			 show:true,
            	    			 cssClass:''
            	    	 };
            	    	 processResponseWithBranch(brancheNames[i],data.result[brancheNames[i]], params.numberOfDays);
            	    }
            	    ctrl.branchNames = brancheNames;
            	    $scope.dropdownList = brancheNames;
            	    console.log(ctrl.branches);
            	} else {
            	  processResponse(data.result, params.numberOfDays);
                }
                deferred.resolve(data.lastUpdated);
            });

            console.log("*********************");
            console.log($scope.widgetConfig.componentId);
            console.log($scope.dashboard.application.components[0].collectorItems);
            var url = $scope.dashboard.application.components[0].collectorItems.SCM[0].options.url;
            var urlParts = url.split("/");
            if(urlParts.length>2) {
            	var projectName = urlParts[urlParts.length-1];
            	projectName = projectName.trim();
            	if(!projectName) {
            		projectName = urlParts[urlParts.length-2];
                	projectName = projectName.trim();
            	}
            }
            //ctrl.title = projectName +"/"+$scope.dashboard.application.components[0].collectorItems.SCM[0].options.branch;
            
            ctrl.title = projectName;
            $scope.subtitle = '[' + ctrl.title + ']';
            console.log("***********************");
            return deferred.promise;
        };

        function showDetail(evt) {
            var target = evt.target,
                pointIndex = target.getAttribute('ct:point-index');

            $modal.open({
                controller: 'RepoDetailController',
                controllerAs: 'detail',
                templateUrl: 'components/widgets/repo/detail.html',
                size: 'lg',
                resolve: {
                    commits: function() {
                    	return ctrl.branches[selectedBranch].groupedCommitData[pointIndex];
                    }
                }
            });
        }

        var groupedCommitData = [];
        function processResponse(data, numberOfDays) {
            // get total commits by day
            var commits = [];
            var groups = _(data).sortBy('timestamp')
                .groupBy(function(item) {
                    return -1 * Math.floor(moment.duration(moment().diff(moment(item.scmCommitTimestamp))).asDays());
                }).value();

            for(var x=-1*numberOfDays+1; x <= 0; x++) {
                if(groups[x]) {
                    commits.push(groups[x].length);
                    groupedCommitData.push(groups[x]);
                }
                else {
                    commits.push(0);
                    groupedCommitData.push([]);
                }
            }

            //update charts
            if(commits.length)
            {
                var labels = [];
                _(commits).forEach(function(c) {
                    labels.push('');
                });

                ctrl.commitChartData = {
                    series: [commits],
                    labels: labels
                };
            }


            // group get total counts and contributors
            var today = toMidnight(new Date());
            var sevenDays = toMidnight(new Date());
            var fourteenDays = toMidnight(new Date());
            sevenDays.setDate(sevenDays.getDate() - 7);
            fourteenDays.setDate(fourteenDays.getDate() - 14);

            var lastDayCount = 0;
            var lastDayContributors = [];

            var lastSevenDayCount = 0;
            var lastSevenDaysContributors = [];

            var lastFourteenDayCount = 0;
            var lastFourteenDaysContributors = [];

            // loop through and add to counts
            _(data).forEach(function (commit) {

                if(commit.scmCommitTimestamp >= today.getTime()) {
                    lastDayCount++;

                    if(lastDayContributors.indexOf(commit.scmAuthor) == -1) {
                        lastDayContributors.push(commit.scmAuthor);
                    }
                }

                if(commit.scmCommitTimestamp >= sevenDays.getTime()) {
                    lastSevenDayCount++;

                    if(lastSevenDaysContributors.indexOf(commit.scmAuthor) == -1) {
                        lastSevenDaysContributors.push(commit.scmAuthor);
                    }
                }

                if(commit.scmCommitTimestamp >= fourteenDays.getTime()) {
                    lastFourteenDayCount++;
                    ctrl.commits.push(commit);
                    if(lastFourteenDaysContributors.indexOf(commit.scmAuthor) == -1) {
                        lastFourteenDaysContributors.push(commit.scmAuthor);
                    }
                }

            });

            ctrl.lastDayCommitCount = lastDayCount;
            ctrl.lastDayContributorCount = lastDayContributors.length;
            ctrl.lastSevenDaysCommitCount = lastSevenDayCount;
            ctrl.lastSevenDaysContributorCount = lastSevenDaysContributors.length;
            ctrl.lastFourteenDaysCommitCount = lastFourteenDayCount;
            ctrl.lastFourteenDaysContributorCount = lastFourteenDaysContributors.length;

            function toMidnight(date) {
                date.setHours(0, 0, 0, 0);
                return date;
            }
        }
        
        
        function processResponseWithBranch(branchName,data, numberOfDays) {
            // get total commits by day
        	console.log("processing branchnames = >"+branchName,data);
            var commits = [];
            var groups = _(data).sortBy('timestamp')
                .groupBy(function(item) {
                    return -1 * Math.floor(moment.duration(moment().diff(moment(item.scmCommitTimestamp))).asDays());
                }).value();

            for(var x=-1*numberOfDays+1; x <= 0; x++) {
                if(groups[x]) {
                    commits.push(groups[x].length);
                    ctrl.branches[branchName].groupedCommitData.push(groups[x]);
                }
                else {
                    commits.push(0);
                    ctrl.branches[branchName].groupedCommitData.push([]);
                }
            }

            //update charts
            if(commits.length)
            {
                var labels = [];
                _(commits).forEach(function(c) {
                    labels.push('');
                });

                ctrl.branches[branchName].commitChartData = {
                    series: [commits],
                    labels: labels
                };
            }


            // group get total counts and contributors
            var today = toMidnight(new Date());
            var sevenDays = toMidnight(new Date());
            var fourteenDays = toMidnight(new Date());
            sevenDays.setDate(sevenDays.getDate() - 7);
            fourteenDays.setDate(fourteenDays.getDate() - 14);

            var lastDayCount = 0;
            var lastDayContributors = [];

            var lastSevenDayCount = 0;
            var lastSevenDaysContributors = [];

            var lastFourteenDayCount = 0;
            var lastFourteenDaysContributors = [];

            // loop through and add to counts
            _(data).forEach(function (commit) {

                if(commit.scmCommitTimestamp >= today.getTime()) {
                    lastDayCount++;

                    if(lastDayContributors.indexOf(commit.scmAuthor) == -1) {
                        lastDayContributors.push(commit.scmAuthor);
                    }
                }

                if(commit.scmCommitTimestamp >= sevenDays.getTime()) {
                    lastSevenDayCount++;

                    if(lastSevenDaysContributors.indexOf(commit.scmAuthor) == -1) {
                        lastSevenDaysContributors.push(commit.scmAuthor);
                    }
                }

                if(commit.scmCommitTimestamp >= fourteenDays.getTime()) {
                    lastFourteenDayCount++;
                    ctrl.branches[branchName].commits.push(commit);
                    if(lastFourteenDaysContributors.indexOf(commit.scmAuthor) == -1) {
                        lastFourteenDaysContributors.push(commit.scmAuthor);
                    }
                }

            });

            ctrl.branches[branchName].lastDayCommitCount = lastDayCount;
            ctrl.branches[branchName].lastDayContributorCount = lastDayContributors.length;
            ctrl.branches[branchName].lastSevenDaysCommitCount = lastSevenDayCount;
            ctrl.branches[branchName].lastSevenDaysContributorCount = lastSevenDaysContributors.length;
            ctrl.branches[branchName].lastFourteenDaysCommitCount = lastFourteenDayCount;
            ctrl.branches[branchName].lastFourteenDaysContributorCount = lastFourteenDaysContributors.length;

            function toMidnight(date) {
                date.setHours(0, 0, 0, 0);
                return date;
            }
        }

        
    }
})();
