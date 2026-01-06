package com.example.backend;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ownerName", unique = true, nullable = false)
    private String ownerName;
    
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @JsonIgnore
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Transaction> transactions = new ArrayList<>();

    @Column(name = "manager", nullable = false)
    private boolean manager = false;

    public Account(){

    }

    public boolean isManager() { return manager; }

    //--SETTERS--

    public void setBalance(BigDecimal balance){this.balance = balance;} 
    
    public void setOwnerName(String ownerName){this.ownerName = ownerName;}

    public void setPassword(String password){this.password = password;}

    public void setManager(boolean manager) { this.manager = manager; }

    //--GETTERS--

    public BigDecimal getBalance(){return balance;}

    public String getOwnerName(){return ownerName;}

    public String getPassword(){return password;}

    public Long getId(){return id;}
}
