

/**
 * Gets all deploy related data
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('deployAllData', deployAllData);

    function deployAllData($http) {
        var testDetailRoute = 'test-data/deploy_detail.json';
        var deployDetailRoute = '/api/deploy/allcomponents';

        return {
            details: details
        };

        function details() {
            return $http.get(HygieiaConfig.local ? testDetailRoute : deployDetailRoute)
                .then(function (response) {
                    return response.data;
                });
        }
    }
})();