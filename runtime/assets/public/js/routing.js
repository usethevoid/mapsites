app.regCtrl('planningBoxCtrl', ["$scope", "timelineSrv", "routingSrv", function($scope, timelineSrv, routingSrv) {
  var H = 40;
  var PADDING = 50;
  $scope.stops = null;
  $scope.W = window.innerWidth - PADDING;

  $scope.$on('routeUpdated', function() {
    $scope.stops = routingSrv.getStops();
    $scope.redraw();
  });

  $scope.$on('routeCleared', function() {
    $scope.stops = null;
    $scope.paper.clear();
  });

  $scope.init = function() {
    if ($scope.paper) {
      $scope.paper.setSize($scope.W, H)
    } else {
      $scope.paper = Raphael("timeline1", $scope.W, H);
    }
    $scope.redraw();
  }
  $scope.redraw = function() {
    if (!$scope.stops)
      return;
    $scope.paper.clear();
    timelineSrv.createTimeline($scope.paper, $scope.W, H, routingSrv.getStopCount(),
      function(id) {
        $scope.setActiveWaypoint(id);
      },
      function() {
        var selected = timelineSrv.getSelected();
        $scope.updateDays(selected.length + 1);
        console.log(selected);
      });
  }

  $scope.init();

  window.addEventListener('resize', function(e) {
    $scope.W = window.innerWidth - PADDING;
    $scope.init();
  }, true);

}]);

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

