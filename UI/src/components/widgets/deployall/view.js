(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('deployallViewController', deployallViewController);

    deployallViewController.$inject = ['$scope', 'DashStatus', 'deployAllData', 'DisplayState', '$q', '$modal'];
    function deployallViewController($scope, DashStatus, deployAllData, DisplayState, $q, $modal) {
        /*jshint validthis:true */
        var ctrl = this;

        // public variables
        ctrl.environments = [];
        ctrl.statuses = DashStatus;
        $scope.events=[] ;
        ctrl.load = load;
       // ctrl.showDetail = showDetail;
        ctrl.title = "";
        function load() {
            var deferred = $q.defer();
            var postData = {
                "envIds" : []
            }
           var envs = $scope.dashboard.application.components[0].collectorItems.DeploymentEnvironment;
            for(var envi = 0; envi < envs.length;envi++){
                postData.envIds.push(envs[envi].id);
                ctrl.environments.push(envs[envi].options.envName);
            }

            deployAllData.details(postData).then(function(data,envs) {
                processResponse(data.result,envs);
                            deferred.resolve(data.lastUpdated);
            });
            document.getElementById("deployTableContainer").style.setProperty('height',(screen.height - 260) + 'px');
            document.getElementById("container").style.setProperty('overflow','hidden');
             return deferred.promise;
        }



         function processResponse(data,envs){
            var envs = $scope.dashboard.application.components[0].collectorItems.DeploymentEnvironment;
            var viewData = [];
            var now = moment().startOf('day');

            //Adding chart data to envs
            for(var ci = 0; ci < envs.length; ci++){
                envs[ci].options.chart = {

                             "Last_5_Days":0,
                             "Last_10_Days":0,
                             "Over_10_Days":0

                 }
            }
            //Add master list of environment with an empty version field
            function addEnvs(project){
                var n1 = moment().startOf('day');
                for(var i = 0; i < envs.length;i++){
                    var env = {
                        "name":envs[i].options.envName,
                        "id":envs[i].options.envId,
                        "releaseVersion":"",
                        "completedDate":0,
                        "versionColor":"#ff0000",
                        "versionDays":"0",
                        "today":n1
                    }
                    project.environments.push(env);
                }
                return project;
            }

            function convertToChartData(chart){
                var chartData = {
                	      	labels: ['< 5 Days','< 10  Days','> 10 Days'],
                	      	series: [chart.Last_5_Days,chart.Last_10_Days,chart.Over_10_Days],
                	      	//colors:['green','orange','red']
                	      	colors:["#333", "#222", "#111"]
                	      }
                	      return chartData;
            }

            function updateChartForEnv(envId,color){

                 for(var ci = 0; ci < envs.length; ci++){
                        if(envs[ci].options.envId == envId){

                            if(color == "white"){
                                envs[ci].options.chart.Last_5_Days = envs[ci].options.chart.Last_5_Days + 1;
                            }else if(color == "orange"){
                                envs[ci].options.chart.Last_10_Days = envs[ci].options.chart.Last_10_Days + 1;
                            }else{
                                envs[ci].options.chart.Over_10_Days = envs[ci].options.chart.Over_10_Days + 1;
                            }
                            break;
                        }
//
                  }
//
            }

            for(var itmi = 0; itmi < data.length;itmi++){ 
                           //find the appropriate project group 
               var pgIdx = -1; 
               var now = moment().startOf('day');
               var versionDays = now.diff(moment(data[itmi].completedDate),'days');
               var versionColor = "#FF0000";
               var addChartColor = "white";
               if(versionDays <= 5)
                    {
                        versionColor = "#ffffff"; //white
                        addChartColor = "white";
                    }
               else if(versionDays > 5 && versionDays <= 10)
                    {
                        versionColor = "#FFA500"; //orange
                        addChartColor = "orange";
                    }
               else {
                    versionColor = "#FF0000"; //red
                    addChartColor = "red";
               }
               updateChartForEnv(data[itmi].environmentId,addChartColor);
               for(var vd =0; vd < viewData.length;vd++){ 
                  if(viewData[vd].projectGroupName == data[itmi].projectGroupName){ 
                         pgIdx = vd; 
                         break; 
                  }  
               } 
               if(pgIdx < 0){

                    var tprojectgroup = {
                        "projectGroupName":data[itmi].projectGroupName,
                        "projectGroupId":"",
                        "projects":[]
                    };
                    //templates to position values
                    var tproject = {
                        "projectName":data[itmi].projectName,
                        "projectId":data[itmi].projectId,
                        "environments":[]
                    }

                    tproject = addEnvs(tproject);

                    for(var j = 0; j < tproject.environments.length; j++){
                           if(tproject.environments[j].id == data[itmi].environmentId){
                                tproject.environments[j].releaseVersion = data[itmi].releaseVersion;
                                tproject.environments[j].completedDate = moment(new Date(data[itmi].completedDate)).fromNow();
                                tproject.environments[j].versionColor = versionColor;
                                tproject.environments[j].versionDays = versionDays;
                                tproject.environments[j].now = now;
                                break;
                           }
                    }
                    tprojectgroup.projects.push(tproject);
                    viewData.push(tprojectgroup);
               }else{
                    //find if project found in group
                    //project group would be viewData[pgIdx]
                    var projIdx = -1;
                    for(var i = 0; i < viewData[pgIdx].projects.length;i++){
                        if(viewData[pgIdx].projects[i].projectId == data[itmi].projectId){
                            projIdx = i;
                            break;
                        }
                    }
                    if(projIdx < 0){
                        //new project
                       var tproject = {
                           "projectName":data[itmi].projectName,
                           "projectId":data[itmi].projectId,
                           "environments":[]
                       }

                       tproject = addEnvs(tproject);
                       for(var j = 0; j < tproject.environments.length; j++){
                           // console.log(tproject.environments[j].id);
                           //   console.log(data[itmi].environmentId);
                              if(tproject.environments[j].id == data[itmi].environmentId){
                                   tproject.environments[j].releaseVersion = data[itmi].releaseVersion;
                                   tproject.environments[j].completedDate = moment(new Date(data[itmi].completedDate)).fromNow();
                                   tproject.environments[j].versionColor = versionColor;
                                   tproject.environments[j].versionDays = versionDays;
                                   tproject.environments[j].now = now;
                                   break;
                              }
                       }
                       viewData[pgIdx].projects.push(tproject);
                    }else{
                        //existing project will not hit the list based on project.
                         for(var j = 0; j < viewData[pgIdx].projects[projIdx].environments.length; j++){
                              if(viewData[pgIdx].projects[projIdx].environments[j].id == data[itmi].environmentId){
                                   viewData[pgIdx].projects[projIdx].environments[j].releaseVersion = data[itmi].releaseVersion;
                                   viewData[pgIdx].projects[projIdx].environments[j].completedDate = moment(new Date(data[itmi].completedDate)).fromNow();
                                   viewData[pgIdx].projects[projIdx].environments[j].versionColor = versionColor;
                                   viewData[pgIdx].projects[projIdx].environments[j].versionDays = versionDays;
                                   viewData[pgIdx].projects[projIdx].environments[j].now = now;
                                   break;
                              }
                         }
                    }
               }

            }
            ctrl.viewData  = viewData;
            for(var ci = 0; ci < envs.length; ci++){
                envs[ci].options.chart = convertToChartData(envs[ci].options.chart);
            }
            ctrl.envs = envs;
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
         }
    }
 })();