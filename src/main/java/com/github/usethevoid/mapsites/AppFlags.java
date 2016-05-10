package com.github.usethevoid.mapsites;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AppFlags {
	@JsonProperty
	boolean routingEnabled;
	@JsonProperty
	boolean areasEnabled;
	
	public AppFlags() {
		routingEnabled = false;
		areasEnabled = false;
	}
}