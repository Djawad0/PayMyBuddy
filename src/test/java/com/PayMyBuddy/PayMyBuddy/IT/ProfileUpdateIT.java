package com.PayMyBuddy.PayMyBuddy.IT;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.PayMyBuddy.PayMyBuddy.model.User;
import com.PayMyBuddy.PayMyBuddy.repository.DBUserRepository;
import com.PayMyBuddy.PayMyBuddy.service.UserService;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProfileUpdateIT {


	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private DBUserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserService userService;

	@AfterEach
	void cleanup() {	       		 
		userService.deleteUserByEmail("user1@exemple.com");   
		userService.deleteUserByEmail("user@exemple.com");
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


	}

	@Test
	public void testProfileUpdateSuccess() throws Exception {

		MockHttpSession session = new MockHttpSession();


		mockMvc.perform(post("/login")
				.param("username", "user1@exemple.com")
				.param("password", "a1234A//")
				.session(session))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/user/dashboard"));


		mockMvc.perform(post("/user/profile")
				.session(session)
				.param("currentPassword", "a1234A//")
				.param("newPassword", "a1234A///")
				.param("confirmNewPassword", "a1234A///")
				.param("username", "user")
				.param("email", "user@exemple.com"))
		.andExpect(status().isOk())
		.andExpect(view().name("profile"));


		mockMvc.perform(post("/logout")        
				.session(session))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/login?logout"));

		mockMvc.perform(post("/login")
				.param("username", "user@exemple.com")
				.param("password", "a1234A///")
				.session(session))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/user/dashboard"));

		Optional<User> userEmail = userRepository.findByEmail("user@exemple.com");

		Optional<User> userUsername = userRepository.findByUsername("user");

		assertTrue(userEmail.isPresent());
		assertTrue(userUsername.isPresent());

	}
}
