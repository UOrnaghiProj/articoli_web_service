package com.xantrix.webapp.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

public class AuthEntryPoint extends BasicAuthenticationEntryPoint {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthEntryPoint.class);
	
	private static String REALM = "REAME";
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException{
		
		String Errmsg ="User e o password non corrette!";
		
		logger.warn("Errore Sicurezza: " + authException.getMessage());
		
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setHeader("WWW-Authenticate", "Basic realm=" + getRealmName() + "");
		
		PrintWriter writer = response.getWriter();
		writer.println(Errmsg);
		
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		setRealmName(REALM);
		super.afterPropertiesSet();
	}

}
