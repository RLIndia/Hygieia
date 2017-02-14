(function () {
    'use strict';

    var widget_state,
        config = {
            view: {
                defaults: {
                    title: 'Project' // widget title
                },
                controller: 'jiraDashboardViewController',
                controllerAs: 'jiraDashboardView',
                templateUrl: 'components/widgets/jira-dashboard/view.html'
            },
            config: {
                controller: 'jiraDashboardConfigController',
                controllerAs: 'jiraDashboardConfig',
                templateUrl: 'components/widgets/jira-dashboard/config.html'
            },
            getState: getState
        };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('jira-dashboard', config);
    }

    function getState(widgetConfig) {
        return HygieiaConfig.local || widgetConfig.id ?
            widget_state.READY :
            widget_state.CONFIGURE;
    }
})();
