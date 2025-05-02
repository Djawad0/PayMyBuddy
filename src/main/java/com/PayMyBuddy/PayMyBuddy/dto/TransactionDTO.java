package com.PayMyBuddy.PayMyBuddy.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionDTO {

	private String senderEmail;
    private String receiverEmail;
    private String description;
    private double amount;
    
    public TransactionDTO(String senderEmail, String receiverEmail, String description, double amount) {
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.description = description;
        this.amount = amount;
    }
	
}
