/**
 * Gets deploy related data
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('cucumberData', cucumberData);

    function cucumberData($http) {
        //Api to be updated to project version issues.
        //Api to be updated to project version issues.
        var testDetailRoute = 'test-data/deploy_detail.json';
        var cucumberDataRoute = '/api/quality/test';

        return {
            details: details
        };

        function details(componentId) {
            console.log("Component ID "  + componentId);
            return $http.get(HygieiaConfig.local ? testDetailRoute : cucumberDataRoute + '?componentId=' + componentId)
                .then(function (response) {
                    return response.data;
                });
        }
    }
})();