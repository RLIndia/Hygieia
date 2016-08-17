/**
 * Collector and collector item data
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('collectorData', collectorData);

    function collectorData($http, $q) {
        var itemRoute = '/api/collector/item';
        var itemRouteBitBucket = '/api/collector/item/bitbucket';
    	
        var itemsByTypeRoute = '/api/collector/item/type/';
        var collectorsByTypeRoute = '/api/collector/type/';

        return {
            itemsByType: itemsByType,
            createCollectorItem: createCollectorItem,
            createCollectorItemBitBucket:createCollectorItemBitBucket,
            collectorsByType: collectorsByType
        };

        function itemsByType(type) {
            if(type != 'functional')
                return $http.get(itemsByTypeRoute + type).then(function (response) {
                    return response.data;
                });
            else
                return $http.get('http://ms01242.starbucks.net/TestDatabaseAPI/teststacks').then(function (response){
                    var data = [];
                   for (var k in response.data){
                        if (typeof response.data[k] !== 'function') {
                             console.log("Key is " + k + ", value is" + response.data[k]);
                             data.push({
                                "id":k,
                                "stackName":response.data[k]
                             });
                        }
                    }
                    console.log(data);
                    return data;
                });
        }

        function createCollectorItem(collectorItem) {
            return $http.post(itemRoute, collectorItem);
        }
        
        function createCollectorItemBitBucket(collectorItem) {
            return $http.post(itemRouteBitBucket, collectorItem);
        }

        function collectorsByType(type) {
            console.log(type);
            return $http.get(collectorsByTypeRoute + type).then(function (response) {
                return response.data;
            });
        }
    }
})();