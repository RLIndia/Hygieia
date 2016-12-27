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
                    for(var envi = 0; envi < postData.length;envi++){
//                        delete postData[envi]["enabled"];
//                        delete postData[envi]["pushed"];
//                        delete postData[envi]["collectorId"];
//                        delete postData[envi]["lastUpdated"];
                        postData.envIds.push(envs[envi].id);

                    }

                    deployAllData.details(postData).then(function(data) {
                                console.log(data);
                               //     processResponse(data.result);
                                    deferred.resolve(data.lastUpdated);
                                });
                                document.getElementById("deployTableContainer").style.setProperty('height',(screen.height - 260) + 'px');
                                document.getElementById("container").style.setProperty('overflow','hidden');
                     return deferred.promise;
        }

         function processResponse(data){
            ctrl.deployAllData = data;
          //  console.log("here..");
            var components = [];
            //templates to position values
            var tcomp = {
                "componentName":"",
                "componentID":"",
                "environments":[]
            }
            var masterenv = []; //master list of environments
            var masterversions = []; //used for coloring.

            var colors = ["#FFFFFF","#FF0000","#5F9EA0","#00FFFF","#A9A9A9","#9932CC","#FFD700","#DEB887","#CD5C5C","#90EE90",
            "#9370D8","#C71585","#4169E1","#9ACD32","#8A2BE2","#d39e5e","#D39E5E","#5ED3CE","#5E5ED3","#838397","#E5F064","#DE7C5A"];
            var lastAllotedColorIndex = 0;
            for(var i = 0; i < data.length; i++){

               //if(data[i].environmentName == "Test14" || data[i].environmentName == "Load")
             //{//to be removed
                    var machingIndx = -1;
                    for(var j =0; j < masterenv.length;j++){
                        if(masterenv[j].environmentID == data[i].environmentID){
                            machingIndx = j;
                            break;
                        }
                    }
                    if(machingIndx < 0){
                        var env = {
                            "environmentName":data[i].environmentName,
                            "environmentID":data[i].environmentID
                        }
              //          console.log("hit here...");
               //         console.log(env);
                        masterenv.push(env);
                    }
             //   }


            }



           // console.log(masterenv);

            for(var i = 0; i < data.length; i++){
                var machingIndx = -1;
                for(var j =0; j < components.length;j++){
                    if(components[j].componentID == data[i].componentID){
                        machingIndx = j;
                        break;
                        //To do add version to the matching env.
                    }
                }
                if(machingIndx < 0){
                    var comp = {
                        "componentID": data[i].componentID,
                        "componentName": data[i].componentName,
                        "environments": []
                    }
                    comp.environments = masterenv;
                    components.push(comp);
                }
                for(var j =0; j < components.length;j++){
                    if(components[j].componentID == data[i].componentID){
                        //find the env and update the version
                        for(var k =0; k < components[j].environments.length;k++){
                            if(components[j].environments[k].environmentID == data[i].environmentID){
                                components[j].environments[k].version = data[i].componentVersion;
                                components[j].environments[k].color = getColorCodeForVersion(components[j].environments[k].version);
                           //     console.log("Color:" + components[j].environments[k].color);
                          //      console.log("Version:" + components[j].environments[k].version);
                                break;
                            }
                        }
                        components[j].environments = JSON.parse(JSON.stringify(components[j].environments));
                    }
                }
            }


            ctrl.environments = masterenv;
            ctrl.components = components;

            function getColorCodeForVersion(version){
                var found = false;
                for(var x = 0; x < masterversions.length; x++){
                    if(masterversions[x].version == version){
                        found = true;
                     //   console.log(masterversions);
                        return masterversions[x].color;
                    }
                }
                if(!found){
                    if(lastAllotedColorIndex > 21)
                        lastAllotedColorIndex = 0;
                    var v = {"version":version,"color":JSON.parse(JSON.stringify(colors[lastAllotedColorIndex]))};
                    masterversions.push(v);
                   //onsole.log(masterversions);
                     //now it should find it.
                    lastAllotedColorIndex++;
                    return(v.color);

                }
            }

         }
    }
 })();