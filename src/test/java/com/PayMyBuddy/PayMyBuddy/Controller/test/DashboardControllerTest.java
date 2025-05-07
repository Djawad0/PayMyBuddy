package com.PayMyBuddy.PayMyBuddy.Controller.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.Model;
import com.PayMyBuddy.PayMyBuddy.configuration.CustomUserDetailsService;
import com.PayMyBuddy.PayMyBuddy.controller.DashboardController;
import com.PayMyBuddy.PayMyBuddy.dto.TransactionDTO;
import com.PayMyBuddy.PayMyBuddy.model.Transaction;
import com.PayMyBuddy.PayMyBuddy.model.User;
import com.PayMyBuddy.PayMyBuddy.service.TransactionService;

@SpringBootTest
class DashboardControllerTest {

	@InjectMocks
	private DashboardController dashboardController;

	@Mock
	private CustomUserDetailsService customUserDetailsService;

	@Mock
	private User user;

	@Mock
	private Model model;

	@Mock
	private TransactionService transactionService;

	@Mock
	private Transaction transaction;

	@Test
	void testGetDashboard() {

		user = new User();
		user.setEmail("test@example.com");
		user.setConnections(new ArrayList<>());

		when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);
		when(transactionService.getTransactionsByUser(user)).thenReturn(new ArrayList<>());

		String viewName = dashboardController.dashboard(model);

		assertEquals("dashboard", viewName);
		verify(model).addAttribute("user", user);
		verify(model).addAttribute("friends", user.getConnections());
		verify(model).addAttribute("transactions", new ArrayList<>());

		ArgumentCaptor<TransactionDTO> captor = ArgumentCaptor.forClass(TransactionDTO.class);
		verify(model).addAttribute(eq("transaction"), captor.capture());

		TransactionDTO transactionDTO = captor.getValue();
		assertEquals("test@example.com", transactionDTO.getSenderEmail());
		assertEquals("", transactionDTO.getDescription());
		assertEquals("", transactionDTO.getReceiverEmail());
		assertEquals(0.0, transactionDTO.getAmount());
	}

	@Test
	void testTransaction_Success() {

		when(transactionService.transaction(any(Transaction.class))).thenReturn("Transaction completed successfully");

		String viewName = dashboardController.transaction(transaction, model);

		assertEquals("redirect:/user/dashboard?success=Transaction completed successfully", viewName);
	}

	 @Test
	    void testTransactionNoSuchElementException() {
	        when(transactionService.transaction(any(Transaction.class)))
	                .thenThrow(new NoSuchElementException("User not found"));

	        String result = dashboardController.transaction(transaction, model);

	        assertEquals("redirect:/user/dashboard?error=User not found", result);
	    }

	    @Test
	    void testTransactionIllegalArgumentException() {
	        when(transactionService.transaction(any(Transaction.class)))
	                .thenThrow(new IllegalArgumentException("Invalid transaction"));

	        String result = dashboardController.transaction(transaction, model);

	        assertEquals("redirect:/user/dashboard?error=Invalid transaction", result);
	    }

	    @Test
	    void testTransactionIllegalStateException() {
	        when(transactionService.transaction(any(Transaction.class)))
	                .thenThrow(new IllegalStateException("Insufficient funds"));

	        String result = dashboardController.transaction(transaction, model);

	        assertEquals("redirect:/user/dashboard?error=Insufficient funds", result);
	    }

	    @Test
	    void testTransactionGeneralException() {
	        when(transactionService.transaction(any(Transaction.class)))
	                .thenThrow(new RuntimeException("Unexpected error"));

	        String result = dashboardController.transaction(transaction, model);

	        assertEquals("redirect:/user/dashboard?error=Transaction error.", result);
	    }

}
