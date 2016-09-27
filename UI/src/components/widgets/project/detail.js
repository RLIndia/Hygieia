(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('projectVersionViewDetailController', projectVersionViewDetailController);

    projectVersionViewDetailController.$inject = ['$modalInstance', 'jiraDashboard', 'collectorName','collector', 'DashStatus'];
    function projectVersionViewDetailController($modalInstance, jiraDashboard, collectorName, collector,DashStatus) {
        /*jshint validthis:true */
        var ctrl = this;
        
        ctrl.statuses = DashStatus;
        //console.log('statuses ==>',DashStatus,environment,collectorName)
        ctrl.jiraDashboard = jiraDashboard;
        ctrl.collectorName = collectorName;
              ctrl.close = close;
        ctrl.collector = collector;
        
        function close() {
            $modalInstance.dismiss('close');
        }
    }
})();