package com.example.txn.repo;

import com.example.txn.entity.StatementObject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatementObjectRepository extends JpaRepository<StatementObject, Long> {
    Optional<StatementObject> findByIdAndUserId(Long id, Long userId);
}
