package com.github.usethevoid.mapsites.api;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.usethevoid.mapsites.AppFlags;
import com.github.usethevoid.mapsites.auth.AccessToken;
import com.github.usethevoid.mapsites.auth.TokenAuthenticator;
import com.github.usethevoid.mapsites.auth.TokenHelper;
import com.github.usethevoid.mapsites.auth.User;
import com.github.usethevoid.mapsites.model.ClientSession;
import com.github.usethevoid.mapsites.model.FrontendConfig;
import com.google.common.base.Optional;

import io.dropwizard.auth.AuthenticationException;

@Path("/reauth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReauthResource {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final TokenHelper tokenHelper;
	private final TokenAuthenticator authenticator;
	
	public ReauthResource(TokenHelper tokenHelper,TokenAuthenticator authenticator) {
		this.tokenHelper = tokenHelper;
		this.authenticator = authenticator;
	}
	
	@GET
	public ClientSession update(@Context HttpServletResponse response,@Context HttpServletRequest request) {
		
		Optional<User> user;
		try {
			user = authenticator.authenticate(TokenHelper.fromHttpRequest(request));
		} catch (AuthenticationException e) {
			logger.error(e.getMessage());
			throw new NotAuthorizedException(response);
		}
		int secondsValid = 0;
		String username = null;
		if(user.isPresent()) {
			Date created = new Date();
			secondsValid = 60;
			Date expire = DateUtils.addHours(created, secondsValid);
			AccessToken token = tokenHelper.create(user.get(),expire);
			tokenHelper.decorateHttpResponse(response, token, secondsValid,false);
			username = user.get().getName();
		}
		return new ClientSession(username,secondsValid);
	}

}
