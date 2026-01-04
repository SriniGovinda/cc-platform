package com.example.txn.controller;

import com.example.txn.entity.Transaction;
import com.example.txn.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }
    /**
     * Fetches all transactions for the authenticated user.
     */

    @GetMapping
    public ResponseEntity<List<Transaction>> list(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(required = false) Long cardId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(
                service.list(userId, from, to, cardId, status, page, size)
        );
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> summary(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to
    ) {
        return ResponseEntity.ok(
                service.summary(userId, from, to)
        );
    }
}
