package com.PayMyBuddy.PayMyBuddy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.PayMyBuddy.PayMyBuddy.model.AdminWallet;


@Repository
public interface AdminWalletRepository extends JpaRepository<AdminWallet, Long> {

}
