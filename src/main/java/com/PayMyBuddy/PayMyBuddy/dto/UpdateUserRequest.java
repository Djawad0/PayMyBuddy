package com.PayMyBuddy.PayMyBuddy.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
	
	private String oldPassword;
    private String username;    
    private String email;       
    private String password;
    private String originalEmail;

}
