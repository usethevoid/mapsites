app.service('pointsSrv',['$http',function($http){

  this.getUnclustered = function(resCb,filter,titleGenerator,iconGenerator,popupGenerator) {
    $http.post('/api/list', filter).then(function successCallback(response) {
      var markers = [];
      var sites = response.data.sites;
      for (var i = 0; i < sites.length; i++) {
        var s = sites[i];

        var coords = s.coords ? s.coords : {
          lat: 0,
          lon: 0
        };

        var marker = L.marker(L.latLng(coords.lat, coords.lon), {
          title: titleGenerator(s),
        });
        marker.site = s;
        marker.setIcon(iconGenerator(s));
        marker.bindPopup(popupGenerator(s,marker));
        markers.push(marker);
      }
      resCb(L.layerGroup(markers));
    }, function errorCallback(response) {
      console.log(response);
    });
  }

}]);
