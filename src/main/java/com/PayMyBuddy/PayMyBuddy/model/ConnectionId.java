package com.PayMyBuddy.PayMyBuddy.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionId implements Serializable {

	@Column(name = "user_id_1")
	    private Integer userId1;

	@Column(name = "user_id_2")
	    private Integer userId2;

	    @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (o == null || getClass() != o.getClass()) return false;
	        ConnectionId that = (ConnectionId) o;
	        return Objects.equals(userId1, that.userId1) && Objects.equals(userId2, that.userId2);
	    }

	    @Override
	    public int hashCode() {
	        return Objects.hash(userId1, userId2);
	    }

}
