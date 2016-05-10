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
