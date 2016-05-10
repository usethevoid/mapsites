package com.github.usethevoid.mapsites.auth;

import java.security.Key;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

public class TokenHelper {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	final String CSRF_CLAIM = "xsrfToken";

	// TODO from config
	final String path = "/";
	final String domain;
	final Key signingKey;

	public TokenHelper(String domain) {
		signingKey = MacProvider.generateKey();
		this.domain = domain;
	}

	public AccessToken create(User user, Date expire) {
		String jwt = Jwts.builder().signWith(SignatureAlgorithm.HS512, signingKey).setSubject(user.getName())
				.setExpiration(expire).setIssuer(domain).claim(CSRF_CLAIM, user.getSessionId()).compact();
		return new AccessToken(user.getSessionId(), jwt);
	}

	public User auth(AccessToken token, boolean checkCsrf) {
		try {
			Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token.getBearerToken()).getBody();
			if (checkCsrf && !token.getCsrfToken().equals(claims.get(CSRF_CLAIM, String.class))) {
				logger.error("claims {} has {}", claims.get(CSRF_CLAIM, String.class), token.getCsrfToken());
				return null;
			}
			return new User(claims.getSubject(), claims.get(CSRF_CLAIM, String.class));
		} catch (Exception ex) {
			logger.error("JWT auth failed");
			return null;
		}

	}

	static private String getBearerToken(Cookie[] cookies) {
		if (cookies == null)
			return null;
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("access_token")) {
				return cookie.getValue();
			}
		}
		return null;
	}

	static public AccessToken fromHttpRequest(HttpServletRequest httpRequest) {
		String bearerToken = getBearerToken(httpRequest.getCookies());
		String csrfToken = httpRequest.getHeader("X-XSRF-TOKEN");
		if (bearerToken == null) {
			return null;
		}
		return new AccessToken(csrfToken, bearerToken);
	}

	public void decorateHttpResponse(HttpServletResponse response, AccessToken token, double hoursValid,
			boolean addCsrf) {
		Cookie tokenCookie = new Cookie("access_token", token.getBearerToken());
		tokenCookie.setHttpOnly(true);
		tokenCookie.setMaxAge((int) (3600 * hoursValid));
		tokenCookie.setPath(path);
		response.addCookie(tokenCookie);

		if (addCsrf) {
			Cookie xsrfCookie = new Cookie("XSRF-TOKEN", token.getCsrfToken());
			xsrfCookie.setDomain(domain);
			xsrfCookie.setMaxAge((int) (3600 * hoursValid));
			xsrfCookie.setPath(path);
			response.addCookie(xsrfCookie);
		}
	}
}
