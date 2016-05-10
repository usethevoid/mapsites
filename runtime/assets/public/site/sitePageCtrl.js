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
