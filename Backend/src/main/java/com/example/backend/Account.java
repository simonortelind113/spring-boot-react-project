package com.example.backend;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    private String ownerName;
    private BigDecimal balance;

    protected Account(){

    }

    public Account(String ownerName){
        this.ownerName = ownerName;
        this.balance = BigDecimal.ZERO;
    }

    public BigDecimal getBalance(){
        return balance;
    }

    public void deposit(BigDecimal amount){
        this.balance = this.balance.add(amount);
    }
}
