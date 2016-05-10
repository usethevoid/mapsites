package com.github.usethevoid.mapsites.directions;

import java.util.List;

public interface Directions {
	public MapRoute pointsToRoute(Coords start,Coords end, List<Coords> points);
}
