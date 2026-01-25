package com.example.backend;

import java.math.BigDecimal;

public class AccountResponse {
    private Long id;
    private String ownerName;
    private BigDecimal balance;
    private Role role;

    public AccountResponse(Account account){
        this.id = account.getId();
        this.ownerName = account.getOwnerName();
        this.balance = account.getBalance();
        this.role = account.getRole();
    }

    //--GETTERS--

    public String getOwnerName(){return ownerName;}
    public Long getId() {return id;}
    public BigDecimal getBalance(){return balance;}
    public Role getRole() {return role;}
}
