package com.PayMyBuddy.PayMyBuddy.IT;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.PayMyBuddy.PayMyBuddy.model.User;
import com.PayMyBuddy.PayMyBuddy.repository.DBUserRepository;
import com.PayMyBuddy.PayMyBuddy.service.UserService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LoginIT {



	@Autowired
	private DBUserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserService userService ;

	@Autowired
	private MockMvc mockMvc;


	@AfterEach
	void cleanup() {
		userService.deleteUserByEmail("test@example.com");	
	}

	@Test
	public void testLoginAdmin() throws Exception {
		User user = new User();
		user.setUsername("test0");
		user.setEmail("test@example.com");
		user.setPassword(passwordEncoder.encode("Password123/"));
		user.setRole("ADMIN"); 
		userRepository.save(user);

		mockMvc.perform(post("/login")
				.param("username", "test@example.com")
				.param("password", "Password123/"))
		.andExpect(status().is3xxRedirection())  
		.andExpect(redirectedUrl("/admin/dashboard"));      
	}

	@Test
	public void testLoginUser() throws Exception {
		User user = new User();
		user.setUsername("test0");
		user.setEmail("test@example.com");
		user.setPassword(passwordEncoder.encode("Password123/"));
		user.setRole("USER"); 
		userRepository.save(user);

		mockMvc.perform(post("/login")
				.param("username", "test@example.com")
				.param("password", "Password123/"))
		.andExpect(status().is3xxRedirection())  
		.andExpect(redirectedUrl("/user/dashboard"));       
	}

}
