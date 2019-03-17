"use strict";

/*global genApp, angular, Util*/

genApp.
controller('GeneratorCtrl', ['$scope', '$timeout', '$log', 'Project', 'Module', 'Template',
    function ($scope, $timeout, $log, Project, Module, Template) {
        $scope.data = {};
        Project.query(
            function (data) {
                if (data.success) {
                    $scope.projects = data.result;

                    var defaultProject = $scope.projects[0];
                    if (localStorage.projectId) {
                        for (var i = 0; i < $scope.projects.length; i++) {
                            var project = $scope.projects[i];
                            if (project.id == localStorage.projectId) {
                                defaultProject = project;
                                break;
                            }
                        }
                    }
                    $scope.switchProject(defaultProject);
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
            $scope.currentProject = record;
            localStorage.projectId = record.id;

            Template.query(
                {
                    projectId: record.id,
                    valid: true
                },
                function (data) {
                    if (data.success) {
                        $scope.templates = data.result;
                    }
                }
            );

            Module.query(
                {
                    projectId: record.id,
                    valid: true
                },
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

        $scope.switchModule = function (record, records, index) {
            record = record || {};
            $scope.gen = angular.copy(record);
            $scope.label = record.displayName || record.name;
            $scope.data.labelFilter = record.displayName || record.name;
            if (record.columns) {
                $scope.columns = record.columns;
                $scope.imports = record.imports;
                if (angular.isArray(records) && index < records.length) {
                    $timeout(function () {
                        $scope.upload(record.name, true);
                        $scope.switchModule(records[index], records, index + 1);
                    }, 50)
                }
            } else {
                Module.table({
                    table: record.tableName || record.name,
                    projectId: record.projectId
                }, function (data) {
                    if (data.success) {
                        var columns = data.result, imports = [];
                        for (var i = 0; i < columns.length; i++) {
                            var column = columns[i];
                            if (/^(var|text)/.test(column.type)) {
                                column.type = 'String';
                                column.jdbcType = 'VARCHAR';
                            } else if (column.type.startsWith('int')) {
                                column.type = 'Integer';
                                column.jdbcType = 'INTEGER';
                            } else if (column.type.startsWith('bigint')) {
                                column.type = 'Long';
                                column.jdbcType = 'LONG';
                            } else if (column.type.startsWith('smallint')) {
                                column.type = 'Short';
                                column.jdbcType = 'INTEGER';
                            } else if (column.type.startsWith('tinyint(1)') || column.type == 'bit(1)' ) {
                                column.type = 'Boolean';
                                column.jdbcType = 'BIT';
                            } else if (column.type.startsWith('tinyint')) {
                                column.type = 'Byte';
                                column.jdbcType = 'BIT';
                            } else if (column.type.startsWith('timestamp') || column.type.startsWith('datetime')) {
                                column.type = 'Date';
                                column.jdbcType = 'TIMESTAMP';
                                imports.indexOf('java.util.Date') < 0 && imports.push('java.util.Date');
                            } else if (column.type.startsWith('date')) {
                                column.type = 'Date';
                                column.jdbcType = 'Date';
                                imports.indexOf('java.util.Date') < 0 && imports.push('java.util.Date');
                            } else {
                                column.type = 'String';
                                column.jdbcType = 'VARCHAR';
                            }
                        }
                        $scope.columns = record.columns = columns;
                        $scope.imports = record.imports = imports;

                        if (angular.isArray(records) && index < records.length) {
                            $timeout(function () {
                                $scope.upload(record.name, true);
                                $scope.switchModule(records[index], records, index + 1);
                            }, 50)//等待数据上传完毕
                        }
                    }
                });
            }
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

        function saveAs(path, content) {
            var file = new File([content], path, {type: "text/plain;charset=utf-8"});
            file.open("w"); // open file with write access
            file.writeln('');
            file.close();
        }
        function errorHandler(e) {
            // var msg = '';
            //
            // switch (e.code) {
            //     case FileError.QUOTA_EXCEEDED_ERR:
            //         msg = 'QUOTA_EXCEEDED_ERR';
            //         break;
            //     case FileError.NOT_FOUND_ERR:
            //         msg = 'NOT_FOUND_ERR';
            //         break;
            //     case FileError.SECURITY_ERR:
            //         msg = 'SECURITY_ERR';
            //         break;
            //     case FileError.INVALID_MODIFICATION_ERR:
            //         msg = 'INVALID_MODIFICATION_ERR';
            //         break;
            //     case FileError.INVALID_STATE_ERR:
            //         msg = 'INVALID_STATE_ERR';
            //         break;
            //     default:
            //         msg = 'Unknown Error';
            //         break;
            // };

            console.log('Error: ' + e.message);
        }
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

                // window.requestFileSystem  = window.requestFileSystem || window.webkitRequestFileSystem;
                //
                // navigator.webkitPersistentStorage.requestQuota(1024*1024,
                //     function(grantedBytes) {
                //         window.requestFileSystem(window.PERSISTENT, grantedBytes, function (fs) {
                //             fs.root.getFile("requestFileSystem_test.txt", {
                //             // fs.root.getFile($scope.currentProject.path + d.path, {
                //                 create: true
                //             }, function (fileEntry) {
                //
                //                 // Create a FileWriter object for our FileEntry (log.txt).
                //                 fileEntry.createWriter(function (fileWriter) {
                //
                //                     fileWriter.onwriteend = function (e) {
                //                         console.log('Write completed.');
                //                     };
                //
                //                     fileWriter.onerror = function (e) {
                //                         console.log('Write failed: ' + e.toString());
                //                     };
                //
                //                     // Create a new Blob and write it to log.txt.
                //                     var blob = new Blob([d.text], {type: 'text/plain'});
                //
                //                     fileWriter.write(blob);
                //
                //                 }, errorHandler)
                //             }, errorHandler)
                //         }, errorHandler)
                //     },
                //     function(errorCode) {
                //         alert("Storage not granted.");
                //     }
                // );
            }
            if (!arr.length) {
                alert('请选择需要上传的文件!');
                return;
            }
            Module.upload({id : name}, {root: $scope.project.path, data:arr}, onSuccess);
        };

        $scope.uploadAll = function () {
            $scope.switchModule($scope.modules[0], $scope.modules, 1);
        };

    }]
);