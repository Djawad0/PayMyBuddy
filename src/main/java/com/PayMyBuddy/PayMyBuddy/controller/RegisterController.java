package com.PayMyBuddy.PayMyBuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.PayMyBuddy.PayMyBuddy.model.User;
import com.PayMyBuddy.PayMyBuddy.service.UserService;

@Controller
public class RegisterController {

	private final UserService userService;

	@Autowired
	public RegisterController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/inscription")
	public String inscriptionPage(@ModelAttribute User user, Model model) {

		model.addAttribute("user", new User());

		return "register"; 
	}

	@PostMapping("/inscription")
	public String inscription(@RequestParam String confirmNewPassword, User user, Model model) {

		if(!user.getPassword().equals(confirmNewPassword)) {
			model.addAttribute("errorPassword", "Passwords do not match.");
			return "register";
		}

		 try {
	            String result = userService.inscription(user);

	            if ("Registration successful".equals(result)) {
	                return "redirect:/login?success=" + result;
	            } else {
	                model.addAttribute("error", result);
	                return "register";
	            }
	        } catch (IllegalArgumentException e) { 
	            model.addAttribute("error", e.getMessage());
	            return "register";
	        } catch (IllegalStateException e) { 
	            model.addAttribute("error", e.getMessage());
	            return "register";
	        } catch (Exception e) { 
	            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
	            return "register";
	        }

	}

}
