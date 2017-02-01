(function () {
    'use strict';

    var widget_state,
        config = {
            view: {
                defaults: {
                    title: 'Automated Smoke Test' // widget title
                },
                controller: 'CucumberViewController',
                controllerAs: 'CucumberView',
                templateUrl: 'components/widgets/cucumber/view.html'
            },
            config: {
                controller: 'CucumberConfigController',
                controllerAs: 'CucumberConfig',
                templateUrl: 'components/widgets/cucumber/config.html'
            },
            getState: getState
        };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('smoketest', config);
    }

    function getState(widgetConfig) {
        return HygieiaConfig.local || widgetConfig.id ?
            widget_state.READY :
            widget_state.CONFIGURE;
    }
})();
