app.regCtrl('galleryUplCtrl', ["$scope", "$http", "Upload", function($scope, $http, Upload) {
  $scope.images = [];
  $scope.uplType = 0;
  $scope.file = null;
  $scope.imgType = "";
  $scope.imgTypes = $scope.imageTypes ? $scope.imageTypes : [];

  $scope.set_type = function(type) {
    $scope.imgType = type;
  }

  $scope.onImageUploaded = function() {
      $scope.updateImages();
  }

  // upload on file select or drop
  $scope.upload = function(file) {
    if (file == null) return;
    Upload.upload({
      url: "/api/private/siteimages/" + $scope.siteId,
      data: {
        file: file,
        imgType: $scope.uplType
      }
    }).then(function(resp) {
      console.log('Success ' + resp.config.data.file.name + 'uploaded. Response: ' + resp.data);
      $scope.updateImages();
    }, function(resp) {
      console.log('Error status: ' + resp.status);
    }, function(evt) {
      var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
    });
  };


  $scope.updateImages = function() {
    if (!$scope.is_created())
      return;
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

  $scope.updateImages();

}]);
