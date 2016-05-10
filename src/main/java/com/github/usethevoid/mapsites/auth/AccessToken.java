package com.github.usethevoid.mapsites.auth;

public class AccessToken {
	private final String bearerToken;
	private final String csrfToken;

	public AccessToken(String csrfToken, String bearerToken) {
		this.csrfToken = csrfToken;
		this.bearerToken = bearerToken;
	}

	public String getCsrfToken() {
		return csrfToken;
	}
	
	public String getBearerToken() {
		return bearerToken;
	}
}
