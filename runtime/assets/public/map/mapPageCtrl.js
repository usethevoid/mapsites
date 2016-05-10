app.regCtrl('mapPageCtrl', ["$scope","$http", function($scope, $http) {
  $scope.$parent.page = "map";
  $scope.onPageCreated(function(config) {
      $scope.mapSearchFields = JSON.parse(config.mapSearchFieldsJson);
  });


  $scope.markers = L.markerClusterGroup({
    chunkedLoading: true,
    showCoverageOnHover: false,
    iconCreateFunction: function(cluster) {
      var childCount = cluster.getChildCount() < 100 ? cluster.getChildCount() : "99+";

      return new L.DivIcon({
        html: '<div><span>' + childCount + '</span></div>',
        className: 'marker-cluster marker-cluster-all',
        iconSize: new L.Point(40, 40)
      });
    }
  });

  $scope.map = new L.Map('map', {
    center: new L.LatLng($scope.center.lat, $scope.center.lon),
    zoom: $scope.overZoom,
    maxZoom: $scope.maxZoom
  });

  $scope.map.addLayer($scope.markers);

  $scope.update_points = function(filter) {
    $http.post('/api/list',filter).then(function successCallback(response) {
      var markers = [];
      var sites = response.data.sites;
      for (var i = 0; i < sites.length; i++) {
        var s = sites[i];
        var title = s.fields.nume;
        var coords = s.coords ? s.coords : {lat:0, lon:0};

        var content = '<a href="#/view/site/' + s.id + '">' + title + '</a>';
        var marker = L.marker(L.latLng(coords.lat, coords.lon), {
          title: title,
        });
        marker.bindPopup(content);
        markers.push(marker);
      }
      $scope.markers.clearLayers();
      $scope.markers.addLayers(markers);
      $scope.map.fitBounds($scope.markers.getBounds());
    }, function errorCallback(response) {
      console.log(response);
    });
  }

  $scope.update_points({});

}]);
