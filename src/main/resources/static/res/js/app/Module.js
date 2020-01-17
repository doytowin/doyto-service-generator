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
    function ($scope, Project, Module, Column) {
        $scope.crud = new Crud(Module, function (data) {
            if (data.success) {
                $scope.crud.p.load();
                $('.modal').modal('hide');
            } else {
                Util.handleFailure(data);
            }
        });
        $scope.crud.import = Module.import;

        Project.query(
            function (json) {
                if (json.success) {
                    $scope.projects = {};
                    for (var i = 0; i < json.data.length; i++) {
                        var project = json.data[i];
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
                alert('sql格式错误!');
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
                    alert("更新成功");
                } else {
                    Util.handleFailure(data);
                }
            });
        };
    }]
);

