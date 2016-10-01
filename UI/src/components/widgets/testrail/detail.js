(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('testrailviewresults', testrailviewresults);

    testrailviewresults.$inject = ['$modalInstance', 'testrailviewresultsdata', 'collectorName','collector', 'DashStatus','runId','runName','projectId','milestoneId','projectName','milestoneName','$q'];
    function testrailviewresults($modalInstance, testrailviewresultsdata, collectorName, collector,DashStatus,runId,runName,projectId,milestoneId,projectName,milestoneName,$q) {
        /*jshint validthis:true */
        var ctrl = this;
        
        ctrl.statuses = DashStatus;
        //console.log('statuses ==>',DashStatus,environment,collectorName)
        ctrl.testrailviewresultsdata = testrailviewresultsdata;
        ctrl.collectorName = collectorName;
              ctrl.close = close;
        ctrl.collector = collector;
        ctrl.load = load;
        ctrl.projectName = projectName;
        ctrl.milestoneName = milestoneName;
        ctrl.runName = runName;
        function load(){
            var deferred = $q.defer();
            console.log("In Load details..." + projectId + " milestoneId " + milestoneId);
            testrailviewresultsdata.details(runId,projectId,milestoneId).then(function(results){

                ctrl.results = processData(results);

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

        function processData(results){
            // for(i = 0; i < hd.result.deploymenthistory.length;i++){
            //     console.log(hd.result.deploymenthistory[i].nodeNames);
            //     hd.result.deploymenthistory[i].nodeNames = JSON.parse(hd.result.deploymenthistory[i].nodeNames);
            // }
            return results.result.runresults;
        }
        function close() {
            $modalInstance.dismiss('close');
        }
        load();
    }
})();