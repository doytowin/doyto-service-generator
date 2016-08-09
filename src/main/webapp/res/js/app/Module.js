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
                var $1 = RegExp.$1.toLowerCase();
                record.name = $1;
                record.modelName = Util.capitalize($1);
                record.varName = $1;
                record.fullName = $1;
                record.displayName = $1;

                $scope.sqlResolved = true;
            } else {
                alert('sql格式错误!');
            }
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

