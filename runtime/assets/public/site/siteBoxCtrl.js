app.regCtrl('siteBoxCtrl', ["$scope", "$http", "$routeParams", function($scope, $http, $routeParams) {
  $scope.$parent.page = "site";
  $scope.site = {}
  $scope.images = [];
  $scope.siteId = $routeParams.siteId;

  $scope.update = function() {
    $http.get('/api/site/' + $scope.siteId).then(function successCallback(response) {
      console.log(response);
      $scope.site = response.data;
      $scope.updateMapFeatures(response.data.coords,response.data.areaGeoJson);
    }, function errorCallback(response) {
      console.log(response);
    });
  }

  $scope.updateImages = function() {
    $http({
      method: 'GET',
      url: '/api/siteimages/' + $scope.siteId
    }).then(function successCallback(response) {
      if (response.data.images) {
        $scope.images = [];
        for (var i = 0; i < response.data.images.length; ++i) {
          var img = response.data.images[i];
          $scope.images.push({
            "img": img.imagePath + "/" + $scope.siteId + "/" + img.file,
            "thumb": img.thumbPath + "/" + $scope.siteId + "/" + img.file,
            "description": img.title ? img.title : "N/A"
          });
        }
      }
      connWarning = false;
    }, function errorCallback(response) {
      connWarning = true;
    });
  }

  $scope.update();
  $scope.updateImages();
}]);
