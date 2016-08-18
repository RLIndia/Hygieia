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

			return $http.get(itemsByTypeRoute + type).then(function (response) {
				return response.data;
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