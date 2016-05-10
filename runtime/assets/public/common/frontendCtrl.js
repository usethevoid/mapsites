app.controller('frontendCtrl', ["$scope", "$uibModal", "$http", '$cookies', '$location', 'configSrv',
  function($scope, $uibModal, $http, $cookies, $location, configSrv) {
    $scope.haveAuth = false;
    $scope.center = {
      lat: 46,
      lon: 25.3
    };
    $scope.overZoom = 6.5;
    $scope.siteZoom = 10;
    $scope.maxZoom = 16;
    $scope.appname = "";
    $scope.showSearchToGuests = false;

    $scope.$on('config', function(e,config) {
        $scope.appname = config.appName;
        $scope.cfg = config;
        if ($scope.onConfig) {
          $scope.onConfig(config);
          $scope.onConfig = null;
        }
    });

    $scope.onPageCreated = function(onConfig) {
      if ($scope.cfg)
        onConfig($scope.cfg);
      else
        $scope.onConfig = onConfig;
    };

    $scope.onAuth = function() {
      console.log("login");
      $scope.haveAuth = true;
      $scope.username = sessionStorage.getItem("name");
    }

    var checkAuth = function() {
      $http.get('/api/reauth', {}).then(function successCallback(response) {
          $scope.config = response.data.config;
          if (!(response.data.name) || response.data.secondsValid == 0) return;
          sessionStorage.setItem('name', response.data.name);
          sessionStorage.setItem('expire', Date.now() + (response.data.secondsValid * 1000));
          $scope.onAuth();
        },
        function errorCallback(response) {

        });
    }

    checkAuth();

    $scope.showLogin = function() {
      var modalInstance = $uibModal.open({
        animation: true,
        templateUrl: 'public/fragments/loginForm.html',
        controller: 'loginCtrl',
        size: "sm",
        resolve: {
          onLogin: function() {
            return $scope.onAuth;
          }
        }
      });
    }

    $scope.logout = function() {
      $scope.haveAuth = false;
      $cookies.remove('XSRF-TOKEN', {
        path: '/',
        domain: $location.host()
      });
    }


  }
]);
