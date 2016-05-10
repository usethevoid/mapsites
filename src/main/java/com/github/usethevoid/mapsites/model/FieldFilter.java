package com.github.usethevoid.mapsites.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FieldFilter {
	@JsonProperty
	public String field;
	@JsonProperty
	public String value;
	
	@JsonCreator
	public FieldFilter() {}
	
	public FieldFilter(String field, String value) {
		this.field = field;
		this.value = value;
	}
}
