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
