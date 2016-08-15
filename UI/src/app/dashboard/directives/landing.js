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
                require: '^widgetContainer',
                restrict: 'E',
                controller: controller,
                scope:true,
                link: link
            };

            function controller($scope, $element) {
                $scope.landingdashboards = null;
                $scope.alerts = [];
                //will be set by link
                $scope.widgetConfig = null;
                $scope.widgetDefinition = null;
            }

            function link(scope, element, attrs, containerController){
                console.log('Running link');
                // make it so name is not case sensitive
                attrs.name = attrs.name.toLowerCase();
                scope.widgetEl = element;
                scope.widgetDefinition = widgetManager.getWidget(attrs.name);
                 // grab values from the registered configuration
                var templateUrl = scope.widgetDefinition.view.templateUrl;
                var controllerName = scope.widgetDefinition.view.controller;
                var controllerAs = scope.widgetDefinition.view.controllerAs || 'ctrl';
                
                console.log(containerController);
                console.log(controllerAs);

                // create the widget's controller based on config values
                scope.widgetViewController = $controller(controllerName + ' as ' + controllerAs, {
                    $scope: scope
                });

                console.log(scope.widgetViewController);
                console.log(scope.widgetConfig);
                console.log(scope.widgetConfig.options.scm.name);


                //pull information about each widget.


            }
        }

    })();