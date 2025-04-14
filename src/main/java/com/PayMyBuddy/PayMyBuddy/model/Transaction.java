package com.PayMyBuddy.PayMyBuddy.model;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;

import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "transaction")
public class Transaction {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private int id;
	    
	    @ManyToOne
	    @JoinColumn(name = "sender_id", nullable = false)
	    private User sender;
	    
	    @ManyToOne
	    @JoinColumn(name = "receiver_id", nullable = false)
	    private User receiver;
	    
	    @Column(name = "description")
	    private String description;
	    
	    @Column(name = "amount")
	    private Double amount;
	    
	    @Transient  
	    private String receiverEmail;
	
}
