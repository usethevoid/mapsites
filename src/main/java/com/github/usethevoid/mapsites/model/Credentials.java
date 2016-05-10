package com.github.usethevoid.mapsites.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Credentials {
	@JsonProperty
	String username;
	@JsonProperty
	String password;

	public String getUsername()
	{
		return username;
	}
	
	public String getPassword()
	{
		return password;
	}
}
