package com.PayMyBuddy.PayMyBuddy.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
import com.PayMyBuddy.PayMyBuddy.service.JwtService;
import com.PayMyBuddy.PayMyBuddy.service.TokenBlacklistService;
import com.PayMyBuddy.PayMyBuddy.service.TransactionService;
import com.PayMyBuddy.PayMyBuddy.service.UserService;

import jakarta.servlet.http.HttpServletRequest;


@RestController
public class UserController {
	
	@Autowired
	private final UserService userService;
	
	@Autowired
	private AdminWalletRepository adminWalletRepository;
	
	@Autowired
    private AuthenticationManager authenticationManager;
	
	@Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private TokenBlacklistService tokenBlacklistService;
	
	@Autowired
	private final ConnectionService connectionService;
	
	@Autowired
	private final TransactionService transactionService;

    public UserController(UserService userService, ConnectionService connectionService, TransactionService transactionService) {
        this.userService = userService;
        this.connectionService = connectionService;
        this.transactionService = transactionService;
    }
	
	@PostMapping("/inscription")
    public ResponseEntity<String> inscription(@RequestBody User user) {
		return userService.inscription(user);       
    }
	
	@GetMapping("/user")
    public String getUser() {
        return "Welcome, User";
    }
    
    @GetMapping("/admin")
    public String getAdmin() {
        return "Welcome, Admin";
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
    
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        String jwt = jwtService.generateToken(userDetails);

        return Map.of("token", jwt);
    }
    
    @PostMapping("/api/logout")
    public String logout(HttpServletRequest request) {
    	
    	 String authHeader = request.getHeader("Authorization");

    	    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
    	        throw new RuntimeException("Token invalide ou manquant");
    	    }

    	    String token = authHeader.substring(7);

    	    tokenBlacklistService.addToken(token);

    	    return "Déconnexion réussie.";
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
                .orElseThrow(() -> new RuntimeException("AdminWallet introuvable"));
        return ResponseEntity.ok("Solde total des commissions : " + wallet.getBalance() + "€");
    }

}
