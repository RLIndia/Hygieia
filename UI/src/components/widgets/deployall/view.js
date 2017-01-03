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
//var postData =  $scope.dashboard.application.components[0].collectorItems.DeploymentEnvironment;


                    //console.log(postData);
                   var envs = $scope.dashboard.application.components[0].collectorItems.DeploymentEnvironment;
                    for(var envi = 0; envi < envs.length;envi++){
//                        delete postData[envi]["enabled"];
//                        delete postData[envi]["pushed"];
//                        delete postData[envi]["collectorId"];
//                        delete postData[envi]["lastUpdated"];
                        postData.envIds.push(envs[envi].id);
                        ctrl.environments.push(envs[envi].options.envName);

                    }

                    deployAllData.details(postData).then(function(data,envs) {
                               // console.log(data);
                                    processResponse(data.result,envs);
                                    deferred.resolve(data.lastUpdated);
                                });
                                document.getElementById("deployTableContainer").style.setProperty('height',(screen.height - 260) + 'px');
                                document.getElementById("container").style.setProperty('overflow','hidden');
                     return deferred.promise;
        }

        function daydiff(caldate){
            return Math.round(((new Date()) - caldate)/(1000*60*60*24));
        }

         function processResponse(data,envs){


  // console.log("here..");
            var envs = $scope.dashboard.application.components[0].collectorItems.DeploymentEnvironment;
            var viewData = [];
            var now = moment().startOf('day');
         //   console.log(data);
      //      console.log(envs);
//
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
                //console.log(env);

                }
                return project;
            }

            for(var itmi = 0; itmi < data.length;itmi++){ 
                           //find the appropriate project group 
               var pgIdx = -1; 
               var now = moment().startOf('day');
               var versionDays = now.diff(moment.utc(data[itmi].completedDate),'days');
               var versionColor = "#FF0000";
               if(versionDays <= 5)
                    {
                        versionColor = "#ffffff";
                    }
               else if(versionDays > 5 && versionDays <= 10)
                    {
                        versionColor = "#FFA500"; //orange
                    }
               else {
                    versionColor = "#FF0000"; //red
               }
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
                       //  console.log(tproject.environments[j].id);
                       //    console.log(data[itmi].environmentId);
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
                        //console.log("Existing project" + viewData[pgIdx].projects[projIdx].projectName);
                        for(var j = 0; j < viewData[pgIdx].projects[projIdx].environments.length; j++){
                           // console.log(tproject.environments[j].id);
                           //   console.log(data[itmi].environmentId);
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
             ctrl.envs = envs;



         }


    }
 })();