app.regCtrl('routingPageCtrl', ["$scope", "pointsSrv", "routingSrv", function($scope, pointsSrv, routingSrv) {
  $scope.startMarker = null;
  $scope.endMarker = null;
  $scope.activeMarker = null
  $scope.pageMode = null;
  $scope.routeLength = null;
  $scope.days = 1;
  $scope.stops = null;

  $scope.waypointMarkers = [];
  $scope.addedMarkers = [];

  $scope.startIcon = L.icon({
    iconUrl: 'css/greener-marker-icon.png',
    iconSize: [25, 41],
    iconAnchor: [13, 41]
  });
  $scope.endIcon = L.icon({
    iconUrl: 'css/orange-marker-icon.png',
    iconSize: [25, 41],
    iconAnchor: [13, 41]
  });
  $scope.disabledIcon = L.icon({
    iconUrl: 'css/gray-marker-icon.png',
    iconSize: [25, 41],
    iconAnchor: [13, 41]
  });
  $scope.enabledIcon = L.icon({
    iconUrl: 'css/blue-marker-icon.png',
    iconSize: [25, 41],
    iconAnchor: [13, 41]
  });
  $scope.waypointIcon = L.icon({
    iconUrl: 'css/green-marker-icon.png',
    iconSize: [25, 41],
    iconAnchor: [13, 41]
  });
  $scope.activeIcon = L.icon({
    iconUrl: 'css/dot.png',
    iconSize: [50,50],
    iconAnchor: [25, 25]
  });

  var selectMarkerButton = function(marker) {
    var button = document.createElement("button");
    button.className = "btn btn-sm btn-success";
    button.innerHTML = '<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>';
    button.onclick = function() {
      $scope.selectMarker(marker);
    }
    return button;
  }

  var unselectMarkerButton = function(marker) {
    var button = document.createElement("button");
    button.className = "btn btn-sm btn-danger";
    button.innerHTML = '<span class="glyphicon glyphicon-minus" aria-hidden="true"></span>';
    button.onclick = function() {
      $scope.unselectMarker(marker);
    }
    return button;
  }
  var removeMarkerButton = function(marker) {
    var button = document.createElement("button");
    button.className = "btn btn-sm btn-danger";
    button.innerHTML = '<span class="glyphicon glyphicon-remove" aria-hidden="true"></span>';
    button.onclick = function() {
      $scope.removeMarker(marker);
    }
    return button;
  }

  var genericPopup = function(content,marker,buttons) {
    var popupContent = document.createElement("div");
    if (content) {
      var contentWrapper = document.createElement("div");
      contentWrapper.innerHTML = content;
      popupContent.appendChild(contentWrapper);
    }
    for(var i=0;i<buttons.length;++i)
      popupContent.appendChild(buttons[i]);
    return popupContent;
  }


  $scope.map = new L.Map('map', {
    center: new L.LatLng($scope.center.lat, $scope.center.lon),
    zoom: $scope.overZoom,
    maxZoom: $scope.maxZoom
  });

  $scope.featureOverlay = new L.geoJson();
  $scope.featureOverlay.addTo($scope.map);
  $scope.mapFeatures = null;

  $scope.markersLayer = null;

  $scope._updateRoute = function() {
    routingSrv.update(function(geoJsonObj,routeLength) {
      $scope.$broadcast('routeUpdated');
      $scope.mapFeatures = geoJsonObj;
      $scope.routeLength = routeLength;
      $scope.featureOverlay.clearLayers();
      $scope.featureOverlay.addData($scope.mapFeatures);
      $scope.stops = routingSrv.getStops();
      setTimeout(function() {
        $scope.map.fitBounds($scope.featureOverlay.getBounds());
      }, 300);
    }, $scope.startMarker, $scope.endMarker, $scope.waypointMarkers);
  }

  $scope._clearRoute = function() {
    $scope.mapFeatures = null;
    $scope.featureOverlay.clearLayers();
    if ($scope.startMarker)
      $scope.map.removeLayer($scope.startMarker)
    if ($scope.endMarker)
      $scope.map.removeLayer($scope.endMarker)
    if ($scope.activeMarker)
        $scope.map.removeLayer($scope.activeMarker)
    for(var i =0;i<$scope.waypointMarkers.length;++i)
      $scope.unselectMarker($scope.waypointMarkers[i]);
    for(var i =0;i<$scope.addedMarkers.length;++i)
      $scope.removeMarker($scope.addedMarkers[i]);
    $scope.waypointMarkers = [];
    $scope.addedMarkers = [];
    $scope.startMarker = null;
    $scope.endMarker = null;
    $scope.activeMarker = null;
    $scope.pageMode = null;
    $scope.routeLength = null;
    $scope.$broadcast('routeCleared');
  }

  $scope.selectMarker = function(marker) {
    if (!marker)
      return;
    marker.setIcon($scope.enabledIcon);
    marker.closePopup();
    marker.bindPopup(genericPopup(marker.site.fields.nume,marker,[unselectMarkerButton(marker)]));
    $scope.waypointMarkers.push(marker);
  }



  $scope.unselectMarker = function(marker) {
    if (!marker)
      return;
    var site = marker.site;
    marker.setIcon($scope.disabledIcon);
    marker.closePopup();
    if (site)
      marker.bindPopup(genericPopup(site.fields.nume,marker,[selectMarkerButton(marker)]));
    var index = $scope.waypointMarkers.indexOf(marker);
    $scope.waypointMarkers[index] = undefined;
  }

  $scope.removeMarker = function(marker) {
    if (!marker)
      return;
    var index = $scope.waypointMarkers.indexOf(marker);
    $scope.waypointMarkers[index] = undefined;
    index = $scope.addedMarkers.indexOf(marker);
    $scope.addedMarkers[index] = undefined;
    $scope.map.removeLayer(marker);
  }

  var titleGenerator = function(site) {
    return site.fields.nume;
  }
  var iconGenerator = function(site) {
    return $scope.disabledIcon;
  }

  var popupGenerator = function(site, marker) {
    return genericPopup(site.fields.nume,marker,[selectMarkerButton(marker)])
  }

  pointsSrv.getUnclustered(function(layer) {
    $scope.map.addLayer(layer);
  }, {}, titleGenerator, iconGenerator, popupGenerator);


  $scope.map.on('click', function(e) {
    if ($scope.pageMode == 'start') {
      if ($scope.startMarker == null) {
        $scope.startMarker = new L.marker(e.latlng).addTo($scope.map);
        $scope.startMarker.setIcon($scope.startIcon);
      } else {
        $scope.startMarker.setLatLng(e.latlng);
      }
    } else if ($scope.pageMode == 'end') {
      if ($scope.endMarker == null) {
        $scope.endMarker = new L.marker(e.latlng).addTo($scope.map);
        $scope.endMarker.setIcon($scope.endIcon);
      } else {
        $scope.endMarker.setLatLng(e.latlng);
      }
    } else if ($scope.pageMode == 'way') {
      var marker = new L.marker(e.latlng).addTo($scope.map);
      marker.setIcon($scope.waypointIcon);
      marker.bindPopup(genericPopup("",marker,[removeMarkerButton(marker)]));
      $scope.waypointMarkers.push(marker);
      $scope.addedMarkers.push(marker);
    }
  });

  $scope.setPageMode = function(mode) {
    $scope.pageMode = mode;
  }

  $scope.updatePageMode = function()  {
    window.scrollTo(0,document.body.scrollHeight);
  }

  $scope.updateDays = function(numDays) {
    $scope.$broadcast('daysUpdated',numDays);
  }

  $scope.setActiveWaypoint = function(index) {
    console.log("way"+index);
    var coords = $scope.stops[index];
    var latlng = L.latLng(coords.lat, coords.lon);
    if ($scope.activeMarker == null) {
      $scope.activeMarker = new L.marker(latlng).addTo($scope.map);
      $scope.activeMarker.setIcon($scope.activeIcon);
    }
    else {
      $scope.activeMarker.setLatLng(latlng);
    }

  }

}]);
