/**
 * Gets deploy related data
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('testrailRunData', testrailRunData);

    function testrailRunData($http) {
        //Api to be updated to project version issues.
        //Api to be updated to project version issues.
        var testDetailRoute = 'test-data/deploy_detail.json';
        var testrailRunDataRoute = '/api/testrailruns/';

        return {
            details: details
        };

        function details(componentId) {
            console.log("Component ID "  + componentId);
            return $http.get(HygieiaConfig.local ? testDetailRoute : testrailRunDataRoute + componentId)
                .then(function (response) {
                    return response.data;
                });
        }
    }
})();