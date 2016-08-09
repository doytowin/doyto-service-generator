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

