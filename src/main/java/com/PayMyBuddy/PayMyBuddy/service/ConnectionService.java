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


	/**
	 * This method allows a user to add another user as a friend.
	 * @param email holds the email of the user to be added as a friend.
	 * @return success or error message.
	 */

	public ResponseEntity<String> createConnection(String email) {

		try {
			User user = customUserDetailsService.getAuthenticatedUser();


			log.info("Attempt to add {} as a friend by {}", email, user.getEmail());


			if (user.getEmail().equals(email)) {
				log.error("Add friend failed: Attempt to add oneself by {}", user.getEmail());
				return ResponseEntity.badRequest().body("You cannot add yourself as a friend.");
			}


			User friend = dbUserRepository.findByEmail(email)
					.orElseThrow(() -> {
						log.error("Add friend failed: User not found with email {}", email);
						return new RuntimeException("User not found : " + email);
					});


			ConnectionId connectionId = new ConnectionId(user.getId(), friend.getId());

			if (dbConnectionRepository.existsById(connectionId)) {
				log.warn("Add friend ignored: {} already added {}", user.getEmail(), friend.getEmail());
				return ResponseEntity.badRequest().body("You have already added this user.");
			}


			Connection connection = new Connection();
			connection.setId(connectionId);
			connection.setUser1(user);
			connection.setUser2(friend);

			dbConnectionRepository.save(connection);

			log.info("Connection successfully added: {} → {}", user.getEmail(), friend.getEmail());
			return ResponseEntity.status(HttpStatus.CREATED).body("Friend successfully added.");

		} catch (RuntimeException e) {
			log.error("Error while adding friend: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while adding friend: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while adding the friend.");
		}

	}

}
