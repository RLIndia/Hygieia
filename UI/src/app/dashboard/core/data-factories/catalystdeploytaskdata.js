/**
 * Gets deploy related data
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('catalystdeployhistorydata', catalystdeployhistorydata);

    function catalystdeployhistorydata($http) {
        //Api to be updated to project version issues.
        //Api to be updated to project version issues.
        var testDetailRoute = 'test-data/deploy_detail.json';
        var catalystdeploymentHisotoryRoute = '/api/catalystdeploymentshistory/';
        console.log("in controller");
        return {
            details: getdetails
        };

        function getdetails(taskId) {
            console.log("Task ID "  + taskId);
            return $http.get(HygieiaConfig.local ? testDetailRoute : catalystdeploymentHisotoryRoute + taskId)
                .then(function (response) {
                    return response.data;
                });
        }
    }
})();