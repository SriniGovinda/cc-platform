package com.example.txn.repo;

import com.example.txn.entity.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findBySagaId(String sagaId);

    @Query("""
      select t from Transaction t
      where t.userId = :userId
        and t.txnDate between :from and :to
        and (:cardId is null or t.cardId = :cardId)
        and (:status is null or t.status = :status)
      order by t.txnDate desc
    """)
    List<Transaction> search(Long userId, Instant from, Instant to, Long cardId, String status, Pageable pageable);

    @Query("""
      select count(t) from Transaction t
      where t.userId = :userId
        and t.txnDate between :from and :to
        and (:cardId is null or t.cardId = :cardId)
        and (:status is null or t.status = :status)
    """)
    long countSearch(Long userId, Instant from, Instant to, Long cardId, String status);
}
