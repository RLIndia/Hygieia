 
(function () {
    'use strict';
    
    var widget_state,
        config = {
            view: {
                defaults: {
                    title: 'Deployment (Chef)' // widget title
                },
                controller: 'chefViewController',
                controllerAs: 'chefView',
                templateUrl: 'components/widgets/chef/view.html'
            },
            config: {
                controller: 'chefConfigController',
                controllerAs: 'chefConfig',
                templateUrl: 'components/widgets/chef/config.html'
            },
            getState: getState
        };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    
    function register(widgetManagerProvider, WidgetState) {
    	
        widget_state = WidgetState;
        widgetManagerProvider.register('chef', config);
    }

    function getState(widgetConfig) {
        return HygieiaConfig.local || widgetConfig.id ?
            widget_state.READY :
            widget_state.CONFIGURE;
    }
})();
