package com.xantrix.webapp.security;

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService{
	
	@Autowired
	private UserConfig config;

	private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		String errMsg = "";
		
		if(username == null || username.length() < 2) {
			errMsg = "Nome Utente assente o non valido";
			logger.warn(errMsg);
			throw new UsernameNotFoundException(errMsg);
		}
		
		Utenti utente = this.getHttpValue(username);
		
		if(utente == null) {
			errMsg = String.format("Utente %s non trovato", username);
			logger.warn(errMsg);
			throw new UsernameNotFoundException(errMsg);
		}
		
		UserBuilder builder = null;
		builder = org.springframework.security.core.userdetails.User.withUsername(utente.getUserId());
		builder.disabled((utente.getAttivo().equals("Si") ? false : true));
		builder.password(utente.getPassword());
		
		String[] profili = utente.getRuoli()
								.stream().map(item -> "ROLE_" + item).toArray(String[]::new);
		
		builder.authorities(profili);
		
		return builder.build();
	}
	
	private Utenti getHttpValue(String userId) {
		
		URI url = null;
		
		try {
			String srvUrl = config.getSrvUrl();
			url = new URI(srvUrl + userId);
			
		}catch(URISyntaxException e) {
			e.printStackTrace();
		}
		
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(config.getUserId(), config.getPassword()));
		
		Utenti utente = null;
		
		try {
			utente = restTemplate.getForObject(url, Utenti.class);
		}catch(Exception e) {
			
			String errMsg = String.format("connessione al servizio di autentificazione non riuscita");
			logger.warn(errMsg);
			
		}
		
		return utente;
	}

	
	
}
