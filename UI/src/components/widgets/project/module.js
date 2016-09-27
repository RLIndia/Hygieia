(function () {
    'use strict';

    var widget_state,
        config = {
            view: {
                defaults: {
                    title: 'Project' // widget title
                },
                controller: 'projectVersionViewController',
                controllerAs: 'projectVersionView',
                templateUrl: 'components/widgets/project/view.html'
            },
            config: {
                controller: 'projectVersionConfigController',
                controllerAs: 'projectVersionConfig',
                templateUrl: 'components/widgets/project/config.html'
            },
            getState: getState
        };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('projectversion', config);
    }

    function getState(widgetConfig) {
        return HygieiaConfig.local || widgetConfig.id ?
            widget_state.READY :
            widget_state.CONFIGURE;
    }
})();
