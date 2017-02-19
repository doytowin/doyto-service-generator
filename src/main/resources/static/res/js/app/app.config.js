"use strict";

/* global genApp */

genApp.
// config(['$locationProvider', function ($locationProvider) {
//     $locationProvider.html5Mode(true);
// }]).
// config(['$stickyStateProvider', function ($stickyStateProvider) {
//     $stickyStateProvider.enableDebug(true);
// }]).
config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {

    var config = angular.extend({
        root: {
            name:'generator',
            url:'/'
        },
        models: []
    }, window.Config);

    angular.forEach(config.models, function (model) {
        var url = model.url;
        var uri = model.uri = model.url.split('?')[0];
        var state = {
            name: config.root.name + '.' + uri,
            url: url,
            views: {}
        };
        state.views[uri] = {
            'templateUrl': 'res/html/' + uri + '.html'
        };
        if (model.sticky) {
            state.sticky = true;
            state.deepStateRedirect = true;
        }
        $stateProvider.state(state);
    });

    $stateProvider.
    state(config.root.name, {
        url: config.root.url,
        abstract: true,
        template: function () {
            var html = '';
            angular.forEach(config.models, function (model) {
                html += '<div ui-view="' + model.uri + '" ng-show="$state.includes(\'' + config.root.name + '.' + model.uri + '\')"></div>';
            });
            return html;
        }
    });
    // For any unmatched url, redirect to /{config.root.url}/main
    $urlRouterProvider.otherwise(config.root.url + config.models[0].url);

}]).
config(['$resourceProvider', function ($resourceProvider) {
    $resourceProvider.defaults.actions.query.isArray = false;
}]);
