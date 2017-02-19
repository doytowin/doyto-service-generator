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