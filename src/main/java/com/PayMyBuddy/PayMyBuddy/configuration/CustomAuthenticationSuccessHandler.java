package com.PayMyBuddy.PayMyBuddy.configuration;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
		for (GrantedAuthority authority : authentication.getAuthorities()) {
			if (authority.getAuthority().equals("ROLE_ADMIN")) {
				response.sendRedirect("/admin/dashboard"); 
				return;
			} else if (authority.getAuthority().equals("ROLE_USER")) {
				response.sendRedirect("/user/dashboard"); 
				return;
			}
		}
		response.sendRedirect("/user/dashboard"); 
	}

}
