(function () {
    'use strict';

    var widget_state,
        config = {
            view: {
                defaults: {
                    title: 'Release Status' // widget title
                },
                controller: 'JiraTeamVelocityViewController',
                controllerAs: 'JiraTeamVelocity',
                templateUrl: 'components/widgets/jira-team-velocity/view.html'
            },
            config: {
                controller: 'JiraTeamVelocityConfigController',
                controllerAs: 'JiraTeamVelocityConfig',
                templateUrl: 'components/widgets/jira-team-velocity/config.html'
            },
            getState: getState
        };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('jira-release-status', config);
    }

    function getState(widgetConfig) {
        return HygieiaConfig.local || widgetConfig.id ?
            widget_state.READY :
            widget_state.CONFIGURE;
    }
})();
