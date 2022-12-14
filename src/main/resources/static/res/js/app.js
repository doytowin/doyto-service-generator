'use strict';

/*global genApp, Crud, Util*/

genApp.
factory('Column', ['$resource',
    function ($resource) {
        return $resource('api/column/:id', {id: '@id'},{
            batch: {url:'api/column/batch', method: 'POST'}
        });
    }]
).
controller('ColumnCtrl', ['$scope', 'Column',
    function ($scope, Column) {
        $scope.crud = new Crud(Column, function (data) {
            if (data.success) {
                $scope.crud.p.load();
                $('.modal').modal('hide');
            } else {
                Util.handleFailure(data);
            }
        });
    }]
);
'use strict';

/*global genApp, Crud, Util*/

genApp.
factory('Module', ['$resource',
    function ($resource) {
        return $resource('api/module/:id', {id: '@id'}, {
            upload: {url:'api/module/upload/:id', method: 'POST'},
            import: {url:'api/import/module', method: 'POST'},
            tableName: {url:'api/column/:table', method: 'GET'},
            saveColumns: {url:'api/column/batch', method: 'POST'},
            template: {url:'api/template/', method: 'GET'}
        });
    }]
).
controller('ModuleCtrl', ['$scope','Project','Module','Column',
    function ($scope, Project, Module, Column) {{
      let succFunc = function (data) {
        if (data.success) {
          $scope.crud.p.load();
          $('.modal').modal('hide');
        } else {
          Util.handleFailure(data);
        }
      };
        $scope.crud = new Crud(Module, succFunc);
        $scope.crud.import = function (record) {
          Module.import(record, succFunc);
        };

        Project.query(
            function (json) {
                if (json.success) {
                    $scope.projects = {};
                    for (var i = 0; i < json.data.list.length; i++) {
                        var project = json.data.list[i];
                        $scope.projects[project.id] = project;
                    }
                    if (localStorage.projectId) {
                        var p = $scope.projects[localStorage.projectId];
                        $scope.crud.project = p;
                        $scope.crud.p.q.projectId = p.id;
                        $scope.crud.p.load(true);
                    }
                }
            }
        );
        $scope.sqlResolved = false;
        $scope.resolveSql = function(record) {
            if (/^create\s+table\s+(\w+)\s?/gi.test(record.createSql)) {

                record.tableName = RegExp.$1;
                var $1 = Util.camelize(RegExp.$1);
                record.name = $1;
                var $2 = Util.capitalize($1);
                record.modelName = $2;
                record.fullName = $2;
                record.displayName = $2;

                $scope.sqlResolved = true;
            } else {
                alert('sql????????????!');
            }
        };

        $scope.editLabels = function(record) {
            Column.query({tableName:record.tableName, projectId:record.projectId}, function (data) {
                if (data.success) {
                    record.columns = data.data;
                    $scope.crud.record = record;
                } else {
                    Util.handleFailure(data);
                }
            });
        };

        $scope.saveLabels = function(columns) {
            Column.batch(columns, function (data) {
                if (data.success) {
                    $('.modal').modal('hide');
                    alert("????????????");
                } else {
                    Util.handleFailure(data);
                }
            });
        };
    }]
);


'use strict';

/*global genApp, Crud, Util*/

genApp.
factory('Project', ['$resource',
    function ($resource) {
        return $resource('api/project/:id', {id: '@id'});
    }]
).
controller('ProjectCtrl', ['$scope', 'Project',
    function ($scope, Project) {
        $scope.crud = new Crud(Project, function (data) {
            if (data.success) {
                $scope.crud.p.load();
                $('.modal').modal('hide');
            } else {
                Util.handleFailure(data);
            }
        });
    }]
);
'use strict';

/*global genApp, Crud, Util*/

genApp.
factory('Template', ['$resource',
    function ($resource) {
        return $resource('api/template/:id', {id: '@id'});
    }]
).
controller('TemplateCtrl', ['$scope', 'Project', 'Template', '$window',
    function ($scope, Project, Template, $window) {

        $scope.crud = new Crud(Template, function (data) {
            if (data.success) {
                $scope.crud.p.load();
                $('.modal').modal('hide');
            } else {
                $scope.errors = Util.handleFailure(data);
            }
        });

        Project.query(
            function (json) {
                if (json.success) {
                    $scope.projects = {};
                    for (var i = 0; i < json.data.list.length; i++) {
                        var project = json.data.list[i];
                        $scope.projects[project.id] = project;
                    }
                    if (localStorage.projectId) {
                        var p = $scope.projects[localStorage.projectId];
                        $scope.crud.project = p;
                        $scope.crud.p.q.projectId = p.id;
                        $scope.crud.p.load(true);
                    }
                }
            }
        );

        $scope.crud.add = function () {
            this.record = {cap:true};
        };

        //???????????????????????????
        $window.readContent = function (files) {
            var file = files[0];
            var reader = new FileReader();
            reader.onload = function (e) {
                $scope.crud.record.content = e.target.data;
                if (!$scope.crud.record.suffix) {
                    $scope.crud.record.suffix = file.name.replace(/^_\./, '.');//??????.??????????????????_
                }
                $scope.$apply();
            };
            reader.readAsText(file);
        };
    }]
);
"use strict";

/* global Page */
/* exported Crud */

var Crud = function (R, successFunc, errorFunc) {
    var defaultErrorMessage = '????????????';
    var loading = false;

    function simpleCopy(source) {
        var destination = {}, exclude = 'createTime|createUserId|updateTime|updateUserId|deleted';
        for (var key in source) {
            if (source.hasOwnProperty(key) && exclude.indexOf(key) < 0) {
                destination[key] = source[key];
            }
        }
        return destination;
    }

    var onSuccess = function (data) {
        loading = false;
        if (typeof successFunc === 'function') {
            successFunc(data);
        } else {
            if (!data.success) {
                alert(data.message || defaultErrorMessage);
            }
        }
    };

    var onError = function (ret) {
        loading = false;
        if (typeof errorFunc === 'function') {
            errorFunc(ret);
        } else {
            if (typeof ret === 'object') {
                alert('Status[' + ret.status + ']: ' + (ret.data.message || ret.statusText || defaultErrorMessage));
            } else {
                alert(typeof ret === 'string' ? ret : defaultErrorMessage);
            }
        }
    };

    this.add = function () {
        this.record = {};
    };

    this.edit = function (record) {
        this.record = simpleCopy(record);
    };

    this.view = function (record) {
        this.record = record;
    };

    this.save = function (record) {
        if (loading) {
            return;
        }
        loading = true;
        record.id ? R.patch(record, onSuccess, onError) : R.save(record, onSuccess, onError);
    };

    this.remove = function (record, message) {
        if (!confirm(message || "???????????????????????????????")) {
            return;
        }
        R.remove({}, {id: record.id}, onSuccess, onError);
    };

    this.p = new Page(R.query).load();
};
"use strict";

/* global Util */
/* exported Page */

// ??????????????????
var FIRST_PAGE = 1;
var Page = function (queryFunc) {
    var page = 0, limit = 10, pages = 0, total = 0, qo;
    function SuccessCallback(json) {
        if (json.success) {
            if (typeof json.data.total === "number") {
                total = json.data.total;
                var newPages = Math.ceil(total / limit);

              if (pages !== newPages) {
                    pages = newPages;
                    if (page > newPages) {// ????????????page??????????????????????????????????????????????????????page??????????????????
                        page = Math.min(newPages, FIRST_PAGE);
                        this && this.load();
                        return;
                    }
                }
                if (!page && pages) {
                    page = FIRST_PAGE;
                }
                var p = this;
                p.pageNumber = page;
                p.pageSize = limit;
                p.pages = pages;
                p.total = total;
                p.from = Math.max((page - 1) * limit + 1, 0);
                p.to = Math.min(page * limit, total);
                p.data = json.data.list;
            }
        } else {
            Util.handleFailure(json);
        }
    }
    this.q = {};
    this.isQueryChanged = function () {
        return !angular.equals(this.q, qo);
    };
    this.load = function (checkQueryChanged) {
        var q = this.q;
        if (checkQueryChanged && angular.equals(this.q, qo)) {
            // ????????????checkQueryChanged???true, ????????????????????????this.q?????????????????????qo????????????,
            // ?????????????????????????????????.
            return;
        }
        q.pageSize = limit;
        q.pageNumber = (page || FIRST_PAGE) - 1;
        qo = angular.copy(q);
        queryFunc(q, angular.bind(this, SuccessCallback));
        return this;
    };
    this.first = function () {
        if (page > FIRST_PAGE) {
            page = FIRST_PAGE;
            this.load();
        }
    };
    this.last = function () {
        if (page !== pages) {
            page = pages;
            this.load();
        }
    };
    this.prev = function () {
        if (page > FIRST_PAGE) {
            page--;
            this.load();
        }
    };
    this.next = function () {
        if (page < pages) {
            page++;
            this.load();
        }
    };
    this.size = function (size) { // ????????????????????????
        limit = size;
        page = Math.min(page, Math.ceil(total / limit));// ????????????p?????????????????????
        page = Math.max(page, FIRST_PAGE);
        this.load();
    };
    this.goto = function (goto) {
        page = goto;
        this.load();
    };
    this.reset = function () {
        this.q = {};
        page = FIRST_PAGE;
        this.load();
    };
};
"use strict";

/* exported Util */

var Util = window.Util || {};
// ??????????????????
Util.formHttpRequestTransform = function (data) {
    /**
     * The workhorse; converts an object to x-www-form-urlencoded serialization.
     * @param {Object} obj
     * @return {String}
     */
    var param = function (obj) {
        var query = '';
        var name, value, fullSubName, subName, subValue, innerObj, i;

        for (name in obj) {
            if (obj.hasOwnProperty(name) && !name.startsWith("$$")) {
                value = obj[name];

                if (value instanceof Array) {
                    for (i = 0; i < value.length; ++i) {
                        subValue = value[i];
                        fullSubName = name + '[' + i + ']';
                        innerObj = {};
                        innerObj[fullSubName] = subValue;
                        query += param(innerObj) + '&';
                    }
                } else if (value instanceof Object) {
                    for (subName in value) {
                        if (value.hasOwnProperty(subName)) {
                            subValue = value[subName];
                            fullSubName = name + '[' + subName + ']';
                            innerObj = {};
                            innerObj[fullSubName] = subValue;
                            query += param(innerObj) + '&';
                        }
                    }
                } else if (value !== undefined && value !== null) {
                    query += encodeURIComponent(name) + '=' + encodeURIComponent(value) + '&';
                }
            }
        }
        return query.length ? query.substr(0, query.length - 1) : query;
    };

    return angular.isObject(data) && String(data) !== '[object File]' ? param(data) : data;
};

Util.escapeHTML = function(text) {
    return !text ? "" : text.replace(/[\"'\/<>]/g, function (a) {
        return {'"': '&quot;', "'": '&#39;', '/': '&#47;', '<': '&lt;', '>': '&gt;'}[a];
    });
};

Util.capitalize = function (s) {
    return typeof s !== 'string' ? s : s.charAt(0).toUpperCase() + s.slice(1);
};

Util.camelize = function(s) {
    return typeof s !== 'string' ? s :
        s.replace(/^([A-Z])|[\s\-_](\w)/g, function (match, p1, p2) {
            return p2 ? p2.toUpperCase() : p1.toLowerCase();
        });
};

Util.handleFailure = function (data) {
    if (data && !data.success) {
        if (data.code === '1') {
            location.href = 'login?redirect=' + encodeURIComponent(location.href);
        } else {
            alert(data.message || '????????????');
            return data.errors;
        }
    }
};
"use strict";

/**
 * ??????????????????????????????????????????????????????
 *
 * @author yuanzhen
 * @date 2016-03-26
 */

/* global genApp*/
// ??????bootstrap???modal?????????????????????????????????????????????????????????????????????????????????
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
    $resourceProvider.defaults.actions.patch = {method: 'PATCH'};
    $resourceProvider.defaults.actions.update = {method: 'PUT'};
}]).
// register the interceptor as a service
factory('fastjsonInterceptor', [function () {
    return {
        'response': function (_response) {
            if (_response.data && typeof _response.data === 'object') {
                // var str = JSON.stringify(_response.data);
                restore$ref(_response.data);
                // _response.data._str = str;
            }
            return _response;
        }
    };
}])
.config(['$httpProvider', function ($httpProvider) {
    $httpProvider.interceptors.push('fastjsonInterceptor');
}])

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
 * @param {template} ???????????????????????????????????????.
 *
 */
