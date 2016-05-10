package com.github.usethevoid.mapsites.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/private/secret")
@Produces(MediaType.APPLICATION_JSON)
public class SecretResource {
	
	@GET
	public String tellSecret() {
		final String value = "Hello master";
		return value;
	}
}
