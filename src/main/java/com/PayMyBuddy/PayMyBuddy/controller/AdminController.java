package com.PayMyBuddy.PayMyBuddy.controller;

import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.PayMyBuddy.PayMyBuddy.dto.TransactionDTO;
import com.PayMyBuddy.PayMyBuddy.model.AdminWallet;
import com.PayMyBuddy.PayMyBuddy.repository.AdminWalletRepository;
import com.PayMyBuddy.PayMyBuddy.service.TransactionService;
import com.PayMyBuddy.PayMyBuddy.service.UserService;

@Controller
public class AdminController {

	private final TransactionService transactionService;    
	private final UserService userService;
	private final AdminWalletRepository adminWalletRepository;

	@Autowired
	public AdminController(UserService userService, AdminWalletRepository adminWalletRepository, TransactionService transactionService) {
		this.userService = userService;
		this.transactionService = transactionService;
		this.adminWalletRepository = adminWalletRepository;
	}

	@GetMapping("/admin/dashboard")
	@PreAuthorize("hasRole('ADMIN')")
	public String showAllTransactions(Model model) {
		List<TransactionDTO> transactions = transactionService.getAllTransactions();
		AdminWallet wallet = adminWalletRepository.findById(1L)
				.orElseThrow(() -> new RuntimeException("AdminWallet not found"));
		model.addAttribute("balance", wallet.getBalance());
		model.addAttribute("transactions", transactions);

		return "admin"; 
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/admin/delete-user")
	public String deleteUserByAdmin(@RequestParam String email, Model model) {
		 try {
	            String result = userService.deleteUserByEmail(email);
	            return "redirect:/admin/dashboard?success=" + result;
	        } catch (NoSuchElementException e) {
	            return "redirect:/admin/dashboard?error=" + e.getMessage();
	        } catch (Exception e) {
	            return "redirect:/admin/dashboard?error=Error when deleting user.";
	        }
	}

}
