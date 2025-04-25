package com.PayMyBuddy.PayMyBuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import com.PayMyBuddy.PayMyBuddy.configuration.CustomUserDetailsService;
import com.PayMyBuddy.PayMyBuddy.dto.TransactionDTO;
import com.PayMyBuddy.PayMyBuddy.model.Transaction;
import com.PayMyBuddy.PayMyBuddy.model.User;
import com.PayMyBuddy.PayMyBuddy.service.TransactionService;

@Controller
public class DashboardController {


	private final CustomUserDetailsService customUserDetailsService;
	private final TransactionService transactionService;    
	

	@Autowired
	public DashboardController(TransactionService transactionService, CustomUserDetailsService customUserDetailsService) {       
		this.transactionService = transactionService;
		this.customUserDetailsService = customUserDetailsService;	
	
	}

	@GetMapping("/user/dashboard")
	public String dashboard(Model model) {

		User user = customUserDetailsService.getAuthenticatedUser();
	
		model.addAttribute("user", user);
		model.addAttribute("friends", user.getConnections());
		model.addAttribute("transaction", new TransactionDTO(user.getEmail(), "", "", 0.0));
		model.addAttribute("transactions", transactionService.getTransactionsByUser(user));

		return "dashboard";
	}

	@PostMapping("/user/transaction")
	public String transaction(Transaction transaction, Model model) {

		ResponseEntity<String> response = transactionService.transaction(transaction);

		if (response.getStatusCode() == HttpStatus.OK) {

			return "redirect:/user/dashboard?success=" + response.getBody();
		} else {
			return "redirect:/user/dashboard?error=" + response.getBody(); 
		}    	
	}
}
