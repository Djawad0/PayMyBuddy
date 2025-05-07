package com.PayMyBuddy.PayMyBuddy.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@Autowired
	private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		http

		.csrf(AbstractHttpConfigurer::disable)
		.authorizeHttpRequests(auth -> {
			auth.requestMatchers("/inscription", "/login").permitAll();
			auth.requestMatchers("/css/**", "/js/**", "/images/**").permitAll();
			auth.requestMatchers("/admin/**").hasRole("ADMIN");
			auth.requestMatchers("/user/**").hasAnyRole("USER", "ADMIN");
			auth.anyRequest().authenticated();
		})
		.formLogin(form -> form
				.loginPage("/login")
				.successHandler(customAuthenticationSuccessHandler)
				.failureUrl("/login?error=true")
				.permitAll()
				)
		.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login?logout")
				.invalidateHttpSession(true) 
				.deleteCookies("JSESSIONID")
				.permitAll()
				)
		.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
		.authenticationProvider(authenticationProvider());

		return http.build();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(customUserDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

}
