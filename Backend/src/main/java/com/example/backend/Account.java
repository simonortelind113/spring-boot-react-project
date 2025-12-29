package com.example.backend;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    private String ownerName;
    private BigDecimal balance = BigDecimal.ZERO;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Transaction> transactions = new ArrayList<>();

    public Account(){

    }

    public void setBalance(BigDecimal balance){
        this.balance = balance;
    }

    public BigDecimal getBalance(){
        return balance;
    }

    public void setOwnerName(String ownerName){
        this.ownerName = ownerName;
    }
}
