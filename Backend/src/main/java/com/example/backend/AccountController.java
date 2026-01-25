package com.example.backend;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService service;
    private final TransactionRepository transactionRepository;

    public AccountController(AccountService service, TransactionRepository transactionRepository, BCryptPasswordEncoder passwordEncoder) {
        this.service = service;
        this.transactionRepository = transactionRepository;
    }
   
    @PostMapping
    public ResponseEntity<?> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        Account account = service.createAccount(request.getOwnerName(),request.getPassword());
        if (account == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Owner name already exists");
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    @PostMapping("/login")
    public ResponseEntity<AccountResponse> login(@Valid @RequestBody LoginRequest request) {
        Account account = service.authenticate(
                request.getOwnerName(),
                request.getPassword()
        );
        return ResponseEntity.ok(new AccountResponse(account));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long id, @RequestParam Long adminId) {
        Account performer = service.getAccount(adminId);
        if (performer.getRole() != Role.MANAGER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only managers can delete accounts.");
        }
        boolean deleted = service.deleteAccount(id);
        return deleted ? ResponseEntity.ok().build()
                    : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<AccountResponse> withdraw(@PathVariable Long id, @RequestParam BigDecimal amount) {
        Account account = service.withdraw(id, amount);
        return ResponseEntity.ok(new AccountResponse(account));    
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long id) {
        return ResponseEntity.ok(new AccountResponse(service.getAccount(id))); 
    }

    @GetMapping("/{id}/transactions")
    public List<Transaction> getTransactions(@PathVariable Long id) {
        return transactionRepository.findByAccountId(id);
    }
    
    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts(@RequestParam Long adminId) {
        Account requester = service.getAccount(adminId);
        if (requester.getRole() != Role.MANAGER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<AccountResponse> response = service.getAllAccounts().stream().map(AccountResponse::new).toList();
        return ResponseEntity.ok(response);
    }
    

    //--DEPOSIT--
    
    @PostMapping("/{id}/deposit-request")
    public ResponseEntity<?> requestDeposit(
            @PathVariable Long id,
            @RequestParam Long performerId,
            @RequestParam BigDecimal amount) {
        service.createDepositRequest(performerId, id, amount);
        return ResponseEntity.ok("Deposit request submitted");
    }

    @GetMapping("/deposit-requests")
    public List<Transaction> getPendingDeposits(@RequestParam Long staffId) {
        return service.getPendingDepositRequests(staffId);
    }

    @PostMapping("/deposit-requests/{transactionId}/approve")
    public ResponseEntity<?> approveDeposit(
            @PathVariable Long transactionId,
            @RequestParam Long staffId) {
        service.approveDeposit(transactionId, staffId);
        return ResponseEntity.ok("Deposit approved");
    }

    @PostMapping("/deposit-requests/{transactionId}/reject")
    public ResponseEntity<?> rejectDeposit(
            @PathVariable Long transactionId,
            @RequestParam Long staffId) {
        service.rejectDeposit(transactionId, staffId);
        return ResponseEntity.ok("Deposit rejected");
    }
}



