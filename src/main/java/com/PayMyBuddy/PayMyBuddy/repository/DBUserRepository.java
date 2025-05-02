package com.PayMyBuddy.PayMyBuddy.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.PayMyBuddy.PayMyBuddy.model.User;


@Repository
public interface DBUserRepository extends JpaRepository<User, Integer> {
	Optional<User> findByEmail(String email);
	Optional<User> findByUsername(String username);

	@Query("SELECT u FROM User u LEFT JOIN FETCH u.connections WHERE u.email = :email")
	Optional<User> findByEmailWithConnections(@Param("email") String email);

}
