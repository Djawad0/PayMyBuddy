package com.PayMyBuddy.PayMyBuddy.service;


import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.PayMyBuddy.PayMyBuddy.configuration.CustomUserDetailsService;
import com.PayMyBuddy.PayMyBuddy.dto.UpdateUserRequest;
import com.PayMyBuddy.PayMyBuddy.model.User;
import com.PayMyBuddy.PayMyBuddy.repository.DBUserRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserService {
	

	private static final Logger log = LogManager.getLogger(UserService.class);
    private DBUserRepository userRepository;
	private BCryptPasswordEncoder passwordEncoder;
	private final CustomUserDetailsService customUserDetailsService;
    
    public ResponseEntity<String> inscription(User user) {   
    	
    	try {
            if (!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
                log.error("Inscription échouée : Email invalide {}", user.getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Votre mail est invalide");
            }

            Optional<User> userOptional = userRepository.findByEmail(user.getEmail());
            if (userOptional.isPresent()) {
                log.error("Inscription échouée : Email déjà utilisé {}", user.getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Votre mail est déjà utilisé");
            }

            user.setRole("USER");
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            userRepository.save(user);

            log.info("Nouvel utilisateur inscrit avec succès : {}", user.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body("Inscription réussie");

        } catch (Exception e) {
            log.error("Erreur lors de l'inscription : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur est survenue lors de l'inscription");
        }
    }
    
    
    public ResponseEntity<String> updateUser( UpdateUserRequest updateRequest) {
    	
    	try {
            User authenticatedUser = customUserDetailsService.getAuthenticatedUser();

            User user = userRepository.findByEmail(authenticatedUser.getEmail())
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

            if (!passwordEncoder.matches(updateRequest.getOldPassword(), user.getPassword())) {
                log.error("Modification échouée : Ancien mot de passe incorrect pour {}", user.getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ancien mot de passe incorrect");
            }

            if (updateRequest.getUsername() != null && !updateRequest.getUsername().isBlank()) {
                user.setUsername(updateRequest.getUsername());
            }

            if (updateRequest.getEmail() != null && !updateRequest.getEmail().isBlank()) {
                user.setEmail(updateRequest.getEmail());
            }

            if (updateRequest.getPassword() != null && !updateRequest.getPassword().isBlank()) {
                user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
            }

            userRepository.save(user);

            log.info("Utilisateur mis à jour avec succès : {}", user.getEmail());
            return ResponseEntity.ok("Informations mises à jour avec succès");

        } catch (RuntimeException e) {
            log.error("Erreur de mise à jour utilisateur : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la mise à jour utilisateur : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur est survenue lors de la mise à jour");
        }
    }
    
    
	
}
