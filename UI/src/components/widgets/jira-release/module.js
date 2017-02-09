(function () {
    'use strict';

    var widget_state,
        config = {
            view: {
                defaults: {
                    title: 'Project' // widget title
                },
                controller: 'jiraReleaseViewController',
                controllerAs: 'jiraReleaseView',
                templateUrl: 'components/widgets/jira-release/view.html'
            },
            config: {
                controller: 'jiraReleaseConfigController',
                controllerAs: 'jiraReleaseConfig',
                templateUrl: 'components/widgets/jira-release/config.html'
            },
            getState: getState
        };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('jira-release', config);
    }

    function getState(widgetConfig) {
        return HygieiaConfig.local || widgetConfig.id ?
            widget_state.READY :
            widget_state.CONFIGURE;
    }
})();
