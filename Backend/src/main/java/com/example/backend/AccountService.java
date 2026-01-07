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
    private final DepositRequestRepository depositReqRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepo,
                          TransactionRepository transactionRepo, 
                          DepositRequestRepository depositReqRepo,
                          BCryptPasswordEncoder passwordEncoder) {
        this.depositReqRepo = depositReqRepo;
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

    public DepositRequest createDepositRequest(
        Long customerId,
        Long targetAccountId,
        BigDecimal amount) {
        Account customer = getAccount(customerId);
        if (customer.getRole() != Role.CUSTOMER) {
            throw new RuntimeException("Only customers can request deposits");
        }
        DepositRequest request = new DepositRequest();
        request.setTargetAccountId(targetAccountId);
        request.setAmount(amount);
        request.setRequestedBy(customerId);
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        return depositReqRepo.save(request);
    }

    public List<DepositRequest> getPendingDepositRequests(Long staffId) {
        Account staff = getAccount(staffId);
        if (staff.getRole() == Role.CUSTOMER) {
            throw new RuntimeException("Access denied");
        }
        return depositReqRepo.findByStatus(RequestStatus.PENDING);
    }

    @Transactional
    public void approveDeposit(Long requestId, Long staffId) {
        Account staff = getAccount(staffId);
        DepositRequest request = depositReqRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Already handled");
        }
        if (staff.getRole() == Role.BANK_ADVISOR &&
            request.getAmount().compareTo(new BigDecimal("10000")) > 0) {
            throw new RuntimeException("Manager approval required");
        }
        Account target = getAccount(request.getTargetAccountId());
        target.setBalance(target.getBalance().add(request.getAmount()));
        Transaction tx = new Transaction();
        tx.setAccount(target);
        tx.setType("DEPOSIT");
        tx.setAmount(request.getAmount());
        transactionRepo.save(tx);
        accountRepo.save(target);
        request.setStatus(RequestStatus.APPROVED);
        request.setHandledBy(staffId);
        depositReqRepo.save(request);
    }

    public void rejectDeposit(Long requestId, Long staffId) {
        getAccount(staffId); 
        DepositRequest request = depositReqRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus(RequestStatus.REJECTED);
        request.setHandledBy(staffId);
        depositReqRepo.save(request);
    }

    //--WITHDRAW--

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



