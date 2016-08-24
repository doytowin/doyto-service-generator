<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<!DOCTYPE html>
<html ng-app="GenApp">
<head>
    <base href="<c:url value="/"/>">
    <title>Generator</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="stylesheet" type="text/css" href="<c:url value="/webjars/bootstrap/3.3.6/css/bootstrap.min.css"/>"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/webjars/font-awesome/4.6.3/css/font-awesome.min.css"/>"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/res/css/app.min.css"/>"/>

    <script type="text/javascript" src="<c:url value="/webjars/jquery/2.1.1/jquery.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/webjars/bootstrap/3.3.6/js/bootstrap.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/webjars/angular/1.5.7/angular.min.js"/>"></script>
    <script src="<c:url value="/webjars/angular-ui-router/0.3.1/release/angular-ui-router.min.js"/>"></script>
    <script src="<c:url value="/webjars/angular-resource/1.5.7/angular-resource.min.js"/>"></script>
    <!--<script src="/webjars/angular-touch/1.5.7/angular-touch.min.js"></script>-->
    <script src="<c:url value="/res/lib/angular/ct-ui-router-extras.min.js"/>"></script>

    <script>
        var genApp = angular.module('GenApp', [
            'ui.router',
            'ct.ui.router.extras.sticky',
            'ngResource'
        ]);
        var Config = window.Config || {};
        Config.models = [
            {
                url: "generator",
                title: "自动生成",
                sticky: true
            },
            {
                url: "project",
                title: "项目",
                sticky: true
            },
            {
                url: "module",
                title: "模块",
                sticky: true
            },
            {
                url: "template",
                title: "模板",
                sticky: true
            },
            {
                url: "column",
                title: "表列",
                sticky: true
            }
        ];
        Config.base = 'app';
    </script>
    <script src="<c:url value="/res/js/app.min.js"/>"></script>
</head>
<body>
<header class="navbar navbar-static-top navbar-inverse" id="top" role="banner">
    <div class="container">
        <div class="navbar-header">
            <button class="navbar-toggle collapsed" type="button" data-toggle="collapse" data-target="#bs-navbar" aria-controls="bs-navbar" aria-expanded="false">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a ui-sref="app.generator" class="navbar-brand">自动生成</a>
        </div>
        <nav id="bs-navbar" class="collapse navbar-collapse">
            <ul class="nav navbar-nav">
                <li ui-sref-active="active"><a ui-sref="app.project">项目</a></li>
                <li ui-sref-active="active"><a ui-sref="app.module">模块</a></li>
                <li ui-sref-active="active"><a ui-sref="app.template">模板</a></li>
            </ul>
        </nav>
    </div>
</header>
<div class="container" ui-view></div>
</body>
</html>
