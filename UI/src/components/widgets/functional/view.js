(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('functionalViewController', functionalViewController);

    functionalViewController.$inject = ['$scope', 'DashStatus', 'functionalData', 'DisplayState', '$q', '$modal'];
    function functionalViewController($scope, DashStatus, functionalData, DisplayState, $q, $modal) {
        /*jshint validthis:true */
        var ctrl = this;

        // public variables
        ctrl.environments = [];
        ctrl.statuses = DashStatus;

        ctrl.load = load;
        ctrl.showDetail = showDetail;
        ctrl.title = "";
       
        function load() {
            var deferred = $q.defer();
            console.log("*********************");
            console.log($scope.widgetConfig.componentId);
            console.log($scope.dashboard.application.components[0].collectorItems.Deployment[0].options.applicationName);
            ctrl.title = $scope.dashboard.application.components[0].collectorItems.Deployment[0].options.applicationName;
            $scope.subtitle = '[' + ctrl.title + ']';
            $scope.sortType = 'name';
            $scope.sortReverse = false;
            console.log("***********************");
            functionalData.details($scope.widgetConfig.componentId).then(function(data) {
                processResponse(data.result);
                deferred.resolve(data.lastUpdated);
            });
            return deferred.promise;
        }

        function showDetail(environment) {
            $modal.open({
                controller: 'FunctionalDetailController',
                controllerAs: 'detail',
                templateUrl: 'components/widgets/functional/detail.html',
                size: 'lg',
                resolve: {
                    environment: function() {
                        return environment;
                    },
                    collectorName: function () {
                        return $scope.dashboard.application.components[0].collectorItems.Deployment[0].collector.name;
                    }
                }
            });
        }

        function processResponse(data) {
            var worker = {
                getEnvironments: getEnvironments,
                getIsDefaultState: getIsDefaultState
            };

            function getIsDefaultState(data, cb) {
                var isDefaultState = true;
                _(data).forEach(function (environment) {
                    var offlineUnits = _(environment.units).where({'deployed': false}).value().length;

                    if(environment.units && environment.units.length == offlineUnits) {
                        isDefaultState = false;
                    }
                });

                cb(isDefaultState);
            }

            function getEnvironments(data, cb) {
                var environments = _(data).map(function (item) {

                    return {
                        name: item.name,
                        url: item.url,
                        units: item.units,
                        serverUpCount: getServerOnlineCount(item.units, true),
                        serverDownCount: getServerOnlineCount(item.units, false),
                        failedComponents: getFailedComponentCount(item.units),
                        lastUpdated: getLatestUpdate(item.units),
                        deployedVersion:getDeployedVersion(item.units),
                    };
                    
                    function getDeployedVersion(units){
                    	if(units.length) {
                    		return units[0].version;
                    	}
                    	
                    }

                    function getFailedComponentCount(units) {
                        return _(units).where({'deployed':false}).value().length;
                    }

                    function getServerOnlineCount(units, isOnline) {
                        var total = 0;
                        _(units).forEach(function (unit) {
                            total += _(unit.servers).where({'online':isOnline})
                                .value()
                                .length;
                        });

                        return total;
                    }

                    function getLatestUpdate(units) {
                        return _.max(units, function(unit) {
                            return unit.lastUpdated;
                        }).lastUpdated;
                    }
                }).value();

                cb({
                    environments: environments
                });
            }

            worker.getIsDefaultState(data, defaultStateCallback);
            worker.getEnvironments(data, environmentsCallback);
        }

        function defaultStateCallback(isDefaultState) {
            //$scope.$apply(function() {
                $scope.display = isDefaultState ? DisplayState.DEFAULT : DisplayState.ERROR;
            //});
        }

        function environmentsCallback(data) {
            //$scope.$apply(function () {
                ctrl.environments = data.environments;
            //});
        }
    }
})();
