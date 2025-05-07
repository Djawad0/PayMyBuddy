package com.PayMyBuddy.PayMyBuddy.Service.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
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

		assertThrows(IllegalArgumentException.class, () -> userService.inscription(user));
	}

	@Test
	void testInscriptionQuandEmailInvalide2() {

		when(user.getEmail()).thenReturn("test@exemplecom");

		 assertThrows(IllegalArgumentException.class, () -> userService.inscription(user));
	}

	@Test
	void testInscriptionQuandEmailEstDejaUtilise() {

		when(user.getEmail()).thenReturn("test@example.com");
		when(userRepository.findByEmail("test@example.com"))
		.thenReturn(Optional.of(user));

		assertThrows(IllegalStateException.class, () -> userService.inscription(user));
	}

	@Test
	void testInscriptionQuandUtilisateurEstDejaUtilise() {

		when(user.getEmail()).thenReturn("test@example.com");

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

		when(user.getUsername()).thenReturn("test");
		when(userRepository.findByUsername("test"))
		.thenReturn(Optional.of(user));

		assertThrows(IllegalStateException.class, () -> userService.inscription(user));
	}

	@Test
	void testInscriptionQuandMotDePasseNonConforme1() {

		when(user.getEmail()).thenReturn("test@example.com");

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

		when(user.getUsername()).thenReturn("test");
		when(userRepository.findByUsername("test")).thenReturn(Optional.empty());

		when(user.getPassword()).thenReturn(null);

		assertThrows(IllegalArgumentException.class, () -> userService.inscription(user));
	}

	@Test
	void testInscriptionQuandMotDePasseNonConforme2() {

		when(user.getEmail()).thenReturn("test@example.com");

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

		when(user.getUsername()).thenReturn("test");
		when(userRepository.findByUsername("test")).thenReturn(Optional.empty());

		when(user.getPassword()).thenReturn("1234");

		assertThrows(IllegalArgumentException.class, () -> userService.inscription(user));
	}


	@Test
	void testInscriptionReussie() {

		when(user.getEmail()).thenReturn("test@example.com");

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

		when(user.getUsername()).thenReturn("test");
		when(userRepository.findByUsername("test")).thenReturn(Optional.empty());

		when(user.getPassword()).thenReturn("1234Ab//");

		when(passwordEncoder.encode("1234Ab//")).thenReturn("encodedPassword");

		 String result = userService.inscription(user);
	        assertEquals("Registration successful", result);
	}

	@Test
	void testUpdateUserUtilisateurNonTrouvé() {

		when(userRepository.findByEmail("test@example.com")).thenReturn(null);

		assertThrows(NoSuchElementException.class, () -> userService.updateUser(updateRequest));
	}

	@Test
	void testUpdateUserAncienMotDePasseIncorrect() {

		UpdateUserRequest updateRequest = new UpdateUserRequest();
		updateRequest.setOriginalEmail("test@example.com");  

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

		when(passwordEncoder.matches("wrongOldPassword", "encodedOldPassword")).thenReturn(false);

		assertThrows(SecurityException.class, () -> userService.updateUser(updateRequest));
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

		assertThrows(IllegalStateException.class, () -> userService.updateUser(updateRequest));
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

		 assertThrows(IllegalStateException.class, () -> userService.updateUser(updateRequest));
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

		assertThrows(IllegalArgumentException.class, () -> userService.updateUser(updateRequest));
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

		String result = userService.updateUser(updateRequest);
        assertEquals("User information updated successfully", result);
	}

	@Test
	void testDeleteUserByEmailQuandUtilisateurIntrouvable() {

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> userService.deleteUserByEmail("test@example.com"));
	}


	@Test
	void testDeleteUserByEmailUtilisateurSupprimerAvecSuccès() {

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

		String result = userService.deleteUserByEmail("test@example.com");
        assertEquals("User successfully deleted", result);
	}




}
