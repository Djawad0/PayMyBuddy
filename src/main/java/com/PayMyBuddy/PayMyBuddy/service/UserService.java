package com.PayMyBuddy.PayMyBuddy.service;

import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.PayMyBuddy.PayMyBuddy.dto.UpdateUserRequest;
import com.PayMyBuddy.PayMyBuddy.model.User;
import com.PayMyBuddy.PayMyBuddy.repository.DBUserRepository;
import lombok.AllArgsConstructor;
import java.util.NoSuchElementException;

@AllArgsConstructor
@Service
public class UserService {


	private static final Logger log = LogManager.getLogger(UserService.class);
	private DBUserRepository userRepository;
	private BCryptPasswordEncoder passwordEncoder;

	/**
	 * This method registers a new user into the application.
	 * @param user contains the data sent by the user after filling all fields (Username, Email, Password).
	 * @return an error or success message.
	 */

	public String inscription(User user) {   

			if (!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
				log.error("Registration failed: Invalid email {}", user.getEmail());
				throw new IllegalArgumentException("Invalid email address");
			}

			Optional<User> userEmail = userRepository.findByEmail(user.getEmail());
			if (userEmail.isPresent()) {
				log.error("Registration failed: Email already in use {}", user.getEmail());
				throw new IllegalStateException("Email is already in use");
			}

			Optional<User> userUsername = userRepository.findByUsername(user.getUsername());

			if (userUsername.isPresent()) {
				log.error("Registration failed: Username already in use {}", user.getUsername());
				throw new IllegalStateException("Username is already in use");
			}

			String password = user.getPassword();
			String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$";

			if (password == null || !password.matches(passwordPattern)) {
				log.error("Registration failed: Password does not meet requirements for {}", user.getEmail());
				throw new IllegalArgumentException("Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a digit, and a special character");
			}


			user.setRole("USER");
			user.setBalance(0.0);       
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			userRepository.save(user);

			log.info("New user successfully registered : {}", user.getEmail());
			 return "Registration successful";

	}

	/**
	 * This method updates user data as requested (Username, Email, Password).
	 * @param updateRequest DTO containing updated user data (Username, Email, Password).
	 * @return an error or success message.
	 */

	public String updateUser( UpdateUserRequest updateRequest) {

			User user = userRepository.findByEmail(updateRequest.getOriginalEmail())
					.orElseThrow(() -> {
						log.error("Update failed: User not found with email {}", updateRequest.getOriginalEmail());
						return new NoSuchElementException("User not found with email : " + updateRequest.getOriginalEmail());
					});

			if (!passwordEncoder.matches(updateRequest.getOldPassword(), user.getPassword())) {
				log.error("Update failed: Incorrect old password for {}", user.getEmail());
				throw new SecurityException("Incorrect old password");
			}

			if (updateRequest.getUsername() != null && !updateRequest.getUsername().isBlank() && !updateRequest.getUsername().equals(user.getUsername())) {
				Optional<User> userUsername = userRepository.findByUsername(updateRequest.getUsername());

				if (userUsername.isPresent()) {
					log.error("Update failed: Username already in use {}", updateRequest.getUsername());
					throw new IllegalStateException("Username is already in use");
				}
				else {
					user.setUsername(updateRequest.getUsername());
				}

			}


			if (updateRequest.getEmail() != null && !updateRequest.getEmail().isBlank() && !updateRequest.getEmail().equals(updateRequest.getOriginalEmail())) {   
				Optional<User> userEmail = userRepository.findByEmail(updateRequest.getEmail());
				if (userEmail.isPresent()) {
					log.error("Update failed: Email already in use {}", updateRequest.getEmail());
					throw new IllegalStateException("Email is already in use");
				}
				else {
					user.setEmail(updateRequest.getEmail());
				}
			}

			if (updateRequest.getPassword() != null && !updateRequest.getPassword().isBlank()) {
				String password = updateRequest.getPassword();
				String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$";

				if (!password.matches(passwordPattern)) {
					log.error("Update failed: Password does not meet requirements for {}", user.getEmail());
					throw new IllegalArgumentException("Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a digit, and a special character");
				}

				else {
					user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
				}
			}

			userRepository.save(user);

			log.info("User successfully updated: {}", user.getEmail());
			return "User information updated successfully";

	}


	/**
	 * This method allows an admin to delete a user by their email address.
	 * @param email the email address of the user to delete.
	 * @return an error or success message.
	 */

	public String deleteUserByEmail(String email) {
			User user = userRepository.findByEmail(email)
					.orElseThrow(() -> {
						log.error("Deletion failed: User not found with email {}", email);
						return new NoSuchElementException("User not found with email : " + email);
					});

			userRepository.delete(user);

			log.info("User deleted by an administrator : {}", email);
			return "User successfully deleted";
		
	}


}
