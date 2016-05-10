package com.github.usethevoid.mapsites.model;

import java.text.DecimalFormat;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.usethevoid.mapsites.directions.Coords;
import com.github.usethevoid.mapsites.directions.MapRoute.RouteSegment;

public class Route {
	static DecimalFormat df = new DecimalFormat("#.#");
	
	@JsonProperty
	String geoJson;
	
	@JsonProperty
	String kms;
	
	@JsonProperty
	int hours;
	
	@JsonProperty
	int minutes;
	
	@JsonProperty
	List<RouteSegment> segments;
	
	public Route(List<RouteSegment> segments ,String geoJson, double kms, int hours, int minutes) {
		this.segments = segments;
		this.geoJson = geoJson;
		this.kms = df.format(kms);
		this.hours = hours;
		this.minutes = minutes;
	}
}
