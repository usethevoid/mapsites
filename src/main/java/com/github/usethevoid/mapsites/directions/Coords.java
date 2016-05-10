package com.github.usethevoid.mapsites.directions;

import org.postgresql.geometric.PGpoint;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Coords {
	@JsonProperty
	public double lat;
	
	@JsonProperty
	public double lon;
	
	public Coords() {}
	
	public Coords(PGpoint p) {
		lat = p.y;
		lon = p.x;
	}
	
	public Coords(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}
	
	public static double lat(double[] p) {
		return p[0];
	}
	
	public static double lon(double[] p) {
		return p[1];
	}
	
	public static double x(double[] p) {
		return p[1];
	}
	
	public static double y(double[] p) {
		return p[0];
	}
	
	public static double[] latLon(double lat,double lon) {
		return new double[] {lat,lon};
	}
}
