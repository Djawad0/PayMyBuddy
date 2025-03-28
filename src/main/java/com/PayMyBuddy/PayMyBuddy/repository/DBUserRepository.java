package com.PayMyBuddy.PayMyBuddy.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.PayMyBuddy.PayMyBuddy.model.User;

public interface DBUserRepository extends JpaRepository<User, Integer> {
	Optional<User> findByEmail(String email);
}
