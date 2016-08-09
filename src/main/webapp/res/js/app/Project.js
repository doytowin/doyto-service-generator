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