package com.github.usethevoid.mapsites.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SiteOp {
	public final static String SAVED = "saved";
	public final static String DELETED = "deleted";
	
	@JsonProperty
	public String op;
	
	@JsonProperty
	public String time;
	
	@JsonProperty
	public Integer id;
	
	public SiteOp(String op, int id) {
		this.op = op;
		this.id = id;
	}
}
