package com.PayMyBuddy.PayMyBuddy.Controller.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.Model;
import com.PayMyBuddy.PayMyBuddy.controller.AdminController;
import com.PayMyBuddy.PayMyBuddy.model.AdminWallet;
import com.PayMyBuddy.PayMyBuddy.repository.AdminWalletRepository;
import com.PayMyBuddy.PayMyBuddy.service.TransactionService;
import com.PayMyBuddy.PayMyBuddy.service.UserService;

@SpringBootTest
public class AdminControllerTest {

	@InjectMocks
	private AdminController adminController ;

	@Mock
	private TransactionService transactionService;

	@Mock
	private AdminWalletRepository adminWalletRepository;

	@Mock
	private UserService userService;

	@Mock
	private Model model;

	@Test
	void testShowAllTransactions() {

		when(transactionService.getAllTransactions()).thenReturn(new ArrayList<>());

		AdminWallet wallet = new AdminWallet();
		wallet.setId(1L);
		wallet.setBalance(1000.0);
		when(adminWalletRepository.findById(1L)).thenReturn(Optional.of(wallet));


		String viewName = adminController.showAllTransactions(model);


		assertEquals("admin", viewName);
		verify(model).addAttribute("balance", 1000.0);
		verify(model).addAttribute("transactions", new ArrayList<>());
	}

	@Test
	void testDeleteUserByAdmin_Success() {

		String email = "user@example.com";
		when(userService.deleteUserByEmail(email)).thenReturn("Deleted user");


		String viewName = adminController.deleteUserByAdmin(email, model);


		assertEquals("redirect:/admin/dashboard?success=Deleted user", viewName);
	}

	 @Test
	    void testDeleteUserByAdmin_NoSuchElementException() {
	        String email = "user@example.com";
	        when(userService.deleteUserByEmail(email)).thenThrow(new NoSuchElementException("User not found"));

	        String viewName = adminController.deleteUserByAdmin(email, model);

	        assertEquals("redirect:/admin/dashboard?error=User not found", viewName);
	    }

	    @Test
	    void testDeleteUserByAdmin_GenericException() {
	        String email = "user@example.com";
	        when(userService.deleteUserByEmail(email)).thenThrow(new RuntimeException("Internal error"));

	        String viewName = adminController.deleteUserByAdmin(email, model);

	        assertEquals("redirect:/admin/dashboard?error=Error when deleting user.", viewName);
	    }

}
