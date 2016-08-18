(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('FunctionalDetailController', FunctionalDetailController);

    FunctionalDetailController.$inject = ['$modalInstance', 'day', 'collectorName', 'DashStatus'];
    function FunctionalDetailController($modalInstance, day, collectorName, DashStatus) {
        /*jshint validthis:true */
        var ctrl = this;
        
        ctrl.statuses = DashStatus;
        console.log('statuses ==>',DashStatus,environment,collectorName)
        ctrl.environment = environment;
        ctrl.collectorName = collectorName;

        ctrl.close = close;

        function close() {
            $modalInstance.dismiss('close');
        }
    }
})();
