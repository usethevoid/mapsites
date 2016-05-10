app.service('configSrv', ['$http','$rootScope', function($http, $rootScope) {
  var self = this;
  self.config = null;
  $http.get('/api/config').then(
    function(response) {
      self.config = response.data;
      $rootScope.$broadcast('config',self.config);
    },
    function(response) {
      console.error(response);
    });
}]);
