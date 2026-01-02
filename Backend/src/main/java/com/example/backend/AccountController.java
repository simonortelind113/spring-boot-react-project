package com.example.backend;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

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
    public Account createAccount(@RequestParam String ownerName) {
        Account account = new Account();
        account.setOwnerName(ownerName);
        account.setBalance(BigDecimal.ZERO);
        return service.createAccount(ownerName);
    }

    @PostMapping("/{id}/deposit")
    public Account deposit(
            @PathVariable Long id,
            @RequestParam BigDecimal amount
    ) {
        return service.deposit(id, amount);
    }

    @PostMapping("/{id}/withdraw")
    public Account withdraw( @PathVariable Long id, @RequestParam BigDecimal amount){
        return service.withdraw(id, amount);
    }

    @GetMapping("/{id}")
    public Account getAccount(@PathVariable Long id){
        return service.getAccount(id);
    }

    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    
    @GetMapping("/{id}/transactions")
    public List<Transaction> getTransactions(@PathVariable Long id) {
        return transactionRepository.findByAccountId(id);
    }

    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestParam String ownerName) {
        Account account = service.getAccountByOwner(ownerName); 
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(account);
    }
    
}


