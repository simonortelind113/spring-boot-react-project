package com.example.backend;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
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
    public Account createAccount(@RequestBody Map<String, String> body) {
        String ownerName = body.get("ownerName");
        String password = body.get("password");
        return service.createAccount(ownerName, password);
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
    public ResponseEntity<Account> login(@RequestBody Map<String, String> body) {
        String ownerName = body.get("ownerName");
        String password = body.get("password");
    
        Account account = service.getAccountByOwner(ownerName);
    
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    
        // Trim and compare to avoid invisible spaces
        if (!account.getPassword().trim().equals(password.trim())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    
        return ResponseEntity.ok(account);
    }    
}



