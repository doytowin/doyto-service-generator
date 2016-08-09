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