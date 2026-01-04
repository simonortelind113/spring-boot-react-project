package com.example.backend;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3001")
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService service;
    private final TransactionRepository transactionRepository;

    public AccountController(AccountService service, TransactionRepository transactionRepository) {
        this.service = service;
        this.transactionRepository = transactionRepository;
    }
   
    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody Map<String, String> body) {
        String ownerName = body.get("ownerName");
        String password = body.get("password");
    
        Account account = service.createAccount(ownerName, password);
    
        if (account == null) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Owner name already exists");
        }
    
        return ResponseEntity.ok(account);
    }

    @PostMapping("/{id}/deposit")
    public Account deposit(@PathVariable Long id, @RequestParam BigDecimal amount) {
        return service.deposit(id, amount);
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
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
    String ownerName = body.get("ownerName");
    String password = body.get("password");

    // 1. Check if input is null
    if (ownerName == null || password == null) {
        return ResponseEntity.badRequest().body("Missing credentials");
    }

    Account account = service.getAccountByOwner(ownerName);

    // 2. Safe check if account exists
    if (account == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Account not found");
    }

    // 3. Safe password check
    String storedPassword = account.getPassword() != null ? account.getPassword().trim() : "";
    if (!storedPassword.equals(password.trim())) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong password");
    }

    return ResponseEntity.ok(account);
}

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(
        @PathVariable Long id, 
        @RequestParam Long adminId) { // Pass the ID of the person performing the delete
        
        Account performer = service.getAccount(adminId);
        
        if (performer == null || !performer.isManager()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only managers can delete accounts.");
        }
    
        boolean deleted = service.deleteAccount(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }


    @GetMapping
    public ResponseEntity<?> getAllAccounts(@RequestParam Long adminId) {
        try {
            Account requester = service.getAccount(adminId);
            if (!requester.isManager()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
            }
            return ResponseEntity.ok(service.getAllAccounts());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Admin account not found.");
        }
    }
}



