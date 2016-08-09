"use strict";

/*global genApp*/

/**
 * @gzdoc directive
 * @name gzBindTemplate
 * @restrict EA
 *
 * @description
 * <ANY gz-href="template"></ANY>
 *
 * @param {template} 含有需要再次绑定内容的变量.
 *
 */
genApp.directive('gzBindTemplate', ['$compile',
    function ($compile) {
        return {
            restrict: 'EA',
            link: function($scope, $elem, $attr) {
                var template = ($scope.$eval($attr.gzBindTemplate));
                $elem.html(template);
                $compile($elem.contents())($scope);//将template再次绑定
            }
        };
    }]
);