'use strict';

/*global genApp, Crud, Util*/

genApp.
factory('Module', ['$resource',
    function ($resource) {
        return $resource('api/module/:id', {id: '@id'}, {
            upload: {url:'api/module/upload/:id', method: 'POST'},
            table: {url:'api/table/:table', method: 'GET'},
            template: {url:'api/template/', method: 'GET'}
        });
    }]
).
controller('ModuleCtrl', ['$scope','Project','Module','Column',
    function ($scope, Project, Module, Column) {
        $scope.crud = new Crud(Module, function (data) {
            if (data.success) {
                $scope.crud.p.load();
                $('.modal').modal('hide');
            } else {
                Util.handleFailure(data);
            }
        });
        Project.query(
            function (data) {
                if (data.success) {
                    $scope.projects = data.result;
                    if (localStorage.projectId) {
                        for (var i = 0; i < $scope.projects.length; i++) {
                            var p = $scope.projects[i];
                            if (p.id == localStorage.projectId) {
                                $scope.crud.project = p;
                                $scope.crud.p.q.projectId = p.id;
                                $scope.crud.p.load(true);
                                break;
                            }
                        }
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
                alert('sql格式错误!');
            }
        };

        $scope.editLabels = function(record) {
            Column.query({tableName:record.tableName, projectId:record.projectId}, function (data) {
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

