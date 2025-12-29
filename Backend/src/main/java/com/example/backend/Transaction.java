package com.example.backend;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Account account;

    private String type; // "DEPOSIT" or "WITHDRAW"
    private BigDecimal amount;
    private LocalDateTime timestamp = LocalDateTime.now();

    public Transaction(){

    }

    public void setAmount(BigDecimal amount){
        this.amount = amount;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void setType(String type){
        this.type = type;
    }

}
