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

app.regCtrl('sitePageCtrl', ["$scope", function($scope) {
  $scope.$parent.page = "site";

  $scope.map = new L.Map('map', {
    center: new L.LatLng($scope.center.lat, $scope.center.lon),
    zoom: $scope.overZoom,
    maxZoom: 19
  });


  $scope.featureOverlay = new L.geoJson();
  $scope.featureOverlay.addTo($scope.map);
  $scope.mapFeatures = null;

  $scope.updateMapFeatures = function(coords,poly) {
    if (!coords && !poly)
      return;
    $scope.map.setView(new L.LatLng(coords.lat, coords.lon), $scope.siteZoom);  
    $scope.marker = L.marker([coords.lat, coords.lon]).addTo($scope.map);
    if (!poly)
      return;
    $scope.mapFeatures = poly;
    $scope.featureOverlay.clearLayers();
    $scope.featureOverlay.addData($scope.mapFeatures);
    $scope.map.fitBounds($scope.featureOverlay.getBounds());
  }

}]);
