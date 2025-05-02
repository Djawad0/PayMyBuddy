package com.PayMyBuddy.PayMyBuddy.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.PayMyBuddy.PayMyBuddy.model.Transaction;
import com.PayMyBuddy.PayMyBuddy.model.User;

@Repository
public interface DBTransactionRepository extends CrudRepository<Transaction, Integer>{
	List<Transaction> findBySenderOrReceiver(User sender, User receiver);
}
