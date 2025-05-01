package com.PayMyBuddy.PayMyBuddy.Service.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.PayMyBuddy.PayMyBuddy.dto.UpdateUserRequest;
import com.PayMyBuddy.PayMyBuddy.model.User;
import com.PayMyBuddy.PayMyBuddy.repository.DBUserRepository;
import com.PayMyBuddy.PayMyBuddy.service.UserService;

@SpringBootTest
public class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private User user;

	@Mock
	private BCryptPasswordEncoder passwordEncoder;

	@Mock
	private DBUserRepository userRepository;

	@Mock
	private UpdateUserRequest updateRequest;

	@Test
	void testInscriptionQuandEmailInvalide1() {

		when(user.getEmail()).thenReturn("testexemple.com");

		ResponseEntity<String> response = userService.inscription(user);

		assertEquals("Invalid email address", response.getBody());
	}

	@Test
	void testInscriptionQuandEmailInvalide2() {

		when(user.getEmail()).thenReturn("test@exemplecom");

		ResponseEntity<String> response = userService.inscription(user);

		assertEquals("Invalid email address", response.getBody());
	}

	@Test
	void testInscriptionQuandEmailEstDejaUtilise() {

		when(user.getEmail()).thenReturn("test@example.com");
		when(userRepository.findByEmail("test@example.com"))
		.thenReturn(Optional.of(user));

		ResponseEntity<String> response = userService.inscription(user);

		assertEquals("Email is already in use", response.getBody());
	}

	@Test
	void testInscriptionQuandUtilisateurEstDejaUtilise() {

		when(user.getEmail()).thenReturn("test@example.com");

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

		when(user.getUsername()).thenReturn("test");
		when(userRepository.findByUsername("test"))
		.thenReturn(Optional.of(user));

		ResponseEntity<String> response = userService.inscription(user);

		assertEquals("Username is already in use", response.getBody());
	}

	@Test
	void testInscriptionQuandMotDePasseNonConforme1() {

		when(user.getEmail()).thenReturn("test@example.com");

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

		when(user.getUsername()).thenReturn("test");
		when(userRepository.findByUsername("test")).thenReturn(Optional.empty());

		when(user.getPassword()).thenReturn(null);

		ResponseEntity<String> response = userService.inscription(user);

		assertEquals("Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a digit, and a special character",
				response.getBody());
	}

	@Test
	void testInscriptionQuandMotDePasseNonConforme2() {

		when(user.getEmail()).thenReturn("test@example.com");

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

		when(user.getUsername()).thenReturn("test");
		when(userRepository.findByUsername("test")).thenReturn(Optional.empty());

		when(user.getPassword()).thenReturn("1234");

		ResponseEntity<String> response = userService.inscription(user);

		assertEquals("Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a digit, and a special character",
				response.getBody());
	}


	@Test
	void testInscriptionErreur() {

		when(user.getEmail()).thenReturn(null);

		ResponseEntity<String> response = userService.inscription(user);

		assertEquals("An error occurred during registration",	response.getBody());
	}

	@Test
	void testInscriptionReussie() {

		when(user.getEmail()).thenReturn("test@example.com");

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

		when(user.getUsername()).thenReturn("test");
		when(userRepository.findByUsername("test")).thenReturn(Optional.empty());

		when(user.getPassword()).thenReturn("1234Ab//");

		when(passwordEncoder.encode("1234Ab//")).thenReturn("encodedPassword");

		ResponseEntity<String> response = userService.inscription(user);

		assertEquals("Registration successful",	response.getBody());
	}

	@Test
	void testUpdateUserUtilisateurNonTrouvé() {

		when(userRepository.findByEmail("test@example.com")).thenReturn(null);

		ResponseEntity<String> response = userService.updateUser(updateRequest);

		assertEquals("User not found with email : null",	response.getBody());
	}

	@Test
	void testUpdateUserAncienMotDePasseIncorrect() {

		UpdateUserRequest updateRequest = new UpdateUserRequest();
		updateRequest.setOriginalEmail("test@example.com");  

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

		when(passwordEncoder.matches("wrongOldPassword", "encodedOldPassword")).thenReturn(false);

		ResponseEntity<String> response = userService.updateUser(updateRequest);

		assertEquals("Incorrect old password", response.getBody());
	}

	@Test
	void testUpdateUserNomUtilisateurEstDejaUtilise() {

		UpdateUserRequest updateRequest = new UpdateUserRequest();
		updateRequest.setOriginalEmail("test@example.com");  
		updateRequest.setUsername("test");
		updateRequest.setOldPassword("oldPassword");

		User user = new User();
		user.setUsername("test1");
		user.setPassword("encodedPassword");

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

		when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);

		when(userRepository.findByUsername("test"))
		.thenReturn(Optional.of(user));

		ResponseEntity<String> response = userService.updateUser(updateRequest);

		assertEquals("Username is already in use", response.getBody());
	}

	@Test
	void testUpdateUserEmailEstDejaUtilise() {

		UpdateUserRequest updateRequest = new UpdateUserRequest();
		updateRequest.setOriginalEmail("test@example.com");  
		updateRequest.setUsername("test");
		updateRequest.setOldPassword("oldPassword");
		updateRequest.setEmail("test2@example.com");

		User user = new User();
		user.setUsername("test1");
		user.setPassword("encodedPassword");

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

		when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);

		when(userRepository.findByUsername("test"))
		.thenReturn(Optional.empty());

		when(userRepository.findByEmail("test2@example.com"))
		.thenReturn(Optional.of(user));

		ResponseEntity<String> response = userService.updateUser(updateRequest);

		assertEquals("Email is already in use", response.getBody());
	}

	@Test
	void testUpdateUserMotDePasseNonConforme() {

		UpdateUserRequest updateRequest = new UpdateUserRequest();
		updateRequest.setOriginalEmail("test@example.com");  
		updateRequest.setUsername("test");
		updateRequest.setOldPassword("oldPassword");
		updateRequest.setEmail("test2@example.com");
		updateRequest.setPassword("1234");

		User user = new User();
		user.setUsername("test1");
		user.setPassword("encodedPassword");

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

		when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);

		when(userRepository.findByUsername("test"))
		.thenReturn(Optional.empty());

		when(userRepository.findByEmail("test2@example.com"))
		.thenReturn(Optional.empty());		    		  

		ResponseEntity<String> response = userService.updateUser(updateRequest);

		assertEquals("Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a digit, and a special character",
				response.getBody());
	}


	@Test
	void testUpdateUserUtilisateurMisAJourAvecSucces() {

		UpdateUserRequest updateRequest = new UpdateUserRequest();
		updateRequest.setOriginalEmail("test@example.com");  
		updateRequest.setUsername("test");
		updateRequest.setOldPassword("oldPassword");
		updateRequest.setEmail("test2@example.com");
		updateRequest.setPassword("1234Ab//");

		User user = new User();
		user.setUsername("test1");
		user.setPassword("encodedPassword");

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

		when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);

		when(userRepository.findByUsername("test"))
		.thenReturn(Optional.empty());

		when(userRepository.findByEmail("test2@example.com"))
		.thenReturn(Optional.empty());		    		  

		ResponseEntity<String> response = userService.updateUser(updateRequest);

		assertEquals("User information updated successfully", response.getBody());
	}

	@Test
	void testDeleteUserByEmailQuandUtilisateurIntrouvable() {

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

		ResponseEntity<String> response = userService.deleteUserByEmail("test@example.com");

		assertEquals("User not found with email : test@example.com", response.getBody());
	}

	@Test
	void testDeleteUserByEmailErreur() {

		when(userRepository.findByEmail("test@example.com")).thenThrow(new RuntimeException("Database error"));

		ResponseEntity<String> response = userService.deleteUserByEmail("test@example.com");

		assertEquals("Error occurred while deleting the user account", response.getBody());
	}

	@Test
	void testDeleteUserByEmailUtilisateurSupprimerAvecSuccès() {

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

		ResponseEntity<String> response = userService.deleteUserByEmail("test@example.com");

		assertEquals("User successfully deleted", response.getBody());
	}




}
