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
}).
filter('pluralize', function() {

    function pluralize(noun) {
        if (typeof noun !== 'string') { return noun; }

        var rules = [
            {regex: /octopus/gi, suffix: 'octopuses'},
            {regex: /person/gi, suffix: 'people'},
            {regex: /ox/gi, suffix: 'oxen'},
            {regex: /goose/gi, suffix: 'geese'},
            {regex: /mouse/gi, suffix: 'mice'},
            {regex: /bison|buffalo|deer|duck|fish|moose|pike|plankton|salmon|sheep|squid|swine|trout|sheap|equipment|information|rice|money|species|series|news/i, suffix: '$&'}, // bison -> bison
            {regex: /(x|ch|ss|sh)$/gi, suffix: '$1es'}, // dish -> dishes, kiss -> kisses
            {regex: /(hetero|canto|photo|zero|piano|pro|kimono|portico|quarto)$/gi, suffix: '$1s'}, // kimono -> kimonos
            {regex: /(?:([^f])fe|([lr])f)$/, suffix: '$1$2ves'}, // knife -> knives, calf -> calves
            {regex: /o$/gi, suffix: 'oes'}, // hero -> heroes
            {regex: /([^aeiouy]|qu)y$/gi, suffix: '$1ies'}, // cherry -> cherries
            {regex: /s$/gi, suffix: 's'}, // cats -> cats
            {regex: /$/gi, suffix: 's'} // cat -> cats
        ];

        for (var i = 0; i < rules.length; i++) {
            var rule = rules[i];
            if (noun.match(rule.regex)) {
                noun = noun.replace(rule.regex, rule.suffix);
                break;
            }
        }

        return noun;
    }

    return function(input) {
        // if null or undefined pass it through
        return !input ? input : pluralize(input);
    };
})