package com.PayMyBuddy.PayMyBuddy.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.PayMyBuddy.PayMyBuddy.configuration.CustomUserDetailsService;
import com.PayMyBuddy.PayMyBuddy.dto.TransactionDTO;
import com.PayMyBuddy.PayMyBuddy.model.AdminWallet;
import com.PayMyBuddy.PayMyBuddy.model.Transaction;
import com.PayMyBuddy.PayMyBuddy.model.User;
import com.PayMyBuddy.PayMyBuddy.repository.AdminWalletRepository;
import com.PayMyBuddy.PayMyBuddy.repository.DBTransactionRepository;
import com.PayMyBuddy.PayMyBuddy.repository.DBUserRepository;

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
	
	@Transactional
	public ResponseEntity<String> transaction(Transaction transaction) {

		 try {
	            User user = customUserDetailsService.getAuthenticatedUser();

	            User friend = dbUserRepository.findByEmail(transaction.getReceiverEmail())
	                    .orElseThrow(() -> {
	                        log.error("Transaction échouée : Destinataire {} non trouvé", transaction.getReceiverEmail());
	                        return new RuntimeException("Utilisateur avec l'email " + transaction.getReceiverEmail() + " non trouvé");
	                    });
	            
	            if (transaction.getAmount() < 0.01 || transaction.getAmount() == 0) {
	                log.error("Virement banque échoué : Montant incorrect");
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                        .body("Montant incorrect.");
	            }	         
	            
	            if (transaction.getAmount() > 500) {
	                log.error("Virement échoué : Montant trop élevé");
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                        .body("Montant trop élevé.");
	            }
	            
	            double fee = transaction.getAmount() * 0.005;
	            double totalDebit = transaction.getAmount() + fee;
	            
	            if (user.getBalance() < totalDebit) {
	                log.error("Transaction échouée : Solde insuffisant pour {}", user.getEmail());
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                        .body("Solde insuffisant pour effectuer la transaction.");
	            }

	            user.setBalance(user.getBalance() - totalDebit);
	            friend.setBalance(friend.getBalance() + transaction.getAmount());
	            
	            AdminWallet wallet = adminWalletRepository.findById(1L)
	                    .orElseGet(() -> {
	                        AdminWallet w = new AdminWallet();
	                        w.setId(1L);
	                        w.setBalance(0);
	                        return w;
	                    });

	            wallet.setBalance(wallet.getBalance() + fee);   
	
		Transaction transactions = new Transaction();
		transactions.setSender(user);
		transactions.setReceiver(friend);
		transactions.setDescription(transaction.getDescription());
		transactions.setAmount(transaction.getAmount());
	
		dbTransactionRepository.save(transactions);
		dbUserRepository.save(user);
		dbUserRepository.save(friend);
		adminWalletRepository.save(wallet);
		
		log.info("Transaction réussie de {}€ de {} vers {}", transaction.getAmount(), user.getEmail(), friend.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body("Transaction réalisée avec succès.");

    } catch (RuntimeException e) {
        log.error("Erreur lors de la transaction : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
        log.error("Erreur inattendue lors de la transaction : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur est survenue lors de la transaction.");
    }
	}
	
	public ResponseEntity<String> addToBankAccount(Transaction transaction) {
		
		 try {
	            User user = customUserDetailsService.getAuthenticatedUser();
	            
	            if (transaction.getAmount() < 0.01) {
	                log.error("Virement banque échoué : Montant incorrect");
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                        .body("Montant incorrect.");
	            }

	            if (user.getBalance() < transaction.getAmount()) {
	                log.error("Virement banque échoué : Solde insuffisant pour {}", user.getEmail());
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                        .body("Solde insuffisant pour effectuer l'opération.");
	            }
	            
	            if (transaction.getAmount() > 500) {
	                log.error("Virement banque échoué : Montant trop élevé");
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                        .body("Montant trop élevé.");
	            }

	            user.setBalance(user.getBalance() - transaction.getAmount());          
		
		Transaction transactions = new Transaction();
		transactions.setSender(user);
		transactions.setReceiver(user);
		transactions.setDescription("bank transfer");
		transactions.setAmount(transaction.getAmount());
		
		
		dbTransactionRepository.save(transactions);
		dbUserRepository.save(user);
		
		log.info("Ajout à la banque de {}€ par {}", transaction.getAmount(), user.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body("Montant ajouté à votre compte bancaire avec succès.");

    } catch (RuntimeException e) {
        log.error("Erreur lors de l'ajout banque : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
        log.error("Erreur inattendue lors de l'ajout banque : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur est survenue lors de l'opération.");
    }
		
	}
	
	
	public ResponseEntity<String> withdrawToBankAccount(Transaction transaction) {
		
		 try {
	            User user = customUserDetailsService.getAuthenticatedUser();
	            
	            if (transaction.getAmount() < 0.01) {
	                log.error("Retrait banque échoué : Montant incorrect");
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                        .body("Montant incorrect.");
	            }

	            if (transaction.getAmount() > 500) {
	                log.error("Retrait banque échoué : Montant trop élevé");
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                        .body("Montant trop élevé.");
	            }

	            user.setBalance(user.getBalance() + transaction.getAmount());
	          
		
		Transaction transactions = new Transaction();
		transactions.setSender(user);
		transactions.setReceiver(user);
		transactions.setDescription("bank withdrawal");
		transactions.setAmount(transaction.getAmount());
		
		
		dbTransactionRepository.save(transactions);
		dbUserRepository.save(user);
		
		
		 log.info("Retrait de {}€ de la banque pour {}", transaction.getAmount(), user.getEmail());
         return ResponseEntity.status(HttpStatus.OK).body("Retrait effectué avec succès.");

     } catch (RuntimeException e) {
         log.error("Erreur lors du retrait banque : {}", e.getMessage());
         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
     } catch (Exception e) {
         log.error("Erreur inattendue lors du retrait banque : {}", e.getMessage());
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur est survenue lors de l'opération.");
     }
		
	}
	
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
