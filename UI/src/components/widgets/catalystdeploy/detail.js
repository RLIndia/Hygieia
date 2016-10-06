(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('Catalystdeployhistorycontroller', Catalystdeployhistorycontroller);

    Catalystdeployhistorycontroller.$inject = ['$scope','$modalInstance', 'catalystdeployhistorydata', 'DashStatus','deployment','taskId','$q'];
    function Catalystdeployhistorycontroller($scope,$modalInstance, catalystdeployhistorydata, DashStatus,deployment,taskId,$q) {
        /*jshint validthis:true */
        var ctrl = this;
        
        ctrl.statuses = DashStatus;

        console.log("In Catalystdeployhistorycontroller");
        //console.log('statuses ==>',DashStatus,environment,collectorName)
       // ctrl.environmentName = day.testCases.results[0].envName;
       
     //   ctrl.historydata = {}; //.details(catalystdeployhistorydata.taskId);
        ctrl.close = close;
        ctrl.deployment = deployment;
        ctrl.taskId = taskId;
       // ctrl.historydata = catalystdeployhistorydata.details(taskId);
        ctrl.load = load;
        function load(){
            var deferred = $q.defer();
            console.log("In Load details...");
            catalystdeployhistorydata.details(taskId).then(function(hd){

                ctrl.historydata = processData(hd);

                // var hd1= [];
                // for(var i = 0; i < hd.length;i++){
                //     hd[i].nodeNames = JSON.parse(hd[i].nodeNames);
                //     hd[i].nodes = JSON.parse(hd[i].nodeNames);
                //     console.log(hd[i].nodes);
                //     hd1.push(hd[i]);
                // }
                // ctrl.historydata = hd1;
            });
            return deferred.promise;
        }

        function processData(hd){
            // for(i = 0; i < hd.result.deploymenthistory.length;i++){
            //     console.log(hd.result.deploymenthistory[i].nodeNames);
            //     hd.result.deploymenthistory[i].nodeNames = JSON.parse(hd.result.deploymenthistory[i].nodeNames);
            // }
            return hd;
        }
        //$scope.catalysttaskhistory = catalystdeployhistorydata.details(taskId);
        
        function close() {
            $modalInstance.dismiss('close');
        }
        ctrl.load();
    }
})();
