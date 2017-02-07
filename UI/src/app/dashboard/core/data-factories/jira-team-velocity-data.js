/**
 * Gets deploy related data
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('jiraTeamVelocityData', jiraTeamVelocityData);

    function jiraTeamVelocityData($http) {
        //Api to be updated to project version issues.
        //Api to be updated to project version issues.
        var jiraTeamVelocityDetailRoute = 'test-data/deploy_detail.json';
        var jiraTeamVelocityDataRoute = '/api/jira-team-velocity/';

        return {
            details: details
        };

        function details(componentId) {
            console.log("Component ID "  + componentId);
            return $http.get(HygieiaConfig.local ? jiraTeamVelocityDetailRoute : jiraTeamVelocityDataRoute + componentId)
                .then(function (response) {
                    return response.data;
                });
        }
    }
})();