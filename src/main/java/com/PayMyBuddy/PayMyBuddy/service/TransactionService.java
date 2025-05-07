package com.PayMyBuddy.PayMyBuddy.service;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.PayMyBuddy.PayMyBuddy.configuration.CustomUserDetailsService;
import com.PayMyBuddy.PayMyBuddy.dto.TransactionDTO;
import com.PayMyBuddy.PayMyBuddy.model.AdminWallet;
import com.PayMyBuddy.PayMyBuddy.model.Transaction;
import com.PayMyBuddy.PayMyBuddy.model.User;
import com.PayMyBuddy.PayMyBuddy.repository.AdminWalletRepository;
import com.PayMyBuddy.PayMyBuddy.repository.DBTransactionRepository;
import com.PayMyBuddy.PayMyBuddy.repository.DBUserRepository;
import java.util.NoSuchElementException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class TransactionService {

	@Autowired
	private AdminWalletRepository adminWalletRepository;

	private static final Logger log = LogManager.getLogger(TransactionService.class);
	private final DBTransactionRepository dbTransactionRepository;
	private final DBUserRepository dbUserRepository;
	private final CustomUserDetailsService customUserDetailsService;


	/**
	 * This method allows a user to send money to another user.
	 * @param transaction stores the information about who receives the money, the description, and the transaction amount.
	 * @return a success or error message.
	 */

	@Transactional
	public String transaction(Transaction transaction) {

			User user = customUserDetailsService.getAuthenticatedUser();

			User friend = dbUserRepository.findByEmail(transaction.getReceiverEmail())
					.orElseThrow(() -> {
						log.error("Transaction failed: Recipient {} not found", transaction.getReceiverEmail());
						return new NoSuchElementException("User with email " + transaction.getReceiverEmail() + " not found");
					});

			if (transaction.getAmount() < 0.01 || transaction.getAmount() == 0.0) {
				log.error("Bank transfer failed: Invalid amount");
				throw new IllegalArgumentException("Invalid amount.");
			}	         

			if (transaction.getAmount() > 500) {
				log.error("Transfer failed: Amount too high");
				throw new IllegalArgumentException("Amount too high.");
			}

			double fee = transaction.getAmount() * 0.005;
			double totalDebit = transaction.getAmount() + fee;

			if (user.getBalance() < totalDebit) {
				log.error("Transaction failed: Insufficient balance for {}", user.getEmail());
				throw new IllegalStateException("Insufficient balance to complete the transaction.");
			}

			user.setBalance(Math.round((user.getBalance() - totalDebit) * 100.0) / 100.0);
			friend.setBalance(Math.round((friend.getBalance() + transaction.getAmount()) * 100.0) / 100.0);

			AdminWallet wallet = adminWalletRepository.findById(1L)
					.orElseGet(() -> {
						AdminWallet w = new AdminWallet();
						w.setId(1L);
						w.setBalance(0);
						return w;
					});

			wallet.setBalance(Math.round((wallet.getBalance() + fee) * 100.0) / 100.0);

			Transaction transactions = new Transaction();
			transactions.setSender(user);
			transactions.setReceiver(friend);
			transactions.setDescription(transaction.getDescription());
			transactions.setAmount(transaction.getAmount());

			dbTransactionRepository.save(transactions);
			dbUserRepository.save(user);
			dbUserRepository.save(friend);
			adminWalletRepository.save(wallet);

			log.info("Successful transaction of {}€ from {} to {}", transaction.getAmount(), user.getEmail(), friend.getEmail());
			 return "Transaction successfully completed.";

	}


	/**
	 * This method allows the user to add money to their bank account.
	 * @param transaction stores the information about who receives the money, the description, and the transaction amount.
	 * @return a success or error message.
	 */

	public String addToBankAccount(Transaction transaction) {

			User user = customUserDetailsService.getAuthenticatedUser();

			if (transaction.getAmount() < 0.01) {
				log.error("Bank transfer failed: Invalid amount");
				throw new IllegalArgumentException("Invalid amount.");
			}

			if (user.getBalance() < transaction.getAmount()) {
				log.error("Bank transfer failed: Insufficient balance for {}", user.getEmail());
				throw new IllegalStateException("Insufficient balance to complete the operation.");
			}

			if (transaction.getAmount() > 500) {
				log.error("Bank transfer failed: Amount too high");
				throw new IllegalArgumentException("Amount too high.");
			}

			user.setBalance(Math.round((user.getBalance() - transaction.getAmount()) * 100.0) / 100.0);          

			Transaction transactions = new Transaction();
			transactions.setSender(user);
			transactions.setReceiver(user);
			transactions.setDescription("bank transfer");
			transactions.setAmount(transaction.getAmount());


			dbTransactionRepository.save(transactions);
			dbUserRepository.save(user);

			log.info("Bank deposit of {}€ by {}", transaction.getAmount(), user.getEmail());
			return "Amount successfully added to your bank account.";

	}

	/**
	 * This method allows the user to withdraw money from their bank account and add it to their app balance.
	 * @param transaction stores the information about who receives the money, the description, and the transaction amount.
	 * @return a success or error message.
	 */

	public String withdrawToBankAccount(Transaction transaction) {

			User user = customUserDetailsService.getAuthenticatedUser();

			if (transaction.getAmount() < 0.01) {
				log.error("Bank withdrawal failed: Invalid amount");
				 throw new IllegalArgumentException("Invalid amount.");
			}

			if (transaction.getAmount() > 500) {
				log.error("Bank withdrawal failed: Amount too high");
				throw new IllegalArgumentException("Amount too high.");
			}

			user.setBalance(Math.round((user.getBalance() + transaction.getAmount()) * 100.0) / 100.0);


			Transaction transactions = new Transaction();
			transactions.setSender(user);
			transactions.setReceiver(user);
			transactions.setDescription("bank withdrawal");
			transactions.setAmount(transaction.getAmount());


			dbTransactionRepository.save(transactions);
			dbUserRepository.save(user);


			log.info("Bank withdrawal of {}€ for {}", transaction.getAmount(), user.getEmail());
			return "Withdrawal successfully completed.";

	}


	/**
	 * This method retrieves the list of transactions for a given user.
	 * @param user stores the user's various information (username, email)
	 * @return the list of transactions.
	 */

	public List<TransactionDTO> getTransactionsByUser(User user) {
		Iterable<Transaction> transactions = dbTransactionRepository.findBySenderOrReceiver(user, user);
		List<TransactionDTO> transactionDTOs = new ArrayList<>();

		for (Transaction t : transactions) {
			transactionDTOs.add(new TransactionDTO(
					t.getSender().getUsername(),
					t.getReceiver().getUsername(),
					t.getDescription(),
					t.getAmount()));
		}

		return transactionDTOs;
	}

	/**
	 * This method retrieves the list of all transactions in the app (Admin only).
	 * @return the list of transactions.
	 */

	public List<TransactionDTO> getAllTransactions() {
		Iterable<Transaction> transactions = dbTransactionRepository.findAll();
		List<TransactionDTO> transactionDTOs = new ArrayList<>();

		for (Transaction t : transactions) {
			transactionDTOs.add(new TransactionDTO(
					t.getSender().getUsername(),
					t.getReceiver().getUsername(),
					t.getDescription(),
					t.getAmount()));
		}

		return transactionDTOs;
	}
}
