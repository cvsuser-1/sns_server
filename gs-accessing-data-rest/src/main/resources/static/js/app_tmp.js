requirejs(["jquery", "bootstrap"], function (jquery, bootstrap) {
});

//Explicitly defines the "foo/title" module:
requirejs(['angular'], function(angular) {
  requirejs(["auth/auth", "home/home", "message/message","navigation/navigation"]);
  'use strict';
  require(['domReady!'], function (doc) {
    domReady(function () {
      //This function is called once the DOM is ready.
      //It will be safe to query the DOM and manipulate
      //DOM nodes in this function.

      angular
      .module('hello', [ 'ngRoute', 'auth', 'home', 'message', 'navigation' ])
      .config(
        function($routeProvider, $httpProvider, $locationProvider) {

          $locationProvider.html5Mode(true);

          $routeProvider.when('/', {
            templateUrl : 'js/home/home.html',
            controller : 'home'
          }).when('/message', {
            templateUrl : 'js/message/message.html',
          controller : 'message'
          }).when('/login', {
            templateUrl : 'js/navigation/login.html',
          controller : 'navigation'
          }).otherwise('/');

          $httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';

        }).run(function(auth) {

        // Initialize auth module with the home page and login/logout path
        // respectively
        auth.init('/', '/login', '/logout');
        
        angular.bootstrap(document, ['hello']);
      });
   });
   
    });
  });
});
