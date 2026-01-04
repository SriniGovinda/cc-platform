package com.example.account.repo;

import com.example.account.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByCustomer_CustomerId(Long customerId);
    Optional<Card> findByCardIdAndCustomer_CustomerId(Long cardId, Long customerId);
}
