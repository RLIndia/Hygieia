(function () {
    'use strict';

    var widget_state,
        config = {
            view: {
                defaults: {
                    title: 'Acceptance Coverage' // widget title
                },
                controller: 'JiraAcceptanceCoverageViewController',
                controllerAs: 'JiraAcceptanceCoverage',
                templateUrl: 'components/widgets/jira-acceptance-coverage/view.html'
            },
            config: {
                controller: 'JiraAcceptanceCoverageConfigController',
                controllerAs: 'JiraAcceptanceCoverageConfig',
                templateUrl: 'components/widgets/jira-acceptance-coverage/config.html'
            },
            getState: getState
        };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('jira-acceptance-coverage', config);
    }

    function getState(widgetConfig) {
        return HygieiaConfig.local || widgetConfig.id ?
            widget_state.READY :
            widget_state.CONFIGURE;
    }
})();
