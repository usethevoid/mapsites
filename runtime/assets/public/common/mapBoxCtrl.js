app.controller('mapBoxCtrl', ["$scope", function($scope) {
  var bing_key = "LfO3DMI9S6GnXD7d0WGs~bq2DRVkmIAzSOFdodzZLvw~Arx8dclDxmZA0Y38tHIJlJfnMbGq5GXeYmrGOUIbS2VLFzRKCK0Yv_bAl6oe-DOc";
  var osm = L.tileLayer('http://otile1.mqcdn.com/tiles/1.0.0/osm/{z}/{x}/{y}.png', {
    attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors, &copy;'
    +'Tiles Courtesy of <a href="http://www.mapquest.com/" target="_blank">MapQuest</a> <img src="http://developer.mapquest.com/content/osm/mq_logo.png">'
  });
  var esri_imagery = new L.esri.basemapLayer('Imagery');
  var esri_labels = new L.esri.basemapLayer('ImageryLabels');


  var ggl = new L.Google('HYBRID');
  var ggl_clean = new L.Google();

  var bing = new L.BingLayer(bing_key, {
    type: 'AerialWithLabels'
  });
  var bing_clean = new L.BingLayer(bing_key, {
    type: 'Aerial'
  });

  var base_layers = {
    'Google topo': ggl,
    'Google': ggl_clean,
    'OSM': osm,
    'Bing topo': bing,
    'Bing': bing_clean,
    'Esri': esri_imagery
  };

  var overlays = {
    'Esri labels': esri_labels
  };
/*
  var areas =  L.tileLayer.wms("http://localhost:8090/geoserver/work/wms", {
    layers: 'work:sites',
    format: 'image/png',
    transparent: true,
    attribution: "Arheologis"
  });
*/
  //$scope.map.addLayer(areas);
  $scope.map.addLayer(osm);
  $scope.map.addControl(new L.Control.Layers(base_layers, overlays));
}]);
