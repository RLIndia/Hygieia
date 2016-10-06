(function () {
    'use strict';

    var widget_state,
        config = {
            view: {
                defaults: {
                    title: 'TestRail' // widget title
                },
                controller: 'TestRailViewController',
                controllerAs: 'TestRailView',
                templateUrl: 'components/widgets/testrail/view.html'
            },
            config: {
                controller: 'TestRailConfigController',
                controllerAs: 'TestRailConfig',
                templateUrl: 'components/widgets/testrail/config.html'
            },
            getState: getState
        };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('testrail', config);
    }

    function getState(widgetConfig) {
        return HygieiaConfig.local || widgetConfig.id ?
            widget_state.READY :
            widget_state.CONFIGURE;
    }
})();
