package com.PayMyBuddy.PayMyBuddy.Controller.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

		when(transactionService.transaction(any(Transaction.class)))
		.thenReturn(new ResponseEntity<>("Successful transaction", HttpStatus.OK));

		String viewName = dashboardController.transaction(transaction, model);

		assertEquals("redirect:/user/dashboard?success=Successful transaction", viewName);
	}

	@Test
	void testTransaction_Error() {

		when(transactionService.transaction(any(Transaction.class)))
		.thenReturn(new ResponseEntity<>("Transaction error", HttpStatus.NOT_FOUND));

		String viewName = dashboardController.transaction(transaction, model);

		assertEquals("redirect:/user/dashboard?error=Transaction error", viewName);
	}

}
