package com.example.backend;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Account account;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;
    private BigDecimal amount;
    private Long requestedBy;
    private Long handledBy;
    private LocalDateTime createdAt = LocalDateTime.now();

    // ---- GETTERS ----

    public Long getId() {return id;}
    public Account getAccount() {return account;}
    public TransactionType getType() {return type;}
    public RequestStatus getStatus() {return status;}
    public BigDecimal getAmount() {return amount;}
    public Long getRequestedBy() {return requestedBy;}
    public Long getHandledBy() {return handledBy;}
    public LocalDateTime getCreatedAt() {return createdAt;}

    // ---- SETTERS ----

    public void setAccount(Account account) {this.account = account;}
    public void setType(TransactionType type) {this.type = type;}
    public void setStatus(RequestStatus status) {this.status = status;}
    public void setAmount(BigDecimal amount) {this.amount = amount;}
    public void setRequestedBy(Long requestedBy) {this.requestedBy = requestedBy;}
    public void setHandledBy(Long handledBy) {this.handledBy = handledBy;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
}
