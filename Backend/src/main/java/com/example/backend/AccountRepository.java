package com.example.backend;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByOwnerName(String ownerName);
    boolean existsByOwnerName(String ownerName);
}
