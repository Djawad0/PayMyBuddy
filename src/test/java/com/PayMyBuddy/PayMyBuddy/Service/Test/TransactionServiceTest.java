package com.PayMyBuddy.PayMyBuddy.Service.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import com.PayMyBuddy.PayMyBuddy.configuration.CustomUserDetailsService;
import com.PayMyBuddy.PayMyBuddy.dto.TransactionDTO;
import com.PayMyBuddy.PayMyBuddy.model.AdminWallet;
import com.PayMyBuddy.PayMyBuddy.model.Transaction;
import com.PayMyBuddy.PayMyBuddy.model.User;
import com.PayMyBuddy.PayMyBuddy.repository.AdminWalletRepository;
import com.PayMyBuddy.PayMyBuddy.repository.DBTransactionRepository;
import com.PayMyBuddy.PayMyBuddy.repository.DBUserRepository;
import com.PayMyBuddy.PayMyBuddy.service.TransactionService;

@SpringBootTest
public class TransactionServiceTest {

	@InjectMocks
	private TransactionService transactionService;

	@Mock
	private DBUserRepository userRepository;

	@Mock
	private Transaction transaction;

	@Mock
	private CustomUserDetailsService customUserDetailsService;

	@Mock
	private User user;

	@Mock
	private AdminWalletRepository adminWalletRepository;

	@Mock
	private DBTransactionRepository dbTransactionRepository;


	@Test
	void testTransactionEchouee() {

		Transaction transaction = new Transaction();
		transaction.setReceiverEmail("test@exemple.com");

		when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);

		when(userRepository.findByEmail("test@exemple.com")).thenReturn(Optional.empty());

		NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
	        transactionService.transaction(transaction);
	    });

	    assertEquals("User with email test@exemple.com not found", exception.getMessage());
	}

	@Test
	void testTransactionMontantIncorrect1() {

		Transaction transaction = new Transaction();
		transaction.setReceiverEmail("test@exemple.com");
		transaction.setAmount(0.0);

		when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);

		when(userRepository.findByEmail("test@exemple.com")).thenReturn(Optional.of(user));

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
	        transactionService.transaction(transaction);
	    });

	    assertEquals("Invalid amount.", exception.getMessage());
	}

	@Test
	void testTransactionMontantIncorrect2() {

		Transaction transaction = new Transaction();
		transaction.setReceiverEmail("test@exemple.com");
		transaction.setAmount(-1.0);

		when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);

		when(userRepository.findByEmail("test@exemple.com")).thenReturn(Optional.of(user));

		 IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
		        transactionService.transaction(transaction);
		    });

		    assertEquals("Invalid amount.", exception.getMessage());
	}


	@Test
	void testTransactionMontantEleve() {

		Transaction transaction = new Transaction();
		transaction.setReceiverEmail("test@exemple.com");
		transaction.setAmount(501.0);

		when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);

		when(userRepository.findByEmail("test@exemple.com")).thenReturn(Optional.of(user));

		 IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
		        transactionService.transaction(transaction);
		    });

		    assertEquals("Amount too high.", exception.getMessage());
	}

	@Test
	void testTransactionSoldeInsuffisant() {

		Transaction transaction = new Transaction();
		transaction.setReceiverEmail("test@exemple.com");
		transaction.setAmount(500.0);

		User user = new User();
		user.setBalance(100.0);

		when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);

		when(userRepository.findByEmail("test@exemple.com")).thenReturn(Optional.of(user));

		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
	        transactionService.transaction(transaction);
	    });

	    assertEquals("Insufficient balance to complete the transaction.", exception.getMessage());
	}

	@Test
	void testTransactionCreeNouveauWalletSiInexistant() {

		Transaction transaction = new Transaction();
		transaction.setReceiverEmail("test@exemple.com");
		transaction.setAmount(500.0);

		User user = new User();
		user.setBalance(1000.0);

		when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);

		when(userRepository.findByEmail("test@exemple.com")).thenReturn(Optional.of(user));

		when(adminWalletRepository.findById(1L)).thenReturn(Optional.empty());


		String response = transactionService.transaction(transaction);

	    assertEquals("Transaction successfully completed.", response);
	}


	@Test
	void testTransactionRealiseeAvecSucces() {

		Transaction transaction = new Transaction();
		transaction.setReceiverEmail("test@exemple.com");
		transaction.setAmount(500.0);

		AdminWallet existingWallet = new AdminWallet();
		existingWallet.setId(1L);
		existingWallet.setBalance(100.0);

		User user = new User();
		user.setBalance(1000.0);

		when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);

		when(userRepository.findByEmail("test@exemple.com")).thenReturn(Optional.of(user));

		when(adminWalletRepository.findById(1L)).thenReturn(Optional.of(existingWallet));


		String response = transactionService.transaction(transaction);

	    assertEquals("Transaction successfully completed.", response);
	}


	@Test
	void testAddToBankAccountMontantIncorrect() {

		Transaction transaction = new Transaction();
		transaction.setAmount(-1.0);

		when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);

		 IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
		        transactionService.addToBankAccount(transaction);
		    });

		    assertEquals("Invalid amount.", exception.getMessage());
	}

	@Test
	void testAddToBankAccountSoldeInsuffisant() {

		Transaction transaction = new Transaction();
		transaction.setAmount(2.0);

		User user = new User();
		user.setBalance(1.0);

		when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);

		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
	        transactionService.addToBankAccount(transaction);
	    });

	    assertEquals("Insufficient balance to complete the operation.", exception.getMessage());
	}

	@Test
	void testAddToBankAccountMontantTropEleve() {

		Transaction transaction = new Transaction();
		transaction.setAmount(501.0);

		User user = new User();
		user.setBalance(1000.0);

		when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
	        transactionService.addToBankAccount(transaction);
	    });

	    assertEquals("Amount too high.", exception.getMessage());
	}

	@Test
	void testAddToBankAccountErreur() {

		Transaction transaction = new Transaction();
		transaction.setAmount(500.0);

		User user = new User();
		user.setBalance(1000.0);

		when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);
		when(userRepository.save(user)).thenThrow(new RuntimeException("Saving error"));

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
	        transactionService.addToBankAccount(transaction);
	    });

	    assertEquals("Saving error", exception.getMessage());
	}

	@Test
	void testAddToBankAccountMontantAjouteAvecSucces() {

		Transaction transaction = new Transaction();
		transaction.setAmount(500.0);

		User user = new User();
		user.setBalance(1000.0);

		when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);

		 String response = transactionService.addToBankAccount(transaction);

		 assertEquals("Amount successfully added to your bank account.", response);
	}

	@Test
	void testWithdrawToBankAccountMontantIncorrect() {

		Transaction transaction = new Transaction();
		transaction.setAmount(-1.0);

		when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
	        transactionService.withdrawToBankAccount(transaction);
	    });

	    assertEquals("Invalid amount.", exception.getMessage());
	}

	@Test
	void testWithdrawToBankAccountMontantTropEleve() {

		Transaction transaction = new Transaction();
		transaction.setAmount(501.0);

		when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);

		 IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
		        transactionService.withdrawToBankAccount(transaction);
		    });

		    assertEquals("Amount too high.", exception.getMessage());
	}


	@Test
	void testWithdrawToBankAccountRetraitEffectueAvecSucces() {

		Transaction transaction = new Transaction();
		transaction.setAmount(500.0);

		when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);

		String response = transactionService.withdrawToBankAccount(transaction);

	    assertEquals("Withdrawal successfully completed.", response);
	}

	@Test
	void testGetTransactionsByUser() {
		User user = new User();
		user.setUsername("testUser");

		List<Transaction> transactions = new ArrayList<>();
		Transaction transaction1 = new Transaction();
		transaction1.setSender(user);
		transaction1.setReceiver(user);
		transaction1.setAmount(100.0);

		transactions.add(transaction1);

		when(dbTransactionRepository.findBySenderOrReceiver(user, user)).thenReturn(transactions);

		List<TransactionDTO> result = transactionService.getTransactionsByUser(user);

		verify(dbTransactionRepository).findBySenderOrReceiver(user, user);

		assertEquals(1, result.size());
		assertEquals(100.0, result.get(0).getAmount());

	}

	@Test
	void testGetAllTransactions() {
		List<Transaction> transactions = new ArrayList<>();
		Transaction transaction1 = new Transaction();
		transaction1.setSender(new User());
		transaction1.setReceiver(new User());
		transaction1.setAmount(100.0);

		transactions.add(transaction1);

		when(dbTransactionRepository.findAll()).thenReturn(transactions);	    

		List<TransactionDTO> result = transactionService.getAllTransactions();

		verify(dbTransactionRepository).findAll();

		assertEquals(1, result.size());
		assertEquals(100.0, result.get(0).getAmount());

	}
	
}
