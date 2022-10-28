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

        //从文件读取模板内容
        $window.readContent = function (files) {
            var file = files[0];
            var reader = new FileReader();
            reader.onload = function (e) {
                $scope.crud.record.content = e.target.data;
                if (!$scope.crud.record.suffix) {
                    $scope.crud.record.suffix = file.name.replace(/^_\./, '.');//去掉.号前面唯一的_
                }
                $scope.$apply();
            };
            reader.readAsText(file);
        };
    }]
);