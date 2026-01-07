package com.example.backend;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DepositRequestRepository
        extends JpaRepository<DepositRequest, Long> {

    List<DepositRequest> findByStatus(RequestStatus status);
}