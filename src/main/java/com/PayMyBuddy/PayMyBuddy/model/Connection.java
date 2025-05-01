package com.PayMyBuddy.PayMyBuddy.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "connection")
public class Connection {

	@EmbeddedId
	private ConnectionId id;


	@ManyToOne
	@JoinColumn(name = "user_id_1", insertable = false, updatable = false)
	private User user1;

	@ManyToOne
	@JoinColumn(name = "user_id_2", insertable = false, updatable = false)
	private User user2;
}
