app.service('routingSrv', ['$http', function($http) {
  var self = this;
  self.route = null;
  self.stops = null;

  var marker2coords = function(m) {
    var latlng = m.getLatLng();
    return {
      lat: latlng.lat,
      lon: latlng.lng
    };
  }

  this.getStops = function() {
    if (self.stops)
      return self.stops;

    var segments = self.route.segments;
    var stops = [];
    for (var i = 0; i < segments.length; ++i)
      stops[i] = segments[i].start;
    stops.push(segments[segments.length - 1].end);

    self.stops = stops;
    return stops;
  }

  this.getLength = function() {
    return {
      kms: self.route.kms,
      hours: self.route.hours,
      minutes: self.route.minutes
    };
  }

  this.getStopCount = function() {
    if (self.stops)
      return self.stops.length;
    return 0;
  }

  this.getGeoJson = function() {
    return JSON.parse(self.route.geoJson);
  }


  this.update = function(resCb, startMarker, endMarker, waypointMarkers) {
    self.stops = null;

    console.log("routing");
    var waypoints = [];
    for (var i = 0; i < waypointMarkers.length; ++i)
      if (waypointMarkers[i])
        waypoints.push(marker2coords(waypointMarkers[i]));

    var routeRequest = {
      start: marker2coords(startMarker),
      end: marker2coords(endMarker),
      waypoints: waypoints
    };
    $http.post('/api/route', routeRequest).then(function successCallback(response) {
      self.route = response.data;
      resCb(self.getGeoJson(),self.getLength());
    }, function errorCallback(response) {
      console.log(response);
    });
  }

}]);
