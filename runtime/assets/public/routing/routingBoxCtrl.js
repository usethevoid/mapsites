app.regCtrl('routingBoxCtrl', ["$scope", function($scope) {
  $scope.mode = null;
  $scope.days = 1;

  $scope.$on('daysUpdated', function(e,days) {
    $scope.days = days;
    $scope.$apply();
  });

  $scope.updatedMode = function() {
    console.log($scope.mode);
    $scope.setPageMode($scope.mode);
  }

  $scope.updateRoute = function() {
    $scope.mode = null;
    $scope.setPageMode($scope.mode);
    $scope._updateRoute();
  }

  $scope.clearRoute = function() {
    $scope.mode = null;
    $scope.setPageMode($scope.mode);
    $scope._clearRoute();
  }




}]);
