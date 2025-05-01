package com.PayMyBuddy.PayMyBuddy.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.PayMyBuddy.PayMyBuddy.configuration.CustomUserDetailsService;
import com.PayMyBuddy.PayMyBuddy.dto.EmailRequest;
import com.PayMyBuddy.PayMyBuddy.dto.TransactionDTO;
import com.PayMyBuddy.PayMyBuddy.dto.UpdateUserRequest;
import com.PayMyBuddy.PayMyBuddy.model.AdminWallet;
import com.PayMyBuddy.PayMyBuddy.model.Transaction;
import com.PayMyBuddy.PayMyBuddy.model.User;
import com.PayMyBuddy.PayMyBuddy.repository.AdminWalletRepository;
import com.PayMyBuddy.PayMyBuddy.service.ConnectionService;
import com.PayMyBuddy.PayMyBuddy.service.TransactionService;
import com.PayMyBuddy.PayMyBuddy.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {

	private final UserService userService;	
	private final AdminWalletRepository adminWalletRepository;
	private final CustomUserDetailsService customUserDetailsService;
	private final ConnectionService connectionService;		
	private final TransactionService transactionService;

	@Autowired
	public UserController(UserService userService, ConnectionService connectionService, TransactionService transactionService,
			AdminWalletRepository adminWalletRepository, CustomUserDetailsService customUserDetailsService) {
		this.userService = userService;
		this.connectionService = connectionService;
		this.transactionService = transactionService;
		this.adminWalletRepository = adminWalletRepository;
		this.customUserDetailsService = customUserDetailsService;
	}

	@PostMapping("/inscription")
	public ResponseEntity<String> inscription(@RequestBody User user) {
		return userService.inscription(user);       
	}

	@PostMapping("/user/connection")
	public ResponseEntity<String> createConnection(@RequestBody EmailRequest request) {
		return connectionService.createConnection(request.getEmail());
	}

	@PostMapping("/user/transaction")
	public ResponseEntity<String> transaction(@RequestBody Transaction transaction) {
		return transactionService.transaction(transaction);
	}

	@GetMapping("/user/transactions")
	public ResponseEntity<List<TransactionDTO>> getUserTransactions() {
		User user = customUserDetailsService.getAuthenticatedUser();
		return ResponseEntity.ok(transactionService.getTransactionsByUser(user));
	}

	@GetMapping("/admin/transactions")
	public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
		return ResponseEntity.ok(transactionService.getAllTransactions());
	}

	@GetMapping("/user/friends")
	public ResponseEntity<List<String>> getUserConnections() {
		User user = customUserDetailsService.getAuthenticatedUser();
		List<String> connections = new ArrayList<>();

		for (User friend : user.getConnections()) {
			connections.add(friend.getUsername());
		}

		return ResponseEntity.ok(connections);
	}

	@PostMapping("/user/update")
	public ResponseEntity<String> updateUser(@RequestBody UpdateUserRequest updateRequest) {
		return userService.updateUser(updateRequest);     
	}

	@PostMapping("/user/addToBankAccount")
	public ResponseEntity<String> addToBankAccount(@RequestBody Transaction transaction) {
		return transactionService.addToBankAccount(transaction);     
	}

	@PostMapping("/user/withdrawToBankAccount")
	public ResponseEntity<String> withdrawToBankAccount(@RequestBody Transaction transaction) {
		return transactionService.withdrawToBankAccount(transaction);     
	}

	@GetMapping("/admin/wallet")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> getWalletBalance() {
		AdminWallet wallet = adminWalletRepository.findById(1L)
				.orElseThrow(() -> new RuntimeException("AdminWallet not found"));
		return ResponseEntity.ok("Total commission balance: " + wallet.getBalance() + "€");
	}

}
