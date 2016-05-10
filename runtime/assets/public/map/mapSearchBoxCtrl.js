app.regCtrl('mapSearchBoxCtrl', ["$scope", "$http", function($scope, $http) {
  var vm = new Object();
  $scope.vm = vm;
  vm.empty = {
    categorie: undefined,
  };
  vm.site = JSON.parse(JSON.stringify(vm.empty));
  vm.siteFields = $scope.mapSearchFields;

  $scope.search = function() {
      var query = [];
      for (var key in vm.site) {
        value = vm.site[key];
        if (value && value.length > 0)
         query.push({"field":key,"value":value});
      }

      $scope.update_points({"multiQuery":query});
      vm.site = JSON.parse(JSON.stringify(vm.empty));
  }

}]);
