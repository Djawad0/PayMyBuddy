package com.PayMyBuddy.PayMyBuddy.IT;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import com.PayMyBuddy.PayMyBuddy.model.User;
import com.PayMyBuddy.PayMyBuddy.repository.DBUserRepository;
import com.PayMyBuddy.PayMyBuddy.service.UserService;

@SpringBootTest
@ActiveProfiles("test")
public class RegisterIT {

	@Autowired
	private DBUserRepository userRepository;

	@Autowired
	private UserService userService ;

	@AfterEach
	public void cleanup() {
		Optional<User> existingUser = userRepository.findByEmail("test@exemple.com");
		
		existingUser.ifPresent(user -> userService.deleteUserByEmail(user.getEmail()));
	}

	@Test
	public void testInscriptionReussie() {
		User user = new User();
		user.setUsername("test0");
		user.setEmail("test@exemple.com");
		user.setPassword("a1234A//");

		String response = userService.inscription(user);
		assertEquals("Registration successful",	response);

		Optional<User> userEmail = userRepository.findByEmail(user.getEmail());

		assertTrue(userEmail.isPresent());


	}


	@Test
	public void testInscriptionEmailInvalide() {
		User user = new User();
		user.setUsername("test0");
		user.setEmail("testexemple.com");
		user.setPassword("a1234A//");

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
	        userService.inscription(user);
	    });
  
	    assertEquals("Invalid email address", thrown.getMessage());

		Optional<User> userEmail = userRepository.findByEmail(user.getEmail());

		assertFalse(userEmail.isPresent());
	}

	@Test
	public void testInscriptionEmailDéjàUtilisé() {

		User user1 = new User();
		user1.setUsername("test0");
		user1.setEmail("test@exemple.com");
		user1.setPassword("a1234A//");

		String response1 = userService.inscription(user1);
		assertEquals("Registration successful",	response1);

		Optional<User> userEmail1 = userRepository.findByEmail(user1.getEmail());

		assertTrue(userEmail1.isPresent());


		User user = new User();
		user.setUsername("test0");
		user.setEmail("test@exemple.com");
		user.setPassword("a1234A//");

		IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
	        userService.inscription(user);
	    });

	    assertEquals("Email is already in use", thrown.getMessage());
		

		Optional<User> userEmail = userRepository.findByEmail(user.getEmail());

		assertTrue(userEmail.isPresent());
	}

	@Test
	public void testInscriptionMotDePasseInvalide() {
		User user = new User();
		user.setUsername("test0");
		user.setEmail("test2@exemple.com");
		user.setPassword("a1234A/");

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
	        userService.inscription(user);
	    });

	    assertEquals("Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a digit, and a special character", thrown.getMessage());

		Optional<User> userEmail = userRepository.findByEmail(user.getEmail());

		assertFalse(userEmail.isPresent());

	}

}
