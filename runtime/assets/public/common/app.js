var app = angular.module('gis', ['ui.bootstrap', 'formly', 'formlyBootstrap',
  'ngCookies', 'smart-table', 'ngRoute', 'ngFileUpload', 'jkuri.gallery'
]);

app.config(['$routeProvider','$controllerProvider',
  function($routeProvider,$controllerProvider) {
    app.regCtrl = $controllerProvider.register;

    $routeProvider.
    when('/view/:page/:siteId?', {
      templateUrl: function(urlattr) {
        return 'public/fragments/' + urlattr.page + '.html';
      },
      resolve: {
        load: function($q, $route, $rootScope) {
          var deferred = $q.defer();
          var dependencies = [
            'public/js/' + $route.current.params.page + '.js'
          ];

          LazyLoad.js(dependencies, function() {
            $rootScope.$apply(function() {
              deferred.resolve();
            });
          });

          return deferred.promise;
        }
      }
    }).
    when('/mod/:page/:siteId?', {
      templateUrl: function(urlattr) {
        return 'private/fragments/' + urlattr.page + '.html';
      },
      resolve: {
        load: function($q, $route, $rootScope) {
          var deferred = $q.defer();
          var dependencies = [
            'private/js/' + $route.current.params.page + '.js'
          ];

          LazyLoad.js(dependencies, function() {
            $rootScope.$apply(function() {
              deferred.resolve();
            });
          });

          return deferred.promise;
        }
      }
    }).
    otherwise({
      redirectTo: 'view/routing'
    });
  }
]);



app.run(function(formlyConfig) {
  formlyConfig.setType({
    name: 'typeahead',
    template: '<input type="text" ng-model="model[options.key]" uib-typeahead="item for item in to.options | filter:$viewValue | limitTo:8" class="form-control">',
    wrapper: ['bootstrapLabel', 'bootstrapHasError'],
  });
  formlyConfig.setType({
    name: 'years',
    template: '<form class="form-group"><div><input type="number" min="0" step="25" class="form-control year" ng-model="model[options.key].startyear">'+
      '<select class="form-control epoch" ng-model="model[options.key].startepoch"><option value="BC">BC</option><option value="AD">AD</option></select>&rarr;'+
      '<input type="number" min="0" step="25" class="form-control year" ng-model="model[options.key].endyear">'+
      '<select class="form-control epoch" ng-model="model[options.key].endepoch"><option value="BC">BC</option><option value="AD">AD</option></select></div></form>',
    wrapper: ['bootstrapLabel', 'bootstrapHasError'],
  });
});
