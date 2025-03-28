package com.PayMyBuddy.PayMyBuddy.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.PayMyBuddy.PayMyBuddy.model.User;
import com.PayMyBuddy.PayMyBuddy.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class UserController {
	
	private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
	
	@PostMapping("/inscription")
    public void inscription(@RequestBody User user) {
        this.userService.inscription(user);       
    }
	
	@GetMapping("/user")
    public String getUser() {
        return "Welcome, User";
    }
    
    @GetMapping("/admin")
    public String getAdmin() {
        return "Welcome, Admin";
    }

}
