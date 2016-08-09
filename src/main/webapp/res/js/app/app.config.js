"use strict";

/* global genApp */

genApp.
// config(['$locationProvider', function ($locationProvider) {
//     $locationProvider.html5Mode(true);
// }]).
// config(['$stickyStateProvider', function ($stickyStateProvider) {
//     $stickyStateProvider.enableDebug(true);
// }]).
//配置微信图片白名单
config(['$compileProvider', function ($compileProvider) {
    //其中 weixin gulp是微信安卓版的 localId 的形式，wxlocalresource 是 iOS 版本的 localId 形式
    $compileProvider.imgSrcSanitizationWhitelist(/^\s*(https?|weixin|wxlocalresource):/);
}]).
config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {

    var config = angular.extend({
        base: 'app',
        models: []
    }, window.Config);

    angular.forEach(config.models, function (model) {
        var url = model.url;
        var uri = model.uri = model.url.split('?')[0];
        var state = {
            name: config.base + '.' + uri,
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
    state(config.base, {
        url: '/' + config.base + '/',
        abstract: true,
        template: function () {
            var html = '';
            angular.forEach(config.models, function (model) {
                html += '<div ui-view="' + model.uri + '" ng-show="$state.includes(\'app.' + model.uri + '\')"></div>';
            });
            return html;
        }
    // }).
    // state('app.default', {
    //     url: '/app/welcome',
    //     template: '欢迎访问'
    });
    // For any unmatched url, redirect to /{config.base}/main
    $urlRouterProvider.otherwise(config.base + '/' + config.models[0].url);

}]).
config(['$resourceProvider', function ($resourceProvider) {
    $resourceProvider.defaults.actions.query.isArray = false;
}]);
