package com.PayMyBuddy.PayMyBuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
			model.addAttribute("errorPassword", "Les mots de passe ne correspondent pas.");
			return "register";
		}

		ResponseEntity<String> response = userService.inscription(user);

		if (response.getStatusCode() == HttpStatus.CREATED) {
			return "redirect:/login?success=" + response.getBody();			
		} else {
			model.addAttribute("error", response.getBody());
			return "register";
		}

	}

}