genApp.directive('gzBindTemplate', ['$compile',
    function ($compile) {
        return {
            restrict: 'EA',
            link: function($scope, $elem, $attr) {
                var template = ($scope.$eval($attr.gzBindTemplate));
                $elem.html(template);
                $compile($elem.contents())($scope);//???template????????????
            }
        };
    }]
);
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
                $rootScope.menuTree = data.data;
            });
        };
        $rootScope.loadLoginUser = function () {
            $http.get('login-user').success(function (data) {
                if (data.success) {
                    $rootScope.loginUser = data.data;
                    $rootScope.loadMenu();
                } else {
                    location.href = 'login?redirect=' + encodeURIComponent(location.href);
                }
            });
        };
    }]
);
"use strict";

/*global genApp, angular, Util*/

genApp.
controller('GeneratorCtrl', ['$scope', '$timeout', '$log', 'Project', 'Module', 'Template', 'Column',
    function ($scope, $timeout, $log, Project, Module, Template, Column) {
        $scope.data = {};
        Project.query(
            function (data) {
                if (data.success) {
                    $scope.projects = data.data.list;

                    var defaultProject = $scope.projects[0];
                    if (localStorage.projectId) {
                        for (var i = 0; i < $scope.projects.length; i++) {
                            var project = $scope.projects[i];
                            if (project.id === localStorage.projectId) {
                                defaultProject = project;
                                break;
                            }
                        }
                    }
                    $scope.switchProject(defaultProject);
                }
            }
        );

        function onSuccess(data) {
            if (data.success) {
                // load();
                //$('.modal').modal('hide');
            } else {
                Util.handleFailure(data);
            }
        }

        //???????????????????????????????????????????????????????????????
        $scope.switchProject = function (record) {
            record = record || {};
            $scope.currentProject = record;
            localStorage.projectId = record.id;

            Template.query(
                {
                    projectId: record.id,
                    valid: true
                },
                function (data) {
                    if (data.success) {
                        $scope.templates = data.data.list;
                    }
                }
            );

            Module.query(
                {
                    projectId: record.id,
                    valid: true
                },
                function (data) {
                    $scope.project = angular.copy(record);
                    $scope.projectName = record.name;
                    if (data.success) {
                        $scope.modules = data.data.list;
                        $scope.switchModule($scope.modules[0]);
                    }
                }
            );
        };

        $scope.switchModule = function (record, records, index) {
            record = record || {};
            $scope.gen = angular.copy(record);
            $scope.label = record.displayName || record.name;
            $scope.data.labelFilter = record.displayName || record.name;
            if (record.columns) {
                $scope.columns = record.columns;
                $scope.imports = record.imports;
                if (angular.isArray(records) && index < records.length) {
                    $timeout(function () {
                        $scope.upload(record.name, true);
                        $scope.switchModule(records[index], records, index + 1);
                    }, 50)
                }
            } else {
                Column.query({
                    tableName: record.tableName || record.name,
                    projectId: record.projectId
                }, function (data) {
                    if (data.success) {
                        var columns = data.data.list, imports = [];
                        for (var i = 0; i < columns.length; i++) {
                            var column = columns[i];
                            if (column.type.startsWith('int')) {
                                column.type = 'Integer';
                                column.jdbcType = 'INTEGER';
                            } else if (column.type.startsWith('bigint')) {
                                column.type = 'Long';
                                column.jdbcType = 'LONG';
                            } else if (column.type.startsWith('smallint')) {
                                column.type = 'Short';
                                column.jdbcType = 'INTEGER';
                            } else if (column.type.startsWith('tinyint(1)') || column.type === 'bit(1)' ) {
                                column.type = 'Boolean';
                                column.jdbcType = 'BIT';
                            } else if (column.type.startsWith('tinyint')) {
                                column.type = 'Byte';
                                column.jdbcType = 'BIT';
                            } else if (column.type.startsWith('timestamp') || column.type.startsWith('datetime')) {
                                column.type = 'Date';
                                column.jdbcType = 'TIMESTAMP';
                                imports.indexOf('java.util.Date') < 0 && imports.push('java.util.Date');
                            } else if (column.type.startsWith('date')) {
                                column.type = 'Date';
                                column.jdbcType = 'Date';
                                imports.indexOf('java.util.Date') < 0 && imports.push('java.util.Date');
                            } else {
                                column.type = 'String';
                                column.jdbcType = 'VARCHAR';
                            }
                        }
                        $scope.columns = record.columns = columns;
                        $scope.imports = record.imports = imports;

                        if (angular.isArray(records) && index < records.length) {
                            $timeout(function () {
                                $scope.upload(record.name, true);
                                $scope.switchModule(records[index], records, index + 1);
                            }, 50)//????????????????????????
                        }
                    }
                });
            }
        };

        $scope.add = function (gen) {
            gen.id = undefined;
            Module.save(gen, onSuccess);
        };

        $scope.save = function (gen) {
            Module.save(gen, onSuccess);
        };

        $scope.remove = function (gen) {
            Module.remove({id: gen.id}, onSuccess);
        };
        $scope.upload = function (name, all) {
            var arr = [];
            var templates = $scope.templates;
            for (var i = 0; i < templates.length; i++) {
                var o = templates[i];
                if (!all && !o.upload) {continue;}
                var prefix = o.cap ? Util.capitalize(name) : name;
                var d = {};
                d.text = angular.element(document.getElementById('template-' + o.id)).find('pre').text();
                d.path = o.path + prefix + o.suffix;
                d.key = o.suffix;
                arr.push(d);

                // window.requestFileSystem  = window.requestFileSystem || window.webkitRequestFileSystem;
                //
                // navigator.webkitPersistentStorage.requestQuota(1024*1024,
                //     function(grantedBytes) {
                //         window.requestFileSystem(window.PERSISTENT, grantedBytes, function (fs) {
                //             fs.root.getFile("requestFileSystem_test.txt", {
                //             // fs.root.getFile($scope.currentProject.path + d.path, {
                //                 create: true
                //             }, function (fileEntry) {
                //
                //                 // Create a FileWriter object for our FileEntry (log.txt).
                //                 fileEntry.createWriter(function (fileWriter) {
                //
                //                     fileWriter.onwriteend = function (e) {
                //                         console.log('Write completed.');
                //                     };
                //
                //                     fileWriter.onerror = function (e) {
                //                         console.log('Write failed: ' + e.toString());
                //                     };
                //
                //                     // Create a new Blob and write it to log.txt.
                //                     var blob = new Blob([d.text], {type: 'text/plain'});
                //
                //                     fileWriter.write(blob);
                //
                //                 }, errorHandler)
                //             }, errorHandler)
                //         }, errorHandler)
                //     },
                //     function(errorCode) {
                //         alert("Storage not granted.");
                //     }
                // );
            }
            if (!arr.length) {
                alert('??????????????????????????????!');
                return;
            }
            Module.upload({id : name}, {root: $scope.project.path, data:arr}, onSuccess);
        };

        $scope.uploadAll = function () {
            $scope.switchModule($scope.modules[0], $scope.modules, 1);
        };

    }]
);
/*!
 * restore$ref.js v1.0.0
 *
 * ??????fastjson?????????????????????????????????$ref??????
 *
 */
