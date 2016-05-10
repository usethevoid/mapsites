package com.github.usethevoid.mapsites.auth;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.util.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;

import com.github.usethevoid.mapsites.model.Credentials;
import com.google.common.base.Optional;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

public class BasicAuthenticator implements Authenticator<Credentials, User> {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Factory<SecurityManager> factory;
	private final SecurityManager securityManager;

	public BasicAuthenticator() {
		this.factory = new IniSecurityManagerFactory("classpath:shiro.ini");
		this.securityManager = factory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
		
	}

	@Override
	public Optional<User> authenticate(Credentials credentials) throws AuthenticationException {
		Subject currentUser = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken(credentials.getUsername(), credentials.getPassword());
		User user = null;
		try {
			currentUser.login(token);
			user = new User((String) currentUser.getPrincipal());
			currentUser.logout();
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return Optional.absent();
		}
		return Optional.of(user);
	}

}
