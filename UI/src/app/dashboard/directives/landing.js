(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')

        // used by widgets to set their current state
        // CONFIGURE will render the common config screen instead of the widget content
        .constant('WidgetState', {
            READY: 1,
            CONFIGURE: 2,
            WAITING: 3,
            NOT_COLLECTING: 4
        })

        // constant to be used by widgets to set their state
        // ERROR: causes the widget's panel to use the 'panel-danger' class
        .constant('DisplayState', {
            DEFAULT: 1,
            ERROR: 2
        })
        .directive('landing', landingDirective);

        landingDirective.$inject = ['$controller', '$http', '$templateCache', '$compile', 'widgetManager', '$modal', 'WidgetState', 'DisplayState', '$interval', 'dashboardData','$cookies'];
        function landingDirective($controller, $http, $templateCache, $compile, widgetManager, $modal, WidgetState, DisplayState, $interval, dashboardData,$cookies) {
            return {
                templateUrl: 'app/dashboard/views/landing.html',
                restrict: 'E',
                controller: controller,
                scope: {
                    widget: '=',
                    title: '@widgetTitle'
                },
                link: link
            };

            function controller($scope, $element) {
                $scope.landingdashboards = null;
                $scope.alerts = [];
            }

            function link(scope, element, attrs, containerController){
                console.log('Running link');
            }
        }

    })();