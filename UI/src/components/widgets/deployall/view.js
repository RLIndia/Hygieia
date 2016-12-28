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

         function processResponse(data,envs){


  console.log("here..");
            var envs = $scope.dashboard.application.components[0].collectorItems.DeploymentEnvironment;
            var viewData = [];

         //   console.log(data);
      //      console.log(envs);
//
            //Add master list of environment with an empty version field
            function addEnvs(project){
                for(var i = 0; i < envs.length;i++){
                var env = {
                    "name":envs[i].options.envName,
                    "id":envs[i].options.envId,
                    "releaseVersion":""
                }
                project.environments.push(env);
                //console.log(env);

                }
                return project;
            }

            for(var itmi = 0; itmi < data.length;itmi++){ 
                           //find the appropriate project group 
               var pgIdx = -1; 
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
                                   break;
                              }
                       }
                       viewData[pgIdx].projects.push(tproject);
                    }else{
                        //existing project will not hit the list based on project.
                        console.log("Existing project" + viewData[pgIdx].projects[projIdx].projectName);
                        for(var j = 0; j < viewData[pgIdx].projects[projIdx].environments.length; j++){
                           // console.log(tproject.environments[j].id);
                           //   console.log(data[itmi].environmentId);
                              if(viewData[pgIdx].projects[projIdx].environments[j].id == data[itmi].environmentId){
                                   viewData[pgIdx].projects[projIdx].environments[j].releaseVersion = data[itmi].releaseVersion;
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