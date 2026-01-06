package com.example.backend;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepo;
    private final TransactionRepository transactionRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepo,
                          TransactionRepository transactionRepo,
                          BCryptPasswordEncoder passwordEncoder) {
        this.accountRepo = accountRepo;
        this.transactionRepo = transactionRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public Account createAccount(String ownerName, String password) {
        if (accountRepo.existsByOwnerName(ownerName)) {
            return null; 
        }
        Account account = new Account();
        account.setOwnerName(ownerName);
        account.setPassword(passwordEncoder.encode(password));
        account.setRole(Role.CUSTOMER); 
        return accountRepo.save(account);
    }

    @Transactional
    public boolean deleteAccount(Long id) {
        if (!accountRepo.existsById(id)) {
            return false;
        }
        transactionRepo.deleteByAccountId(id);
        accountRepo.deleteById(id);
        return true;
    }

    public Account getAccountByOwner(String ownerName) {
        return accountRepo.findByOwnerName(ownerName);
    }

    public Account getAccount(Long id) {
        return accountRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public List<Account> getAllAccounts() {
        return accountRepo.findAll();
    }

    public Account deposit(Long performerId, Long targetAccountId, BigDecimal amount) {
        Account performer = getAccount(performerId);
        Account target = getAccount(targetAccountId);
        if (performer.getRole() == Role.CUSTOMER) {
            throw new RuntimeException("Customers cannot deposit money");
        }

        if (performer.getRole() == Role.BANK_ADVISOR
                && amount.compareTo(new BigDecimal("10000")) > 0) {
            throw new RuntimeException("Manager approval required for deposits over 10,000");
        }
        target.setBalance(target.getBalance().add(amount));
        Transaction tx = new Transaction();
        tx.setAccount(target);
        tx.setType("DEPOSIT");
        tx.setAmount(amount);
        transactionRepo.save(tx);
        return accountRepo.save(target);
    }

    public Account withdraw(Long accountId, BigDecimal amount) {
        Account account = getAccount(accountId);
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
}



