/**
 * Gets deploy related data
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('projectData', projectData);

    function projectData($http) {
        //Api to be updated to project version issues.
        //Api to be updated to project version issues.
        var testDetailRoute = 'test-data/deploy_detail.json';
        var projectversionissuesRoute = '/api/projectVersionIssues/';

        return {
            details: details
        };

        function details(componentId) {
            console.log("Component ID "  + componentId);
            return $http.get(HygieiaConfig.local ? testDetailRoute : projectversionissuesRoute + componentId)
                .then(function (response) {
                    return response.data;
                });
        }
    }
})();