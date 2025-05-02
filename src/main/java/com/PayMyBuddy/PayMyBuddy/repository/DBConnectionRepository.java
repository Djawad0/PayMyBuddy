package com.PayMyBuddy.PayMyBuddy.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.PayMyBuddy.PayMyBuddy.model.Connection;
import com.PayMyBuddy.PayMyBuddy.model.ConnectionId;

@Repository
public interface DBConnectionRepository extends CrudRepository<Connection, ConnectionId> {

}
