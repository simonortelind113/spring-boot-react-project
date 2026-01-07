package com.example.backend;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class DepositRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long targetAccountId;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private Long requestedBy;
    private Long handledBy;

    private LocalDateTime createdAt;

    //--GETTERS--

    public Long getId() { return id; }
    public Long getTargetAccountId() { return targetAccountId; }
    public BigDecimal getAmount() { return amount; }
    public RequestStatus getStatus() { return status; }
    public Long getRequestedBy() { return requestedBy; }
    public Long getHandledBy() { return handledBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    //--SETTERS--

    public void setTargetAccountId(Long targetAccountId) {this.targetAccountId = targetAccountId;}
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setStatus(RequestStatus status) { this.status = status; }
    public void setRequestedBy(Long requestedBy) {this.requestedBy = requestedBy;}
    public void setHandledBy(Long handledBy) {this.handledBy = handledBy;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
}
