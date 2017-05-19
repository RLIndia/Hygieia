(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('productViewController', productViewController);

    productViewController.$inject = ['$scope', '$document', '$modal', '$location', '$q', '$routeParams', '$timeout', 'buildData','deployData', 'codeAnalysisData', 'collectorData', 'dashboardData', 'pipelineData', 'testSuiteData', 'productBuildData', 'productCodeAnalysisData', 'productCommitData', 'productSecurityAnalysisData', 'productTestSuiteData'];
    function productViewController($scope, $document, $modal, $location, $q, $routeParams, $timeout, buildData,deployData, codeAnalysisData, collectorData, dashboardData, pipelineData, testSuiteData, productBuildData, productCodeAnalysisData, productCommitData, productSecurityAnalysisData, productTestSuiteData) {
        /*jshint validthis:true */
        var ctrl = this;

        //region Dexie configuration
        // setup our local db
        var db = new Dexie('ProductPipelineDb');
        Dexie.Promise.on('error', function(err) {
            // Log to console or show en error indicator somewhere in your GUI...
            console.log('Uncaught Dexie error: ' + err);
        });

        // IMPORTANT: when updating schemas be sure to version the database
        // https://github.com/dfahlander/Dexie.js/wiki/Design#database-versioning
        db.version(1).stores({
            lastRequest: '[type+id]',
            testSuite: '++id,timestamp,[componentId+timestamp]',
            codeAnalysis: '++id,timestamp,[componentId+timestamp]',
            securityAnalysis: '++id,timestamp,[componentId+timestamp]',
            buildData: '++id,timestamp,[componentId+timestamp]',
            deployData: '++id,timestamp,[componentId+timestamp]',
            prodCommit: '++id,timestamp,[collectorItemId+timestamp]'
        });

        console.log()
        // create classes
        var LastRequest = db.lastRequest.defineClass({
            id: String,
            type: String,
            timestamp: Number
        });

        // ad a convenience method to save back the request
        LastRequest.prototype.save = function() {
            db.lastRequest.put(this);
        };

        db.open();

        // clear out any collection data if there is a reset parameter
        if($routeParams.delete) {
            db.delete().then(function() {
                // redirect to this page without the parameter
                window.location.href = '/#/dashboard/' + $routeParams.id;
            });
        }

        // remove any data from the existing tables
        if($routeParams.reset || HygieiaConfig.local) {
            db.lastRequest.clear();
            db.codeAnalysis.clear();
            db.testSuite.clear();
            db.buildData.clear();
            db.prodCommit.clear();
        }
        // endregion

        // private properties
        var teamDashboardDetails = {},
            isReload = null;

        // public properties
       // ctrl.stages = ['Commit', 'Build', 'Dev', 'QA', 'Int', 'Perf', 'Prod'];
        ctrl.stages = ['Commit', 'Build'];
        ctrl.deployStages = ['dev', 'qa', 'int'];
        ctrl.sortableOptions = {
            additionalPlaceholderClass: 'product-table-tr',
            placeholder: function(el) {
                // create a placeholder row
                var tr = $document[0].createElement('div');
                for(var x=0;x<=ctrl.stages.length;x++) {
                    var td = $document[0].createElement('div');
                    td.setAttribute('class', 'product-table-td');

                    if(x == 0) {
                        // add the name of the row so it somewhat resembles the actual data
                        var name = $document[0].createElement('div');
                        name.setAttribute('class', 'team-name');
                        name.innerText = el.element[0].querySelector('.team-name').innerText;
                        td.setAttribute('class', 'product-table-td team-name-cell');
                        td.appendChild(name);
                    }
                    tr.appendChild(td);
                }

                return tr;
            },
            orderChanged: function() {
                // re-order our widget options
                var teams = ctrl.configuredTeams,
                    existingConfigTeams = $scope.widgetConfig.options.teams,
                    newConfigTeams = [];

                _(teams).forEach(function(team) {
                    _(existingConfigTeams).forEach(function(configTeam) {
                        if(team.collectorItemId == configTeam.collectorItemId) {
                            newConfigTeams.push(configTeam);
                        }
                    });
                });

                $scope.widgetConfig.options.teams = newConfigTeams;
                updateWidgetOptions($scope.widgetConfig.options);
            }
        };

        // public methods
        ctrl.load = load;
        ctrl.addTeam = addTeam;
        ctrl.editTeam = editTeam;
        ctrl.openDashboard = openDashboard;
        ctrl.viewTeamStageDetails = viewTeamStageDetails;
        ctrl.viewQualityDetails = viewQualityDetails;

        // public data methods
        ctrl.teamStageHasCommits = teamStageHasCommits;

        // set our data before we get things started
        var widgetOptions = angular.copy($scope.widgetConfig.options);

        if (widgetOptions && widgetOptions.teams) {
            ctrl.configuredTeams = widgetOptions.teams;
        }

        //region public methods
        function load() {
            // determine our current state
            if (isReload === null) {
                isReload = false;
            }
            else if(isReload === false) {
                isReload = true;
            }

            console.log(">>>>>>>>>>>>>>>>>>Pipeline<<<<<<<<<<<<<<<<");
            //console.log();
            //Commented below line on 10-May-2017 to display deploy data from deploy widget. Leiu code is processDeployment : Vinod
            //collectTeamStageData(widgetOptions.teams, [].concat(ctrl.stages));

            var requestedData = getTeamDashboardDetails(widgetOptions.teams);
            console.log('*************** in load product requested data view ****************');
            console.log(teamDashboardDetails);
            console.log('*************** end in load product requested data view ****************');
            if(!requestedData) {
                for(var collectorItemId in teamDashboardDetails) {
                    getTeamComponentData(collectorItemId);
                }
            }
        }

        // remove data from the db where data is older than the provided timestamp
        function cleanseData(table, beforeTimestamp) {
            table.where('timestamp').below(beforeTimestamp).toArray(function(rows) {
                _(rows).forEach(function(row) {
                    table.delete(row.id);
                })
            });
        }

        function addTeam() {
            $modal.open({
                templateUrl: 'components/widgets/product/add-team/add-team.html',
                controller: 'addTeamController',
                controllerAs: 'ctrl'
            }).result.then(function(config) {
                if(!config) {
                    return;
                }

                // prepare our response for the widget upsert
                var options = $scope.widgetConfig.options;

                // make sure it's an array
                if(!options.teams || !options.teams.length) {
                    options.teams = [];
                }

                // add our new config to the array
                options.teams.push(config);

                updateWidgetOptions(options);
            });
        }

        function editTeam(collectorItemId) {
            var team = false;
            _($scope.widgetConfig.options.teams)
                .filter({collectorItemId: collectorItemId})
                .forEach(function(t) {
                    team = t;
                });

            if(!team) { return; }

            $modal.open({
                templateUrl: 'components/widgets/product/edit-team/edit-team.html',
                controller: 'editTeamController',
                controllerAs: 'ctrl',
                resolve: {
                    editTeamConfig: function() {
                        return {
                            team: team
                        }
                    }
                }
            }).result.then(function(config) {
                if(!config) {
                    return;
                }

                var newOptions = $scope.widgetConfig.options;

                // take the collector item out of the team array
                if(config.remove) {
                    // do remove
                    var keepTeams = [];

                    _(newOptions.teams).forEach(function(team) {
                        if(team.collectorItemId != config.collectorItemId) {
                            keepTeams.push(team);
                        }
                    });

                    newOptions.teams = keepTeams;
                }
                else {
                    for(var x=0;x<newOptions.teams.length;x++) {
                        if(newOptions.teams[x].collectorItemId == config.collectorItemId) {
                            newOptions.teams[x] = config;
                        }
                    }
                }

                updateWidgetOptions(newOptions);
            });
        }

        function openDashboard(item) {
            var dashboardDetails = teamDashboardDetails[item.collectorItemId];
            if(dashboardDetails) {
                $location.path('/dashboard/' + dashboardDetails.id);
            }
        }

        function viewTeamStageDetails(team, stage) {
            // only show details if we have commits
            if(!teamStageHasCommits(team, stage)) {
                console.log('not teamStageHasCommits **********');
                return false;
            }

            console.log('in teamStageHasCommits **********');

            $modal.open({
                templateUrl: 'components/widgets/product/environment-commits/environment-commits.html',
                controller: 'productEnvironmentCommitController',
                controllerAs: 'ctrl',
                size: 'lg',
                resolve: {
                    modalData: function() {
                        return {
                            team: team,
                            stage: stage,
                            stages: ctrl.stages
                        };
                    }
                }
            });
        }

        function viewQualityDetails(team, stage, metricIndex) {
            $modal.open({
                templateUrl: 'components/widgets/product/quality-details/quality-details.html',
                controller: 'productQualityDetailsController',
                controllerAs: 'ctrl',
                size: 'lg',
                resolve: {
                    modalData: function() {
                        return {
                            team: team,
                            stage: stage,
                            metricIndex: metricIndex
                        }
                    }
                }
            })
        }
        //endregion

        //region private methods
        function setTeamData(collectorItemId, data) {
            var team = false,
                idx = false;

            _(ctrl.configuredTeams).forEach(function(configuredTeam, i) {
                if(configuredTeam.collectorItemId == collectorItemId) {
                    idx = i;
                    team = configuredTeam;
                }
            });

            if(!team) { return; }

            var obj = ctrl.configuredTeams[idx];

            // hackish way to update the configured teams object in place so their entire
            // object does not need to be replaced which would cause a full refresh of the
            // row instead of just the numbers. some deep merge tools did not replace everything
            // correctly so this way we can be explicit in the behavior
            for(var x in data) {
                var xData = data[x];
                if(typeof xData == 'object' && obj[x] != undefined) {
                    for(var y in xData) {
                        var yData = xData[y];

                        if(typeof yData == 'object' && obj[x][y] != undefined) {
                            for (var z in yData) {
                                var zData = yData[z];
                                obj[x][y][z] = zData;
                            }
                        }
                        else {
                            obj[x][y] = yData;
                        }
                    }
                }
                else {
                    obj[x] = xData;
                }
            }
        }

        function getTeamDashboardDetails(teams) {
            console.log('*************** in teams ***********************');
            console.log(teams);
            var update = false;
            _(teams).forEach(function(team) {
                if(!teamDashboardDetails[team.collectorItemId]) {
                    update = true;
                }
            });

            // if we already have all the teams, don't make the call
            if (!update) {
                return false;
            }

            console.log('*************** in teams 1***********************');

            // let's grab our products and update all the board info
            collectorData.itemsByType('product').then(function(response) {
                console.log(response);
                _(teams).forEach(function(team) {
                    console.log(team);
                    _(response).forEach(function(board) {
                        if (team.collectorItemId == board.id) {
                            dashboardData.detail(board.options.dashboardId).then(function(result) {
                            //Section introduced by Vinod to support Octopus Deploy
                            console.log('*************** in teams 2***********************');
                            console.log(result);
                                teamDashboardDetails[team.collectorItemId] = result;
                                //try fetching deploy details here
                                var mappings = [];
                                _(result.widgets).filter({name:'pipeline'}).forEach(function(pipeline){
                                    mappings = pipeline.options.mappings;

                                });

                                _(result.widgets).filter({name:'deploy'}).forEach(function(widget){
                                  //  var componentId = widget.componentId;
                                    deployData.details(widget.componentId).then(function(deploys){
                                       console.log('Got a deployment');
                                       //team.stages = ctrl.deployStages;
                                       team.stages = [];
                                       for(var s =0;s < ctrl.deployStages.length;s++){
                                        team.stages[ctrl.deployStages[s]] = {
                                        };
                                       }
                                       processDeployment(mappings,deploys,team);
                                    });
                                });



                                getTeamComponentData(team.collectorItemId);
                            });
                        }
                    });
                });
            });

            return true;
        }

//        Object.prototype.getKey = function(value){
//          for(var key in this){
//           // console.log(this[key] + ' ' + value);
//            if(this[key].toLowerCase() == value.toLowerCase()){
//                console.log(key);
//              return key;
//            }
//          }
//          return null;
//        };

        function findInArray(list,value){
           for (var key in list) {
             if (list.hasOwnProperty(key)) {
               console.log(key + " -> " + list[key]);
               if(list[key].toLowerCase() == value.toLowerCase())
                {
                    console.log("found returning " + key);
                    return(key);
                }
             }
           }
//            for(var li in list){
//                if(list[li].toLowerCase() == value.toLowerCase()){
//
//                    return(keys[li]);
//                }
//            }
            return null;
        }

        function processDeployment(mappings,deploys,team){

              // team.stages[stage].summary
             // var summary = {};
              console.log(mappings);
              console.log(deploys);
              console.log(team);
            //  to get
         //     summary.average.days, summary.deviation.number , lastUpdated.shortDisplay,  version.number

            //iterate over deploy stages
                // find the stage in mappings


                   console.log('*************** Printing teams ****************');
                   console.log(team);
                   if(!team) {
                       return;
                   }

                   var nowTimestamp = moment().valueOf();
                   // loop through each team and request pipeline data
                   //_(team).forEach(function(configuredTeam) {
                   //expected : team.stages[stage].summary

                        _(deploys.result).forEach(function(deploy){
                            var summary = {};
                            //get the stage name from mappings
                            if(deploy.units.length > 0)
                            {
                            summary.version = deploy.units[0].version;
                            if(deploy.units[0].servers.length > 0) {
                                summary.server = deploy.units[0].servers[0];
                               // summary.server.online = deploy.units[0].servers[0].online;
                            }
                            summary.deployed =  deploy.units[0].deployed;
                            summary.lastUpdated = deploy.units[0].lastUpdated;
                            summary.units = deploy.units;
                            }
                            console.log(summary);
                            console.log(team.stages);
                            console.log(deploy.name);
                            //console.log(findInArray(mappings,deploy.name));
                            team.stages[findInArray(mappings,deploy.name)].summary = summary;
                        });


//                       var commitDependencyObject = {
//                           db: db,
//                           configuredTeam: configuredTeam,
//                           nowTimestamp: nowTimestamp,
//                           setTeamData: setTeamData,
//                           cleanseData: cleanseData,
//                           pipelineData: pipelineData,
//                           $q: $q,
//                           ctrlStages: ctrlStages
//                       };
//
//                       productCommitData.process(commitDependencyObject);

        }


        function updateWidgetOptions(options) {
            // get a list of collector ids
            var collectorItemIds = [];
            _(options.teams).forEach(function(team) {
                collectorItemIds.push(team.collectorItemId);
            });

            var data = {
                name: 'product',
                componentId: $scope.dashboard.application.components[0].id,
                collectorItemIds: collectorItemIds,
                options: options
            };

            $scope.upsertWidget(data);
        }

        // return whether this stage has commits. used to determine whether details
        // will be shown for this team in the specific stage
        function teamStageHasCommits(team, stage) {
            return team.stages && team.stages[stage] && team.stages[stage].commits && team.stages[stage].commits.length;
        }

        function getTeamComponentData(collectorItemId) {
            var team = teamDashboardDetails[collectorItemId],
                componentId = team.application.components[0].id;

            function getCaMetric(metrics, name, fallback) {
                var val = fallback === undefined ? false : fallback;
                _(metrics).filter({name:name}).forEach(function(item) {
                    val = item.value || parseFloat(item.formattedValue);
                });
                return val;
            }

            var processDependencyObject = {
                db: db,
                componentId: componentId,
                collectorItemId: collectorItemId,
                setTeamData: setTeamData,
                cleanseData: cleanseData,
                isReload: isReload,
                $timeout: $timeout,
                $q: $q
            };

            console.log('*************** in processDependencyObject ****************');
            console.log(processDependencyObject);
            console.log('*************** end processDependencyObject ****************');

            // request and process our data
            productBuildData.process(angular.extend(processDependencyObject, { buildData: buildData }));
            productSecurityAnalysisData.process(angular.extend(processDependencyObject, { codeAnalysisData: codeAnalysisData, getCaMetric: getCaMetric }));
            productCodeAnalysisData.process(angular.extend(processDependencyObject, { codeAnalysisData: codeAnalysisData, getCaMetric: getCaMetric }));
            productTestSuiteData.process(angular.extend(processDependencyObject, { testSuiteData: testSuiteData }));
        }``

        function collectTeamStageData(teams, ctrlStages) {
            // no need to go further if teams aren't configured
            console.log('*************** Printing teams ****************');
            console.log(teams);
            if(!teams || !teams.length) {
                return;
            }

            var nowTimestamp = moment().valueOf();
            // loop through each team and request pipeline data
            _(teams).forEach(function(configuredTeam) {
                var commitDependencyObject = {
                    db: db,
                    configuredTeam: configuredTeam,
                    nowTimestamp: nowTimestamp,
                    setTeamData: setTeamData,
                    cleanseData: cleanseData,
                    pipelineData: pipelineData,
                    $q: $q,
                    ctrlStages: ctrlStages
                };

                productCommitData.process(commitDependencyObject);
            });
        }
        //endregion
    }
})();
