( function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('TestRailConfigController', TestRailConfigController);

    TestRailConfigController.$inject = ['modalData', 'collectorData','$modalInstance','$timeout'];
    function TestRailConfigController(modalData, collectorData, $modalInstance,$timeout) {
        /*jshint validthis:true */
        var ctrl = this;

        var widgetConfig = modalData.widgetConfig;

        // public variables
        // ctrl.deployJob;
        ctrl.testrailprojects = [];
        ctrl.projectDropdownDisabled = true;
        ctrl.projectDropdownPlaceholder = 'Loading...';
        ctrl.submitted = false;
       
             // public methods
        ctrl.submit = submit;
        console.log("modalData");
        console.log(modalData);
        collectorData.itemsByType('Testrail').then(
        function (data) {
            ctrl.testrailprojects = data;
        	});

        function submit(){

        }
    }
})();
