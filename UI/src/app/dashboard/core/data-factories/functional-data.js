/**
 * Gets deploy related data
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('functionalData', functionalData);

    function functionalData($http) {
        var testDetailRoute = 'test-data/deploy_detail.json';
        var deployDetailRoute = '/api/functionalTest/';

        return {
            details: details
        };

        function details(componentId) {
            return $http.get(HygieiaConfig.local ? testDetailRoute : deployDetailRoute + componentId)
                .then(function (response) {
                    return response.data;
                });
        }
    }
})();