'use strict';

/* App Module */
/* exported genApp */
//
// var genApp = angular.module('GenApp', [
//     'ngResource',
//     'ui.router'
//     // 'ct.ui.router.extras.sticky'
// ]);

'use strict';

/*global genApp, Crud, Util*/

genApp.
factory('Column', ['$resource',
    function ($resource) {
        return $resource('api/column/:id', {id: '@id'});
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
            table: {url:'api/module/table/:table', method: 'GET'},
            template: {url:'api/template/', method: 'GET'}
        });
    }]
).
controller('ModuleCtrl', ['$scope','Project','Module','Column',
    function ($scope, Project, Module, Column) {
        Project.query(
            function (data) {
                if (data.success) {
                    $scope.projects = data.result;
                }
            }
        );
        $scope.crud = new Crud(Module, function (data) {
            if (data.success) {
                $scope.crud.p.load();
                $('.modal').modal('hide');
            } else {
                Util.handleFailure(data);
            }
        });
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
                alert('sql格式错误!');
            }
        };

        $scope.editLabels = function(record) {
            Column.query({tableName:record.tableName}, function (data) {
                if (data.success) {
                    record.columns = data.result;
                    $scope.crud.record = record;
                } else {
                    Util.handleFailure(data);
                }
            });
        };

        $scope.saveLabels = function(columns) {
            Column.save(columns, function (data) {
                if (data.success) {
                    alert("更新成功");
                    $('.modal').modal('hide');
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

        Project.query(
            function (data) {
                if (data.success) {
                    $scope.projects = data.result;
                }
            }
        );

        $scope.crud = new Crud(Template, function (data) {
            if (data.success) {
                $scope.crud.p.load();
                $('.modal').modal('hide');
            } else {
                Util.handleFailure(data);
            }
        });

        //从文件读取模板内容
        $window.readContent = function (files) {
            var file = files[0];
            var reader = new FileReader();
            reader.onload = function (e) {
                $scope.crud.record.content = e.target.result;
                if (!$scope.crud.record.suffix) {
                    $scope.crud.record.suffix = file.name.replace(/^_\./, '.');//去掉.号前面唯一的_
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
    var defaultErrorMessage = '访问错误';
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
        R.save(record, onSuccess, onError);
    };

    this.remove = function (record, message) {
        if (!confirm(message || "确定要删除这条记录吗?")) {
            return;
        }
        R.remove({}, {id: record.id}, onSuccess, onError);
    };

    this.p = new Page(R.query).load();
};
"use strict";

/* global Util */
/* exported Page */

// 分页的处理类
var Page = function (queryFunc) {
    var page = 0, limit = 10, pages = 0, total = 0, qo;
    function SuccessCallback(data) {
        if (data.success) {
            if (typeof data.total === "number") {
                total = data.total;
                var newPages = Math.ceil(total / limit);
                if (pages !== newPages) {
                    pages = newPages;
                    if (page > newPages) {// 当前页号page大于总页数时，是没有数据的，需要修正page然后重新加载
                        page = Math.min(newPages, 1);
                        this && this.load();
                        return;
                    }
                }
                if (!page && pages) {
                    page = 1;
                }
                var p = this;
                p.page = page;
                p.limit = limit;
                p.pages = pages;
                p.total = total;
                p.from = Math.max((page - 1) * limit + 1, 0);
                p.to = Math.min(page * limit, total);
                p.result = data.result;
            }
        } else {
            Util.handleFailure(data);
        }
    }
    this.q = {};
    this.isQueryChanged = function () {
        return !angular.equals(this.q, qo);
    };
    this.load = function (checkQueryChanged) {
        var q = this.q;
        if (checkQueryChanged && angular.equals(this.q, qo)) {
            // 如果设置checkQueryChanged为true, 并且本次查询条件this.q和前次查询条件qo是一样的,
            // 则不执行不必要加载操作.
            return;
        }
        q.page = page || 1;
        q.limit = limit;
        qo = angular.copy(q);
        queryFunc(q, angular.bind(this, SuccessCallback));
        return this;
    };
    this.first = function () {
        if (page > 1) {
            page = 1;
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
        if (page > 1) {
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
    this.size = function (size) { // 设置每页显示条数
        limit = size;
        page = Math.min(page, Math.ceil(total / limit));// 当前页数p不能大于总页数
        page = Math.max(page, 1);
        this.load();
    };
    this.goto = function (goto) {
        page = goto;
        this.load();
    };
    this.reset = function () {
        this.q = {};
        page = 1;
        this.load();
    };
};
"use strict";

/* exported Util */

var Util = window.Util || {};
// 分页的处理类
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
        if (data.code === '0001') {
            location.href = 'login?redirect=' + encodeURIComponent(location.href);
        } else {
            alert(data.message || '访问错误');
        }
    }
};
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
});
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
"use strict";

/*global genApp, angular, Util*/

genApp.
controller('GeneratorCtrl', ['$scope', 'Project', 'Module', 'Template',
    function ($scope, Project, Module, Template) {
        Project.query(
            function (data) {
                if (data.success) {
                    $scope.projects = data.result;
                    $scope.switchProject($scope.projects[0]);
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

        //切换项目后需要切换该项目所配置的模板和模块
        $scope.switchProject = function (record) {
            record = record || {};

            Template.query(
                {projectId: record.id},
                function (data) {
                    if (data.success) {
                        $scope.templates = data.result;
                    }
                }
            );

            Module.query(
                {projectId: record.id},
                function (data) {
                    $scope.project = angular.copy(record);
                    $scope.projectName = record.name;
                    if (data.success) {
                        $scope.modules = data.result;
                        $scope.switchModule($scope.modules[0]);
                    }
                }
            );
        };

        $scope.switchModule = function (record) {
            record = record || {};
            // Module.table({table: record.tableName || record.name}, function (data) {
                $scope.gen = angular.copy(record);
                $scope.label = record.displayName;
                // if (data.success) {
                //     var columns = data.result, imports = [];
                    var columns = record.columns, imports = [];
                    for (var i = 0; i < columns.length; i++) {
                        var column = columns[i];
                        if (/^(var|text)/.test(column.type)) {
                            column.type = 'String';
                            column.jdbcType = 'VARCHAR';
                        } else if (column.type.startsWith('int')) {
                            column.type = 'Integer';
                            column.jdbcType = 'INTEGER';
                        } else if (column.type.startsWith('smallint')) {
                            column.type = 'Short';
                            column.jdbcType = 'INTEGER';
                        } else if (column.type.startsWith('tinyint')) {
                            column.type = 'Boolean';
                            column.jdbcType = 'BIT';
                        } else if (column.type.startsWith('timestamp') || column.type.startsWith('datetime')) {
                            column.type = 'Date';
                            column.jdbcType = 'TIMESTAMP';
                            imports.indexOf('java.util.Date') < 0 && imports.push('java.util.Date');
                        } else {
                            column.type = 'String';
                            column.jdbcType = 'VARCHAR';
                        }
                    }
                    $scope.columns = columns;
                    $scope.imports = imports;
                // }
            // });
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
            }
            if (!arr.length) {
                alert('请选择需要上传的文件!');
                return;
            }
            Module.upload({id : name}, {root: $scope.project.path, data:arr}, onSuccess);
        };

    }]
);