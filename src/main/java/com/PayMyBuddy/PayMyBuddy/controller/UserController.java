package com.PayMyBuddy.PayMyBuddy.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
		 try {
	            String result = userService.inscription(user);
	            return ResponseEntity.status(HttpStatus.CREATED).body(result);
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur lors de l'inscription: " + e.getMessage());
	        } catch (IllegalStateException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur d'état lors de l'inscription: " + e.getMessage());
	        } catch (RuntimeException e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne: " + e.getMessage());
	        }
	}

	@PostMapping("/user/connection")
	public ResponseEntity<String> createConnection(@RequestBody EmailRequest request) {
		try {
            String result = connectionService.createConnection(request.getEmail());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur lors de la connexion: " + e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur d'état lors de la connexion: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne: " + e.getMessage());
        }
	}

	@PostMapping("/user/transaction")
	public ResponseEntity<String> transaction(@RequestBody Transaction transaction) {
		  try {
	            String result = transactionService.transaction(transaction);
	            return ResponseEntity.ok(result);
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur lors de la transaction: " + e.getMessage());
	        } catch (IllegalStateException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur d'état lors de la transaction: " + e.getMessage());
	        } catch (NoSuchElementException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur d'état lors de la transaction: " + e.getMessage());
	        } catch (RuntimeException e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne: " + e.getMessage());
	        }
	}

	@GetMapping("/user/transactions")
	public ResponseEntity<List<TransactionDTO>> getUserTransactions() {
		  try {
	            User user = customUserDetailsService.getAuthenticatedUser();
	            List<TransactionDTO> transactions = transactionService.getTransactionsByUser(user);
	            return ResponseEntity.ok(transactions);
	        
	        } catch (RuntimeException e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	        }
	}

	@GetMapping("/admin/transactions")
	public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
		try {
            List<TransactionDTO> transactions = transactionService.getAllTransactions();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
	}

	@GetMapping("/user/friends")
	public ResponseEntity<List<String>> getUserConnections() {
		try {
            User user = customUserDetailsService.getAuthenticatedUser();
            List<String> connections = new ArrayList<>();
            for (User friend : user.getConnections()) {
                connections.add(friend.getUsername());
            }
            return ResponseEntity.ok(connections);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
	}

	@PostMapping("/user/update")
	public ResponseEntity<String> updateUser(@RequestBody UpdateUserRequest updateRequest) {
		try {
            String result = userService.updateUser(updateRequest);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur d'état lors de la mise à jour de l'utilisateur: " + e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur d'état lors de la mise à jour de l'utilisateur: " + e.getMessage());
        }catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur d'état lors de la mise à jour de l'utilisateur: " + e.getMessage());
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne: " + e.getMessage());
        }
	}

	@PostMapping("/user/addToBankAccount")
	public ResponseEntity<String> addToBankAccount(@RequestBody Transaction transaction) {
		 try {
	            String result = transactionService.addToBankAccount(transaction);
	            return ResponseEntity.ok(result);
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur lors de l'ajout sur le compte bancaire: " + e.getMessage());
	        } catch (IllegalStateException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur d'état lors de l'ajout sur le compte bancaire: " + e.getMessage());
	        } catch (RuntimeException e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne: " + e.getMessage());
	        }
	}

	@PostMapping("/user/withdrawToBankAccount")
	public ResponseEntity<String> withdrawToBankAccount(@RequestBody Transaction transaction) {
		 try {
	            String result = transactionService.withdrawToBankAccount(transaction);
	            return ResponseEntity.ok(result);
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur lors du retrait du compte bancaire: " + e.getMessage());
	        } catch (RuntimeException e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne: " + e.getMessage());
	        }
	}

	@GetMapping("/admin/wallet")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> getWalletBalance() {
		 try {
	            AdminWallet wallet = adminWalletRepository.findById(1L)
	                    .orElseThrow(() -> new NoSuchElementException("AdminWallet non trouvé"));
	            return ResponseEntity.ok("Total commission balance: " + wallet.getBalance() + "€");
	        } catch (NoSuchElementException e) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("AdminWallet non trouvé");
	        }
	}
	

}
