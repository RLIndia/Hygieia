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
                    deployAllData.details().then(function(data) {
                                    processResponse(data.result);
                                    deferred.resolve(data.lastUpdated);
                                });
                     return deferred.promise;
        }

         function processResponse(data){
            ctrl.deployAllData = data;
            console.log("here..");
            var components = [];
            //templates to position values
            var tcomp = {
                "componentName":"",
                "componentID":"",
                "environments":[]
            }
            var masterenv = []; //master list of environments

            for(var i = 0; i < data.length; i++){

                if(data[i].environmentName == "Test14" || data[i].environmentName == "Load") //to be removed
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
                        console.log("hit here...");
                        console.log(env);
                        masterenv.push(env);
                    }
                }


            }



            console.log(masterenv);

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
                                console.log("Version:" + components[j].environments[k].version);
                                break;
                            }
                        }
                        components[j].environments = JSON.parse(JSON.stringify(components[j].environments));
                    }
                }
            }


            ctrl.environments = masterenv;
            ctrl.components = components;


         }
    }
 })();