"use strict";

/*global genApp, angular, Util*/

genApp.
controller('GeneratorCtrl', ['$scope', 'Project', 'Module', 'Template',
    function ($scope, Project, Module, Template) {
        Project.query(
            function (data) {
                if (data.success) {
                    $scope.projects = data.result;
                    $scope.switchProject($scope.projects[0]);
                }
            }
        );

        function onSuccess(data) {
            if (data.success) {
                // load();
                //$('.modal').modal('hide');
            } else {
                Util.handleFailure(data);
            }
        }

        //切换项目后需要切换该项目所配置的模板和模块
        $scope.switchProject = function (record) {
            record = record || {};

            Template.query(
                {projectId: record.id},
                function (data) {
                    if (data.success) {
                        $scope.templates = data.result;
                    }
                }
            );

            Module.query(
                {projectId: record.id},
                function (data) {
                    $scope.project = angular.copy(record);
                    $scope.projectName = record.name;
                    if (data.success) {
                        $scope.modules = data.result;
                        $scope.switchModule($scope.modules[0]);
                    }
                }
            );
        };

        $scope.switchModule = function (record) {
            record = record || {};
            // Module.table({table: record.tableName || record.name}, function (data) {
                $scope.gen = angular.copy(record);
                $scope.label = record.displayName;
                // if (data.success) {
                //     var columns = data.result, imports = [];
                    var columns = record.columns, imports = [];
                    for (var i = 0; i < columns.length; i++) {
                        var column = columns[i];
                        if (/^(var|text)/.test(column.type)) {
                            column.type = 'String';
                            column.jdbcType = 'VARCHAR';
                        } else if (column.type.startsWith('int')) {
                            column.type = 'Integer';
                            column.jdbcType = 'INTEGER';
                        } else if (column.type.startsWith('smallint')) {
                            column.type = 'Short';
                            column.jdbcType = 'INTEGER';
                        } else if (column.type.startsWith('tinyint')) {
                            column.type = 'Boolean';
                            column.jdbcType = 'BIT';
                        } else if (column.type.startsWith('timestamp') || column.type.startsWith('datetime')) {
                            column.type = 'Date';
                            column.jdbcType = 'TIMESTAMP';
                            imports.indexOf('java.util.Date') < 0 && imports.push('java.util.Date');
                        } else {
                            column.type = 'String';
                            column.jdbcType = 'VARCHAR';
                        }
                    }
                    $scope.columns = columns;
                    $scope.imports = imports;
                // }
            // });
        };

        $scope.add = function (gen) {
            gen.id = undefined;
            Module.save(gen, onSuccess);
        };

        $scope.save = function (gen) {
            Module.save(gen, onSuccess);
        };

        $scope.remove = function (gen) {
            Module.remove({id: gen.id}, onSuccess);
        };

        $scope.upload = function (name, all) {
            var arr = [];
            var templates = $scope.templates;
            for (var i = 0; i < templates.length; i++) {
                var o = templates[i];
                if (!all && !o.upload) {continue;}
                var prefix = o.cap ? Util.capitalize(name) : name;
                var d = {};
                d.text = angular.element(document.getElementById('template-' + o.id)).find('pre').text();
                d.path = o.path + prefix + o.suffix;
                d.key = o.suffix;
                arr.push(d);
            }
            if (!arr.length) {
                alert('请选择需要上传的文件!');
                return;
            }
            Module.upload({id : name}, {root: $scope.project.path, data:arr}, onSuccess);
        };

    }]
);