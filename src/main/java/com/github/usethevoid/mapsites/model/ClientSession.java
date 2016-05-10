package com.github.usethevoid.mapsites.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientSession
{
	@JsonProperty
	String name;
	
	@JsonProperty
	Integer secondsValid;
	
	public ClientSession()
	{
		// Jackson deserialization
	}

	public ClientSession(String name, int secondsValid)
	{
		this.name = name;
		this.secondsValid = secondsValid;
	}
	
}
