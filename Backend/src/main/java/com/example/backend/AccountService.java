package com.example.backend;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    //--DEPOSITS--

    public Transaction createDepositRequest(
        Long customerId,
        Long targetAccountId,
        BigDecimal amount) {
        Account customer = getAccount(customerId);
        if (customer.getRole() != Role.CUSTOMER) {
            throw new RuntimeException("Only customers can request deposits");
        }
        Transaction tx = new Transaction();
        tx.setAccount(getAccount(targetAccountId));
        tx.setType(TransactionType.DEPOSIT);
        tx.setAmount(amount);
        tx.setStatus(RequestStatus.PENDING);
        tx.setRequestedBy(customerId);
        tx.setCreatedAt(LocalDateTime.now());
        return transactionRepo.save(tx);
    }


    public List<Transaction> getPendingDepositRequests(Long staffId) {
        Account staff = getAccount(staffId);
        if (staff.getRole() == Role.CUSTOMER) {
            throw new RuntimeException("Access denied");
        }
        return transactionRepo.findByTypeAndStatus( TransactionType.DEPOSIT,RequestStatus.PENDING );
    }
    
    @Transactional
    public void approveDeposit(Long transactionId, Long staffId) {
        Account staff = getAccount(staffId);
        Transaction tx = transactionRepo.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        if (tx.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Already handled");
        }
        if (staff.getRole() == Role.BANK_ADVISOR &&
            tx.getAmount().compareTo(new BigDecimal("10000")) > 0) {
            throw new RuntimeException("Manager approval required");
        }
        Account target = tx.getAccount();
        target.setBalance(target.getBalance().add(tx.getAmount()));
        tx.setStatus(RequestStatus.APPROVED);
        tx.setHandledBy(staffId);
        accountRepo.save(target);
        transactionRepo.save(tx);
    }
    

    public void rejectDeposit(Long transactionId, Long staffId) {
        getAccount(staffId);
        Transaction tx = transactionRepo.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        tx.setStatus(RequestStatus.REJECTED);
        tx.setHandledBy(staffId);
        transactionRepo.save(tx);
    }
        
    @Transactional
    public Account withdraw(Long accountId, BigDecimal amount) {
        Account account = getAccount(accountId);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }
        account.setBalance(account.getBalance().subtract(amount));
        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setType(TransactionType.WITHDRAWAL);
        tx.setAmount(amount);
        tx.setStatus(RequestStatus.APPROVED);
        tx.setCreatedAt(LocalDateTime.now());
        transactionRepo.save(tx);
        return accountRepo.save(account);
    }

    //--AUTHENTICATE--

    public Account authenticate(String ownerName, String rawPassword) {
        Account account = accountRepo.findByOwnerName(ownerName);
        if (account == null) {
            throw new RuntimeException("Invalid credentials");
        }
        if (!passwordEncoder.matches(rawPassword, account.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        return account;
    } 
}



