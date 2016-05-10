package com.github.usethevoid.mapsites.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.usethevoid.mapsites.auth.AccessToken;
import com.github.usethevoid.mapsites.auth.TokenHelper;
import com.github.usethevoid.mapsites.auth.User;
import com.google.common.base.Optional;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

public class TokenAuthFilter implements Filter {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Authenticator<AccessToken, User> auth;
	
	public TokenAuthFilter(Authenticator<AccessToken, User> auth)  {
		this.auth = auth;
	}
	
	public void init(FilterConfig filterConfig) {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		logger.info("Auth access: {}", httpRequest.getRequestURL());
		
		AccessToken token = TokenHelper.fromHttpRequest(httpRequest);
		
		Optional<User> user = null;
		try {
			user = auth.authenticate(token);
			if (user.isPresent()) 
				chain.doFilter(request, response);
			else 
				httpResponse.sendError(401);
		} catch (AuthenticationException e) {
			httpResponse.sendError(403);
		}
		
	}

	public void destroy() {
	}
}
