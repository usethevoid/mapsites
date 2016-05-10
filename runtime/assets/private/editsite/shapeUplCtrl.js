app.regCtrl('shapeUplCtrl', ["$scope", "$http","Upload", function($scope, $http, Upload) {
  // upload on file select or drop
  $scope.file = null;

  $scope.loadTextCoords = function() {
    console.log("test");

  }

  $scope.upload_inner = function(file) {
    if (file == null) return;
    Upload.upload({
      url: "/api/private/shapeconv",
      data: {
        file: file,
      }
    }).then(function(resp) {
      console.log('Success ' + resp.config.data.file.name + 'uploaded. Response: ' + resp.data);
      $scope.onGeoJson('inner',resp.data);
    }, function(resp) {
      console.log('Error status: ' + resp.status);
    }, function(evt) {
      var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
    });
  };

}]);