var restore$ref = function () {
    function resolvePath(root, expression) {
        var refs = expression.split(/[\.\[\]]+/);
        var resolved = root;
        for (var i = 1, j = refs.length; i < j; ++i) {
            var ref = refs[i];
            if (ref !== '') {
                if (isNaN(ref)) {
                    resolved = resolved[ref];
                } else {
                    resolved = resolved[parseInt(ref)];
                }
            }
        }
        return resolved;
    }

    /**
     * {"$ref":"$"}         ???????????????
     * {"$ref":"@"}         ????????????
     * {"$ref":".."}        ???????????????
     * {"$ref":"../.."}     ???????????????????????????
     * {"$ref":"$.members[0].reportTo"}    ?????????????????????
     */
    function resolve$ref(root, ancient, key) {
        var parent = ancient.node;
        var $ref = parent[key].$ref;
        if ($ref.substring(0, 1) == "$") {
            parent[key] = resolvePath(root, $ref);
        } else if ($ref == "@") {
            parent[key] = ancient.node;
        } else if ($ref.substring(0, 2) == "..") {
            var len = $ref.split(/[\/]+/).length;
            while (len-- > 0) {
                ancient = ancient.parent;
            }
            parent[key] = ancient.node;
        } else {
            console.log("???????????????$ref:" + $ref);
        }
    }

    /**
     * ????????????JSON
     *
     * @param root      ?????????JSON??????
     * @param ancient   ancient.node????????????, ??????????????????
     * @param key       ancient.node[key]??????????????????????????????
     */
    function resolveObject(root, ancient, key) {
        var current = ancient ? ancient.node[key] : root;
        if (current !== null && typeof current === "object") {
            if (Array.isArray(current)) {
                for (var i = 0; i < current.length; i++) {
                    resolveObject(root, {
                        parent: ancient,
                        node: current
                    }, i);
                }
            } else {
                if (current.$ref) {
                    resolve$ref(root, ancient, key);//??????$ref
                } else {
                    for (var p in current) {
                        if (current.hasOwnProperty(p)) {
                            resolveObject(root, {
                                parent: ancient,
                                node: current
                            }, p)
                        }
                    }
                }
            }
        }
    }

    return function () {
        resolveObject(arguments[0]);
    };
}();