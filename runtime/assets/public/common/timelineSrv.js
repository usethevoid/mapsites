app.service('timelineSrv', function() {
  var self = this;
  var RAD = 10;
  var UNSELECTED = "#fff";
  var SELECTED = "#5cf";
  var ACTIVE = "#f31";


  self.groups = {};
  self.circles = {};
  self.active = null;
  self.count;

  this._toggleSelect = function(id,onSelected) {
       if (self.groups[id].selected) {
              self.groups[id].selected = false;
              self.circles[id].attr("fill", ACTIVE);
            } else {
              self.groups[id].selected = true;
              self.circles[id].attr("fill", SELECTED);
            }
      onSelected();
  }

  this._onClick = function(id,onActive,onSelected) {
            if (!self.active) {
              self.circles[id].attr("fill", ACTIVE);
            }
            else {
                // if is already active
                if (self.active == id)
                    self._toggleSelect(id,onSelected); //only toggle select
                else {
                    // reset previously active to selection state
                    if(self.groups[self.active].selected)
                        self.circles[self.active].attr("fill", SELECTED);
                    else
                        self.circles[self.active].attr("fill", UNSELECTED);

                    self.circles[id].attr("fill", ACTIVE);
                }
            }
            self.active = id;
            onActive(id);
  }

  this.createTimeline = function(paper, w, h, count, onActive, onSelected) {
    var OFFX = 0.5* w / (count+1);
    var Y = h / 2;
    var px = 0;
    self.count = count;

    for (var i = 0; i < count; ++i) {
      (function(i) {
        var group = paper.set();
        group.id = i;
        self.groups[i] = group;

        var x = OFFX + i * w / (count+1);

        var circle = paper.circle(x, Y, RAD);
        self.circles[i] = circle;
        circle.attr("fill", UNSELECTED);
        circle.attr("stroke", "#5cf");
        circle.attr("stroke-width", 3);
        var label = paper.text(x, Y, "")

        group.push(circle);
        group.push(label);
        group.attr({
          cursor: 'pointer',
        });

        if (i > 0) {
          group.mouseup(function(e) {
            self._onClick(i,onActive,onSelected);
          });

          var line = paper.path(["M", px + RAD, Y, "L", x - RAD, Y]);
          line.attr("stroke", "#5cf");
          line.attr("stroke-width", 3);
        }
        px = x;
      })(i);
    }
  }

  this.getSelected = function() {
    var selected  = [];
    for(var i = 0; i < self.count; ++i)
      if(self.groups[i].selected)
        selected.push(i);
    return selected;
  }


});
