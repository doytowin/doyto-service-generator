"use strict";

/* global genApp */

genApp.
run(['$rootScope', function ($rootScope) {
        $rootScope.Math = window.Math;
        $rootScope.Date = window.Date;
    }]
).
run(['$rootScope', '$http', function ($rootScope, $http) {
        $rootScope.loadMenu = function () {
            $http.get('api/menu/tree').success(function (data) {
                $rootScope.menuTree = data.result;
            });
        };
        $rootScope.loadLoginUser = function () {
            $http.get('login-user').success(function (data) {
                if (data.success) {
                    $rootScope.loginUser = data.result;
                    $rootScope.loadMenu();
                } else {
                    location.href = 'login?redirect=' + encodeURIComponent(location.href);
                }
            });
        };
    }]
);