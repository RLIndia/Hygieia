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
         }
    }
 })();