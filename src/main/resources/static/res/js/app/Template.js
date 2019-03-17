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
            function (data) {
                if (data.success) {
                    $scope.projects = data.result;
                    console.log($scope.projects);
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

        $scope.crud.add = function () {
            this.record = {cap:true};
        };

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