(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('Catalystdeployhistorycontroller', Catalystdeployhistorycontroller);

    Catalystdeployhistorycontroller.$inject = ['$modalInstance', 'catalystdeployhistorydata', 'collectorName','collector', 'DashStatus'];
    function Catalystdeployhistorycontroller($modalInstance, catalystdeployhistorydata, collectorName, collector,DashStatus) {
        /*jshint validthis:true */
        var ctrl = this;
        
        ctrl.statuses = DashStatus;
        //console.log('statuses ==>',DashStatus,environment,collectorName)
       // ctrl.environmentName = day.testCases.results[0].envName;
        ctrl.collectorName = collectorName;
        ctrl.historydata = catalystdeployhistorydata;
        ctrl.close = close;
        ctrl.collector = collector;
        
        function close() {
            $modalInstance.dismiss('close');
        }
    }
})();
