

/**
 * Gets deploy related data
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('testrailviewresultsdata', testrailviewresultsdata);

    function testrailviewresultsdata($http) {
        //Api to be updated to project version issues.
        //Api to be updated to project version issues.
        var testDetailRoute = 'test-data/deploy_detail.json';
        var testrailRunDataRoute = '/api/testrailrunsresult/';

        return {
            details: details
        };

        function details(runId,projectId,milestoneId) {
           // console.log("Component ID "  + componentId);
            return $http.get(HygieiaConfig.local ? testDetailRoute : testrailRunDataRoute + runId + "?projectId=" + projectId + "&milestoneId=" + milestoneId)
                .then(function (response) {
                    return response.data;
                });
        }
    }
})();