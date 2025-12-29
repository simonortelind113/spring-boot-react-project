package com.example.backend;

import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
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
    

    @GetMapping("/test")
    public String test() {
        return "Bellik is the best ever!!!";
    }
    
}


