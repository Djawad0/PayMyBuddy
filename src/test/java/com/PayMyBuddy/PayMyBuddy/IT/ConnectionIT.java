package com.PayMyBuddy.PayMyBuddy.IT;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.PayMyBuddy.PayMyBuddy.model.User;
import com.PayMyBuddy.PayMyBuddy.repository.DBUserRepository;
import com.PayMyBuddy.PayMyBuddy.service.UserService;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ConnectionIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private DBUserRepository userRepository;

	@Autowired
	private UserService userService ;

	@AfterEach
	void cleanup() {	       

		userService.deleteUserByEmail("user1@exemple.com");
		userService.deleteUserByEmail("user2@exemple.com");

	}

	@BeforeEach
	void setup() {	       	
		User user1 = new User();
		user1.setUsername("user1");
		user1.setEmail("user1@exemple.com");
		user1.setPassword(passwordEncoder.encode("a1234A//"));
		user1.setBalance(100.0);
		user1.setRole("USER");
		userRepository.save(user1);

		User user2 = new User();
		user2.setUsername("user2");
		user2.setEmail("user2@exemple.com");
		user2.setPassword(passwordEncoder.encode("a1234A//"));
		user2.setBalance(100.0);
		user2.setRole("USER");
		userRepository.save(user2);
	}

	@Test
	@WithUserDetails(value = "user1@exemple.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void testAddFriendSuccess() throws Exception {

		mockMvc.perform(post("/user/add-friend")
				.param("friendEmail", "user2@exemple.com"))
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("success"));

		User user1 = userRepository.findByEmailWithConnections("user1@exemple.com").orElseThrow();

		List<String> connections = new ArrayList<>();

		for (User friend : user1.getConnections()) {
			connections.add(friend.getEmail());
		}

		assertFalse(connections.isEmpty());
		assertTrue(connections.contains("user2@exemple.com"));
	}

}
