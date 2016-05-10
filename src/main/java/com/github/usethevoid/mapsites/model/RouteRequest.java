package com.github.usethevoid.mapsites.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.usethevoid.mapsites.directions.Coords;

public class RouteRequest {
	@JsonProperty
	public Coords start;
	
	@JsonProperty
	public Coords end;
	
	@JsonProperty
	public List<Coords> waypoints;
}
