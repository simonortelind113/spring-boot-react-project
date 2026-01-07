package com.example.backend;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import jakarta.transaction.Transactional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountId(Long accountId);

    List<Transaction> findByStatus(RequestStatus status);

    List<Transaction> findByType(TransactionType type);

    List<Transaction> findByTypeAndStatus(TransactionType type, RequestStatus status);

    @Modifying
    @Transactional
    void deleteByAccountId(Long accountId);
}

