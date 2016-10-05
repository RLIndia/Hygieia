/**
 * Gets deploy related data
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('chefData', chefData);

    function chefData($http) {
        //Api to be updated to project version issues.
        //Api to be updated to project version issues.
        var testDetailRoute = 'test-data/deploy_detail.json';
        var chefDataRoute = '/api/chefData/';

        return {
            details: details
        };

        function details(componentId) {
            console.log("Component ID "  + componentId);
            return $http.get(HygieiaConfig.local ? testDetailRoute : chefDataRoute + componentId)
                .then(function (response) {
                    return response.data;
                });
        }
    }
})();