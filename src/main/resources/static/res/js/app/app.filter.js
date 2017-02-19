"use strict";

/* global genApp */

genApp.
filter('regex', function() {
    return function(input, field, regex) {
        if (!input || !input.length) {
            return [];
        }
        var out = [];
        regex = new RegExp(regex);
        for (var i = 0; i < input.length; i++) {
            if (regex.test(input[i][field])) {
                out.push(input[i]);
            }
        }
        return out;
    };
}).
filter('capitalize', function() {
    return function(token) {
        return !!token ? token.charAt(0).toUpperCase() + token.slice(1) : '';
    };
}).
filter('camelize', function() {
    return function(token) {
        var ret = '';
        if (!!token) {
            var fragments = token.split(/[-_.\s]/);
            ret = fragments[0];
            for (var i = 1; i < fragments.length; i++) {
                ret += fragments[i].charAt(0).toUpperCase() + fragments[i].slice(1);
            }
        }
        return ret;
    };
});