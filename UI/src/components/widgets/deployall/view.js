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

        ctrl.load = load;
       // ctrl.showDetail = showDetail;
        ctrl.title = "";
        function load() {
                    var deferred = $q.defer();
                    var postData = {
                        "envIds" : []
                    }
//var postData =  $scope.dashboard.application.components[0].collectorItems.DeploymentEnvironment;


                    console.log(postData);
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

//          //  console.log("here..");\
//           var viewData = [];
//            var tprojectgroup = {
//                "projectGroupName":"",
//                "projectGroupId":"",
//                "projects":[]
//            };
//            //templates to position values
//            var tproject = {
//                "projectName":"",
//                "projectId":"",
//                "environments":[]
//            }
//
//            //Add master list of environment with an empty version field
//            for(var i = 0; i < envs.length;i++){
//                var env = {
//                    "name":envs[i].options.envName,
//                    "id":envs[i].options.envId,
//                    "releaseversion":""
//                }
//                tcomp.environments.push(env);
//
//            }
//
//            //Iterate through all the returned items
//            for(var proji = 0; proji < data.length;proji++){
//                //
//                console.log(data[proji].projectName)
//            }
//
//            ctrl.deployAllData = viewData;






         }
    }
 })();