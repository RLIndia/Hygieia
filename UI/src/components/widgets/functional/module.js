(function () {
    'use strict';

    var widget_state,
        config = {
            view: {
                defaults: {
                    title: 'Functional' // widget title
                },
                controller: 'functionalViewController',
                controllerAs: 'functionalView',
                templateUrl: 'components/widgets/functional/view.html'
            },
            config: {
                controller: 'FunctionalConfigController',
                controllerAs: 'functionalConfig',
                templateUrl: 'components/widgets/functional/config.html'
            },
            getState: getState
        };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('functional', config);
    }

    function getState(widgetConfig) {
        return HygieiaConfig.local || widgetConfig.id ?
            widget_state.READY :
            widget_state.CONFIGURE;
    }
})();
