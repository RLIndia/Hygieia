(function () {
    'use strict';

    var widget_state,
        config = {
            view: {
                defaults: {
                    title: 'Deploy' // widget title
                },
                controller: 'deployallViewController',
                controllerAs: 'deployallView',
                templateUrl: 'components/widgets/deployall/view.html'
            },
            getState: getState
        };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('deployall', config);
    }

    function getState(widgetConfig) {
//        return HygieiaConfig.local || widgetConfig.id ?
//            widget_state.READY :
//            widget_state.CONFIGURE;
//Modified to always be ready
        return widget_state.READY;
    }
})();
