app.regCtrl('editSitePageCtrl', ["$scope", "$route", "formlyConfig", "$http", function($scope, $route, formlyConfig, $http) {
  $scope.$parent.page = "editsite";

  $scope.onPageCreated(function(config) {
    $scope.previewFields = JSON.parse(config.previewFieldsJson);
    $scope.completeFields = JSON.parse(config.completeFieldsJson);
    $scope.imageTypes = config.imageTypes;
    $scope.showAreaImport = config.appFlags.areasEnabled;
  });

  if ("siteId" in $route.current.params)
    $scope.siteId = $route.current.params.siteId;

  $scope.vm = new Object();
  $scope.vm.site = {};
  $scope.origSite = null;

  $scope.map = new L.Map('map', {
    center: new L.LatLng($scope.center.lat, $scope.center.lon),
    zoom: $scope.overZoom,
    maxZoom: $scope.maxZoom
  });

  $scope.styles = {
    "tmp": {
      "color": "#ff7800",
      "weight": 5,
      "opacity": 0.65
    },
    "saved": {
      "color": "#0078ff",
      "weight": 2,
      "opacity": 0.4
    }
  }

  $scope.featureOverlay = new L.geoJson();
  $scope.featureOverlay.addTo($scope.map);
  $scope.mapFeatures = null;



  $scope.updateMapFeatures = function(coords, poly) {
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


  $scope.onGeoJson = function(item, data) {
    $scope.updateMapFeatures(data);
    $scope.featureOverlay.setStyle($scope.styles["tmp"]);
  }

  $scope.onImage = function(item, response) {

  }

  $scope.saveArea = function() {
    $http({
      method: 'POST',
      url: '/api/private/sitearea/' + $scope.siteId,
      data: JSON.stringify($scope.mapFeatures)
    }).then(function successCallback(response) {
      $scope.connWarning = false;
      $scope.featureOverlay.setStyle($scope.styles["saved"]);
      if (response.data.areaGeoJson != null)
        $scope.updateMapFeatures(JSON.parse(response.data.areaGeoJson));
    }, function errorCallback(response) {
      $scope.connWarning = true;
    });
  }

  $scope.update = function() {
    if (!$scope.siteId) return;
    $http.get('/api/site/' + $scope.siteId).then(function successCallback(response) {
      $scope.origSite = response.data;
      if (!$scope.origSite) {
        $scope.siteId = null;
        return;
      }

      $scope.updateMapFeatures(response.data.coords,response.data.areaGeoJson);
      $scope.vm.site = JSON.parse(JSON.stringify($scope.origSite.fields));
      connWarning = false;
    }, function errorCallback(response) {
      console.log(response);
      connWarning = true;
    });

  }

  $scope.is_created = function() {
    var res = $scope.siteId ? true : false;
    return res;
  }
  if ($scope.is_created())
    $scope.vm.siteFields = $scope.completeFields;
  else
    $scope.vm.siteFields = $scope.previewFields;

}]);
