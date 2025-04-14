package com.PayMyBuddy.PayMyBuddy.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "username")
	private String username;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private String password;

	@Column(name = "role")
	private String role;
	
	@Column(name = "balance")
	private Double balance;

	@OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
	private List<Transaction> sentTransactions;

	@OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
	private List<Transaction> receivedTransactions;

	@ManyToMany
	@JoinTable(
			name = "connection",
			joinColumns = @JoinColumn(name = "user_id_1"),
			inverseJoinColumns = @JoinColumn(name = "user_id_2")
			)
	private List<User> connections = new ArrayList<>();



}
