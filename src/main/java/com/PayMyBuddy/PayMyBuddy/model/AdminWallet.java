package com.PayMyBuddy.PayMyBuddy.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AdminWallet {
	
	@Id
    private Long id = 1L; 

    private double balance;

}
