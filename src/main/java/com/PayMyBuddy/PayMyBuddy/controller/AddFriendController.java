package com.PayMyBuddy.PayMyBuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.PayMyBuddy.PayMyBuddy.service.ConnectionService;

@Controller
public class AddFriendController {

	private final ConnectionService connectionService;

	@Autowired
	public AddFriendController(ConnectionService connectionService) {	        
		this.connectionService = connectionService;		
	}

	@GetMapping("/user/add-friend")
	public String addFriendPage() {
		return "add-friend"; 
	}


	@PostMapping("/user/add-friend")
	public String addFriend(@RequestParam("friendEmail") String friendEmail, Model model) {
		try {
			String result = connectionService.createConnection(friendEmail);
			model.addAttribute("success", result);
		} catch (IllegalArgumentException | IllegalStateException e) {
			model.addAttribute("error", e.getMessage());
		} catch (Exception e) {
			model.addAttribute("error", "An unexpected error occurred.");
		}

		return "add-friend";
	}

}
