package com.example.account.repo;

import com.example.account.entity.LimitReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LimitReservationRepository extends JpaRepository<LimitReservation, Long> {
    Optional<LimitReservation> findBySagaId(String sagaId);
}
