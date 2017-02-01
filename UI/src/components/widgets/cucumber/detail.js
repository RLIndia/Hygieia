(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('cucumberviewresults', cucumberviewresults);

    cucumberviewresults.$inject = ['$scope','$modalInstance', 'cucumberData', 'collectorName','collector', 'DashStatus','buildNumber','cucumberResult','componentId','instanceUrl','jobName','jobUrl','$q'];
    function cucumberviewresults($scope,$modalInstance, cucumberData, collectorName, collector,DashStatus,buildNumber,cucumberResult,componentId,instanceUrl,jobName,jobUrl,$q) {
        /*jshint validthis:true */
        var ctrl = this;
        
        ctrl.statuses = DashStatus;
        //console.log('statuses ==>',DashStatus,environment,collectorName)
        ctrl.cucumberviewresultsdata = cucumberData;
        ctrl.collectorName = collectorName;
        ctrl.close = close;
        ctrl.collector = collector;
        ctrl.load = load;
        ctrl.instanceUrl = instanceUrl;
        ctrl.jobName = jobName;
        ctrl.buildNumber=buildNumber;
        ctrl.cucumberResult=cucumberResult;
        console.log('detail view');
        console.log(cucumberResult);
        //ctrl.runName = runName;
        function load(){
            var deferred = $q.defer();
            console.log("In Load details..." + instanceUrl + " JobName " + jobName);
            
            cucumberData.details(componentId).then(function(data) {
                processData(data.result[0]);
                deferred.resolve(data.lastUpdated);
            });

            return deferred.promise;
        }

        function processData(results){
            // for(i = 0; i < hd.result.deploymenthistory.length;i++){
            //     console.log(hd.result.deploymenthistory[i].nodeNames);
            //     hd.result.deploymenthistory[i].nodeNames = JSON.parse(hd.result.deploymenthistory[i].nodeNames);
            // }
            return results.testCapabilities;
        }
        function close() {
            $modalInstance.dismiss('close');
        }
        load();
    }
})();