package com.PayMyBuddy.PayMyBuddy.service;

import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
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
    
    public ResponseEntity<String> inscription(User user) {   
    	
    	try {
            if (!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
                log.error("Inscription échouée : Email invalide {}", user.getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Votre mail est invalide");
            }

            Optional<User> userEmail = userRepository.findByEmail(user.getEmail());
            if (userEmail.isPresent()) {
                log.error("Inscription échouée : Email déjà utilisé {}", user.getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Votre mail est déjà utilisé");
            }
            
            Optional<User> userUsername = userRepository.findByUsername(user.getUsername());
            
            if (userUsername.isPresent()) {
                log.error("Inscription échouée : Nom d'utilisateur déjà utilisé {}", user.getUsername());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Votre nom d'utilisateur est déjà utilisé");
            }
            
            String password = user.getPassword();
            String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$";

            if (password == null || !password.matches(passwordPattern)) {
                log.error("Inscription échouée : mot de passe non conforme pour {}", user.getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Le mot de passe doit contenir au moins 8 caractères, dont une majuscule, une minuscule, un chiffre et un caractère spécial");
            }
            

            user.setRole("USER");
            user.setBalance(0.0);       
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
    		 User user = userRepository.findByEmail(updateRequest.getOriginalEmail())
    			        .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email : " + updateRequest.getOriginalEmail()));
  
            if (!passwordEncoder.matches(updateRequest.getOldPassword(), user.getPassword())) {
                log.error("Modification échouée : Ancien mot de passe incorrect pour {}", user.getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ancien mot de passe incorrect");
            }

            if (updateRequest.getUsername() != null && !updateRequest.getUsername().isBlank() && !updateRequest.getUsername().equals(user.getUsername())) {
            	Optional<User> userUsername = userRepository.findByUsername(updateRequest.getUsername());
                
                if (userUsername.isPresent()) {
                    log.error("Modification échouée : Nom d'utilisateur déjà utilisé {}", updateRequest.getUsername());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Votre nom d'utilisateur est déjà utilisé");
                }
                else {
                user.setUsername(updateRequest.getUsername());
                }
                
                }
                

            if (updateRequest.getEmail() != null && !updateRequest.getEmail().isBlank() && !updateRequest.getEmail().equals(updateRequest.getOriginalEmail())) {   
            	 Optional<User> userEmail = userRepository.findByEmail(updateRequest.getEmail());
                 if (userEmail.isPresent()) {
                     log.error("Modification échouée : Email déjà utilisé {}", updateRequest.getEmail());
                     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Votre mail est déjà utilisé");
                 }
                 else {
                user.setEmail(updateRequest.getEmail());
                 }
            }

            if (updateRequest.getPassword() != null && !updateRequest.getPassword().isBlank()) {
            	 String password = updateRequest.getPassword();
                 String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$";

                 if (!password.matches(passwordPattern)) {
                     log.error("Modification échouée : mot de passe non conforme pour {}", user.getEmail());
                     return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                         .body("Le mot de passe doit contenir au moins 8 caractères, dont une majuscule, une minuscule, un chiffre et un caractère spécial");
                 }
                 
                 else {
                user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
                 }
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
    
    
    public ResponseEntity<String> deleteUserByEmail(String email) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(email);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Utilisateur introuvable avec l'email : " + email);
            }

            userRepository.delete(userOpt.get());

            log.info("Utilisateur supprimé par un administrateur : {}", email);
            return ResponseEntity.ok("Utilisateur supprimé avec succès.");

        } catch (Exception e) {
            log.error("Erreur lors de la suppression de l'utilisateur : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la suppression du compte utilisateur.");
        }
    }
    
	
}
