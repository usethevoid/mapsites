app.regCtrl('editSiteBoxCtrl', ["$scope", "$http", "$location","$uibModal", function($scope, $http, $location,$uibModal) {
  $scope.lastSave = 0;
  $scope.connWarning = false;

  function decorate_fields(fields) {
    return {
      "fields": fields
    }
  }

  $scope.getCurrentTime = function() {
    var time = (new Date()).toTimeString();
    var sep = time.indexOf(" ");
    return time.substring(0,sep);
  }

  function create() {
    $http({
      method: 'PUT',
      url: '/api/site',
      data: JSON.stringify(decorate_fields($scope.vm.site))
    }).then(function successCallback(response) {
      $scope.lastSave = $scope.getCurrentTime();
      var id = response.data.id;
      $location.path("mod/editsite/" + id);
      connWarning = false;
    }, function errorCallback(response) {
      connWarning = true;
    });
  }

  function save(id) {
    var delta = {};
    for (var field in $scope.vm.site) {
      var current = $scope.vm.site[field];
      var orig = $scope.origSite[field];
      if (current !== orig)
        delta[field] = current;
    }
    console.log(delta);

    $http({
      method: 'POST',
      url: '/api/private/site/' + id,
      data: JSON.stringify(decorate_fields(delta))
    }).then(function successCallback(response) {
      $scope.lastSave = $scope.getCurrentTime();
      connWarning = false;
    }, function errorCallback(response) {
      connWarning = true;
    });
  }

  $scope.save = function() {
    if ($scope.is_created()) {
      save($scope.siteId);
    } else {
      create();
    }
  }

  $scope._delete = function() {
    $http({
      method: 'DELETE',
      url: '/api/private/site/' + $scope.siteId
    }).then(function successCallback(response) {
      $location.path("/");
      connWarning = false;
    }, function errorCallback(response) {
      connWarning = true;
    });
  }

  $scope.delete = function() {
    var modalInstance = $uibModal.open({
      animation: true,
      templateUrl: 'private/fragments/confirmDelete.html',
      controller: 'confirmDeleteCtrl',
      size: "sm",
      resolve: {
        onConfirm: function() {
          return $scope._delete;
        }
      }
    });
  }

  $scope.update();
}]);
