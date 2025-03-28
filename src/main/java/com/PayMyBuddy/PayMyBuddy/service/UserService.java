package com.PayMyBuddy.PayMyBuddy.service;


import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.PayMyBuddy.PayMyBuddy.model.User;
import com.PayMyBuddy.PayMyBuddy.repository.DBUserRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserService {

    private DBUserRepository userRepository;
	private BCryptPasswordEncoder passwordEncoder;
    
    public void inscription(User user) {   
    	
    	if(!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
    		throw new RuntimeException("Votre mail est invalide");
    	}
    	
    	Optional<User> userOptional = this.userRepository.findByEmail(user.getEmail());
    	if(userOptional.isPresent()) {
    		throw new RuntimeException("Votre mail est déjà utilisé");
    	}
    	
    	user.setRole("USER");
    	
    	String mdp = this.passwordEncoder.encode(user.getPassword());
    	user.setPassword(mdp);
    	
        this.userRepository.save(user);
    }
    
    
	
}
