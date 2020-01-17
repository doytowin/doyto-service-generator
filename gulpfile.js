// 引入 gulp
var gulp = require('gulp');

// 引入组件
var jshint = require('gulp-jshint'),
    concat = require('gulp-concat'),
    uglify = require('gulp-uglify'),
    clncss = require('gulp-clean-css'),
    rename = require('gulp-rename'),
    os     = require('os'),
    //fs     = require('fs'),
    path = require('path');

// 检查脚本
gulp.task('lint',  async() => {
    gulp.
    src('./src/main/webapp/res/js/app/*.js').
    pipe(jshint({
        "node": true,
        "browser": true,
        "esnext": true,
        "bitwise": true,
        "camelcase": true,
        "curly": true,
        "eqeqeq": true,
        "expr": true,
        "immed": true,
        "devel": true,
        "indent": 4,
        "latedef": true,
        "newcap": true,
        "noarg": true,
        //"quotmark": "single",
        "undef": true,
        "unused": true,
        "strict": true,
        "trailing": true,
        "smarttabs": true,
        "multistr": true,
        "globals": {
            "$": false,
            "angular": false
        }
    })).
    pipe(jshint.reporter('default'));
});

// 合并，压缩文件
gulp.task('scripts',  async() => {
    gulp.src('./src/main/resources/static/res/js/app/*.js')
        .pipe(concat('app.js'))
        .pipe(gulp.dest('./src/main/resources/static/res/js'))
        .pipe(rename('app.min.js'))
        .pipe(uglify())
        .pipe(gulp.dest('./src/main/resources/static/res/js'))
    gulp.src('./src/main/resources/static/res/lib/markdown/marked.js')
        .pipe(rename('marked.min.js'))
        .pipe(uglify())
        .pipe(gulp.dest('./src/main/resources/static/res/lib/markdown'))
})

// 合并，压缩文件
gulp.task('css',  async() => {
    gulp.src('./src/main/resources/static/res/css/app/*.css')
        .pipe(concat('app.css'))
        .pipe(gulp.dest('./src/main/resources/static/res/css'))
        .pipe(rename('app.min.css'))                            //- 合并后的文件名
        .pipe(clncss())                                         //- 压缩处理成一行
        .pipe(gulp.dest('./src/main/resources/static/res/css'));    //- 输出文件本地
});

// 默认任务
gulp.task('default', gulp.series('lint', 'css', 'scripts',  async() => {
    // 监听文件变化
    gulp.watch('./src/main/resources/static/res/js/app/*.js', gulp.series('lint', 'scripts'));
    gulp.watch('./src/main/resources/static/res/css/app/*.css', gulp.series('css'));
}));

