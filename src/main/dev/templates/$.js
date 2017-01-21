'use strict';

/*global genApp, Crud, Util*/

genApp.
factory('{{gen.name | capitalize}}', ['$resource',
    function ($resource) {
        return $resource('api/{{gen.name}}/:id', {id: '@id'});
    }]
).
controller('{{gen.name | capitalize}}Ctrl', ['$scope', '{{gen.name | capitalize}}',
    function ($scope, {{gen.name | capitalize}}) {
        $scope.crud = new Crud({{gen.name | capitalize}}, function (data) {
            if (data.success) {
                $scope.crud.p.load();
                $('.modal').modal('hide');
            } else {
                $scope.errors = Util.handleFailure(data);
            }
        });
    }]
);