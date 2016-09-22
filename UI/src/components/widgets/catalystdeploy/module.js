(function () {
    'use strict';

    var widget_state,
        config = {
            view: {
                defaults: {
                    title: 'Catalyst Deploy' // widget title
                },
                controller: 'catalystDeployViewController',
                controllerAs: 'catalystDeployView',
                templateUrl: 'components/widgets/catalystdeploy/view.html'
            },
            config: {
                controller: 'catalystRepoConfigController',
                controllerAs: 'catalystRepoConfig',
                templateUrl: 'components/widgets/catalystdeploy/config.html'
            },
            getState: getState
        };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('catalystdeploy', config);
    }

    function getState(widgetConfig) {
        return HygieiaConfig.local || widgetConfig.id ?
            widget_state.READY :
            widget_state.CONFIGURE;
    }

    
})();