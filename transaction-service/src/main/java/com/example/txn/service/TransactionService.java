package com.example.txn.service;

import com.example.txn.entity.Transaction;
import com.example.txn.repo.TransactionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository repo;

    public TransactionService(TransactionRepository repo) { this.repo = repo; }

    public List<Transaction> list(Long userId, Instant from, Instant to, Long cardId, String status, int page, int size) {
        // common interview practice: validate + handle defaults
        Instant safeFrom = from == null ? Instant.now().minus(30, ChronoUnit.DAYS) : from;
        Instant safeTo = to == null ? Instant.now() : to;
        return repo.search(userId, safeFrom, safeTo, cardId, status, PageRequest.of(page, size));
    }

    public Map<String, Object> summary(Long userId, Instant from, Instant to) {
        List<Transaction> txns = list(userId, from, to, null, null, 0, 5000);

        // Streams/Collectors: group-by category + stats
        IntSummaryStatistics stats = txns.stream()
                .collect(Collectors.summarizingInt(Transaction::getAmountYen));

        Map<String, Integer> byCategory = txns.stream()
                .collect(Collectors.groupingBy(Transaction::getCategory, Collectors.summingInt(Transaction::getAmountYen)));

        return Map.of(
                "count", stats.getCount(),
                "totalYen", stats.getSum(),
                "avgYen", Math.round(stats.getAverage()),
                "maxYen", stats.getMax(),
                "minYen", stats.getMin(),
                "byCategory", byCategory
        );
    }

    @Transactional
    public Transaction charge(String sagaId, Long userId, Long cardId, Integer amountYen, String merchant, String category) {
        // idempotent
        return repo.findBySagaId(sagaId).orElseGet(() -> {
            Transaction t = new Transaction();
            t.setSagaId(sagaId);
            t.setUserId(userId);
            t.setCardId(cardId);
            t.setAmountYen(amountYen);
            t.setMerchantName(merchant);
            t.setCategory(category);
            t.setStatus("SUCCESS");
            return repo.save(t);
        });
    }

    @Transactional
    public void reverse(String sagaId) {
        Transaction t = repo.findBySagaId(sagaId).orElseThrow(() -> new IllegalArgumentException("Unknown sagaId")); 
        t.setStatus("REVERSED");
        repo.save(t);
    }

    public String newSagaId() {
        return UUID.randomUUID().toString();
    }
}
