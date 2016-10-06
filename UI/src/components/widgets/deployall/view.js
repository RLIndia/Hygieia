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
//            var env = {
//                "environmentName":"",
//                "environmentId":"",
//                "version":""
//            }

            for(var i = 0; i < data.length; i++){


                var machingIndx = -1;
                for(var j =0; j < masterenv.length;j++){
                    if(masterenv[j].environmentId == data[i].environmentId){
                        machingIndx = j;
                        continue;
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

            for(var i = 0; i < data.length; i++){
                var machingIndx = -1;
                for(var j =0; j < components.length;j++){
                    if(components[j].componentID == data[i].componentID){
                        machingIndx = j;
                        continue;
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
                               //ar version = data[i].componentVersion;
                               //omponents[j].environments[k].version = version.toString();
                                components[j].environments[k].version = data[i].componentVersion;
                                console.log("Version:" + components[j].environments[k].version);
                                continue;
                            }
                        }
                        components[j].environments = JSON.parse(JSON.stringify(components[j].environments));
                        //To do add version to the matching env.
                    }
                }
            }


            ctrl.environments = masterenv;
            ctrl.components = components;

            function getMatchingEnv(environmentId,callBack){
                    for(var i = 0;i< masterenv.length;i++){
                        console.log('in ' + i);
                        if(masterenv[i].environmentId == environmentId){
                            callBack(i);
                            return;
                        }
                        if(i >= masterenv.length){
                            callBack(-1);
                            return;
                        }
                    }
            }

            function getMachingRow(compId,contexti,callBack){
                var found = false;
                for(var j = 0; j < components.length;j++){
                    if(components[j].componentId == compId){
                        found = true;
                        callBack(components[j],contexti);
                        return;
                    }
                    if(!found && j >= components.length){
                        //send null to Insert a new component
                        callBack(null,contexti);
                        return;
                    }
                }

            }
         }
    }
 })();