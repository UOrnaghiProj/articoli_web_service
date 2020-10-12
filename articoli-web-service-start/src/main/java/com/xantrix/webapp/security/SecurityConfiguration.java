package com.xantrix.webapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	private static String REALM = "REAME";
	
	@Autowired
	@Qualifier("customUserDetailsService")
	private CustomUserDetailsService customUserDetailsService;
	
	@Bean
	public BCryptPasswordEncoder pswEnc() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Sistema di sicurezza scritto nel codice
	 */
	/*
	@Bean
	@Override
	public UserDetailsService userDetailsService() {
		
		UserBuilder users = User.builder();
		
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		
		manager.createUser(
				users
				.username("user")
				.password(new BCryptPasswordEncoder().encode("password"))
				.roles("USER")
				.build());
		
		manager.createUser(
				users
				.username("admin")
				.password(new BCryptPasswordEncoder().encode("admin"))
				.roles("USER","ADMIN")
				.build());
		
		return manager;	
	}
	*/
	
	private static final String[] USER_MATCHER = {"/api/articoli/cerca/**"};
	private static final String[] ADMIN_MATCHER = {"/api/articoli/inserisci/**",
													"/api/articoli/modifica/**",
													"/api/articoli/elimina/**"};
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf()
		.disable()
		.authorizeRequests()
		.antMatchers(USER_MATCHER).hasRole("USER")
		.antMatchers(ADMIN_MATCHER).hasRole("ADMIN")
		.anyRequest().authenticated()
		.and()
		.httpBasic().realmName(REALM).authenticationEntryPoint(getBasicAuthEntryPoint())
		.and()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
	}
	
	@Bean
	public AuthEntryPoint getBasicAuthEntryPoint() {
		return new AuthEntryPoint();
	}
	
	/**
	 * Per far funzionare il webservice con i FE come Angular
	 */
	@Override
	public void configure(WebSecurity web) throws Exception{
		web.ignoring().antMatchers(HttpMethod.OPTIONS,"/**");
	}
	
	/**
	 * @Autowired ci indica che questo metodo di configurazione verrà chiamato dopo 
	 * l'istanziamento del AuthenticationManagerBuilder e ne definisce gli 
	 * userDetailsService utilizzando quelli custom 
	 * @param auth
	 * @throws Exception
	 */
	@Autowired
	public void configuredGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.userDetailsService(customUserDetailsService)
			.passwordEncoder(new BCryptPasswordEncoder());
	}
	
}
