"use strict";

/**
 * 临时解决方案都写在这里，需要及时清理
 *
 * @author yuanzhen
 * @date 2016-03-26
 */

/* global genApp*/
// 修正bootstrap的modal显示时，浏览器历史记录的前进或后退导致背景不消失的问题
genApp.
run(["$rootScope", function ($rootScope) {
    $rootScope.$on('$stateChangeSuccess', function () {
        angular.forEach(document.querySelectorAll('.modal-backdrop'), function (elem) {
            elem.parentNode.removeChild(elem);
        });
    });
}]).
run(["$rootScope", "$state", function ($rootScope, $state) {
    $rootScope.$state = $state;
}]);
