app.regCtrl("searchPageCtrl", ["$scope", "$http", function($scope, $http) {
  $scope.$parent.page = "search";
  $scope.query = "";
  $scope.sites = [];
  $scope.search = function() {
    console.log("search");
    filter = JSON.stringify({
      fuzzyQuery: {field:"nume",value:$scope.query}
    });
    $http.post('/api/list', filter).then(function successCallback(response) {
      console.log(response);
      $scope.sites = response.data.sites;
    }, function errorCallback(response) {
      console.log(response);
    });
  }

  $scope.search();
}]);
