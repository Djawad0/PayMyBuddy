package com.PayMyBuddy.PayMyBuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
		ResponseEntity<String> response = connectionService.createConnection(friendEmail);

		if (response.getStatusCode().is2xxSuccessful()) {
			model.addAttribute("success", response.getBody());
		} else {
			model.addAttribute("error", response.getBody());
		}

		return "add-friend";
	}

}
