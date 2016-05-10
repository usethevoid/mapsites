app.regCtrl('confirmDeleteCtrl', ["$scope", "$uibModalInstance", "onConfirm",
  function($scope, $uibModalInstance, onConfirm) {
    $scope.yes = function() {
      onConfirm();
      $uibModalInstance.close();
    }

    $scope.no = function() {
      $uibModalInstance.close();
    }

  }
]);

app.regCtrl('editSiteBoxCtrl', ["$scope", "$http", "$location","$uibModal", function($scope, $http, $location,$uibModal) {
  $scope.lastSave = 0;
  $scope.connWarning = false;

  function decorate_fields(fields) {
    return {
      "fields": fields
    }
  }

  $scope.getCurrentTime = function() {
    var time = (new Date()).toTimeString();
    var sep = time.indexOf(" ");
    return time.substring(0,sep);
  }

  function create() {
    $http({
      method: 'PUT',
      url: '/api/site',
      data: JSON.stringify(decorate_fields($scope.vm.site))
    }).then(function successCallback(response) {
      $scope.lastSave = $scope.getCurrentTime();
      var id = response.data.id;
      $location.path("mod/editsite/" + id);
      connWarning = false;
    }, function errorCallback(response) {
      connWarning = true;
    });
  }

  function save(id) {
    var delta = {};
    for (var field in $scope.vm.site) {
      var current = $scope.vm.site[field];
      var orig = $scope.origSite[field];
      if (current !== orig)
        delta[field] = current;
    }
    console.log(delta);

    $http({
      method: 'POST',
      url: '/api/private/site/' + id,
      data: JSON.stringify(decorate_fields(delta))
    }).then(function successCallback(response) {
      $scope.lastSave = $scope.getCurrentTime();
      connWarning = false;
    }, function errorCallback(response) {
      connWarning = true;
    });
  }

  $scope.save = function() {
    if ($scope.is_created()) {
      save($scope.siteId);
    } else {
      create();
    }
  }

  $scope._delete = function() {
    $http({
      method: 'DELETE',
      url: '/api/private/site/' + $scope.siteId
    }).then(function successCallback(response) {
      $location.path("/");
      connWarning = false;
    }, function errorCallback(response) {
      connWarning = true;
    });
  }

  $scope.delete = function() {
    var modalInstance = $uibModal.open({
      animation: true,
      templateUrl: 'private/fragments/confirmDelete.html',
      controller: 'confirmDeleteCtrl',
      size: "sm",
      resolve: {
        onConfirm: function() {
          return $scope._delete;
        }
      }
    });
  }

  $scope.update();
}]);

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
