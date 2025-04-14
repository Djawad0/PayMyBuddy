package com.PayMyBuddy.PayMyBuddy.configuration;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.PayMyBuddy.PayMyBuddy.model.User;
import com.PayMyBuddy.PayMyBuddy.repository.DBUserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	 @Autowired
	    private DBUserRepository dbUserRepository;

	    @Override
	    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
	    	Optional<User> userOpt = dbUserRepository.findByEmail(email);
	    	
	    	User user = userOpt.orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec le nom d'utilisateur : " + email));
	        
	        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), getGrantedAuthorities(user.getRole()));
	    }

	    private List<GrantedAuthority> getGrantedAuthorities(String role) {       
	        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
	    }
	    
	    public User getAuthenticatedUser() {
	 
	        String email = SecurityContextHolder.getContext().getAuthentication().getName();
	        Optional<User> userOpt = dbUserRepository.findByEmail(email);


	        return userOpt.orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email : " + email));
	    }
	
}
