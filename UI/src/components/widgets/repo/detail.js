(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('RepoDetailController', RepoDetailController);

    RepoDetailController.$inject = ['$modalInstance', 'commits', 'DashStatus','branchURL'];
    function RepoDetailController($modalInstance, commits, DashStatus,branchURL) {
        /*jshint validthis:true */
        var ctrl = this;

        ctrl.statuses = DashStatus;
        ctrl.commits = commits;
        ctrl.branchURL = branchURL;
    }
})();