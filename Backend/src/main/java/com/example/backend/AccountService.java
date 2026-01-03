package com.example.backend;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;

@Service
public class AccountService {

    private final AccountRepository accountRepo;
    private final TransactionRepository transactionRepo;

    public AccountService(AccountRepository accountRepo, TransactionRepository transactionRepo) {
        this.accountRepo = accountRepo;
        this.transactionRepo = transactionRepo;
    }

@PostMapping
public Account createAccount(String ownerName, String password) {
    if (accountRepo.existsByOwnerName(ownerName)) {
        throw new RuntimeException("Owner name already exists");
    }
    Account account = new Account();
    account.setOwnerName(ownerName);
    account.setPassword(password);
    return accountRepo.save(account);
}

public Account getAccountByOwner(String ownerName) {
    return accountRepo.findByOwnerName(ownerName);
}


    public Account deposit(Long accountId, BigDecimal amount) {
        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }            

        account.setBalance(account.getBalance().add(amount));

        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setType("DEPOSIT");
        tx.setAmount(amount);
        transactionRepo.save(tx);

        return accountRepo.save(account);
    }

    public Account withdraw(Long accountId, BigDecimal amount) {
        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        account.setBalance(account.getBalance().subtract(amount));

        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setType("WITHDRAW");
        tx.setAmount(amount);
        transactionRepo.save(tx);

        return accountRepo.save(account);
    }

    public Account getAccount(Long id){
        return accountRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Account not found"));
    }
}


