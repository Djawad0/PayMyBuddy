package com.PayMyBuddy.PayMyBuddy.service;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.PayMyBuddy.PayMyBuddy.configuration.CustomUserDetailsService;
import com.PayMyBuddy.PayMyBuddy.model.Connection;
import com.PayMyBuddy.PayMyBuddy.model.ConnectionId;
import com.PayMyBuddy.PayMyBuddy.model.User;
import com.PayMyBuddy.PayMyBuddy.repository.DBConnectionRepository;
import com.PayMyBuddy.PayMyBuddy.repository.DBUserRepository;
import lombok.AllArgsConstructor;


@AllArgsConstructor
@Service
public class ConnectionService {
	
	private static final Logger log = LogManager.getLogger(ConnectionService.class);

	private final DBConnectionRepository dbConnectionRepository;
	private final DBUserRepository dbUserRepository;
	private final CustomUserDetailsService customUserDetailsService;
	
	
	public ResponseEntity<String> createConnection(String email) {
		
		try {
	        User user = customUserDetailsService.getAuthenticatedUser();

	       
	        log.info("Tentative d'ajout en ami de {} par {}", email, user.getEmail());

	       
	        if (user.getEmail().equals(email)) {
	            log.error("Ajout ami échoué : Tentative d'ajout de soi-même par {}", user.getEmail());
	            return ResponseEntity.badRequest().body("Vous ne pouvez pas vous ajouter comme ami.");
	        }

	     
	        User friend = dbUserRepository.findByEmail(email)
	                .orElseThrow(() -> {
	                    log.error("Ajout ami échoué : Utilisateur non trouvé avec email {}", email);
	                    return new RuntimeException("Utilisateur non trouvé : " + email);
	                });

	       
	        ConnectionId connectionId = new ConnectionId(user.getId(), friend.getId());

	        if (dbConnectionRepository.existsById(connectionId)) {
	            log.warn("Ajout ami ignoré : {} a déjà ajouté {}", user.getEmail(), friend.getEmail());
	            return ResponseEntity.badRequest().body("Vous avez déjà ajouté cet utilisateur.");
	        }

	        
	        Connection connection = new Connection();
	        connection.setId(connectionId);
	        connection.setUser1(user);
	        connection.setUser2(friend);

	        dbConnectionRepository.save(connection);

	        log.info("Connexion ajoutée avec succès : {} → {}", user.getEmail(), friend.getEmail());
	        return ResponseEntity.status(HttpStatus.CREATED).body("Ami ajouté avec succès.");

	    } catch (RuntimeException e) {
	        log.error("Erreur lors de l'ajout d'ami : {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	    } catch (Exception e) {
	        log.error("Erreur inattendue lors de l'ajout d'ami : {}", e.getMessage(), e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Une erreur est survenue lors de l'ajout de l'ami.");
	    }
		
	}
	
}
