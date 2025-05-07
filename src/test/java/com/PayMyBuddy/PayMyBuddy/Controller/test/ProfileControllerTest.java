package com.PayMyBuddy.PayMyBuddy.Controller.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.Model;
import com.PayMyBuddy.PayMyBuddy.configuration.CustomUserDetailsService;
import com.PayMyBuddy.PayMyBuddy.controller.ProfileController;
import com.PayMyBuddy.PayMyBuddy.dto.UpdateUserRequest;
import com.PayMyBuddy.PayMyBuddy.model.Transaction;
import com.PayMyBuddy.PayMyBuddy.model.User;
import com.PayMyBuddy.PayMyBuddy.service.TransactionService;
import com.PayMyBuddy.PayMyBuddy.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@SpringBootTest
public class ProfileControllerTest {

	@InjectMocks
	private ProfileController profileController;

	@Mock
	private CustomUserDetailsService customUserDetailsService;

	@Mock
	private Model model;

	@Mock
	private Transaction transaction;

	@Mock
	private TransactionService transactionService;

	@Mock
	private User user;

	@Mock
	private UserService userService;

	@Test
	void testProfilePage() {

		when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);  

		String result = profileController.profilePage(model);

		verify(model).addAttribute("user", user);
		assertEquals("profile", result);
	}


	@Test
	void testUpdateProfileMotsDePasseNeCorrespondentPas() {

		String result = profileController.updateProfile(user, "1234", "12345", "1234", model);


		verify(model).addAttribute("errorPassword", "Passwords do not match.");
		assertEquals("profile", result);
	}

	@Test
	void testUpdateProfileEmailInvalide1() {

		User user = new User();
		user.setEmail("testexample.com");

		when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);

		String result = profileController.updateProfile(user, "1234", "12345", "12345", model);


		verify(model).addAttribute("errorEmail", "Invalid email.");
		assertEquals("profile", result);
	}

	@Test
	void testUpdateProfileEmailInvalide2() {

		User user = new User();
		user.setEmail("test@examplecom");

		when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);

		String result = profileController.updateProfile(user, "1234", "12345", "12345", model);


		verify(model).addAttribute("errorEmail", "Invalid email.");
		assertEquals("profile", result);
	}

	@Test
	void testUpdateProfileUtilisateurIntrouvable() {

		User user = new User();
		user.setEmail("test@example.com");

		when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);

		 when(userService.updateUser(any(UpdateUserRequest.class))).thenReturn("User information updated successfully");

		when(customUserDetailsService.loadUserByUsername(user.getEmail())).thenThrow(new UsernameNotFoundException("Updated user not found."));

		String result = profileController.updateProfile(user, "1234", "12345", "12345", model);


		verify(model).addAttribute("error", "Updated user not found.");
		assertEquals("profile", result);
	}

	@Test
	void testUpdateProfileErreur() {

		User user = new User();
		user.setEmail("test@example.com");

		when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);
		when(userService.updateUser(any(UpdateUserRequest.class))).thenReturn("Some update error");


		String result = profileController.updateProfile(user, "1234", "12345", "12345", model);


		verify(model).addAttribute("error", "Some update error");
		assertEquals("profile", result);
	}

	@Test
	void testUpdateProfileSuccess() {

		User user = new User();
		user.setEmail("test@example.com");

		when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);
		when(userService.updateUser(any(UpdateUserRequest.class))).thenReturn("User information updated successfully");

		UserDetails updatedUserDetails = new org.springframework.security.core.userdetails.User(
				user.getEmail(),
				"password", 
				new ArrayList<>());
		when(customUserDetailsService.loadUserByUsername(user.getEmail())).thenReturn(updatedUserDetails);

		String result = profileController.updateProfile(user, "1234", "12345", "12345", model);


		verify(model).addAttribute("success", "User information updated successfully");
		assertEquals("profile", result);
	}

	@Test
	void testDepositMoneyError() {

		when(customUserDetailsService.getAuthenticatedUser()).thenThrow(new RuntimeException("Something went wrong"));

		String result = profileController.depositMoney(0.0, model);

		verify(model).addAttribute("errorDepositMoney", "Error during deposit.");
		assertEquals("profile", result);
	}

	@Test
	void testDepositMoneySuccess() {

		User user = new User();
		user.setEmail("test@example.com");

		when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);

		 when(transactionService.addToBankAccount(any(Transaction.class)))
         .thenReturn("Amount successfully added to your bank account.");

		String result = profileController.depositMoney(10.0, model);

		assertTrue(result.contains("redirect:/user/profile?successDepositMoney="));
	}

	@Test
	void testWithdrawMoneyError() {

		 when(customUserDetailsService.getAuthenticatedUser()).thenThrow(new RuntimeException("Something went wrong"));

	        String result = profileController.withdrawMoney(0.0, model);

	        verify(model).addAttribute("errorWithdrawMoney", "Error during withdrawal.");
	        assertEquals("profile", result);
	}

	@Test
	void testWithdrawMoneySuccess() {

		User user = new User();
		user.setEmail("test@example.com");

		when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);

		 when(transactionService.withdrawToBankAccount(any(Transaction.class)))
         .thenReturn("Withdrawal successfully completed.");

		String result = profileController.withdrawMoney(10.0, model);

		assertTrue(result.contains("redirect:/user/profile?successWithdrawMoney="));
	}
	
	
	
}
