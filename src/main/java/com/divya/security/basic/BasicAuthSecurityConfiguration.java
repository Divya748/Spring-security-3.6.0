package com.divya.security.basic;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.codec.Encoder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class BasicAuthSecurityConfiguration {
	
	

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(
						auth -> {
							auth
							.anyRequest().authenticated();
						});
		//to keep application stateless
//		http.sessionManagement(
//				session -> 
//					session.sessionCreationPolicy(
//							SessionCreationPolicy.STATELESS)
//				);
		http.formLogin();
		http.httpBasic();
		http.csrf().disable();
		// for h2 database
		http.headers().frameOptions().sameOrigin();
		return http.build();
	}
	
	
	//this is for storing user-details in-memory database
//	@Bean
//	public UserDetailsService userDetailsService() {
//		
//		var user =org.springframework.security.core.userdetails.User.withUsername("divya")
//			.password("{noop}divya123")
//			.roles("USER")
//			.build();
//		
//		var admin =org.springframework.security.core.userdetails.User.withUsername("admin")
//				.password("{noop}admin123")
//				.roles("ADMIN")
//				.build();
//		
//		return new InMemoryUserDetailsManager(user,admin);
//	}
	
	@Bean
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder()
				.setType(EmbeddedDatabaseType.H2)
				.addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
				.build();
	}
	
	@Bean
	public UserDetailsService userDetailsService(DataSource dataSource) {
		var user = org.springframework.security.core.userdetails.User.withUsername("divya")
						.password(passwordEncoder().encode("divya123"))
						//"{noop}divya123"
						.roles("USER")
						.build();

		var admin = org.springframework.security.core.userdetails.User.withUsername("admin")
				.password(passwordEncoder().encode("admin123"))
				.roles("ADMIN").build();
		
		var jdbc = new JdbcUserDetailsManager(dataSource);
		jdbc.createUser(user);
		jdbc.createUser(admin);
		
		return jdbc;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
