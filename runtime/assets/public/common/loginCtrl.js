app.controller('loginCtrl', ["$scope", "$uibModalInstance", "$http", "onLogin",
  function($scope, $uibModalInstance, $http, onLogin) {
    $scope.credentials = {};
    $scope.onLogin = onLogin;

    $scope.login = function() {
      console.log('send ' + $scope.credentials.username + '/' + $scope.credentials.password);
      $http.post('/api/login', {
        username: $scope.credentials.username,
        password: $scope.credentials.password
      }).then(function successCallback(response) {
          if (!(response.data.name.length > 0) || response.data.secondsValid == 0) return;
          sessionStorage.setItem('name', response.data.name);
          sessionStorage.setItem('expire', Date.now() + (response.data.secondsValid * 1000));
          $uibModalInstance.dismiss();
          $scope.onLogin();
        },
        function errorCallback(response) {
          console.log("login failed");
        });
    }

  }
]);
