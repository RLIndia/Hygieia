(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('FunctionalDetailController', FunctionalDetailController);

    FunctionalDetailController.$inject = ['$modalInstance', 'day', 'collectorName','collector', 'DashStatus'];
    function FunctionalDetailController($modalInstance, day, collectorName, collector,DashStatus) {
        /*jshint validthis:true */
        var ctrl = this;
        
        ctrl.statuses = DashStatus;
        //console.log('statuses ==>',DashStatus,environment,collectorName)
        ctrl.environmentName = day.testCases.results[0].envName;
        ctrl.collectorName = collectorName;
        ctrl.testResults = day;
        ctrl.close = close;
        ctrl.collector = collector;
        
        function close() {
            $modalInstance.dismiss('close');
        }
    }
})();
