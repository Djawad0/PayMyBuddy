package com.PayMyBuddy.PayMyBuddy.Service.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import com.PayMyBuddy.PayMyBuddy.configuration.CustomUserDetailsService;
import com.PayMyBuddy.PayMyBuddy.model.Connection;
import com.PayMyBuddy.PayMyBuddy.model.ConnectionId;
import com.PayMyBuddy.PayMyBuddy.model.User;
import com.PayMyBuddy.PayMyBuddy.repository.DBConnectionRepository;
import com.PayMyBuddy.PayMyBuddy.repository.DBUserRepository;
import com.PayMyBuddy.PayMyBuddy.service.ConnectionService;

@SpringBootTest
public class ConnectionServiceTest {

	@InjectMocks
	private ConnectionService connectionService;

	@Mock
	private CustomUserDetailsService customUserDetailsService;

	@Mock
	private User user;

	@Mock
	private Connection connection;

	@Mock
	private DBUserRepository userRepository;

	@Mock
	private DBConnectionRepository dbConnectionRepository;

	@Test
	void testCreateConnectionAjoutDeSoiMeme() {

		User user = new User();
		user.setEmail("test@example.com");

		when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);	    

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
	        connectionService.createConnection("test@example.com");
	    });

	    assertEquals("You cannot add yourself as a friend.", thrown.getMessage());

	}

	@Test
	void testCreateConnectionUtilisateurNonTrouve() {

		User user = new User();
		user.setEmail("test1@example.com");

		when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);	    

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());	  

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
	        connectionService.createConnection("test@example.com");
	    });

	    assertEquals("User not found: test@example.com", thrown.getMessage());

	}

	@Test
	void testCreateConnectionUtilisateurDejaAjoute() {

		User user = new User();
		user.setEmail("test1@example.com");

		when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);	    

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));	  

		when(dbConnectionRepository.existsById(any(ConnectionId.class))).thenReturn(true);	  

		IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
	        connectionService.createConnection("test@example.com");
	    });

	    assertEquals("You have already added this user.", thrown.getMessage());

	}

	@Test
	void testCreateConnectionUtilisateurAjouteAvecSucces() {

		User user = new User();
		user.setEmail("test1@example.com");

		when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);	    

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));	  

		when(dbConnectionRepository.existsById(any(ConnectionId.class))).thenReturn(false);	  

		String response = connectionService.createConnection("test@example.com");
		
	    assertEquals("Friend successfully added.", response);

	}
	
}
