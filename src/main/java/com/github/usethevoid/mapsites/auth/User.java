package com.github.usethevoid.mapsites.auth;

import java.security.Principal;
import java.util.UUID;



public class User implements Principal {
	private final String name;
	private final String sessionId;
	
	public User(String name) {		
		this.name = name;
		this.sessionId = UUID.randomUUID().toString();	
	}
	public User(String name, String sessionId) {
		this.name = name;
		this.sessionId = sessionId;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public String getSessionId() {
		return sessionId;
	}

}
