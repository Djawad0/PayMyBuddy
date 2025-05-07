package com.PayMyBuddy.PayMyBuddy.controller;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.PayMyBuddy.PayMyBuddy.configuration.CustomUserDetailsService;
import com.PayMyBuddy.PayMyBuddy.dto.UpdateUserRequest;
import com.PayMyBuddy.PayMyBuddy.model.Transaction;
import com.PayMyBuddy.PayMyBuddy.model.User;
import com.PayMyBuddy.PayMyBuddy.service.TransactionService;
import com.PayMyBuddy.PayMyBuddy.service.UserService;

@Controller
public class ProfileController {

	private final CustomUserDetailsService customUserDetailsService;
	private final TransactionService transactionService;    
	private final UserService userService;

	@Autowired
	public ProfileController(UserService userService, TransactionService transactionService, CustomUserDetailsService customUserDetailsService) {
		this.userService = userService;
		this.transactionService = transactionService;
		this.customUserDetailsService = customUserDetailsService;		
	}


	@GetMapping("/user/profile")
	public String profilePage(Model model) {
		User user = customUserDetailsService.getAuthenticatedUser();
		model.addAttribute("user", user); 
		return "profile"; 
	}

	@PostMapping("/user/profile")
	public String updateProfile(
			@ModelAttribute("user") User user,
			@RequestParam String currentPassword,
			@RequestParam(required = false) String newPassword,
			@RequestParam(required = false) String confirmNewPassword,
			Model model) {

		if (newPassword != null && !newPassword.isBlank() && !newPassword.equals(confirmNewPassword)) {
			User updatedUser = customUserDetailsService.getAuthenticatedUser(); 
	        model.addAttribute("user", updatedUser);
			model.addAttribute("errorPassword", "Passwords do not match.");
			return "profile";
		}

		User userAuth = customUserDetailsService.getAuthenticatedUser();

		UpdateUserRequest updateRequest = new UpdateUserRequest();


		updateRequest.setOriginalEmail(userAuth.getEmail());

		updateRequest.setUsername(user.getUsername());	 
		if (!user.getEmail().contains("@") || !user.getEmail().contains(".")) {       
			model.addAttribute("errorEmail", "Invalid email.");
			return "profile";
		}
		else {
			updateRequest.setEmail(user.getEmail());	    
		}    	   	    
		updateRequest.setPassword(newPassword);
		updateRequest.setOldPassword(currentPassword);



		try {
	            String result = userService.updateUser(updateRequest);

	            if ("User information updated successfully".equals(result)) {
			try {
			
				UserDetails updatedUserDetails = customUserDetailsService.loadUserByUsername(user.getEmail());


				Authentication newAuth = new UsernamePasswordAuthenticationToken(
						updatedUserDetails,
						updatedUserDetails.getPassword(),
						updatedUserDetails.getAuthorities()
						);

				SecurityContextHolder.getContext().setAuthentication(newAuth);
				
				User updatedUser = customUserDetailsService.getAuthenticatedUser();  
	               
                model.addAttribute("user", updatedUser);

				model.addAttribute("success", result);

				return "profile";

			} catch (UsernameNotFoundException e) {
				model.addAttribute("error", "Updated user not found.");
				return "profile";
			}
		} else {
			model.addAttribute("error", result);
			return "profile";
		}
		} catch (IllegalArgumentException | IllegalStateException | SecurityException | NoSuchElementException e) {
			User updatedUser = customUserDetailsService.getAuthenticatedUser(); 
	        model.addAttribute("user", updatedUser);
	        model.addAttribute("error", e.getMessage());
	    }
		 return "profile";
		
	}

	@PostMapping("/user/deposit")
	public String depositMoney(@RequestParam double amount, Model model) {
		try {
			User user = customUserDetailsService.getAuthenticatedUser();

			Transaction transaction = new Transaction();
			transaction.setSender(user);
			transaction.setReceiver(user);
			transaction.setAmount(amount);
			transaction.setDescription("Deposit to bank account");

			String result = transactionService.addToBankAccount(transaction);
                    
            return "redirect:/user/profile?successDepositMoney=" + result;

		} catch (IllegalArgumentException | IllegalStateException e) {
	        return "redirect:/user/profile?errorDepositMoney=" + e.getMessage();
	    }catch (Exception e) {
			model.addAttribute("errorDepositMoney", "Error during deposit.");
			return "profile";  
		}
	}

	@PostMapping("/user/withdraw")
	public String withdrawMoney(@RequestParam double amount, Model model) {
		try {
			User user = customUserDetailsService.getAuthenticatedUser();

			Transaction transaction = new Transaction();
			transaction.setSender(user);
			transaction.setReceiver(user);
			transaction.setAmount(amount);
			transaction.setDescription("Bank withdrawal");

	        String result = transactionService.withdrawToBankAccount(transaction);
                    
            return "redirect:/user/profile?successWithdrawMoney=" + result;
	           

		}  catch (IllegalArgumentException e) {
	        return "redirect:/user/profile?errorWithdrawMoney=" + e.getMessage();
	    } catch (Exception e) {
			model.addAttribute("errorWithdrawMoney", "Error during withdrawal.");
			return "profile";  
		}
	}

}
