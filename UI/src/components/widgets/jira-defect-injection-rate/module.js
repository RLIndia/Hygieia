(function () {
    'use strict';

    var widget_state,
        config = {
            view: {
                defaults: {
                    title: 'Defect Injection Rate' // widget title
                },
                controller: 'JiraDefectInjectionRateViewController',
                controllerAs: 'JiraDefectInjectionRate',
                templateUrl: 'components/widgets/jira-team-velocity/view.html'
            },
            config: {
                controller: 'JiraDefectInjectionRateConfigController',
                controllerAs: 'JiraDefectInjectionRateConfig',
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
        widgetManagerProvider.register('jira-defect-injection-rate', config);
    }

    function getState(widgetConfig) {
        return HygieiaConfig.local || widgetConfig.id ?
            widget_state.READY :
            widget_state.CONFIGURE;
    }
})();
