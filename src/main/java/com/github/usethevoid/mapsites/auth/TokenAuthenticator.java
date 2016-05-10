package com.github.usethevoid.mapsites.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

public class TokenAuthenticator implements Authenticator<AccessToken, User> {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final TokenHelper tokenHelper;
	private final boolean checkCsrf;
	
    public TokenAuthenticator(TokenHelper tokenHelper,boolean checkCsrf) {
		this.tokenHelper = tokenHelper;
		this.checkCsrf = checkCsrf;
	}

	@Override
    public Optional<User> authenticate(AccessToken token) throws AuthenticationException {
		logger.info("Checking token");
    	return Optional.fromNullable(tokenHelper.auth(token,checkCsrf));
    }
}