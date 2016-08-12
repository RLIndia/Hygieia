/**
 * Gets code repo related data
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('codeRepoData', codeRepoData);

    function codeRepoData($http) {
        var testDetailRoute = 'test-data/commit_detail.json';
        var caDetailRoute = '/api/commit';
        var caDetailRouteBitbucket = '/api/commit/bitbucket';

        return {
            details: details
        };

        // get 15 days worth of commit data for the component
        function details(params) {
        	if(params.scmType ==='Bitbucket' || params.scmType ==='GitHub' ) {
        		var url = caDetailRouteBitbucket;
        	} else {
        		var url = HygieiaConfig.local ? testDetailRoute : caDetailRoute
        	}
            return $http.get(url, { params: params })
                .then(function (response) {
                    return response.data;
                });
        }
    }
})();