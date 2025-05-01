package com.PayMyBuddy.PayMyBuddy.IT;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TransactionIT {

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
	public void testTransaction() throws Exception {
		mockMvc.perform(post("/user/transaction")
				.param("receiverEmail", "user2@exemple.com")
				.param("amount", "50.0")
				.param("description", "Test payment"))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrlPattern("/user/dashboard?success=*"));

		User sender = userRepository.findByEmail("user1@exemple.com").orElseThrow();
		User receiver = userRepository.findByEmail("user2@exemple.com").orElseThrow();

		double expectedSenderBalance = 100.0 - 50.0 - (50.0 * 0.005); 
		double expectedReceiverBalance = 100.0 + 50.0;

		assertEquals(expectedSenderBalance, sender.getBalance());
		assertEquals(expectedReceiverBalance, receiver.getBalance());

	}

	@Test
	@WithUserDetails(value = "user1@exemple.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void testDepositMoney() throws Exception {
		double depositAmount = 25.0;

		double initialBalance = userRepository.findByEmail("user1@exemple.com").orElseThrow().getBalance();

		mockMvc.perform(post("/user/deposit")
				.param("amount", String.valueOf(depositAmount)))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrlPattern("/user/profile?successDepositMoney=*"));

		double newBalance = userRepository.findByEmail("user1@exemple.com").orElseThrow().getBalance();

		assertEquals(initialBalance - depositAmount, newBalance);
	}

	@Test
	@WithUserDetails(value = "user1@exemple.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void testWithdrawMoney() throws Exception {
		double withdrawAmount = 30.0;


		double initialBalance = userRepository.findByEmail("user1@exemple.com").orElseThrow().getBalance();


		mockMvc.perform(post("/user/withdraw")
				.param("amount", String.valueOf(withdrawAmount)))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrlPattern("/user/profile?successWithdrawMoney=*"));


		double newBalance = userRepository.findByEmail("user1@exemple.com").orElseThrow().getBalance();

		assertEquals(initialBalance + withdrawAmount, newBalance);
	}

}
