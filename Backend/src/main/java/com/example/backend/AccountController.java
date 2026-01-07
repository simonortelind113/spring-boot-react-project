package com.example.backend;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService service;
    private final TransactionRepository transactionRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AccountController(AccountService service, TransactionRepository transactionRepository, BCryptPasswordEncoder passwordEncoder) {
        this.service = service;
        this.transactionRepository = transactionRepository;
        this.passwordEncoder = passwordEncoder;
    }
   
    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody Map<String, String> body) {
        String ownerName = body.get("ownerName");
        String password = body.get("password");
        if (ownerName == null || password == null) {
            return ResponseEntity.badRequest().body("Missing fields");
        }
        Account account = service.createAccount(ownerName, password);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Owner name already exists");
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String ownerName = body.get("ownerName");
        String password = body.get("password");
        if (ownerName == null || password == null) {
            return ResponseEntity.badRequest().body("Missing credentials");
        }
        Account account = service.getAccountByOwner(ownerName);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Account not found");
        }
        if (!passwordEncoder.matches(password, account.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong password");
        }
        return ResponseEntity.ok(account);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(
        @PathVariable Long id,
        @RequestParam Long adminId) {
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
    public Account withdraw(@PathVariable Long id, @RequestParam BigDecimal amount) {
        return service.withdraw(id, amount);
    }

    @GetMapping("/{id}")
    public Account getAccount(@PathVariable Long id) {
        return service.getAccount(id);
    }

    @GetMapping("/{id}/transactions")
    public List<Transaction> getTransactions(@PathVariable Long id) {
        return transactionRepository.findByAccountId(id);
    }
    
    @GetMapping
    public ResponseEntity<?> getAllAccounts(@RequestParam Long adminId) {
        Account requester = service.getAccount(adminId);
        if (requester.getRole() != Role.MANAGER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }
        return ResponseEntity.ok(service.getAllAccounts());
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
    public List<DepositRequest> getPendingDeposits(@RequestParam Long staffId) {
        return service.getPendingDepositRequests(staffId);
    }

    @PostMapping("/deposit-requests/{id}/approve")
    public ResponseEntity<?> approveDeposit(
            @PathVariable Long id,
            @RequestParam Long staffId) {
        service.approveDeposit(id, staffId);
        return ResponseEntity.ok("Deposit approved");
    }

    @PostMapping("/deposit-requests/{id}/reject")
    public ResponseEntity<?> rejectDeposit(
            @PathVariable Long id,
            @RequestParam Long staffId) {
        service.rejectDeposit(id, staffId);
        return ResponseEntity.ok("Deposit rejected");
    }
}



